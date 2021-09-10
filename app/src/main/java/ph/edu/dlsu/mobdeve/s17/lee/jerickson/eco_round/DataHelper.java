package ph.edu.dlsu.mobdeve.s17.lee.jerickson.eco_round;

import java.util.ArrayList;

public class DataHelper {
    public static ArrayList<Expense> loadExpenseData() {
        ArrayList<Expense> data = new ArrayList<>();

        data.add(new Expense("Bacsilog lunch", "Food",
                70.00, R.drawable.bacsilog,
                new CustomDate(2021, 7, 2)));
        data.add(new Expense("Beep Card Load", "Transportation",
                150.00, R.drawable.beepcard,
                new CustomDate(2021, 7, 5)));

        data.add(new Expense("Globe Internet", "Internet",
                2699.00, R.drawable.globeinternet,
                new CustomDate(2021, 6, 29)));

        data.add(new Expense("Water bill", "Utilities",
                1215.43, R.drawable.mayniladbill,
                new CustomDate(2021, 6, 25)));
        data.add(new Expense("Suicide Squad 2021", "Entertainment",
                250.00, R.drawable.movieticket,
                new CustomDate(2021, 6, 24)));
        data.add(new Expense("6 Liters", "Gas",
                300.00, R.drawable.gasrec,
                new CustomDate(2021, 6, 24)));
        return data;
    }
}