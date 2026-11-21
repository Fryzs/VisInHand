package com.visinhand.client;

import com.leclowndu93150.thaumaturge.client.input.TTKeybinds;
import com.visinhand.VisInHand;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

/**
 * In VR the "Change focus" binding opens {@link FocusPickerScreen} instead of Thaumaturge's
 * mouse-driven radial (which is switched off for VR in {@code CasterKeyHandlerMixin}).
 */
@EventBusSubscriber(modid = VisInHand.MOD_ID, value = Dist.CLIENT)
public final class VRFocusInput {
    private VRFocusInput() {}

    @SubscribeEvent
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
}
