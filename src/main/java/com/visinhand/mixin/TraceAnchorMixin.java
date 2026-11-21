package com.visinhand.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.visinhand.VRAim;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.vivecraft.api.data.VRBodyPartData;

/**
 * Area tools anchor on the block the controller points at: the Architect focus delivery and the
 * Elemental Shovel, both through {@code traceAnchor(ItemStack, Level, LivingEntity)}.
 */
@Pseudo
@Mixin(targets = {
        "com.leclowndu93150.thaumaturge.content.spell.delivery.ArchitectDelivery",
        "com.leclowndu93150.thaumaturge.content.equipment.ElementalShovelItem"
})
public abstract class TraceAnchorMixin {
    @WrapOperation(require = 0, 
            method = "traceAnchor",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;pick(DFZ)Lnet/minecraft/world/phys/HitResult;"))
    private HitResult visinhand$anchorFromController(
            LivingEntity caster, double range, float partialTick, boolean fluids, Operation<HitResult> original) {
        VRBodyPartData controller = VRAim.toolController(caster);
        return controller != null
                ? VRAim.pick(caster, controller, range, fluids)
                : original.call(caster, range, partialTick, fluids);
    }

    // Thaumaturge 1.1.0: ArchitectDelivery traces its own ray from getEyePosition() along getLookAngle().

    @WrapOperation(require = 0, 
            method = "traceAnchor",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getEyePosition()Lnet/minecraft/world/phys/Vec3;"))
    private Vec3 visinhand$anchorFrom(LivingEntity caster, Operation<Vec3> original) {
        VRBodyPartData controller = VRAim.toolController(caster);
        return controller != null ? controller.getPos() : original.call(caster);
    }

    @WrapOperation(require = 0, 
            method = "traceAnchor",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getLookAngle()Lnet/minecraft/world/phys/Vec3;"))
    private Vec3 visinhand$anchorAlong(LivingEntity caster, Operation<Vec3> original) {
        VRBodyPartData controller = VRAim.toolController(caster);
        return controller != null ? controller.getDir() : original.call(caster);
    }
}
