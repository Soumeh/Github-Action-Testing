package org.solstice.rollingStones.mixin.client;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.solstice.rollingStones.content.item.FovModifierItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractClientPlayerEntity.class)
public abstract class AbstractClientPlayerEntityMixin extends PlayerEntity {

	public AbstractClientPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
		super(world, pos, yaw, gameProfile);
	}

	@Redirect(method = "getFovMultiplier", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/AbstractClientPlayerEntity;isUsingItem()Z"))
	private boolean ignored(AbstractClientPlayerEntity instance) {
		return false;
	}

	@ModifyArg(method = "getFovMultiplier", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/math/MathHelper;lerp(FFF)F"), index = 2)
	private float handleFovMultiplierItems(float fov) {
		if (this.isUsingItem()) {
			if (this.getActiveItem().getItem() instanceof FovModifierItem fovModifier) {
				fov = fovModifier.getFovMultiplier(this, fov);
			} else if (MinecraftClient.getInstance().options.getPerspective().isFirstPerson() && this.isUsingSpyglass()) {
				fov = 0.5F;
			}
		}
		return fov;
	}

}
