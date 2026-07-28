package me.witherbuilder13.advanced_tree_config.util.blockvector;

import com.mojang.serialization.MapCodec;
import me.witherbuilder13.advanced_tree_config.AdvancedTreeConfig;
import me.witherbuilder13.advanced_tree_config.util.ATCRegistries;
import net.minecraft.core.Registry;

public interface BlockVectorType<L extends BlockVector> {
    BlockVectorType<SimpleBlockVector> SIMPLE = register("simple", SimpleBlockVector.CODEC);
    BlockVectorType<DirectionalBlockVector> DIRECTIONAL = register("directional", DirectionalBlockVector.CODEC);
    BlockVectorType<AngularBlockVector> ANGULAR = register("angular", AngularBlockVector.CODEC);

    MapCodec<L> codec();

    private static <P extends BlockVector> BlockVectorType<P> register(final String path, final MapCodec<P> codec) {
        return Registry.register(ATCRegistries.BLOCK_VECTOR_TYPE, AdvancedTreeConfig.id(path), () -> codec);
    }

    static void init() {}
}
