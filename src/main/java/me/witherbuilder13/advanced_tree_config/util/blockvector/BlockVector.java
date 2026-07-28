package me.witherbuilder13.advanced_tree_config.util.blockvector;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import me.witherbuilder13.advanced_tree_config.util.ATCRegistries;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;

public interface BlockVector {
    Vec3i getVector(RandomSource random);

    Codec<BlockVector> CODEC = ATCRegistries.BLOCK_VECTOR_TYPE.byNameCodec().dispatch(BlockVector::type, BlockVectorType::codec);

    BlockVectorType<?> type();
    MapCodec<? extends BlockVector> codec();
}
