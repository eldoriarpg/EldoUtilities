package de.eldoria.eldoutilities.voronoi;

import de.eldoria.eldoutilities.voronoi.feature.Feature;
import de.eldoria.eldoutilities.voronoi.untis.Fragment;
import de.eldoria.eldoutilities.voronoi.util.DimensionAdapter;
import de.eldoria.eldoutilities.voronoi.util.VoronoiSettings;

public class Voronoi<Dim, FeatureType extends Feature<Dim>> extends Fragment<Dim, FeatureType> {
    public Voronoi(Dim center, int size, VoronoiSettings<Dim> settings, DimensionAdapter<Dim> dimensionAdapter) {
        super(null, center, size, settings, dimensionAdapter);
    }

    /**
     * Creates a new voronoi diagram
     *
     * @param settings         settings of voronoi
     * @param dimensionAdapter adapter for dimension mapping
     * @param <Dim>            type of dimension implementation
     * @param <FeatureType>    type of feature implementation
     * @return voronoi instance
     */
    public static <Dim, FeatureType extends Feature<Dim>> Voronoi<Dim, FeatureType> create(VoronoiSettings<Dim> settings, DimensionAdapter<Dim> dimensionAdapter) {
        int size = settings.getRadius() * 2;
        return new Voronoi<>(settings.getCenter(), size, settings, dimensionAdapter);
    }

    @Override
    public int getLayerCount(int count) {
        return count;
    }
}
