package org.solstice.rollingStones.datagen;

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

	public record SmithingStoneData(Item item, int tier, String name) {}

	public static final List<SmithingStoneData> SMITHING_STONE_DATA = List.of(
		new SmithingStoneData(RollingItems.SIMPLE_SMITHING_STONE, 1, "simple"),
		new SmithingStoneData(RollingItems.MALEDICTIVE_SMITHING_STONE, 2, "maledictive"),
		new SmithingStoneData(RollingItems.HONED_SMITHING_STONE, 2, "honed"),
		new SmithingStoneData(RollingItems.GILDED_SMITHING_STONE, 3, "gilded"),
		new SmithingStoneData(RollingItems.MIDAS_SMITHING_STONE, 5, "midas")
	);

	public static final Map<RegistryKey<Upgrade>, Item> UPGRADE_MATERIALS = Map.of(
		DENSITY, Items.TUFF,
		DRAWBACK, Items.ANDESITE,
		DURABILITY, Items.COBBLESTONE,
		EFFICIENCY, Items.DEEPSLATE,
		PROTECTION, Items.GRANITE,
		SHARPNESS, Items.BASALT,
		TENSION, Items.DIORITE
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
		SMITHING_STONE_DATA.forEach(data -> {
			Item item = data.item;
			Ingredient template = Ingredient.ofItems(item);
			UPGRADE_MATERIALS.forEach((upgradeKey, upgradeItem) -> {
				Ingredient addition = Ingredient.ofItems(upgradeItem);
				RegistryEntry<Upgrade> upgrade = registryLookup.getWrapperOrThrow(RollingRegistryKeys.UPGRADE).getOrThrow(upgradeKey);
				Identifier upgradeId = upgradeKey.getValue();

				String name = "upgrade/" + upgradeId.getPath() + "/" + data.name;
				Identifier path = Identifier.of(upgradeId.getNamespace(), name);

				SmithingUpgradeRecipeBuilder.create(RecipeCategory.MISC, template, base, addition, upgrade, data.tier, false)
					.criterion("has_smithing_stone", conditionsFromItem(item))
					.offerTo(exporter, path);
			});
		});
	}

}
