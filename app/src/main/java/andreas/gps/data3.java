package andreas.gps;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class data3 extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eempty_test);
    }


    public void switchMain(View view) {
        Intent intent = new Intent(this, mainInt.class);
        startActivity(intent);
    }
}
