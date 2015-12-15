package andreas.gps;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class login extends AppCompatActivity implements View.OnClickListener{

    Integer SESSIONID = 2;
    Button login_button;
    EditText login_edit, password_edit;
    TextView register_text;
    private String result = "";
    private JSONObject DataOnServer;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Intent intent;
    String TAG = "abcd";


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login);

        //login
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_low_in_rank);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        login_edit = (EditText)findViewById(R.id.login_edit);
        password_edit = (EditText)findViewById(R.id.password_edit);
        login_button = (Button)findViewById(R.id.login_button);
        register_text = (TextView)findViewById(R.id.register_text);

        login_button.setOnClickListener(this);
        register_text.setOnClickListener(this);

        preferences = getSharedPreferences("myPreferences", Context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.apply();

        getMessage();

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.login_button:
                String login_edit_string = login_edit.getText().toString();
                String password_edit_string = password_edit.getText().toString();
                if (!correctLogin(login_edit_string,password_edit_string)){
                    Toast.makeText(getApplicationContext(), "Username or password is incorrect.", Toast.LENGTH_SHORT).show();
                }
                else{
                    editor.putString("myusername",login_edit_string);
                    editor.apply();

                    intent = new Intent(this,mainInt.class);
                    startActivity(intent);
                }

                break;

            case R.id.register_text:

                intent = new Intent(this, Register.class);
                startActivity(intent);

                break;
        }
    }

    public boolean correctLogin(String username, String password){
        boolean correct = false;
        boolean user;
        boolean pass;
        Integer n = 0;
        try{
            Log.i(TAG,DataOnServer.toString());
            JSONArray Obj = DataOnServer.getJSONArray("data");
            while (n <= Obj.length()){

                JSONObject data = (JSONObject) Obj.get(n);
                user = data.getJSONObject("data").getString("Username").equals(username);
                pass = data.getJSONObject("data").getString("Password").equals(password);

                if (user && pass){
                    correct = true;
                    break;
                }
                n+=1;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return correct;
    }

    public void getMessage(){
        //set your own group number and session ID http://daddi.cs.kuleuven.be/peno3/data/{group number}/{session ID}
        new GetAsyncTask().execute("http://daddi.cs.kuleuven.be/peno3/data/B3/" + SESSIONID);
    }

    public String getDataFromServer(String URL){

        try {
            java.net.URL url = new URL(URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            if (conn.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));

            String output;
            while ((output = br.readLine()) != null) {
                result += output;
            }
            conn.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    class GetAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            return getDataFromServer(urls[0]);
        }
        @Override
        protected void onPostExecute(String result) {
            try{
                DataOnServer = new JSONObject(result);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }


}
