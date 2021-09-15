package ph.edu.dlsu.mobdeve.s17.lee.jerickson.eco_round;

import android.content.Intent;
import android.os.Bundle;
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

import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;


import ph.edu.dlsu.mobdeve.s17.lee.jerickson.eco_round.databinding.ActivityListBinding;

import static ph.edu.dlsu.mobdeve.s17.lee.jerickson.eco_round.ExpenseAdapter.mTTS;


public class ListActivity extends AppCompatActivity {
    public ArrayList<Expense> expenses;
    private ActivityListBinding binding;
    private ExpenseAdapter expenseAdapter;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db;
    private Spinner sortSpinner, filterSpinner;
    private String sortSel = "Latest" , filtSel = "All";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        binding = ActivityListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        expenses = new ArrayList<>();
        db = FirebaseFirestore.getInstance();

        expenseAdapter = new ExpenseAdapter(getApplicationContext(), expenses);
        binding.rvExpenses.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        binding.rvExpenses.setAdapter(expenseAdapter);

        new Thread(new Runnable() {
            @Override
            public void run() {
                EventChangeListener();

                binding.rvExpenses.post(new Runnable() {
                    @Override
                    public void run() {
                        expenseAdapter.setData(expenses);
                    }
                });
            }
        }).start();

        //Sort Spinner
        sortSpinner = binding.spinSortBy;
        ArrayAdapter<CharSequence> sortOpts = ArrayAdapter.createFromResource(this,R.array.sort_options,
                android.R.layout.simple_spinner_item);
        sortOpts.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sortSpinner.setAdapter(sortOpts);
        setSortOption();


        //Filter Spinner
        filterSpinner = binding.spinFilterBy;
        ArrayAdapter<CharSequence> filtOpts = ArrayAdapter.createFromResource(this,R.array.filter_options,
                android.R.layout.simple_spinner_item);
        filtOpts.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filterSpinner.setAdapter(filtOpts);
        setFilterOption();


        addExp();
        navigate();
    }

   public void setSortOption(){
       sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
           @Override
           public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
               sortSel = adapterView.getItemAtPosition(i).toString();
               expenses.clear();
               if(sortSel.equalsIgnoreCase("Latest") && filtSel.equalsIgnoreCase("All"))
               {
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
                                                       expenses.add(docCh.getDocument().toObject(Expense.class));

                                                   }

                                               }
                                           }
                                       }
                                   });

                           binding.rvExpenses.post(new Runnable() {
                               @Override
                               public void run() {
                                   expenseAdapter.setData(expenses);
                               }
                           });
                       }
                   }).start();

               }
               else if(sortSel.equalsIgnoreCase("Latest") && !(filtSel.equalsIgnoreCase("All")))
               {

                   expenses.clear();
                   new Thread(new Runnable() {
                       @Override
                       public void run() {

                           db.collection("expenses").orderBy("dateCreated", Query.Direction.DESCENDING).whereEqualTo("category", filtSel)
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

                           binding.rvExpenses.post(new Runnable() {
                               @Override
                               public void run() {
                                   Log.e("PANSININ" , expenses + "");
                                   expenseAdapter.setData(expenses);
                               }
                           });
                       }
                   }).start();

               }
               else if(sortSel.equalsIgnoreCase("Oldest") && filtSel.equalsIgnoreCase("All"))
               {
                   expenses.clear();
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
                                                       expenses.add(docCh.getDocument().toObject(Expense.class));
                                                   }

                                               }
                                           }
                                       }
                                   });
                           binding.rvExpenses.post(new Runnable() {
                               @Override
                               public void run() {
                                   expenseAdapter.setData(expenses);
                               }
                           });
                       }
                   }).start();

               }
               else if(sortSel.equalsIgnoreCase("Oldest") && !filtSel.equalsIgnoreCase("All"))
               {
                   expenses.clear();
                   new Thread(new Runnable() {
                       @Override
                       public void run() {
                           db.collection("expenses").orderBy("dateCreated", Query.Direction.ASCENDING).whereEqualTo("category", filtSel)
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
                           binding.rvExpenses.post(new Runnable() {
                               @Override
                               public void run() {
                                   expenseAdapter.setData(expenses);
                               }
                           });
                       }
                   }).start();
               }
               else if(sortSel.equalsIgnoreCase("Most Expensive") && filtSel.equalsIgnoreCase("All"))
               {
                   expenses.clear();
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
                                                       expenses.add(docCh.getDocument().toObject(Expense.class));
                                                   }

                                               }
                                           }
                                       }
                                   });
                           binding.rvExpenses.post(new Runnable() {
                               @Override
                               public void run() {
                                   expenseAdapter.setData(expenses);
                               }
                           });
                       }
                   }).start();

               }
               else if(sortSel.equalsIgnoreCase("Most Expensive") && !filtSel.equalsIgnoreCase("All"))
               {
                   expenses.clear();
                   new Thread(new Runnable() {
                       @Override
                       public void run() {
                           db.collection("expenses").orderBy("price", Query.Direction.DESCENDING).whereEqualTo("category", filtSel)
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
                           binding.rvExpenses.post(new Runnable() {
                               @Override
                               public void run() {
                                   expenseAdapter.setData(expenses);
                               }
                           });
                       }
                   }).start();

               }
               else if(sortSel.equalsIgnoreCase("Cheapest") && filtSel.equalsIgnoreCase("All"))
               {
                   expenses.clear();
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
                                                       expenses.add(docCh.getDocument().toObject(Expense.class));
                                                   }

                                               }
                                           }
                                       }
                                   });
                           binding.rvExpenses.post(new Runnable() {
                               @Override
                               public void run() {
                                   expenseAdapter.setData(expenses);
                               }
                           });
                       }
                   }).start();

               }
               else if(sortSel.equalsIgnoreCase("Cheapest") && !filtSel.equalsIgnoreCase("All"))
               {
                   expenses.clear();
                   new Thread(new Runnable() {
                       @Override
                       public void run() {
                           db.collection("expenses").orderBy("price", Query.Direction.ASCENDING).whereEqualTo("category", filtSel)
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
                           binding.rvExpenses.post(new Runnable() {
                               @Override
                               public void run() {
                                   expenseAdapter.setData(expenses);
                               }
                           });
                       }
                   }).start();

               }
               else if(sortSel.equalsIgnoreCase("A to Z") && filtSel.equalsIgnoreCase("All"))
               {
                   expenses.clear();
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
                                                       expenses.add(docCh.getDocument().toObject(Expense.class));
                                                   }

                                               }
                                           }
                                       }
                                   });
                           binding.rvExpenses.post(new Runnable() {
                               @Override
                               public void run() {
                                   expenseAdapter.setData(expenses);
                               }
                           });
                       }
                   }).start();

               }
               else if(sortSel.equalsIgnoreCase("A to Z") && !filtSel.equalsIgnoreCase("All"))
               {
                   expenses.clear();
                   new Thread(new Runnable() {
                       @Override
                       public void run() {
                           db.collection("expenses").orderBy("title", Query.Direction.ASCENDING).whereEqualTo("category", filtSel)
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
                           binding.rvExpenses.post(new Runnable() {
                               @Override
                               public void run() {
                                   expenseAdapter.setData(expenses);
                               }
                           });
                       }
                   }).start();

               }
               else if(sortSel.equalsIgnoreCase("Z to A") && filtSel.equalsIgnoreCase("All"))
               {
                   expenses.clear();
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
                                                       expenses.add(docCh.getDocument().toObject(Expense.class));
                                                   }

                                               }
                                           }
                                       }
                                   });
                           binding.rvExpenses.post(new Runnable() {
                               @Override
                               public void run() {
                                   expenseAdapter.setData(expenses);
                               }
                           });
                       }
                   }).start();

               }
               else if(sortSel.equalsIgnoreCase("Z to A") && !filtSel.equalsIgnoreCase("All"))
               {
                   expenses.clear();
                   new Thread(new Runnable() {
                       @Override
                       public void run() {
                           db.collection("expenses").orderBy("title", Query.Direction.DESCENDING).whereEqualTo("category", filtSel)
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
                           binding.rvExpenses.post(new Runnable() {
                               @Override
                               public void run() {
                                   expenseAdapter.setData(expenses);
                               }
                           });
                       }
                   }).start();
               }
           }

           @Override
           public void onNothingSelected(AdapterView<?> adapterView) {

           }
       });


    }

    public void setFilterOption()
    {
        filterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                filtSel = adapterView.getItemAtPosition(i).toString();

                if(sortSel.equalsIgnoreCase("Latest") && filtSel.equalsIgnoreCase("All"))
                {
                    expenses.clear();
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
                                                        expenses.add(docCh.getDocument().toObject(Expense.class));

                                                    }

                                                }
                                            }
                                        }
                                    });

                            binding.rvExpenses.post(new Runnable() {
                                @Override
                                public void run() {
                                    expenseAdapter.setData(expenses);
                                }
                            });
                        }
                    }).start();

                }
                else if(sortSel.equalsIgnoreCase("Latest") && !(filtSel.equalsIgnoreCase("All")))
                {

                    expenses.clear();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            db.collection("expenses").orderBy("dateCreated", Query.Direction.DESCENDING).whereEqualTo("category", filtSel)
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

                            binding.rvExpenses.post(new Runnable() {
                                @Override
                                public void run() {
                                    expenseAdapter.setData(expenses);
                                }
                            });
                        }
                    }).start();

                }
                else if(sortSel.equalsIgnoreCase("Oldest") && filtSel.equalsIgnoreCase("All"))
                {
                    expenses.clear();
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
                                                        expenses.add(docCh.getDocument().toObject(Expense.class));
                                                    }

                                                }
                                            }
                                        }
                                    });
                            binding.rvExpenses.post(new Runnable() {
                                @Override
                                public void run() {
                                    expenseAdapter.setData(expenses);
                                }
                            });
                        }
                    }).start();

                }
                else if(sortSel.equalsIgnoreCase("Oldest") && !filtSel.equalsIgnoreCase("All"))
                {
                    expenses.clear();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            db.collection("expenses").orderBy("dateCreated", Query.Direction.ASCENDING).whereEqualTo("category", filtSel)
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
                            binding.rvExpenses.post(new Runnable() {
                                @Override
                                public void run() {
                                    expenseAdapter.setData(expenses);
                                }
                            });
                        }
                    }).start();
                }
                else if(sortSel.equalsIgnoreCase("Most Expensive") && filtSel.equalsIgnoreCase("All"))
                {
                    expenses.clear();
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
                                                        expenses.add(docCh.getDocument().toObject(Expense.class));
                                                    }

                                                }
                                            }
                                        }
                                    });
                            binding.rvExpenses.post(new Runnable() {
                                @Override
                                public void run() {
                                    expenseAdapter.setData(expenses);
                                }
                            });
                        }
                    }).start();

                }
                else if(sortSel.equalsIgnoreCase("Most Expensive") && !filtSel.equalsIgnoreCase("All"))
                {
                    expenses.clear();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            db.collection("expenses").orderBy("price", Query.Direction.DESCENDING).whereEqualTo("category", filtSel)
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
                            binding.rvExpenses.post(new Runnable() {
                                @Override
                                public void run() {
                                    expenseAdapter.setData(expenses);
                                }
                            });
                        }
                    }).start();

                }
                else if(sortSel.equalsIgnoreCase("Cheapest") && filtSel.equalsIgnoreCase("All"))
                {
                    expenses.clear();
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
                                                        expenses.add(docCh.getDocument().toObject(Expense.class));
                                                    }

                                                }
                                            }
                                        }
                                    });
                            binding.rvExpenses.post(new Runnable() {
                                @Override
                                public void run() {
                                    expenseAdapter.setData(expenses);
                                }
                            });
                        }
                    }).start();

                }
                else if(sortSel.equalsIgnoreCase("Cheapest") && !filtSel.equalsIgnoreCase("All"))
                {
                    expenses.clear();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            db.collection("expenses").orderBy("price", Query.Direction.ASCENDING).whereEqualTo("category", filtSel)
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
                            binding.rvExpenses.post(new Runnable() {
                                @Override
                                public void run() {
                                    expenseAdapter.setData(expenses);
                                }
                            });
                        }
                    }).start();

                }
                else if(sortSel.equalsIgnoreCase("A to Z") && filtSel.equalsIgnoreCase("All"))
                {
                    expenses.clear();
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
                                                        expenses.add(docCh.getDocument().toObject(Expense.class));
                                                    }

                                                }
                                            }
                                        }
                                    });
                            binding.rvExpenses.post(new Runnable() {
                                @Override
                                public void run() {
                                    expenseAdapter.setData(expenses);
                                }
                            });
                        }
                    }).start();

                }
                else if(sortSel.equalsIgnoreCase("A to Z") && !filtSel.equalsIgnoreCase("All"))
                {
                    expenses.clear();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            db.collection("expenses").orderBy("title", Query.Direction.ASCENDING).whereEqualTo("category", filtSel)
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
                            binding.rvExpenses.post(new Runnable() {
                                @Override
                                public void run() {
                                    expenseAdapter.setData(expenses);
                                }
                            });
                        }
                    }).start();

                }
                else if(sortSel.equalsIgnoreCase("Z to A") && filtSel.equalsIgnoreCase("All"))
                {
                    expenses.clear();
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
                                                        expenses.add(docCh.getDocument().toObject(Expense.class));
                                                    }

                                                }
                                            }
                                        }
                                    });
                            binding.rvExpenses.post(new Runnable() {
                                @Override
                                public void run() {
                                    expenseAdapter.setData(expenses);
                                }
                            });
                        }
                    }).start();

                }
                else if(sortSel.equalsIgnoreCase("Z to A") && !filtSel.equalsIgnoreCase("All"))
                {
                    expenses.clear();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            db.collection("expenses").orderBy("title", Query.Direction.DESCENDING).whereEqualTo("category", filtSel)
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
                            binding.rvExpenses.post(new Runnable() {
                                @Override
                                public void run() {
                                    expenseAdapter.setData(expenses);
                                }
                            });
                        }
                    }).start();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
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
                        //expenseAdapter.setData(expenses);
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


