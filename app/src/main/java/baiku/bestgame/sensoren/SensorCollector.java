
package baiku.bestgame.sensoren;


import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

import java.util.EmptyStackException;
import java.util.Stack;


public class SensorCollector implements SensorEventListener {
    public String TAG = "abcd";
    public int accelerometer = Sensor.TYPE_ACCELEROMETER;
    public int gyroscoop = Sensor.TYPE_GYROSCOPE;
    public int light = Sensor.TYPE_LIGHT;

    private static SensorManager sensorManager;
    public void start(Context context) {
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        //lastUpdate = System.currentTimeMillis();
        //register this class as a listener for the orientation and
        // accelerometer sensors
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_FASTEST);
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE),
                SensorManager.SENSOR_DELAY_NORMAL);
        sensorManager.registerListener(this,
                sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT),
                SensorManager.SENSOR_DELAY_NORMAL);


    }

    public boolean has_sensor(int sensor){
        return sensorManager.getDefaultSensor(sensor) != null;
    }

    public void stop(){
        sensorManager.unregisterListener(this);
    }


    private Stack<SensorActor> SensorActors;

    public SensorCollector(SensorActor sensact){
        SensorActors = new Stack<>();
        this.set(sensact);
    }

    public void set(SensorActor SensorActor){
        try {
            SensorActors.pop();
        } catch (EmptyStackException e){
            Log.i(TAG, e.toString());
        }
        SensorActors.push(SensorActor);
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        SensorActor currentSensorActor = SensorActors.peek();

        float[] values = event.values;
        float x;
        float y;
        float z;
        float time;
        time = event.timestamp;

        int eventtype = event.sensor.getType();
        switch (eventtype) {
            case Sensor.TYPE_GYROSCOPE:

                // Movement
                x = values[0];
                y = values[1];
                z = values[2];
                currentSensorActor.Verwerk_Gyroscoop(x, y, z, time);
                break;
            case Sensor.TYPE_ACCELEROMETER:
                // Movement
                x = values[0];
                y = values[1];
                z = values[2];

                currentSensorActor.Verwerk_Accelerometer(x, y, z, time);
                break;
            case Sensor.TYPE_LIGHT:
                x = values[0];
                currentSensorActor.Verwerk_licht(x, time);

        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
 