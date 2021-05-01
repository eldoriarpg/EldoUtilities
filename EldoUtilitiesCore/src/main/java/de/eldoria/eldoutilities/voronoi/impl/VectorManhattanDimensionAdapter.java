package de.eldoria.eldoutilities.voronoi.impl;

import org.bukkit.util.Vector;

public class VectorManhattanDimensionAdapter extends VectorDimensionAdapter {

    @Override
    public double distance(Vector pos1, Vector pos2) {
        return distanceSquared(pos1, pos2);
    }

    @Override
    public double distanceSquared(Vector pos1, Vector pos2) {
        return Math.abs(pos1.getX() - pos2.getX()) + Math.abs(pos1.getZ() - pos2.getZ());
    }
}
