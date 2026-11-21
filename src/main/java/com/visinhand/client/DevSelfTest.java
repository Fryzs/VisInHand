package com.visinhand.client;

import com.visinhand.VisInHand;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

/**
 * Dev-only check, enabled by {@code -Dvisinhand.selftest=true} ({@code ./gradlew runSelftest}):
 * waits for the first menu, opens the focus picker with sample foci, saves
 * {@code run/screenshots/visinhand_selftest.png} and quits the game. A watchdog closes the game
 * after two minutes even if a step never happens.
 */
@EventBusSubscriber(modid = VisInHand.MOD_ID, value = Dist.CLIENT)
public final class DevSelfTest {
    private static final boolean ENABLED = Boolean.getBoolean("visinhand.selftest");
    private static final int SETTLE_TICKS = 40;
    private static final int RENDER_TICKS = 20;

    private static final int WATCHDOG_TICKS = 20 * 120;

    private static int ticks;
    private static int totalTicks;
    private static int stage;

    private DevSelfTest() {}

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        if (!ENABLED) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        // Watchdog: whatever happens, never leave a test game running.
        if (++totalTicks > WATCHDOG_TICKS && stage < 3) {
            VisInHand.LOGGER.warn("[selftest] watchdog: giving up after {} ticks, closing the game", totalTicks);
            stage = 3;
            mc.stop();
            return;
        }
        switch (stage) {
            case 0 -> {
                // Any menu once loading is over (a fresh profile shows the accessibility screen first).
                boolean loaded = mc.getOverlay() == null && mc.screen != null && mc.player == null;
                if (loaded && ++ticks >= SETTLE_TICKS) {
                    List<ItemStack> sample = BuiltInRegistries.ITEM.keySet().stream()
                            .filter(id -> id.getNamespace().equals("thaumaturge") && id.getPath().contains("focus"))
                            .sorted()
                            .limit(7)
                            .map(id -> new ItemStack(BuiltInRegistries.ITEM.get(id)))
                            .toList();
                    VisInHand.LOGGER.info("[selftest] opening focus picker with {} sample foci", sample.size());
                    mc.setScreen(new FocusPickerScreen(sample));
                    ticks = 0;
                    stage = 1;
                }
            }
            case 1 -> {
                if (++ticks >= RENDER_TICKS) {
                    Screenshot.grab(mc.gameDirectory, "visinhand_selftest.png", mc.getMainRenderTarget(),
                            msg -> VisInHand.LOGGER.info("[selftest] {}", msg.getString()));
                    ticks = 0;
                    stage = 2;
                }
            }
            case 2 -> {
                if (++ticks >= 5) {
                    VisInHand.LOGGER.info("[selftest] done, closing the game");
                    stage = 3;
                    mc.stop();
                }
            }
            default -> {}
        }
    }
}
