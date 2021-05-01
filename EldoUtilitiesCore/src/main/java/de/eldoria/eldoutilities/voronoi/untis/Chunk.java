package de.eldoria.eldoutilities.voronoi.untis;

import de.eldoria.eldoutilities.voronoi.feature.Feature;
import de.eldoria.eldoutilities.voronoi.util.DimensionAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class Chunk<Dim, FeatureType extends Feature<Dim>> extends VoronoiUnit<Dim, FeatureType> {
    private final List<FeatureType> features = new ArrayList<>();

    public Chunk(VoronoiUnit<Dim, FeatureType> parent, Dim center, double size, DimensionAdapter<Dim> dimensionAdapter) {
        super(parent, center, size, dimensionAdapter);
    }

    @Override
    public boolean isEmpty() {
        return features.isEmpty();
    }

    @Override
    public int getFeatureCount() {
        return features.size();
    }

    @Override
    public void addFeature(FeatureType feature) {
        features.add(feature);
    }

    @Override
    public Collection<FeatureType> getFeatures() {
        return Collections.unmodifiableCollection(features);
    }

    @Override
    public Chunk<Dim, FeatureType> getChunk(Dim point) {
        return this;
    }

    @Override
    public int getLayerCount(int count) {
        return parent.getLayerCount(count + 1);
    }

    @Override
    public VoronoiUnit<Dim, FeatureType> retrieveLayerUnit(Dim point, int count) {
        if (count != 0) {
            throw new IndexOutOfBoundsException("The requested layer is below chunk level");
        }
        return this;
    }
}
