package baiku.bestgame.sensoren;

public abstract class SensorActor {
    public abstract void Verwerk_Accelerometer(float x, float y, float z, float timenu);
    public abstract void Verwerk_Gyroscoop(float x, float y, float z,float timenu);
    public abstract void Verwerk_licht(float x,float timenu) ;
}
