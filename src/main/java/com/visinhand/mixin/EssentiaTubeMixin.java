package com.visinhand.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.visinhand.VRAim;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.vivecraft.api.data.VRBodyPartData;

/** Configuring a tube side with the wand picks the side the controller points at. */
@Pseudo
@Mixin(targets = "com.leclowndu93150.thaumaturge.content.essentia.tube.BlockEssentiaTransport")
public abstract class EssentiaTubeMixin {
    @WrapOperation(require = 0, 
            method = "traceLook",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;pick(DFZ)Lnet/minecraft/world/phys/HitResult;"))
    private static HitResult visinhand$tubeFromController(
            Player player, double range, float partialTick, boolean fluids, Operation<HitResult> original) {
        VRBodyPartData controller = VRAim.toolController(player);
        return controller != null
                ? VRAim.pick(player, controller, range, fluids)
                : original.call(player, range, partialTick, fluids);
    }
}
