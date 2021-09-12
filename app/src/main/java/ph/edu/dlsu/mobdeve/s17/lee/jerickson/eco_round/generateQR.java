package ph.edu.dlsu.mobdeve.s17.lee.jerickson.eco_round;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import ph.edu.dlsu.mobdeve.s17.lee.jerickson.eco_round.databinding.ActivityGenerateQrBinding;
import ph.edu.dlsu.mobdeve.s17.lee.jerickson.eco_round.databinding.ActivitySettingsBinding;

public class generateQR extends AppCompatActivity {

    private ActivityGenerateQrBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_qr);
        binding = ActivityGenerateQrBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}