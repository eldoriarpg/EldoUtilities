package de.eldoria.eldoutilities.voronoi.untis;

import de.eldoria.eldoutilities.container.Pair;
import de.eldoria.eldoutilities.utils.EMath;
import de.eldoria.eldoutilities.voronoi.feature.Feature;
import de.eldoria.eldoutilities.voronoi.feature.WeightedFeature;
import de.eldoria.eldoutilities.voronoi.util.DimensionAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public abstract class VoronoiUnit<Dim, FeatureType extends Feature<Dim>> {
    protected final VoronoiUnit<Dim, FeatureType> parent;
    protected final Dim center;
    protected final int size;
    protected final DimensionAdapter<Dim> dimensionAdapter;
    private final double northBorder;
    private final double eastBorder;
    private final double southBorder;
    private final double westBorder;
    private Dim upperLeft;
    private Dim lowerRight;

    public VoronoiUnit(VoronoiUnit<Dim, FeatureType> parent, Dim center, int size, DimensionAdapter<Dim> dimensionAdapter) {
        this.parent = parent;
        this.center = center;
        this.size = size;
        this.dimensionAdapter = dimensionAdapter;
        double centerZ = dimensionAdapter.getZ(center);
        double centerX = dimensionAdapter.getX(center);
        int borderOffset = size / 2;
        this.northBorder = centerZ + borderOffset;
        this.eastBorder = centerX + borderOffset;
        this.southBorder = centerZ - borderOffset;
        this.westBorder = centerX - borderOffset;

    }

    public Dim getUpperLeft() {
        if (upperLeft == null) {
            upperLeft = dimensionAdapter.construct(westBorder, northBorder);

        }
        return upperLeft;
    }

    public Dim getLowerRight() {
        if (lowerRight == null) {
            lowerRight = dimensionAdapter.construct(eastBorder, southBorder);
        }
        return lowerRight;
    }

    public abstract boolean isEmpty();

    public abstract int getFeatureCount();

    public abstract void addFeature(FeatureType feature);

    public abstract Collection<FeatureType> getFeatures();

    public static <Dim, FeatureType extends Feature<Dim>> VoronoiUnit<Dim, FeatureType>
    getEmpty(VoronoiUnit<Dim, FeatureType> parent, Dim center, int size, DimensionAdapter<Dim> dimensionAdapter) {
        return new VoronoiUnit<Dim, FeatureType>(parent, center, size, dimensionAdapter) {

            @Override
            public boolean isEmpty() {
                return true;
            }

            @Override
            public int getFeatureCount() {
                return 0;
            }

            @Override
            public void addFeature(FeatureType feature) {
                throw new UnsupportedOperationException("This is an empty unit. It should be only used to avoid overhead.");
            }

            @Override
            public Collection<FeatureType> getFeatures() {
                return Collections.emptyList();
            }

            @Override
            public Chunk<Dim, FeatureType> getChunk(Dim point) {
                throw new UnsupportedOperationException("This is an empty unit. It should be only used to avoid overhead.");
            }

            @Override
            public int getLayerCount(int count) {
                return this.parent.getLayerCount(0);
            }

            @Override
            public VoronoiUnit<Dim, FeatureType> retrieveLayerUnit(Dim point, int count) {
                return this;
            }

            @Override
            public WeightedFeature<Dim, FeatureType> getClosestFeature(Dim pos) {
                throw new UnsupportedOperationException("This is an empty unit. It should be only used to avoid overhead.");
            }
        };
    }

    /**
     * Get the chunk for the position.
     * This will create the chunk if it is not presen.
     *
     * @param point point
     * @return the chunk of the point
     */
    public abstract Chunk<Dim, FeatureType> getChunk(Dim point);

    public int getLayerCount() {
        return getLayerCount(0);
    }

    /**
     * Get the layer this unit is on in the grid.
     *
     * @param count count of the layer
     * @return layer count
     */
    public abstract int getLayerCount(int count);

    public abstract VoronoiUnit<Dim, FeatureType> retrieveLayerUnit(Dim point, int count);

    /**
     * Check if a point is inside this unit.
     *
     * @param pos pos to check
     * @return true if the position is inside this unit.
     */
    public boolean isInside(Dim pos) {
        Dim upperLeft = getBoundaries().first;
        Dim lowerRight = getBoundaries().second;
        double posX = dimensionAdapter.getX(pos);
        double posZ = dimensionAdapter.getZ(pos);

        if (posX < dimensionAdapter.getX(upperLeft) || posX >= dimensionAdapter.getX(lowerRight)) {
            return false;
        }

        if (posZ < dimensionAdapter.getZ(lowerRight) || posZ >= dimensionAdapter.getZ(upperLeft)) {
            return false;
        }
        return true;
    }

    /**
     * Retrieve the boundaries of this unit.
     * <p>
     * The right and upper boundaries are considered as exclusive. {@link #isInside(Dim)} will return false for them
     *
     * @return A pair with the both corners. first is the upper left and second the lower right corner.
     */
    public Pair<Dim, Dim> getBoundaries() {
        return new Pair<>(getUpperLeft(), getLowerRight());
    }

    public Dim getBorder(Border border) {
        double centerZ = dimensionAdapter.getZ(center);
        double centerX = dimensionAdapter.getX(center);

        double centerOffset = size / 2d;
        switch (border) {
            case NORTH:
                return dimensionAdapter.construct(centerX, northBorder);
            case NORTH_EAST:
                return dimensionAdapter.construct(eastBorder, northBorder);
            case EAST:
                return dimensionAdapter.construct(eastBorder, centerZ);
            case SOUTH_EAST:
                return dimensionAdapter.construct(eastBorder, southBorder);
            case SOUTH:
                return dimensionAdapter.construct(centerX, southBorder);
            case SOUTH_WEST:
                return dimensionAdapter.construct(westBorder, southBorder);
            case WEST:
                return dimensionAdapter.construct(westBorder, centerZ);
            case NORTH_WEST:
                return dimensionAdapter.construct(westBorder, northBorder);
            default:
                throw new IllegalStateException("Unexpected value: " + border);
        }
    }

    public Collection<Border> getBordersCloserThan(Dim pos, double threshold) {
        List<Border> borders = new ArrayList<>();
        for (Border border : Border.values()) {
            if (getBorderDistance(border, pos) < threshold) {
                borders.add(border);
            }
        }
        return borders;
    }

    public double getNearestBorderDistance(Dim pos) {
        double min = Double.MAX_VALUE;
        for (Border value : Border.values()) {
            min = Math.min(getBorderDistance(value, pos), min);
        }
        return min;
    }

    public double getBorderDistance(Border border, Dim pos) {
        switch (border) {
            case NORTH_EAST:
            case NORTH_WEST:
            case SOUTH_WEST:
            case SOUTH_EAST:
                return dimensionAdapter.distance(getBorder(border), pos);
            case NORTH:
                return EMath.diff(northBorder, dimensionAdapter.getZ(pos));
            case EAST:
                return EMath.diff(eastBorder, dimensionAdapter.getX(pos));
            case SOUTH:
                return EMath.diff(southBorder, dimensionAdapter.getZ(pos));
            case WEST:
                return EMath.diff(westBorder, dimensionAdapter.getX(pos));
            default:
                throw new IllegalStateException("Unexpected value: " + border);
        }
    }

    protected VoronoiUnit<Dim, FeatureType> getNeightbour(Border border) {
        switch (border) {
            case NORTH:
                return retrievePositionOnLayer(dimensionAdapter.plus(getBorder(border), 0, 1), 0);
            case NORTH_EAST:
                return retrievePositionOnLayer(dimensionAdapter.plus(getBorder(border), 1, 1), 0);
            case EAST:
                return retrievePositionOnLayer(dimensionAdapter.plus(getBorder(border), 1, 0), 0);
            case SOUTH_EAST:
                return retrievePositionOnLayer(dimensionAdapter.plus(getBorder(border), 1, -1), 0);
            case SOUTH:
                return retrievePositionOnLayer(dimensionAdapter.plus(getBorder(border), 0, -1), 0);
            case SOUTH_WEST:
                return retrievePositionOnLayer(dimensionAdapter.plus(getBorder(border), -1, -1), 0);
            case WEST:
                return retrievePositionOnLayer(dimensionAdapter.plus(getBorder(border), -1, 0), 0);
            case NORTH_WEST:
                return retrievePositionOnLayer(dimensionAdapter.plus(getBorder(border), -1, 1), 0);
            default:
                throw new IllegalStateException("Unexpected value: " + border);
        }
    }

    private VoronoiUnit<Dim, FeatureType> retrievePositionOnLayer(Dim point, int layer) {
        if (isInside(point)) {
            return retrieveLayerUnit(point, layer);
        }
        return parent.retrieveLayerUnit(point, layer + 1);
    }

    public WeightedFeature<Dim, FeatureType> getClosestFeature(Dim pos) {
        // find the closes feature
        WeightedFeature<Dim, FeatureType> closestFeature = findClosestFeatureHere(pos);

        if (closestFeature == null) {
            // This wont happen until the whole thing is empty.
            return null;
        }

        Collection<Border> bordersCloserThan = getBordersCloserThan(closestFeature.getFeature().getPos(), closestFeature.getDistance());

        List<FeatureType> neighborFeatures = new ArrayList<>();
        for (Border border : bordersCloserThan) {
            VoronoiUnit<Dim, FeatureType> neightbour = getNeightbour(border);
            neighborFeatures.addAll(neightbour.getFeatures());
        }

        if (neighborFeatures.isEmpty()) {
            return closestFeature;
        }

        List<WeightedFeature<Dim, FeatureType>> weightedFeatures = weightedFeatures(pos, neighborFeatures);

        WeightedFeature<Dim, FeatureType> otherClosest = weightedFeatures.get(0);

        return closestFeature.getDistanceSquared() > otherClosest.getDistanceSquared() ? otherClosest : closestFeature;

    }

    private WeightedFeature<Dim, FeatureType> findClosestFeatureHere(Dim pos) {
        List<WeightedFeature<Dim, FeatureType>> weightedFeatures = weightFeatures(pos);
        if (weightedFeatures.isEmpty()) {
            return null;
        }
        return weightedFeatures.get(0);
    }

    public List<WeightedFeature<Dim, FeatureType>> weightFeatures(Dim pos) {
        return weightedFeatures(pos, getFeatures());
    }

    private List<WeightedFeature<Dim, FeatureType>> weightedFeatures(Dim pos, Collection<FeatureType> features) {
        return features.stream()
                .map(f -> WeightedFeature.weight(pos, f, dimensionAdapter))
                .sorted()
                .collect(Collectors.toList());
    }
}
