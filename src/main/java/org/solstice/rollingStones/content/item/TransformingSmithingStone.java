package org.solstice.rollingStones.content.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.random.Random;
import org.solstice.euclidsElements.tag.api.MapTagKey;

public class TransformingSmithingStone extends SmithingStoneItem {

	protected final MapTagKey<Item, RegistryEntry<Item>> transformationKey;
	protected final Item defaultItem;

	public TransformingSmithingStone(Settings settings, MapTagKey<Item, RegistryEntry<Item>> transformationKey, Item defaultItem) {
		super(settings);
		this.transformationKey = transformationKey;
		this.defaultItem = defaultItem;
	}

	public ItemStack onUpgrade(ItemStack stack, RegistryWrapper.WrapperLookup lookup, Random random) {
		RegistryEntry<Item> entry = Registries.ITEM.getEntry(stack.getItem());
		RegistryEntry<Item> transformedEntry = entry.getValue(
			this.transformationKey,
			Registries.ITEM.getEntry(this.defaultItem)
		);

		return stack.copyComponentsToNewStack(transformedEntry.value(), stack.getCount());
	}

}
