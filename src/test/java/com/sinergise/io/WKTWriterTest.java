package com.sinergise.io;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.sinergise.geometry.Geometry;
import com.sinergise.geometry.GeometryCollection;
import com.sinergise.geometry.LineString;
import com.sinergise.geometry.MultiLineString;
import com.sinergise.geometry.MultiPoint;
import com.sinergise.geometry.MultiPolygon;
import com.sinergise.geometry.Point;
import com.sinergise.geometry.Polygon;
import org.junit.jupiter.api.Test;

class WKTWriterTest {

  private final WKTWriter writer = new WKTWriter();

  @Test
  void validLineStringTest() {
    Geometry lineString = new LineString(new double[]{30, 10, 10, 30, 40, 40});

    String wkt = writer.write(lineString);

    assertEquals("LINESTRING (30.0 10.0, 10.0 30.0, 40.0 40.0)", wkt);
  }

  @Test
  void invalidMissingYCoordinateLineStringTest() {
    LineString lineString = new LineString(new double[]{30});

    String wkt = writer.write(lineString);

    assertEquals("LINESTRING EMPTY", wkt);
  }

  @Test
  void emptyLineString() {
    LineString lineString = new LineString();

    String wkt = writer.write(lineString);

    assertEquals("LINESTRING EMPTY", wkt);
  }

  @Test
  void emptyPoint() {
    Point point = new Point();

    String wkt = writer.write(point);

    assertEquals("POINT EMPTY", wkt);
  }

  @Test
  void validPoint() {
    Point point = new Point(1, 5);

    String wkt = writer.write(point);

    assertEquals("POINT (1.0 5.0)", wkt);
  }

  @Test
  void validMultiLineString() {
    Geometry multiLineString = new MultiLineString(new LineString[]{
        new LineString(new double[]{10, 10, 20, 20, 10, 40}),
        new LineString(new double[]{40, 40, 30, 30, 40, 20, 30, 10})
    });

    String wkt = writer.write(multiLineString);
    assertEquals("MULTILINESTRING ((10.0 10.0, 20.0 20.0, 10.0 40.0),"
        + " (40.0 40.0, 30.0 30.0, 40.0 20.0, 30.0 10.0))", wkt);
  }

  @Test
  void emptyMultiLineString() {
    MultiLineString point = new MultiLineString(new LineString[]{});

    String wkt = writer.write(point);
    assertEquals("MULTILINESTRING EMPTY", wkt);
  }


  @Test
  void validPolygon() {
    Polygon point = new Polygon(
        new LineString(new double[]{35, 10, 45, 45, 15, 40, 10, 20, 35, 10}), new LineString[]{
        new LineString(new double[]{20, 30, 35, 35, 30, 20, 20, 30})
    });

    String wkt = writer.write(point);
    assertEquals("POLYGON ((35.0 10.0, 45.0 45.0, 15.0 40.0, 10.0 20.0, 35.0 10.0),"
        + " (20.0 30.0, 35.0 35.0, 30.0 20.0, 20.0 30.0))", wkt);
  }


  @Test
  void emptyPolygon() {
    Polygon polygon = new Polygon();

    String wkt = writer.write(polygon);
    assertEquals("POLYGON EMPTY", wkt);
  }


  @Test
  void emptyMultiPolygon() {
    MultiPolygon polygon = new MultiPolygon();

    String wkt = writer.write(polygon);
    assertEquals("MULTIPOLYGON EMPTY", wkt);
  }

  @Test
  void validMultiPolygon() {
    MultiPolygon polygon = new MultiPolygon(new Polygon[]{
        new Polygon(new LineString(new double[]{35, 10, 45, 45, 15, 40, 10, 20, 35, 10}),
            new LineString[]{
                new LineString(new double[]{20, 30, 35, 35, 30, 20, 20, 30})
            }),
        new Polygon(new LineString(new double[]{35, 10, 45, 45, 15, 40, 10, 20, 35, 10}),
            new LineString[]{
                new LineString(new double[]{20, 30, 35, 35, 30, 20, 20, 30})
            })
    });

    String wkt = writer.write(polygon);

    assertEquals(
        "MULTIPOLYGON (((35.0 10.0, 45.0 45.0, 15.0 40.0, 10.0 20.0, 35.0 10.0), (20.0 30.0, 35.0 35.0, 30.0 20.0, 20.0 30.0)), ((35.0 10.0, 45.0 45.0, 15.0 40.0, 10.0 20.0, 35.0 10.0), (20.0 30.0, 35.0 35.0, 30.0 20.0, 20.0 30.0)))",
        wkt);
  }


  @Test
  void emptyMultiPoint() {
    MultiPoint multiPoint = new MultiPoint();

    String wkt = writer.write(multiPoint);
    assertEquals("MULTIPOINT EMPTY", wkt);
  }


  @Test
  void validMultiPoint() {
    MultiPoint multiPoint = new MultiPoint(
        new Point[]{
            new Point(5, 10),
            new Point(100, 150)
        }
    );

    String wkt = writer.write(multiPoint);
    assertEquals("MULTIPOINT ((5.0 10.0), (100.0 150.0))", wkt);
  }

  @Test
  void geometryCollectionTest() {
    GeometryCollection<Geometry> geometries = new GeometryCollection<>(
        new Geometry[]{
            new Point(4, 6),
            new LineString(new double[]{4, 6, 7, 10})
        });

    String wkt = writer.write(geometries);

    assertEquals("GEOMETRYCOLLECTION (POINT (4.0 6.0), LINESTRING (4.0 6.0, 7.0 10.0))", wkt);
  }

}