package com.sinergise.io;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sinergise.geometry.Geometry;
import com.sinergise.geometry.GeometryCollection;
import com.sinergise.geometry.LineString;
import com.sinergise.geometry.MultiLineString;
import com.sinergise.geometry.MultiPoint;
import com.sinergise.geometry.MultiPolygon;
import com.sinergise.geometry.Point;
import com.sinergise.geometry.Polygon;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class WKTReaderTest {

  private final WKTReader reader = new WKTReader();

  @Test
  public void testEmptyPoint() {
    String WKT = "POINT EMPTY";
    Geometry geometry = reader.read(WKT);

    assertTrue(geometry instanceof Point);

    Point point = (Point) geometry;
    assertTrue(point.isEmpty());
  }

  @Test
  public void testPoint() {
    String WKT = "POINT (4 -6)";
    Geometry geometry = reader.read(WKT);

    assertTrue(geometry instanceof Point);

    Point point = (Point) geometry;
    Assertions.assertFalse(point.isEmpty());
    assertEquals(4, point.getX());
    assertEquals(-6, point.getY());
  }

  @Test
  public void testEmptyMultiPoint() {
    String WKT = "MULTIPOINT EMPTY";
    Geometry geometry = reader.read(WKT);

    assertTrue(geometry instanceof MultiPoint);

    MultiPoint multiPoint = (MultiPoint) geometry;
    assertTrue(multiPoint.isEmpty());
  }

  @Test
  public void testMultiPoint() {
    String WKT = "MULTIPOINT ((5.0 10.0), (100.0 150.0))";
    Geometry geometry = reader.read(WKT);

    assertTrue(geometry instanceof MultiPoint);

    MultiPoint multiPoint = (MultiPoint) geometry;
    Assertions.assertFalse(multiPoint.isEmpty());

    Point point1 = multiPoint.get(0);
    Point point2 = multiPoint.get(1);

    assertEquals(5.0d, point1.getX());
    assertEquals(10.0d, point1.getY());

    assertEquals(100.0d, point2.getX());
    assertEquals(150.0d, point2.getY());
  }

  @Test
  public void testEmptyLineString() {
    String WKT = "LINESTRING EMPTY";
    Geometry geometry = reader.read(WKT);

    assertTrue(geometry instanceof LineString);

    LineString lineString = (LineString) geometry;
    assertTrue(lineString.isEmpty());
  }

  @Test
  public void testLineString() {
    String WKT = "LINESTRING (30.0 10.0, 10.0 30.0, 40.0 40.0)";
    Geometry geometry = reader.read(WKT);

    assertTrue(geometry instanceof LineString);

    LineString lineString = (LineString) geometry;
    Assertions.assertFalse(lineString.isEmpty());

    assertEquals(30.0d, lineString.getX(0));
    assertEquals(10.0d, lineString.getY(0));
    assertEquals(10.0d, lineString.getX(1));
    assertEquals(30.0d, lineString.getY(1));
    assertEquals(40.0d, lineString.getX(2));
    assertEquals(40.0d, lineString.getY(2));
  }

  @Test
  public void testEmptyMultiLineString() {
    String WKT = "MultiLineString EMPTY";
    Geometry geometry = reader.read(WKT);

    assertTrue(geometry instanceof MultiLineString);

    MultiLineString multiLineString = (MultiLineString) geometry;
    assertTrue(multiLineString.isEmpty());
  }

  @Test
  public void testMultiLineString() {
    String WKT = "MultiLineString ((10 10, 20 20), (15 15, 30 15))";
    Geometry geometry = reader.read(WKT);

    assertTrue(geometry instanceof MultiLineString);

    MultiLineString multiLineString = (MultiLineString) geometry;
    Assertions.assertFalse(multiLineString.isEmpty());

    LineString lineString = multiLineString.get(0);
    LineString lineString2 = multiLineString.get(1);

    assertEquals(10.0d, lineString.getX(0));
    assertEquals(10.0d, lineString.getY(0));
    assertEquals(20.0d, lineString.getY(1));
    assertEquals(20.0d, lineString.getY(1));
    assertEquals(15.0d, lineString2.getX(0));
    assertEquals(15.0d, lineString2.getY(0));
    assertEquals(30.0d, lineString2.getX(1));
    assertEquals(15.0d, lineString2.getY(1));
  }


  @Test
  public void testEmptyPolygon() {
    String WKT = "POLYGON EMPTY";
    Geometry geometry = reader.read(WKT);

    assertTrue(geometry instanceof Polygon);

    Polygon polygon = (Polygon) geometry;
    assertTrue(polygon.isEmpty());
  }

  @Test
  public void testPolygon() {
    String WKT = "POLYGON ((0.5 0.5,5 0,5 5,0 5,0.5 0.5), (1.5 1,4 3,4 1,1.5 1))";
    Geometry geometry = reader.read(WKT);

    assertTrue(geometry instanceof Polygon);

    Polygon polygon = (Polygon) geometry;
    Assertions.assertFalse(polygon.isEmpty());

    LineString outerRing = polygon.getOuter();
    assertEquals(0.5d, outerRing.getX(0));
    assertEquals(0.5d, outerRing.getY(0));
    assertEquals(5d, outerRing.getX(1));
    assertEquals(0d, outerRing.getY(1));
    assertEquals(5d, outerRing.getX(2));
    assertEquals(5d, outerRing.getY(2));
    assertEquals(0d, outerRing.getX(3));
    assertEquals(5d, outerRing.getY(3));
    assertEquals(0.5d, outerRing.getX(4));
    assertEquals(0.5d, outerRing.getX(4));

    LineString exteriorRing = polygon.getHole(0);
    assertEquals(1.5d, exteriorRing.getX(0));
    assertEquals(1d, exteriorRing.getY(0));
    assertEquals(4d, exteriorRing.getX(1));
    assertEquals(3d, exteriorRing.getY(1));
    assertEquals(4d, exteriorRing.getX(2));
    assertEquals(1d, exteriorRing.getY(2));
    assertEquals(1.5d, exteriorRing.getX(3));
    assertEquals(1d, exteriorRing.getY(3));
  }

  @Test
  public void testEmptyMultiPolygon() {
    String WKT = "MULTIPOLYGON EMPTY";
    Geometry geometry = reader.read(WKT);

    assertTrue(geometry instanceof MultiPolygon);

    MultiPolygon multiPolygon = (MultiPolygon) geometry;
    assertTrue(multiPolygon.isEmpty());
  }

  @Test
  public void testMultiPolygon() {
    String WKT = "MULTIPOLYGON(((0 1,3 0,4 3,0 4,0 1)), ((3.66 4.44,6 3,555.15 -551.09,3.66 4.44)), ((0 0,-1 -2,-3 -2,-2 -1,0 0)))";
    Geometry geometry = reader.read(WKT);

    assertTrue(geometry instanceof MultiPolygon);

    MultiPolygon multiPolygon = (MultiPolygon) geometry;
    Assertions.assertFalse(multiPolygon.isEmpty());

    Polygon polygon = multiPolygon.get(0);
    Polygon polygon2 = multiPolygon.get(1);
    Polygon polygon3 = multiPolygon.get(2);

    LineString polygonOuterRing = polygon.getOuter();
    LineString polygon2OuterRing = polygon2.getOuter();
    LineString polygon3OuterRing = polygon3.getOuter();

    assertEquals(0d, polygonOuterRing.getX(0));
    assertEquals(1d, polygonOuterRing.getY(0));
    assertEquals(3d, polygonOuterRing.getX(1));
    assertEquals(0d, polygonOuterRing.getY(1));
    assertEquals(4d, polygonOuterRing.getX(2));
    assertEquals(3d, polygonOuterRing.getY(2));
    assertEquals(0d, polygonOuterRing.getX(3));
    assertEquals(4d, polygonOuterRing.getY(3));
    assertEquals(0d, polygonOuterRing.getX(4));
    assertEquals(1d, polygonOuterRing.getY(4));

    assertEquals(3.66d, polygon2OuterRing.getX(0));
    assertEquals(4.44d, polygon2OuterRing.getY(0));
    assertEquals(6d, polygon2OuterRing.getX(1));
    assertEquals(3d, polygon2OuterRing.getY(1));
    assertEquals(555.15d, polygon2OuterRing.getX(2));
    assertEquals(-551.09d, polygon2OuterRing.getY(2));
    assertEquals(3.66d, polygon2OuterRing.getX(3));
    assertEquals(4.44d, polygon2OuterRing.getY(3));

    assertEquals(0d, polygon3OuterRing.getX(0));
    assertEquals(0d, polygon3OuterRing.getY(0));
    assertEquals(-1d, polygon3OuterRing.getX(1));
    assertEquals(-2d, polygon3OuterRing.getY(1));
    assertEquals(-3d, polygon3OuterRing.getX(2));
    assertEquals(-2d, polygon3OuterRing.getY(2));
    assertEquals(-2d, polygon3OuterRing.getX(3));
    assertEquals(-1d, polygon3OuterRing.getY(3));
    assertEquals(0d, polygon3OuterRing.getX(4));
    assertEquals(0d, polygon3OuterRing.getY(4));
  }

  @Test
  public void testGeometryCollection() {
    String WKT = "GEOMETRYCOLLECTION (POINT (-4 -6), LINESTRING(4 6, 7 10))";

    Geometry geometry = reader.read(WKT);

    assertTrue(geometry instanceof GeometryCollection<?>);

    GeometryCollection<?> geometryCollection = (GeometryCollection<?>) geometry;

    Geometry geometry1 = geometryCollection.get(0);
    Geometry geometry2 = geometryCollection.get(1);

    assertTrue(geometry1 instanceof Point);
    assertTrue(geometry2 instanceof LineString);

    Point point = (Point) geometry1;
    LineString lineString = (LineString) geometry2;

    assertEquals(-4d, point.getX());
    assertEquals(-6d, point.getY());

    assertEquals(4, lineString.getX(0));
    assertEquals(6, lineString.getY(0));
    assertEquals(7, lineString.getX(1));
    assertEquals(10, lineString.getY(1));
  }
}