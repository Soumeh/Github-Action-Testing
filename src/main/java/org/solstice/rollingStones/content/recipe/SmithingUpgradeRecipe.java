package org.solstice.rollingStones.content.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.*;
import net.minecraft.recipe.input.SmithingRecipeInput;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.World;
import org.solstice.rollingStones.content.item.component.ItemUpgradesComponent;
import org.solstice.rollingStones.content.upgrade.Upgrade;
import org.solstice.rollingStones.registry.*;

import java.util.stream.Stream;

public record SmithingUpgradeRecipe (
        Ingredient base,
//        Ingredient material,
		Ingredient upgrade,
		int tier,
		boolean requiresPreviousTier
) implements SmithingRecipe {

    public static final MapCodec<SmithingUpgradeRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("base").forGetter(SmithingUpgradeRecipe::base),
//		Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("material").forGetter(SmithingUpgradeRecipe::material),
		Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("upgrade").forGetter(SmithingUpgradeRecipe::upgrade),
		Codec.INT.fieldOf("tier").forGetter(SmithingUpgradeRecipe::tier),
		Codec.BOOL.optionalFieldOf("requires_previous_tier", true).forGetter(SmithingUpgradeRecipe::requiresPreviousTier)
	).apply(instance, SmithingUpgradeRecipe::new));

//    @Override
//    public RecipeType<?> getType() {
//        return RollingRecipeTypes.SMITHING_UPGRADE;
//    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RollingRecipeSerializers.SMITHING_UPGRADE;
    }

	@Override
	public boolean matches(SmithingRecipeInput input, World world) {
		return this.base.test(input.base()) &&
			Registries.ITEM.getEntry(input.addition().getItem()).isIn(RollingTags.UPGRADE_MATERIALS) &&
			this.upgrade.test(input.template());
	}

	@Override
	public ItemStack craft(SmithingRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
		if (!this.base.test(input.base())) return ItemStack.EMPTY;

		RegistryEntry<Item> materialEntry = Registries.ITEM.getEntry(input.addition().getItem());
		RegistryEntry<Upgrade> upgrade = materialEntry.getValue(RollingTags.UPGRADE_MATERIALS);
		if (upgrade == null) return ItemStack.EMPTY;

		if (!this.upgrade.test(input.template())) return ItemStack.EMPTY;

		ItemUpgradesComponent component = input.base().getOrDefault(RollingComponentTypes.UPGRADES, null);

		ItemUpgradesComponent.Builder builder;
		if (component == null) {
			builder = ItemUpgradesComponent.Builder.create();
		} else {
			if (component.upgrades().getOrDefault(upgrade, 0) >= this.tier) return ItemStack.EMPTY;
			builder = ItemUpgradesComponent.Builder.from(component);
		}

		ItemStack result = input.base().copyWithCount(1);
		builder.set(upgrade, this.tier);
		result.set(RollingComponentTypes.UPGRADES, builder.build());
		return result;
	}

	@Override
	public ItemStack getResult(RegistryWrapper.WrapperLookup lookup) {
		return this.base.getMatchingStacks()[0];
//		ItemStack stack = new ItemStack(Items.IRON_CHESTPLATE);

//		RegistryEntry<Upgrade> upgrade = RegistryEntry.of(Upgrade.EMPTY);
//
//		ItemUpgradesComponent upgrades = stack.getOrDefault(RollingComponentTypes.UPGRADES, ItemUpgradesComponent.EMPTY);
//		if (upgrades.upgrades().getOrDefault(upgrade, 0) >= this.tier) return ItemStack.EMPTY;
//
//		ItemUpgradesComponent newUpgrades = ItemUpgradesComponent.Builder.of(upgrades)
//			.set(upgrade, this.tier)
//			.build();
//
//		stack.set(RollingComponentTypes.UPGRADES, newUpgrades);
//		return stack;
	}

	@Override
	public boolean testBase(ItemStack stack) {
		return this.base.test(stack);
	}

	@Override
	public boolean testAddition(ItemStack stack) {
		return Registries.ITEM.getEntry(stack.getItem()).isIn(RollingTags.UPGRADE_MATERIALS);
	}

	@Override
	public boolean testTemplate(ItemStack stack) {
		return this.upgrade.test(stack);
	}

	@Override
	public boolean isEmpty() {
		return Stream.of(this.base, this.upgrade).anyMatch(Ingredient::isEmpty);
	}

}
