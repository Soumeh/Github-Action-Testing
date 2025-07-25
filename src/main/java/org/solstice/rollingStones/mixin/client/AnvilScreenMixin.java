package org.solstice.rollingStones.mixin.client;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.AnvilScreen;
import net.minecraft.client.gui.screen.ingame.ForgingScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilScreen.class)
public abstract class AnvilScreenMixin extends ForgingScreen<AnvilScreenHandler> {

	@Unique private static final Text CURSED_TEXT = Text.translatable("container.repair.cursed");

	public AnvilScreenMixin(AnvilScreenHandler handler, PlayerInventory playerInventory, Text title, Identifier texture) {
		super(handler, playerInventory, title, texture);
	}

	@Inject(method = "drawForeground", at = @At("TAIL"))
	protected void drawForeground(DrawContext context, int mx, int my, CallbackInfo ci) {
		if (this.handler.getLevelCost() != -1) return;

		int k = this.backgroundWidth - 8 - this.textRenderer.getWidth(CURSED_TEXT) - 2;
		context.fill(k - 2, 67, this.backgroundWidth - 8, 79, 1325400064);
		context.drawTextWithShadow(this.textRenderer, CURSED_TEXT, k, 69, 16736352);
	}

}
