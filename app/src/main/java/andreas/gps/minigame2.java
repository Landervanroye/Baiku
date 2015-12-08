package andreas.gps;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;



public class minigame2 extends AppCompatActivity {


    public SensorActComb LGA;
    public TextView acc;
    public TextView gyr;
    public TextView light;
    public TextView motivation;
    public boolean okay = false;
    public double acc_goal = 2.0;
    public double gyr_goal = 3.0;
    public float light_goal = 800;
    public TextView timer;
    public CountDownTimer myCountDownTimer;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.empty_test2);
        LGA = new SensorActComb(this);


    }

    public void update(String norm){
        acc = (TextView) findViewById(R.id.acceleration);
        acc.setText("Acceleration: " + norm + " m/s²");
    }

    public void update_gyroscope(String norm) {
        gyr = (TextView) findViewById(R.id.gyroscope);
        gyr.setText("Rotation: " + norm + " rad/s");
    }

    public void update_light(String norm) {
        light = (TextView) findViewById(R.id.light);
        light.setText("Light: " + norm + " lx");
    }

    public void goBack(View view) {
        Intent intent = new Intent(this, mainInt.class);
        startActivity(intent);
    }



    public void startGame(View view) {

        LGA.start(getApplicationContext());
        TextView explain = (TextView) findViewById(R.id.explain);
        explain.setText("Game has started.");
        Button button = (Button) findViewById(R.id.button);
        button.setEnabled(false);
        button.setText("There is no way back");

        myCountDownTimer = new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                timer = (TextView) findViewById(R.id.timer);
                timer.setText("Time remaining: " + Long.toString(millisUntilFinished / 1000));
            }

            public void onFinish() {
                gyr = (TextView) findViewById(R.id.gyroscope);
                acc = (TextView) findViewById(R.id.acceleration);
                light = (TextView) findViewById(R.id.light);
                gyr.setText("Max. Gyroscope: " + LGA.max_gyr + " rad/s");
                acc.setText("Max. Acceleration: " + LGA.max_norm + "m/s²");
                light.setText("Max. Light intensity: " + LGA.light_max + " lx");
                double max_gyr = LGA.gyr_max;
                double acc_max = LGA.acc_max;
                float light_max = LGA.max_light;
                if (light_max > light_goal && acc_max > acc_goal && max_gyr > gyr_goal) {
                    okay = true;
                }
                LGA.stop();
                timer = (TextView) findViewById(R.id.timer);
                timer.setText("Done!");
                if (okay) {
                    Toast.makeText(getApplicationContext(), "You won the game and got 50 points", Toast.LENGTH_LONG).show();
                    motivation.setText("Well done! You won!");

                } else  {
                    Toast.makeText(getApplicationContext(),"You lost.", Toast.LENGTH_LONG).show();
                    motivation.setText("You lost. Maybe next time!");
                }


            }
        };
        myCountDownTimer.start();


    }





    public void switchMain(View view) {
        myCountDownTimer.cancel();
        Intent intent = new Intent(this, mainInt.class);
        startActivity(intent);
    }
}
