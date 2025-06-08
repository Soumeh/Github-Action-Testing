package org.solstice.rollingStones.registry;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.solstice.rollingStones.RollingStones;
import org.solstice.rollingStones.content.block.entity.StrongboxBlockEntity;

public class RollingBlockEntityTypes {

	public static void init() {}

	public static final BlockEntityType<StrongboxBlockEntity> STRONGBOX = register("strongbox",
		BlockEntityType.Builder.create(StrongboxBlockEntity::new, RollingBlocks.STRONGBOX)
	);

	public static <T extends BlockEntity> BlockEntityType<T> register(String name, BlockEntityType.Builder<T> builder) {
		Identifier id = RollingStones.of(name);
//		Type<?> type = Util.getChoiceType(TypeReferences.BLOCK_ENTITY, id.toString());
		return Registry.register(Registries.BLOCK_ENTITY_TYPE, id, builder.build());
	}

}
