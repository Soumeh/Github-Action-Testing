package org.solstice.rollingStones.client;

import net.fabricmc.api.ClientModInitializer;
import org.solstice.rollingStones.client.registry.*;

public class RollingStonesClient implements ClientModInitializer {

	@Override
	public void onInitializeClient() {
		RollingEntityRenderers.init();
	}

}
