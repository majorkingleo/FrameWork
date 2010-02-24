/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package at.redeye.FrameWork.utilities;

import java.util.Vector;

/**
 * 
 * @author martin
 */
public class StringUtils {

    private static int defaultAutoLineLength = 40;

    static boolean contains(char c, String what) {
        return what.indexOf(c) >= 0;
    }

    static int skip_char(StringBuilder s, String what, int pos) {
        while (pos <= (s.length() - 1)) {
            char c;

            c = s.charAt(pos);

            if (contains(c, what)) {
                pos++;
                continue;
            }

            break;
        }

        return pos;
    }

    static int skip_char_reverse(StringBuilder s, String what, int pos) {
        while (pos > 0) {
            char c;

            c = s.charAt(pos);

            if (contains(c, what)) {
                pos--;
                continue;
            }

            break;
        }

        return pos;
    }

    public static int skip_spaces_reverse(StringBuilder s, int pos) {
        return skip_char_reverse(s, " \t\n\r", pos);
    }

    public static int skip_spaces(StringBuilder s, int pos) {
        return skip_char(s, " \t\n\r", pos);
    }

    public static boolean is_space(char c) {
        if (c == ' ' || c == '\t' || c == '\n' || c == '\r') {
            return true;
        }

        return false;
    }

    public static Vector<String> split_str(StringBuilder s, String c) {
        Vector<String> res = new Vector<String>();

        int start = 0;
        do {
            int pos = s.indexOf(c, start);

            if (pos < 0) {
                res.add(s.substring(start));
                break;
            }

            res.add(s.substring(start, pos));

            start = pos + 1;

        } while (start > 0);

        return res;
    }

    public static String strip(StringBuilder s, String what) {
        int start = skip_char(s, what, 0);
        int end = skip_char_reverse(s, what, s.length() - 1);

        return s.substring(start, end + 1);
    }

    public static String strip_post(StringBuilder s, String what) {
        int end = skip_char_reverse(s, what, s.length() - 1);

        return s.substring(0, end + 1);
    }

    public static String strip_post(String str, String what) {
        StringBuilder s = new StringBuilder();
        s.append(str);
        return strip_post(s, what);
    }

    public static String strip(String s, String what) {
        return strip(new StringBuilder(s), what);
    }

    public static void set_defaultAutoLineLenght(int length) {
        defaultAutoLineLength = length;
    }

    public static int get_defaultAutoLineLenght() {
        return defaultAutoLineLength;
    }

    public static String autoLineBreak(String what) {
        return autoLineBreak(what, defaultAutoLineLength);
    }

    public static String autoLineBreak(StringBuilder what, int length) {
        return autoLineBreak(what.toString(), length);
    }

    public static String autoLineBreak(StringBuilder what) {
        return autoLineBreak(what.toString(), defaultAutoLineLength);
    }

    public static String autoLineBreak(String what, int length) {

        final char[] myPreferedSigns = {';', '.', ',', '!', '?', '>', '-'};
        final char[] mySpaceSigns = {' ', '\t'};

        final int searchWindowLengthPreferedSigns = 20;
        final int searchWindowLengthSpaceSigns = 50;

        if (length < searchWindowLengthPreferedSigns / 2 ||
                length >= what.length() ||
                searchWindowLengthPreferedSigns / 2 >= what.length()) {
            // Doesn't make sense
            return what;
        }
        
        char[] in = what.toCharArray();
        StringBuilder str = new StringBuilder();

        for (int walker = 1; walker < in.length; walker++) {

            if (in.length <= searchWindowLengthPreferedSigns / 2) {
                break;
            }

            if (walker % length == 0) {

            //   System.out.println("Want to break at: " +
            //          in[walker - 2] + in[walker - 1] + ">" +
            //          in[walker] + "<" + in[walker + 1] + in[walker + 2]);

                // try to find a sign in search window
                boolean found = false;
                for (int signidx = 0; signidx < myPreferedSigns.length; signidx++) {

                    // try with preferred signs
                    for (int index = 1;
                            index <= (searchWindowLengthPreferedSigns / 2); index++) {

                        if ((walker + index + 1) >= in.length || (walker - index) <= 0) {
                            break; // not enough left
                        }

                        if (in[walker + index] == myPreferedSigns[signidx]) {
                            str.append(new String(in, 0, walker + index + 1));
                            str.append("\n");                            

                            // mob hier stand 2 aber damit verwerfen wir das nächste Zeichen. auch nicht gut
                            walker += 1; // jump over break sign

                            // spaces überspringen
                            for( int i = 0 ; i < mySpaceSigns.length; i++ )
                            {
                                if( in[walker+1] == mySpaceSigns[i] )
                                {
                                    walker++;
                                }
                            }


                            String rest = new String(in, walker + index,
                                    in.length - (walker + index));
                            in = rest.toCharArray();
                            walker = 0;
                            found = true;
                            break;
                        } else if (in[walker - index] == myPreferedSigns[signidx]) {
                            str.append(new String(in, 0, walker - index + 1));
                            str.append("\n");

                            // mob hier stand 2 aber damit verwerfen wir das nächste Zeichen. auch nicht gut
                            walker += 1; // jump over break sign

                            // spaces überspringen
                            for( int i = 0 ; i < mySpaceSigns.length; i++ )
                            {
                                if( in[walker+1] == mySpaceSigns[i] )
                                {
                                    walker++;
                                }
                            }

                            String rest = new String(in, walker - index,
                                    in.length - (walker - index));
                            in = rest.toCharArray();

                            walker = 0;
                            found = true;
                            break;
                        }
                    }

                    if (found) {
                        break;
                    }
                }

                for (int signidx = 0; signidx < mySpaceSigns.length; signidx++) {
                    // try with blanks
                    for (int index = 1;
                            index <= (searchWindowLengthSpaceSigns / 2); index++) {

                        if ((walker + index + 1) >= in.length || (walker - index) <= 0) {
                            break; // not enough left
                        }

                        if (in[walker + index] == mySpaceSigns[signidx]) {

                            str.append(new String(in, 0, walker + index));
                            str.append("\n");
                            walker++;
                            String rest = new String(in, walker + index,
                                    in.length - (walker + index));
                            in = rest.toCharArray();
                            walker = 0;
                            found = true;
                            break;
                        } else if (in[walker - index] == mySpaceSigns[signidx]) {

                            str.append(new String(in, 0, walker - index + 1));
                            str.append("\n");
                            walker++;
                            String rest = new String(in, walker - index,
                                    in.length - (walker - index));
                            in = rest.toCharArray();
                            walker = 0;
                            found = true;
                            break;
                        }

                    }

                    if (found) {
                        break;
                    }

                }

            }

        }
        str.append(in); // rest
        return str.toString();
    }

    public static String FormatDouble( double d )
    {
        String s = String.format("%f",d);
        s = strip_post(s,"0");
        s = strip_post(s,".");
        s = strip_post(s,",");

        return s;
    }
}
