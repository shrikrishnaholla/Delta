package com.delta.delta;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Button;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;0
import com.delta.delta.utils.Recommender;

import org.json.JSONObject;

import java.io.File;
import com.quickblox.core.QBCallbackImpl;
import com.quickblox.core.QBSettings;
import com.quickblox.core.result.Result;
import com.quickblox.module.auth.QBAuth;
import com.quickblox.module.auth.result.QBSessionResult;
import com.quickblox.module.chat.QBChatService;
import com.quickblox.module.chat.smack.SmackAndroid;
import com.quickblox.module.users.model.QBUser;


public class LoginActivity extends Activity {

    public ImageButton loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        File f = new File(
                "/data/data/com.delta.delta/shared_prefs/user_info.xml");
        if (f.exists()) {
            Log.d("TAG", "SharedPreferences user_info : exist");
            SharedPreferences sharedpreferences = getSharedPreferences("user_info", 0);
            if(sharedpreferences.contains("accessToken") && sharedpreferences.contains("userProfile")) {
                String userProfileString = sharedpreferences.getString("userProfile", null);
                if (userProfileString != null) {
                    try {
                        new Recommender(this, new JSONObject(userProfileString));
                    } catch (Exception e) {
                        Log.e("JSON parser Exception", "Failed to parse user profile string : " + userProfileString, e);
                    }
                }
            }
        }

        loginButton = (ImageButton) findViewById(R.id.btnLogin);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), LinkedInAuthActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
