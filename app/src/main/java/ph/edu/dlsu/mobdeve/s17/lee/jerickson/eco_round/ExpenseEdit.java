package ph.edu.dlsu.mobdeve.s17.lee.jerickson.eco_round;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

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
        /*
        Code to retrieve image from firebase storage

        recPhoto.setImageResource();
        */

    updateOnClick();
    }
    private void updateOnClick(){
        binding.btnUpdate.setOnClickListener(view -> {
            String newTitle, newPrice, newCategory;
            newTitle = title.getText().toString().trim();
            newPrice = price.getText().toString().trim();
            Double updPrice = Double.parseDouble(newPrice);
            newCategory = category.getText().toString().trim();
            /*
            * Insert input validation here
            *
            * */
            Expense updatedExpense = new Expense(newCategory, expense.getDateCreated(), expense.getExpenseID(), updPrice, expense.getReceiptID(), newTitle, mAuth.getCurrentUser().getUid());
            db.collection("expenses").document(expense.getExpenseID()).set(updatedExpense).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(ExpenseEdit.this, "Expense Updated", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
