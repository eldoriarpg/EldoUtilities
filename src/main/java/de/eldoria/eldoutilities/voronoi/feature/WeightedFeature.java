package de.eldoria.eldoutilities.voronoi.feature;

import de.eldoria.eldoutilities.voronoi.util.DimensionAdapter;
import org.jetbrains.annotations.NotNull;

public final class WeightedFeature<Dim, FeatureType extends Feature<Dim>> implements Comparable<WeightedFeature<Dim, FeatureType>> {
    private final double distance;
    private final FeatureType feature;

    private WeightedFeature(FeatureType featzure, double distance) {
        this.feature = featzure;
        this.distance = distance;
    }

    public static <Dim, FeatureType extends Feature<Dim>> WeightedFeature<Dim, FeatureType> weight(Dim pos, FeatureType feature, DimensionAdapter<Dim> adapter) {
        return new WeightedFeature<>(feature, adapter.distanceSquared(feature.getPos(), pos));
    }

    public double getDistanceSquared() {
        return distance;
    }

    public double getDistance() {
        return Math.sqrt(distance);
    }

    public FeatureType getFeature() {
        return feature;
    }

    @Override
    public int compareTo(@NotNull WeightedFeature<Dim, FeatureType> o) {
        return Double.compare(distance, o.distance);
    }
}
