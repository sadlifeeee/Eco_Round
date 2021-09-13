package ph.edu.dlsu.mobdeve.s17.lee.jerickson.eco_round;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
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
import com.google.firebase.firestore.FirebaseFirestore;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import ph.edu.dlsu.mobdeve.s17.lee.jerickson.eco_round.databinding.ActivityRegisterBinding;

import static android.content.ContentValues.TAG;

public class RegisterActivity extends AppCompatActivity {

    private ActivityRegisterBinding binding;

    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private int RC_SIGN_IN = 0;

    private final String mainKey = "g7zgoIV6uNeWve4ybSWr";

    FirebaseFirestore db;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db = FirebaseFirestore.getInstance();
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
                            try {
                                String hashPass = encrypt(password, mainKey);
                                String userID = mAuth.getCurrentUser().getUid();

                                User user = new User(email , hashPass);

                                db.collection("users").document(userID).set(user).addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "User registered successfully!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Failed to Register User! Please try again", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), "Failed to Register User! Please try again, ERROR CODE: " + e , Toast.LENGTH_SHORT).show();
                            }


                        } else {
                            Toast.makeText(getApplicationContext(), "Failed to Register User! User already exist", Toast.LENGTH_SHORT).show();
                        }
                    });

            FirebaseAuth.getInstance().signOut();
        }
    }


    private String encrypt(String Data , String mainKey) throws Exception {
        SecretKeySpec key = generateKey(mainKey);
        Cipher c = Cipher.getInstance("AES");
        c.init(Cipher.ENCRYPT_MODE , key);
        byte[] encVal = c.doFinal(Data.getBytes());
        String encryptedValue = Base64.encodeToString(encVal , Base64.DEFAULT);
        return encryptedValue;
    }

    private SecretKeySpec generateKey(String mainKey) throws Exception{
        final MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] bytes = mainKey.getBytes("UTF-8");
        digest.update(bytes , 0 , bytes.length);
        byte[] key = digest.digest();
        SecretKeySpec secretKeySpec = new SecretKeySpec(key , "AES");
        return secretKeySpec;
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
