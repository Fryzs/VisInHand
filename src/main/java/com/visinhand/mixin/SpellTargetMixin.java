package com.visinhand.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.visinhand.VRAim;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.vivecraft.api.data.VRBodyPartData;

/**
 * Wand spells start at the controller and fly where it points.
 *
 * <p>{@code SpellTarget.wandOrigin(Player, InteractionHand)} is the authoritative origin of every
 * wand cast (server and client), and {@code visualWandOrigin} places the wand-tip effects.
 */
@Pseudo
@Mixin(targets = "com.leclowndu93150.thaumaturge.api.spell.cast.SpellTarget")
public abstract class SpellTargetMixin {
    /** Roughly the length of a held wand, so effects leave its tip rather than the grip. */
    private static final double WAND_TIP = 0.45;

    @WrapOperation(require = 0, 
            method = "wandOrigin",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getEyePosition()Lnet/minecraft/world/phys/Vec3;"))
    private static Vec3 visinhand$castFromController(
            Player caster, Operation<Vec3> original, @Local(argsOnly = true) InteractionHand hand) {
        VRBodyPartData controller = VRAim.controller(caster, hand);
        return controller != null ? controller.getPos() : original.call(caster);
    }

    @WrapOperation(require = 0, 
            method = "wandOrigin",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;getLookAngle()Lnet/minecraft/world/phys/Vec3;"))
    private static Vec3 visinhand$aimWithController(
            Player caster, Operation<Vec3> original, @Local(argsOnly = true) InteractionHand hand) {
        VRBodyPartData controller = VRAim.controller(caster, hand);
        return controller != null ? controller.getDir() : original.call(caster);
    }

    // Thaumaturge 1.1.0 and older: wand casts go through SpellTarget.origin(LivingEntity), which
    // builds the start point as position().add(0, eyeHeight - 0.1, 0) and heads along getLookAngle().

    @WrapOperation(require = 0, 
            method = "origin",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/phys/Vec3;add(DDD)Lnet/minecraft/world/phys/Vec3;"))
    private static Vec3 visinhand$legacyCastFromController(
            Vec3 position, double x, double y, double z, Operation<Vec3> original,
            @Local(argsOnly = true) LivingEntity caster) {
        VRBodyPartData controller = VRAim.toolController(caster);
        return controller != null ? controller.getPos() : original.call(position, x, y, z);
    }

    @WrapOperation(require = 0, 
            method = "origin",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;getLookAngle()Lnet/minecraft/world/phys/Vec3;"))
    private static Vec3 visinhand$legacyAimWithController(LivingEntity caster, Operation<Vec3> original) {
        VRBodyPartData controller = VRAim.toolController(caster);
        return controller != null ? controller.getDir() : original.call(caster);
    }

    @Inject(require = 0, method = "visualWandOrigin", at = @At("HEAD"), cancellable = true)
    private static void visinhand$wandTip(Player caster, InteractionHand hand, CallbackInfoReturnable<Vec3> cir) {
        VRBodyPartData controller = VRAim.controller(caster, hand);
        if (controller != null) {
            cir.setReturnValue(controller.getPos().add(controller.getDir().scale(WAND_TIP)));
        }
    }
}
