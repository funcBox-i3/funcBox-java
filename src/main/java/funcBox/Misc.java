package funcBox;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Legacy miscellaneous utility class for numeric and string helpers.
 *
 * <p>This class remains part of the public API for backward compatibility. Newer
 * modules may delegate to these methods where behavior parity is required.</p>
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
    private Misc(){}

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
     * Determines whether a string is a case-insensitive palindrome.
     *
     * <p>A palindrome reads identically forward and backward after lower-casing
     * with {@link Locale#ROOT}.</p>
     *
     * @param val the string to evaluate
     * @return {@code true} if palindrome, otherwise {@code false}; returns false for null/empty
     */
    public static boolean isPalindrome(String val) {
        if (val == null || val.isEmpty()) return false;
        val = val.toLowerCase(Locale.ROOT);
        StringBuilder pal = new StringBuilder();
        for (int i = val.length() - 1; i >= 0; i--) {
            pal.append(val.charAt(i));
        }
        return pal.toString().equals(val);
    }

    /**
     * Returns all proper factors of a number, excluding 1 and the number itself.
     *
     * @param num the number whose factors are to be computed
     * @return string representation of factors, or {@code []} for non-positive input
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
     * <p>Behavior details:</p>
     * <ul>
     *   <li>Returns {@code -1} for negative input</li>
     *   <li>Returns {@code -1} if computed value would overflow {@code int}</li>
     * </ul>
     *
     * @param num the index in the Fibonacci sequence
     * @return Fibonacci value, or {@code -1} for invalid/overflow cases
     */
    public static int fibonacci(int num) {
        if (num < 0) return -1;
        if (num <= 1) return num;
        int prevFib = 0;
        int fib = 1;
        for (int i = 2; i <= num; i++) {
            long next = (long) fib + prevFib;
            if (next > Integer.MAX_VALUE) return -1;
            prevFib = fib;
            fib = (int) next;
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
}