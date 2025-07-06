package org.solstice.rollingStones.content.command;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.command.ServerCommandSource;
import org.solstice.euclidsElements.api.command.ListedRegistryEntryArgumentType;
import org.solstice.rollingStones.content.upgrade.Upgrade;
import org.solstice.rollingStones.registry.RollingRegistryKeys;

public class UpgradeArgumentType extends ListedRegistryEntryArgumentType<Upgrade> {

	protected UpgradeArgumentType(CommandRegistryAccess access) {
		super(access, RollingRegistryKeys.UPGRADE, Upgrade.ENTRY_CODEC);
	}

	public static UpgradeArgumentType upgrade(CommandRegistryAccess access) {
		return new UpgradeArgumentType(access);
	}

	public static RegistryEntry<Upgrade> getUpgrade(CommandContext<ServerCommandSource> context, String argument) {
		return getArgument(context, argument);
	}

}
