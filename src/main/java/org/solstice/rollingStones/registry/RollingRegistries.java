package org.solstice.rollingStones.registry;

import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import org.solstice.rollingStones.content.upgrade.Upgrade;

public class RollingRegistries {

	public static void init() {
		DynamicRegistries.registerSynced(RollingRegistryKeys.UPGRADE, Upgrade.CODEC, Upgrade.CODEC);
	}

}
