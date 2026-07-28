package me.witherbuilder13.advanced_tree_config.feature.util.branchcondition;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

public class NotBranchCondition implements BranchCondition {

    private final BranchCondition condition;

    public NotBranchCondition(BranchCondition condition) {
        this.condition = condition;
    }

    public BranchCondition condition() {
        return condition;
    }

    public static MapCodec<NotBranchCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BranchCondition.CODEC.fieldOf("condition").forGetter(NotBranchCondition::condition)
    ).apply(instance, NotBranchCondition::new));

    @Override
    public boolean test(List<Integer> placedBranches) {
        return !condition.test(placedBranches);
    }

    @Override
    public BranchConditionType<?> type() {
        return BranchConditionType.NOT;
    }

    @Override
    public MapCodec<? extends BranchCondition> codec() {
        return CODEC;
    }
}
