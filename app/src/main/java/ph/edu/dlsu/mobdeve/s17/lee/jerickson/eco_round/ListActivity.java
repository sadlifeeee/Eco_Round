package ph.edu.dlsu.mobdeve.s17.lee.jerickson.eco_round;

import android.content.Intent;
import android.media.metrics.Event;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.common.graph.Graph;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firestore.v1.Document;

import java.sql.Array;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;


import ph.edu.dlsu.mobdeve.s17.lee.jerickson.eco_round.databinding.ActivityListBinding;

import static ph.edu.dlsu.mobdeve.s17.lee.jerickson.eco_round.ExpenseAdapter.mTTS;


public class ListActivity extends AppCompatActivity {
    private ArrayList<Expense> expenses;
    private ActivityListBinding binding;
    private ExpenseAdapter expenseAdapter;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db;

    private Handler mHandler = new Handler();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        binding = ActivityListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        expenses = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        new Thread(new Runnable() {
            @Override
            public void run() {
                expenses = EventChangeListener();

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("PANSININ" , "" + expenses.size());
                        binding.rvExpenses.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                        expenseAdapter = new ExpenseAdapter(getApplicationContext(), expenses);
                        binding.rvExpenses.setAdapter(expenseAdapter);
                        expenseAdapter.setData(expenses);
                    }
                });
            }
        }).start();

        addExp();
        navigate();
        openFilterWindow();
    }

    private void openFilterWindow() {
        binding.filterLayer.setOnClickListener(view -> {
            Intent filterPopUp = new Intent(ListActivity.this , filterPopup.class);
            startActivity(filterPopUp);
        });
    }

    public ArrayList<Expense> EventChangeListener(){
        ArrayList<Expense> expenseTemp = new ArrayList<>();

        db.collection("expenses").orderBy("dateCreated", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if(error != null)
                        {
                            Log.e("Firestore error", error.getMessage());
                            return;
                        }

                        for(DocumentChange docCh : value.getDocumentChanges()){

                            if (docCh.getType() == DocumentChange.Type.ADDED)
                            {
                                DocumentSnapshot doc = docCh.getDocument();
                                if(doc.getString("userID").trim().equalsIgnoreCase(mAuth.getCurrentUser().getUid())){
                                    expenseTemp.add(docCh.getDocument().toObject(Expense.class));
                                }

                            }
                        }
                    }
                });

        return expenseTemp;
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
        binding.bottomNav.setSelectedItemId(R.id.nav_home);

        binding.bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected( MenuItem item) {
                switch(item.getItemId()){
                    case R.id.nav_home:
                        return true;
                    case R.id.nav_settings:
                        startActivity(new Intent(getApplicationContext(), SettingsActivity.class));
                        overridePendingTransition(0 , 0);
                        finish();
                        return true;
                    case R.id.nav_graph:
                        startActivity(new Intent(getApplicationContext(), graphActivity.class));
                        overridePendingTransition(0 , 0);
                        finish();
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    protected void onDestroy() {

        if(mTTS != null){
            mTTS.stop();
            mTTS.shutdown();
        }

        super.onDestroy();
    }

    private void addExp(){
        binding.btnAdd.setOnClickListener(view -> {
            Intent i = new Intent(ListActivity.this, ExpenseAdd.class);
            startActivity(i);
        });
    }

}


