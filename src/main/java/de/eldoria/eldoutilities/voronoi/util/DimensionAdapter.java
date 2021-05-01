package de.eldoria.eldoutilities.voronoi.util;

public interface DimensionAdapter<Dim> {
    Dim plus(Dim pos, int x, int z);

    /**
     * Get the distance from a point to a point.
     *
     * @param pos1 position of point;
     * @param pos2 position of another point;
     * @return distance
     */
    double distance(Dim pos1, Dim pos2);

    /**
     * Get the distance from a point to a point.
     *
     * @param pos1 position of point;
     * @param pos2 position of another point;
     * @return distance squared
     */
    double distanceSquared(Dim pos1, Dim pos2);

    /**
     * Compares x position of the feature.
     *
     * @param pos pos of the other position
     * @return the value {@code 0} if {@code pos} is
     * numerically equal to this feature; A value less than
     * {@code 0} if {@code pos} is numerically less than
     * this feature; and a value greater than {@code 0}
     * if {@code pos} is numerically greater than
     * this feature.
     */
    int xCompare(Dim ref, Dim pos);

    /**
     * Compares z position of the dimensions.
     *
     * @param pos pos of the other position
     * @return the value {@code 0} if {@code pos} is
     * numerically equal to this feature; A value less than
     * {@code 0} if {@code pos} is numerically less than
     * this feature; and a value greater than {@code 0}
     * if {@code pos} is numerically greater than
     * this feature.
     */
    int zCompare(Dim ref, Dim pos);

    /**
     * Get the x value of dimension
     *
     * @param pos dimension
     * @return x value of dimension
     */
    double getX(Dim pos);

    /**
     * Get the z value of dimension
     *
     * @param pos dimension
     * @return z value of dimension
     */
    double getZ(Dim pos);

    /**
     * Set the z value of dimension
     *
     * @param pos dimension
     * @param value value of z
     * @return the new dimension with z set
     */
    Dim setZ(Dim pos, double value);

    /**
     * Get the x value of dimension
     *
     * @param pos dimension
     * @param value value of x
     * @return the new dimension with x set
     */
    Dim setX(Dim pos, double value);

    Dim construct(double x, double z);

    Dim multiply(Dim pos, double multiplier);
}
