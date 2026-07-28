package me.witherbuilder13.advanced_tree_config.feature.util.branchcondition;

import com.mojang.serialization.MapCodec;
import me.witherbuilder13.advanced_tree_config.AdvancedTreeConfig;
import me.witherbuilder13.advanced_tree_config.util.ATCRegistries;
import net.minecraft.core.Registry;

public interface BranchConditionType<C extends BranchCondition> {
    BranchConditionType<IfPlacedBranchCondition> IF_PLACED = register("if_placed", IfPlacedBranchCondition.CODEC);
    BranchConditionType<NotBranchCondition> NOT = register("not", NotBranchCondition.CODEC);
    BranchConditionType<AnyOfBranchCondition> ANY_OF = register("any_of", AnyOfBranchCondition.CODEC);
    BranchConditionType<AllOfBranchCondition> ALL_OF = register("all_of", AllOfBranchCondition.CODEC);

    MapCodec<C> codec();

    private static <P extends BranchCondition> BranchConditionType<P> register(final String path, final MapCodec<P> codec) {
        return Registry.register(ATCRegistries.BRANCH_CONDITION_TYPE, AdvancedTreeConfig.id(path), () -> codec);
    }

    static void init() {}
}
