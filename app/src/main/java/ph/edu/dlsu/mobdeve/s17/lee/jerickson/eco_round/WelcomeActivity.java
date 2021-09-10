package ph.edu.dlsu.mobdeve.s17.lee.jerickson.eco_round;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import ph.edu.dlsu.mobdeve.s17.lee.jerickson.eco_round.databinding.ActivityWelcomeBinding;

public class WelcomeActivity extends AppCompatActivity {

    private ActivityWelcomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        binding = ActivityWelcomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Checks if the user is already signed in
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        if(account != null){
            Intent goToSecond = new Intent(WelcomeActivity.this, ListActivity.class);
            startActivity(goToSecond);
            finish();
        }
    }

    private void init() {

        // All On Clicks
        loginOnClick();
        registerOnClick();
    }

    private void loginOnClick() {
        binding.btnLogin.setOnClickListener(v -> {
            Intent i = new Intent(WelcomeActivity.this, MainActivity.class);
            startActivity(i);
            finish();
        });
    }

    private void registerOnClick() {
        binding.btnSignup.setOnClickListener(v -> {
            Intent i = new Intent(WelcomeActivity.this, RegisterActivity.class);
            startActivity(i);
            finish();
        });
    }
}