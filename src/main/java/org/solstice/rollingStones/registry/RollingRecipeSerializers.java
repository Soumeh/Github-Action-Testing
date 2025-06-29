package org.solstice.rollingStones.registry;

import com.mojang.serialization.MapCodec;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import org.solstice.euclidsElements.content.api.recipe.CodecRecipeSerializer;
import org.solstice.rollingStones.RollingStones;
import org.solstice.rollingStones.content.recipe.SmithingUpgradeRecipe;

public class RollingRecipeSerializers {

	public static void init() {}

    public static final RecipeSerializer<SmithingUpgradeRecipe> SMITHING_UPGRADE = register("smithing_upgrade", SmithingUpgradeRecipe.CODEC);

	private static <T extends Recipe<?>> RecipeSerializer<T> register(String name, MapCodec<T> codec) {
		return register(name, new CodecRecipeSerializer<>(codec));
	}

	private static <T extends Recipe<?>> RecipeSerializer<T> register(String name, RecipeSerializer<T> serializer) {
		return Registry.register(Registries.RECIPE_SERIALIZER, RollingStones.of(name), serializer);
    }

}
