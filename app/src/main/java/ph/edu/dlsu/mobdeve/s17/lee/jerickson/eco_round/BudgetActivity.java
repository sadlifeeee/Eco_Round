package ph.edu.dlsu.mobdeve.s17.lee.jerickson.eco_round;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import ph.edu.dlsu.mobdeve.s17.lee.jerickson.eco_round.databinding.ActivityBudgetBinding;

public class BudgetActivity  extends AppCompatActivity {
    private ActivityBudgetBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_budget);
        binding = ActivityBudgetBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //submitOnClick();
        cancelOnClick();
    }
/*
    private void submitOnClick() {
        Intent i = new Intent(BudgetActivity.this, SettingsActivity.class);
        binding.bttnSubmitBudget.setOnClickListener(view -> {
            i.putExtra(SettingsActivity.BUDGETVALUE, binding.etBudget.getText().toString());
        });
    }
*/
    private void cancelOnClick() {
        Intent i = new Intent(BudgetActivity.this, SettingsActivity.class);
        binding.bttnCancelBudget.setOnClickListener(view -> {
            startActivity(i);
            finish();
        });
    }
}
