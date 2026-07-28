package me.witherbuilder13.advanced_tree_config.util.blockvector;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.valueproviders.*;
import org.jspecify.annotations.NonNull;

import java.util.Arrays;

public class AngularBlockVector implements BlockVector {

    private FloatProvider vertical;
    private FloatProvider horizontal;
    private final DistanceType distanceType;
    IntProvider length;

    private static final FloatProvider DEFAULT_ROTATION = ConstantFloat.of(0.0F);

    private AngularBlockVector(FloatProvider vertical, FloatProvider horizontal, DistanceType distanceType, IntProvider length) {
        this.vertical = vertical;
        this.horizontal = horizontal;
        this.distanceType = distanceType;
        this.length = length;
    }

    public static AngularBlockVector of(DistanceType distanceType, IntProvider length) {
        return new AngularBlockVector(DEFAULT_ROTATION, DEFAULT_ROTATION, distanceType, length);
    }

    public AngularBlockVector vertical(FloatProvider vertical) {
        this.vertical = vertical;
        return this;
    }

    public AngularBlockVector horizontal(FloatProvider horizontal) {
        this.horizontal = horizontal;
        return this;
    }

    @Override
    public Vec3i getVector(RandomSource random) {
        float verticalAngle = vertical.sample(random);
        float horizontalAngle = horizontal.sample(random);
        int l = length.sample(random);

        float ux = (float) (Math.cos(verticalAngle) * Math.cos(horizontalAngle));
        float uy = (float) (Math.sin(verticalAngle));
        float uz = (float) (Math.cos(verticalAngle) * Math.sin(horizontalAngle));

        return switch (distanceType) {
            case MANHATTAN -> distributeTaxicab(l, ux, uy, uz);
            case CHEBYSHEV -> distributeChebyshev(l, ux, uy, uz);
            default -> new Vec3i(
                    Math.round(l * ux),
                    Math.round(l * uy),
                    Math.round(l * uz)
            );
        };
    }

    private Vec3i distributeTaxicab(int l, float ux, float uy, float uz) {
        float s = Math.abs(ux) + Math.abs(uy) + Math.abs(uz);
        float[] raw = { l * ux / s, l * uy / s, l * uz / s };

        int[] truncated = new int[3];
        float[] frac = new float[3];
        int usedAbsSum = 0;
        for (int i = 0; i < 3; i++) {
            truncated[i] = (int) raw[i]; // truncates toward zero
            frac[i] = Math.abs(raw[i] - truncated[i]);
            usedAbsSum += Math.abs(truncated[i]);
        }

        int deficit = l - usedAbsSum;
        Integer[] order = { 0, 1, 2 };
        Arrays.sort(order, (a, b) -> Float.compare(frac[b], frac[a]));
        for (int i = 0; i < deficit; i++) {
            int axis = order[i % 3];
            truncated[axis] += raw[axis] >= 0 ? 1 : -1;
        }

        return new Vec3i(truncated[0], truncated[1], truncated[2]);
    }

    private Vec3i distributeChebyshev(int l, float ux, float uy, float uz) {
        float absUx = Math.abs(ux);
        float absUy = Math.abs(uy);
        float absUz = Math.abs(uz);
        float m = Math.max(absUx, Math.max(absUy, absUz));

        int x = Math.round(l * ux / m);
        int y = Math.round(l * uy / m);
        int z = Math.round(l * uz / m);

        // guard against float noise on the dominant axis so |dominant| == l exactly
        if (absUx >= absUy && absUx >= absUz) {
            x = ux >= 0 ? l : -l;
        } else if (absUy >= absUz) {
            y = uy >= 0 ? l : -l;
        } else {
            z = uz >= 0 ? l : -l;
        }

        return new Vec3i(x, y, z);
    }

    private FloatProvider vertical() {
        return this.vertical;
    }

    private FloatProvider horizontal() {
        return this.horizontal;
    }

    private DistanceType distanceType() {
        return this.distanceType;
    }

    private IntProvider length() {
        return this.length;
    }

    public static MapCodec<AngularBlockVector> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            FloatProviders.codec(-90.0F, 90.0F).optionalFieldOf("vertical_rotation", DEFAULT_ROTATION).forGetter(AngularBlockVector::vertical),
            FloatProviders.codec(-180.0F, 180.0F).optionalFieldOf("horizontal_rotation", DEFAULT_ROTATION).forGetter(AngularBlockVector::horizontal),
            DistanceType.CODEC.fieldOf("distance_type").forGetter(AngularBlockVector::distanceType),
            IntProviders.codec(1, 64).fieldOf("length").forGetter(AngularBlockVector::length)
    ).apply(instance, AngularBlockVector::new));

    @Override
    public BlockVectorType<?> type() {
        return BlockVectorType.ANGULAR;
    }

    @Override
    public MapCodec<? extends BlockVector> codec() {
        return CODEC;
    }

    public enum DistanceType implements StringRepresentable {
        EUCLIDEAN("euclidean"),
        MANHATTAN("manhattan"),
        CHEBYSHEV("chebyshev");

        private final String id;

        DistanceType(final String id) {
            this.id = id;
        }

        public static final Codec<DistanceType> CODEC = StringRepresentable.fromEnum(DistanceType::values);

        @Override
        public @NonNull String getSerializedName() {
            return id;
        }
    }
}
