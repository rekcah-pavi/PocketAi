package com.hci.pocketai;

import android.app.AlertDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.hci.pocketai.helpers.TextToSpeechHelper;

import java.io.File;

public class TextToVoiceActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private File audioFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_text_to_voice);

        // Initialize views
        Button runButton = findViewById(R.id.runbutton2);
        EditText queryBox = findViewById(R.id.querybox2);
        ImageView backImg = findViewById(R.id.backimg5);
        ImageView cancelButton = findViewById(R.id.clear2);
        ImageView playPauseIcon = findViewById(R.id.play_pause_icon);
        ImageView downloadIcon = findViewById(R.id.download_icon);
        ProgressBar loadingIndicator = findViewById(R.id.progress3);

        Spinner languageSpinner = findViewById(R.id.language_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.languages,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        languageSpinner.setAdapter(adapter);



        TextToSpeechHelper ttsHelper = new TextToSpeechHelper();

        // Clear text
        cancelButton.setOnClickListener(v -> queryBox.setText(""));

        // Navigate back
        backImg.setOnClickListener(v -> {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }
            startActivity(new Intent(TextToVoiceActivity.this, MainActivity.class));
            finish();
        });

        // Generate audio from text
        runButton.setOnClickListener(v -> {
            String selectedLanguage = languageSpinner.getSelectedItem().toString();

            // map the selected language to its language code
            String languageCode = "";

            switch (selectedLanguage) {
                case "English":
                    languageCode = "en";
                    break;
                case "Tamil":
                    languageCode = "ta";
                    break;
                case "Sinhala":
                    languageCode = "si";
                    break;
            }

            playPauseIcon.setVisibility(View.GONE);
            downloadIcon.setVisibility(View.GONE);
            String text = queryBox.getText().toString().trim();
            if (text.isEmpty()) {
                Toast.makeText(this, "Enter text to generate audio!", Toast.LENGTH_SHORT).show();
                return;
            }
            loadingIndicator.setVisibility(View.VISIBLE);

            audioFile = new File(getCacheDir(), "output_audio.mp3");
            ttsHelper.textToSpeech(text, languageCode, audioFile, success -> {
                if (success) {
                    runOnUiThread(() -> {
                        playPauseIcon.setVisibility(View.VISIBLE);
                        downloadIcon.setVisibility(View.VISIBLE);
                        playPauseIcon.setImageResource(R.drawable.ic_play_arrow);
                        runOnUiThread(() -> Toast.makeText(this, "Audio generated successfully!", Toast.LENGTH_SHORT).show());
                    });
                } else {
                    runOnUiThread(() -> Toast.makeText(this, "Audio generation failed!", Toast.LENGTH_SHORT).show());
                }
                loadingIndicator.setVisibility(View.GONE);
            });
        });

        // Play or pause audio
        playPauseIcon.setOnClickListener(v -> {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                playPauseIcon.setImageResource(R.drawable.ic_play_arrow);
            } else {
                playAudio(audioFile, playPauseIcon);
            }
        });

        // Download audio
        downloadIcon.setOnClickListener(v -> downloadAudio(audioFile));
    }

    private void playAudio(File audioFile, ImageView playPauseIcon) {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.stop();
                mediaPlayer.release();
            }

            mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(audioFile.getAbsolutePath());
            mediaPlayer.prepare();
            mediaPlayer.start();

            mediaPlayer.setOnCompletionListener(mp -> {
                playPauseIcon.setImageResource(R.drawable.ic_play_arrow);
            });

            playPauseIcon.setImageResource(R.drawable.ic_pause);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to play audio!", Toast.LENGTH_SHORT).show();
        }
    }

    private void downloadAudio(File audioFile) {
        try {
            Uri fileUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", audioFile);

            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("audio/mp3");
            intent.putExtra(Intent.EXTRA_STREAM, fileUri);
            intent.putExtra(Intent.EXTRA_TITLE, "Download Audio");
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

            startActivity(Intent.createChooser(intent, "Download Audio"));
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to share/download audio!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }
}
