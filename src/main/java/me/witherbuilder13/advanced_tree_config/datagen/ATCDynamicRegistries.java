package me.witherbuilder13.advanced_tree_config.datagen;

import me.witherbuilder13.advanced_tree_config.util.ATCRegistries;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import org.jspecify.annotations.NonNull;

import java.util.concurrent.CompletableFuture;

public class ATCDynamicRegistries extends FabricDynamicRegistryProvider {

    public ATCDynamicRegistries(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(HolderLookup.Provider registries, Entries entries) {
        entries.addAll(registries.lookupOrThrow(ATCRegistries.BRANCH));
        entries.addAll(registries.lookupOrThrow(Registries.FEATURE));
    }

    @Override
    public @NonNull String getName() {
        return "BTC Dynamic Registries";
    }
}
