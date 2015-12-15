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
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class Register extends AppCompatActivity implements View.OnClickListener{

    Integer SESSIONID = 2;
    Button register_button;
    EditText login_register_edit, password_register_edit, email_edit, password_confirm_edit;
    private String Username;
    private String Password;
    private String Email;
    private String result = "";
    private JSONObject DataOnServer;
    private static final String TAG = "lmao";
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getMessage();

        //login
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_low_in_rank);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        sharedpreferences = getSharedPreferences("myPreferences", Context.MODE_PRIVATE);
        editor = getSharedPreferences("myPreferences", Context.MODE_PRIVATE).edit();
        login_register_edit = (EditText) findViewById(R.id.login_register_edit);
        password_register_edit = (EditText) findViewById(R.id.password_register_edit);
        email_edit = (EditText) findViewById(R.id.email_edit);
        password_confirm_edit = (EditText) findViewById(R.id.password_confirm_edit);
        register_button = (Button) findViewById(R.id.register_button);


        register_button.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        String password_register_string = password_register_edit.getText().toString();
        String password_confirm_string = password_confirm_edit.getText().toString();
        String login_edit_string = login_register_edit.getText().toString();
        String email_edit_string = email_edit.getText().toString();


        if (password_confirm_string.equals("") || password_register_string.equals("") || login_edit_string.equals("") || email_edit_string.equals("")) {
            Toast.makeText(getApplicationContext(), "Please fill in all fields.", Toast.LENGTH_SHORT).show();

        } else if (!password_register_string.equals(password_confirm_string)) {
            Toast.makeText(getApplicationContext(), "Passwords do not match!", Toast.LENGTH_SHORT).show();

        } else if (!email_edit_string.contains("@")) {
            Toast.makeText(getApplicationContext(), "Please fill in a valid email adress.", Toast.LENGTH_SHORT).show();

        } else if (containsUsername(login_edit_string)){
            Toast.makeText(getApplicationContext(), "Username already exists.", Toast.LENGTH_SHORT).show();

        } else if (containsEmail(email_edit_string)) {
            Toast.makeText(getApplicationContext(), "This email address is already in use.", Toast.LENGTH_SHORT).show();

        } else {
            Username = login_register_edit.getText().toString();
            Password = password_register_edit.getText().toString();
            Email = email_edit.getText().toString();
            putMessage();
            Toast.makeText(getApplicationContext(), "Registered succesfully!", Toast.LENGTH_SHORT).show();
        }

    }

    public boolean containsUsername(String input){
        boolean contains = false;
        Integer n = 0;
        try{
            JSONArray Obj = DataOnServer.getJSONArray("data");
            while (n <= Obj.length()){

                JSONObject data = (JSONObject) Obj.get(n);
                contains = data.getJSONObject("data").getString("Username").equals(input);

                if (contains){
                    break;
                }
                n+=1;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return contains;
    }

    public boolean containsEmail(String input){
        boolean contains = false;
        Integer n = 0;
        try{
            JSONArray Obj = DataOnServer.getJSONArray("data");
            while (n <= Obj.length()){

                JSONObject data = (JSONObject) Obj.get(n);
                contains = data.getJSONObject("data").getString("Email").equals(input);

                if (contains){
                    break;
                }
                n+=1;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return contains;
    }

    public void putMessage() {
        new PutAsyncTask().execute("http://daddi.cs.kuleuven.be/peno3/data/B3/" + SESSIONID);
    }

    public void getMessage(){
        //set your own group number and session ID http://daddi.cs.kuleuven.be/peno3/data/{group number}/{session ID}
        new GetAsyncTask().execute("http://daddi.cs.kuleuven.be/peno3/data/B3/" + SESSIONID);
    }

    public void ToMainInt(){
        Intent intent = new Intent(this, mainInt.class);
        startActivity(intent);
    }

    public String createAccount(String Username, String Password, String Email, String URL) {

        JSONObject obj = new JSONObject();
        try {
            obj.put("Username", Username);
            obj.put("Password", Password);
            obj.put("Email", Email);
        } catch (JSONException e) {
            e.printStackTrace();

        }

        try {
            URL url = new URL(URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setRequestMethod("PUT");
            //conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Content-Type", "application/json");

            OutputStream os = conn.getOutputStream();

            os.write(obj.toString().getBytes());
            os.flush();

            //Read the acknowledgement message after putting data to server
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    (conn.getInputStream())));
            String output;
            System.out.println("Output from Server .... \n");
            while ((output = br.readLine()) != null) {
                System.out.println(output);
            }

            conn.disconnect();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            Log.i(TAG,e.toString());

            e.printStackTrace();
        }
        editor.putString("myusername", Username);
        editor.apply();
        String status = "Put Data to server succesfully";
        return status;
    }

    public String getDataFromServer(String URL){

        try {
            URL url = new URL(URL);
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
            Log.i(TAG,e.toString());
        }
        return result;
    }

    class PutAsyncTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            return createAccount(Username, Password, Email, urls[0]);}

        protected void onPostExecute(String result) {
            ToMainInt();
        }
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
                Log.i(TAG,e.toString());
            }
        }
    }


}

