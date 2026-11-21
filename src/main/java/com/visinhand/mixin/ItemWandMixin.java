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

/** Draining a node and refining a vis crystal target what the wand points at. */
@Pseudo
@Mixin(targets = "com.leclowndu93150.thaumaturge.content.wands.ItemWand")
public abstract class ItemWandMixin {
    private static final String PICK =
            "Lnet/minecraft/world/entity/player/Player;pick(DFZ)Lnet/minecraft/world/phys/HitResult;";

    @WrapOperation(require = 0, method = "targetedNode", at = @At(value = "INVOKE", target = PICK))
    private static HitResult visinhand$nodeFromController(
            Player player, double range, float partialTick, boolean fluids, Operation<HitResult> original) {
        VRBodyPartData controller = VRAim.toolController(player);
        return controller != null
                ? VRAim.pick(player, controller, range, fluids)
                : original.call(player, range, partialTick, fluids);
    }

    @WrapOperation(require = 0, method = "refineTarget", at = @At(value = "INVOKE", target = PICK))
    private HitResult visinhand$refineFromController(
            Player player, double range, float partialTick, boolean fluids, Operation<HitResult> original) {
        VRBodyPartData controller = VRAim.toolController(player);
        return controller != null
                ? VRAim.pick(player, controller, range, fluids)
                : original.call(player, range, partialTick, fluids);
    }
}
