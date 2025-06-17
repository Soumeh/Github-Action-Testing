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

	public Codec<Conditions> getConditionsCodec() {
		return Conditions.CODEC;
	}

	public void trigger(ServerPlayerEntity player, ItemStack stack, Identifier recipeId) {
		this.trigger(player, conditions -> conditions.matches(stack, recipeId));
	}

	public record Conditions (
		Optional<LootContextPredicate> player,
		Optional<ItemPredicate> item,
		Identifier recipeId
	) implements AbstractCriterion.Conditions {

		public static final Codec<Conditions> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			EntityPredicate.LOOT_CONTEXT_PREDICATE_CODEC.optionalFieldOf("player").forGetter(Conditions::player),
			ItemPredicate.CODEC.optionalFieldOf("item").forGetter(Conditions::item),
			Identifier.CODEC.optionalFieldOf("recipe_id", Identifier.of("", "")).forGetter(Conditions::recipeId)
		).apply(instance, Conditions::new));

		public static AdvancementCriterion<Conditions> any() {
			return RollingAdvancementCriteria.UPGRADED_ITEM.create(
				new Conditions(Optional.empty(), Optional.empty(), Identifier.of("", ""))

//				new Conditions(Optional.empty(), Identifier.of("", ""), NumberRange.IntRange.ANY)
			);
		}

		public boolean matches(ItemStack stack, Identifier recipeId) {
			if (this.item.isPresent() && !this.item.get().test(stack)) return false;
			return this.recipeId.equals(recipeId);
		}

	}

}
