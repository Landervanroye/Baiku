package andreas.gps;


import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class Servercomm extends Activity {

    public JSONObject receivedmessage;
    String TAG = "abcd";
    public Socket mSocket;
    static ServercommEventListener mCallback;


    public JSONObject getLastMessage(){
        return receivedmessage;
    }

    public void sendMessage(JSONObject data) {
        Log.i(TAG, "sending message");

        try {
            JSONObject obj = new JSONObject();
            obj.put("groupid", "CW1B3");
            obj.put("sessionid", "1");
            obj.put("data", data);
            mSocket.emit("broadcast", obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i(TAG, "message sent");
    }


    public void disconnect(){
        this.disconnect();
    }

    public interface ServercommEventListener {
        void respondToMessage();
    }

    static void registerCallback(Activity callback) {
        mCallback = (ServercommEventListener) callback;
    }

    private Emitter.Listener onNewMessage = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    Log.i(TAG, "message received");
                    receivedmessage = (JSONObject) args[0];
                    mCallback.respondToMessage();


                }
            };
            new Handler(Looper.getMainLooper()).post(r);
        }
    };


    {
        try {
            IO.Options opts = new IO.Options();
            opts.path = "/peno3/socket.io";
            mSocket = IO.socket("http://daddi.cs.kuleuven.be", opts);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        mSocket.on("broadcastReceived", onNewMessage);

        mSocket.connect();
        try {
            JSONObject obj = new JSONObject();
            obj.put("groupid", "CW1B3");
            obj.put("sessionid", "1");
            mSocket.emit("register", obj);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
