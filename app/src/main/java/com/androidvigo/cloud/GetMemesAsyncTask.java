package com.androidvigo.cloud;


import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("UnusedDeclaration")
public class GetMemesAsyncTask extends AsyncTask<Void, Void, String> {

    public static final String API_ENDPOINT = "http://alltheragefaces.com/api/all/faces";

    private List<MemeEntity> mMemeEntityList;

    @Override
    protected void onPostExecute(String s) {

        super.onPostExecute(s);
    }

    @Override
    protected String doInBackground(Void... params) {

        // OMG Background thread
        String result = askWithHttpURLConnection();

        if (result != null) {

            try {

                JSONArray memeJSONArray = new JSONArray(result);
                mMemeEntityList = new ArrayList<>(memeJSONArray.length());

                for (int position = 0; position < memeJSONArray.length(); position++) {

                    MemeEntity memeEntity = parseMemeJSON(memeJSONArray.getJSONObject(position));
                    mMemeEntityList.add(memeEntity);
                }

                Log.d("[DEBUG]", "GetMemesAsyncTask doInBackground - Meme list size: "+mMemeEntityList.size());

            } catch (JSONException e) {

                Log.e("[ERROR]", "GetMemesAsyncTask, doInBackground (37)- " +
                    "Error obtaining the meme array: "+e.getMessage());
            }
        }

        return null;
    }

    public String askWithHttpURLConnection() {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;


        String memeString = null;
        try {

            URL memeURL = new URL(API_ENDPOINT);

            urlConnection = (HttpURLConnection) memeURL.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into the memeString
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();

            if (inputStream == null) {

                Log.e("[ERROR]", "MainActivity, askWithHttpURLConnection (47)- " +
                    "The stream was null");

                return null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;

            while ((line = reader.readLine()) != null) {

                // Beautify JSON
                buffer.append(line + "\n");
            }

            if (buffer.length() != 0) {

                memeString = buffer.toString();

            } else {

                Log.e("[ERROR]", "MainActivity, askWithHttpURLConnection (70)- " +
                    "Empty buffer");
            }

        } catch (MalformedURLException e) {

            Log.e("[ERROR]", "MainActivity, askWithHttpURLConnection (36)- " +
                "memeURL is malformed: " + e.getMessage());

        } catch (IOException e) {

            Log.e("[ERROR]", "MainActivity, askWithHttpURLConnection (45)- " +
                "IOException: " + e.getMessage());

        } finally {

            if (urlConnection != null) {

                urlConnection.disconnect();
            }

            if (reader != null) {

                try {

                    reader.close();
                    ;

                } catch (final IOException e) {

                    Log.e("[ERROR]", "MainActivity, askWithHttpURLConnection (99)- " +
                        "Error closing the reader");
                }
            }
        }

        return memeString;
    }

    public MemeEntity parseMemeJSON(JSONObject jsonObject) {

        MemeEntity memeEntity = new MemeEntity();

        try {

            memeEntity.setTitle(jsonObject.getString("title"));
            memeEntity.setEmotion(jsonObject.getString("emotion"));
            memeEntity.setCanonical(jsonObject.getString("canonical"));
            memeEntity.setCanonical(jsonObject.getString("created"));
            memeEntity.setCategoryfk(jsonObject.getString("categoryfk"));
            memeEntity.setTags(jsonObject.getString("tags"));
            memeEntity.setViews(jsonObject.getString("views"));
            memeEntity.setMoreinformation(jsonObject.getString("moreinformation"));
            memeEntity.setIs_censored(jsonObject.getString("is_censored"));
            memeEntity.setId(jsonObject.getString("id"));
            memeEntity.setWeekly_views(jsonObject.getString("weekly_views"));
            memeEntity.setPng(jsonObject.getString("png"));
            memeEntity.setJpg(jsonObject.getString("jpg"));
            memeEntity.setLargepng(jsonObject.getString("largepng"));
            memeEntity.setSvg(jsonObject.getString("svg"));

        } catch (JSONException e) {

            memeEntity = null;

            Log.e("[ERROR]", "GetMemesAsyncTask, parseMemeJSON (139)- " +
                "Error: " + e.getMessage());
        }

        return memeEntity;
    }

}
