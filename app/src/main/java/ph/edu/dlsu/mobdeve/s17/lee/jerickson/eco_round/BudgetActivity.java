package ph.edu.dlsu.mobdeve.s17.lee.jerickson.eco_round;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

import ph.edu.dlsu.mobdeve.s17.lee.jerickson.eco_round.databinding.ActivityBudgetBinding;

public class BudgetActivity  extends AppCompatActivity {
    private ActivityBudgetBinding binding;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);
        binding = ActivityBudgetBinding.inflate(getLayoutInflater());
        db = FirebaseFirestore.getInstance();
        setContentView(binding.getRoot());
        submitOnClick();
        cancelOnClick();

        DocumentReference docRef = db.collection("users").document(mAuth.getCurrentUser().getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot documentSnapshot = task.getResult();
                    if(documentSnapshot.exists())
                    {
                        double budget = (double) documentSnapshot.get("userBudget");
                        binding.tvExisingbudgetValue.setText(String.format("P %.2f", budget));
                    }
                }
            }
        });
    }

    private void submitOnClick() {
        binding.bttnSubmitBudget.setOnClickListener(view -> {
            String tempVal = binding.etBudget.getText().toString();
            double setBudg = Double.parseDouble(tempVal);
            db.collection("users").document(mAuth.getCurrentUser().getUid()).update("userBudget",setBudg);
            Intent i = new Intent(BudgetActivity.this, ListActivity.class);
            startActivity(i);
            finish();
        });
    }

    private void cancelOnClick() {
        Intent i = new Intent(BudgetActivity.this, SettingsActivity.class);
        binding.bttnCancelBudget.setOnClickListener(view -> {
            startActivity(i);
            finish();
        });
    }
}
