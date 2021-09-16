package ph.edu.dlsu.mobdeve.s17.lee.jerickson.eco_round;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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

import java.util.ArrayList;
import java.util.List;

import ph.edu.dlsu.mobdeve.s17.lee.jerickson.eco_round.databinding.ActivityFilterPopupBinding;

public class filterPopup extends AppCompatActivity {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db;

    public ArrayList<Expense> expenses;

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
    }

    private void sortFilter() {
        binding.btnSort.setOnClickListener(view -> {
            String sort , filter;

            sort = sortSpinner.getItemAtPosition(sortSpinner.getSelectedItemPosition()).toString();
            filter = filterSpinner.getItemAtPosition(sortSpinner.getSelectedItemPosition()).toString();
            new Thread(new Runnable() {
                @Override
                public void run() {

                    setFilterOption(sort , filter);

                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Intent goBack = new Intent(getApplicationContext() , ListActivity.class);
                            goBack.putExtra("filtered" , expenses);
                            startActivity(goBack);
                        }
                    });
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
          else if(sort.equalsIgnoreCase("Oldest") && !filter.equalsIgnoreCase("All"))
          {
              oldestNotAll(filter);
          }
          else if(sort.equalsIgnoreCase("Most Expensive") && filter.equalsIgnoreCase("All"))
          {
              mostExpensiveAll();
          }
          else if(sort.equalsIgnoreCase("Most Expensive") && !filter.equalsIgnoreCase("All"))
          {
              mostExpensiveNotAll(filter);
          }
          else if(sort.equalsIgnoreCase("Cheapest") && filter.equalsIgnoreCase("All"))
          {
              cheapestAll();
          }
          else if(sort.equalsIgnoreCase("Cheapest") && !filter.equalsIgnoreCase("All"))
          {
              cheapestNotAll(filter);
          }
          else if(sort.equalsIgnoreCase("A to Z") && filter.equalsIgnoreCase("All"))
          {
              aTOzAll();
          }
          else if(sort.equalsIgnoreCase("A to Z") && !filter.equalsIgnoreCase("All"))
          {
              aTOznotAll(filter);
          }
          else if(sort.equalsIgnoreCase("Z to A") && filter.equalsIgnoreCase("All"))
          {
              zToAAll();
          }
          else if(sort.equalsIgnoreCase("Z to A") && !filter.equalsIgnoreCase("All"))
          {
              zTOaNotAll(filter);
          }

    }

    private void latestAll(){
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
                                     expenses.add(docCh.getDocument().toObject(Expense.class));
                                 }

                             }
                         }
                     }
                 });
    }

    private void latestNotAll(String filter) {
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
                                expenses.add(docCh.getDocument().toObject(Expense.class));
                            }

                        }
                    }
                }
            });
    }

    private void oldestAll() {
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
                                    expenses.add(docCh.getDocument().toObject(Expense.class));
                                }

                            }
                        }
                    }
                });
    }

    private void oldestNotAll(String filter) {
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

                      }
                  });
    }

    private void mostExpensiveAll() {
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
                                expenses.add(docCh.getDocument().toObject(Expense.class));
                            }

                        }
                    }
                }
            });
    }

    private void mostExpensiveNotAll(String filter) {
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
                      }
                  });
    }

    private void cheapestAll() {
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
                                      expenses.add(docCh.getDocument().toObject(Expense.class));
                                  }

                              }
                          }
                      }
                  });
    }

    private void cheapestNotAll(String filter) {
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
                                      expenses.add(docCh.getDocument().toObject(Expense.class));
                                  }

                              }
                          }
                      }
                  });
    }

    private void aTOzAll() {
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
                                      expenses.add(docCh.getDocument().toObject(Expense.class));
                                  }

                              }
                          }
                      }
                  });
    }

    private void aTOznotAll(String filter) {
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
                                      expenses.add(docCh.getDocument().toObject(Expense.class));
                                  }

                              }
                          }
                      }
                  });
    }

    private void zToAAll() {
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
                                      expenses.add(docCh.getDocument().toObject(Expense.class));
                                  }

                              }
                          }
                      }
                  });
    }

    private void zTOaNotAll(String filter) {
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
                                      expenses.add(docCh.getDocument().toObject(Expense.class));
                                  }

                              }
                          }
                      }
                  });
    }
}