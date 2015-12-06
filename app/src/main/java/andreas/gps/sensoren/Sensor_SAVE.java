package andreas.gps.sensoren;

import android.util.Log;

public class Sensor_SAVE extends SensorActor {
    private float licht;
    private float maxXaccelero =0;
    private float maxXgyro = 0;

    public float getMaxXaccelero() {
        return maxXaccelero;
    }

    public float getMaxXgyro() {
        return maxXgyro;
    }

    public float getLicht() {
        return licht;
    }


    @Override
    public void Verwerk_Accelerometer(float x, float y, float z, float timenu) {
        if (Math.abs(x)>Math.abs(maxXaccelero)){
            maxXaccelero =Math.abs(x);
            Log.i("newmaxX", String.valueOf(maxXaccelero));
        }
    }

    @Override
    public void Verwerk_Gyroscoop(float x, float y, float z, float timenu) {
        if (Math.abs(x) > Math.abs(maxXgyro)) {
            maxXgyro = Math.abs(x);
        }
    }

    @Override
    public void Verwerk_licht(float x, float timenu) {
        licht=x;
    }
}
