package org.solstice.rollingStones.content.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;
import org.solstice.euclidsElements.registry.EuclidsComponentTypes;
import org.solstice.rollingStones.content.upgrade.Upgrade;
import org.solstice.rollingStones.registry.ModItems;
import org.solstice.rollingStones.registry.ModRegistryKeys;

import java.util.List;

public class SmithingStoneItem extends Item {

    private final RegistryKey<Upgrade> upgrade;
    private final int tier;

    public SmithingStoneItem(RegistryKey<Upgrade> upgrade, int tier, Settings settings) {
        super(settings);
        this.upgrade = upgrade;
        this.tier = tier;
    }

	public static ItemStack forUpgrade(UpgradeInfo info) {
		ItemStack stack = new ItemStack(ModItems.SMITHING_STONE.get());
		Identifier upgradeId = info.entry.getKey().orElseThrow().getValue();
		Identifier modelId = Registries.ITEM.getId(stack.getItem())
				.withPrefixedPath(upgradeId.getNamespace() + "." + upgradeId.getPath() + ".")
				.withSuffixedPath(info.tier + ".");
		stack.set(EuclidsComponentTypes.CUSTOM_ITEM_MODEL, modelId);
		return stack;
	}

    public RegistryEntry<Upgrade> getUpgrade(World world) {
        return this.getUpgrade(world.getRegistryManager());
    }

    public RegistryEntry<Upgrade> getUpgrade(RegistryWrapper.WrapperLookup lookup) {
        return lookup.getWrapperOrThrow(ModRegistryKeys.UPGRADE).getOrThrow(this.upgrade);
    }

    public int getTier() {
        return this.tier;
    }


    @Override
    public void appendTooltip(ItemStack stack, TooltipContext context, List<Text> tooltip, TooltipType type) {
        super.appendTooltip(stack, context, tooltip, type);
        RegistryWrapper.WrapperLookup lookup = context.getRegistryLookup();
        if (lookup == null) return;

        RegistryEntry<Upgrade> upgrade = this.getUpgrade(lookup);
        tooltip.add(Upgrade.getTooltip(upgrade, tier));
    }

	public record UpgradeInfo(RegistryEntry<Upgrade> entry, int tier) {}

}
