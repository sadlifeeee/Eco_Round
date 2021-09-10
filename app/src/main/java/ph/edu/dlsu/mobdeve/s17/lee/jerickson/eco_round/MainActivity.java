package ph.edu.dlsu.mobdeve.s17.lee.jerickson.eco_round;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.FirebaseDatabase;

import ph.edu.dlsu.mobdeve.s17.lee.jerickson.eco_round.databinding.ActivityMainBinding;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {

    private StoragePreferences storagePreferences;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private ActivityMainBinding binding;

    private String savePass = "", saveEmail = "";

    private GoogleSignInClient mGoogleSignInClient;

    private int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        storagePreferences = new StoragePreferences(this);

        init();



    }

    @Override
    protected void onStart() {
        super.onStart();

        // Checks if the user is already signed in

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null) {
            Intent goToSecond = new Intent(MainActivity.this, ListActivity.class);
            startActivity(goToSecond);
            finish();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        if(binding.switchRemember.isChecked()) {
            saveEmail = binding.etEmail.getText().toString();
            savePass = binding.etPassword.getText().toString();
        }
        else {
            saveEmail = "";
            savePass = "";
        }

        storagePreferences.saveStringPreferences("email", saveEmail);
        storagePreferences.saveStringPreferences("password", savePass);
        storagePreferences.saveBooleanPreferences("remember" , binding.switchRemember.isChecked());
    }

    public void init() {

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        saveEmail = storagePreferences.getStringPreferences("email");

        savePass = storagePreferences.getStringPreferences("password");

        boolean checked = storagePreferences.getBooleanPreferences("remember");


        binding.switchRemember.setChecked(checked);
        binding.etEmail.setText(saveEmail);
        binding.etPassword.setText(savePass);

        // All On Clicks
        googleLoginOnClick();
        loginOnClick();
        registerOnClick();
    }

    private void registerOnClick() {
        binding.btnSignup.setOnClickListener(v -> {
            Intent goToSecond = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(goToSecond);
            finish();
        });
    }

    private void loginOnClick() {
        binding.btnLogin.setOnClickListener(view -> {
            String email = binding.etEmail.getText().toString().trim();
            String password = binding.etPassword.getText().toString().trim();

            if(email.isEmpty()) {
                binding.etEmail.setError("Email is required");
                binding.etEmail.requestFocus();
            }
            else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
                binding.etEmail.setError("Email is invalid");
                binding.etEmail.requestFocus();
            }
            else if(password.isEmpty()) {
                binding.etPassword.setError("Password is required");
                binding.etPassword.requestFocus();
            }
            else if(password.length() < 6) {
                binding.etPassword.setError("Password should have a length of 6 characters");
                binding.etPassword.requestFocus();
            }
            else {
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if(task.isSuccessful()){
                                Toast.makeText(getApplicationContext(), "Login Successful!", Toast.LENGTH_SHORT).show();
                                Intent goToSecond = new Intent(MainActivity.this, ListActivity.class);
                                startActivity(goToSecond);
                                finish();

                            } else {
                                Toast.makeText(getApplicationContext(), "Login Failed! Password or Email is incorrect" , Toast.LENGTH_SHORT).show();
                            }
                        });
            }

        });
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
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        User user = new User(currentUser.getEmail());

                        FirebaseDatabase.getInstance().getReference("Users")
                                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                .setValue(user).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                Toast.makeText(getApplicationContext(), "Login Successful!", Toast.LENGTH_SHORT).show();
                                Intent goToSecond = new Intent(MainActivity.this, ListActivity.class);
                                startActivity(goToSecond);
                                finish();
                            } else {
                                Toast.makeText(getApplicationContext(), "Login Failed! Password or Email is incorrect", Toast.LENGTH_SHORT).show();
                            }
                        });
                        Log.d(TAG, "signInWithCredential:success");

                    } else {
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
                        Toast.makeText(getApplicationContext(), "Login Failed! Password or Email is incorrect" , Toast.LENGTH_SHORT).show();
                    }
                });
    }
}