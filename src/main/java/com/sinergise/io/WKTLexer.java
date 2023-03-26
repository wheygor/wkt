package com.sinergise.io;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WKTLexer {

  public static Queue<WKTToken> tokenize(final String WKT) {
    Queue<WKTToken> tokens = new LinkedList<>();
    int position = 0;

    while (position < WKT.length()) {
      WKTToken token = creatToken(WKT, position);
      position = token.endIndex();
      tokens.add(token);
    }

    if (position != WKT.length()) {
      throw new IllegalStateException("Unexpected error encountered!. "
          + "Final position should be at the end of the WKT string.");
    }

    return tokens;
  }

  private static WKTToken creatToken(final String WKT, final int position) {
    if (position > WKT.length() || position < 0) {
      throw new TokenizationException(
          "Error occurred during tokenization. Position argument is out of bounds.");
    }

    List<WKTTokenType> wktTokenTypeList = List.of(WKTTokenType.values());

    for (WKTTokenType tokenType : wktTokenTypeList) {
      Pattern tokenPattern = createPatternForTokenTypeAtPosition(tokenType, position);
      Matcher tokenMatcher = tokenPattern.matcher(WKT);

      if (tokenMatcher.matches()) {
        String match = tokenMatcher.group(1);
        return new WKTToken(tokenType, match, position + match.length());
      }
    }
    throw new TokenizationException(
        String.format("Invalid WKT string '%s'. No token matched!", WKT));
  }

  private static Pattern createPatternForTokenTypeAtPosition(WKTTokenType tokenType, int position) {
    return Pattern.compile(".{" + position + "}" + tokenType.getPattern() + ".*",
        Pattern.CASE_INSENSITIVE);
  }
}
