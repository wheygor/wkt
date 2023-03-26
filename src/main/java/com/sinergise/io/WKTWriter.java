package com.sinergise.io;

import com.sinergise.geometry.Geometry;
import com.sinergise.geometry.GeometryCollection;
import com.sinergise.geometry.LineString;
import com.sinergise.geometry.MultiLineString;
import com.sinergise.geometry.MultiPoint;
import com.sinergise.geometry.MultiPolygon;
import com.sinergise.geometry.Point;
import com.sinergise.geometry.Polygon;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.stream.Stream;

public class WKTWriter {

  private static final String COORDINATE_STRING_FORMAT = "%.1f %.1f";
  private static final String EMPTY_GEOMETRY = "EMPTY";

  private static final String STARTING_PARENTHESES = "(";
  private static final String ENDING_PARENTHESES = ")";
  private static final String COMMA = ",";
  private static final String WHITESPACE = " ";

  private final Writer writer = new StringWriter();

  /**
   * Transforms the input Geometry object into WKT-formatted String. e.g.
   * <pre><code>
   * new WKTWriter().write(new LineString(new double[]{30, 10, 10, 30, 40, 40}));
   * //returns "LINESTRING (30 10, 10 30, 40 40)"
   * </code></pre>
   */
  public String write(final Geometry geometry) {
    try (writer) {
      writeGeometryTaggedText(geometry);
      return writer.toString();
    } catch (IOException e) {
      throw new WKTWriteException("IOException occurred during WKT write.", e);
    }
  }

  private void writeGeometryTaggedText(Geometry geometry) throws IOException {
    WKTGeometryType geometryType = WKTGeometryType.from(geometry);

    writeGeometryTag(geometryType);
    writeGeometryText(geometry, geometryType);
  }

  private void writeGeometryText(Geometry geometry, WKTGeometryType geometryType)
      throws IOException {
    if (geometry.isEmpty()) {
      writeEmptyGeometry(writer);
      return;
    }

    switch (geometryType) {
      case POINT -> writePointText((Point) geometry);
      case LINESTRING -> writeLineStringText((LineString) geometry);
      case POLYGON -> writePolygonText((Polygon) geometry);
      case GEOMETRY_COLLECTION, MULTIPOINT, MULTILINESTRING, MULTIPOLYGON ->
          writeGeometryCollectionText((GeometryCollection<?>) geometry);
    }
  }

  private void writePolygonText(Polygon polygon) throws IOException {
    writeStartingParentheses(writer);

    LineString outerLineString = polygon.getOuter();
    writeLineStringText(outerLineString);

    writeSeparatorIfNeeded(true);

    for (int i = 0; i < polygon.getNumHoles(); i++) {
      writeSeparatorIfNeeded(i > 0);
      writeLineStringText(polygon.getHole(i));
    }

    writeEndingParentheses(writer);
  }

  private void writeGeometryCollectionText(GeometryCollection<?> geometryCollection)
      throws IOException {
    writeStartingParentheses(writer);

    boolean isInstanceOfGeometryCollection = geometryCollection.getClass()
        .isAssignableFrom(GeometryCollection.class);

    for (int i = 0; i < geometryCollection.size(); i++) {
      writeSeparatorIfNeeded(i > 0);

      Geometry geometry = geometryCollection.get(i);
      if (isInstanceOfGeometryCollection) {
        writeGeometryTaggedText(geometry);
      } else {
        writeGeometryText(geometry, WKTGeometryType.from(geometry));
      }
    }

    writeEndingParentheses(writer);
  }

  private void writeLineStringText(LineString lineString) throws IOException {
    if (lineString.getNumCoords() == 0) {
      writeEmptyGeometry(writer);
      return;
    }

    writeStartingParentheses(writer);

    for (int i = 0; i < lineString.getNumCoords(); i++) {
      writeSeparatorIfNeeded(i > 0);
      writePoint(lineString.getX(i), lineString.getY(i));
    }

    writeEndingParentheses(writer);
  }

  private void writePointText(Point point) throws IOException {
    writeStartingParentheses(writer);
    writePoint(point);
    writeEndingParentheses(writer);
  }

  private void writePoint(Point point) throws IOException {
    writePoint(point.getX(), point.getY());
  }

  private void writePoint(double x, double y) throws IOException {
    String coordinates = String.format(COORDINATE_STRING_FORMAT, x, y);
    writer.write(coordinates);
  }

  private void writeSeparatorIfNeeded(boolean separatorNeeded) throws IOException {
    if (separatorNeeded) {
      writeComma(writer);
      writeWhitespace(writer);
    }
  }

  private void writeWhitespace(Writer writer) throws IOException {
    writer.write(WHITESPACE);
  }

  private void writeComma(Writer writer) throws IOException {
    writer.write(COMMA);
  }

  private void writeEndingParentheses(Writer writer) throws IOException {
    writer.write(ENDING_PARENTHESES);
  }

  private void writeStartingParentheses(Writer writer) throws IOException {
    writer.write(STARTING_PARENTHESES);
  }

  private void writeEmptyGeometry(Writer writer) throws IOException {
    writer.write(EMPTY_GEOMETRY);
  }

  private void writeGeometryTag(WKTGeometryType geometryType) throws IOException {
    writer.write(geometryType.getWKTTag());
    writer.write(WHITESPACE);
  }

  public enum WKTGeometryType {
    POINT("POINT", Point.class),
    MULTIPOINT("MULTIPOINT", MultiPoint.class),
    LINESTRING("LINESTRING", LineString.class),
    MULTILINESTRING("MULTILINESTRING", MultiLineString.class),
    POLYGON("POLYGON", Polygon.class),
    MULTIPOLYGON("MULTIPOLYGON", MultiPolygon.class),
    GEOMETRY_COLLECTION("GEOMETRYCOLLECTION", GeometryCollection.class);

    private final String WKTTag;
    private final Class<? extends Geometry> clazz;

    WKTGeometryType(String WKTTag, Class<? extends Geometry> clazz) {
      this.WKTTag = WKTTag;
      this.clazz = clazz;
    }

    private static WKTGeometryType from(Geometry geometry) {
      return Stream.of(WKTGeometryType.values())
          .filter(geometryType -> geometryType.clazz.equals(geometry.getClass()))
          .findFirst()
          .orElseThrow(() -> new IllegalStateException(
              "Unexpected geometry class: " + geometry.getClass().getSimpleName()));
    }

    public String getWKTTag() {
      return WKTTag;
    }
  }
}
