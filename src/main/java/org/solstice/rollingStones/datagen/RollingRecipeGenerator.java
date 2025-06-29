package org.solstice.rollingStones.datagen;

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.advancement.criterion.InventoryChangedCriterion;
import net.minecraft.block.Blocks;
import net.minecraft.data.DataOutput;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.ShapedRecipeJsonBuilder;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.predicate.NumberRange;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.solstice.euclidsElements.autoDatagen.api.generator.AdvancedRecipeProvider;
import org.solstice.rollingStones.api.recipe.SmithingUpgradeRecipeBuilder;
import org.solstice.rollingStones.content.upgrade.Upgrade;
import org.solstice.rollingStones.registry.RollingBlocks;
import org.solstice.rollingStones.registry.RollingItems;
import org.solstice.rollingStones.registry.RollingRegistryKeys;
import org.solstice.rollingStones.registry.RollingTags;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import static org.solstice.rollingStones.registry.RollingUpgrades.*;

public class RollingRecipeGenerator extends AdvancedRecipeProvider {

	public static final Object2IntOpenHashMap<Item> SMITHING_STONE_TIERS = new Object2IntOpenHashMap<>(Map.of(
		RollingItems.SIMPLE_SMITHING_STONE, 1,
		RollingItems.HONED_SMITHING_STONE, 2,
		RollingItems.GILDED_SMITHING_STONE, 3
	));

	public static final Map<RegistryKey<Upgrade>, Item> UPGRADE_MATERIALS = Map.of(
		DENSITY, Items.TUFF,
		DRAWBACK, Items.BLACKSTONE,
		DURABILITY, Items.COBBLESTONE,
		EFFICIENCY, Items.PAPER,
		PROTECTION, Items.IRON_INGOT,
		SHARPNESS, Items.FLINT,
		TENSION, Items.STRING
	);

	protected final DataOutput.PathResolver recipesPathResolver;
	protected final DataOutput.PathResolver advancementsPathResolver;

	public RollingRecipeGenerator(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registryLookup) {
		super(output, registryLookup);
		this.recipesPathResolver = output.getResolver(RegistryKeys.RECIPE);
		this.advancementsPathResolver = output.getResolver(RegistryKeys.ADVANCEMENT);
	}

	public void generate(RecipeExporter exporter, RegistryWrapper.WrapperLookup registryLookup) {
		ShapedRecipeJsonBuilder
			.create(RecipeCategory.DECORATIONS, RollingBlocks.STRONGBOX)
			.input('#', Blocks.DEEPSLATE)
			.pattern("###")
			.pattern("# #")
			.pattern("###")
			.criterion("has_deepslate", conditionsFromItem(Blocks.DEEPSLATE))
			.criterion("has_lots_of_items", Criteria.INVENTORY_CHANGED.create(new InventoryChangedCriterion.Conditions(
				Optional.empty(),
				new InventoryChangedCriterion.Conditions.Slots(
					NumberRange.IntRange.atLeast(20),
					NumberRange.IntRange.ANY,
					NumberRange.IntRange.ANY
				),
				List.of()
			)))
			.offerTo(exporter);

		Ingredient base = Ingredient.fromTag(RollingTags.UPGRADABLE);
		SMITHING_STONE_TIERS.forEach((smithingStoneItem, tier) -> {
			Ingredient template = Ingredient.ofItems(smithingStoneItem);
			UPGRADE_MATERIALS.forEach((upgradeKey, upgradeItem) -> {
				Ingredient addition = Ingredient.ofItems(upgradeItem);
				RegistryEntry<Upgrade> upgrade = registryLookup.getWrapperOrThrow(RollingRegistryKeys.UPGRADE).getOrThrow(upgradeKey);
				Identifier upgradeId = upgradeKey.getValue();

				String name = "upgrade/" + upgradeId.getPath() + "/tier_" + tierNames[tier - 1];

				Identifier path = Identifier.of(upgradeId.getNamespace(), name);

				SmithingUpgradeRecipeBuilder.create(RecipeCategory.MISC, template, base, addition, upgrade, tier, true)
					.criterion("has_smithing_stone", conditionsFromItem(smithingStoneItem))
					.offerTo(exporter, path);
			});
		});
	}

	public static String[] tierNames = new String[]{"one", "two", "three"};

}
