package funcBox;


public class funcBox {

    //Prime
    public boolean isPrime(int num) {
        if (num <= 1) {
            return false;
        } else {
            for (int i = 2; i < Math.sqrt(num); i++) {
                if (num % 2 == 0) {
                    return false;
                }
                break;
            }
        }
        return true;
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