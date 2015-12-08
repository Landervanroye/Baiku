package andreas.gps;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class minigame3 extends AppCompatActivity {

    public SensorActMF MagneticField;
    public double goal = 1000.0;
    TextView explain;
    public CountDownTimer myCountDownTimer;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.empty_test3);
        MagneticField = new SensorActMF(this);

    }

    public void goBack(View view) {
        Intent intent = new Intent(this, mainInt.class);
        startActivity(intent);
    }
    public void update(String norm){
        TextView acceleration_show = (TextView) findViewById(R.id.acceleration);
        acceleration_show.setText("Magnetic field: " + norm + " µT");
    }

    public void update_two(String norm) {
        TextView max_acc = (TextView) findViewById(R.id.textView);
        max_acc.setText("Max. Magnetic Field: " + norm + " µT");
    }


    public void startGame(View view) {

        MagneticField.start(getApplicationContext());
        explain = (TextView) findViewById(R.id.explanation);
        explain.setText("Game has started");
        Button button = (Button) findViewById(R.id.button);
        button.setEnabled(false);
        button.setText("There is no way back");
        myCountDownTimer = new CountDownTimer(30000, 1000) {
            TextView counter = (TextView) findViewById(R.id.counter);
            TextView acceleration_show = (TextView) findViewById(R.id.acceleration);
            TextView motivation = (TextView) findViewById(R.id.textView2);
            boolean value = false;



            public void onTick(long millisUntilFinished) {
                motivation.setText("");
                counter.setText("Time remaining: " + millisUntilFinished / 1000);


            }

            public void onFinish() {
                counter.setText("Done!");
                Double acc_max = MagneticField.acc_max;
                if (acc_max > goal) {
                    value = true;
                }
                MagneticField.stop();
                acceleration_show.setText("The game has ended");
                if (value) {
                    Toast.makeText(getApplicationContext(), "You won the game and got 10 points", Toast.LENGTH_LONG).show();
                    motivation.setText("Well done! You won!");

                } else if (!value) {
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


