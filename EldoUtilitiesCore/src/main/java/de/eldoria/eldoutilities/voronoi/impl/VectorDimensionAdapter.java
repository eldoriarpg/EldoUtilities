package de.eldoria.eldoutilities.voronoi.impl;

import de.eldoria.eldoutilities.voronoi.util.DimensionAdapter;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

public class VectorDimensionAdapter implements DimensionAdapter<Vector> {
    @Override
    public Vector plus(Vector pos, int x, int z) {
        return pos.clone().add(new Vector(x, 0, z));
    }

    @Override
    public double distance(Vector pos1, Vector pos2) {
        return Math.sqrt(NumberConversions.square(pos1.getX() - pos2.getX()) + NumberConversions.square(pos1.getZ() - pos2.getZ()));
    }

    @Override
    public double distanceSquared(Vector pos1, Vector pos2) {
        return NumberConversions.square(pos1.getX() - pos2.getX()) + NumberConversions.square(pos1.getZ() - pos2.getZ());
    }

    @Override
    public int xCompare(Vector ref, Vector pos) {
        return Double.compare(ref.getX(), pos.getX());
    }

    @Override
    public int zCompare(Vector ref, Vector pos) {
        return Double.compare(ref.getZ(), pos.getZ());
    }

    @Override
    public double getX(Vector pos) {
        return pos.getX();
    }

    @Override
    public double getZ(Vector pos) {
        return pos.getZ();
    }

    @Override
    public Vector setX(Vector pos, double value) {
        return pos.clone().setX(value);
    }

    @Override
    public Vector setZ(Vector pos, double value) {
        return pos.clone().setZ(value);
    }

    @Override
    public Vector construct(double x, double z) {
        return new Vector(x, 0, z);
    }

    @Override
    public Vector multiply(Vector pos, double multiplier) {
        return pos.clone().multiply(multiplier);
    }
}
