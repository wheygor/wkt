package com.sinergise.io;

import com.sinergise.geometry.Geometry;

public class WKTReader {

  /**
   * Transforms the input WKT-formatted String into Geometry object
   */
  public Geometry read(String wktString) {
    WKTParser parser = new WKTParser(wktString);
    return parser.parse();
  }

}
