package org.solstice.rollingStones.content.item;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.random.Random;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class CursedSmithingStoneItem extends SmithingStoneItem {

	private final int curseRolls;

    public CursedSmithingStoneItem(Settings settings, int curseRolls) {
        super(settings);
		this.curseRolls = curseRolls;
    }

//	@Override
//	public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
//		super.appendTooltip(stack, context, tooltip, type);
//		Text text = Text.translatable("item.cursed_smithing_stone.tooltip").formatted(Formatting.RED);
//		tooltip.add(text);
//	}

	@Override
	public ItemStack onUpgrade(ItemStack stack, RegistryWrapper.WrapperLookup lookup, Random random) {
		int curseRolls = this.curseRolls;

		RegistryEntryList<Enchantment> curses = lookup.getWrapperOrThrow(RegistryKeys.ENCHANTMENT).getOrThrow(EnchantmentTags.CURSE);
		while (curseRolls > 0) {
			RegistryEntry<Enchantment> curse = curses.getRandom(random).get();
			int level = random.nextBetween(curse.value().getMinLevel(), curse.value().getMaxLevel());

			stack.addEnchantment(curse, level);
			--curseRolls;
		}

		return stack;
	}

}
