package me.witherbuilder13.advanced_tree_config.datagen;

import me.witherbuilder13.advanced_tree_config.AdvancedTreeConfig;
import me.witherbuilder13.advanced_tree_config.feature.util.Branch;
import me.witherbuilder13.advanced_tree_config.util.ATCRegistries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;

public class BuiltinBranchConfigs {

    public static void bootstrap(BootstrapContext<Branch.Config> context) {

    }

    private static ResourceKey<Branch.Config> of(String name) {
        return ResourceKey.create(ATCRegistries.BRANCH, AdvancedTreeConfig.id(name));
    }
}
