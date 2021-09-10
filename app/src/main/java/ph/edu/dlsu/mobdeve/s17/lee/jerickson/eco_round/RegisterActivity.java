package ph.edu.dlsu.mobdeve.s17.lee.jerickson.eco_round;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import ph.edu.dlsu.mobdeve.s17.lee.jerickson.eco_round.databinding.ActivityRegisterBinding;

import static android.content.ContentValues.TAG;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;

    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private int RC_SIGN_IN = 0;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        init();
    }

    private void init() {
        googleLoginOnClick();
        loginOnClick();
        registerOnClick();
    }

    private void registerOnClick() {
        binding.btnRegister.setOnClickListener(v -> {
            registerUser();
        });
    }

    private void registerUser() {
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();
        String confirmPass = binding.etConfirmPassword.getText().toString().trim();

        if (!(password.equals(confirmPass))) {
            binding.etConfirmPassword.setError("Password does not match");
            binding.etConfirmPassword.requestFocus();
        } else if (email.isEmpty()) {
            binding.etEmail.setError("Email is required");
            binding.etEmail.requestFocus();
        } else if (password.isEmpty()) {
            binding.etPassword.setError("Password is required");
            binding.etPassword.requestFocus();
        } else if (confirmPass.isEmpty()) {
            binding.etConfirmPassword.setError("Confirm Password is required");
            binding.etConfirmPassword.requestFocus();
        } else if (password.length() < 6) {
            binding.etPassword.setError("Password should have at least 6 characters");
            binding.etPassword.requestFocus();
        } else {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(RegisterActivity.this, task -> {
                        if (task.isSuccessful()) {
                            User user = new User(email);

                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            Toast.makeText(getApplicationContext(), "User registered successfully!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getApplicationContext(), "Failed to Register User! Please try again", Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        } else {
                            Toast.makeText(getApplicationContext(), "Failed to Register User! Please try again", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void googleLoginOnClick() {
        binding.btnGoogleLogin.setOnClickListener(view -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            Intent goToSecond = new Intent(RegisterActivity.this, ListActivity.class);
            startActivity(goToSecond);
            finish();
        } catch (ApiException e) {
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(getApplicationContext(), "Google Sign In Error", Toast.LENGTH_SHORT).show();
        }
    }

    public void loginOnClick() {
        binding.btnSignIn.setOnClickListener(v -> {
            Intent goToSecond = new Intent(RegisterActivity.this, MainActivity.class);
            startActivity(goToSecond);
            finish();
        });
    }

}
