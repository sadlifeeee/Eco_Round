package ph.edu.dlsu.mobdeve.s17.lee.jerickson.eco_round;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import ph.edu.dlsu.mobdeve.s17.lee.jerickson.eco_round.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;
    private GoogleSignInClient mGoogleSignInClient;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db;
    private String userID = mAuth.getCurrentUser().getUid();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        navigate();
        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigate();
    }

    @Override
    protected void onStart() {
        navigate();
        super.onStart();
    }

    private void init(){

        db = FirebaseFirestore.getInstance();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("227831008319-b6trtqhbaegtjpvqfdbo65iuvnckhn7o.apps.googleusercontent.com")
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        logoutOnClick();
        budgetOnClick();
        generateQRonClick();
    }

    private void navigate() {
        binding.bottomNav.setSelectedItemId(R.id.nav_settings);

        binding.bottomNav.setOnNavigationItemSelectedListener(item -> {
            switch(item.getItemId()){
                case R.id.nav_home:
                    startActivity(new Intent(getApplicationContext(),ListActivity.class));
                    overridePendingTransition(0 , 0);
                    finish();
                    return true;
                case R.id.nav_settings:
                    return true;
            }
            return false;
        });
    }
    private void logoutOnClick() {
        binding.tvLogoutbutton.setOnClickListener(v ->  {
            FirebaseAuth.getInstance().signOut();
            Toast.makeText(getApplicationContext(), "Successfully Signed Out" , Toast.LENGTH_SHORT).show();

            mGoogleSignInClient.signOut().addOnCompleteListener(task -> {
                if(task.isSuccessful()) {
                    Intent i = new Intent(SettingsActivity.this, WelcomeActivity.class);
                    startActivity(i);
                    finish();
                }
            });


        });
    }

    private void budgetOnClick() {
        binding.llBudgetsettings.setOnClickListener(view -> {
            Intent i = new Intent(SettingsActivity.this, BudgetActivity.class);
            startActivity(i);
            finish();
        });
    }

    private void generateQRonClick() {
        DocumentReference documentReference = db.collection("users").document(userID);

        documentReference.addSnapshotListener(this, (value, error) -> {
            String password;

            password = value.getString("hashPassword");

            if(password == null) {
                binding.generateQRLayout.setVisibility(View.GONE);
            } else {
                binding.tvGenerateQRLogin.setOnClickListener(view -> {
                    Intent i = new Intent(SettingsActivity.this, generateQR.class);
                    startActivity(i);
                    finish();
                });
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
