package org.solstice.rollingStones.mixin.client;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalIntRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.client.gui.screen.ingame.ForgingScreen;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.solstice.rollingStones.registry.RollingEnchantmentEffects;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilScreen.class)
public abstract class AnvilScreenMixin extends ForgingScreen<AnvilScreenHandler> {

	@Shadow
	@Final
	private PlayerEntity player;
	@Unique
	private static final Text CURSED_TEXT = Text.translatable("container.repair.cursed");

	public AnvilScreenMixin(AnvilScreenHandler handler, PlayerInventory playerInventory, Text title, Identifier texture) {
		super(handler, playerInventory, title, texture);
	}

	@Inject(
		method = "drawForeground",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/screen/slot/Slot;canTakeItems(Lnet/minecraft/entity/player/PlayerEntity;)Z"
		)
	)
	protected void drawForeground(
		DrawContext context,
		int mouseX,
		int mouseY,
		CallbackInfo ci,
		@Local(index = 5) LocalIntRef color,
		@Local LocalRef<Text> textRef
	) {
		if (this.handler.getSlot(2).canTakeItems(this.player)) return;
		if (this.handler.getLevelCost() > 0) return;

		boolean primaryCursed = EnchantmentHelper.hasAnyEnchantmentsWith(
			this.handler.getSlot(2).getStack(),
			RollingEnchantmentEffects.PREVENT_REPAIRING
		);
		boolean secondaryCursed = EnchantmentHelper.hasAnyEnchantmentsWith(
			this.handler.getSlot(1).getStack(),
			RollingEnchantmentEffects.PREVENT_REPAIRING
		);
		if (primaryCursed || secondaryCursed) {
			color.set(16736352);
			textRef.set(CURSED_TEXT);
		}
	}

}
