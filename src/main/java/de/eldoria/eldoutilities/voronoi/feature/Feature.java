package de.eldoria.eldoutilities.voronoi.feature;

public abstract class Feature<Dim> {
    private final Dim pos;

    protected Feature(Dim pos) {
        this.pos = pos;
    }

    public Dim getPos() {
        return pos;
    }
}
