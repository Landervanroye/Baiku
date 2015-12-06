package andreas.gps.sensoren;

import android.media.MediaRecorder;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.io.IOException;
// HOE GEBRUIKEN:
// wanneer object aangemaakt wordt met tijdspan als variabele komt het systeem in werking


public class SoundAct extends AppCompatActivity {

    private MediaRecorder mRecorder = null;
    private SoundAct deze_meter = this;
    int test;


    public int getMaxsound() {
        return mRecorder.getMaxAmplitude();
    }

    public SoundAct(int tijdspan)  {
        if (mRecorder == null) {
            //mRecorder initialiseren
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            mRecorder.setOutputFile("/dev/null/");
            //tijdspan initialiseren
            mRecorder.setMaxDuration(tijdspan);
            //na maxduration...
            mRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
                @Override
                public void onInfo(MediaRecorder mr, int what, int extra) {
                    if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                        int maxamp = mRecorder.getMaxAmplitude();
                        Log.w("maxamp",String.valueOf(maxamp));
                    //stoppen
                    mRecorder.stop();
                    mRecorder.release();
                    mRecorder = null;

                    deze_meter.act_sound(maxamp);

                }

                }
            });

            try {
                mRecorder.prepare();
            } catch (IllegalStateException | IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            // starten
            mRecorder.start();
            mRecorder.getMaxAmplitude();
        }

    }






    // HIER WORDT GEREAGEERD OP HET GEKREGEN RESULTAAT
    public void act_sound(int maxamplitude) {

        Log.w("actsound", String.valueOf(maxamplitude));
        test = maxamplitude;
        Log.w("actsound test", String.valueOf(test));

    }




}
