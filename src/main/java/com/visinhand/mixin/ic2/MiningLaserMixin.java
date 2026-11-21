package com.visinhand.mixin.ic2;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.visinhand.VRAim;
import ic2.core.util.Vector3;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.vivecraft.api.data.VRBodyPartData;

/**
 * IC2's mining laser fires from {@code Util.getEyePosition} along {@code Util.getLook} (the
 * head). For VR players both come from the controller holding the laser instead.
 */
@Pseudo
@Mixin(targets = "ic2.core.item.tool.ItemToolMiningLaser")
public abstract class MiningLaserMixin {
    private static final String IC2 = "ic2";
    // shootLaser is overloaded; a bare name would only match the first overload.
    private static final String SHOOT_FROM_ENTITY =
            "shootLaser(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/LivingEntity;FFIZZ)Z";
    private static final String SHOOT_FROM_POINT =
            "shootLaser(Lnet/minecraft/world/level/Level;Lic2/core/util/Vector3;Lnet/minecraft/world/entity/LivingEntity;FFIZZ)Z";

    @WrapOperation(
            require = 0,
            method = {SHOOT_FROM_ENTITY, SHOOT_FROM_POINT, "onItemUseFirst", "use"},
            at = @At(value = "INVOKE", target = "Lic2/core/util/Util;getEyePosition(Lnet/minecraft/world/entity/Entity;)Lic2/core/util/Vector3;"))
    private Vector3 visinhand$laserFrom(Entity shooter, Operation<Vector3> original) {
        VRBodyPartData controller = VRAim.toolController(shooter, IC2);
        return controller != null ? new Vector3(controller.getPos()) : original.call(shooter);
    }

    @WrapOperation(
            require = 0,
            method = {SHOOT_FROM_ENTITY, SHOOT_FROM_POINT, "onItemUseFirst", "use"},
            at = @At(value = "INVOKE", target = "Lic2/core/util/Util;getLook(Lnet/minecraft/world/entity/Entity;)Lic2/core/util/Vector3;"))
    private Vector3 visinhand$laserAlong(Entity shooter, Operation<Vector3> original) {
        VRBodyPartData controller = VRAim.toolController(shooter, IC2);
        return controller != null ? new Vector3(controller.getDir()) : original.call(shooter);
    }
}
