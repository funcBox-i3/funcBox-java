package funcBox;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Funbox {

    // Prime function
    public boolean isPrime( int num) {

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

}