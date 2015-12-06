package andreas.gps;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class minigame1 extends AppCompatActivity {

    public SensorActAcc Accelerometer;
    public double goal = 4.0;
    TextView explain;



    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.empty_test);
        Accelerometer = new SensorActAcc(this);

    }

    public void update(String norm){
        TextView acceleration_show = (TextView) findViewById(R.id.acceleration);
        acceleration_show.setText("Acceleration: " + norm + " m/s²");
    }

    public void update_two(String norm) {
        TextView max_acc = (TextView) findViewById(R.id.textView);
        max_acc.setText("Max acceleration: " + norm + " m/s²");
    }


    public void startGame(View view) {

        Accelerometer.start(getApplicationContext());
        explain = (TextView) findViewById(R.id.explanation);
        explain.setText("Game has started");
        Button button = (Button) findViewById(R.id.button);
        button.setEnabled(false);
        button.setText("There is no way back");
        new CountDownTimer(30000, 1000) {
            TextView counter = (TextView) findViewById(R.id.counter);
            TextView acceleration_show = (TextView) findViewById(R.id.acceleration);
            TextView motivation = (TextView) findViewById(R.id.textView2);
            TextView max_acc_view = (TextView) findViewById(R.id.textView);
            boolean value = false;
            boolean fair_play = true;



            public void onTick(long millisUntilFinished) {
                counter.setText("Time remaining: " + millisUntilFinished / 1000);


            }

            public void onFinish() {
                counter.setText("Done!");
                String max_acc = Accelerometer.max_norm;
                Double acc_max = Accelerometer.acc_max;
                if (acc_max > goal) {
                    value = true;
                }
                if (acc_max > 10.0) {
                    fair_play = false;
                }
                Accelerometer.stop();
                acceleration_show.setText("");
                max_acc_view.setText("Max. acceleration: " + max_acc + " m/s²");
                if (value && fair_play) {
                    Toast.makeText(getApplicationContext(), "You won the game and got 10 points", Toast.LENGTH_LONG).show();
                    motivation.setText("Well done! You won!");

                } else if (!value) {
                    Toast.makeText(getApplicationContext(),"You lost the game", Toast.LENGTH_LONG).show();
                    motivation.setText("Hmm... Maybe next time!");
                } else {
                    Toast.makeText(getApplicationContext(),"You weren't on your bike and cheated. You lost", Toast.LENGTH_LONG).show();
                    motivation.setText("You lost the game because of cheating. If you didn't, you should try a career as a professional cyclist");
                }

            }
        }.start();


    }






    public void switchMain(View view) {
        Intent intent = new Intent(this, mainInt.class);
        startActivity(intent);
    }
}
