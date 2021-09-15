package ph.edu.dlsu.mobdeve.s17.lee.jerickson.eco_round;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

import ph.edu.dlsu.mobdeve.s17.lee.jerickson.eco_round.databinding.ActivityForgetPassBinding;
import ph.edu.dlsu.mobdeve.s17.lee.jerickson.eco_round.databinding.ActivityGenerateQrBinding;

public class forgetPassActivity extends AppCompatActivity {

    private ActivityForgetPassBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pass);
        binding = ActivityForgetPassBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();

    }

    private void init() {

        mAuth = FirebaseAuth.getInstance();

        binding.btnSend.setOnClickListener(view -> {

            String email = binding.etEmail.getText().toString().trim();

            if(email.isEmpty()){
                binding.etEmail.setError("Email is required");
                binding.etEmail.requestFocus();
            }

            mAuth.sendPasswordResetEmail(email).addOnSuccessListener(unused -> {
                Toast.makeText(getApplicationContext(), "Successful! Check your mail for the link", Toast.LENGTH_SHORT).show();
            }).addOnFailureListener(e -> {
                Toast.makeText(getApplicationContext(), "An Error Occured! Reset Link was not Sent ERR:" + e.getMessage(), Toast.LENGTH_SHORT).show();
            });
        });

        binding.btnCancel.setOnClickListener(view -> {
            Intent goToSecond = new Intent(forgetPassActivity.this, MainActivity.class);
            startActivity(goToSecond);
            finish();
        });
    }
}