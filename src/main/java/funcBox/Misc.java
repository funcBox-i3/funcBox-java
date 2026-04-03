package funcBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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

        List<Integer> allPrimes = new ArrayList<>();

        if (limit < 3) {
            allPrimes.add(2);
        } else {
            int size = (limit / 2) + 1;
            byte[] isPrime = new byte[size]; // 0 = true, 1 = false
            for (int i = 0; i < size; i++) {
                isPrime[i] = 0;
            }

            int sqrtLimit = (int) (Math.sqrt(limit)) / 2 + 1;

            for (int i = 1; i < sqrtLimit; i++) {
                if (isPrime[i] == 0) {
                    int startIdx = 2 * i * (i + 1);
                    for (int j = startIdx; j < size; j += 2 * i + 1) {
                        isPrime[j] = 1;
                    }
                }
            }

            allPrimes.add(2);
            for (int i = 1; i < size; i++) {
                if (isPrime[i] == 0) {
                    allPrimes.add(2 * i + 1);
                }
            }
        }

        List<Integer> result = new ArrayList<>();
        for (int prime : allPrimes) {
            if (prime >= start && prime <= limit) {
                result.add(prime);
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

        if (num <= 1) return false;
        if (num <= 3) return true;

        if (num % 2 == 0 || num % 3 == 0) return false;

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
        List<Integer> factors = new ArrayList<>();
        for (int i = 1; i <= num; i++) {
            if (num % i == 0 && i != num) {
                factors.add(i);
            }
        }
        return factors.toString();
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
        // 1. Guard against invalid inputs explicitly
        if (num < 0) {
            throw new IllegalArgumentException("Fibonacci index cannot be negative: " + num);
        }

        // 2. Guard against hardware limits (the 93rd number overflows a 64-bit signed long)
        if (num > 92) {
            throw new ArithmeticException("Fibonacci result exceeds the capacity of a long (max index 92).");
        }

        if (num <= 1) return num;

        // 3. Use 64-bit variables for the internal state
        long prevFib = 0;
        long fib = 1;

        for (int i = 2; i <= num; i++) {
            long next = prevFib + fib;
            prevFib = fib;
            fib = next;
        }

        return fib;
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

        String normalized1;
        String normalized2;

        if (caseSensitive) {
            normalized1 = str1.replaceAll("\\s+", "");
            normalized2 = str2.replaceAll("\\s+", "");
        } else {
            normalized1 = str1.toLowerCase(Locale.ROOT).replaceAll("\\s+", "");
            normalized2 = str2.toLowerCase(Locale.ROOT).replaceAll("\\s+", "");
        }

        if (normalized1.length() != normalized2.length()) {
            return false;
        }

        if (normalized1.isEmpty()) {
            return false;
        }

        int[] charFreq = new int[256];

        for (char c : normalized1.toCharArray()) {
            charFreq[c]++;
        }

        for (char c : normalized2.toCharArray()) {
            charFreq[c]--;
            if (charFreq[c] < 0) {
                return false;
            }
        }

        return true;
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

        String normalized = str.replaceAll("\\s+", " ").trim();

        if (normalized.isEmpty()) {
            return "";
        }

        if (!normalized.contains(" ")) {
            if (normalized.length() == 1) {
                return normalized.toUpperCase(Locale.ROOT);
            }
            return Character.toUpperCase(normalized.charAt(0)) + normalized.substring(1).toLowerCase(Locale.ROOT);
        }

        String[] words = normalized.split(" ");

        if (words.length == 0) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < words.length; i++) {
            String word = words[i];

            if (word.isEmpty()) {
                continue;
            }

            if (word.length() == 1) {
                sb.append(Character.toUpperCase(word.charAt(0)));
            } else {
                sb.append(Character.toUpperCase(word.charAt(0)))
                        .append(word.substring(1).toLowerCase(Locale.ROOT));
            }

            if (i < words.length - 1) {
                sb.append(" ");
            }
        }

        return sb.toString();
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

}