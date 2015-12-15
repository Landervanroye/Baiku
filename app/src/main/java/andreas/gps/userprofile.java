package andreas.gps;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

public class userprofile extends AppCompatActivity {

    public SharedPreferences prefs;
    public String myusername;
    public TextView points1;
    public TextView points2;
    public TextView points3;
    public TextView streak1;
    public TextView streak2;
    public TextView streak3;

    @Override
    public void onCreate(Bundle Savedinstancestate){
        super.onCreate(Savedinstancestate);
        setContentView(R.layout.layout_userprofile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_low_in_rank);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("My Profile");

        Bundle b = getIntent().getExtras();
        myusername = b.getString("username");

        prefs = getSharedPreferences(myusername, Context.MODE_PRIVATE);
        points1 = (TextView) findViewById(R.id.highestpersonalpoints1);
        points2 = (TextView) findViewById(R.id.highestpersonalpoints2);
        points3 = (TextView) findViewById(R.id.highestpersonalpoints3);
        streak1 = (TextView) findViewById(R.id.killstreak1);
        streak2 = (TextView) findViewById(R.id.killstreak2);
        streak3 = (TextView) findViewById(R.id.killstreak3);
    }

    @Override
    public void onResume(){
        super.onResume();
        int p1 = prefs.getInt("Highestpoints1",0);
        int p2 = prefs.getInt("Highestpoints2",0);
        int p3 = prefs.getInt("Highestpoints3",0);
        int k1 = prefs.getInt("killstreak1",0);
        int k2 = prefs.getInt("killstreak2",0);
        int k3 = prefs.getInt("killstreak3",0);
        points1.setText("1) " + Integer.toString(p1));
        points2.setText("2) " + Integer.toString(p2));
        points3.setText("3) " + Integer.toString(p3));
        streak1.setText("1) " + Integer.toString(k1));
        streak2.setText("2) " + Integer.toString(k2));
        streak3.setText("3) " + Integer.toString(k3));
    }
}
