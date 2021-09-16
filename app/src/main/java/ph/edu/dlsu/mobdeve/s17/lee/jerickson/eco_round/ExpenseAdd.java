package ph.edu.dlsu.mobdeve.s17.lee.jerickson.eco_round;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import ph.edu.dlsu.mobdeve.s17.lee.jerickson.eco_round.databinding.ActivityExpenseAddBinding;

public class ExpenseAdd extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private ActivityExpenseAddBinding binding;
    private Spinner spinnerVal;
    private String catSelected, title;
    private double price;
    private Uri imageUri;
    private static final int PICK_IMAGE_REQUEST = 1;

    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_add);

        binding = ActivityExpenseAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        db = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference("receipts");
        databaseReference = FirebaseDatabase.getInstance().getReference("receipts");
        spinnerVal = binding.spinnerCatOptions;
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.category_options,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVal.setAdapter(adapter);
        spinnerVal.setOnItemSelectedListener(this);
        openFileChooser();
    }

    private void openFileChooser(){
        binding.btnUpload.setOnClickListener(view -> {
            Intent i = new Intent();
            i.setType("image/*");
            i.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(i, PICK_IMAGE_REQUEST);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
        && data != null && data.getData() != null)
        {
            imageUri = data.getData();

            Picasso.with(this).load(imageUri).into(binding.ivReceipt);
        }
    }

    private String getFileExtension(Uri uri)
    {
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void uploadReceipt(String expense_ID)
    {
        String imgUrl = "";
        if(imageUri != null)
        {
            StorageReference fileRef = storageReference.child(expense_ID + "." + getFileExtension(imageUri));

            fileRef.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(ExpenseAdd.this, "Upload Successful", Toast.LENGTH_SHORT).show();
                            if(taskSnapshot.getMetadata() != null)
                            {
                                if(taskSnapshot.getMetadata().getReference() != null)
                                {
                                    Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String imgUrl = uri.toString();
                                            Receipt receipt = new Receipt(expense_ID, imgUrl);

                                            Log.i("PHOTO URL", imgUrl);
                                            db.collection("expenses").document(expense_ID).update("receiptID", imgUrl);
                                            String receiptId = expense_ID;
                                            databaseReference.child(receiptId).setValue(receipt);

                                        }
                                    });
                                }
                            }
                        }
                    });
        }
        else{
            Toast.makeText(this,"No File Selected", Toast.LENGTH_SHORT).show();
            
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        catSelected = adapterView.getItemAtPosition(i).toString();
        if(!binding.etAddExpenseCost.toString().equals("") && !binding.etAddExpenseTitle.toString().equals(""))
        {
            addExpenseOnClick();
        }
        else {
            Toast.makeText(getApplicationContext(), "Cost and Title cannot be empty", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        binding.btnAddSave.setOnClickListener(view -> {
            Toast.makeText(getApplicationContext(), "Category cannot be blank", Toast.LENGTH_SHORT).show();
        });
    }

    private void addExpenseOnClick(){
        binding.btnAddSave.setOnClickListener(view -> {
            title = binding.etAddExpenseTitle.getText().toString().trim();
            String tempPrice = binding.etAddExpenseCost.getText().toString().trim();
            price = Double.parseDouble(tempPrice);
            String docId = RandomStringGenerator.getAlphaNumericString(20);

            Expense added = new Expense(catSelected, Timestamp.now(),docId, price, docId, title, mAuth.getCurrentUser().getUid());

            db.collection("expenses").document(docId).set(added);
            uploadReceipt(docId);
            Toast.makeText(ExpenseAdd.this, "Expense Successfully Added", Toast.LENGTH_SHORT).show();
            Intent i = new Intent(ExpenseAdd.this, ListActivity.class);
            startActivity(i);
            finish();
        });
    }
}
