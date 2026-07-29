package me.witherbuilder13.advanced_tree_config.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.witherbuilder13.advanced_tree_config.feature.util.Branch;
import me.witherbuilder13.advanced_tree_config.feature.util.BranchShape;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import org.jspecify.annotations.NonNull;

import java.util.*;
import java.util.stream.Collectors;

public record AdvancedTreeFeature(List<Branch.Group> groups, boolean rotateRandomly) implements Feature {

    public static final MapCodec<AdvancedTreeFeature> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.list(Branch.Group.CODEC.codec()).fieldOf("branch_groups").forGetter(t -> t.groups),
            Codec.BOOL.optionalFieldOf("rotate_randomly", true).forGetter(t -> t.rotateRandomly)
    ).apply(instance, AdvancedTreeFeature::new));

    public AdvancedTreeFeature(List<Branch.Group> groups) {
        this(groups, true);

        if (groups.isEmpty())
            throw new IllegalArgumentException("\"branch_groups\" cannot be empty");

        List<Integer> branchIds = new ArrayList<>();
        for (Branch.Group group : groups)
            for (Branch branch : group.branches())
                if (branchIds.contains(branch.id()))
                    throw new IllegalArgumentException("Duplicate branch id: " + branch.id());
                else
                    branchIds.add(branch.id());
    }

    @Override
    public @NonNull MapCodec<AdvancedTreeFeature> codec() {
        return CODEC;
    }

    @Override
    public boolean place(WorldGenLevel level, ChunkGenerator chunkGenerator, RandomSource random, BlockPos origin) {

        List<Branch> branches = new ArrayList<>();
        groups.forEach(group -> {
            if (group.probability() > random.nextFloat())
                branches.add(group.sample(random));
        });

        Map<Integer, List<Branch>> branchesByStartNode = branches.stream().collect(Collectors.groupingBy(Branch::startNode));
        List<Node> nodes = new ArrayList<>();
        nodes.add(new Node(0, origin));

        List<Integer> placedBranches = new ArrayList<>();

        return placeBranches(nodes.getFirst(), nodes, branchesByStartNode, level, random, placedBranches);
    }

    private boolean placeBranches(Node startNode, List<Node> nodes, Map<Integer, List<Branch>> branchesByStartNode, WorldGenLevel level, RandomSource random, List<Integer> placedBranches) {
        List<Branch> branchesWithStartNode = branchesByStartNode.getOrDefault(startNode.id, List.of());

        if (branchesWithStartNode.isEmpty())
            return true;

        for (Branch branch : branchesWithStartNode) {

            if (branch.condition().isPresent() && !branch.condition().get().test(placedBranches))
                continue;

            placedBranches.add(branch.id());

            BlockStateProvider provider = branch.branchProvider();
            int offsetX = branch.config().offsetX().sample(random);
            int offsetY = branch.config().offsetY().sample(random);
            int offsetZ = branch.config().offsetZ().sample(random);

            Vec3i vector = branch.config().blockVector().getVector(random);

            BlockPos endPos = new BlockPos(
                    startNode.pos.getX() + reduce(vector.getX()) + offsetX,
                    startNode.pos.getY() + reduce(vector.getY()) + offsetY,
                    startNode.pos.getZ() + reduce(vector.getZ()) + offsetZ
            );

            BlockPos startPos = getStartPosWithOffset(startNode.pos, offsetX, offsetY, offsetZ);
            List<BlockPos> line = getLine(startPos, endPos);

            for (BlockPos pos : line)
                if (!setPosWithThickness(pos, level, provider, random, branch.config()))
                    return false;

            placedBranches.add(branch.id());

            Node endNode = new Node(branch.endNode(), endPos);
            nodes.add(endNode);

            if (!placeBranches(endNode, nodes, branchesByStartNode, level, random, placedBranches))
                return false;
        }

        return true;
    }

    private static int reduce(int length) {
        return length - Integer.signum(length);
    }

    private static BlockPos getStartPosWithOffset(BlockPos startPos, int offsetX, int offsetY, int offsetZ) {
        return new BlockPos(startPos.getX() + offsetX, startPos.getY() + offsetY, startPos.getZ() + offsetZ);
    }

    private static List<BlockPos> getLine(BlockPos start, BlockPos end) {
        List<BlockPos> line = new ArrayList<>();

        int x1 = start.getX(), y1 = start.getY(), z1 = start.getZ();
        int x2 = end.getX(), y2 = end.getY(), z2 = end.getZ();

        int dx = Math.abs(x2 - x1), dy = Math.abs(y2 - y1), dz = Math.abs(z2 - z1);
        int sx = x1 < x2 ? 1 : -1;
        int sy = y1 < y2 ? 1 : -1;
        int sz = z1 < z2 ? 1 : -1;

        int x = x1, y = y1, z = z1;

        if (dx >= dy && dx >= dz) {
            int errY = dx / 2, errZ = dx / 2;
            for (int i = 0; i <= dx; i++) {
                line.add(new BlockPos(x, y, z));
                errY -= dy;
                errZ -= dz;
                if (errY < 0) { y += sy; errY += dx; }
                if (errZ < 0) { z += sz; errZ += dx; }
                x += sx;
            }
        } else if (dy >= dx && dy >= dz) {
            int errX = dy / 2, errZ = dy / 2;
            for (int i = 0; i <= dy; i++) {
                line.add(new BlockPos(x, y, z));
                errX -= dx;
                errZ -= dz;
                if (errX < 0) { x += sx; errX += dy; }
                if (errZ < 0) { z += sz; errZ += dy; }
                y += sy;
            }
        } else {
            int errX = dz / 2, errY = dz / 2;
            for (int i = 0; i <= dz; i++) {
                line.add(new BlockPos(x, y, z));
                errX -= dx;
                errY -= dy;
                if (errX < 0) { x += sx; errX += dz; }
                if (errY < 0) { y += sy; errY += dz; }
                z += sz;
            }
        }

        return line;
    }

    private static boolean setPosWithThickness(
            BlockPos pos, WorldGenLevel level, BlockStateProvider provider, RandomSource random, Branch.Config config
    ) {
        int tX = config.thicknessX().sample(random);
        int tY = config.thicknessY().sample(random);
        int tZ = config.thicknessZ().sample(random);

        for (int x = thicknessLow(tX); x <= thicknessHigh(tX); x++)
            for (int y = thicknessLow(tY); y <= thicknessHigh(tY); y++)
                for (int z = thicknessLow(tZ); z <= thicknessHigh(tZ); z++) {
                    BlockPos currentPos = pos.offset(x, y, z);

                    BlockState state = provider.getState(level, random, currentPos);

                    if (config.shape() == BranchShape.ROUND) {
                        if (isWithinEllipsoid(currentPos, pos, new Vec3i(tX, tY, tZ))) {
                            if (state.equals(level.getBlockState(currentPos)))
                                continue;
                            if (!level.setBlock(currentPos, state, 19)) {
                                return false;
                            }
                        }
                    } else if (config.shape() == BranchShape.SQUARE_CUT_CORNERS) {
                        if (!isCorner(currentPos, pos, tX, tY, tZ)) {
                            if (state.equals(level.getBlockState(currentPos)))
                                continue;
                            if (!level.setBlock(currentPos, state, 19))
                                return false;
                        }
                    } else {
                        if (state.equals(level.getBlockState(currentPos)))
                            continue;
                        if (!level.setBlock(currentPos, state, 19))
                            return false;
                    }
                }

        return true;
    }

    private static int thicknessLow(int thickness) {
        return (thickness - 1) / -2;
    }

    private static int thicknessHigh(int thickness) {
        return thickness / 2;
    }

    private static boolean isWithinEllipsoid(BlockPos currentPos, BlockPos centerPos, Vec3i size) {

        double px = sampleCoord(currentPos.getX());
        double py = sampleCoord(currentPos.getY());
        double pz = sampleCoord(currentPos.getZ());

        double cx = centerCoord(centerPos.getX(), size.getX());
        double cy = centerCoord(centerPos.getY(), size.getY());
        double cz = centerCoord(centerPos.getZ(), size.getZ());

        double hx = size.getX() / 2.0;
        double hy = size.getY() / 2.0;
        double hz = size.getZ() / 2.0;

        double dx = (px - cx) / hx;
        double dy = (py - cy) / hy;
        double dz = (pz - cz) / hz;

        return (dx * dx + dy * dy + dz * dz) <= 1.0;
    }

    private static double sampleCoord(int blockCoord) {
        return blockCoord + 0.5;
    }

    private static double centerCoord(int centerBlockCoord, int axisSize) {
        return (axisSize % 2 == 0) ? centerBlockCoord + 1.0 : centerBlockCoord + 0.5;
    }
    
    private static boolean isCorner(BlockPos currentPos, BlockPos centerPos, int tX, int tY, int tZ) {
        int x = currentPos.getX();
        int y = currentPos.getY();
        int z = currentPos.getZ();
        
        int xMin = centerPos.getX() + thicknessLow(tX);
        int yMin = centerPos.getY() + thicknessLow(tY);
        int zMin = centerPos.getZ() + thicknessLow(tZ);
        int xMax = centerPos.getX() + thicknessHigh(tX);
        int yMax = centerPos.getY() + thicknessHigh(tY);
        int zMax = centerPos.getZ() + thicknessHigh(tZ);
        
        boolean xThick = tX >= 3;
        boolean yThick = tY >= 3;
        boolean zThick = tZ >= 3;
        
        int thickAxes = (xThick ? 1 : 0) + (yThick ? 1 : 0) + (zThick ? 1 : 0);
        if (thickAxes < 2) return false;
        
        boolean xOk = !xThick || x == xMin || x == xMax;
        boolean yOk = !yThick || y == yMin || y == yMax;
        boolean zOk = !zThick || z == zMin || z == zMax;
        
        return xOk && yOk && zOk;
    }

    private record Node(int id, BlockPos pos) {}
}
