package org.solstice.rollingStones.content.item;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

public interface FovModifierItem {

	@Environment(EnvType.CLIENT)
	default float getFovMultiplier(MinecraftClient client, PlayerEntity player, float fov) {
		return fov;
	}

}
