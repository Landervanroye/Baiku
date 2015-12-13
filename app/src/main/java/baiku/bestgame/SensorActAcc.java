package baiku.bestgame;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import java.text.DecimalFormat;


public class SensorActAcc extends Activity implements SensorEventListener {

    private SensorManager sensorManager;

    public minigame1 acclass;


    public SensorActAcc(minigame1 acc) {
        acclass = acc;
    }

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
                sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR),
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT),
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE_UNCALIBRATED),
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
            case Sensor.TYPE_ROTATION_VECTOR:
                x = values[0];
                y = values[1];
                z = values[2];
                Verwerk_rot(x, y, z);
            case Sensor.TYPE_LIGHT:
                x = values[0];
                Verwerk_licht(x);
                break;
            case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
                x = values[0];
                y = values[1];
                z = values[2];
                Verwerk_Gyroscoop(x,y,z);

        }
    }
    public void stop(){
        sensorManager.unregisterListener(this);
    }






    ///// verwerking

    public float magnetic_field_x;
    void Verwerk_Magn(float x, float y, float z) {
        magnetic_field_x = x;
    }
    public float accelerometer_x;
    public float accelerometer_y;
    public float accelerometer_z;
    public double norm;
    public String ad_norm;
    public double acc_max = 0.0;
    public String max_norm;


    void Verwerk_Accelerometer(float x, float y, float z) {
        accelerometer_x = x;
        accelerometer_y = y;
        accelerometer_z = z;
        norm = Math.sqrt(x*x+y*y+z*z);
        norm = Math.abs(norm-10.0);
        if (norm > acc_max) {
            acc_max = norm;
        }
        DecimalFormat numberFormat = new DecimalFormat("#.00");
        if (norm < 0.70) {
            norm = 0.0000000000000000;
        }
        ad_norm = numberFormat.format(norm);
        max_norm = numberFormat.format(acc_max);
        acclass.update(ad_norm);
        acclass.update_two(max_norm);

    }

    public float gyr_x;
    public float gyr_y;
    public float gyr_z;
    public double gyr_full;
    public double gyr_max = 0.0;
    public String full_gyr;
    public String max_gyr;

    void Verwerk_Gyroscoop(float x, float y, float z) {
        gyr_x = x;
        gyr_y = y;
        gyr_z = z;
        gyr_full = Math.sqrt(x*x+y*y*+z*z);
        DecimalFormat numberFormat = new DecimalFormat("#.00");
        if (gyr_full > gyr_max) {
            gyr_max = gyr_full;
        }
        full_gyr = numberFormat.format(gyr_full);
        max_gyr = numberFormat.format(gyr_max);




    }

    void verwerk_Prox(float x) {

    }

    void Verwerk_rot(float x, float y, float z) {

    }

    public float light;
    public String ad_light;
    public float max_light = 0;
    public String light_max;

    void Verwerk_licht(float x) {
        light = x;
        DecimalFormat numberFormat = new DecimalFormat("#.00");
        ad_light = numberFormat.format(light);
        if (light > max_light) {
            max_light = light;
        }
        light_max = numberFormat.format(max_light);


    }



}