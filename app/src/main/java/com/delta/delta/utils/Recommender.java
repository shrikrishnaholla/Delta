package com.delta.delta.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URLEncoder;

/**
 * Created by shrikrishna on 12/10/14.
 */
public class Recommender {
    String email;
    JSONObject otherFields;
    ProgressDialog pd;
    Context ctx;
    public Recommender(Context context, JSONObject userProfile) {
        email=null; otherFields=null;
        ctx = context;
        try {
            email = userProfile.keys().next().toString();
            otherFields = userProfile.getJSONObject(email);
        } catch (Exception e) {
            Log.e("userProfileErrorInRecommender", "Cant separate email and other values");
        }
        try {
            new GetRecommendationAsyncTask().execute("http://delta.ngrok.com/user/" + URLEncoder.encode(email, "utf-8") + "/reco");
        } catch (Exception e) {
            Log.e("URLEncoderError", "Couldn't encode email " + email, e);
        }
    }

    private class GetRecommendationAsyncTask extends AsyncTask<String, Void, Boolean> {
        JSONObject recommendedUserProfile;

        @Override
        protected void onPreExecute(){

            pd = new ProgressDialog(ctx);
            pd = ProgressDialog.show(ctx, "", "Finding a person for you to meet",true);
        }

        @Override
        protected Boolean doInBackground(String... urls) {
            recommendedUserProfile = new JSONObject();
            if(urls.length>0){
                String url = urls[0];
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpget = new HttpGet(url);
                try {
                    HttpResponse response = httpClient.execute(httpget);
                    if (response != null) {
                        //If status is OK 200
                        if (response.getStatusLine().getStatusCode() == 200) {
                            String result = EntityUtils.toString(response.getEntity());
                            Log.i("Recommended user", result);
                            //Convert the string result to a JSON Object
                            JSONObject resultJson = new JSONObject(result);
                            Log.i("JSON Recommended User profile!!!", "" + resultJson);
                            //Extract data from JSON Response
                            try {
                                JSONObject valueJSON = new JSONObject(resultJson.getString("data"));
                                recommendedUserProfile.put(resultJson.getString("reco_user_email"), valueJSON);
                            } catch (Exception e) {
                                Log.e("JSONGetError", "Failed to get data and email of recommended user");
                            }

                            Log.i("Recommended User", recommendedUserProfile.toString());

                            return true;
                        }
                    }
                } catch (IOException e) {
                    Log.e("Authorize", "Error Http response " + e.getLocalizedMessage());
                } catch (ParseException e) {
                    Log.e("Authorize", "Error Parsing Http response " + e.getLocalizedMessage());
                } catch (JSONException e) {
                    Log.e("Authorize", "Error Parsing Http response " + e.getLocalizedMessage());
                }
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean status){
            if(pd!=null && pd.isShowing()){
                pd.dismiss();
            }
            if(status){
                Log.d("STATUS", "Successfully got recommended user profile!!!!!");
                Log.i("Recommended User", recommendedUserProfile.toString());
                //If everything went Ok, change to another activity.
//                Intent startProfileActivity = new Intent(MainActivity.this, ProfileActivity.class);
//                MainActivity.this.startActivity(startProfileActivity);
            }
        }

    };
}
