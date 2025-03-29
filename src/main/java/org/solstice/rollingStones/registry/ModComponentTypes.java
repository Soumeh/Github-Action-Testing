package org.solstice.rollingStones.registry;

import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.solstice.rollingStones.RollingStones;
import org.solstice.rollingStones.content.item.component.ItemUpgradesComponent;

public class ModComponentTypes {

    public static final DeferredRegister<ComponentType<?>> REGISTRY = DeferredRegister
            .create(Registries.DATA_COMPONENT_TYPE, RollingStones.MOD_ID);

    public static final ComponentType<ItemUpgradesComponent> UPGRADES = register("upgrades", ItemUpgradesComponent.CODEC);
    public static final ComponentType<ItemUpgradesComponent> STORED_UPGRADES = register("stored_upgrades", ItemUpgradesComponent.CODEC);

	private static <T> ComponentType<T> register(String name, Codec<T> codec) {
        return register(name, codec, PacketCodecs.registryCodec(codec));
    }

    private static <T> ComponentType<T> register(String name, Codec<T> codec, PacketCodec<RegistryByteBuf, T> packetCodec) {
        ComponentType<T> component = ComponentType.<T>builder()
                .codec(codec)
                .packetCodec(packetCodec)
                .cache()
                .build();
        REGISTRY.register(name, () -> component);
        return component;
    }

}
