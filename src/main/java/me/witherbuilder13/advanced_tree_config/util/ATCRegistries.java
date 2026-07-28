package me.witherbuilder13.advanced_tree_config.util;

import me.witherbuilder13.advanced_tree_config.AdvancedTreeConfig;
import me.witherbuilder13.advanced_tree_config.feature.util.Branch;
import me.witherbuilder13.advanced_tree_config.feature.util.branchcondition.BranchConditionType;
import me.witherbuilder13.advanced_tree_config.util.blockvector.BlockVectorType;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

public class ATCRegistries {

    public static final ResourceKey<Registry<Branch.Config>> BRANCH = ResourceKey.createRegistryKey(AdvancedTreeConfig.id("branch_config"));

    public static final ResourceKey<Registry<BlockVectorType<?>>> BLOCK_VECTOR_TYPE_KEY = ResourceKey.createRegistryKey(AdvancedTreeConfig.id("block_vector_type"));
    public static final Registry<BlockVectorType<?>> BLOCK_VECTOR_TYPE = FabricRegistryBuilder.create(BLOCK_VECTOR_TYPE_KEY).buildAndRegister();

    public static final ResourceKey<Registry<BranchConditionType<?>>> BRANCH_CONDITION_TYPE_KEY = ResourceKey.createRegistryKey(AdvancedTreeConfig.id("branch_condition_type"));
    public static final Registry<BranchConditionType<?>> BRANCH_CONDITION_TYPE = FabricRegistryBuilder.create(BRANCH_CONDITION_TYPE_KEY).buildAndRegister();
}
