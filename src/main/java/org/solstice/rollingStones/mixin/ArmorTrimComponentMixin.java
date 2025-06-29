package org.solstice.rollingStones.mixin;

import net.minecraft.item.trim.ArmorTrim;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ArmorTrim.class)
public class ArmorTrimComponentMixin {

	@ModifyArg(
		method = "<clinit>",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/text/Text;translatable(Ljava/lang/String;)Lnet/minecraft/text/MutableText;"
		),
		index = 0
	)
	private static String replaceTrimTranslation(String key) {
		return "item.decorations";
	}

}
