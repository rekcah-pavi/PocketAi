package com.hci.pocketai;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.hci.pocketai.helpers.CustomHelper;
import com.hci.pocketai.helpers.ImageHelper;

import java.io.OutputStream;

public class TxtToImgActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_txt_to_img);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button run_button = findViewById(R.id.runbutton);
        EditText queryBox = findViewById(R.id.querybox);

        ImageView backImg = findViewById(R.id.backimg);
        ImageView cancelButton = findViewById(R.id.clear);
        ImageView imageview = findViewById(R.id.photo);


        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                queryBox.setText("");
            }
        });

        run_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hide the keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                View currentFocus = getCurrentFocus();
                if (currentFocus != null) {
                    imm.hideSoftInputFromWindow(currentFocus.getWindowToken(), 0);
                }

                // Disable the button to prevent multiple clicks
                run_button.setEnabled(false);

                // Show loading indicator
                ProgressBar loadingIndicator = findViewById(R.id.loadingIndicator);
                loadingIndicator.setVisibility(View.VISIBLE);

                String query = queryBox.getText().toString();


                ImageHelper generator = new ImageHelper();
                generator.generateImage(query, 6, new ImageHelper.ImageCallback() {
                    @Override
                    public void onImageGenerated(String base64Image) {
                        // Hide loading indicator
                        loadingIndicator.setVisibility(View.GONE);

                        // Re-enable the button
                        run_button.setEnabled(true);

                        if (base64Image != null) {
                            byte[] decodedString = android.util.Base64.decode(base64Image, android.util.Base64.DEFAULT);
                            Bitmap decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
                            imageview.setImageBitmap(decodedByte);
                        } else {
                            CustomHelper.showAlert(TxtToImgActivity.this, "error", "Failed to generate image.");
                        }
                    }
                });
            }
        });

        imageview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create a dialog to ask for confirmation
                new AlertDialog.Builder(TxtToImgActivity.this)
                        .setTitle("Save Image")
                        .setMessage("Are you sure you want to save this image?")
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Proceed with saving the image
                                BitmapDrawable drawable = (BitmapDrawable) imageview.getDrawable();
                                Bitmap bitmap = drawable.getBitmap();

                                // Prepare ContentValues to describe the image's metadata
                                ContentValues values = new ContentValues();
                                String txt = queryBox.getText().toString();
                                String trimmedTxt = txt.length() > 50 ? txt.substring(0, 50) : txt;
                                String fileName = trimmedTxt.replaceAll(" ", "_");

                                fileName += "_" + System.currentTimeMillis() + ".jpg";

                                values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
                                values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                                values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES);

                                // Insert the image using MediaStore
                                OutputStream outputStream;
                                try {
                                    // Insert image details into MediaStore and get the Uri for the new image file
                                    android.net.Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                                    if (uri != null) {
                                        // Open output stream to write image data
                                        outputStream = getContentResolver().openOutputStream(uri);
                                        // Compress the bitmap to JPEG and write it to the output stream
                                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                                        outputStream.close();
                                        Toast.makeText(TxtToImgActivity.this, "Image saved to Pictures", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(TxtToImgActivity.this, "Failed to save image", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    Toast.makeText(TxtToImgActivity.this, "Error while saving image", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("Cancel", null) // Do nothing on cancel
                        .show();
            }
        });

        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TxtToImgActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}