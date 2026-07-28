package me.witherbuilder13.advanced_tree_config.util.blockvector;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;

public class SimpleBlockVector implements BlockVector {

    private IntProvider lengthX;
    private IntProvider lengthY;
    private IntProvider lengthZ;

    private static final IntProvider DEFAULT_LENGTH = ConstantInt.of(1);

    private SimpleBlockVector(IntProvider lengthX, IntProvider lengthY, IntProvider lengthZ) {
        this.lengthX = lengthX;
        this.lengthY = lengthY;
        this.lengthZ = lengthZ;
    }

    public static SimpleBlockVector of() {
        return new SimpleBlockVector(DEFAULT_LENGTH, DEFAULT_LENGTH, DEFAULT_LENGTH);
    }

    public SimpleBlockVector lengthX(IntProvider lengthX) {
        this.lengthX = lengthX;
        return this;
    }

    public SimpleBlockVector lengthY(IntProvider lengthY) {
        this.lengthY = lengthY;
        return this;
    }

    public SimpleBlockVector lengthZ(IntProvider lengthZ) {
        this.lengthZ = lengthZ;
        return this;
    }

    public static MapCodec<SimpleBlockVector> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            IntProviders.codec(1, 64).optionalFieldOf("length_x", DEFAULT_LENGTH).forGetter(l -> l.lengthX),
            IntProviders.codec(1, 64).optionalFieldOf("length_y", DEFAULT_LENGTH).forGetter(l -> l.lengthY),
            IntProviders.codec(1, 64).optionalFieldOf("length_z", DEFAULT_LENGTH).forGetter(l -> l.lengthZ)
    ).apply(instance, SimpleBlockVector::new));

    @Override
    public MapCodec<SimpleBlockVector> codec() {
        return CODEC;
    }

    @Override
    public Vec3i getVector(RandomSource random) {
        return new Vec3i(lengthX.sample(random), lengthY.sample(random), lengthZ.sample(random));
    }

    @Override
    public BlockVectorType<?> type() {
        return BlockVectorType.SIMPLE;
    }
}
