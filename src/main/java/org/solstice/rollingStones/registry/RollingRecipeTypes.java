package org.solstice.rollingStones.registry;

import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.solstice.euclidsElements.util.recipe.IdentifiableRecipeType;
import org.solstice.rollingStones.RollingStones;

public class RollingRecipeTypes {

	public static void init() {}

    public static final RecipeType<Recipe<?>> SMITHING_UPGRADE = register("smithing_upgrade");

    private static <T extends Recipe<?>> RecipeType<Recipe<?>> register(String name) {
		Identifier id = RollingStones.of(name);
		RecipeType<Recipe<?>> type = new IdentifiableRecipeType<>(id);
		return Registry.register(Registries.RECIPE_TYPE, id, type);
    }

}
