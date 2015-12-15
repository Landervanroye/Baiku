package andreas.gps;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;
import android.widget.TextView;

/**
 * OPMERKING: NIET VERGETEN: sensoren stoppen wanneer niet nodig
 * hoe gebruiken?
 * 1) object aanmaken
 * 2) object.start(getApplicationContext());
 *
 */
public class SensorAct extends Activity implements SensorEventListener {

    private SensorManager sensorManager;







    public void start(Context context) {



        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        //lastUpdate = System.currentTimeMillis();
        //register this class as a listener for the orientation and
        // accelerometer sensors

        // voor meer snelheid meerdere managers


        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY),
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_GAME_ROTATION_VECTOR),
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT),
                SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    public void onSensorChanged(SensorEvent event) {
        float[] values = event.values;
        float x;
        float y;
        float z;

        int eventtype = event.sensor.getType();
        switch (eventtype) {
            case Sensor.TYPE_GYROSCOPE:

                // Movement
                x = values[0];
                y = values[1];
                z = values[2];
                Verwerk_Gyroscoop(x, y, z);
                break;
            case Sensor.TYPE_ACCELEROMETER:
                // Movement
                x = values[0];
                y = values[1];
                z = values[2];
                Verwerk_Accelerometer(x, y, z);
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                x = values[0];
                y = values[1];
                z = values[2];
                Verwerk_Magn(x, y, z);
                break;
            case Sensor.TYPE_PROXIMITY:
                x = values[0];
                verwerk_Prox(x);
                break;
            case Sensor.TYPE_GAME_ROTATION_VECTOR:
                x = values[0];
                y = values[1];
                z = values[2];
                Verwerk_rot(x, y, z);
            case Sensor.TYPE_LIGHT:
                x = values[0];
                Verwerk_licht(x);

        }
    }
    public void stop(){
        sensorManager.unregisterListener(this);
    }






    ///// verwerking

    void Verwerk_Magn(float x, float y, float z) {
        Log.w("Magn", String.valueOf(x) + " " + String.valueOf(y) + " " + String.valueOf(z));
        TextView edit_message = (TextView) findViewById(R.id.text_sensor);
        edit_message.setText(String.valueOf(x) + " " + String.valueOf(y) + " " + String.valueOf(z));
    }

    void Verwerk_Accelerometer(float x, float y, float z) {
        Log.w("Acc",String.valueOf(x) + " " + String.valueOf(y) + " " + String.valueOf(z));
    }

    void Verwerk_Gyroscoop(float x, float y, float z) {

    }

    void verwerk_Prox(float x) {

    }

    void Verwerk_rot(float x, float y, float z) {

    }

    void Verwerk_licht(float x) {

    }
}