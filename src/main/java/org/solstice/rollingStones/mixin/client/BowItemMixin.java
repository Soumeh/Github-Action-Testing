package org.solstice.rollingStones.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import org.solstice.rollingStones.content.item.FovModifierItem;
import org.solstice.rollingStones.registry.RollingAttributes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(BowItem.class)
public class BowItemMixin implements FovModifierItem {

	@Unique
	private static float getMaxPullMultiplier(LivingEntity user) {
		return (float) user.getAttributeValue(RollingAttributes.MAX_PULL_TIME_MULTIPLIER);
	}

	@Environment(EnvType.CLIENT)
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
