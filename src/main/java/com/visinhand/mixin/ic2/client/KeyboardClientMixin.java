package com.visinhand.mixin.ic2.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.visinhand.client.ClientVR;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

/**
 * IC2 sends the "forward" key to the server for jetpacks and quantum leggings. VR players walk
 * with the thumbstick, which may not press the forward key, so while in VR pushing the stick
 * forward counts as holding it.
 */
@Pseudo
@Mixin(targets = "ic2.core.util.KeyboardClient")
public abstract class KeyboardClientMixin {
    /** How far forward the stick must be pushed (0..1) to count as the key being held. */
    private static final float STICK_FORWARD = 0.3F;

    @WrapOperation(
            require = 0,
            method = "sendKeyUpdate",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;isDown()Z"))
    private boolean visinhand$stickIsForward(KeyMapping key, Operation<Boolean> original) {
        boolean down = original.call(key);
        if (down) {
            return true;
        }
        Minecraft mc = Minecraft.getInstance();
        return key == mc.options.keyUp
                && mc.player != null
                && mc.player.input.forwardImpulse > STICK_FORWARD
                && ClientVR.isActive();
    }
}
