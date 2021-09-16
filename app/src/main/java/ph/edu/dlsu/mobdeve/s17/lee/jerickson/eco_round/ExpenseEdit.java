package ph.edu.dlsu.mobdeve.s17.lee.jerickson.eco_round;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import ph.edu.dlsu.mobdeve.s17.lee.jerickson.eco_round.databinding.ActivityExpenseEditBinding;

public class ExpenseEdit extends AppCompatActivity {
    private ActivityExpenseEditBinding binding;
    EditText title, price, category;
    ImageView recPhoto;
    FirebaseFirestore db;

    private Expense expense;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_expense_edit);

        binding = ActivityExpenseEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        db = FirebaseFirestore.getInstance();
        this.title = binding.etExpenseTitle;
        this.price = binding.etExpenseCost;
        this.category = binding.etExpenseCat;
        this.recPhoto = binding.ivEditreceipt;
        expense = (Expense) getIntent().getSerializableExtra("expense");
        Intent intent = getIntent();
        title.setText(expense.getTitle());
        price.setText(expense.getPrice().toString());
        category.setText(expense.getCategory());
        Picasso.with(this).load(expense.getReceiptID()).fit().centerCrop().into(recPhoto);
        updateOnClick();

    }

    private void updateOnClick(){
        binding.btnCancel.setOnClickListener(view -> {
            Intent i = new Intent(ExpenseEdit.this, ListActivity.class);
            startActivity(i);
            finish();
        });

        binding.btnUpdate.setOnClickListener(view -> {
            String newTitle, newPrice, newCategory, newRec;
            newTitle = title.getText().toString().trim();
            newPrice = price.getText().toString().trim();
            Double updPrice = Double.parseDouble(newPrice);
            newCategory = category.getText().toString().trim();
            //newRec = expense.getReceiptID();
            if(newTitle.isEmpty())
            {
                title.setError("Title is required");
                title.requestFocus();
            }
            else if(newPrice.isEmpty())
            {
                price.setError("Price is required");
                price.requestFocus();
            }
            else if(newCategory.isEmpty())
            {
                category.setError("Category is required");
                category.requestFocus();
            }
            else{
                Expense updatedExpense = new Expense(newCategory, expense.getExpenseID(), updPrice, expense.getReceiptID(), newTitle, mAuth.getCurrentUser().getUid());
                Log.i("EXPENSE ID", expense.getExpenseID());
                db.collection("expenses").document(expense.getExpenseID()).update("category", newCategory, "price", updPrice,
                        "title", newTitle).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(ExpenseEdit.this, "Expense Updated", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(ExpenseEdit.this, ListActivity.class);
                        startActivity(i);
                        finish();
                    }
                });
            }



        });
    }
}
