package me.witherbuilder13.advanced_tree_config;

import me.witherbuilder13.advanced_tree_config.feature.AdvancedTreeFeature;
import me.witherbuilder13.advanced_tree_config.feature.util.branchcondition.BranchConditionType;
import me.witherbuilder13.advanced_tree_config.util.ATCRegistries;
import me.witherbuilder13.advanced_tree_config.feature.util.Branch;
import me.witherbuilder13.advanced_tree_config.util.blockvector.BlockVectorType;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AdvancedTreeConfig implements ModInitializer {
	public static final String MOD_ID = "advanced_tree_config";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		DynamicRegistries.registerSynced(ATCRegistries.BRANCH, Branch.Config.CODEC.codec());
		Registry.register(BuiltInRegistries.FEATURE_TYPE, id("advanced_tree"), AdvancedTreeFeature.CODEC);
		BlockVectorType.init();
		BranchConditionType.init();
	}

	public static Identifier id(String path) {
		return Identifier.fromNamespaceAndPath(MOD_ID, path);
	}
}
