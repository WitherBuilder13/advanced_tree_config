package me.witherbuilder13.advanced_tree_config.datagen;

import me.witherbuilder13.advanced_tree_config.AdvancedTreeConfig;
import me.witherbuilder13.advanced_tree_config.feature.AdvancedTreeFeature;
import me.witherbuilder13.advanced_tree_config.feature.util.BranchShape;
import me.witherbuilder13.advanced_tree_config.feature.util.branchcondition.BranchCondition;
import me.witherbuilder13.advanced_tree_config.util.ATCRegistries;
import me.witherbuilder13.advanced_tree_config.feature.util.Branch;
import me.witherbuilder13.advanced_tree_config.util.blockvector.DirectionalBlockVector;
import me.witherbuilder13.advanced_tree_config.util.blockvector.SimpleBlockVector;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

import java.util.List;

public class ATCFeatures {
    public static final ResourceKey<Feature> OAK = createKey("oak");
    public static final ResourceKey<Feature> ACACIA = createKey("acacia");

    public static void bootstrap(BootstrapContext<Feature> context) {
        HolderGetter<Branch.Config> treePartConfigs = context.lookup(ATCRegistries.BRANCH);

        context.register(OAK, new AdvancedTreeFeature(
                List.of(
                        Branch.Group.create(
                                new Branch(1, 0, 1, BlockStateProvider.simple(Blocks.OAK_LOG), Holder.direct(
                                        Branch.Config.builder(SimpleBlockVector.of())
                                                .offsetY(ConstantInt.of(10))
                                                .thicknessX(UniformInt.of(1, 5))
                                                .thicknessY(UniformInt.of(1, 5))
                                                .thicknessZ(UniformInt.of(1, 5))
                                                .shape(BranchShape.SQUARE_CUT_CORNERS)
                                                .build()
                                ))
                        )
                )
        ));
        context.register(ACACIA, new AdvancedTreeFeature(
                List.of(
                        Branch.Group.create(
                                new Branch(1, 0, 1, BlockStateProvider.simple(Blocks.ACACIA_LOG), Holder.direct(
                                        Branch.Config.builder(SimpleBlockVector.of().lengthY(UniformInt.of(4, 7)))
                                                .build()
                                ))
                        ),
                        Branch.Group.create(
                                new Branch(2, 2, 3, BlockStateProvider.simple(Blocks.ACACIA_LOG), Holder.direct(
                                        Branch.Config.builder(SimpleBlockVector.of().lengthY(UniformInt.of(1, 5)))
                                                .build()
                                ))
                        ),
                        Branch.Group.create(
                                new Branch(3, 1, 2, BlockStateProvider.simple(Blocks.ACACIA_LOG), Holder.direct(
                                        Branch.Config.builder(new DirectionalBlockVector(List.of(Direction.NORTH, Direction.UP), UniformInt.of(2, 4)))
                                                .build()
                                )),
                                new Branch(4, 1, 2, BlockStateProvider.simple(Blocks.ACACIA_LOG), Holder.direct(
                                        Branch.Config.builder(new DirectionalBlockVector(List.of(Direction.SOUTH, Direction.UP), UniformInt.of(2, 4)))
                                                .build()
                                )),
                                new Branch(5, 1, 2, BlockStateProvider.simple(Blocks.ACACIA_LOG), Holder.direct(
                                        Branch.Config.builder(new DirectionalBlockVector(List.of(Direction.EAST, Direction.UP), UniformInt.of(2, 4)))
                                                .build()
                                )),
                                new Branch(6, 1, 2, BlockStateProvider.simple(Blocks.ACACIA_LOG), Holder.direct(
                                        Branch.Config.builder(new DirectionalBlockVector(List.of(Direction.WEST, Direction.UP), UniformInt.of(2, 4)))
                                                .build()
                                ))
                        ),
                        Branch.Group.create(
                                new Branch(7, 1, 4, BlockStateProvider.simple(Blocks.ACACIA_LOG), Holder.direct(
                                        Branch.Config.builder(new DirectionalBlockVector(List.of(Direction.NORTH, Direction.UP), UniformInt.of(2, 4)))
                                                .offsetY(UniformInt.of(-3, -1))
                                                .build()
                                )).condition(BranchCondition.not(BranchCondition.ifPlaced(3))),
                                new Branch(8, 1, 4, BlockStateProvider.simple(Blocks.ACACIA_LOG), Holder.direct(
                                        Branch.Config.builder(new DirectionalBlockVector(List.of(Direction.SOUTH, Direction.UP), UniformInt.of(2, 4)))
                                                .offsetY(UniformInt.of(-3, -1))
                                                .build()
                                )).condition(BranchCondition.not(BranchCondition.ifPlaced(4))),
                                new Branch(9, 1, 4, BlockStateProvider.simple(Blocks.ACACIA_LOG), Holder.direct(
                                        Branch.Config.builder(new DirectionalBlockVector(List.of(Direction.EAST, Direction.UP), UniformInt.of(2, 4)))
                                                .offsetY(UniformInt.of(-3, -1))
                                                .build()
                                )).condition(BranchCondition.not(BranchCondition.ifPlaced(5))),
                                new Branch(10, 1, 4, BlockStateProvider.simple(Blocks.ACACIA_LOG), Holder.direct(
                                        Branch.Config.builder(new DirectionalBlockVector(List.of(Direction.WEST, Direction.UP), UniformInt.of(2, 4)))
                                                .offsetY(UniformInt.of(-3, -1))
                                                .build()
                                )).condition(BranchCondition.not(BranchCondition.ifPlaced(6)))
                        ).probability(0.75F)
                )
        ));
    }

    public static ResourceKey<Feature> createKey(String path) {
        return ResourceKey.create(Registries.FEATURE, Identifier.fromNamespaceAndPath(AdvancedTreeConfig.MOD_ID, path));
    }
}
