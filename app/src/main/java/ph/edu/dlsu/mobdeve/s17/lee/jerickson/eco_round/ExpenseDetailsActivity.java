package ph.edu.dlsu.mobdeve.s17.lee.jerickson.eco_round;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;

import java.sql.Time;

import ph.edu.dlsu.mobdeve.s17.lee.jerickson.eco_round.databinding.ActivityExpenseDetailsBinding;

public class ExpenseDetailsActivity extends AppCompatActivity {
    public static final String CATEGORY = "CATEGORY", TITLE = "TITLE",
            PRICE = "PRICE", DATEDET = "DATEDET", RECEIPTIMG = "RECEIPTIMG", CATEGORYPIC = "CATEGORYPIC";
    TextView category, title, price, date, expId, pNoFormat;
    ImageView receiptImg, catImg;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
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
        this.expId = findViewById(R.id.tv_expID);
        this.pNoFormat = findViewById(R.id.tv_priceHid);

        Intent i = getIntent();
        category.setText(i.getStringExtra(CATEGORY));
        title.setText(i.getStringExtra(TITLE));
        date.setText(i.getStringExtra(DATEDET));
        //receiptImg.setImageResource(i.getStringExtra(RECEIPTIMG,0));
        catImg.setImageResource(i.getIntExtra(CATEGORYPIC,0));
        price.setText(String.format("P %.2f", i.getDoubleExtra(PRICE,0)));
        expId.setText((i.getStringExtra("expenseID")));
        expId.setVisibility(View.INVISIBLE);
        pNoFormat.setText(String.format("%.2f", i.getDoubleExtra("priceNoFormat", 0)));
        pNoFormat.setVisibility(View.INVISIBLE);
        updateExpenseOnClick();

    }

    private void updateExpenseOnClick(){
        binding.bttnEditDetails.setOnClickListener(view -> {
            String categ, titleEd, receipt, expenseID, tempPrice;
            Intent i = getIntent();
            Double fPrice = 0.00;
            categ = category.getText().toString().trim();
            titleEd = title.getText().toString().trim();
            receipt = expId.getText().toString().trim();
            expenseID = expId.getText().toString().trim();
            tempPrice = pNoFormat.getText().toString().trim();
            Log.i("STRING PRICE", tempPrice);
            fPrice = Double.parseDouble(tempPrice);

            Expense exp = new Expense(categ, expenseID, fPrice, receipt, titleEd, mAuth.getCurrentUser().getUid());

            Intent intent = new Intent(ExpenseDetailsActivity.this, ExpenseEdit.class);
            intent.putExtra("expense", exp);
            startActivity(intent);
        });


    }
}

