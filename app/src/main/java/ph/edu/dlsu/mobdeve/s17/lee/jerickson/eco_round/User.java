package ph.edu.dlsu.mobdeve.s17.lee.jerickson.eco_round;

public class User {

    public String email;
    public String hashPassword;
    public double userBudget;
    public String name;

    public User() {

    }

    public User(String email , String name) {
        this.email = email;
        this.name = name;
    }

    public User(String email , String hashPassword , String name) {
        this.name = name;
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
