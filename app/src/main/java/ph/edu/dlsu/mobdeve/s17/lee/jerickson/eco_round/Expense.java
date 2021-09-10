package ph.edu.dlsu.mobdeve.s17.lee.jerickson.eco_round;

public class Expense {
    private String title, category;
    private Double price;
    private int receiptID, categoryImg;
    private CustomDate dateCreated;

    public Expense(String title, String category, Double price, int receiptID, CustomDate dateCreated){
        this.title = title;
        this.price = price;
        this.receiptID = receiptID;
        this.dateCreated = dateCreated;
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

    public Expense(String title, String category, Double price, int receiptID){
        this.title = title;
        this.category = category;
        this.price = price;
        this.receiptID = receiptID;
        this.dateCreated = new CustomDate();
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

    public int getReceiptID() {
        return receiptID;
    }

    public CustomDate getDateCreated() {
        return dateCreated;
    }

    public int getCategoryImg() {
        return  categoryImg;
    }
}
