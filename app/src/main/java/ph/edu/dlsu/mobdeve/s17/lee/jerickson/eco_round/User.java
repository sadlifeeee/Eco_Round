package ph.edu.dlsu.mobdeve.s17.lee.jerickson.eco_round;

public class User {

    public String email;
    public String hashPassword;

    public User() {

    }

    public User(String email) {
        this.email = email;
    }

    public User(String email , String hashPassword) {
        this.email = email;
        this.hashPassword = hashPassword;
    }
}
