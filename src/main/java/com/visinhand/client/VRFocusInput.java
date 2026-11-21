package com.visinhand.client;

import com.leclowndu93150.thaumaturge.client.input.TTKeybinds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import java.util.List;

/**
 * In VR the "Change focus" binding opens {@link FocusPickerScreen} instead of Thaumaturge's
 * mouse-driven radial (which is switched off for VR in {@code CasterKeyHandlerMixin}).
 * Registered by {@link VisInHandClient} only when Thaumaturge is installed.
 */
public final class VRFocusInput {
    private VRFocusInput() {}

    public static void onClientTick(ClientTickEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || !ClientVR.isActive()) {
            return;
        }
        boolean pressed = false;
        while (TTKeybinds.CHANGE_FOCUS.consumeClick()) {
            pressed = true;
        }
        if (pressed && mc.screen == null && FocusPickerScreen.heldCaster(mc.player) != null) {
            mc.setScreen(new FocusPickerScreen());
        }
    }

    /** The picker filled with sample foci, for the dev self-test. */
    static Screen demoPicker(List<ItemStack> sample) {
        return new FocusPickerScreen(sample);
    }
}
