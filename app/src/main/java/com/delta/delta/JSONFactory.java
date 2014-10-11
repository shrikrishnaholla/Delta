package com.delta.delta;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by shrikrishna on 11/10/14.
 */
public class JSONFactory {
    public static JSONObject getUserJSONProfile(JSONObject sourceJSON, String accessToken) {
        JSONObject userProfileJSON = new JSONObject();
        JSONObject valueJSON = new JSONObject();
        valueJSON = listTransform(sourceJSON, valueJSON, "interests");

        valueJSON = hashTransform(sourceJSON, valueJSON, "languages", "values", "language", "name");
        valueJSON = hashTransform(sourceJSON, valueJSON, "skills", "values", "skill", "name");
        valueJSON = hashTransform(sourceJSON, valueJSON, "groupMemberships", "values", "group", "name");
        valueJSON = hashTransform(sourceJSON, valueJSON, "threeCurrentPositions", "values", "company", "name");

        try {
            valueJSON.put("accessToken", accessToken);
        } catch (Exception e) {
            Log.e("accessTokenException", "Exception putting accessToken");
        }

        try {
            valueJSON.put("firstName", sourceJSON.get("firstName"));
            valueJSON.put("lastName", sourceJSON.get("lastName"));
        } catch (Exception e) {
            Log.e("nameException", "Exception putting names");
        }

        try {
            valueJSON.put("bucket", Bucketify.getBucket(((JSONObject) ((JSONArray) ((JSONObject) sourceJSON.get("threeCurrentPositions")).get("values")).get(0)).get("title").toString().split(" ")[0]));
        } catch (Exception e) {
            Log.e("nameException", "Exception putting into buckets");
        }

        try {
            valueJSON.put("accessToken", accessToken);
        } catch (Exception e) {
            Log.e("accessTokenException", "Exception putting accessToken");
        }

        Log.i("ValueJSON", valueJSON.toString());


        try {
            userProfileJSON.put(sourceJSON.get("emailAddress").toString(), valueJSON);
        } catch (Exception e) {
            Log.e("EMAILException", "Exception putting email");
        }


        return userProfileJSON;
    }

    public static JSONObject listTransform(JSONObject sourceJSON, JSONObject resultJSON, String key) {
        try {
            String[] valueArray = sourceJSON.get(key).toString().split(",");
            for (int index=0; index < valueArray.length; index++) {
                valueArray[index] = valueArray[index].trim();
            }
            JSONArray jsonifiedValueArray = new JSONArray(Arrays.asList(valueArray));
            resultJSON.put(key, jsonifiedValueArray);
        } catch (Exception e) {
            Log.e("AssociationException","Association not found");
        }
        return resultJSON;
    }

    public static JSONObject hashTransform(JSONObject sourceJSON, JSONObject resultJSON, String key, String arrayKey, String innerKeyL1, String innerKeyL2) {
        try {
            JSONObject valueAsJSONObject = ((JSONObject) sourceJSON.get(key));
            JSONArray valuesArray = valueAsJSONObject.getJSONArray(arrayKey);
            ArrayList<String> valuesArrayList = new ArrayList<String>(valuesArray.length());
            for (int index = 0; index < valuesArray.length(); index++) {
                JSONObject value = valuesArray.getJSONObject(index);
                String valueName = ((JSONObject) value.get(innerKeyL1)).get(innerKeyL2).toString();
                valuesArrayList.add(valueName);
            }

            JSONArray jsonifiedValueArray = new JSONArray(valuesArrayList);
            resultJSON.put(key, jsonifiedValueArray);
        } catch (Exception e) {
            Log.e("hashTransformException","hashTransform failed", e);
        }
        return resultJSON;
    }
}
