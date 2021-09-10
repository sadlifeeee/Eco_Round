package ph.edu.dlsu.mobdeve.s17.lee.jerickson.eco_round;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import ph.edu.dlsu.mobdeve.s17.lee.jerickson.eco_round.databinding.ActivityExpenseDetailsBinding;

public class ExpenseDetailsActivity extends AppCompatActivity {
    public static final String CATEGORY = "CATEGORY", TITLE = "TITLE",
            PRICE = "PRICE", DATEDET = "DATEDET", RECEIPTIMG = "RECEIPTIMG", CATEGORYPIC = "CATEGORYPIC";
    TextView category, title, price, date;
    ImageView receiptImg, catImg;
    private ActivityExpenseDetailsBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityExpenseDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        this.category = findViewById(R.id.tv_catTitle);
        this.title = findViewById(R.id.tv_detTitle);
        this.price = findViewById(R.id.tv_detPrice);
        this.date = findViewById(R.id.tv_detDate);
        this.receiptImg = findViewById(R.id.iv_detReceipt);
        this.catImg = findViewById(R.id.iv_catPic);

        Intent i = getIntent();
        category.setText(i.getStringExtra(CATEGORY));
        title.setText(i.getStringExtra(TITLE));
        date.setText(i.getStringExtra(DATEDET));
        receiptImg.setImageResource(i.getIntExtra(RECEIPTIMG,0));
        catImg.setImageResource(i.getIntExtra(CATEGORYPIC,0));
        price.setText(String.format("P %.2f", i.getDoubleExtra(PRICE,0)));
    }
}

