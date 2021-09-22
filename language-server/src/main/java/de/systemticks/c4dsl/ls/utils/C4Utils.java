package de.systemticks.c4dsl.ls.utils;

public class C4Utils {

    public static int findFirstNonWhitespace(final CharSequence line, int startPos,
            boolean treatNewLineAsWhitespace) {
        if (line == null)
            return -1;
        int len = line.length();
        if (len == 0)
            return -1;
        if (startPos >= len)
            return -1;

        int pos = startPos;
        char c = line.charAt(pos);

        do {
            c = line.charAt(pos);
            if (!treatNewLineAsWhitespace) {
                if (c == '\n' || c == '\r')
                    return -1;
            }
            if (c > ' ')
                return pos;
            pos++;
        } while (pos < len);

        return -1;
    }	

}
