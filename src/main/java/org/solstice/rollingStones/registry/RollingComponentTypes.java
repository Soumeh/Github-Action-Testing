package org.solstice.rollingStones.registry;

import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import org.solstice.euclidsElements.EuclidsElements;
import org.solstice.rollingStones.RollingStones;
import org.solstice.rollingStones.content.item.component.ItemUpgradesComponent;

public class RollingComponentTypes {

	public static void init() {}

    public static final ComponentType<ItemUpgradesComponent> UPGRADES = register("upgrades", ItemUpgradesComponent.CODEC);
    public static final ComponentType<ItemUpgradesComponent> STORED_UPGRADES = register("stored_upgrades", ItemUpgradesComponent.CODEC);
	public static final ComponentType<Boolean> UPGRADE_GLINT_OVERRIDE = register("upgrade_glint_override", Codec.BOOL);

	private static <T> ComponentType<T> register(String name, Codec<T> codec) {
		return register(name, codec, PacketCodecs.registryCodec(codec));
	}

	private static <T> ComponentType<T> register(String name, Codec<T> codec, PacketCodec<RegistryByteBuf, T> packetCodec) {
		ComponentType<T> component = ComponentType.<T>builder()
			.codec(codec)
			.packetCodec(packetCodec)
			.cache()
			.build();
		return Registry.register(Registries.DATA_COMPONENT_TYPE, RollingStones.of(name), component);
	}

}
