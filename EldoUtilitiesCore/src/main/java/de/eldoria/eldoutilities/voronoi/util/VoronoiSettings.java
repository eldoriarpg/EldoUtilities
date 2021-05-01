package de.eldoria.eldoutilities.voronoi.util;

public class VoronoiSettings<Dim> {
    private final int radius;
    private final Dim center;
    private final int minFragmentSize;

    public VoronoiSettings(int radius, Dim center, int minFragmentSize) {
        this.radius = radius;
        this.center = center;
        this.minFragmentSize = minFragmentSize;
    }

    public int getRadius() {
        return radius;
    }

    public Dim getCenter() {
        return center;
    }

    public int getMinFragmentSize() {
        return minFragmentSize;
    }
}
