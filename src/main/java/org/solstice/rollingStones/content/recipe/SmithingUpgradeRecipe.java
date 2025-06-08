package org.solstice.rollingStones.content.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.trim.*;
import net.minecraft.recipe.*;
import net.minecraft.recipe.input.SmithingRecipeInput;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.world.World;
import org.solstice.rollingStones.content.upgrade.Upgrade;
import org.solstice.rollingStones.registry.RollingRecipeSerializers;
import org.solstice.rollingStones.registry.RollingRecipeTypes;

import java.util.Optional;

public record SmithingUpgradeRecipe (
        Ingredient base,
        Ingredient material,
		RegistryEntry<Upgrade> upgrade,
		int tier
) implements SmithingRecipe {

    public static final MapCodec<SmithingUpgradeRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
		Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("base").forGetter(SmithingUpgradeRecipe::base),
		Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("material").forGetter(SmithingUpgradeRecipe::material),
		Upgrade.ENTRY_CODEC.fieldOf("upgrade").forGetter(SmithingUpgradeRecipe::upgrade),
		Codec.INT.fieldOf("tier").forGetter(SmithingUpgradeRecipe::tier)
    ).apply(instance, SmithingUpgradeRecipe::new));

    @Override
    public RecipeType<?> getType() {
        return RollingRecipeTypes.SMITHING_UPGRADE;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return RollingRecipeSerializers.SMITHING_UPGRADE;
    }

    @Override
    public ItemStack craft(SmithingRecipeInput arg, RegistryWrapper.WrapperLookup arg2) {
        ItemStack itemstack = arg.base();
        if (this.base.test(itemstack)) {
            Optional<RegistryEntry.Reference<ArmorTrimMaterial>> optional = ArmorTrimMaterials.get(arg2, arg.addition());
            Optional<RegistryEntry.Reference<ArmorTrimPattern>> optional1 = ArmorTrimPatterns.get(arg2, arg.template());
            if (optional.isPresent() && optional1.isPresent()) {
                ArmorTrim armortrim = (ArmorTrim)itemstack.get(DataComponentTypes.TRIM);
                if (armortrim != null && armortrim.equals((RegistryEntry)optional1.get(), (RegistryEntry)optional.get())) {
                    return ItemStack.EMPTY;
                }

                ItemStack itemstack1 = itemstack.copyWithCount(1);
                itemstack1.set(DataComponentTypes.TRIM, new ArmorTrim((RegistryEntry)optional.get(), (RegistryEntry)optional1.get()));
                return itemstack1;
            }
        }

        return ItemStack.EMPTY;
    }

    @Override
    public ItemStack getResult(RegistryWrapper.WrapperLookup registriesLookup) {
        ItemStack itemstack = new ItemStack(Items.IRON_CHESTPLATE);
        Optional<RegistryEntry.Reference<ArmorTrimPattern>> optional = registriesLookup.getWrapperOrThrow(RegistryKeys.TRIM_PATTERN).streamEntries().findFirst();
        Optional<RegistryEntry.Reference<ArmorTrimMaterial>> optional1 = registriesLookup.getWrapperOrThrow(RegistryKeys.TRIM_MATERIAL).getOptional(ArmorTrimMaterials.REDSTONE);
        if (optional.isPresent() && optional1.isPresent()) {
            itemstack.set(DataComponentTypes.TRIM, new ArmorTrim((RegistryEntry)optional1.get(), (RegistryEntry)optional.get()));
        }

        return itemstack;
    }

    @Override
    public boolean matches(SmithingRecipeInput input, World world) {
        return this.base.test(input.template()) && this.material.test(input.addition());
    }

    @Override
    public boolean testTemplate(ItemStack stack) {
        return this.base.test(stack);
    }

    @Override
    public boolean testBase(ItemStack stack) {
		return stack.isIn(this.upgrade.value().getDefinition().getSupportedItems());
    }

    @Override
    public boolean testAddition(ItemStack stack) {
        return this.material.test(stack);
    }

}
