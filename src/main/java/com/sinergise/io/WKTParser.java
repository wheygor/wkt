package com.sinergise.io;

import static com.sinergise.io.WKTTokenType.COMMA;
import static com.sinergise.io.WKTTokenType.EMPTY;
import static com.sinergise.io.WKTTokenType.GEOMETRYCOLLECTION;
import static com.sinergise.io.WKTTokenType.LEFT_PARENTHESES;
import static com.sinergise.io.WKTTokenType.LINESTRING;
import static com.sinergise.io.WKTTokenType.MULTILINESTRING;
import static com.sinergise.io.WKTTokenType.MULTIPOINT;
import static com.sinergise.io.WKTTokenType.MULTIPOLYGON;
import static com.sinergise.io.WKTTokenType.POINT;
import static com.sinergise.io.WKTTokenType.POLYGON;
import static com.sinergise.io.WKTTokenType.RIGHT_PARENTHESES;
import static com.sinergise.io.WKTTokenType.WHITESPACE;

import com.sinergise.geometry.Geometry;
import com.sinergise.geometry.GeometryCollection;
import com.sinergise.geometry.LineString;
import com.sinergise.geometry.MultiLineString;
import com.sinergise.geometry.MultiPoint;
import com.sinergise.geometry.MultiPolygon;
import com.sinergise.geometry.Point;
import com.sinergise.geometry.Polygon;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;
import java.util.function.Supplier;
import java.util.stream.DoubleStream;

public class WKTParser {

  private static final int POINT_TOKEN_COUNT = 3;

  private final Queue<WKTToken> tokens;

  public WKTParser(final String WKT) {
    this.tokens = WKTLexer.tokenize(WKT);
  }

  public Geometry parse() {
    Geometry geometry = parseGeometryTaggedText();
    if (!tokens.isEmpty()) {
      throw new WKTParseException("Invalid WKT string. Unconsumed tokens remain.");
    }
    return geometry;
  }

  private Geometry parseGeometryTaggedText() {
    if (tokens.isEmpty()) {
      throw new WKTParseException("Invalid WKT string. No tokens provided.");
    }

    WKTToken nextToken = tokens.peek();

    return switch (nextToken.type()) {
      case POINT -> parseGeometryTaggedText(POINT, this::parsePointText);
      case MULTIPOINT -> parseGeometryTaggedText(MULTIPOINT, this::parseMultiPointText);
      case LINESTRING -> parseGeometryTaggedText(LINESTRING, this::parseLineStringText);
      case MULTILINESTRING ->
          parseGeometryTaggedText(MULTILINESTRING, this::parseMultiLineStringText);
      case POLYGON -> parseGeometryTaggedText(POLYGON, this::parsePolygonText);
      case MULTIPOLYGON -> parseGeometryTaggedText(MULTIPOLYGON, this::parseMultiPolygonText);
      case GEOMETRYCOLLECTION ->
          parseGeometryTaggedText(GEOMETRYCOLLECTION, this::parseGeometryCollectionText);
      case WHITESPACE, COMMA, EMPTY, LEFT_PARENTHESES,
          RIGHT_PARENTHESES, NUMBER -> throw new WKTParseException(
          "Unexpected token. Expected a token of type geometry.");
    };
  }

  private Geometry parseGeometryTaggedText(WKTTokenType tokenType,
      Supplier<Geometry> geometrySupplier) {
    consumeNextTokenOfType(tokenType);
    consumeNextTokenOfTypeIfExists(WHITESPACE);
    return geometrySupplier.get();
  }

  private Point parsePointText() {
    if (checkAndRemoveNextTokenIfTypeEmpty()) {
      return new Point();
    }

    consumeNextTokenOfType(LEFT_PARENTHESES);
    Point point = parsePoint();
    consumeNextTokenOfType(RIGHT_PARENTHESES);

    return point;
  }

  private Point parsePoint() {
    if (tokens.isEmpty() || tokens.size() < POINT_TOKEN_COUNT) {
      throw new WKTParseException("Invalid Point WKT string. Missing or malformed coordinates.");
    }

    double coordinateX = Double.parseDouble(tokens.poll().value());
    consumeNextTokenOfType(WHITESPACE);
    double coordinateY = Double.parseDouble(tokens.poll().value());

    return new Point(coordinateX, coordinateY);
  }

  private MultiPoint parseMultiPointText() {
    if (checkAndRemoveNextTokenIfTypeEmpty()) {
      return new MultiPoint();
    }

    consumeNextTokenOfType(LEFT_PARENTHESES);

    List<Point> points = new ArrayList<>(2);

    while (!checkNextTokenIsRightParentheses()) {
      Point point = parsePointText();

      points.add(point);

      consumeNextTokenOfTypeIfExists(COMMA);
      consumeNextTokenOfTypeIfExists(WHITESPACE);
    }

    consumeNextTokenOfType(RIGHT_PARENTHESES);

    return new MultiPoint(points.toArray(new Point[0]));
  }

  private LineString parseLineStringText() {
    if (checkAndRemoveNextTokenIfTypeEmpty()) {
      return new LineString();
    }

    consumeNextTokenOfType(LEFT_PARENTHESES);

    List<Point> points = new ArrayList<>(2);
    while (!checkNextTokenIsRightParentheses()) {
      Point point = parsePoint();

      points.add(point);

      consumeNextTokenOfTypeIfExists(COMMA);
      consumeNextTokenOfTypeIfExists(WHITESPACE);
    }

    consumeNextTokenOfType(RIGHT_PARENTHESES);

    double[] coordinates = points.stream()
        .flatMapToDouble(point -> DoubleStream.concat(
            DoubleStream.of(point.getX()),
            DoubleStream.of(point.getY())))
        .toArray();

    return new LineString(coordinates);
  }

  private MultiLineString parseMultiLineStringText() {
    if (checkAndRemoveNextTokenIfTypeEmpty()) {
      return new MultiLineString();
    }

    consumeNextTokenOfType(LEFT_PARENTHESES);

    List<LineString> lineStrings = new ArrayList<>(2);
    while (!checkNextTokenIsRightParentheses()) {
      LineString point = parseLineStringText();

      lineStrings.add(point);

      consumeNextTokenOfTypeIfExists(COMMA);
      consumeNextTokenOfTypeIfExists(WHITESPACE);
    }

    consumeNextTokenOfType(RIGHT_PARENTHESES);

    return new MultiLineString(lineStrings.toArray(new LineString[0]));
  }

  private Polygon parsePolygonText() {
    if (checkAndRemoveNextTokenIfTypeEmpty()) {
      return new Polygon();
    }

    consumeNextTokenOfType(LEFT_PARENTHESES);

    List<LineString> lineStrings = new ArrayList<>(2);
    while (!checkNextTokenIsRightParentheses()) {
      LineString point = parseLineStringText();

      lineStrings.add(point);

      consumeNextTokenOfTypeIfExists(COMMA);
      consumeNextTokenOfTypeIfExists(WHITESPACE);
    }

    consumeNextTokenOfType(RIGHT_PARENTHESES);

    LineString lineString = lineStrings.remove(0);
    return new Polygon(lineString, lineStrings.toArray(new LineString[0]));
  }

  private MultiPolygon parseMultiPolygonText() {
    if (checkAndRemoveNextTokenIfTypeEmpty()) {
      return new MultiPolygon();
    }

    consumeNextTokenOfType(LEFT_PARENTHESES);

    List<Polygon> polygons = new ArrayList<>(2);

    while (!checkNextTokenIsRightParentheses()) {
      Polygon polygon = parsePolygonText();

      polygons.add(polygon);

      consumeNextTokenOfTypeIfExists(COMMA);
      consumeNextTokenOfTypeIfExists(WHITESPACE);
    }

    consumeNextTokenOfType(RIGHT_PARENTHESES);

    return new MultiPolygon(polygons.toArray(new Polygon[0]));
  }

  private GeometryCollection<?> parseGeometryCollectionText() {
    if (checkAndRemoveNextTokenIfTypeEmpty()) {
      return new GeometryCollection<>();
    }

    consumeNextTokenOfType(LEFT_PARENTHESES);

    List<Geometry> geometries = new ArrayList<>(2);

    while (!checkNextTokenIsRightParentheses()) {
      Geometry geometry = parseGeometryTaggedText();
      geometries.add(geometry);

      consumeNextTokenOfTypeIfExists(COMMA);
      consumeNextTokenOfTypeIfExists(WHITESPACE);
    }

    consumeNextTokenOfType(RIGHT_PARENTHESES);

    return new GeometryCollection<>(geometries);
  }

  private boolean checkNextTokenIsRightParentheses() {
    if (tokens.isEmpty()) {
      return false;
    }

    return RIGHT_PARENTHESES.equals(tokens.peek().type());
  }

  private boolean checkAndRemoveNextTokenIfTypeEmpty() {
    if (!tokens.isEmpty() && EMPTY.equals(tokens.peek().type())) {
      tokens.remove();
      return true;
    }
    return false;
  }

  private void consumeNextTokenOfType(WKTTokenType tokenTypeToConsume) {
    if (tokens.isEmpty()) {
      throw new WKTParseException(
          String.format("Cannot consume token of tokenTypeToConsume '%s'. Token queue empty.",
              tokenTypeToConsume));
    }

    WKTToken token = tokens.poll();

    if (!tokenTypeToConsume.equals(token.type())) {
      throw new WKTParseException(String.format("Expected token of type '%s'. Got '%s' instead.",
          tokenTypeToConsume, token.type()));
    }
  }

  private void consumeNextTokenOfTypeIfExists(WKTTokenType tokenTypeToConsume) {
    if (tokens.isEmpty()) {
      return;
    }

    if (tokens.peek().type().equals(tokenTypeToConsume)) {
      tokens.remove();
    }
  }
}
