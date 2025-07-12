package org.solstice.rollingStones.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BowItem;
import org.solstice.rollingStones.registry.RollingAttributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(BowItem.class)
public class BowItemMixin {

	@Unique
	private static float getPullProgress(LivingEntity user, int useTicks) {
		float multiplier = (float)useTicks / 20.0F;
		multiplier = (multiplier * multiplier + multiplier * 2.0F) / 4.0F;
		float maxPullModifier = (float) user.getAttributeValue(RollingAttributes.MAX_PULL_TIME_MULTIPLIER);
		return Math.min(multiplier, maxPullModifier);
	}

	@WrapOperation(method = "onStoppedUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/BowItem;getPullProgress(I)F"))
	private float getPullProgressWithModifier(int useTicks, Operation<Float> original, @Local(argsOnly = true) LivingEntity user) {
		return getPullProgress(user, useTicks);
	}

	@ModifyArg(
		method = "onStoppedUsing",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/item/BowItem;shootAll(Lnet/minecraft/server/world/ServerWorld;Lnet/minecraft/entity/LivingEntity;Lnet/minecraft/util/Hand;Lnet/minecraft/item/ItemStack;Ljava/util/List;FFZLnet/minecraft/entity/LivingEntity;)V"
		),
		index = 7
	)
	private boolean isCritical(boolean x, @Local(argsOnly = true) LivingEntity user, @Local float power) {
		float maxPullModifier = (float) user.getAttributeValue(RollingAttributes.MAX_PULL_TIME_MULTIPLIER);
		float criticalWindow = maxPullModifier * 0.2F;
		return power > maxPullModifier - criticalWindow && power < maxPullModifier;
	}

}
