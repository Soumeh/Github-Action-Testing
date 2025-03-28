package org.solstice.rollingStones.registry;

import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.solstice.rollingStones.RollingStones;
import org.solstice.rollingStones.content.recipe.SmithingUpgradeRecipe;

public class ModRecipeSerializers {

    public static final DeferredRegister<RecipeSerializer<?>> REGISTRY = DeferredRegister
            .create(Registries.RECIPE_SERIALIZER, RollingStones.MOD_ID);

    public static final RecipeSerializer<?> SMITHING_UPGRADE = register("smithing_upgrade", SmithingUpgradeRecipe.SERIALIZER);

		private static RecipeSerializer<?> register(String name, RecipeSerializer<?> serializer) {
        REGISTRY.register(name, () -> serializer);
        return serializer;
    }

}
