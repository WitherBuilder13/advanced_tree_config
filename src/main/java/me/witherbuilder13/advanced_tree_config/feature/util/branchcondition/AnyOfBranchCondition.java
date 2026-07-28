package me.witherbuilder13.advanced_tree_config.feature.util.branchcondition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.List;

public class AnyOfBranchCondition implements BranchCondition {

    private final List<BranchCondition> conditions;

    public AnyOfBranchCondition(List<BranchCondition> conditions) {
        this.conditions = conditions;
    }

    public List<BranchCondition> conditions() {
        return conditions;
    }

    public static MapCodec<AnyOfBranchCondition> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.list(BranchCondition.CODEC).fieldOf("conditions").forGetter(AnyOfBranchCondition::conditions)
    ).apply(instance, AnyOfBranchCondition::new));

    @Override
    public boolean test(List<Integer> placedBranches) {
        return conditions.stream().anyMatch(condition -> condition.test(placedBranches));
    }

    @Override
    public BranchConditionType<?> type() {
        return BranchConditionType.ANY_OF;
    }

    @Override
    public MapCodec<? extends BranchCondition> codec() {
        return null;
    }
}
