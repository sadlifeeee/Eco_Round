package ph.edu.dlsu.mobdeve.s17.lee.jerickson.eco_round;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.zxing.WriterException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;
import androidmads.library.qrgenearator.QRGSaver;
import ph.edu.dlsu.mobdeve.s17.lee.jerickson.eco_round.databinding.ActivityGenerateQrBinding;

public class generateQR extends AppCompatActivity {

    private ActivityGenerateQrBinding binding;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db;
    private String userID = mAuth.getCurrentUser().getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_qr);
        binding = ActivityGenerateQrBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();




    }

    private void init() {
        cancelOnClick();
        generateQROnClick();
        db = FirebaseFirestore.getInstance();
        ActivityCompat.requestPermissions(generateQR.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        ActivityCompat.requestPermissions(generateQR.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
    }

    private void generateQROnClick() {
        binding.btnGenerateQR.setOnClickListener(view -> {
            DocumentReference documentReference = db.collection("users").document(userID);

            documentReference.addSnapshotListener(this, (value, error) -> {
                String email , password;

                email = value.getString("email");
                password = value.getString("hashPassword");
                String data = email + " " + password;
                QRGEncoder qrgEncoder = new QRGEncoder(data, null , QRGContents.Type.TEXT, 200);

                Bitmap qrBits = qrgEncoder.getBitmap();
                binding.ivGeneratedQR.setImageBitmap(qrBits);
            });

        });

        binding.btnSave.setOnClickListener(view -> {
            /*
            try {
                File sdCard = Environment.getExternalStorageDirectory();
                File dir = new File(sdCard.getAbsolutePath() + "/SaveImages");
                if(!dir.exists())
                    dir.mkdir();


                BitmapDrawable drawable = (BitmapDrawable) binding.ivGeneratedQR.getDrawable();
                Bitmap bitmap = drawable.getBitmap();

                File file = new File(dir,userID +".jpg");

                FileOutputStream outputStream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
                Toast.makeText(generateQR.this,"Successfuly Saved",Toast.LENGTH_SHORT).show();
                outputStream.flush();
                outputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            */

            BitmapDrawable drawable = (BitmapDrawable) binding.ivGeneratedQR.getDrawable();
            Bitmap bitmap = drawable.getBitmap();


            String savePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) + "/Camera/";
            Log.e("PANSININ" , savePath);
            QRGSaver qrgSaver = new QRGSaver();

            boolean save = qrgSaver.save(savePath, userID, bitmap, QRGContents.ImageType.IMAGE_JPEG);

            String result = save ? "Image Successfully Saved" : "Image Not Saved";
            Toast.makeText(getApplicationContext(), result ,Toast.LENGTH_SHORT).show();
        });

    }

    private void cancelOnClick() {
        binding.btnCancel.setOnClickListener(view -> {
            Intent i = new Intent(generateQR.this, SettingsActivity.class);
            startActivity(i);
            finish();
        });
    }
}