package org.solstice.rollingStones.mixin;

import net.minecraft.item.MiningToolItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(MiningToolItem.class)
public class MiningToolItemMixin {

	@ModifyConstant(method = "postDamageEntity", constant = @Constant(intValue = 2))
	private int durableToolAttacks(int value) {
		return 1;
	}

}
