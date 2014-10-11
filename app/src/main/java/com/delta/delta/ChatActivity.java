package com.delta.delta;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.delta.delta.utils.ChatAdapter;
import com.delta.delta.utils.ChatMessage;

import java.util.ArrayList;


public class ChatActivity extends Activity {

    public ArrayList<ChatMessage> messages;
    public ImageButton btnSendChat;
    public EditText message;
    public ListView chatList;
    public ChatAdapter chatAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        message = (EditText) findViewById(R.id.etMessage);
        btnSendChat = (ImageButton) findViewById(R.id.btnSendChat);
        chatList = (ListView) findViewById(R.id.chatList);
        messages = new ArrayList<ChatMessage>();
        chatAdapter = new ChatAdapter(ChatActivity.this, messages);
        chatList.setAdapter(chatAdapter);

        btnSendChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chatAdapter.add(new ChatMessage(message.getText().toString()));
                chatAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.chat, menu);
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
}
