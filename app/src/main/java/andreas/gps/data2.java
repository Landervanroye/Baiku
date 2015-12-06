package andreas.gps;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class data2 extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_data2);
    }


    public void switchMain(View view) {
        Intent intent = new Intent(this, mainInt.class);
        startActivity(intent);
    }
    public void sensorStart(View view) {
        SensorAct sensor = new SensorAct();
        sensor.start(getApplicationContext());
    }
    public void sensorStop(View view) {
        SensorAct sensor = new SensorAct();
        sensor.stop();
    }

}
