package org.solstice.rollingStones.content.upgrade;

import net.minecraft.component.ComponentType;
import net.minecraft.item.ItemStack;
import org.solstice.rollingStones.content.item.SmithingStoneItem;
import org.solstice.rollingStones.content.item.component.ItemUpgradesComponent;
import org.solstice.rollingStones.registry.RollingComponentTypes;

public class UpgradeHelper {

	private static ComponentType<ItemUpgradesComponent> getUpgradesComponentType(ItemStack stack) {
		return stack.getItem() instanceof SmithingStoneItem ? RollingComponentTypes.STORED_UPGRADES : RollingComponentTypes.UPGRADES;
	}

	public static ItemUpgradesComponent apply(ItemStack stack, java.util.function.Consumer<ItemUpgradesComponent.Builder> applier) {
		ComponentType<ItemUpgradesComponent> componentType = getUpgradesComponentType(stack);
		ItemUpgradesComponent component = stack.getOrDefault(componentType, ItemUpgradesComponent.DEFAULT);

		ItemUpgradesComponent.Builder builder = new ItemUpgradesComponent.Builder(component);
		applier.accept(builder);

		ItemUpgradesComponent newComponent = builder.build();
		stack.set(componentType, newComponent);
		return newComponent;
	}

}
