package org.solstice.rollingStones.mixin.technical;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.component.ComponentHolder;
import net.minecraft.component.ComponentType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipAppender;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.neoforged.neoforge.common.util.AttributeTooltipContext;
import org.solstice.rollingStones.registry.ModComponentTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.function.Consumer;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin implements ComponentHolder {

    @Shadow
    public abstract <T extends TooltipAppender> void appendTooltip(ComponentType<T> componentType, Item.TooltipContext context, Consumer<Text> textConsumer, TooltipType type);

    @Inject(
            method = "getTooltip",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/item/ItemStack;appendTooltip(Lnet/minecraft/component/ComponentType;Lnet/minecraft/item/Item$TooltipContext;Ljava/util/function/Consumer;Lnet/minecraft/item/tooltip/TooltipType;)V",
                    ordinal = 0
            )
    )
    private void addCustomComponentTooltips(Item.TooltipContext context, PlayerEntity player, TooltipType type, CallbackInfoReturnable<List<Text>> cir, @Local Consumer<Text> consumer) {
        this.appendTooltip(ModComponentTypes.STORED_UPGRADES, context, consumer, type);
        this.appendTooltip(ModComponentTypes.UPGRADES, context, consumer, type);
    }

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
