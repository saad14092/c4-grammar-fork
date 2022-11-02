package de.systemticks.c4dsl.ls.utils;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.assertj.core.api.Assertions.assertThat;

public class C4UtilsTest {
    
    @Test
    public void getStartPositionOK() {

        assertEquals(0, C4Utils.getStartPosition("My Text", "My"));
        assertEquals(3, C4Utils.getStartPosition("My Text", "Text"));
        assertEquals(C4Utils.NOT_FOUND_WITHIN_STRING, C4Utils.getStartPosition("My Text", "Not there"));

    }

    @Test
    public void getStartPositionInvalidParameters() {
        assertEquals(C4Utils.NOT_FOUND_WITHIN_STRING, C4Utils.getStartPosition(null, null));
        assertEquals(C4Utils.NOT_FOUND_WITHIN_STRING, C4Utils.getStartPosition("Line", null));
        assertEquals(C4Utils.NOT_FOUND_WITHIN_STRING, C4Utils.getStartPosition(null, "key"));
        assertEquals(C4Utils.NOT_FOUND_WITHIN_STRING, C4Utils.getStartPosition("", ""));
    }

    @ParameterizedTest
    @CsvSource({"0,3", "1,3", "2,3", "3,3", "4,4", "5,6", "6,6"})
    public void findFirstNonWhitespace(int input, int expected) {
       assertEquals(expected, C4Utils.findFirstNonWhitespace("   My Line", input, true));
    }

    @Test
    public void findFirstNonWhitespaceInvalidParameters() {
        assertEquals(C4Utils.NOT_FOUND_WITHIN_STRING, C4Utils.findFirstNonWhitespace(null, 0, true));
        assertEquals(C4Utils.NOT_FOUND_WITHIN_STRING, C4Utils.findFirstNonWhitespace("", 0, true));
        assertEquals(C4Utils.NOT_FOUND_WITHIN_STRING, C4Utils.findFirstNonWhitespace("My Line", -1, true));
        assertEquals(C4Utils.NOT_FOUND_WITHIN_STRING, C4Utils.findFirstNonWhitespace("My Line", 10, true));
    }

    @Test
    public void writeContentToFile(@TempDir File tmpDir) {
        
        File out = new File(tmpDir, "test.out");

        assertDoesNotThrow( () -> C4Utils.writeContentToFile(out, "something"));

        assertEquals(1, tmpDir.listFiles().length);

        assertEquals("test.out", tmpDir.listFiles()[0].getName());

        assertDoesNotThrow( () -> {
            String content = new String(Files.readAllBytes(Paths.get(tmpDir.listFiles()[0].getAbsolutePath())));
            assertEquals("something", content);
        });
    }

    @Test
    public void isBlank() {
        assertAll( "String is identified as blank" ,
            () -> assertTrue(C4Utils.isBlank(null)),
            () -> assertTrue(C4Utils.isBlank(null)),
            () -> assertTrue(C4Utils.isBlank("")),
            () -> assertTrue(C4Utils.isBlank("       ")),
            () -> assertTrue(C4Utils.isBlank("\t\t "))
        );
    }

    @Test
    public void leftFromCursorEmpty() {
        assertAll( "No String found left from cursor" ,
            () -> assertThat( C4Utils.leftFromCursor(null, 0)).isEmpty(),
            () -> assertThat( C4Utils.leftFromCursor("Foo", -1)).isEmpty()
        );
    }

    @Test
    public void leftFromCursorHasRightValue() {
        assertAll( "String found left from cursor" ,
            () -> assertThat(C4Utils.leftFromCursor("   My   Test", 6)).hasValue("My"),
            () -> assertThat(C4Utils.leftFromCursor("   My   Test", 8)).hasValue("My"),
            () -> assertThat(C4Utils.leftFromCursor("   My   Test", 10)).hasValue("My   Te")
        );
    }

    @Test
    public void tokenizeUnquotedStrings() {
        List<String> tokens = C4Utils.tokenize("This contains simple strings").stream().map(LineToken::getToken).collect(Collectors.toList()) ;
        assertThat(tokens ).containsExactly("This", "contains", "simple", "strings");
    }

    @Test
    public void tokenizeUnquotedAndQuotedStrings() {
        List<String> tokens = C4Utils.tokenize("This contains      \"   quoted   \" strings").stream().map(LineToken::getToken).collect(Collectors.toList());
        assertThat(tokens ).containsExactly("This", "contains", "\"   quoted   \"", "strings");
    }

    @Test
    public void tokenizRelationShip() {
        List<String> tokens = C4Utils.tokenize("abc -> def \"My Description\"").stream().map(LineToken::getToken).collect(Collectors.toList()) ;
        assertThat(tokens ).containsExactly("abc", "->", "def", "\"My Description\"");
    }

    @Test
    public void tokenizAssignment() {
        List<String> tokens = C4Utils.tokenize("user = person \"A User\" {").stream().map(LineToken::getToken).collect(Collectors.toList()) ;
        assertThat(tokens ).containsExactly("user", "=", "person", "\"A User\"", "{");
    }

    @Test
    public void tokenizeEmpty() {
        List<String> tokens = C4Utils.tokenize("     ").stream().map(LineToken::getToken).collect(Collectors.toList());
        assertThat(tokens ).isEmpty();
    }

    @Test
    public void cursorInsideToken() {
        LineToken token = new LineToken("My Token", 10, 18);
        assertAll(
            () -> assertThat(C4Utils.cursorInsideToken(token, 15)).isTrue(),
            () -> assertThat(C4Utils.cursorInsideToken(token, 18)).isTrue(),
            () -> assertThat(C4Utils.cursorInsideToken(token, 19)).isFalse()
        );
    }

    @Test
    public void cursorAfterToken() {
        LineToken token = new LineToken("My Token", 10, 18);
        assertAll(
            () -> assertThat(C4Utils.cursorAfterToken(token, 20)).isTrue(),
            () -> assertThat(C4Utils.cursorAfterToken(token, 5)).isFalse(),
            () -> assertThat(C4Utils.cursorAfterToken(token, 18)).isFalse()
        );
    }

    @Test
    public void cursorBeforeToken() {
        LineToken token = new LineToken("My Token", 10, 18);
        assertAll(
            () -> assertThat(C4Utils.cursorBeforeToken(token, 20)).isFalse(),
            () -> assertThat(C4Utils.cursorBeforeToken(token, 9)).isTrue(),
            () -> assertThat(C4Utils.cursorBeforeToken(token, 10)).isFalse()
        );
    }

    @Test
    public void abc() {
        List<LineToken> tokens = C4Utils.tokenize("    abc -> def  \"Some Description\"    ");
        C4Utils.findPositionInTokenList(tokens, 40);
    }

}
