package com.visinhand.client;

import com.leclowndu93150.thaumaturge.api.casters.FocusCaster;
import com.leclowndu93150.thaumaturge.client.input.TTKeybinds;
import com.leclowndu93150.thaumaturge.compat.curio.ThaumaturgeCuriosCompat;
import com.leclowndu93150.thaumaturge.content.casters.CasterManager;
import com.leclowndu93150.thaumaturge.content.casters.FocusPouchItem;
import com.leclowndu93150.thaumaturge.content.spell.item.FocusItems;
import com.leclowndu93150.thaumaturge.network.ServerboundCasterKeyPayload;
import com.leclowndu93150.thaumaturge.network.ServerboundFocusChangePayload;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

/**
 * A focus picker you can point at with a VR controller: every focus in the inventory and in focus
 * pouches sits on a ring, "remove focus" in the middle, and the Architect area controls (normally
 * Ctrl/Shift + G, which VR has no keyboard for) underneath.
 *
 * <p>It sends Thaumaturge's own packets, so the server side is unchanged.
 */
public final class FocusPickerScreen extends Screen {
    private static final int INVENTORY_SIZE = 36;
    /** Larger than a slot so a controller laser can hit it comfortably. */
    private static final int CELL = 26;
    private static final int GOLD = 0xFFE0B040;
    private static final int HOVER = 0x80FFFFFF;
    private static final int CELL_BACK = 0x60000000;
    /** {@code CasterManager.toggleMisc} modes: grow the area, cycle its axis. */
    private static final int MISC_GROW = 0;
    private static final int MISC_AXIS = 1;

    private final List<Entry> foci = new ArrayList<>();
    private final List<ItemStack> demo;
    private @Nullable String current;

    public FocusPickerScreen() {
        this(List.of());
    }

    /** Opens with the given stacks as fake foci; only for the dev self-test (no player needed). */
    FocusPickerScreen(List<ItemStack> demo) {
        super(Component.translatable("screen.visinhand.focus_picker"));
        this.demo = demo;
    }

    /** The caster the player holds, main hand first, or {@code null}. */
    public static @Nullable ItemStack heldCaster(Player player) {
        if (player.getMainHandItem().getItem() instanceof FocusCaster) {
            return player.getMainHandItem();
        }
        if (player.getOffhandItem().getItem() instanceof FocusCaster) {
            return player.getOffhandItem();
        }
        return null;
    }

    @Override
    protected void init() {
        foci.clear();
        Player player = minecraft.player;
        if (!demo.isEmpty()) {
            for (int i = 0; i < demo.size(); i++) {
                foci.add(new Entry("demo" + i, demo.get(i)));
            }
            current = "demo0";
        } else if (player != null) {
            collectFoci(player);
        } else {
            onClose();
            return;
        }

        int cx = width / 2;
        int cy = height / 2 - 12;
        int n = foci.size();
        int radius = Math.min(Math.max(40, 20 + n * 6), Math.min(width, height) / 2 - 40);
        for (int i = 0; i < n; i++) {
            double angle = Math.toRadians(-90.0 + 360.0 * i / n);
            int x = cx + (int) Math.round(Math.cos(angle) * radius) - CELL / 2;
            int y = cy + (int) Math.round(Math.sin(angle) * radius) - CELL / 2;
            Entry entry = foci.get(i);
            addRenderableWidget(new ItemCell(x, y, entry.stack(), entry.stack().getHoverName(),
                    entry.key().equals(current), () -> choose(entry.key())));
        }
        addRenderableWidget(new ItemCell(cx - CELL / 2, cy - CELL / 2, new ItemStack(Items.BARRIER),
                Component.translatable("screen.visinhand.remove_focus"), false,
                () -> choose(CasterManager.REMOVE_FOCUS)));

        int rowY = Math.min(height - 28, cy + radius + CELL);
        addRenderableWidget(Button.builder(Component.translatable("screen.visinhand.area_size"),
                        b -> send(new ServerboundCasterKeyPayload(MISC_GROW)))
                .bounds(cx - 154, rowY, 100, 20)
                .tooltip(Tooltip.create(Component.translatable("screen.visinhand.area_size.tip")))
                .build());
        addRenderableWidget(Button.builder(Component.translatable("screen.visinhand.area_axis"),
                        b -> send(new ServerboundCasterKeyPayload(MISC_AXIS)))
                .bounds(cx - 50, rowY, 100, 20)
                .tooltip(Tooltip.create(Component.translatable("screen.visinhand.area_axis.tip")))
                .build());
        addRenderableWidget(Button.builder(Component.translatable("gui.done"), b -> onClose())
                .bounds(cx + 54, rowY, 100, 20)
                .build());
    }

    private void collectFoci(Player player) {
        Map<String, ItemStack> sorted = new TreeMap<>();
        List<ItemStack> main = player.getInventory().items;
        for (int slot = 0; slot < Math.min(INVENTORY_SIZE, main.size()); slot++) {
            ItemStack stack = main.get(slot);
            addFocus(sorted, stack);
            if (stack.getItem() instanceof FocusPouchItem) {
                addPouch(sorted, stack);
            }
        }
        if (ModList.get().isLoaded("curios")) {
            for (ItemStack curio : ThaumaturgeCuriosCompat.equippedCurios(player)) {
                if (curio.getItem() instanceof FocusPouchItem) {
                    addPouch(sorted, curio);
                }
            }
        }
        sorted.forEach((key, stack) -> foci.add(new Entry(key, stack)));

        ItemStack caster = heldCaster(player);
        if (caster != null && caster.getItem() instanceof FocusCaster fc) {
            ItemStack socketed = fc.socketedFocus(caster);
            current = FocusItems.isFocus(socketed) ? FocusItems.sortKey(socketed) : null;
        }
    }

    private static void addPouch(Map<String, ItemStack> sorted, ItemStack pouch) {
        for (ItemStack stack : FocusPouchItem.getInventory(pouch)) {
            addFocus(sorted, stack);
        }
    }

    private static void addFocus(Map<String, ItemStack> sorted, ItemStack stack) {
        if (FocusItems.isFocus(stack)) {
            String key = FocusItems.sortKey(stack);
            if (key != null) {
                sorted.putIfAbsent(key, stack.copy());
            }
        }
    }

    private void choose(String focusKey) {
        send(new ServerboundFocusChangePayload(focusKey));
        onClose();
    }

    private static void send(CustomPacketPayload payload) {
        PacketDistributor.sendToServer(payload);
        ClientVR.tap();
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);
        graphics.drawCenteredString(font, title, width / 2, 16, 0xFFFFFF);
        if (foci.isEmpty()) {
            graphics.drawCenteredString(font, Component.translatable("screen.visinhand.no_foci"),
                    width / 2, height / 2 + 6, 0xA0A0A0);
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (TTKeybinds.CHANGE_FOCUS.matches(keyCode, scanCode)) {
            onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private record Entry(String key, ItemStack stack) {}

    /** A square button showing one item; gold frame for the focus already in the wand. */
    private static final class ItemCell extends AbstractWidget {
        private final ItemStack stack;
        private final boolean selected;
        private final Runnable action;

        ItemCell(int x, int y, ItemStack stack, Component name, boolean selected, Runnable action) {
            super(x, y, CELL, CELL, name);
            this.stack = stack;
            this.selected = selected;
            this.action = action;
            setTooltip(Tooltip.create(name));
        }

        @Override
        protected void renderWidget(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            int x = getX();
            int y = getY();
            graphics.fill(x, y, x + width, y + height, CELL_BACK);
            if (selected) {
                graphics.renderOutline(x, y, width, height, GOLD);
            }
            if (isHoveredOrFocused()) {
                graphics.fill(x + 1, y + 1, x + width - 1, y + height - 1, HOVER);
            }
            graphics.renderItem(stack, x + (width - 16) / 2, y + (height - 16) / 2);
        }

        @Override
        public void onClick(double mouseX, double mouseY) {
            action.run();
        }

        @Override
        protected void updateWidgetNarration(NarrationElementOutput output) {
            defaultButtonNarrationText(output);
        }
    }
}
