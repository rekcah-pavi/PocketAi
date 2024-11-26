package com.hci.pocketai.helpers;

import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class TextToSpeechHelper {

    // gTTS API endpoint
    private static final String API_URL = "https://translate.google.com/translate_tts";
    private static final String USER_AGENT = "Mozilla/5.0";
    private static final int MAX_TEXT_LENGTH = 200; // gTTS limit

    // Function to convert text to speech
    public void textToSpeech(String text, String language, File outputFile, TTSCallback callback) {
        List<String> textChunks = splitTextIntoChunks(text, MAX_TEXT_LENGTH);
        new TextToSpeechTask(callback, outputFile, textChunks, language).execute();
    }

    // Split the text into chunks of the specified maximum length
    private List<String> splitTextIntoChunks(String text, int maxLength) {
        List<String> chunks = new ArrayList<>();
        int start = 0;
        while (start < text.length()) {
            int end = Math.min(start + maxLength, text.length());
            // Ensure we do not split words by looking for the last space
            if (end < text.length() && text.charAt(end) != ' ') {
                end = text.lastIndexOf(' ', end);
                if (end == -1 || end <= start) end = Math.min(start + maxLength, text.length());
            }
            chunks.add(text.substring(start, end));
            start = end + 1; // Move past the space
        }
        return chunks;
    }

    // AsyncTask to handle API calls in the background
    private static class TextToSpeechTask extends AsyncTask<Void, Void, Boolean> {
        private TTSCallback callback;
        private File outputFile;
        private List<String> textChunks;
        private String language;

        public TextToSpeechTask(TTSCallback callback, File outputFile, List<String> textChunks, String language) {
            this.callback = callback;
            this.outputFile = outputFile;
            this.textChunks = textChunks;
            this.language = language;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            HttpURLConnection connection = null;
            BufferedOutputStream output = null;

            try {
                // Open the output file for writing
                output = new BufferedOutputStream(new FileOutputStream(outputFile));

                for (String text : textChunks) {
                    try {
                        // Construct API URL with parameters
                        String urlStr = API_URL + "?ie=UTF-8&client=tw-ob&q=" +
                                java.net.URLEncoder.encode(text, "UTF-8") +
                                "&tl=" + language;
                        URL url = new URL(urlStr);

                        // Prepare connection
                        connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");
                        connection.setRequestProperty("User-Agent", USER_AGENT);
                        connection.setDoInput(true);

                        // Check response
                        int responseCode = connection.getResponseCode();
                        if (responseCode == HttpURLConnection.HTTP_OK) {
                            // Download the audio chunk
                            InputStream input = new BufferedInputStream(connection.getInputStream());
                            byte[] buffer = new byte[1024];
                            int bytesRead;
                            while ((bytesRead = input.read(buffer)) != -1) {
                                output.write(buffer, 0, bytesRead);
                            }
                            input.close();
                        } else {
                            return false; // Request failed
                        }
                    } finally {
                        if (connection != null) {
                            connection.disconnect();
                        }
                    }
                }

                output.flush();
                return true;

            } catch (Exception e) {
                e.printStackTrace();
                return false; // Error occurred
            } finally {
                try {
                    if (output != null) output.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (callback != null) {
                callback.onTTSCompleted(success);
            }
        }
    }

    // Callback interface to handle TTS result
    public interface TTSCallback {
        void onTTSCompleted(boolean success);
    }
}
