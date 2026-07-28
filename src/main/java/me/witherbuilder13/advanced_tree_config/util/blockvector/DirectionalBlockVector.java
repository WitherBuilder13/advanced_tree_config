package me.witherbuilder13.advanced_tree_config.util.blockvector;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.util.valueproviders.IntProviders;

import java.util.List;

public class DirectionalBlockVector implements BlockVector {

    private final List<Direction> directions;
    private final IntProvider length;

    public DirectionalBlockVector(List<Direction> directions, IntProvider length) {
        this.directions = directions;
        this.length = length;

        if (
                (directions.contains(Direction.UP) && directions.contains(Direction.DOWN)) ||
                        (directions.contains(Direction.NORTH) && directions.contains(Direction.SOUTH)) ||
                        (directions.contains(Direction.EAST) && directions.contains(Direction.WEST))
        ) {
            throw new IllegalArgumentException("\"directions\" can only contain one direction per axis");
        } else if (directions.isEmpty()) {
            throw new IllegalArgumentException("\"directions\" must not be empty");
        }
    }

    private List<Direction> directions() {
        return directions;
    }

    private IntProvider length() {
        return length;
    }

    public static MapCodec<DirectionalBlockVector> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.list(Direction.CODEC).fieldOf("directions").forGetter(DirectionalBlockVector::directions),
            IntProviders.codec(1, 64).fieldOf("length").forGetter(DirectionalBlockVector::length)
    ).apply(instance, DirectionalBlockVector::new));

    @Override
    public Vec3i getVector(RandomSource random) {
        int l = length.sample(random);
        int x = 0;
        int y = 0;
        int z = 0;

        if (directions.contains(Direction.UP))
            y = l;
        else if (directions.contains(Direction.DOWN))
            y = -l;

        if (directions.contains(Direction.NORTH))
            z = -l;
        else if (directions.contains(Direction.SOUTH))
            z = l;

        if (directions.contains(Direction.EAST))
            x = l;
        else if (directions.contains(Direction.WEST))
            x = -l;

        return new Vec3i(x, y, z);
    }

    @Override
    public BlockVectorType<?> type() {
        return BlockVectorType.DIRECTIONAL;
    }

    @Override
    public MapCodec<? extends BlockVector> codec() {
        return CODEC;
    }
}
