package ph.edu.dlsu.mobdeve.s17.lee.jerickson.eco_round;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomnavigation.BottomNavigationView;
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

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;


import ph.edu.dlsu.mobdeve.s17.lee.jerickson.eco_round.databinding.ActivityListBinding;

import static ph.edu.dlsu.mobdeve.s17.lee.jerickson.eco_round.ExpenseAdapter.mTTS;


public class ListActivity extends AppCompatActivity {
    private ArrayList<Expense> expenses;
    private ActivityListBinding binding;
    private ExpenseAdapter expenseAdapter;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        binding = ActivityListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        expenses = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        //expenses = DataHelper.loadExpenseData();
        EventChangeListener();
        expenseAdapter = new ExpenseAdapter(getApplicationContext(), expenses);
        binding.rvExpenses.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        binding.rvExpenses.setAdapter(expenseAdapter);
        addExp();
        navigate();
    }

    public void EventChangeListener(){
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
                                Log.i("EXPENSE USER ID", doc.getString("userID"));
                                Log.i("CURRENT USER ID", mAuth.getCurrentUser().getUid());
                                Boolean match = doc.getString("userID").trim().equalsIgnoreCase(mAuth.getCurrentUser().getUid());
                                Log.i("MATCH", String.valueOf(match));
                                if(doc.getString("userID").trim().equalsIgnoreCase(mAuth.getCurrentUser().getUid())){
                                    Timestamp expiresAt = doc.getTimestamp("expiresAt");
                                    Timestamp currDate = Timestamp.now();
                                    if(currDate.compareTo(expiresAt) == 0 || currDate.compareTo(expiresAt) > 0)
                                    {
                                        String expIDtoDelete = doc.getString("expenseID");
                                        db.collection("expenses").document(expIDtoDelete).delete();
                                    }
                                    else{
                                        expenses.add(docCh.getDocument().toObject(Expense.class));
                                    }

                                }

                            }
                        }
                    }
                });
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
