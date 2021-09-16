package ph.edu.dlsu.mobdeve.s17.lee.jerickson.eco_round;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.metrics.Event;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.common.graph.Graph;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firestore.v1.Document;
import com.google.firestore.v1.Value;

import java.io.Serializable;
import java.sql.Array;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import ph.edu.dlsu.mobdeve.s17.lee.jerickson.eco_round.databinding.ActivityListBinding;

import static ph.edu.dlsu.mobdeve.s17.lee.jerickson.eco_round.ExpenseAdapter.mTTS;


public class ListActivity extends AppCompatActivity implements Serializable {
    private ArrayList<Expense> expenses;
    private ActivityListBinding binding;
    private ExpenseAdapter expenseAdapter;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db;
    private static final String CHANNEL_ID = "CHANNEL1";
    private String notifTitle1 = "WARNING!", notifTitle2 = "CRITICAL WARNING!";
    private String notifMess1 = "You are now ALMOST at your spending limit",
    notifMess2 = "You have now EXCEEDED your spending limit";
    private double tempBud, tempTot;
    private boolean check = false;

    private Handler mHandler = new Handler();

    private Runnable task = new Runnable() {
        @Override
        public void run() {
            EventChangeListener();

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    expenseAdapter.setData(expenses);
                    expenseAdapter.notifyDataSetChanged();
                }
            });

            expenseAdapter.notifyDataSetChanged();
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        binding = ActivityListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        expenses = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        notifChannelOreoAbove();
        expenseAdapter = new ExpenseAdapter(getApplicationContext(), expenses);
        binding.rvExpenses.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        binding.rvExpenses.setAdapter(expenseAdapter);

        ExecutorService service = Executors.newFixedThreadPool(10);
            service.execute(task);

        menuSetters();
        addExp();
        navigate();
        openFilterWindow();

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void setBudget(double tempB)
    {
        this.tempBud = tempB;
    }
    private void menuSetters() {
        String userID = mAuth.getCurrentUser().getUid();
        DocumentReference documentReference = db.collection("users").document(userID);

        documentReference.addSnapshotListener(this, (value, error) -> {
            String name = value.getString("name");
            double budget = value.getDouble("userBudget");

            binding.tvBudgetNumber.setText(String.format("P %.2f", budget));
            binding.tvUsername.setText("Hi! " + name);

            db.collection("expenses").get().addOnCompleteListener(task -> {
                double total = 0;
                if(task.isSuccessful()) {
                    for(QueryDocumentSnapshot document : task.getResult()) {
                        if(document.getString("userID").equals(userID))
                            total += document.getDouble("price");
                    }
                    binding.tvTotalSpent.setText(String.format("P %.2f", total));
                    tempTot = total;
                    checkForWarnings(total,budget);
                } else {
                    Log.e("FB ERROR" , "Error Getting Document");
                }
            });
        });



    }

    private void openFilterWindow() {
        binding.filterLayer.setOnClickListener(view -> {
            Intent filterPopUp = new Intent(ListActivity.this , filterPopup.class);
            startActivity(filterPopUp);
        });
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

                        expenseAdapter.notifyDataSetChanged();
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        navigate();

        Intent intent = getIntent();

        check = intent.getBooleanExtra("check" , false);

        if(check == true) {
            new Thread(new Runnable() {
                @Override
                public void run() {

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            expenseAdapter.setData(filterPopup.expenses);
                            expenseAdapter.notifyDataSetChanged();
                        }
                    });
                }
            }).start();

            check = false;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        navigate();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
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

    private void notifBuilderAlmost(){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_monetization)
                .setContentTitle(notifTitle1)
                .setContentText(notifMess1)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        Intent intent = new Intent(ListActivity.this, ListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(ListActivity.this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(1, builder.build());
    }

    private void notifBuilderExceed(){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_baseline_monetization)
                .setContentTitle(notifTitle2)
                .setContentText(notifMess2)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        Intent intent = new Intent(ListActivity.this, ListActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(ListActivity.this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(2, builder.build());
    }

    private void notifChannelOreoAbove()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Notification", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private void checkForWarnings(double tempT, double tempB)
    {
        double res = tempT/tempB;
        if(res > 1)
        {
            notifBuilderExceed();
        }
        else if(res >= 0.9)
        {
            notifBuilderAlmost();
        }

    }
}


