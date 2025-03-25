package org.solstice.rollingStones.util;

import com.mojang.serialization.MapCodec;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;

public record CodecRecipeSerializer<T extends Recipe<?>> (
        MapCodec<T> codec
) implements RecipeSerializer<T> {

    @Override
    public PacketCodec<RegistryByteBuf, T> packetCodec() {
        return PacketCodecs.registryCodec(codec.codec());
    }

}
