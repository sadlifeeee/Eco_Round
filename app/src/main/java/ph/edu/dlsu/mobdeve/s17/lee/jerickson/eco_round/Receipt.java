package ph.edu.dlsu.mobdeve.s17.lee.jerickson.eco_round;

public class Receipt {

    private String name;

    private String imageUrl;

    public Receipt()
    {

    }

    public Receipt(String name, String imageUrl)
    {
        if(name.trim().equals(""))
        {
            name = "Empty name";
        }
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
