package com.hci.pocketai;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.hci.pocketai.helpers.ImgToTxtHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class ImgtoTxtActivity extends AppCompatActivity {

    private Button imgupButton;
    private ProgressBar loadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_imgto_txt);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        ImageView backImg = findViewById(R.id.back_im);


        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ImgtoTxtActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });



        imgupButton = findViewById(R.id.imgup);
        loadingIndicator = findViewById(R.id.progress2); // Initialize ProgressBar

        imgupButton.setOnClickListener(v -> {
            // Disable button and show ProgressBar
            imgupButton.setEnabled(false);
            loadingIndicator.setVisibility(View.VISIBLE);

            openImgFilePicker();
        });

    }


    private static final int IMAGE_PICK_CODE = 100;

    private void openImgFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_PICK_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri imageUri = data.getData();
                handleImageSelection(imageUri);
            }
        }
    }

    private void handleImageSelection(Uri imageUri) {
        try {
            TextView textView = findViewById(R.id.textb); // Replace with your TextView ID
            ProgressBar loadingIndicator = findViewById(R.id.progress2); // Replace with your ProgressBar ID
            Button processButton = findViewById(R.id.imgup); // Replace with your Button ID

            // Show loading indicator
            loadingIndicator.setVisibility(View.VISIBLE);
            processButton.setEnabled(false);

            File imageFile = createFileFromUri(imageUri);
            if (imageFile != null) {
                ImgToTxtHelper ocrHelper = new ImgToTxtHelper();
                ocrHelper.processImage(imageFile, extractedText -> {
                    runOnUiThread(() -> {
                        textView.setText(extractedText);

                        // Hide ProgressBar and enable button after processing is done
                        loadingIndicator.setVisibility(View.GONE);
                        processButton.setEnabled(true);
                    });
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to process the image file.", Toast.LENGTH_SHORT).show();

            ProgressBar loadingIndicator = findViewById(R.id.progress2); // Replace with your ProgressBar ID
            Button processButton = findViewById(R.id.imgup); // Replace with your Button ID

            loadingIndicator.setVisibility(View.GONE);
            processButton.setEnabled(true); // Enable the button even if there's an error
        }
    }

    private File createFileFromUri(Uri uri) throws Exception {
        String fileName = getFileName(uri);
        File tempFile = new File(getCacheDir(), fileName);

        try (InputStream inputStream = getContentResolver().openInputStream(uri);
             FileOutputStream outputStream = new FileOutputStream(tempFile)) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
        }
        return tempFile;
    }

    private String getFileName(Uri uri) {
        String fileName = "temp_image_file";
        Cursor cursor = null;
        try {
            cursor = getContentResolver().query(uri, null, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                if (nameIndex != -1) {
                    fileName = cursor.getString(nameIndex);
                }
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return fileName;
    }

}