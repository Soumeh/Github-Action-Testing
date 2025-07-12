package org.solstice.rollingStones.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.SmithingRecipe;
import net.minecraft.recipe.input.RecipeInput;
import net.minecraft.recipe.input.SmithingRecipeInput;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.screen.*;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;
import org.solstice.rollingStones.content.recipe.RandomizedSmithingRecipe;
import org.solstice.rollingStones.registry.RollingAdvancementCriteria;
import org.solstice.rollingStones.registry.RollingRecipeSerializers;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SmithingScreenHandler.class)
public abstract class SmithingScreenHandlerMixin extends ForgingScreenHandler {

	@Shadow @Nullable private RecipeEntry<SmithingRecipe> currentRecipe;

//	@Shadow protected abstract SmithingRecipeInput createRecipeInput();

	@Unique private Random random;
	@Unique private Property seed;

	public SmithingScreenHandlerMixin(@Nullable ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
		super(type, syncId, playerInventory, context);
	}

	@Inject(method = "<init>(ILnet/minecraft/entity/player/PlayerInventory;Lnet/minecraft/screen/ScreenHandlerContext;)V", at = @At("TAIL"))
	private void init(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context, CallbackInfo ci) {
		this.random = Random.create();
		this.seed = Property.create();
	}

	@Inject(method = "onTakeOutput", at = @At("HEAD"))
	private void triggerUpgradeCriterion(PlayerEntity player, ItemStack stack, CallbackInfo ci) {
		if (player.getWorld().isClient()) return;
		if (this.currentRecipe == null) return;
		if (!this.currentRecipe.value().getSerializer().equals(RollingRecipeSerializers.SMITHING_UPGRADE)) return;

		this.seed.set(this.random.nextInt());
		RollingAdvancementCriteria.UPGRADED_ITEM.trigger((ServerPlayerEntity)player, stack, this.currentRecipe.id());
	}

	@Inject(method = "updateResult", at = @At("HEAD"))
	private void setRandomSeed(CallbackInfo ci) {
		this.random.setSeed(this.seed.get() + this.input.getStack(1).getItem().hashCode());
	}

	@WrapOperation(
		method = "updateResult",
		at = @At(
			value = "INVOKE",
			target = "Lnet/minecraft/recipe/SmithingRecipe;craft(Lnet/minecraft/recipe/input/RecipeInput;Lnet/minecraft/registry/RegistryWrapper$WrapperLookup;)Lnet/minecraft/item/ItemStack;"
		)
	)
	private ItemStack craft(
		SmithingRecipe instance,
		RecipeInput input,
		RegistryWrapper.WrapperLookup lookup,
		Operation<ItemStack> original,
		@Local RecipeEntry<SmithingRecipe> entry
	) {
		if (entry.value() instanceof RandomizedSmithingRecipe recipe) return recipe.craft((SmithingRecipeInput)input, lookup, this.random);
		return original.call(instance, input, lookup);
	}

}
