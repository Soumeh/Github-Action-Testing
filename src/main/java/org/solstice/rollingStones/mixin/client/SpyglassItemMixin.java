package org.solstice.rollingStones.mixin.client;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.SpyglassItem;
import org.solstice.rollingStones.content.item.FovModifierItem;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(SpyglassItem.class)
public class SpyglassItemMixin implements FovModifierItem {

	@Environment(EnvType.CLIENT)
	@Override
	public float getFovMultiplier(MinecraftClient client, PlayerEntity player, float fov) {
		if (client.options.getPerspective().isFirstPerson() && player.isUsingItem()) return 0.1F;
		return fov;
	}

}
