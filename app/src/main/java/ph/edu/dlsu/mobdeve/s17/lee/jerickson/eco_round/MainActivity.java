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

import ph.edu.dlsu.mobdeve.s17.lee.jerickson.eco_round.databinding.ActivityMainBinding;

import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {

    private StoragePreferences storagePreferences;

    private ActivityMainBinding binding;

    private String savePass = "", saveEmail = "";

    private GoogleSignInClient mGoogleSignInClient;

    private int RC_SIGN_IN = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        storagePreferences = new StoragePreferences(this);

        init();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

    }

    @Override
    protected void onStart() {
        super.onStart();

        // Checks if the user is already signed in
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        if(account != null){
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
            Intent goToSecond = new Intent(MainActivity.this, ListActivity.class);
            startActivity(goToSecond);
            finish();
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

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            Intent goToSecond = new Intent(MainActivity.this, ListActivity.class);
            startActivity(goToSecond);
            finish();
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());

            Toast.makeText(getApplicationContext(),"Google Sign In Error",Toast.LENGTH_SHORT).show();
        }
    }
}