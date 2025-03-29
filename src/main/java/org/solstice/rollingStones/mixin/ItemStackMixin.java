package org.solstice.rollingStones.mixin;

import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {

//    @Redirect(
//            method = "getTooltip",
//            at = @At(
//                    value = "INVOKE",
//                    target = "Lnet/neoforged/neoforge/common/util/AttributeUtil;addAttributeTooltips(Lnet/minecraft/item/ItemStack;Ljava/util/function/Consumer;Lnet/neoforged/neoforge/common/util/AttributeTooltipContext;)V"
//            )
//    )
//    private void replaceAttributeTooltips(ItemStack stack, Consumer<Text> tooltip, AttributeTooltipContext ctx, @Local Consumer<Text> consumer) {
//    }

}
