package funcBox;

import java.util.ArrayList;
import java.util.List;

/**
 * Miscellaneous utility class providing numeric and string helper methods.
 *
 * <p>All methods are static; this class cannot be instantiated.</p>
 *
 * <p><b>Developer Note:</b> Algorithms in this class are optimized for
 * performance. Modify with care and benchmark changes when possible.</p>
 *
 * @author Kishore P
 * @since 1.0.0
 */
public final class Misc {

    /**
     * Prevents instantiation of this static utility class.
     */
    private Misc() {
    }

    private static final long[] FIB_CACHE = new long[93];
    private static final long[] SMALL_PRIMES = {2, 3, 5, 7, 11, 13, 17, 19, 23, 29, 31, 37, 41, 43, 47, 53, 59, 61, 67, 71, 73, 79, 83, 89, 97};

    static {
        // Precompute Fibonacci up to long capacity (index 92)
        FIB_CACHE[0] = 0;
        FIB_CACHE[1] = 1;
        for (int i = 2; i <= 92; i++) FIB_CACHE[i] = FIB_CACHE[i - 1] + FIB_CACHE[i - 2];
    }

    /**
     * Returns all prime numbers in the inclusive range {@code [start, limit]}.
     *
     * @param start the starting value of the range (inclusive)
     * @param limit the upper bound of the range (inclusive)
     * @return list of prime numbers in ascending order for the requested range
     * @throws IllegalArgumentException if {@code start < 2} or {@code limit < 2}
     */
    public static List<Integer> primes(int start, int limit) {
        if (limit < 2) {
            throw new IllegalArgumentException("Limit must be at least 2");
        }
        if (start < 2) {
            throw new IllegalArgumentException("Start must be at least 2");
        }
        if (start > limit) {
            return new ArrayList<>();
        }

        // Single-pass: sieve odd numbers only, emit directly into result list
        List<Integer> result = new ArrayList<>();

        if (limit < 3) {
            result.add(2);
            return result;
        }

        int size = (limit / 2) + 1;
        byte[] sieve = new byte[size]; // Java zeroes this — no manual init needed

        int sqrtLimit = (int) (Math.sqrt(limit)) / 2 + 1;
        for (int i = 1; i < sqrtLimit; i++) {
            if (sieve[i] == 0) {
                int step = 2 * i + 1;
                for (int j = 2 * i * (i + 1); j < size; j += step) {
                    sieve[j] = 1;
                }
            }
        }

        // Emit primes directly in-range (no intermediate list)
        if (start <= 2) result.add(2);
        for (int i = 1; i < size; i++) {
            if (sieve[i] == 0) {
                int prime = 2 * i + 1;
                if (prime >= start) result.add(prime);
            }
        }

        return result;
    }

    /**
     * Determines whether a number is prime.
     *
     * <p>Values {@code <= 1} are treated as non-prime.</p>
     *
     * @param num the number to test
     * @return {@code true} if the number is prime; {@code false} otherwise
     */
    public static boolean isPrime(int num) {
        if (num < 2) return false;
        
        // Fast exit for very small numbers using precomputed table
        if (num <= 97) {
            for (long p : SMALL_PRIMES) {
                if (num == p) return true;
                if (num < p) return false;
            }
        }

        if (num % 2 == 0 || num % 3 == 0) return false;

        // O(√n) trial division with 6k +/- 1 optimization
        for (int i = 5; i * i <= num; i += 6) {
            if (num % i == 0 || num % (i + 2) == 0) {
                return false;
            }
        }

        return true;
    }

    /**
     * Determines whether a string is a valid palindrome using a two-pointer approach.
     *
     * <p>A valid palindrome reads identically forward and backward, ignoring non-alphanumeric
     * characters and treating uppercase and lowercase letters as equivalent.</p>
     *
     * <p><b>Algorithm:</b> Two-pointer approach from both ends.
     * Time: O(n) where n is string length. Space: O(1) constant.</p>
     *
     * <p><b>Guard-rails:</b></p>
     * <ul>
     *   <li>Null input: Returns {@code false}</li>
     *   <li>Empty or whitespace-only string: Returns {@code true} (vacuous truth)</li>
     *   <li>Non-alphanumeric characters: Automatically skipped during comparison</li>
     * </ul>
     *
     * <p><b>Examples:</b></p>
     * <ul>
     *   <li>{@code isPalindrome("racecar")} → {@code true}</li>
     *   <li>{@code isPalindrome("A man, a plan, a canal: Panama")} → {@code true} (spaces/punctuation ignored)</li>
     *   <li>{@code isPalindrome("hello")} → {@code false}</li>
     *   <li>{@code isPalindrome("12321")} → {@code true}</li>
     *   <li>{@code isPalindrome(null)} → {@code false}</li>
     * </ul>
     *
     * @param val the string to evaluate; may be null
     * @return {@code true} if the string is a valid palindrome; {@code false} for null or non-palindromes
     * @since 1.1.1
     */
    public static boolean isPalindrome(String val) {
        if (val == null) {
            return false;
        }

        int left = 0;
        int right = val.length() - 1;

        while (left < right) {
            char leftChar = val.charAt(left);
            char rightChar = val.charAt(right);

            if (!Character.isLetterOrDigit(leftChar)) {
                left++;
            }
            else if (!Character.isLetterOrDigit(rightChar)) {
                right--;
            }
            else {
                if (Character.toLowerCase(leftChar) != Character.toLowerCase(rightChar)) {
                    return false;
                }
                left++;
                right--;
            }
        }

        return true;
    }

    /**
     * Returns all factors of a number, including {@code 1} but excluding the number itself.
     *
     * <p>For prime numbers, only {@code [1]} is returned since primes have no other
     * factors besides {@code 1} and themselves.</p>
     *
     * @param num the number whose factors are to be computed
     * @return string representation of factors (e.g., {@code [1, 2, 3]}),
     * or {@code []} for non-positive input
     */
    public static String getFactors(int num) {
        if (num <= 0) return "[]";
        if (num == 1) return "[1]";

        // O(√n) — collect factor pairs from both ends
        List<Integer> lo = new ArrayList<>();
        List<Integer> hi = new ArrayList<>();

        int sqrt = (int) Math.sqrt(num);
        for (int i = 1; i <= sqrt; i++) {
            if (num % i == 0) {
                lo.add(i);                     // small factor
                int pair = num / i;
                if (pair != i && pair != num) { // large factor (exclude num itself)
                    hi.add(pair);
                }
            }
        }

        // Merge: lo is ascending, hi needs to be reversed (descending → ascending)
        for (int i = hi.size() - 1; i >= 0; i--) {
            lo.add(hi.get(i));
        }
        return lo.toString();
    }

    /**
     * Returns the Fibonacci number at index {@code num}.
     *
     * <p><b>Guard-rails:</b></p>
     * <ul>
     *   <li>Throws {@link IllegalArgumentException} for negative index values</li>
     *   <li>Throws {@link ArithmeticException} when {@code num > 92} because the result
     *       exceeds the range of a signed 64-bit {@code long}</li>
     * </ul>
     *
     * <p>This method uses an iterative approach with O(n) time and O(1) space.</p>
     *
     * @param num the index in the Fibonacci sequence (valid range: {@code 0..92})
     * @return the Fibonacci value at the given index as a {@code long}
     * @throws IllegalArgumentException if {@code num < 0}
     * @throws ArithmeticException      if {@code num > 92}
     */
    public static long fibonacci(int num) {
        if (num < 0) {
            throw new IllegalArgumentException("Fibonacci index cannot be negative: " + num);
        }
        if (num > 92) {
            throw new ArithmeticException("Fibonacci result exceeds the capacity of a long (max index 92).");
        }

        // O(1) return from precomputed cache
        return FIB_CACHE[num];
    }

    /**
     * Splits a list of numbers into prime and composite numbers.
     *
     * <p>The returned list contains two lists:
     * <ul>
     * <li>Index {@code 0} contains prime numbers</li>
     * <li>Index {@code 1} contains composite numbers</li>
     * </ul>
     * Values {@code <= 1} are ignored.
     *
     * @param numbers the list of numbers to analyze
     * @return a two-list structure containing primes and composites; for null input both are empty
     * @since 1.1.0
     */
    public static List<List<Integer>> splitPrimeComposite(List<Integer> numbers) {
        if (numbers == null) {
            List<List<Integer>> empty = new ArrayList<>();
            empty.add(new ArrayList<>());
            empty.add(new ArrayList<>());
            return empty;
        }

        List<Integer> primes = new ArrayList<>();
        List<Integer> composites = new ArrayList<>();

        for (int n : numbers) {

            if (n <= 1) {
                continue;
            }

            if (isPrime(n)) {
                primes.add(n);
            } else {
                composites.add(n);
            }
        }

        List<List<Integer>> result = new ArrayList<>();
        result.add(primes);
        result.add(composites);

        return result;
    }

    /**
     * Determines whether two strings are anagrams of each other with configurable case sensitivity.
     *
     * <p>Two strings are anagrams if they contain the same characters with the
     * same frequencies. Whitespace is always ignored during comparison.</p>
     *
     * <p><b>Algorithm:</b> Character frequency mapping using a fixed-size array.
     * Time: O(n + m) where n, m are string lengths. Space: O(1) constant.</p>
     *
     * <p><b>Case Sensitivity Control:</b></p>
     * <ul>
     *   <li>{@code caseSensitive=false} (default): Treats 'A' and 'a' as same (case-insensitive)</li>
     *   <li>{@code caseSensitive=true}: Treats 'A' and 'a' as different (case-sensitive)</li>
     * </ul>
     *
     * <p><b>Examples:</b></p>
     * <ul>
     *   <li>{@code isAnagram("Listen", "silent", false)} → {@code true} (case-insensitive)</li>
     *   <li>{@code isAnagram("Listen", "silent", true)} → {@code false} (case-sensitive: L ≠ l)</li>
     *   <li>{@code isAnagram("listen", "silent", true)} → {@code true} (case-sensitive match)</li>
     *   <li>{@code isAnagram("a b", "ba", false)} → {@code true} (whitespace ignored)</li>
     *   <li>{@code isAnagram(null, "test", false)} → {@code false} (null-safe)</li>
     * </ul>
     *
     * @param str1          the first string to compare
     * @param str2          the second string to compare
     * @param caseSensitive {@code true} for case-sensitive comparison;
     *                      {@code false} for case-insensitive (default: treats 'A' and 'a' as same)
     * @return {@code true} if strings are anagrams; {@code false} for null, empty, or non-matching strings
     * @since 1.1.1
     */
    public static boolean isAnagram(String str1, String str2, boolean caseSensitive) {

        if (str1 == null || str2 == null) {
            return false;
        }

        // Optimized frequency counting — fast 256-size array for ASCII/Latin-1
        int[] freq = new int[256];
        int len1 = 0; 

        for (int i = 0, n = str1.length(); i < n; i++) {
            char c = str1.charAt(i);
            if (c <= ' ') continue; // Fast whitespace skip
            if (!caseSensitive && c >= 'A' && c <= 'Z') c += 32; // Fast case fold
            if (c < 256) freq[c]++;
            len1++;
        }

        if (len1 == 0) return false;

        int len2 = 0;
        for (int i = 0, n = str2.length(); i < n; i++) {
            char c = str2.charAt(i);
            if (c <= ' ') continue;
            if (!caseSensitive && c >= 'A' && c <= 'Z') c += 32;
            if (c < 256) {
                freq[c]--;
                if (freq[c] < 0) return false;
            }
            len2++;
        }

        return len1 == len2;
    }

    /**
     * Capitalizes the first character of each word in a string with comprehensive guard-rails.
     *
     * <p><b>Guard-Rails &amp; Defensive Checks:</b></p>
     * <ul>
     *   <li>Null safety: Returns empty string for null input (prevents NullPointerException)</li>
     *   <li>Whitespace handling: Normalizes all whitespace (spaces, tabs, newlines) to single spaces</li>
     *   <li>Empty string handling: Returns empty string for empty or whitespace-only input</li>
     *   <li>Single character validation: Safely handles single-character words</li>
     *   <li>Special character preservation: Handles special characters and numbers gracefully</li>
     * </ul>
     *
     * <p><b>Algorithm Performance:</b> O(n) single-pass with StringBuilder for optimal speed.</p>
     *
     * <p><b>Edge Cases Covered:</b></p>
     * <ul>
     *   <li>{@code null} → {@code ""} (empty string)</li>
     *   <li>{@code ""} → {@code ""} (empty string)</li>
     *   <li>{@code "   "} → {@code ""} (whitespace-only)</li>
     *   <li>{@code "a"} → {@code "A"} (single character)</li>
     *   <li>{@code "hello  WORLD"} → {@code "Hello World"} (multiple spaces collapsed)</li>
     *   <li>{@code "hello\tWORLD"} → {@code "Hello World"} (tabs normalized)</li>
     *   <li>{@code "hello\nWORLD"} → {@code "Hello World"} (newlines normalized)</li>
     *   <li>{@code "123 abc"} → {@code "123 Abc"} (numbers preserved)</li>
     *   <li>{@code "@test #demo"} → {@code "@test #demo"} (special chars handled)</li>
     * </ul>
     *
     * @param str the input string to capitalize word-by-word
     * @return the capitalized string, or empty string if input is null/empty/whitespace-only
     * @since 1.1.1
     */
    public static String capitalizeEachWord(String str) {
        if (str == null) {
            return "";
        }

        // Single O(n) pass: normalize whitespace, trim, and capitalize — no regex, no extra split
        final int len = str.length();
        StringBuilder sb = new StringBuilder(len);
        boolean newWord = true;   // treat start-of-string as word boundary
        boolean anyChar = false;  // tracks whether we wrote any non-space content

        for (int i = 0; i < len; i++) {
            char c = str.charAt(i);

            if (c == ' ' || c == '\t' || c == '\n' || c == '\r') {
                // Collapse all whitespace runs into a single space (only after real content)
                if (anyChar) {
                    sb.append(' ');
                    anyChar = false;
                }
                newWord = true;
            } else {
                if (newWord) {
                    sb.append(Character.toUpperCase(c));
                    newWord = false;
                } else {
                    sb.append(Character.toLowerCase(c));
                }
                anyChar = true;
            }
        }

        // Remove any trailing space that was appended before detecting end-of-string
        int end = sb.length();
        while (end > 0 && sb.charAt(end - 1) == ' ') {
            end--;
        }

        return sb.substring(0, end);
    }

    /**
     * Truncates a string to a maximum length.
     *
     * <p>If the input length is less than or equal to {@code maxLength}, the original
     * string is returned unchanged.</p>
     *
     * <p>Examples:</p>
     * <ul>
     *   <li>{@code truncate("long text...", 20)} -> {@code "long text..."}</li>
     *   <li>{@code truncate("abcdefghijklmnopqrstuvwxyz", 10)} -> {@code "abcdefghij"}</li>
     * </ul>
     *
     * @param text      the input text, may be null
     * @param maxLength the maximum allowed length; must be non-negative
     * @return the truncated or original string; returns empty string for null input
     * @throws IllegalArgumentException if {@code maxLength < 0}
     * @since 1.1.1
     */
    public static String truncate(String text, int maxLength) {
        if (maxLength < 0) {
            throw new IllegalArgumentException("maxLength must be non-negative");
        }
        if (text == null) {
            return "";
        }
        if (text.length() <= maxLength) {
            return text;
        }
        return text.substring(0, maxLength);
    }

    /**
     * Clamps a value to the inclusive range {@code [min, max]}.
     *
     * <p>If {@code value < min}, returns {@code min}. If {@code value > max},
     * returns {@code max}. Otherwise, returns {@code value} cast to {@code int}.</p>
     *
     * @param value the value to clamp
     * @param min   the minimum allowed value (inclusive)
     * @param max   the maximum allowed value (inclusive)
     * @return the clamped value as {@code int}
     * @throws IllegalArgumentException if {@code min > max}
     * @since 1.1.1
     */
    public static int clamp(long value, int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException(min + " > " + max);
        }
        return (int) Math.min(max, Math.max(value, min));
    }

    /**
     * Calculates the Levenshtein distance (edit distance) between two strings.
     *
     * <p>The Levenshtein distance is the minimum number of single-character edits
     * (insertions, deletions, or substitutions) required to change one string into the other.
     * A distance of 0 means the strings are identical.</p>
     *
     * <p><b>Guard-rails:</b></p>
     * <ul>
     *   <li>Null inputs: Treated as empty strings</li>
     * </ul>
     *
     * <p><b>Algorithm Performance:</b> O(n * m) time and O(m) space, where n and m
     * are the lengths of the strings. Optimised to use a single 1D array.</p>
     *
     * @param str1 the first string, may be null
     * @param str2 the second string, may be null
     * @return the Levenshtein distance between the two strings
     * @since 1.1.1
     */
    public static int levenshteinDistance(String str1, String str2) {
        if (str1 == null) str1 = "";
        if (str2 == null) str2 = "";

        int m = str1.length();
        int n = str2.length();

        if (m == 0) return n;
        if (n == 0) return m;

        // 1-D rolling DP with charAt — zero allocations inside the loop
        int[] dp = new int[n + 1];
        for (int j = 1; j <= n; j++) dp[j] = j;

        for (int i = 1; i <= m; i++) {
            char sc = str1.charAt(i - 1);
            int prev = i - 1;
            dp[0] = i;
            for (int j = 1; j <= n; j++) {
                int temp = dp[j];
                if (sc == str2.charAt(j - 1)) {
                    dp[j] = prev;
                } else {
                    int min = dp[j - 1];
                    if (temp < min) min = temp;
                    if (prev < min) min = prev;
                    dp[j] = 1 + min;
                }
                prev = temp;
            }
        }
        return dp[n];
    }

    /**
     * Calculates the Levenshtein distance between a target string and an array of candidate strings.
     *
     * @param target     the target string, may be null
     * @param candidates the array of candidate strings, may be null
     * @return an array of distances corresponding to each candidate. Returns an empty array
     *         if candidates is null or empty.
     * @since 1.1.1
     */
    public static int[] levenshteinDistance(String target, String[] candidates) {
        if (candidates == null || candidates.length == 0) {
            return new int[0];
        }

        int[] scores = new int[candidates.length];
        for (int i = 0; i < candidates.length; i++) {
            scores[i] = levenshteinDistance(target, candidates[i]);
        }

        return scores;
    }

    /**
     * Calculates a normalized fuzzy match score between two strings, ranging from 0.0 to 1.0.
     *
     * <p>A score of 1.0 means an exact match, while 0.0 means completely different.</p>
     *
     * @param str1 the first string, may be null
     * @param str2 the second string, may be null
     * @return a similarity score between 0.0 and 1.0
     * @since 1.1.1
     */
    public static double fuzzyMatchScore(String str1, String str2) {
        if (str1 == null) str1 = "";
        if (str2 == null) str2 = "";

        if (str1.equals(str2)) return 1.0;

        int maxLength = Math.max(str1.length(), str2.length());
        if (maxLength == 0) return 1.0;

        int distance = levenshteinDistance(str1, str2);
        return 1.0 - ((double) distance / maxLength);
    }

    /**
     * Calculates normalized fuzzy match scores for an array of candidate strings against a target.
     *
     * @param target     the target string, may be null
     * @param candidates the array of candidate strings, may be null
     * @return an array of similarity scores (0.0 to 1.0) corresponding to each candidate
     * @since 1.1.1
     */
    public static double[] fuzzyMatchScore(String target, String[] candidates) {
        if (candidates == null || candidates.length == 0) {
            return new double[0];
        }

        double[] scores = new double[candidates.length];
        for (int i = 0; i < candidates.length; i++) {
            scores[i] = fuzzyMatchScore(target, candidates[i]);
        }

        return scores;
    }

}