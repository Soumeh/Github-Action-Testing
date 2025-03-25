package org.solstice.rollingStones.content.recipe;

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
import org.solstice.rollingStones.registry.ModRecipeTypes;
import org.solstice.rollingStones.util.CodecRecipeSerializer;

import java.util.Optional;

public record SmithingUpgradeRecipe (
        Ingredient template,
        Ingredient base,
        Ingredient addition
) implements SmithingRecipe {

    public static final MapCodec<SmithingUpgradeRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("template").forGetter(SmithingUpgradeRecipe::template),
            Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("base").forGetter(SmithingUpgradeRecipe::base),
            Ingredient.DISALLOW_EMPTY_CODEC.fieldOf("addition").forGetter(SmithingUpgradeRecipe::addition)
    ).apply(instance, SmithingUpgradeRecipe::new));

    public static final RecipeSerializer<SmithingUpgradeRecipe> SERIALIZER = new CodecRecipeSerializer<>(CODEC);

    @Override
    public RecipeType<?> getType() {
        return ModRecipeTypes.SMITHING_UPGRADE.get();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return SERIALIZER;
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
        return this.template.test(input.template()) && this.base.test(input.base()) && this.addition.test(input.addition());
    }

    @Override
    public boolean testTemplate(ItemStack stack) {
        return this.template.test(stack);
    }

    @Override
    public boolean testBase(ItemStack stack) {
        return this.base.test(stack);
    }

    @Override
    public boolean testAddition(ItemStack stack) {
        return this.addition.test(stack);
    }

}
