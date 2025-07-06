package org.solstice.rollingStones.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BowItem;
import org.solstice.rollingStones.content.item.FovModifierItem;
import org.solstice.rollingStones.registry.RollingAttributes;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(BowItem.class)
public class BowItemMixin implements FovModifierItem {

	@Environment(EnvType.CLIENT)
	@Override
	public float getFovMultiplier(MinecraftClient client, PlayerEntity player, float fov) {
		if (!player.isUsingItem()) return fov;

		int useTicks = player.getItemUseTime();
		float multiplier = (float) useTicks / 20.0F;
		float maxPullModifier = (float) player.getAttributeValue(RollingAttributes.MAX_PULL_TIME_MULTIPLIER);
		float criticalWindow = maxPullModifier * 0.2F;

		if (multiplier > maxPullModifier - criticalWindow && multiplier < maxPullModifier)
			maxPullModifier -= criticalWindow;

		multiplier = Math.min(multiplier, maxPullModifier);
		multiplier *= multiplier;
		return fov * (1.0F - multiplier * 0.15F);
	}

}
