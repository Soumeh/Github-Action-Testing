package org.solstice.rollingStones.registry;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.enums.NoteBlockInstrument;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import org.solstice.rollingStones.RollingStones;
import org.solstice.rollingStones.content.block.StrongboxBlock;

import java.util.function.Function;

public class RollingBlocks {

	public static void init() {}

	public static final Block STRONGBOX = register("strongbox",
		settings -> new StrongboxBlock(settings, () -> RollingBlockEntityTypes.STRONGBOX),
		AbstractBlock.Settings.create()
			.requiresTool()
			.strength(12, 120)
			.mapColor(MapColor.DEEPSLATE_GRAY)
			.instrument(NoteBlockInstrument.BASEDRUM)
			.sounds(BlockSoundGroup.DEEPSLATE)
	);

	public static Block register(String name) {
		return register(name, Block::new, AbstractBlock.Settings.create());
	}

	public static Block register(String name, AbstractBlock.Settings settings) {
		return register(name, Block::new, settings);
	}

	public static Block register(String name, Function<AbstractBlock.Settings, Block> function, AbstractBlock.Settings settings) {
		return register(name, function, settings, new Item.Settings());
	}

	public static Block register(String name, Function<AbstractBlock.Settings, Block> function, AbstractBlock.Settings blockSettings, Item.Settings itemSettings) {
		Identifier id = RollingStones.of(name);
		RegistryKey<Block> blockKey = RegistryKey.of(RegistryKeys.BLOCK, id);
		Block block = function.apply(blockSettings);
		Registry.register(Registries.BLOCK, blockKey, block);
		RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, id);
		Item item = new BlockItem(block, itemSettings);
		Registry.register(Registries.ITEM, itemKey, item);
		return block;
	}

}
