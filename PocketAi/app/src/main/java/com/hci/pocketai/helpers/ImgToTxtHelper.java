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

public class ImgToTxtHelper {

    // OCR.space API endpoint and key
    private static final String API_URL = BuildConfig.OCR_URL;
    private static final String API_KEY = BuildConfig.OCR_API_KEY;

    // Function to send image data to OCR.space API for OCR
    public void processImage(File imageFile, OCRCallback callback) {
        new ProcessImageTask(callback).execute(imageFile);
    }

    // AsyncTask to handle the API call in the background
    private static class ProcessImageTask extends AsyncTask<File, Void, String> {
        private OCRCallback callback;

        public ProcessImageTask(OCRCallback callback) {
            this.callback = callback;
        }

        @Override
        protected String doInBackground(File... params) {
            File imageFile = params[0];
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                // Prepare URL and connection
                URL url = new URL(API_URL);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("apikey", API_KEY); // Add API key to the header
                connection.setRequestProperty("Content-Type", "multipart/form-data");
                connection.setDoOutput(true);

                // Build multipart form-data request
                String boundary = "*****" + System.currentTimeMillis() + "*****";
                connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

                BufferedOutputStream outputStream = new BufferedOutputStream(connection.getOutputStream());
                outputStream.write(("--" + boundary + "\r\n").getBytes());
                outputStream.write(("Content-Disposition: form-data; name=\"file\"; filename=\"" + imageFile.getName() + "\"\r\n").getBytes());
                outputStream.write(("Content-Type: application/octet-stream\r\n\r\n").getBytes());

                // Write file content
                FileInputStream inputStream = new FileInputStream(imageFile);
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                inputStream.close();

                // End of multipart
                outputStream.write(("\r\n--" + boundary + "--\r\n").getBytes());
                outputStream.flush();

                // Get response from the server
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    // Parse JSON response to extract the OCR text
                    JSONObject responseJson = new JSONObject(response.toString());
                    if (responseJson.getBoolean("IsErroredOnProcessing")) {
                        return null; // Error in processing
                    }
                    JSONObject parsedResults = responseJson.getJSONArray("ParsedResults").getJSONObject(0);
                    String extractedText = parsedResults.getString("ParsedText");

                    return extractedText;

                } else {
                    return null; // Request failed
                }

            } catch (Exception e) {
                e.printStackTrace();
                return null; // Error occurred
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
        protected void onPostExecute(String extractedText) {
            if (callback != null) {
                callback.onOCRProcessed(extractedText);
            }
        }
    }

    // Callback interface to handle the extracted text result
    public interface OCRCallback {
        void onOCRProcessed(String extractedText);
    }
}
