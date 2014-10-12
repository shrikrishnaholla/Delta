package com.delta.delta;

import android.app.Activity;
import android.content.Intent;
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
import com.quickblox.core.QBCallback;
import com.quickblox.core.QBCallbackImpl;
import com.quickblox.core.QBSettings;
import com.quickblox.core.result.Result;
import com.quickblox.module.auth.QBAuth;
import com.quickblox.module.auth.result.QBSessionResult;
import com.quickblox.module.chat.QBChatService;
import com.quickblox.module.chat.listeners.ChatMessageListener;
import com.quickblox.module.chat.listeners.SessionCallback;
import com.quickblox.module.chat.smack.SmackAndroid;
import com.quickblox.module.chat.xmpp.QBPrivateChat;
import com.quickblox.module.users.QBUsers;
import com.quickblox.module.users.model.QBUser;

import org.jivesoftware.smack.packet.Message;

import java.util.ArrayList;


public class ChatActivity extends Activity {

    public ArrayList<ChatMessage> messages;
    public ImageButton btnSendChat;
    public EditText message;
    public ListView chatList;
    public ChatAdapter chatAdapter;

    public QBUser user;
    public QBPrivateChat chat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        SmackAndroid.init(this);
        QBSettings.getInstance().fastConfigInit("15301", "kQVLuM853FmJRXW", "uXrLkmMgdCAfENu");

        user = new QBUser("abc", "12345678");
        QBAuth.createSession(user, new QBCallbackImpl() {
            @Override
            public void onComplete(Result result) {
                if (result.isSuccess()) {
                    QBSessionResult res = (QBSessionResult) result;
                    user.setId(res.getSession().getUserId());
                    QBUsers.signIn(user, new QBCallback() {
                        @Override
                        public void onComplete(Result result) {
                            if (result.isSuccess()) {
//            ((App)getApplication()).setQbUser(user);
                                QBChatService.getInstance().loginWithUser(user, new SessionCallback() {
                                    @Override
                                    public void onLoginSuccess() {
                                        chat = QBChatService.getInstance().createChat();
                                        chat.addChatMessageListener(new ChatMessageListener() {
                                            Message msg;
                                            @Override
                                            public void processMessage(Message message) {
                                                msg = message;
                                                Log.d("chat activity onCreate", "Messages: " + message.getBody());
                                            }

                                            @Override
                                            public boolean accept(Message.Type messageType) {
                                                switch (messageType) {
                                                    case chat:
                                                        ChatMessage chatMessage = new ChatMessage(msg.getBody());
                                                        chatAdapter.add(chatMessage);
                                                        chatAdapter.notifyDataSetChanged();
                                                        return true; // process 1-1 chat messages
                                                    default:
                                                        return false;
                                                }
                                            }
                                        });
                                    }

                                    @Override
                                    public void onLoginError(String e) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onComplete(Result result, Object o) {

                        }
                    });
                } else {
                    Log.e("login activity onCreate onComplete", "Errors " + result.getErrors().toString());
                }
            }

            @Override
            public void onComplete(Result result, Object o) {
            }
        });

        message = (EditText) findViewById(R.id.etMessage);
        btnSendChat = (ImageButton) findViewById(R.id.btnSendChat);
        chatList = (ListView) findViewById(R.id.chatList);
        messages = new ArrayList<ChatMessage>();
        chatAdapter = new ChatAdapter(ChatActivity.this, messages);
        chatList.setAdapter(chatAdapter);

        btnSendChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatMessage chatMessage = new ChatMessage(message.getText().toString());
                try {
                    chat.sendMessage(1687571, chatMessage.message);
                } catch(Exception e) {

                }
                chatAdapter.add(chatMessage);
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
