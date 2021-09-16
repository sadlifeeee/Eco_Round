package ph.edu.dlsu.mobdeve.s17.lee.jerickson.eco_round;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;

import ph.edu.dlsu.mobdeve.s17.lee.jerickson.eco_round.databinding.ActivityGraphBinding;
import ph.edu.dlsu.mobdeve.s17.lee.jerickson.eco_round.databinding.ActivityListBinding;

public class graphActivity extends AppCompatActivity {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db;
    private ActivityGraphBinding binding;
    private String userID = mAuth.getCurrentUser().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        binding = ActivityGraphBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        db = FirebaseFirestore.getInstance();
        navigate();

        setupChart();
        calculateLoadChart();
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

    private void setupChart() {
        binding.mainPieChart.setDrawHoleEnabled(true);
        binding.mainPieChart.setUsePercentValues(true);
        binding.mainPieChart.setEntryLabelTextSize(12);
        binding.mainPieChart.setEntryLabelColor(Color.BLACK);
        binding.mainPieChart.setCenterText("Spending by Category");
        binding.mainPieChart.setCenterTextSize(24);
        binding.mainPieChart.getDescription().setEnabled(false);

        Legend legend = binding.mainPieChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setDrawInside(false);
        legend.setEnabled(true);
    }

    private void calculateLoadChart() {
        String[] typeArray = { "Entertainment" , "Food" , "Gas" , "Gift" , "Home rent" , "Internet" , "Phone" , "Shopping" , "Transportation" , "Utilities"};
        int[] intTypeArray = { 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0}; // String and Int == Equals
        float[] percentageArray = {0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 , 0 ,0};

        db.collection("expenses").get().addOnCompleteListener(task -> {
            float total = 0;
            if(task.isSuccessful()) {
                for(QueryDocumentSnapshot document : task.getResult()) {
                    if(document.getString("userID").equals(userID)) {
                        for(int i = 0; i < 10; i++) {
                            if(document.getString("category").equals(typeArray[i])){
                                intTypeArray[i] += 1;
                            }
                        }
                    }
                    total += 1;
                }

                for(int i = 0; i < 10; i++) {
                    percentageArray[i] = (float) intTypeArray[i] / total;
                }

                ArrayList<PieEntry> entries = new ArrayList<>();
                if(percentageArray[0] != 0)
                    entries.add(new PieEntry(percentageArray[0] , "Entertainment"));
                if(percentageArray[1] != 0)
                    entries.add(new PieEntry(percentageArray[1] , "Food"));
                if(percentageArray[2] != 0)
                    entries.add(new PieEntry(percentageArray[2] , "Gas"));
                if(percentageArray[3] != 0)
                    entries.add(new PieEntry(percentageArray[3] , "Gift"));
                if(percentageArray[4] != 0)
                    entries.add(new PieEntry(percentageArray[4] , "Home Rent"));
                if(percentageArray[5] != 0)
                    entries.add(new PieEntry(percentageArray[5] , "Internet"));
                if(percentageArray[6] != 0)
                    entries.add(new PieEntry(percentageArray[6] , "Phone"));
                if(percentageArray[7] != 0)
                    entries.add(new PieEntry(percentageArray[7] , "Shopping"));
                if(percentageArray[8] != 0)
                    entries.add(new PieEntry(percentageArray[8] , "Transportation"));
                if(percentageArray[9] != 0)
                    entries.add(new PieEntry(percentageArray[9] , "Utilities"));

                ArrayList<Integer> colors = new ArrayList<>();
                for(int color: ColorTemplate.MATERIAL_COLORS){
                    colors.add(color);
                }

                for(int color: ColorTemplate.VORDIPLOM_COLORS){
                    colors.add(color);
                }

                PieDataSet dataSet = new PieDataSet(entries , "Expense Category");
                dataSet.setColors(colors);

                PieData data = new PieData(dataSet);
                data.setDrawValues(true);
                data.setValueFormatter(new PercentFormatter(binding.mainPieChart));
                data.setValueTextSize(12f);
                data.setValueTextColor(Color.BLACK);

                binding.mainPieChart.setData(data);
                binding.mainPieChart.invalidate();

                binding.mainPieChart.animateY(1400 , Easing.EaseInOutQuad);

            } else {
                Log.e("FB ERROR" , "Error Getting Document (GraphActivity)");
            }
        });

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