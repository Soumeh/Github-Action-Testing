package org.solstice.rollingStones.content.recipe;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.recipe.input.SmithingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.random.Random;

public interface RandomizedSmithingRecipe extends SmithingRecipe {

	default ItemStack craft(SmithingRecipeInput input, RegistryWrapper.WrapperLookup lookup) {
		return ItemStack.EMPTY;
	}

	ItemStack craft(SmithingRecipeInput input, RegistryWrapper.WrapperLookup lookup, Random random);

}
