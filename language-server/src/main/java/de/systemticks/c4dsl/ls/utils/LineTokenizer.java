package de.systemticks.c4dsl.ls.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.AllArgsConstructor;
import lombok.Data;

public class LineTokenizer {
    
    private final static String TOKENIZE_PATTERN = "\\w+|\"([^\"]*)\"|->|=|\\{";
    private final static Pattern pattern = Pattern.compile(TOKENIZE_PATTERN);

    public List<LineToken> tokenize(String line) {

        List<LineToken> result = new ArrayList<>();
        if(C4Utils.isBlank(line)) {
            return result;
        }

        Matcher matcher = pattern.matcher(line);

        while(matcher.find()) {
            result.add(new LineToken(matcher.group(0), matcher.start(), matcher.end()));
        }

        return result;
    }

    public CursorLocation cursorLocation(List<LineToken> tokens, int charAt) {

        if(tokens == null || tokens.size() == 0) {
            return new CursorLocation(-1, TokenPosition.NOT_APPLICABLE);
        }

        for(int i=0; i<tokens.size(); i++) {
            LineToken t = tokens.get(i);
            if(cursorBeforeToken(t, charAt)) {
                return new CursorLocation(i, TokenPosition.BEFORE);
            }
            if(cursorInsideToken(t, charAt)) {
                return new CursorLocation(i, TokenPosition.INSIDE);
            }
        }

        return new CursorLocation(tokens.size()-1, TokenPosition.AFTER);

    }

    boolean cursorInsideToken(LineToken token, int charAt) {
        return (charAt > token.getStart() && charAt <= token.getEnd());
    }

    boolean cursorAfterToken(LineToken token, int charAt) {
        return charAt > token.getEnd();
    }

    boolean cursorBeforeToken(LineToken token, int charAt) {
        return charAt <= token.getStart();
    }

    public boolean isBetweenTokens(CursorLocation cursor, int indexFrom, int indexTo) {
        return (cursor.getTokenIndex() == indexFrom && cursor.getTokenPosition().equals(TokenPosition.AFTER)) || 
               (cursor.getTokenIndex() == indexTo && cursor.getTokenPosition().equals(TokenPosition.BEFORE)) ;
    }

    public boolean isInsideToken(CursorLocation cursor, int index) {
        return cursor.getTokenIndex() == index && cursor.getTokenPosition().equals(TokenPosition.INSIDE);  
    }

    public boolean isBeforeToken(CursorLocation cursor, int index) {
        return cursor.getTokenIndex() == index && cursor.getTokenPosition().equals(TokenPosition.BEFORE);
    }

    public enum TokenPosition { BEFORE, INSIDE, AFTER, NOT_APPLICABLE };

    @Data
    @AllArgsConstructor
    public class CursorLocation {
        private int tokenIndex;
        private TokenPosition tokenPosition;
    }
}
