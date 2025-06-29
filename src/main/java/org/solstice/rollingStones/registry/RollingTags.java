package org.solstice.rollingStones.registry;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.item.Item;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.TagKey;
import org.solstice.euclidsElements.tag.api.MapTagKey;
import org.solstice.rollingStones.RollingStones;
import org.solstice.rollingStones.content.upgrade.Upgrade;

public class RollingTags {

	public static void init() {}

	public static final TagKey<Upgrade> OBTAINABLE = of("obtainable", RollingRegistryKeys.UPGRADE);
	public static final TagKey<Item> UPGRADABLE = of("upgradable", RegistryKeys.ITEM);

	public static final MapTagKey<Item, RegistryEntry<Upgrade>> UPGRADE_MATERIALS = ofMap("upgrade_materials", RegistryKeys.ITEM, Upgrade.ENTRY_CODEC, Upgrade.ENTRY_PACKET_CODEC);

	public static <T> TagKey<T> of(String name, RegistryKey<Registry<T>> registry) {
		return TagKey.of(registry, RollingStones.of(name));
	}

	public static <T, R> MapTagKey<T, R> ofMap(String name, RegistryKey<Registry<T>> registry, Codec<R> codec) {
		return MapTagKey.of(registry, codec, RollingStones.of(name));
	}

	public static <T, R> MapTagKey<T, R> ofMap(String name, RegistryKey<Registry<T>> registry, Codec<R> codec, PacketCodec<? extends ByteBuf, R> networkCodec) {
		return MapTagKey.of(registry, codec, networkCodec, RollingStones.of(name));
	}

}
