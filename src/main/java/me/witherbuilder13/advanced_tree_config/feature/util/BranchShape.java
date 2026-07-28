package me.witherbuilder13.advanced_tree_config.feature.util;

import com.mojang.serialization.Codec;
import net.minecraft.util.StringRepresentable;
import org.jspecify.annotations.NonNull;

public enum BranchShape implements StringRepresentable {
    SQUARE("square"),
    ROUND("round");

    public static final Codec<BranchShape> CODEC = StringRepresentable.fromEnum(BranchShape::values);
    private final String id;

    BranchShape(final String id) {
        this.id = id;
    }

    @Override
    public @NonNull String getSerializedName() {
        return this.id;
    }
}
