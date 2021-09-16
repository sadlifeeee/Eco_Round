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

    private ActivityFilterPopupBinding binding;

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter_popup);
        binding = ActivityFilterPopupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels , height = dm.heightPixels;

        getWindow().setLayout((int) (width *.9) , (int) (height *.6));

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
        sortSpinner.setSelection(0);

        //Filter Spinner
        filterSpinner = binding.spinFilterBy;
        ArrayAdapter<CharSequence> filtOpts = ArrayAdapter.createFromResource(this,R.array.filter_options,
                android.R.layout.simple_spinner_item);
        filtOpts.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(filtOpts);
        filterSpinner.setSelection(0);

        sortFilter();
        backOnClick();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent goBack = new Intent(getApplicationContext() , ListActivity.class);
        startActivity(goBack);
        finish();
    }

    private void backOnClick() {
        Intent goBack = new Intent(getApplicationContext() , ListActivity.class);
        startActivity(goBack);
        finish();
    }


    private void sortFilter() {
        binding.btnSort.setOnClickListener(view -> {
            String sort , filter;
            sort = sortSpinner.getItemAtPosition(sortSpinner.getSelectedItemPosition()).toString();
            filter = filterSpinner.getItemAtPosition(filterSpinner.getSelectedItemPosition()).toString();
            Log.e("filter" , "" + filter);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ArrayList<Expense> expenseTemp = new ArrayList<>();

                    expenseTemp = setFilterOption(sort , filter);

                }
            }).start();
        });
    }

    public ArrayList<Expense> setFilterOption(String sort , String filter)
    {
        ArrayList<Expense> expenseTemp = new ArrayList<>();

          if(sort.equalsIgnoreCase("Latest") && filter.equalsIgnoreCase("All"))
          {
              expenseTemp = latestAll();
          }
          else if(sort.equalsIgnoreCase("Latest") && !(filter.equalsIgnoreCase("All")))
          {
              expenseTemp = latestNotAll(filter);
          }
          else if(sort.equalsIgnoreCase("Oldest") && filter.equalsIgnoreCase("All"))
          {
              expenseTemp = oldestAll();
          }
          else if(sort.equalsIgnoreCase("Oldest") && !filter.equalsIgnoreCase("All"))
          {
              expenseTemp = oldestNotAll(filter);
          }
          else if(sort.equalsIgnoreCase("Most Expensive") && filter.equalsIgnoreCase("All"))
          {
              expenseTemp = mostExpensiveAll();
          }
          else if(sort.equalsIgnoreCase("Most Expensive") && !filter.equalsIgnoreCase("All"))
          {
              expenseTemp = mostExpensiveNotAll(filter);
          }
          else if(sort.equalsIgnoreCase("Cheapest") && filter.equalsIgnoreCase("All"))
          {
              expenseTemp = cheapestAll();
          }
          else if(sort.equalsIgnoreCase("Cheapest") && !filter.equalsIgnoreCase("All"))
          {
              expenseTemp = cheapestNotAll(filter);
          }
          else if(sort.equalsIgnoreCase("A to Z") && filter.equalsIgnoreCase("All"))
          {
              expenseTemp = aTOzAll();
          }
          else if(sort.equalsIgnoreCase("A to Z") && !filter.equalsIgnoreCase("All"))
          {
              expenseTemp = aTOznotAll(filter);
          }
          else if(sort.equalsIgnoreCase("Z to A") && filter.equalsIgnoreCase("All"))
          {
              expenseTemp = zToAAll();
          }
          else if(sort.equalsIgnoreCase("Z to A") && !filter.equalsIgnoreCase("All"))
          {
              expenseTemp = zTOaNotAll(filter);
          }

          return expenseTemp;
    }

    private ArrayList<Expense> latestAll() {
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
                                        startActivity(goBack);
                                        finish();
                                    }
                                });
                            }
                        });
            }
        }).start();


         return expenseTemp;
    }

    private ArrayList<Expense> latestNotAll(String filter) {

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


        return expenseTemp;
    }

    private ArrayList<Expense> oldestAll() {
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


        return expenseTemp;
    }

    private ArrayList<Expense> oldestNotAll(String filter) {

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
                                            expenses.add(docCh.getDocument().toObject(Expense.class));
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

        return expenseTemp;
    }

    private ArrayList<Expense> mostExpensiveAll() {
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
                                for(DocumentChange docCh : value.getDocumentChanges()){

                                    if (docCh.getType() == DocumentChange.Type.ADDED)
                                    {
                                        DocumentSnapshot doc = docCh.getDocument();
                                        if(doc.getString("userID").trim().equalsIgnoreCase(mAuth.getCurrentUser().getUid())){
                                            expenseTemp.add(docCh.getDocument().toObject(Expense.class));
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
                            }
                        });
            }
        }).start();

        return expenseTemp;
    }

    private ArrayList<Expense> mostExpensiveNotAll(String filter) {
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
                                            expenses.add(docCh.getDocument().toObject(Expense.class));
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


        return expenseTemp;
    }

    private ArrayList<Expense> cheapestAll() {
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

        return expenseTemp;
    }

    private ArrayList<Expense> cheapestNotAll(String filter) {
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


        return expenseTemp;
    }

    private ArrayList<Expense> aTOzAll() {
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


        return expenseTemp;
    }

    private ArrayList<Expense> aTOznotAll(String filter) {
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


        return expenseTemp;
    }

    private ArrayList<Expense> zToAAll() {
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


        return expenseTemp;
    }

    private ArrayList<Expense> zTOaNotAll(String filter) {
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

        return expenseTemp;
    }
}