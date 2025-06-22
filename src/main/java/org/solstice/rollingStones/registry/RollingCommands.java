package org.solstice.rollingStones.registry;

import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import org.solstice.rollingStones.RollingStones;
import org.solstice.rollingStones.content.command.UpgradeArgumentType;
import org.solstice.rollingStones.content.command.UpgradeCommand;

public class RollingCommands {

	public static void init() {
		ArgumentTypeRegistry.registerArgumentType(
			RollingStones.of("upgrade"),
			UpgradeArgumentType.class, ConstantArgumentSerializer.of(UpgradeArgumentType::upgrade)
		);
		CommandRegistrationCallback.EVENT.register((dispatcher, access, environment) ->
			UpgradeCommand.register(dispatcher, access)
		);
	}

}
