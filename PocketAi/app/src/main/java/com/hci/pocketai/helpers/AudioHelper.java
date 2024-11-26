package com.hci.pocketai.helpers;


import android.os.AsyncTask;

import com.hci.pocketai.BuildConfig;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AudioHelper {

    // Cloudflare API token and endpoint for audio processing (Whisper API)
    private static final String ACCOUNT_ID = BuildConfig.CLOUDFLARE_ACCOUNT_ID;
    private static final String API_URL = "https://api.cloudflare.com/client/v4/accounts/"+ACCOUNT_ID+"/ai/run/@cf/openai/whisper-tiny-en";
    private static final String API_TOKEN = "Bearer "+ BuildConfig.CLOUDFLARE_API;

    // Function to send audio data to the API
    public void processAudio(File audioFile, AudioCallback callback) {
        new ProcessAudioTask(callback).execute(audioFile);
    }

    // AsyncTask to handle API call in the background
    private static class ProcessAudioTask extends AsyncTask<File, Void, String> {
        private AudioCallback callback;

        public ProcessAudioTask(AudioCallback callback) {
            this.callback = callback;
        }

        @Override
        protected String doInBackground(File... params) {
            File audioFile = params[0];
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                // Prepare URL and connection
                URL url = new URL(API_URL);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Authorization", API_TOKEN);
                connection.setRequestProperty("Content-Type", "application/octet-stream");
                connection.setDoOutput(true);

                // Send binary audio data
                BufferedOutputStream outputStream = new BufferedOutputStream(connection.getOutputStream());
                FileInputStream inputStream = new FileInputStream(audioFile);
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.flush();
                inputStream.close();

                // Get response from the server
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    // Parse the response JSON and get the transcribed text
                    JSONObject responseJson = new JSONObject(response.toString());
                    JSONObject result = responseJson.getJSONObject("result");
                    String transcribedText = result.getString("text");

                    return transcribedText;

                } else {
                    return null;  // Request failed
                }

            } catch (Exception e) {
                e.printStackTrace();
                return null;  // Error occurred
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (Exception ignored) {
                    }
                }
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }

        @Override
        protected void onPostExecute(String transcribedText) {
            if (callback != null) {
                callback.onAudioProcessed(transcribedText);
            }
        }
    }

    // Callback interface to handle the transcribed text result
    public interface AudioCallback {
        void onAudioProcessed(String transcribedText);
    }
}
