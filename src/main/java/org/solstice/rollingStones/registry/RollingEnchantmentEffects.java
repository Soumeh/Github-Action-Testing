package org.solstice.rollingStones.registry;

import com.mojang.serialization.Codec;
import net.minecraft.component.ComponentType;
import net.minecraft.enchantment.effect.EnchantmentValueEffect;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Unit;
import org.solstice.rollingStones.RollingStones;

public class RollingEnchantmentEffects {

	public static void init() {}

	public static final ComponentType<EnchantmentValueEffect> MAX_DURABILITY = register("max_durability", EnchantmentValueEffect.CODEC);
	public static final ComponentType<Unit> PREVENT_REPAIRING = register("prevent_repairing", Unit.CODEC);

	private static <T> ComponentType<T> register(String name, Codec<T> codec) {
		return register(name, codec, PacketCodecs.registryCodec(codec));
	}

	private static <T> ComponentType<T> register(String name, Codec<T> codec, PacketCodec<RegistryByteBuf, T> packetCodec) {
		ComponentType<T> component = ComponentType.<T>builder()
			.codec(codec)
			.packetCodec(packetCodec)
			.cache()
			.build();
		return Registry.register(Registries.ENCHANTMENT_EFFECT_COMPONENT_TYPE, RollingStones.of(name), component);
	}

}
