package ph.edu.dlsu.mobdeve.s17.lee.jerickson.eco_round;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import ph.edu.dlsu.mobdeve.s17.lee.jerickson.eco_round.databinding.ActivityGraphBinding;
import ph.edu.dlsu.mobdeve.s17.lee.jerickson.eco_round.databinding.ActivityListBinding;

public class graphActivity extends AppCompatActivity {

    private ActivityGraphBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        binding = ActivityGraphBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        navigate();
    }

    @Override
    protected void onStart() {
        super.onStart();
        navigate();
    }

    @Override
    protected void onResume() {
        super.onResume();
        navigate();
    }

    private void navigate() {
        binding.bottomNav.setSelectedItemId(R.id.nav_graph);

        binding.bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_home:
                        startActivity(new Intent(getApplicationContext(), ListActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.nav_settings:
                        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                        overridePendingTransition(0, 0);
                        finish();
                        return true;
                    case R.id.nav_graph:
                        return true;
                }
                return false;
            }
        });
    }
}