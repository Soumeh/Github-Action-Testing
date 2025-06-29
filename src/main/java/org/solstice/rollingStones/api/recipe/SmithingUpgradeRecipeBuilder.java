package org.solstice.rollingStones.api.recipe;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.AdvancementRequirements;
import net.minecraft.advancement.AdvancementRewards;
import net.minecraft.advancement.criterion.RecipeUnlockedCriterion;
import net.minecraft.data.server.recipe.RecipeExporter;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.book.RecipeCategory;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.solstice.rollingStones.content.recipe.SmithingUpgradeRecipe;
import org.solstice.rollingStones.content.upgrade.Upgrade;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class SmithingUpgradeRecipeBuilder {

	private final RecipeCategory category;
	private final Ingredient template;
	private final Ingredient base;
	private final Ingredient addition;
	private final RegistryEntry<Upgrade> upgrade;
	private final int tier;
	private final boolean requiresPreviousTier;

	private final Map<String, AdvancementCriterion<?>> criteria = new LinkedHashMap<>();

	public SmithingUpgradeRecipeBuilder(
		RecipeCategory category,
		Ingredient template,
		Ingredient base,
		Ingredient addition,
		RegistryEntry<Upgrade> upgrade,
		int tier,
		boolean requiresPreviousTier
	) {
		this.category = category;
		this.template = template;
		this.base = base;
		this.addition = addition;
		this.upgrade = upgrade;
		this.tier = tier;
		this.requiresPreviousTier = requiresPreviousTier;
	}

	public static SmithingUpgradeRecipeBuilder create(
		RecipeCategory category,
		Ingredient template,
		Ingredient base,
		Ingredient addition,
		RegistryEntry<Upgrade> upgrade,
		int tier,
		boolean requiresPreviousTier
	) {
		return new SmithingUpgradeRecipeBuilder(category, template, base, addition, upgrade, tier, requiresPreviousTier);
	}

	public SmithingUpgradeRecipeBuilder criterion(String name, AdvancementCriterion<?> criterion) {
		this.criteria.put(name, criterion);
		return this;
	}

	public void offerTo(RecipeExporter exporter, Identifier id) {
		if (this.criteria.isEmpty()) throw new IllegalStateException("No way of obtaining recipe " + id);

		Advancement.Builder builder = exporter.getAdvancementBuilder()
			.criterion("has_the_recipe", RecipeUnlockedCriterion.create(id))
			.rewards(AdvancementRewards.Builder.recipe(id))
			.criteriaMerger(AdvancementRequirements.CriterionMerger.OR);
		Objects.requireNonNull(builder);

		this.criteria.forEach(builder::criterion);
		SmithingUpgradeRecipe recipe = new SmithingUpgradeRecipe(
			this.template, this.base, this.addition, this.upgrade, this.tier, this.requiresPreviousTier
		);
		exporter.accept(id, recipe, builder.build(id.withPrefixedPath("recipes/" + this.category.getName() + "/")));
	}

}
