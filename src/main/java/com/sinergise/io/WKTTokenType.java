package com.sinergise.io;

public enum WKTTokenType {
  POINT("\\b(POINT)\\b"),
  MULTIPOINT("\\b(MULTIPOINT)\\b"),
  LINESTRING("\\b(LINESTRING)\\b"),
  MULTILINESTRING("\\b(MULTILINESTRING)\\b"),
  POLYGON("\\b(POLYGON)\\b"),
  MULTIPOLYGON("\\b(MULTIPOLYGON)\\b"),
  GEOMETRYCOLLECTION("\\b(GEOMETRYCOLLECTION)\\b"),
  EMPTY("\\b(EMPTY)\\b"),
  LEFT_PARENTHESES("(\\()"),
  RIGHT_PARENTHESES("(\\))"),
  COMMA("(,)"),
  NUMBER("(-?\\d+(\\.\\d+)?)"),
  WHITESPACE("(\\s)");

  private final String pattern;

  WKTTokenType(String pattern) {
    this.pattern = pattern;
  }

  public String getPattern() {
    return pattern;
  }
}
