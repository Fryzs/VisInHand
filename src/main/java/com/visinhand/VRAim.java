package com.visinhand;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.vivecraft.api.VRAPI;
import org.vivecraft.api.data.VRBodyPartData;
import org.vivecraft.api.data.VRPose;

/**
 * Where a VR player's controller is and where it points. Every method returns {@code null}
 * (or the given fallback) for players who are not in VR, so callers keep Thaumaturge's
 * original head-based behaviour for them.
 */
public final class VRAim {
    private static final String THAUMATURGE = "thaumaturge";

    private VRAim() {}

    /** The controller holding {@code hand}, or {@code null} when the entity is not a VR player. */
    public static @Nullable VRBodyPartData controller(@Nullable Entity entity, InteractionHand hand) {
        if (!(entity instanceof Player player)) {
            return null;
        }
        try {
            VRAPI api = VRAPI.instance();
            if (!api.isVRPlayer(player)) {
                return null;
            }
            VRPose pose = api.getVRPose(player);
            return pose == null ? null : pose.getHand(hand);
        } catch (RuntimeException | LinkageError e) {
            // Never break a cast because of a Vivecraft hiccup; fall back to the headset.
            VisInHand.LOGGER.debug("Could not read VR pose", e);
            return null;
        }
    }

    /** The controller of the hand that holds a Thaumaturge item, preferring the main hand. */
    public static @Nullable VRBodyPartData toolController(@Nullable Entity entity) {
        return entity instanceof LivingEntity living ? controller(living, toolHand(living)) : null;
    }

    /** The hand holding a Thaumaturge item: main hand first, then off hand, else main hand. */
    public static InteractionHand toolHand(LivingEntity entity) {
        if (isThaumaturgeItem(entity.getMainHandItem())) {
            return InteractionHand.MAIN_HAND;
        }
        if (isThaumaturgeItem(entity.getOffhandItem())) {
            return InteractionHand.OFF_HAND;
        }
        return InteractionHand.MAIN_HAND;
    }

    private static boolean isThaumaturgeItem(ItemStack stack) {
        return !stack.isEmpty() && THAUMATURGE.equals(BuiltInRegistries.ITEM.getKey(stack.getItem()).getNamespace());
    }

    /** Same as {@link Entity#pick} but casting the ray from the controller. */
    public static HitResult pick(Entity entity, VRBodyPartData controller, double range, boolean fluids) {
        Vec3 from = controller.getPos();
        Vec3 to = from.add(controller.getDir().scale(range));
        return entity.level().clip(new ClipContext(
                from,
                to,
                ClipContext.Block.OUTLINE,
                fluids ? ClipContext.Fluid.ANY : ClipContext.Fluid.NONE,
                entity));
    }
}
