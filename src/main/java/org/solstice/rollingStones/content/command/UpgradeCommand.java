package org.solstice.rollingStones.content.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.solstice.rollingStones.content.upgrade.Upgrade;
import org.solstice.rollingStones.content.upgrade.UpgradeHelper;

import java.util.Collection;

public class UpgradeCommand {

	private static final SimpleCommandExceptionType FAILED_EXCEPTION = new SimpleCommandExceptionType(Text.translatable("commands.enchant.failed"));

	public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess) {
		LiteralArgumentBuilder<ServerCommandSource> builder = CommandManager.literal("upgrade").requires((source) -> source.hasPermissionLevel(2));

		builder.then(CommandManager.argument("targets", EntityArgumentType.entities())
			.then(CommandManager.argument("upgrade", UpgradeArgumentType.upgrade(registryAccess))
				.executes(context -> execute(
					context.getSource(),
					EntityArgumentType.getEntities(context, "targets"),
					UpgradeArgumentType.getUpgrade(context, "upgrade")
				)
			)
			.then(CommandManager.argument("level", IntegerArgumentType.integer(0))
				.executes(context -> execute(
					context.getSource(),
					EntityArgumentType.getEntities(context, "targets"),
					UpgradeArgumentType.getUpgrade(context, "upgrade"),
					IntegerArgumentType.getInteger(context, "level"))
				)
			)
		));

		dispatcher.register(builder);
	}

	private static int execute(
		ServerCommandSource source,
		Collection<? extends Entity> targets,
		RegistryEntry<Upgrade> entry
	) throws CommandSyntaxException {
		return execute(source, targets, entry, entry.value().getDefinition().maxTier());
	}

	private static int execute(
		ServerCommandSource source,
		Collection<? extends Entity> targets,
		RegistryEntry<Upgrade> entry,
		int tier
	) throws CommandSyntaxException {
		int successfulUpgrades = 0;
		for (Entity entity : targets) {
			if (!(entity instanceof LivingEntity target)) continue;

			ItemStack stack = target.getMainHandStack();
			if (stack.isEmpty()) continue;

			UpgradeHelper.apply(stack, builder -> builder.set(entry, tier));
			++successfulUpgrades;
		}

		if (successfulUpgrades == 0) throw FAILED_EXCEPTION.create();

		Text message;
		if (targets.size() == 1) message = Text.translatable("commands.upgrade.success.single",
			Upgrade.getSimpleName(entry, tier), targets.iterator().next().getDisplayName()
		);
		else message = Text.translatable("commands.upgrade.success.multiple",
			Upgrade.getSimpleName(entry, tier), targets.size()
		);
		source.sendFeedback(() -> message, true);

		return successfulUpgrades;
	}

}
