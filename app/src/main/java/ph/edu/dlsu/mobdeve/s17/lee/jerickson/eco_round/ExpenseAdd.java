package ph.edu.dlsu.mobdeve.s17.lee.jerickson.eco_round;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import ph.edu.dlsu.mobdeve.s17.lee.jerickson.eco_round.databinding.ActivityExpenseAddBinding;

public class ExpenseAdd extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private ActivityExpenseAddBinding binding;
    private Spinner spinnerVal;
    private String catSelected, title;
    private double price;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_add);

        binding = ActivityExpenseAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        db = FirebaseFirestore.getInstance();
        spinnerVal = binding.spinnerCatOptions;
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.category_options,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVal.setAdapter(adapter);
        spinnerVal.setOnItemSelectedListener(this);

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        catSelected = adapterView.getItemAtPosition(i).toString();
        if(!binding.etAddExpenseCost.toString().equals("") && !binding.etAddExpenseTitle.toString().equals(""))
        {
            addExpenseOnClick();
        }
        else {
            Toast.makeText(getApplicationContext(), "Cost and Title cannot be empty", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        binding.btnAddSave.setOnClickListener(view -> {
            Toast.makeText(getApplicationContext(), "Category cannot be blank", Toast.LENGTH_SHORT).show();
        });
    }

    private void addExpenseOnClick(){
        binding.btnAddSave.setOnClickListener(view -> {
            title = binding.etAddExpenseTitle.getText().toString().trim();
            String tempPrice = binding.etAddExpenseCost.getText().toString().trim();
            price = Double.parseDouble(tempPrice);
            String docId = RandomStringGenerator.getAlphaNumericString(20);
            Expense added = new Expense(catSelected, Timestamp.now(),docId, price, docId, title, mAuth.getCurrentUser().getUid());
            /*
            * Insert Photo upload code here
            * */
            db.collection("expenses").document(docId).set(added);
            Toast.makeText(ExpenseAdd.this, "Expense Successfully Added", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(ExpenseAdd.this, ListActivity.class);
            startActivity(i);
            finish();
        });
    }
}
