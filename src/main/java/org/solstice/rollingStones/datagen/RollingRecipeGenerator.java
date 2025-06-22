package org.solstice.rollingStones.datagen;

import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementEntry;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.data.server.recipe.CraftingRecipeJsonBuilder;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.data.server.recipe.RecipeProvider;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;
import org.solstice.rollingStones.api.recipe.SmithingUpgradeRecipeBuilder;
import org.solstice.rollingStones.content.upgrade.Upgrade;
import org.solstice.rollingStones.registry.RollingUpgrades;
import org.solstice.rollingStones.registry.RollingItems;
import org.solstice.rollingStones.registry.RollingRegistryKeys;

import java.util.*;
import java.util.concurrent.CompletableFuture;

import static org.solstice.rollingStones.registry.RollingUpgrades.*;

public class RollingRecipeGenerator extends RecipeProvider {

	public static final Object2IntOpenHashMap<Item> SMITHING_STONE_TIERS = new Object2IntOpenHashMap<>(Map.of(
		RollingItems.SIMPLE_SMITHING_STONE, 1,
		RollingItems.HONED_SMITHING_STONE, 2,
		RollingItems.PERFECT_SMITHING_STONE, 3
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

	@Override
	public CompletableFuture<?> run(DataWriter writer, RegistryWrapper.WrapperLookup registryLookup) {
		final Set<Identifier> recipes = Sets.newHashSet();
		final List<CompletableFuture<?>> result = new ArrayList<>();

		RecipeExporter exporter = new RecipeExporter() {

			public void accept(Identifier id, Recipe<?> recipe, @Nullable AdvancementEntry advancement) {
				if (!recipes.add(id)) {
					throw new IllegalStateException("Duplicate recipe " + id);
				}
				result.add(DataProvider.writeCodecToPath(writer, registryLookup, Recipe.CODEC, recipe, RollingRecipeGenerator.this.recipesPathResolver.resolveJson(id)));
				if (advancement != null)
					result.add(DataProvider.writeCodecToPath(writer, registryLookup, Advancement.CODEC, advancement.value(), RollingRecipeGenerator.this.advancementsPathResolver.resolveJson(advancement.id())));
			}

			public Advancement.Builder getAdvancementBuilder() {
				AdvancementEntry entry = new AdvancementEntry(CraftingRecipeJsonBuilder.ROOT, null);
				return Advancement.Builder.createUntelemetered().parent(entry);
			}

		};

		this.generate(exporter, registryLookup);
		return CompletableFuture.allOf(result.toArray(CompletableFuture[]::new));
	}

	public void generate(RecipeExporter exporter, RegistryWrapper.WrapperLookup registryLookup) {
		SMITHING_STONE_TIERS.forEach((smithingStoneItem, tier) -> {
			Ingredient template = Ingredient.ofItems(smithingStoneItem);
			UPGRADE_MATERIALS.forEach((upgradeKey, upgradeItem) -> {
				Ingredient addition = Ingredient.ofItems(upgradeItem);
				RegistryEntry<Upgrade> upgrade = registryLookup.getWrapperOrThrow(RollingRegistryKeys.UPGRADE).getOrThrow(upgradeKey);

				String name = "tier_" + tier + "_" + upgradeKey.getValue().getPath() + "_upgrade";
				Identifier path = Identifier.of(upgradeKey.getValue().getNamespace(), name);

				SmithingUpgradeRecipeBuilder.create(RecipeCategory.MISC, template, addition, upgrade, tier, true)
					.criterion("has_smithing_stone", conditionsFromItem(smithingStoneItem))
					.offerTo(exporter, path);
			});
		});
	}

	@Override public void generate(RecipeExporter exporter) {}

}
