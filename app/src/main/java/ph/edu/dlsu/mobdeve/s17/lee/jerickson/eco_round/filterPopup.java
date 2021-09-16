package ph.edu.dlsu.mobdeve.s17.lee.jerickson.eco_round;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import ph.edu.dlsu.mobdeve.s17.lee.jerickson.eco_round.databinding.ActivityFilterPopupBinding;

public class filterPopup extends AppCompatActivity implements Serializable{

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db;

    public static ArrayList<Expense> expenses = new ArrayList<>();

    private Spinner sortSpinner, filterSpinner;

    public int saveSort = 0, saveFilter = 0;
    public String saveSortTemp = "", saveFilterTemp = "";

    private ActivityFilterPopupBinding binding;

    private StoragePreferences storagePreferences;
    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_popup);
        binding = ActivityFilterPopupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        storagePreferences = new StoragePreferences(this);

        saveSortTemp = storagePreferences.getStringPreferences("SORT");

        saveFilterTemp = storagePreferences.getStringPreferences("FILTER");

        if(saveSortTemp.isEmpty())
            saveSortTemp = "0";

        if(saveFilterTemp.isEmpty())
            saveFilterTemp = "0";


        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels , height = dm.heightPixels;

        getWindow().setLayout((int) (width *.9) , (int) (height *.4));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0; params.y = -20;

        getWindow().setAttributes(params);

        db = FirebaseFirestore.getInstance();

        //Sort Spinner
        sortSpinner = binding.spinSortBy;
        ArrayAdapter<CharSequence> sortOpts = ArrayAdapter.createFromResource(this,R.array.sort_options,
                android.R.layout.simple_spinner_item);
        sortOpts.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(sortOpts);
        saveSort = Integer.parseInt(saveSortTemp);
        sortSpinner.setSelection(saveSort);

        //Filter Spinner
        filterSpinner = binding.spinFilterBy;
        ArrayAdapter<CharSequence> filtOpts = ArrayAdapter.createFromResource(this,R.array.filter_options,
                android.R.layout.simple_spinner_item);
        filtOpts.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(filtOpts);
        saveFilter = Integer.parseInt(saveFilterTemp);
        filterSpinner.setSelection(saveFilter);

        sortFilter();
    }

    @Override
    protected void onPause() {
        super.onPause();

        saveSort = sortSpinner.getSelectedItemPosition();
        saveFilter = filterSpinner.getSelectedItemPosition();
        saveSortTemp = saveSort + "";
        saveFilterTemp = saveFilter + "";


        storagePreferences.saveStringPreferences("SORT", saveSortTemp);
        storagePreferences.saveStringPreferences("FILTER", saveFilterTemp);
    }

    private void sortFilter() {
        binding.btnSort.setOnClickListener(view -> {
            String sort , filter;
            sort = sortSpinner.getItemAtPosition(sortSpinner.getSelectedItemPosition()).toString();
            filter = filterSpinner.getItemAtPosition(filterSpinner.getSelectedItemPosition()).toString();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    setFilterOption(sort , filter);
                }
            }).start();
        });
    }

    public void setFilterOption(String sort , String filter)
    {

          if(sort.equalsIgnoreCase("Latest") && filter.equalsIgnoreCase("All"))
          {
             latestAll();
          }
          else if(sort.equalsIgnoreCase("Latest") && !(filter.equalsIgnoreCase("All")))
          {
              latestNotAll(filter);
          }
          else if(sort.equalsIgnoreCase("Oldest") && filter.equalsIgnoreCase("All"))
          {
              oldestAll();
          }
          else if(sort.equalsIgnoreCase("Oldest") && !(filter.equalsIgnoreCase("All")))
          {
              oldestNotAll(filter);
          }
          else if(sort.equalsIgnoreCase("Most Expensive") && filter.equalsIgnoreCase("All"))
          {
              mostExpensiveAll();
          }
          else if(sort.equalsIgnoreCase("Most Expensive") && !(filter.equalsIgnoreCase("All")))
          {
              mostExpensiveNotAll(filter);
          }
          else if(sort.equalsIgnoreCase("Cheapest") && filter.equalsIgnoreCase("All"))
          {
              cheapestAll();
          }
          else if(sort.equalsIgnoreCase("Cheapest") && !(filter.equalsIgnoreCase("All")))
          {
              cheapestNotAll(filter);
          }
          else if(sort.equalsIgnoreCase("A to Z") && filter.equalsIgnoreCase("All"))
          {
              aTOzAll();
          }
          else if(sort.equalsIgnoreCase("A to Z") && !(filter.equalsIgnoreCase("All")))
          {
              aTOznotAll(filter);
          }
          else if(sort.equalsIgnoreCase("Z to A") && filter.equalsIgnoreCase("All"))
          {
              zToAAll();
          }
          else if(sort.equalsIgnoreCase("Z to A") && !(filter.equalsIgnoreCase("All")))
          {
              zTOaNotAll(filter);
          }

    }

    private void latestAll() {
        ArrayList<Expense> expenseTemp = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
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

                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent goBack = new Intent(getApplicationContext() , ListActivity.class);
                                        expenses = expenseTemp;
                                        goBack.putExtra("check" , true);
                                        startActivity(goBack);
                                        finish();
                                    }
                                });
                            }
                        });
            }
        }).start();

    }

    private void latestNotAll(String filter) {

        ArrayList<Expense> expenseTemp = new ArrayList<>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                db.collection("expenses").orderBy("dateCreated", Query.Direction.DESCENDING).whereEqualTo("category", filter)
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
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent goBack = new Intent(getApplicationContext() , ListActivity.class);
                                        expenses = expenseTemp;
                                        goBack.putExtra("check" , true);
                                        startActivity(goBack);
                                        finish();
                                    }
                                });

                            }
                        });

            }
        }).start();

    }

    private void oldestAll() {

        ArrayList<Expense> expenseTemp = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                db.collection("expenses").orderBy("dateCreated", Query.Direction.ASCENDING)
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

                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent goBack = new Intent(getApplicationContext() , ListActivity.class);
                                        expenses = expenseTemp;
                                        goBack.putExtra("check" , true);
                                        startActivity(goBack);
                                        finish();
                                    }
                                });
                            }
                        });
            }
        }).start();


    }

    private void oldestNotAll(String filter) {

        ArrayList<Expense> expenseTemp = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                db.collection("expenses").orderBy("dateCreated", Query.Direction.ASCENDING).whereEqualTo("category", filter)
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

                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent goBack = new Intent(getApplicationContext() , ListActivity.class);
                                        expenses = expenseTemp;
                                        goBack.putExtra("check" , true);
                                        startActivity(goBack);
                                        finish();
                                    }
                                });

                            }
                        });

            }
        }).start();

    }

    private void mostExpensiveAll() {
        ArrayList<Expense> expenseTemp = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                db.collection("expenses").orderBy("price", Query.Direction.DESCENDING)
                        .addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                                if(error != null)
                                {
                                    Log.e("Firestore error", error.getMessage());
                                    return;
                                }
                                for(DocumentChange docCh : value.getDocumentChanges()) {

                                    if (docCh.getType() == DocumentChange.Type.ADDED) {
                                        DocumentSnapshot doc = docCh.getDocument();
                                        if (doc.getString("userID").trim().equalsIgnoreCase(mAuth.getCurrentUser().getUid())) {
                                            expenseTemp.add(docCh.getDocument().toObject(Expense.class));
                                        }

                                    }
                                }
                                    mHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Intent goBack = new Intent(getApplicationContext() , ListActivity.class);
                                            expenses = expenseTemp;
                                            goBack.putExtra("check" , true);
                                            startActivity(goBack);
                                            finish();
                                        }
                                    });

                            }
                        });
            }
        }).start();

    }

    private void mostExpensiveNotAll(String filter) {
        ArrayList<Expense> expenseTemp = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                db.collection("expenses").orderBy("price", Query.Direction.DESCENDING).whereEqualTo("category", filter)
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

                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent goBack = new Intent(getApplicationContext() , ListActivity.class);
                                        expenses = expenseTemp;
                                        goBack.putExtra("check" , true);
                                        startActivity(goBack);
                                        finish();
                                    }
                                });
                            }
                        });
            }
        }).start();

    }

    private void cheapestAll() {
        ArrayList<Expense> expenseTemp = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                db.collection("expenses").orderBy("price", Query.Direction.ASCENDING)
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

                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent goBack = new Intent(getApplicationContext() , ListActivity.class);
                                        expenses = expenseTemp;
                                        goBack.putExtra("check" , true);
                                        startActivity(goBack);
                                        finish();
                                    }
                                });
                            }
                        });

            }
        }).start();

    }

    private void cheapestNotAll(String filter) {
        ArrayList<Expense> expenseTemp = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                db.collection("expenses").orderBy("price", Query.Direction.ASCENDING).whereEqualTo("category", filter)
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

                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent goBack = new Intent(getApplicationContext() , ListActivity.class);
                                        expenses = expenseTemp;
                                        goBack.putExtra("check" , true);
                                        startActivity(goBack);
                                        finish();
                                    }
                                });
                            }
                        });
            }
        }).start();

    }

    private void aTOzAll() {

        ArrayList<Expense> expenseTemp = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                db.collection("expenses").orderBy("title", Query.Direction.ASCENDING)
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

                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent goBack = new Intent(getApplicationContext() , ListActivity.class);
                                        expenses = expenseTemp;
                                        goBack.putExtra("check" , true);
                                        startActivity(goBack);
                                        finish();
                                    }
                                });
                            }
                        });
            }
        }).start();

    }

    private void aTOznotAll(String filter) {
        ArrayList<Expense> expenseTemp = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                db.collection("expenses").orderBy("title", Query.Direction.ASCENDING).whereEqualTo("category", filter)
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

                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent goBack = new Intent(getApplicationContext() , ListActivity.class);
                                        expenses = expenseTemp;
                                        goBack.putExtra("check" , true);
                                        startActivity(goBack);
                                        finish();
                                    }
                                });
                            }
                        });
            }
        }).start();

    }

    private void zToAAll() {
        ArrayList<Expense> expenseTemp = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                db.collection("expenses").orderBy("title", Query.Direction.DESCENDING)
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

                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent goBack = new Intent(getApplicationContext() , ListActivity.class);
                                        expenses = expenseTemp;
                                        goBack.putExtra("check" , true);
                                        startActivity(goBack);
                                        finish();
                                    }
                                });
                            }
                        });
            }
        }).start();

    }

    private void zTOaNotAll(String filter) {
        ArrayList<Expense> expenseTemp = new ArrayList<>();
        new Thread(new Runnable() {
            @Override
            public void run() {
                db.collection("expenses").orderBy("title", Query.Direction.DESCENDING).whereEqualTo("category", filter)
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

                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent goBack = new Intent(getApplicationContext() , ListActivity.class);
                                        expenses = expenseTemp;
                                        goBack.putExtra("check" , true);
                                        startActivity(goBack);
                                        finish();
                                    }
                                });
                            }
                        });
            }
        }).start();

    }
}