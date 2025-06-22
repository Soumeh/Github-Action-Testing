package org.solstice.rollingStones.content.criterion;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancement.AdvancementCriterion;
import net.minecraft.advancement.criterion.AbstractCriterion;
import net.minecraft.item.ItemStack;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.predicate.entity.LootContextPredicate;
import net.minecraft.predicate.item.ItemPredicate;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.solstice.rollingStones.registry.RollingAdvancementCriteria;

import java.util.Optional;

public class UpgradedItemCriterion extends AbstractCriterion<UpgradedItemCriterion.Conditions> {

	@Override
	public Codec<Conditions> getConditionsCodec() {
		return Conditions.CODEC;
	}

	public void trigger(ServerPlayerEntity player, ItemStack stack, Identifier recipeId) {
		System.out.println("AAAAAaaaaaaaaaaaaAAAAAAAA?");
		this.trigger(player, conditions -> {
			var test = conditions.matches(stack, recipeId);
			System.out.println(test);
			return test;
		});
	}

	public record Conditions (
		Optional<LootContextPredicate> player,
		Optional<ItemPredicate> item,
		Optional<Identifier> recipeId
	) implements AbstractCriterion.Conditions {

		public static final Codec<Conditions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(Conditions::player),
			ItemPredicate.CODEC.optionalFieldOf("item").forGetter(Conditions::item),
			Identifier.CODEC.optionalFieldOf("recipe_id").forGetter(Conditions::recipeId)
		).apply(instance, Conditions::new));

		public static AdvancementCriterion<Conditions> any() {
			return RollingAdvancementCriteria.UPGRADED_ITEM.create(
				new Conditions(Optional.empty(), Optional.empty(), Optional.empty())
			);
		}

		public boolean matches(ItemStack stack, Identifier recipeId) {
			if (this.item.isPresent() && !this.item.get().test(stack)) return false;
			if (this.recipeId.isPresent() && !this.recipeId.get().equals(recipeId)) return false;
			return true;
		}

	}

}
