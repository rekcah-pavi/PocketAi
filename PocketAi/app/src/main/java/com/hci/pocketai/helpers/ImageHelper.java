package com.hci.pocketai.helpers;

import android.os.AsyncTask;

import com.hci.pocketai.BuildConfig;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageHelper {

    // Cloudflare API token and endpoint
    private static final String ACCOUNT_ID = BuildConfig.CLOUDFLARE_ACCOUNT_ID;
    private static final String API_URL = "https://api.cloudflare.com/client/v4/accounts/"+ACCOUNT_ID+"/ai/run/@cf/black-forest-labs/flux-1-schnell";
    private static final String API_TOKEN = "Bearer "+ BuildConfig.CLOUDFLARE_API;

    // Function to generate image
    public void generateImage(String prompt, int numSteps, ImageCallback callback) {
        new GenerateImageTask(callback).execute(prompt, String.valueOf(numSteps));
    }

    // AsyncTask to handle API call in the background
    private static class GenerateImageTask extends AsyncTask<String, Void, String> {
        private ImageCallback callback;

        public GenerateImageTask(ImageCallback callback) {
            this.callback = callback;
        }

        @Override
        protected String doInBackground(String... params) {
            String prompt = params[0];
            int numSteps = Integer.parseInt(params[1]);
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                // Prepare URL and connection
                URL url = new URL(API_URL);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Authorization", API_TOKEN);
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                // Create JSON payload
                JSONObject data = new JSONObject();
                data.put("prompt", prompt);
                data.put("num_steps", numSteps);

                // Write JSON data to request body
                Writer writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), "UTF-8"));
                writer.write(data.toString());
                writer.close();

                // Get response from the server
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    // Parse the response JSON and get the base64 image string
                    JSONObject responseJson = new JSONObject(response.toString());
                    JSONObject result = responseJson.getJSONObject("result");
                    String imageBase64 = result.getString("image");

                    return imageBase64;

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
        protected void onPostExecute(String base64Image) {
            if (callback != null) {
                callback.onImageGenerated(base64Image);
            }
        }
    }

    // Callback interface to handle the base64 result
    public interface ImageCallback {
        void onImageGenerated(String base64Image);
    }
}
