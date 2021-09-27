package de.systemticks.c4dsl.ls.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class C4Utils {

    public static final int NOT_FOUND_WITHIN_STRING = -1;

    public static int getStartPosition(String line, String key) {

		Matcher m = Pattern.compile("\\b"+key+"\\b", Pattern.CASE_INSENSITIVE).matcher(line);
		if (m.find()) {
   			return m.start();
		}

		return NOT_FOUND_WITHIN_STRING;
	}

    public static int findFirstNonWhitespace(final CharSequence line, int startPos,
            boolean treatNewLineAsWhitespace) {
        if (line == null)
            return NOT_FOUND_WITHIN_STRING;
        int len = line.length();
        if (len == 0)
            return NOT_FOUND_WITHIN_STRING;
        if (startPos >= len)
            return NOT_FOUND_WITHIN_STRING;

        int pos = startPos;
        char c = line.charAt(pos);

        do {
            c = line.charAt(pos);
            if (!treatNewLineAsWhitespace) {
                if (c == '\n' || c == '\r')
                    return NOT_FOUND_WITHIN_STRING;
            }
            if (c > ' ')
                return pos;
            pos++;
        } while (pos < len);

        return NOT_FOUND_WITHIN_STRING;
    }	

}
