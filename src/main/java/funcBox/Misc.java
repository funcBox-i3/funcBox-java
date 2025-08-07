package funcBox;

import java.util.ArrayList;
import java.util.List;


public class Misc {

    //primeList
    public List<Integer> primes(int start, int limit) {
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

    //Prime
    public boolean isPrime(int num) {
        if (num <= 1) {
            return false;
        }
        int n = 2;
        while (n * n <= num) {
            if (num % n == 0) {
                return false;
            }
            n++;
        }
        return n * n > num;
    }

    //Palindrome
    public boolean isPalindrome(String val) {
        val = val.toLowerCase();
        StringBuilder pal = new StringBuilder();
        for (int i = val.length() - 1; i >= 0; i--) {
            pal.append(val.charAt(i));
        }
        return pal.toString().equals(val);
    }

    //Factors
    public String getFactors(int num) {
        if (num <= 0) return "[]";
        java.util.List<Integer> factors = new java.util.ArrayList<>();
        for (int i = 2; i <= num; i++) {
            if (num % i == 0 && i != num) {
                factors.add(i);
            }
        }
        return factors.toString();
    }

    //Fibonacci
    public int fibonacci(int num) {
        if (num <= 1) return num;
        int fib = 1;
        int prevFib = 1;
        for (int i = 2; i <= num; i++) {
            int temp = fib;
            fib += prevFib;
            prevFib = temp;
        }
        return fib;
    }
}