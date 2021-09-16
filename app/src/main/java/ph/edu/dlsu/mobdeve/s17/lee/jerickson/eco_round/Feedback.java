package ph.edu.dlsu.mobdeve.s17.lee.jerickson.eco_round;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import ph.edu.dlsu.mobdeve.s17.lee.jerickson.eco_round.databinding.ActivityFeedbackBinding;

public class Feedback extends AppCompatActivity {

    private ActivityFeedbackBinding binding;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        binding = ActivityFeedbackBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels , height = dm.heightPixels;

        getWindow().setLayout((int) (width *.85) , (int) (height *.5));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0; params.y = -20;

        getWindow().setAttributes(params);

        onClicks();
    }

    private void onClicks() {

        binding.btnSend.setOnClickListener(view -> {
            String feedback = binding.etFeedback.getText().toString();

            if(feedback.isEmpty()){
                binding.etFeedback.setError("Password does not match");
                binding.etFeedback.requestFocus();
            }
            else {
                db.collection("feedback").add(feedback).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if(task.isSuccessful()) {
                            Toast.makeText(getApplicationContext() , "Successfully Sent Feedback!" , Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext() , "Feedback not Sent" , Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        });
    }
}