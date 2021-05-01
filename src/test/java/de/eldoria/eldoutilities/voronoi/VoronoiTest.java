package de.eldoria.eldoutilities.voronoi;

import de.eldoria.eldoutilities.container.Pair;
import de.eldoria.eldoutilities.utils.Stopwatch;
import de.eldoria.eldoutilities.voronoi.feature.WeightedFeature;
import de.eldoria.eldoutilities.voronoi.impl.VectorDimensionAdapter;
import de.eldoria.eldoutilities.voronoi.impl.VectorFeature;
import de.eldoria.eldoutilities.voronoi.impl.VectorManhattanDimensionAdapter;
import de.eldoria.eldoutilities.voronoi.untis.VoronoiUnit;
import de.eldoria.eldoutilities.voronoi.util.VoronoiSettings;
import org.bukkit.util.Vector;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class VoronoiTest {
    private VectorDimensionAdapter dimensionAdapter;
    private VoronoiSettings<Vector> settings;
    private Voronoi<Vector, VectorFeature> voronoi;
    private Vector upperRight;
    private Vector upperLeft;
    private Vector lowerRight;
    private Vector lowerLeft;

    @BeforeEach
    void build() {
        dimensionAdapter = new VectorDimensionAdapter();
        settings = new VoronoiSettings<>(30000000, new Vector(), 1024);
        voronoi = Voronoi.create(settings, dimensionAdapter);
        upperRight = new Vector(0, 0, 0);
        upperLeft = new Vector(-1, 0, 0);
        lowerRight = new Vector(0, 0, -1);
        lowerLeft = new Vector(-1, 0, -1);
    }

    @Test
    void testBoundaries() {
        int radius = settings.getRadius();

        Pair<Vector, Vector> boundaries = voronoi.getBoundaries();

        VoronoiUnit<Vector, VectorFeature> currUnit = voronoi.retrieveLayerUnit(upperRight, 1);
        Pair<Vector, Vector> currBoundaries = currUnit.getBoundaries();
        Assertions.assertEquals(0, (int) currBoundaries.first.getX());
        Assertions.assertEquals(radius, (int) currBoundaries.first.getZ());
        Assertions.assertEquals(radius, (int) currBoundaries.second.getX());
        Assertions.assertEquals(0, (int) currBoundaries.second.getZ());

        Assertions.assertFalse(currUnit.isInside(new Vector(radius, 0, radius)));
        Assertions.assertTrue(currUnit.isInside(new Vector(0, 0, 0)));

        currUnit = voronoi.retrieveLayerUnit(upperLeft, 1);
        currBoundaries = currUnit.getBoundaries();
        Assertions.assertEquals(-radius, (int) currBoundaries.first.getX());
        Assertions.assertEquals(radius, (int) currBoundaries.first.getZ());
        Assertions.assertEquals(0, (int) currBoundaries.second.getX());
        Assertions.assertEquals(0, (int) currBoundaries.second.getZ());

        Assertions.assertFalse(currUnit.isInside(new Vector(0, 0, 0)));
        Assertions.assertTrue(currUnit.isInside(new Vector(-radius, 0, 0)));

        currUnit = voronoi.retrieveLayerUnit(lowerLeft, 1);
        currBoundaries = currUnit.getBoundaries();
        Assertions.assertEquals(-radius, (int) currBoundaries.first.getX());
        Assertions.assertEquals(0, (int) currBoundaries.first.getZ());
        Assertions.assertEquals(0, (int) currBoundaries.second.getX());
        Assertions.assertEquals(-radius, (int) currBoundaries.second.getZ());

        Assertions.assertFalse(currUnit.isInside(new Vector(0, 0, 0)));
        Assertions.assertTrue(currUnit.isInside(new Vector(-radius, 0, -radius)));

        currUnit = voronoi.retrieveLayerUnit(lowerRight, 1);
        currBoundaries = currUnit.getBoundaries();
        Assertions.assertEquals(0, (int) currBoundaries.first.getX());
        Assertions.assertEquals(0, (int) currBoundaries.first.getZ());
        Assertions.assertEquals(radius, (int) currBoundaries.second.getX());
        Assertions.assertEquals(-radius, (int) currBoundaries.second.getZ());

        Assertions.assertFalse(currUnit.isInside(new Vector(radius, 0, 0)));
        Assertions.assertTrue(currUnit.isInside(new Vector(0, 0, -radius)));
    }

    @Test
    void innerBoundariesTest() {
        
    }

    @Test
    void creationTest() {
        Assertions.assertEquals(0, voronoi.getFeatureCount());

        voronoi.addFeature(new VectorFeature(upperRight));

        Assertions.assertEquals(1, voronoi.getFeatureCount());

        voronoi.addFeature(new VectorFeature(upperLeft));
        voronoi.addFeature(new VectorFeature(upperLeft));
        voronoi.addFeature(new VectorFeature(lowerLeft));

        // Check for correct feature count
        Assertions.assertEquals(4, voronoi.getFeatureCount());

        VoronoiUnit<Vector, VectorFeature> upperRightChunk = voronoi.getChunk(upperRight);
        VoronoiUnit<Vector, VectorFeature> upperLeftChunk = voronoi.getChunk(upperLeft);
        VoronoiUnit<Vector, VectorFeature> lowerRightChunk = voronoi.getChunk(lowerRight);
        VoronoiUnit<Vector, VectorFeature> lowerLeftChunk = voronoi.getChunk(lowerLeft);

        // Check for correct depth
        int layerCount = upperRightChunk.getLayerCount();

        Assertions.assertEquals(17, layerCount);

        // check if the same chunks are returned.
        Assertions.assertSame(upperRightChunk, voronoi.retrieveLayerUnit(upperRight, layerCount));
        Assertions.assertSame(upperLeftChunk, voronoi.retrieveLayerUnit(upperLeft, layerCount));
        Assertions.assertSame(lowerRightChunk, voronoi.retrieveLayerUnit(lowerRight, layerCount));
        Assertions.assertSame(lowerLeftChunk, voronoi.retrieveLayerUnit(lowerLeft, layerCount));

        // check if no chunks are the same
        Assertions.assertNotSame(upperRightChunk, voronoi.retrieveLayerUnit(upperLeft, layerCount));
        Assertions.assertNotSame(upperLeftChunk, voronoi.retrieveLayerUnit(lowerRight, layerCount));
        Assertions.assertNotSame(lowerRightChunk, voronoi.retrieveLayerUnit(lowerLeft, layerCount));
        Assertions.assertNotSame(lowerLeftChunk, voronoi.retrieveLayerUnit(upperRight, layerCount));
    }

    @Test
    void locationTest() {
        ThreadLocalRandom current = ThreadLocalRandom.current();
        int features = 1000;

        List<VectorFeature> collect = IntStream.range(0, features)
                .mapToObj(i -> new VectorFeature(
                        new Vector(
                                current.nextInt(-1000000, 1000000),
                                current.nextInt(-1000000, 1000000),
                                current.nextInt(-1000000, 1000000))))
                .collect(Collectors.toList());


        Stopwatch stopwatch = new Stopwatch();

        stopwatch.start();
        collect.forEach(f -> voronoi.addFeature(f));
        stopwatch.printAndRestart("Insert");

        Assertions.assertEquals(features, voronoi.getFeatureCount());
        stopwatch.printAndRestart("Count");

        for (VectorFeature feature : collect) {
            Assertions.assertEquals(feature, voronoi.getClosestFeature(feature.getPos()).getFeature());
        }
        stopwatch.printAndRestart("Search first");

        for (VectorFeature feature : collect) {
            Assertions.assertEquals(feature, voronoi.getClosestFeature(feature.getPos()).getFeature());
        }
        stopwatch.printAndRestart("Search second");

        // 52
        // 289
        // 78
        // 54
    }

    @Test
    void benchmark() {
        ThreadLocalRandom current = ThreadLocalRandom.current();
        int features = 1000;
        int searchCount = 100;
        int range = 5000;

        System.out.printf("Searches: %d%n", searchCount);
        System.out.printf("Features: %d%n", features);
        System.out.printf("Range: %d%n", range);

        List<VectorFeature> featureList = IntStream.range(0, features)
                .mapToObj(i -> new VectorFeature(
                        new Vector(
                                current.nextInt(-range, range),
                                current.nextInt(-range, range),
                                current.nextInt(-range, range))))
                .collect(Collectors.toList());


        Stopwatch stopwatch = new Stopwatch();

        stopwatch.start();
        featureList.forEach(f -> voronoi.addFeature(f));
        stopwatch.printAndRestart("Insert");

        List<Vector> searches = IntStream.range(0, searchCount)
                .mapToObj(i ->
                        new Vector(
                                current.nextInt(-range, range),
                                current.nextInt(-range, range),
                                current.nextInt(-range, range)))
                .collect(Collectors.toList());

        stopwatch.start();
        for (Vector search : searches) {
            voronoi.getClosestFeature(search);
        }
        stopwatch.printAndRestart("Cold Searches");
        for (Vector search : searches) {
            voronoi.getClosestFeature(search);
        }
        stopwatch.printAndRestart("Hot Searches");
    }

    @Test
    void visual() {
        voronoi = Voronoi.create(new VoronoiSettings<>(1000000, new Vector(), 8), new VectorManhattanDimensionAdapter());

        ThreadLocalRandom current = ThreadLocalRandom.current();
        int features = 20;
        int range = 100;

        System.out.printf("Features: %d%n", features);
        System.out.printf("Range: %d%n", range);

        List<VectorFeature> featureList = IntStream.range(0, features)
                .mapToObj(i -> new NamedFeature(
                        new Vector(
                                current.nextInt(-range, range),
                                current.nextInt(-range, range),
                                current.nextInt(-range, range)),
                        i))
                .collect(Collectors.toList());

        Stopwatch stopwatch = new Stopwatch();

        stopwatch.start();
        featureList.forEach(f -> voronoi.addFeature(f));
        stopwatch.printAndRestart("Insert");

        List<List<String>> layout = new ArrayList<>();

        int line = 0;

        for (int x = -1 * range; x < range; x++) {
            layout.add(line, new ArrayList<>());
            for (int z = -1 * range; z < range; z++) {
                WeightedFeature<Vector, VectorFeature> closestFeature = voronoi.getClosestFeature(new Vector(x, 0, z));
                List<String> currLine = layout.get(line);
                currLine.add(((NamedFeature) closestFeature.getFeature()).getName());
            }
            line++;
        }

        String field = layout.stream().map(s -> String.join(" ", s)).collect(Collectors.joining("\n"));
        System.out.println(field);
    }

    void testNeighbour() {

    }

    private static class NamedFeature extends VectorFeature {

        private final String name;

        public NamedFeature(Vector pos, int num) {
            super(pos);
            name = String.format("%02X", num);
        }

        public String getName() {
            return name;
        }
    }
}