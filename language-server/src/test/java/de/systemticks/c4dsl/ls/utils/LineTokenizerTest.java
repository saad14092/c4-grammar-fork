package de.systemticks.c4dsl.ls.utils;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.systemticks.c4dsl.ls.utils.LineTokenizer.CursorLocation;
import de.systemticks.c4dsl.ls.utils.LineTokenizer.TokenPosition;

import static org.assertj.core.api.Assertions.assertThat;

public class LineTokenizerTest {
    
    private LineTokenizer tokenizer;

    @BeforeEach
    void setup() {
        tokenizer = new LineTokenizer();
    }

    @Test
    void tokenizeSimpleStrings() {
        List<LineToken> tokens = tokenizer.tokenize("A simple list of strings");
        assertThat(tokens.stream().map(LineToken::getToken)).containsExactly("A", "simple", "list", "of", "strings");
    }

    @Test
    void tokenizeStringsWithQuotes() {
        List<LineToken> tokens = tokenizer.tokenize("A \"list\" with \"some\" quotes");
        assertThat(tokens.stream().map(LineToken::getToken)).containsExactly("A", "\"list\"", "with", "\"some\"", "quotes");
    }

    @Test
    public void tokenizeRelationShip() {
        List<LineToken> tokens = tokenizer.tokenize("abc -> def \"My Description\"") ;
        assertThat(tokens.stream().map(LineToken::getToken)).containsExactly("abc", "->", "def", "\"My Description\"");
    }

    @Test
    public void tokenizeAssignment() {
        List<LineToken> tokens = tokenizer.tokenize("user = person \"A User\" {") ;
        assertThat(tokens.stream().map(LineToken::getToken)).containsExactly("user", "=", "person", "\"A User\"", "{");
    }

    @Test
    void beforeFirstToken() {
        List<LineToken> tokens = tokenizer.tokenize(" A simple list of strings");
        CursorLocation location = tokenizer.cursorLocation(tokens, 0);
        assertThat(location.getTokenIndex()).isEqualTo(0);
        assertThat(location.getTokenPosition()).isEqualTo(TokenPosition.BEFORE);
    }

    @Test
    void beforeTypingA() {
        List<LineToken> tokens = tokenizer.tokenize(" A simple list of strings");
        CursorLocation location = tokenizer.cursorLocation(tokens, 1);
        assertThat(location.getTokenIndex()).isEqualTo(0);
        assertThat(location.getTokenPosition()).isEqualTo(TokenPosition.BEFORE);
    }

    @Test
    void justTypedA() {
        List<LineToken> tokens = tokenizer.tokenize(" A simple list of strings");
        CursorLocation location = tokenizer.cursorLocation(tokens, 2);
        assertThat(location.getTokenIndex()).isEqualTo(0);
        assertThat(location.getTokenPosition()).isEqualTo(TokenPosition.INSIDE);
    }

    @Test
    void withinStrings() {
        List<LineToken> tokens = tokenizer.tokenize(" A simple list of strings");
        CursorLocation location = tokenizer.cursorLocation(tokens, 21);
        assertThat(location.getTokenIndex()).isEqualTo(4);
        assertThat(location.getTokenPosition()).isEqualTo(TokenPosition.INSIDE);
    }

    @Test
    void someWhereAfterLastToken() {
        List<LineToken> tokens = tokenizer.tokenize(" A simple list of strings          ");
        CursorLocation location = tokenizer.cursorLocation(tokens, 31);
        assertThat(location.getTokenIndex()).isEqualTo(4);
        assertThat(location.getTokenPosition()).isEqualTo(TokenPosition.AFTER);
    }

    @Test
    void isInBetweenTokens() {
        List<LineToken> tokens = tokenizer.tokenize("Two Tokens ");
        CursorLocation location = tokenizer.cursorLocation(tokens, 4);
        assertThat(tokenizer.isBetweenTokens(location, 0, 1)).isTrue();
    }

    @Test
    void isNotInBetweenTokens() {
        List<LineToken> tokens = tokenizer.tokenize("Two Tokens ");
        CursorLocation location = tokenizer.cursorLocation(tokens, 5);
        assertThat(tokenizer.isBetweenTokens(location, 0, 1)).isFalse();
    }

    @Test
    void isInsideToken() {
        List<LineToken> tokens = tokenizer.tokenize("Two Tokens ");
        CursorLocation location = tokenizer.cursorLocation(tokens, 5);
        assertThat(tokenizer.isInsideToken(location, 1)).isTrue();
    }

    @Test
    void isNotInsideToken() {
        List<LineToken> tokens = tokenizer.tokenize("Two Tokens ");
        CursorLocation location = tokenizer.cursorLocation(tokens, 4);
        assertThat(tokenizer.isInsideToken(location, 1)).isFalse();
    }
}
