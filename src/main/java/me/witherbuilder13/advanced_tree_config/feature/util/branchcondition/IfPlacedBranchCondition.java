package me.witherbuilder13.advanced_tree_config.feature.util.branchcondition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

public class IfPlacedBranchCondition implements BranchCondition {

    private final int branchId;

    public IfPlacedBranchCondition(int branchId) {
        this.branchId = branchId;
    }

    public int branchId() {
        return branchId;
    }

    @Override
    public boolean test(List<Integer> placedBranches) {
        return placedBranches.contains(branchId);
    }

    @Override
    public BranchConditionType<?> type() {
        return BranchConditionType.IF_PLACED;
    }

    public static MapCodec<IfPlacedBranchCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.INT.fieldOf("id").forGetter(IfPlacedBranchCondition::branchId)
    ).apply(instance, IfPlacedBranchCondition::new));

    @Override
    public MapCodec<? extends BranchCondition> codec() {
        return CODEC;
    }
}
