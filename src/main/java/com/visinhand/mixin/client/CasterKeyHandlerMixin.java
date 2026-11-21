package com.visinhand.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.leclowndu93150.thaumaturge.client.input.TTKeybinds;
import com.visinhand.client.ClientVR;
import net.minecraft.client.KeyMapping;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;

/**
 * Thaumaturge's focus radial follows the mouse, which VR does not have. While in VR its handler
 * sees "Change focus" as never held, and {@code VRFocusInput} opens the pointable picker instead.
 * Everything else in the handler (the Architect "G" key) keeps working.
 */
@Pseudo
@Mixin(targets = "com.leclowndu93150.thaumaturge.client.casters.CasterKeyHandler")
public abstract class CasterKeyHandlerMixin {
    @WrapOperation(
            require = 0,
            method = "onClientTick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/KeyMapping;isDown()Z"))
    private static boolean visinhand$noRadialInVR(KeyMapping key, Operation<Boolean> original) {
        if (key == TTKeybinds.CHANGE_FOCUS && ClientVR.isActive()) {
            return false;
        }
        return original.call(key);
    }
}
