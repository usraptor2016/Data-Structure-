import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/**
 * The program generates a tag cloud from a given text file.
 *
 * @author Wenbo Nan
 * @author Chenghan Wen
 *
 */
public final class TagCloudGenerator {

    /**
     * Private constructor so this utility class cannot be instantiated.
     */
    private TagCloudGenerator() {
    }

    /**
     * Definition of whitespace separators.
     */
    private static final String SEPARATORS = " \t\n\r,-.!?[]\"';:/`()*";

    /**
     * Maximum font size.
     */
    private static final int MAX_FONT = 48;

    /**
     * Minimum font size.
     */
    private static final int MIN_FONT = 11;

    /**
     * Compare {@code Map.Pair<String,Integer>}s in alphabetical order according
     * to the keys.
     */
    private static class MapPairKeyLT
            implements Comparator<Map.Entry<String, Integer>> {
        @Override
        public int compare(Map.Entry<String, Integer> o1,
                Map.Entry<String, Integer> o2) {
            int ans = o1.getKey().compareTo(o2.getKey());
            if (ans == 0) {
                ans = o1.getValue().compareTo(o2.getValue());
            }
            return ans;
        }
    }

    /**
     * Compare {@code Map.Pair<String,Integer>}s in alphabetical order according
     * to the keys.
     */
    private static class MapPairValueLT
            implements Comparator<Map.Entry<String, Integer>> {
        @Override
        public int compare(Map.Entry<String, Integer> o1,
                Map.Entry<String, Integer> o2) {
            int ans = o2.getValue().compareTo(o1.getValue());
            if (ans == 0) {
                ans = o2.getKey().compareTo(o1.getKey());
            }
            return ans;
        }
    }

    /**
     * Checks whether the given {@code String} represents a valid integer value
     * in the range Integer.MIN_VALUE..Integer.MAX_VALUE.
     *
     * @param s
     *            the {@code String} to be checked
     * @return true if the given {@code String} represents a valid integer,
     *         false otherwise
     * @ensures canParseInt = [the given String represents a valid integer]
     */
    public static boolean canParseInt(String s) {
        assert s != null : "Violation of: s is not null";
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Returns the first "word" (maximal length string of characters not in
     * {@code SEPARATORS}) or "separator string" (maximal length string of
     * characters in {@code SEPARATORS}) in the given {@code text} starting at
     * the given {@code position}.
     *
     * @param text
     *            the {@code String} from which to get the word or separator
     *            string
     * @param position
     *            the starting index
     * @return the first word or separator string found in {@code text} starting
     *         at index {@code position}
     * @requires 0 <= position < |text|
     * @ensures <pre>
     * nextWordOrSeparator =
     *   text[position, position + |nextWordOrSeparator|)  and
     * if entries(text[position, position + 1)) intersection entries(SEPARATORS) = {}
     * then
     *   entries(nextWordOrSeparator) intersection entries(SEPARATORS) = {}  and
     *   (position + |nextWordOrSeparator| = |text|  or
     *    entries(text[position, position + |nextWordOrSeparator| + 1))
     *      intersection entries(SEPARATORS) /= {})
     * else
     *   entries(nextWordOrSeparator) is subset of entries(SEPARATORS)  and
     *   (position + |nextWordOrSeparator| = |text|  or
     *    entries(text[position, position + |nextWordOrSeparator| + 1))
     *      is not subset of entries(SEPARATORS))
     * </pre>
     */
    private static String nextWordOrSeparator(String text, int position) {
        assert text != null : "Violation of: text is not null";
        assert 0 <= position : "Violation of: 0 <= position";
        assert position < text.length() : "Violation of: position < |text|";

        StringBuilder s = new StringBuilder();
        if (SEPARATORS.indexOf(text.charAt(position)) >= 0) {
            s.append(text.charAt(position));
            for (int i = position + 1; i < text.length(); i++) {
                if (SEPARATORS.indexOf(text.charAt(i)) >= 0) {
                    s.append(text.charAt(i));
                } else {
                    i = text.length();
                }
            }
        } else {
            s.append(text.charAt(position));
            for (int i = position + 1; i < text.length(); i++) {
                if (SEPARATORS.indexOf(text.charAt(i)) < 0) {
                    s.append(text.charAt(i));
                } else {
                    i = text.length();
                }
            }
        }
        return s.toString();
    }

    /**
     * Generates a map containing all the words read from an input file and
     * their corresponding counts.
     *
     * @param in
     *            the input stream
     * @return the map of words and their count
     * @throws IOException
     *             string read problem
     */
    private static Map<String, Integer> generateMap(BufferedReader in)
            throws IOException {
        Map<String, Integer> m = new HashMap<>();
        String str = in.readLine();
        while (str != null) {
            str = str.toLowerCase();
            int position = 0;
            while (position < str.length()) {
                String word = nextWordOrSeparator(str, position);
                if (SEPARATORS.indexOf(word.charAt(0)) < 0) {
                    if (m.containsKey(word)) {
                        int x = m.get(word);
                        m.put(word, x + 1);
                    } else {
                        m.put(word, 1);
                    }
                }
                position += word.length();
            }
            str = in.readLine();
        }
        return m;
    }

    /**
     * Generates a sorting machine containing the top words in a map.
     *
     * @param m
     *            the map to get words from
     * @param size
     *            the number of elements to add in the sorting machine
     * @param arr
     *            the array containing the maximum and minimum occurrences of
     *            the words
     * @return the sorting machine of the top words in the file
     */
    private static List<Map.Entry<String, Integer>> getWordsAlphabetical(
            List<Map.Entry<String, Integer>> m, int size, int[] arr) {
        Comparator<Map.Entry<String, Integer>> cs = new MapPairKeyLT();
        List<Map.Entry<String, Integer>> s = new ArrayList<>();
        if (m.size() != 0) {
            Map.Entry<String, Integer> p;
            if (m.size() == 1) {
                p = m.remove(0);
                s.add(p);
            } else {
                int size2 = size;
                if (size > m.size()) {
                    size2 = m.size();
                }
                p = m.remove(0);
                s.add(p);
                arr[0] = p.getValue();
                for (int i = 1; i < size2 - 1; i++) {
                    p = m.remove(0);
                    s.add(p);
                }
                p = m.remove(0);
                s.add(p);
                arr[1] = p.getValue();
                s.sort(cs);
            }
        }
        return s;
    }

    /**
     * Sort words by their counts, with more counts being in the front.
     *
     * @param m
     *            the map to sort
     * @clears m
     * @return sorting machine of map pair sorted with the word count
     */
    private static List<Map.Entry<String, Integer>> getWordsFrequency(
            Set<Map.Entry<String, Integer>> m) {
        Comparator<Map.Entry<String, Integer>> cs = new MapPairValueLT();
        List<Map.Entry<String, Integer>> s = new ArrayList<>();
        Iterator<Map.Entry<String, Integer>> itr = m.iterator();
        while (itr.hasNext()) {
            Map.Entry<String, Integer> p = itr.next();
            s.add(p);
        }
        s.sort(cs);
        return s;
    }

    /**
     * Output the generated tag cloud to an HTML file.
     *
     * @param out
     *            the output stream
     * @param s
     *            the sorting machine of words and their counts to print
     * @param file
     *            the name of the input file
     * @param max
     *            the count of word that appears the most
     * @param min
     *            the count of word that appears the least
     */
    private static void printHTML(PrintWriter out,
            List<Map.Entry<String, Integer>> s, String file, int max, int min) {
        out.println("<html> <head>");
        out.println(
                "<title>Top " + s.size() + " words in " + file + "</title>");
        out.println(
                "<link href=\"http://cse.osu.edu/software/2231/web-sw2/assignments/"
                        + "projects/tag-cloud-generator/data/tagcloud.css\""
                        + " rel=\"stylesheet\" type=\"text/css\">");
        out.println("</head> <body>");
        out.println("<h2>Top " + s.size() + " words in " + file + "</h2>");
        out.println("<hr>");
        out.println("<div class =\"cdiv\">");
        out.println("<p class=\"cbox\">");
        while (s.size() > 0) {
            Map.Entry<String, Integer> p = s.remove(0);
            printWord(p, out, max, min);
        }
        out.println("</p> </div> </body> </html>");
    }

    /**
     * Gets a positive integer from the user input.
     *
     * @param in
     *            the input stream
     * @return the positive integer the user inputs
     */
    private static int getNonNegativeInteger(Scanner in) {
        System.out.println(
                "Please enter the number of words to be included in the tag cloud: ");
        String str = in.nextLine();
        while (true) {
            while (!canParseInt(str)) {
                System.out.println("Please enter a number: ");
                str = in.nextLine();
            }
            int x = Integer.parseInt(str);
            if (x < 0) {
                System.out.println("Please enter a non-negative number: ");
                str = in.nextLine();
            } else {
                return x;
            }
        }
    }

    /**
     * Output a word.
     *
     * @param p
     *            the map pair of word an its count to print
     * @param out
     *            the output stream
     * @param max
     *            the count of word that appears the most
     * @param min
     *            the count of word that appears the least
     */
    private static void printWord(Map.Entry<String, Integer> p, PrintWriter out,
            int max, int min) {
        int font;
        if (max != min) {
            font = (MAX_FONT - MIN_FONT) * (p.getValue() - min) / (max - min)
                    + MIN_FONT;
        } else {
            font = (MAX_FONT + MIN_FONT) / 2;
        }
        out.println("<span style=\"cursor:default\" class=\"f" + font
                + "\" title=\"count: " + p.getValue() + "\">" + p.getKey()
                + "</span>");
    }

    /**
     * Main method.
     *
     * @param args
     *            the command line arguments
     */
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.println("Please enter the name of the input file: ");
        String inFile = in.nextLine();
        BufferedReader file;
        try {
            file = new BufferedReader(new FileReader(inFile));
        } catch (IOException e) {
            System.err.println("Error opening file.");
            in.close();
            return;
        }
        System.out.println("Please enter the name of the output file: ");
        String outFile = in.nextLine();
        PrintWriter html;
        try {
            html = new PrintWriter(new BufferedWriter(new FileWriter(outFile)));
        } catch (IOException e) {
            System.err.println("Error creating file.");
            in.close();
            try {
                file.close();
            } catch (IOException e1) {
                System.err.println("Error closing file.");
            }
            return;
        }
        Map<String, Integer> map;
        try {
            map = generateMap(file);
        } catch (IOException e1) {
            System.err.println("Error reading from file.");
            in.close();
            html.close();
            return;
        }
        Set<Map.Entry<String, Integer>> m = map.entrySet();
        int num = getNonNegativeInteger(in);
        int[] maxAndMin = new int[2];
        List<Map.Entry<String, Integer>> s = getWordsFrequency(m);
        List<Map.Entry<String, Integer>> s2 = getWordsAlphabetical(s, num,
                maxAndMin);
        printHTML(html, s2, inFile, maxAndMin[0], maxAndMin[1]);
        in.close();
        try {
            file.close();
        } catch (IOException e) {
            System.err.println("Error closing file.");
        }
        html.close();
    }

}
