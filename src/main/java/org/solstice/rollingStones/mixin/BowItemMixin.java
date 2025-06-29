package org.solstice.rollingStones.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import org.solstice.rollingStones.content.item.FovModifierItem;
import org.solstice.rollingStones.registry.RollingAttributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(BowItem.class)
public class BowItemMixin implements FovModifierItem {

	@Unique
	private static float getMaxPullMultiplier(LivingEntity user) {
		return (float) user.getAttributeValue(RollingAttributes.MAX_PULL_TIME_MULTIPLIER);
	}

	@Unique
	private static float getPullProgress(LivingEntity user, int useTicks) {
		float multiplier = (float)useTicks / 20.0F;
		multiplier = (multiplier * multiplier + multiplier * 2.0F) / 3.0F;
		return Math.min(multiplier, getMaxPullMultiplier(user));
	}

	@Redirect(method = "onStoppedUsing", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/BowItem;getPullProgress(I)F"))
	private float getPullProgressWithModifier(int useTicks, @Local(argsOnly = true) LivingEntity user) {
		return getPullProgress(user, useTicks);
	}

	@Override
	public float getFovMultiplier(MinecraftClient client, PlayerEntity player, float fov) {
		if (!player.isUsingItem()) return fov;

		int useTicks = player.getItemUseTime();
		float multiplier = (float) useTicks / 20.0F;
		multiplier = Math.min(multiplier, getMaxPullMultiplier(player));
		multiplier *= multiplier;
		return fov * (1.0F - multiplier * 0.15F);
	}

}
