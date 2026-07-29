package me.witherbuilder13.advanced_tree_config.datagen;

import me.witherbuilder13.advanced_tree_config.util.ATCRegistries;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;

public class ATCDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		pack.addProvider(ATCDynamicRegistries::new);
	}

	@Override
	public void buildRegistry(RegistrySetBuilder builder) {
		builder.add(ATCRegistries.BRANCH, BuiltinBranchConfigs::bootstrap);
		builder.add(Registries.FEATURE, ATCFeatures::bootstrap);
	}
}
