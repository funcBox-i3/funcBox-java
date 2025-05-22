package funcBox;


public class funcBox {

    // Prime function
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
    public boolean isPalindrome(String val){
        val = val.toLowerCase();
        StringBuilder pal= new StringBuilder();
        for (int i = val.length()-1;i>=0;i--) {
            pal.append(val.charAt(i));
        }
        return pal.toString().equals(val);
    }

}