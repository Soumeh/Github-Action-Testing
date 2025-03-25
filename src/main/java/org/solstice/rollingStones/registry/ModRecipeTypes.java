package org.solstice.rollingStones.registry;

import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.solstice.rollingStones.RollingStones;
import org.solstice.rollingStones.util.IdentifiableRecipeType;

public class ModRecipeTypes {

    public static final DeferredRegister<RecipeType<?>> REGISTRY = DeferredRegister
            .create(Registries.RECIPE_TYPE, RollingStones.MOD_ID);

    public static final DeferredHolder<RecipeType<?>, IdentifiableRecipeType<Recipe<?>>> SMITHING_UPGRADE = register("smithing_upgrade");

    private static DeferredHolder<RecipeType<?>, IdentifiableRecipeType<Recipe<?>>> register(String name) {
        return REGISTRY.register(name, IdentifiableRecipeType::new);
    }

}
