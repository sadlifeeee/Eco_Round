package ph.edu.dlsu.mobdeve.s17.lee.jerickson.eco_round;

import android.util.Log;

import com.google.firebase.Timestamp;

import java.io.Serializable;
import java.util.Calendar;

public class Expense implements Serializable {
    private String title, category, userID, expenseID, receiptID;
    private Double price;
    private int categoryImg;
    private Timestamp dateCreated, expiresAt;

    public Expense(String category,  Timestamp dateCreated, String expenseID, Double price, String receiptID, String title, String userID){
        this.title = title;
        this.price = price;
        this.receiptID = receiptID;
        this.dateCreated = dateCreated;
        this.expenseID = expenseID;
        this.userID = userID;
        this.category = category.trim();

        //add 30 days
        Timestamp tempTime = dateCreated;
        Calendar cal = Calendar.getInstance();
        cal.setTime(tempTime.toDate());
        cal.add(Calendar.DAY_OF_MONTH,30);
        this.expiresAt = new Timestamp(cal.getTime());


        if (category == "Utilities"){
            this.categoryImg = R.drawable.utilities;
        }
        else if(category.equalsIgnoreCase("Food")){
            this.categoryImg = R.drawable.food;
            System.out.println("HELLOOOOO PUMASOK DITO HELLOOOO");
            Log.i("Test", " I am hereeee");
        }
        else if(category == "Transportation") {
            this.categoryImg = R.drawable.transpo;
        }
        else if(category == "Internet") {
            this.categoryImg = R.drawable.internet;
        }
        else if(category == "Home rent") {
            this.categoryImg = R.drawable.homerent;
        }
        else if(category == "Entertainment") {
            this.categoryImg = R.drawable.entertainment;
        }
        else if(category == "Gas") {
            this.categoryImg = R.drawable.gas;
        }
        else if(category == "Gift") {
            this.categoryImg = R.drawable.giftcard;
        }
        else if(category == "Phone") {
            this.categoryImg = R.drawable.phone;
        }
        else if(category == "Shopping") {
            this.categoryImg = R.drawable.shopping;
        }
    }

    public Expense(String category, String expenseID, Double price, String receiptID, String title, String userID){
        this.title = title;
        this.price = price;
        this.receiptID = receiptID;
        this.expenseID = expenseID;
        this.userID = userID;
        this.category = category.trim();


        if (category == "Utilities"){
            this.categoryImg = R.drawable.utilities;
        }
        else if(category.equalsIgnoreCase("Food")){
            this.categoryImg = R.drawable.food;
            System.out.println("HELLOOOOO PUMASOK DITO HELLOOOO");
            Log.i("Test", " I am hereeee");
        }
        else if(category == "Transportation") {
            this.categoryImg = R.drawable.transpo;
        }
        else if(category == "Internet") {
            this.categoryImg = R.drawable.internet;
        }
        else if(category == "Home rent") {
            this.categoryImg = R.drawable.homerent;
        }
        else if(category == "Entertainment") {
            this.categoryImg = R.drawable.entertainment;
        }
        else if(category == "Gas") {
            this.categoryImg = R.drawable.gas;
        }
        else if(category == "Gift") {
            this.categoryImg = R.drawable.giftcard;
        }
        else if(category == "Phone") {
            this.categoryImg = R.drawable.phone;
        }
        else if(category == "Shopping") {
            this.categoryImg = R.drawable.shopping;
        }
    }

    public Expense(){

    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public Double getPrice() {
        return price;
    }

    public String getReceiptID() {
        return receiptID;
    }

    public Timestamp getDateCreated() {
        return dateCreated;
    }

    public Timestamp getExpiresAt() {
        return expiresAt;
    }

    public int getCategoryImg() {
        return  categoryImg;
    }

    public String getUserID(){
        return userID;
    }

    public String getExpenseID(){
        return expenseID;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCategory(String category) {
        this.category = category;

        if (category == "Utilities"){
            this.categoryImg = R.drawable.utilities;
        }
        else if(category == "Food"){
            this.categoryImg = R.drawable.food;
        }
        else if(category == "Transportation") {
            this.categoryImg = R.drawable.transpo;
        }
        else if(category == "Internet") {
            this.categoryImg = R.drawable.internet;
        }
        else if(category == "Home rent") {
            this.categoryImg = R.drawable.homerent;
        }
        else if(category == "Entertainment") {
            this.categoryImg = R.drawable.entertainment;
        }
        else if(category == "Gas") {
            this.categoryImg = R.drawable.gas;
        }
        else if(category == "Gift") {
            this.categoryImg = R.drawable.giftcard;
        }
        else if(category == "Phone") {
            this.categoryImg = R.drawable.phone;
        }
        else if(category == "Shopping") {
            this.categoryImg = R.drawable.shopping;
        }
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setExpenseID(String expenseID) {
        this.expenseID = expenseID;
    }

    public void setReceiptID(String receiptID) {
        this.receiptID = receiptID;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setCategoryImg() {
        if (this.category == "Utilities"){
            this.categoryImg = R.drawable.utilities;
        }
        else if(this.category == "Food"){
            this.categoryImg = R.drawable.food;
        }
        else if(this.category == "Transportation") {
            this.categoryImg = R.drawable.transpo;
        }
        else if(this.category == "Internet") {
            this.categoryImg = R.drawable.internet;
        }
        else if(this.category == "Home rent") {
            this.categoryImg = R.drawable.homerent;
        }
        else if(this.category == "Entertainment") {
            this.categoryImg = R.drawable.entertainment;
        }
        else if(this.category == "Gas") {
            this.categoryImg = R.drawable.gas;
        }
        else if(this.category == "Gift") {
            this.categoryImg = R.drawable.giftcard;
        }
        else if(this.category == "Phone") {
            this.categoryImg = R.drawable.phone;
        }
        else if(this.category == "Shopping") {
            this.categoryImg = R.drawable.shopping;
        }
    }

    public void setDateCreated(Timestamp dateCreated) {
        this.dateCreated = dateCreated;
    }

    public void setExpiresAt(Timestamp expiresAt) {
        this.expiresAt = expiresAt;
    }
}
