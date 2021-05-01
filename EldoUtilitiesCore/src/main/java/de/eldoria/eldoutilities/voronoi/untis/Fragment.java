package de.eldoria.eldoutilities.voronoi.untis;

import de.eldoria.eldoutilities.voronoi.feature.Feature;
import de.eldoria.eldoutilities.voronoi.feature.WeightedFeature;
import de.eldoria.eldoutilities.voronoi.util.DimensionAdapter;
import de.eldoria.eldoutilities.voronoi.util.VoronoiSettings;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

public class Fragment<Dim, FeatureType extends Feature<Dim>> extends VoronoiUnit<Dim, FeatureType> {
    private final VoronoiSettings<Dim> settings;
    private VoronoiUnit<Dim, FeatureType> upperLeftSector;
    private VoronoiUnit<Dim, FeatureType> upperRightSector;
    private VoronoiUnit<Dim, FeatureType> lowerLeftSector;
    private VoronoiUnit<Dim, FeatureType> lowerRightSector;

    public Fragment(VoronoiUnit<Dim, FeatureType> parent, Dim center, double size, VoronoiSettings<Dim> settings,
                    DimensionAdapter<Dim> dimensionAdapter) {
        super(parent, center, size, dimensionAdapter);
        this.settings = settings;
    }

    @Override
    public boolean isEmpty() {
        for (VoronoiUnit<Dim, FeatureType> sector : getSectors()) {
            if (!sector.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int getFeatureCount() {
        int count = 0;
        for (VoronoiUnit<Dim, FeatureType> sector : getSectors()) {
            count += sector.getFeatureCount();
        }
        return count;
    }

    private Collection<VoronoiUnit<Dim, FeatureType>> getSectors() {
        return Arrays.asList(getUpperLeftSectorOrEmpty(), getUpperRightSectorOrEmpty(), getLowerLeftOrEmpty(), getLowerRightOrEmpty());
    }

    @Override
    public void addFeature(FeatureType feature) {
        getSector(feature).addFeature(feature);
    }

    protected VoronoiUnit<Dim, FeatureType> getSector(FeatureType feature) {
        return getSector(feature.getPos());
    }

    protected VoronoiUnit<Dim, FeatureType> getSector(Dim pos) {
        int xComp = dimensionAdapter.xCompare(pos, center);
        int zComp = dimensionAdapter.zCompare(pos, center);
        if (xComp != -1) {
            // target sector is on the right side
            return zComp == -1 ? getLowerRightSector() : getUpperRightSector();
        } else {
            // target sector is on the left side
            return zComp == -1 ? getLowerLeftSector() : getUpperLeftSector();
        }
    }

    private VoronoiUnit<Dim, FeatureType> getSectorOrEmpty(Dim pos) {
        int xComp = dimensionAdapter.xCompare(pos, center);
        int zComp = dimensionAdapter.zCompare(pos, center);
        if (xComp != -1) {
            return zComp == -1 ? getLowerRightOrEmpty() : getUpperRightSectorOrEmpty();
        } else {
            return zComp == -1 ? getLowerLeftOrEmpty() : getUpperLeftSectorOrEmpty();
        }
    }

    @Override
    public Collection<FeatureType> getFeatures() {
        List<FeatureType> features = new ArrayList<>();
        for (VoronoiUnit<Dim, FeatureType> sector : getSectors()) {
            features.addAll(sector.getFeatures());
        }
        return features;
    }

    @Override
    public Chunk<Dim, FeatureType> getChunk(Dim point) {
        return getSector(point).getChunk(point);
    }

    @Override
    public int getLayerCount(int count) {
        return parent.getLayerCount(count + 1);
    }

    @Override
    public VoronoiUnit<Dim, FeatureType> retrieveLayerUnit(Dim point, int count) {
        if (count == 0) {
            return this;
        }
        return getSectorOrEmpty(point).retrieveLayerUnit(point, count - 1);
    }

    @Override
    public WeightedFeature<Dim, FeatureType> getClosestFeature(Dim pos) {
        // lets check if we can still reduce the total features
        VoronoiUnit<Dim, FeatureType> sector = getSector(pos);
        if (sector.getFeatureCount() < 5) {
            return super.getClosestFeature(pos);
        }
        return sector.getClosestFeature(pos);

    }

    public VoronoiUnit<Dim, FeatureType> getUpperLeftSector() {
        if (upperLeftSector == null) {
            upperLeftSector = buildFragmentOrChunk(half -> dimensionAdapter.plus(center, -half, half));
        }
        return upperLeftSector;
    }

    public VoronoiUnit<Dim, FeatureType> getUpperRightSector() {
        if (upperRightSector == null) {
            upperRightSector = buildFragmentOrChunk(half -> dimensionAdapter.plus(center, half, half));
        }
        return upperRightSector;
    }

    public VoronoiUnit<Dim, FeatureType> getLowerLeftSector() {
        if (lowerLeftSector == null) {
            lowerLeftSector = buildFragmentOrChunk(half -> dimensionAdapter.plus(center, -half, -half));
        }
        return lowerLeftSector;
    }

    public VoronoiUnit<Dim, FeatureType> getLowerRightSector() {
        if (lowerRightSector == null) {
            lowerRightSector = buildFragmentOrChunk(half -> dimensionAdapter.plus(center, half, -half));
        }
        return lowerRightSector;
    }

    private VoronoiUnit<Dim, FeatureType> buildFragmentOrChunk(Function<Double, Dim> newCenter) {
        double newHalf = size / 2;
        Dim center = newCenter.apply(newHalf / 2d);
        if (size <= settings.getMinFragmentSize()) {
            return new Chunk<>(this, center, newHalf, dimensionAdapter);
        } else {
            return new Fragment<>(this, center, newHalf, settings, dimensionAdapter);
        }
    }

    public VoronoiUnit<Dim, FeatureType> getEmptyIfAbsent(Supplier<VoronoiUnit<Dim, FeatureType>> supplier, Function<Double, Dim> newCenter) {
        double newSize = size / 2;
        Dim center = newCenter.apply(newSize / 2);
        if (supplier.get() == null) {
            return VoronoiUnit.getEmpty(this, center, newSize, dimensionAdapter);
        }
        return supplier.get();
    }

    public VoronoiUnit<Dim, FeatureType> getUpperLeftSectorOrEmpty() {
        return getEmptyIfAbsent(() -> upperLeftSector, half -> dimensionAdapter.plus(center, -half, half));
    }

    public VoronoiUnit<Dim, FeatureType> getUpperRightSectorOrEmpty() {
        return getEmptyIfAbsent(() -> upperRightSector, half -> dimensionAdapter.plus(center, half, half));
    }

    public VoronoiUnit<Dim, FeatureType> getLowerLeftOrEmpty() {
        return getEmptyIfAbsent(() -> lowerLeftSector, half -> dimensionAdapter.plus(center, -half, -half));
    }

    public VoronoiUnit<Dim, FeatureType> getLowerRightOrEmpty() {
        return getEmptyIfAbsent(() -> lowerRightSector, half -> dimensionAdapter.plus(center, half, -half));
    }
}
