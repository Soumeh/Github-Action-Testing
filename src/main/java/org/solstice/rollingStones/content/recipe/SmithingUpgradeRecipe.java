package org.solstice.rollingStones.content.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.item.trim.ArmorTrimMaterial;
import net.minecraft.item.trim.ArmorTrimMaterials;
import net.minecraft.item.trim.ArmorTrimPattern;
import net.minecraft.recipe.*;
import net.minecraft.recipe.input.SmithingRecipeInput;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import org.solstice.rollingStones.content.item.SmithingStoneItem;
import org.solstice.rollingStones.content.item.component.ItemUpgradesComponent;
import org.solstice.rollingStones.content.upgrade.Upgrade;
import org.solstice.rollingStones.content.upgrade.UpgradeHelper;
import org.solstice.rollingStones.registry.*;

import java.util.Optional;
import java.util.stream.Stream;

public record SmithingUpgradeRecipe (
		Ingredient template,
		Ingredient base,
		Ingredient addition,
		RegistryEntry<Upgrade> upgrade,
		int tier,
		boolean requiresPreviousTier
) implements RandomizedSmithingRecipe {

    public static final MapCodec<SmithingUpgradeRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("template").forGetter(SmithingUpgradeRecipe::template),
		Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("base").forGetter(SmithingUpgradeRecipe::base),
		Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("addition").forGetter(SmithingUpgradeRecipe::addition),
		Upgrade.ENTRY_CODEC.fieldOf("upgrade").forGetter(SmithingUpgradeRecipe::upgrade),
		Codec.INT.fieldOf("tier").forGetter(SmithingUpgradeRecipe::tier),
		Codec.BOOL.optionalFieldOf("requires_previous_tier", true).forGetter(SmithingUpgradeRecipe::requiresPreviousTier)
	).apply(instance, SmithingUpgradeRecipe::new));


    @Override
    public RecipeSerializer<?> getSerializer() {
        return RollingRecipeSerializers.SMITHING_UPGRADE;
    }

	@Override
	public boolean matches(SmithingRecipeInput input, World world) {
		if (input.template().contains(RollingComponentTypes.STORED_UPGRADES))
			return this.template.test(input.template());

		return this.addition.test(input.addition())
			&& this.template.test(input.template());
	}

	@Override
	public ItemStack craft(SmithingRecipeInput input, RegistryWrapper.WrapperLookup lookup, Random random) {
		if (!this.template.test(input.template())) return ItemStack.EMPTY;
		if (!this.base.test(input.base())) return ItemStack.EMPTY;

		ItemUpgradesComponent upgrades = input.base().getOrDefault(RollingComponentTypes.UPGRADES, ItemUpgradesComponent.DEFAULT);
		ItemUpgradesComponent.Builder builder = new ItemUpgradesComponent.Builder(upgrades);

		ItemUpgradesComponent storedUpgrades = input.template().getOrDefault(RollingComponentTypes.STORED_UPGRADES, null);
		if (storedUpgrades != null) {
			if (!input.addition().isEmpty()) return ItemStack.EMPTY;
			storedUpgrades.upgrades().forEach(builder::add);
		} else {
			if (!this.addition.test(input.addition())) return ItemStack.EMPTY;

			TagKey<Item> supportedItems = this.upgrade.value().getDefinition().getSupportedItems().getTagKey().orElseThrow();
			if (!input.base().isIn(supportedItems)) return ItemStack.EMPTY;

			builder.set(this.upgrade, this.tier);
		}

		ItemUpgradesComponent newUpgrades = builder.build();
		if (upgrades.equals(newUpgrades)) return ItemStack.EMPTY;

		ItemStack result = input.base().copyWithCount(1);
		result.set(RollingComponentTypes.UPGRADES, builder.build());
		if (input.template().getItem() instanceof SmithingStoneItem item) result = item.onUpgrade(result, lookup, random);

		return result;
	}

	@Override
	public ItemStack getResult(RegistryWrapper.WrapperLookup lookup) {
		ItemStack result = new ItemStack(Items.IRON_CHESTPLATE);
		UpgradeHelper.apply(result, builder -> builder.set(this.upgrade, this.tier));
		return result;
	}

	@Override
	public boolean testTemplate(ItemStack stack) {
		return this.template.test(stack);
	}

	@Override
	public boolean testBase(ItemStack stack) {
		return this.base.test(stack);
	}

	@Override
	public boolean testAddition(ItemStack stack) {
		return this.addition.test(stack);
	}

	@Override
	public boolean isEmpty() {
		return Stream.of(this.template, this.base, this.addition)
			.anyMatch(Ingredient::isEmpty);
	}

}
