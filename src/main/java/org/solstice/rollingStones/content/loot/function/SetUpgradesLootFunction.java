package org.solstice.rollingStones.content.loot.function;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProviderTypes;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.MathHelper;
import org.solstice.rollingStones.content.item.component.ItemUpgradesComponent;
import org.solstice.rollingStones.content.upgrade.Upgrade;
import org.solstice.rollingStones.registry.RollingComponentTypes;
import org.solstice.rollingStones.registry.RollingLootFunctionTypes;

import java.util.List;
import java.util.Map;

public class SetUpgradesLootFunction extends ConditionalLootFunction {

	public static final MapCodec<SetUpgradesLootFunction> CODEC = RecordCodecBuilder.mapCodec(
		instance -> addConditionsField(instance)
			.and(
				instance.group(
					Codec.unboundedMap(Upgrade.ENTRY_CODEC, LootNumberProviderTypes.CODEC)
						.optionalFieldOf("upgrades", Map.of())
						.forGetter(function -> function.upgrades),
					Codec.BOOL.fieldOf("add").orElse(false).forGetter(function -> function.add)
				)
			)
			.apply(instance, SetUpgradesLootFunction::new)
	);
	private final Map<RegistryEntry<Upgrade>, LootNumberProvider> upgrades;
	private final boolean add;

	public SetUpgradesLootFunction(List<LootCondition> conditions, Map<RegistryEntry<Upgrade>, LootNumberProvider> upgrades, boolean add) {
		super(conditions);
		this.upgrades = Map.copyOf(upgrades);
		this.add = add;
	}

	@Override
	public LootFunctionType<SetUpgradesLootFunction> getType() {
		return RollingLootFunctionTypes.SET_UPGRADES;
	}

	@Override
	protected ItemStack process(ItemStack stack, LootContext context) {
		ItemUpgradesComponent component = stack.get(RollingComponentTypes.UPGRADES);
		ItemUpgradesComponent.Builder builder = ItemUpgradesComponent.Builder.of(component);

		if (this.add) this.upgrades.forEach((upgrade, level) ->
			builder.set(upgrade, MathHelper.clamp(builder.getTier(upgrade) + level.nextInt(context), 0, 255))
		);
		else this.upgrades.forEach((upgrade, level) ->
			builder.set(upgrade, MathHelper.clamp(level.nextInt(context), 0, 255))
		);

		stack.set(RollingComponentTypes.UPGRADES, builder.build());
		return stack;
	}


	public static class Builder extends ConditionalLootFunction.Builder<SetUpgradesLootFunction.Builder> {

		private final ImmutableMap.Builder<RegistryEntry<Upgrade>, LootNumberProvider> upgrades;
		private final boolean add;

		public Builder(boolean add) {
			this.upgrades = ImmutableMap.builder();
			this.add = add;
		}

		protected SetUpgradesLootFunction.Builder getThisBuilder() {
			return this;
		}

		public SetUpgradesLootFunction.Builder upgrade(RegistryEntry<Upgrade> upgrade, LootNumberProvider tier) {
			this.upgrades.put(upgrade, tier);
			return this;
		}

		public LootFunction build() {
			return new SetUpgradesLootFunction(this.getConditions(), this.upgrades.build(), this.add);
		}

	}

}
