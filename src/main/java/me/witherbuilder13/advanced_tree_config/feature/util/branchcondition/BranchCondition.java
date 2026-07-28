package me.witherbuilder13.advanced_tree_config.feature.util.branchcondition;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import me.witherbuilder13.advanced_tree_config.util.ATCRegistries;

import java.util.Arrays;
import java.util.List;

public interface BranchCondition {

    static BranchCondition allOf(BranchCondition... conditions) {
        return new AllOfBranchCondition(Arrays.stream(conditions).toList());
    }

    static BranchCondition anyOf(BranchCondition... conditions) {
        return new AnyOfBranchCondition(Arrays.stream(conditions).toList());
    }

    static BranchCondition not(BranchCondition condition) {
        return new NotBranchCondition(condition);
    }

    static BranchCondition ifPlaced(int branchId) {
        return new IfPlacedBranchCondition(branchId);
    }

    boolean test(List<Integer> placedBranches);

    Codec<BranchCondition> CODEC = ATCRegistries.BRANCH_CONDITION_TYPE.byNameCodec().dispatch(BranchCondition::type, BranchConditionType::codec);

    BranchConditionType<?> type();
    MapCodec<? extends BranchCondition> codec();
}
