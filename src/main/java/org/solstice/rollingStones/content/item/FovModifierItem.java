package org.solstice.rollingStones.content.item;

import net.minecraft.entity.player.PlayerEntity;

public interface FovModifierItem {

	default float getFovMultiplier(PlayerEntity player, float fov) {
		return fov;
	}

}
