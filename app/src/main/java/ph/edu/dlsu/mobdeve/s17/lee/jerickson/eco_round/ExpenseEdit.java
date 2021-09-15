package ph.edu.dlsu.mobdeve.s17.lee.jerickson.eco_round;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import ph.edu.dlsu.mobdeve.s17.lee.jerickson.eco_round.databinding.ActivityExpenseEditBinding;

public class ExpenseEdit extends AppCompatActivity {
    private ActivityExpenseEditBinding binding;
    EditText title, price, category;
    ImageView recPhoto;
    FirebaseFirestore db;

    private Uri imageUri;
    private static final int PICK_IMAGE_REQUEST = 1;

    private StorageReference storageReference;
    private DatabaseReference databaseReference;

    private Expense expense;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_expense_edit);

        binding = ActivityExpenseEditBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        db = FirebaseFirestore.getInstance();
        this.title = binding.etExpenseTitle;
        this.price = binding.etExpenseCost;
        this.category = binding.etExpenseCat;
        this.recPhoto = binding.ivEditreceipt;
        expense = (Expense) getIntent().getSerializableExtra("expense");
        storageReference = FirebaseStorage.getInstance().getReference("receipts");
        databaseReference = FirebaseDatabase.getInstance().getReference("receipts");
        Intent intent = getIntent();
        title.setText(expense.getTitle());
        price.setText(expense.getPrice().toString());
        category.setText(expense.getCategory());
        Picasso.with(this).load(expense.getReceiptID()).fit().centerCrop().into(recPhoto);
        updateOnClick();
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

            Picasso.with(this).load(imageUri).into(binding.ivEditreceipt);
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
                            Toast.makeText(ExpenseEdit.this, "Upload Successful", Toast.LENGTH_SHORT).show();
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

    private void updateOnClick(){
        binding.btnUpdate.setOnClickListener(view -> {
            String newTitle, newPrice, newCategory, newRec;
            newTitle = title.getText().toString().trim();
            newPrice = price.getText().toString().trim();
            Double updPrice = Double.parseDouble(newPrice);
            newCategory = category.getText().toString().trim();
            newRec = expense.getReceiptID();
            /*
            * Insert input validation here
            *
            * */


            Expense updatedExpense = new Expense(newCategory, expense.getExpenseID(), updPrice, expense.getReceiptID(), newTitle, mAuth.getCurrentUser().getUid());
            Log.i("EXPENSE ID", expense.getExpenseID());
            uploadReceipt(newRec);
            db.collection("expenses").document(expense.getExpenseID()).update("category", newCategory, "price", updPrice,
                    "title", newTitle).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Toast.makeText(ExpenseEdit.this, "Expense Updated", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(ExpenseEdit.this, ListActivity.class);
                    startActivity(i);
                    finish();
                }
            });
        });
    }
}
