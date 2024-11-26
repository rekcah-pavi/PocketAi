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

import com.hci.pocketai.helpers.AudioHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class VoiceToTxtActivity extends AppCompatActivity {
    private static final int AUDIO_PICK_CODE = 1001;
    private Button voiceupButton;
    private ProgressBar loadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_voice_to_txt);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        ImageView backImg = findViewById(R.id.back_bt);


        backImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(VoiceToTxtActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        voiceupButton = findViewById(R.id.voiceup);
        loadingIndicator = findViewById(R.id.progress); // Initialize ProgressBar

        voiceupButton.setOnClickListener(v -> {
            // Disable button and show ProgressBar
            voiceupButton.setEnabled(false);
            loadingIndicator.setVisibility(View.VISIBLE);

            openAudioFilePicker();
        });

    }




    private void openAudioFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("audio/*");
        startActivityForResult(intent, AUDIO_PICK_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == AUDIO_PICK_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri audioUri = data.getData();
                handleAudioSelection(audioUri);
            }
        }
    }

    private void handleAudioSelection(Uri audioUri) {
        try {
            TextView textView2 = findViewById(R.id.textb);
            File audioFile = createFileFromUri(audioUri);
            if (audioFile != null) {
                AudioHelper audioHelper = new AudioHelper();
                audioHelper.processAudio(audioFile, transcribedText -> {
                    runOnUiThread(() -> {
                        textView2.setText(transcribedText);

                        // Hide ProgressBar and enable button after processing is done
                        loadingIndicator.setVisibility(View.GONE);
                        voiceupButton.setEnabled(true);
                    });
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to process the audio file.", Toast.LENGTH_SHORT).show();
            loadingIndicator.setVisibility(View.GONE);
            voiceupButton.setEnabled(true); // Enable the button even if there's an error
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
        String fileName = "temp_audio_file";
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
