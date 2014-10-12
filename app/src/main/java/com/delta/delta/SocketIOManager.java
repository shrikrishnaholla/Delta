package com.delta.delta;

import android.content.Context;
import android.util.Log;

import com.delta.delta.utils.ChatAdapter;
import com.delta.delta.utils.ChatMessage;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by shrikrishna on 12/10/14.
 */
public class SocketIOManager {
    Socket socket;
    String fromEmail, toEmail;
    Context ctx;
    ChatAdapter chatAdapterCopy;

    public SocketIOManager(Context context, String url, JSONObject self, JSONObject other, ChatAdapter chatAdapter) {
        chatAdapterCopy = chatAdapter;
        ctx = context;
        fromEmail = self.keys().next();
        toEmail = other.keys().next();
        try {
            socket = IO.socket(url);
        } catch (Exception e) {
            Log.e("SocketIOConnectionError", "Could not connect", e);
        }

        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.i("SocketIOConnection", "Socket.io User connected");
                socket.emit("new connection", toEmail);
            }
        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                Log.i("SocketIOConnection", "Socket.io User disconnected");
            }
        });

        socket.connect();
    }
    // This Library uses org.json to parse and compose JSON strings:

    public void sendMessage(String message) {
        // Sending an object
        JSONObject msg = new JSONObject();
        try {
            msg.put("from", fromEmail);
            msg.put("to", toEmail);
            msg.put("message", message);
        } catch (Exception e) {
            Log.i("JSONError", "JSONObject exception in SocketIO message", e);
        }
//    obj.put("binary", new byte[42]);
        socket.emit("new message", msg);
    }

    public void incomingMsgHandler() {
        // Receiving an object
        socket.on("incoming message", new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject reply = (JSONObject)args[0];
                try {
                    String from = reply.getString("from");
                    String msg = reply.getString("message");

                    if (!from.equalsIgnoreCase(fromEmail)) {
                        chatAdapterCopy.add(new ChatMessage(msg));
                        chatAdapterCopy.notifyDataSetChanged();
                    }
                }  catch (Exception e) {
                    Log.i("JSONError", "JSONObject exception in extracting data from SocketIO message", e);
                }
            }
        });
    }


}
