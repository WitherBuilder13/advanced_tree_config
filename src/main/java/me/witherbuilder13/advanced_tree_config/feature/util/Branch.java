package me.witherbuilder13.advanced_tree_config.feature.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.witherbuilder13.advanced_tree_config.feature.util.branchcondition.BranchCondition;
import me.witherbuilder13.advanced_tree_config.util.ATCRegistries;
import me.witherbuilder13.advanced_tree_config.util.blockvector.BlockVector;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.codec.RegistryFileCodec;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedList;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;

import java.util.List;
import java.util.Optional;

public class Branch {

    private final int id;
    private final int startNode;
    private final int endNode;
    private final BlockStateProvider branchProvider;
    private final Holder<Config> config;
    private IntProvider count;
    private boolean rotateRandomly;
    private Optional<BranchCondition> condition;
    private int weight;

    public static MapCodec<Branch> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.intRange(1, 256).fieldOf("id").forGetter(t -> t.id),
            Codec.intRange(0, 63).fieldOf("start_node").forGetter(t -> t.startNode),
            Codec.intRange(1, 64).fieldOf("end_node").forGetter(t -> t.endNode),
            BlockStateProvider.CODEC.fieldOf("branch_provider").forGetter(t -> t.branchProvider),
            RegistryFileCodec.create(ATCRegistries.BRANCH, Branch.Config.CODEC.codec(), true)
                    .fieldOf("config").forGetter(t -> t.config),
            IntProviders.codec(1, 256).optionalFieldOf("count", ConstantInt.of(1)).forGetter(t -> t.count),
            Codec.BOOL.optionalFieldOf("rotate_randomly", true).forGetter(t -> t.rotateRandomly),
            BranchCondition.CODEC.optionalFieldOf("condition").forGetter(t -> t.condition),
            Codec.intRange(1, 1000000).optionalFieldOf("weight", 1).forGetter(t -> t.weight)
    ).apply(instance, Branch::new));

    private Branch(
            int id, int startNode, int endNode, BlockStateProvider branchProvider, Holder<Config> config, IntProvider count, boolean rotateRandomly, Optional<BranchCondition> condition, int weight
    ) {
        this.id = id;
        this.startNode = startNode;
        this.endNode = endNode;
        this.branchProvider = branchProvider;
        this.config = config;
        this.count = count;
        this.rotateRandomly = rotateRandomly;
        this.condition = condition;
        this.weight = weight;

        if (startNode >= endNode) {
            throw new IllegalArgumentException("start_node must be less than end_node");
        }
    }

    public Branch(int id, int startNode, int endNode, BlockStateProvider branchProvider, Holder<Branch.Config> config) {
        this(id, startNode, endNode, branchProvider, config, ConstantInt.of(1), true, Optional.empty(), 1);
    }

    public Branch count(IntProvider count) {
        this.count = count;
        return this;
    }

    public Branch dontRotateRandomly() {
        this.rotateRandomly = false;
        return this;
    }

    public Branch condition(BranchCondition condition) {
        this.condition = Optional.of(condition);
        return this;
    }

    public Branch weight(int weight) {
        this.weight = weight;
        return this;
    }

    public int id() {
        return id;
    }

    public int startNode() {
        return startNode;
    }

    public int endNode() {
        return endNode;
    }

    public BlockStateProvider branchProvider() {
        return branchProvider;
    }

    public Branch.Config config() {
        return config.value();
    }

    public Optional<BranchCondition> condition() {
        return condition;
    }

    public int weight() {
        return weight;
    }

    //` ------------------------------------------------------------------------------------------------------

    public static class Group {
        private float probability;
        private final List<Branch> branches;
        private IntProvider count;

        private Group(List<Branch> branches, float probability, IntProvider count) {
            this.branches = branches;
            this.probability = probability;
            this.count = count;

            if (branches.isEmpty())
                throw new IllegalArgumentException("\"branches\" cannot be empty");
        }

        public Group(List<Branch> branches) {
            this(branches, 1.0F, ConstantInt.of(1));
        }

        public Group probability(float probability) {
            this.probability = probability;
            return this;
        }

        public Group count(IntProvider count) {
            this.count = count;
            return this;
        }

        public static Group create(Branch... branches) {
            return new Group(List.of(branches));
        }

        public List<Branch> branches() {
            return branches;
        }

        public float probability() {
            return probability;
        }

        public Branch sample(RandomSource random) {
            WeightedList.Builder<Branch> branchListBuilder = WeightedList.builder();

            for (Branch branch : this.branches)
                branchListBuilder.add(branch, branch.weight());

            WeightedList<Branch> branches = branchListBuilder.build();

            return branches.getRandomOrThrow(random);
        }

        public static MapCodec<Branch.Group> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Codec.list(Branch.CODEC.codec()).fieldOf("branches").forGetter(t -> t.branches),
                Codec.floatRange(0.0F, 1.0F).optionalFieldOf("probability", 1.0F).forGetter(t -> t.probability),
                IntProviders.codec(1, 256).optionalFieldOf("count", ConstantInt.of(1)).forGetter(t -> t.count)
        ).apply(instance, Branch.Group::new));
    }

    //` ----------------------------------------------------------------------------------------------------------------------------------------------

    public record Config(
            BranchShape shape,
            BlockVector blockVector,
            IntProvider thicknessX,
            IntProvider thicknessY,
            IntProvider thicknessZ,
            IntProvider offsetX,
            IntProvider offsetY,
            IntProvider offsetZ
    ) {
        public static MapCodec<Branch.Config> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                BranchShape.CODEC.optionalFieldOf("shape", BranchShape.SQUARE).forGetter(c -> c.shape),
                BlockVector.CODEC.fieldOf("branch_vector").forGetter(c -> c.blockVector),
                IntProviders.codec(1, 16).optionalFieldOf("thickness_x", ConstantInt.of(1)).forGetter(c -> c.thicknessX),
                IntProviders.codec(1, 16).optionalFieldOf("thickness_y", ConstantInt.of(1)).forGetter(c -> c.thicknessY),
                IntProviders.codec(1, 16).optionalFieldOf("thickness_z", ConstantInt.of(1)).forGetter(c -> c.thicknessZ),
                IntProviders.codec(-64, 64).optionalFieldOf("offset_x", ConstantInt.of(0)).forGetter(c -> c.offsetX),
                IntProviders.codec(-64, 64).optionalFieldOf("offset_y", ConstantInt.of(0)).forGetter(c -> c.offsetY),
                IntProviders.codec(-64, 64).optionalFieldOf("offset_z", ConstantInt.of(0)).forGetter(c -> c.offsetZ)
        ).apply(instance, Branch.Config::new));

        @Override
        public BranchShape shape() {
            return shape;
        }

        @Override
        public BlockVector blockVector() {
            return blockVector;
        }

        @Override
        public IntProvider thicknessX() {
            return thicknessX;
        }

        @Override
        public IntProvider thicknessY() {
            return thicknessY;
        }

        @Override
        public IntProvider thicknessZ() {
            return thicknessZ;
        }

        @Override
        public IntProvider offsetX() {
            return offsetX;
        }

        @Override
        public IntProvider offsetY() {
            return offsetY;
        }

        @Override
        public IntProvider offsetZ() {
            return offsetZ;
        }

        //* -------------------------------------------------------------------------------------------------------------------------------

        public static Builder builder(BlockVector blockVector) {
            return new Builder().branch(blockVector);
        }

        public static class Builder {
            private BranchShape shape = BranchShape.SQUARE;
            private BlockVector blockVector;
            private IntProvider thickness_x = ConstantInt.of(1);
            private IntProvider thickness_y = ConstantInt.of(1);
            private IntProvider thickness_z = ConstantInt.of(1);
            private IntProvider offset_x = ConstantInt.of(0);
            private IntProvider offset_y = ConstantInt.of(0);
            private IntProvider offset_z = ConstantInt.of(0);

            public Builder shape(BranchShape shape) {
                this.shape = shape;
                return this;
            }

            public Builder branch(BlockVector blockVector) {
                this.blockVector = blockVector;
                return this;
            }

            public Builder thicknessX(IntProvider thickness_x) {
                this.thickness_x = thickness_x;
                return this;
            }

            public Builder thicknessY(IntProvider thickness_y) {
                this.thickness_y = thickness_y;
                return this;
            }

            public Builder thicknessZ(IntProvider thickness_z) {
                this.thickness_z = thickness_z;
                return this;
            }

            public Builder offsetX(IntProvider offset_x) {
                this.offset_x = offset_x;
                return this;
            }

            public Builder offsetY(IntProvider offset_y) {
                this.offset_y = offset_y;
                return this;
            }

            public Builder offsetZ(IntProvider offset_z) {
                this.offset_z = offset_z;
                return this;
            }

            public Branch.Config build() {
                return new Branch.Config(
                        this.shape,
                        this.blockVector,
                        this.thickness_x,
                        this.thickness_y,
                        this.thickness_z,
                        this.offset_x,
                        this.offset_y,
                        this.offset_z
                );
            }
        }
    }
}
