package org.solstice.rollingStones.util;

import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;

public record IdentifiableRecipeType<T extends Recipe<?>>(Identifier id) implements RecipeType<T> {

    @Override
    public String toString() {
        return id.toString();
    }

}
