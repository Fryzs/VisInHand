package com.visinhand.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.visinhand.VRAim;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.vivecraft.api.data.VRBodyPartData;

/** Picking the item form of a scanned block uses the same controller ray as the scan itself. */
@Pseudo
@Mixin(targets = "com.leclowndu93150.thaumaturge.api.research.scan.ScanningManager")
public abstract class ScanningManagerMixin {
    @WrapOperation(require = 0, 
            method = "blockStack",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getEyePosition()Lnet/minecraft/world/phys/Vec3;"))
    private static Vec3 visinhand$scanFrom(Player player, Operation<Vec3> original) {
        VRBodyPartData controller = VRAim.toolController(player);
        return controller != null ? controller.getPos() : original.call(player);
    }

    @WrapOperation(require = 0, 
            method = "blockStack",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getViewVector(F)Lnet/minecraft/world/phys/Vec3;"))
    private static Vec3 visinhand$scanAlong(Player player, float partialTick, Operation<Vec3> original) {
        VRBodyPartData controller = VRAim.toolController(player);
        return controller != null ? controller.getDir() : original.call(player, partialTick);
    }
}
