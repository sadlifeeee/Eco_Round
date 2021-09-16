package ph.edu.dlsu.mobdeve.s17.lee.jerickson.eco_round;

public class User {

    public String email;
    public String hashPassword;
    public double userBudget;

    public User() {

    }

    public User(String email) {
        this.email = email;
    }

    public User(String email , String hashPassword) {
        this.email = email;
        this.hashPassword = hashPassword;
        this.userBudget = 0;
    }

    public double getUserBudget()
    {
        return userBudget;
    }

    public void setUserBudget(double userBudget) {
        this.userBudget = userBudget;
    }
}
