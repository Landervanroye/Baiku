package andreas.gps;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Random;

import andreas.gps.sensoren.SensorActor;
import andreas.gps.sensoren.SensorCollector;
import andreas.gps.sensoren.Sensor_SAVE;
import andreas.gps.sensoren.SoundAct;

public class gameMode extends AppCompatActivity
        implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener, Servercomm.ServercommEventListener, NavigationView.OnNavigationItemSelectedListener {

    //    variables
    public SharedPreferences personalPreferences;
    public SharedPreferences.Editor personalPreferencesEditor;
    public static SensorCollector sensorcol;
    private Circle circleTarget;
    private static final String TAG = "abcd";
    public LatLng TargetLocMap;
    public LatLng TargetLocActual;
    private Marker markerTarget;
    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    private LocationRequest mLocationRequest;
    public Marker mymarker;
    boolean gps_connected = false;
    boolean network_connected = false;
    public float zoomlevel = 18;
    public boolean zoomed = false;
    public LatLng loc;
    private double mySpeed = 0;
    private int kill_button_counter = 0;
    Marker markerTarget2 = null;
    public boolean Soundpowerup = false;
    public int powerUpOffensiveUpdateFrequencyPrice = 80;
    public int powerUpOffensiveEasyKillPrice = 50;
    public int powerUpDefensiveStopSignalPrice = 120;
    public int powerUpDefensiveSoundPrice = 100;
    public int powerUpDefensiveHardKillPrice = 150;
    public int powerUpDefensivePursuerPrice = 75;

    //text kkillmoves
    public boolean killmoveinprogress = false;
    public String TAG2 = "abcdef";
    private String killmoveAcellorText = "Accelerate!";
    private String killmoveGyroText = "Shoot him down!";
    private String killmoveSoundText = "Scream him to death!";
    private String killmoveSpeedText = "Get to your highest speed!";
    private String killmovelightText = "Remove all light!";
    private String killmovePressButtonText = "Press him to death!";
    private double killmoveAcellorValue = 20;
    private double killmoveGyroValue = 40;
    private double killmoveSoundValue = 25000;
    private double killmoveSpeedValue = 6.4;
    private double killmovelightValue = 2;
    private double killmovePressButtonValue = 50;
    private long killmovetimer = 30000;

    public ArrayList<String> pursuers = new ArrayList<>();
    public String myusername;
    Servercomm mServercomm;
    public String NotifyOffline = "NotifyOffline";
    public String TargetID = "";
    public String getPriorities = "getPriorities";
    public String eliminated = "eliminated";
    public String pickedTarget = "pickedTarget";
    public String locationUpdate = "locationUpdate";
    public String priorityID = "";
    public boolean easykill = false;
    private Handler mHandler = new Handler();
    public double prioritylevel = 0;
    public LatLng targetLocation;
    public String priorityCategory = "priorityCategory";
    public String getNewLocation = "getNewLocation";
    public String giveNewLocation = "giveNewLocation";
    public ProgressDialog progressDialog;
    public int missedLocationUpdates = 0;
    ConnectivityManager connectivityManager;
    LocationManager locationManager;
    NetworkInfo activeNetwork;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    public int mypoints;
    public int targetpoints = 0;
    public int mymoney;
    Dialog alertDialog;
    public boolean pressquit = false;
    public boolean stopSignal = false;
    Integer frequencyUpdate = 32768;
    public String pursuer = "";
    private Runnable targetLocationRequest;
    private Runnable FollowPursuer = new Runnable() {
        @Override
        public void run() {
            requestLocationUpdate(pursuer);
            mHandler.postDelayed(FollowPursuer, 5000);
        }
    };
    public String audioFilePath;
    byte[] bytes;
    byte[] sound;
    public MediaRecorder mediaRecorder;
    AlertDialog.Builder builder;
    public boolean hardkill = false;
    public String previoustarget;
    public Runnable changeMessage = new Runnable() {
        @Override
        public void run() {
            if (!priorityID.equals("")) {
                Log.i(TAG, "progressdialog changing text");
                TargetID = priorityID;
                notifyHunting();
                progressDialog.setMessage("Tracking target..");

            } else {
                Log.i(TAG, "no target found yet");
                progressDialog.dismiss();
                show_alertdialog_target();
            }
            targetLocationRequest.run();
        }
    };
    private Runnable changeTargetRunnable = new Runnable(){

        @Override
        public void run() {
            changeTarget();
        }
    };
    public Runnable removePursuercallback = new Runnable(){

        @Override
        public void run() {
            mHandler.removeCallbacks(FollowPursuer);
            try {
                markerTarget2.remove();
            } catch (Exception e){
                Log.i(TAG, e.toString());
            }
            pursuer = "";
        }
    };
    public JSONObject top10 = new JSONObject();
    public MenuItem money;
    public CountDownTimer myCountDownTimer;
    TextView points_score;
    TextView killMoveText;
    public Button kill_button;
    public Sensor_SAVE sensorsave;
    public boolean noExtraPursuers = false;
    public int powerUpDefensiveNoExtraPursuersPrice = 200;
    public Runnable sendLocActual = new Runnable() {
        @Override
        public void run() {
            JSONObject data = new JSONObject();
            try {
                data.put("sender", myusername);
                data.put("receiver", "");
                data.put("category", giveNewLocation);
                data.put("message", "");
                data.put("points", mypoints);
                data.put("latitude", loc.latitude);
                data.put("longitude", loc.longitude);
                mServercomm.sendMessage(data);
            } catch (Exception e) {
                e.printStackTrace();
            }
            mHandler.postDelayed(sendLocActual,1000);
        }
    };

    //Lifecycle
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        Log.i(TAG, "Got into oncreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nav_drawer_low_in_rank);

        //login
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_low_in_rank);
        setSupportActionBar(toolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Servercomm.registerCallback(this);

        // navigation drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout_low_in_rank);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view_low_in_rank);
        navigationView.setNavigationItemSelectedListener(this);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        activeNetwork = connectivityManager.getActiveNetworkInfo();
        preferences = getSharedPreferences("myPreferences", Context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.apply();
        audioFilePath = getFilesDir().getAbsolutePath()+"abc.3gp";
        points_score = (TextView) findViewById(R.id.points_score);
        sensorsave = new Sensor_SAVE();
        sensorcol = new SensorCollector(sensorsave);
        myusername = preferences.getString("myusername", "");
        personalPreferences = getSharedPreferences(myusername,Context.MODE_PRIVATE);
        personalPreferencesEditor = personalPreferences.edit();
        personalPreferencesEditor.apply();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(5000)        // 5 seconds, in milliseconds
                .setFastestInterval(1000); // 1 second, in milliseconds
        assignTargetLocationRequest();
        Menu mymenu = navigationView.getMenu();
        money = mymenu.findItem(R.id.mypoints);
        killMoveText = (TextView) findViewById(R.id.killMoveText);
        points_score = (TextView) findViewById(R.id.points_score);
        kill_button = (Button) findViewById(R.id.kill_button);
        kill_button.setText("Press me");
        ////////////// test
        //killMoveAccelor();


        Log.i(TAG, "Oncreate success");
    }
    public void assignTargetLocationRequest(){
        targetLocationRequest = new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "running targetLocationRequest");
                if (missedLocationUpdates > 3){
                    Toast.makeText(gameMode.this, "Target not responding, getting new target", Toast.LENGTH_SHORT).show();
                    missedLocationUpdates = 0;
                    changeTarget();
                } else if (TargetID.equals("")){
                    Log.i(TAG, "No target yet");
                    mHandler.postDelayed(changeTargetRunnable,60000);
                } else {
                    Log.i(TAG, "request LocationUpdate");
                    requestLocationUpdate(TargetID);
                    mHandler.postDelayed(targetLocationRequest, frequencyUpdate);
                }
            }
        };
    }
    @Override
    protected void onPause() {
        Log.i(TAG, "Paused.");
        TargetID = "";
        notifyOffline("");
        notifyOffline(TargetID);
        super.onPause();
        if (mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);
            mGoogleApiClient.disconnect();
        }

        personalPreferencesEditor.putInt("mymoney", mymoney);
        personalPreferencesEditor.apply();
        mHandler.removeCallbacks(targetLocationRequest);
        mHandler.removeCallbacks(FollowPursuer);
        mHandler.removeCallbacks(changeTargetRunnable);
        mHandler.removeCallbacks(changeMessage);
        mHandler.removeCallbacks(removePursuercallback);
        mHandler.removeCallbacks(sendLocActual);
        mServercomm = null;
        try {
            myCountDownTimer.cancel();
            killmovefinished(false);
        } catch (Exception e){
            Log.i(TAG,e.toString());
        }

    }
    @Override
    protected void onResume() {
        Log.i(TAG, "Onresume");
        zoomed = false;
        super.onResume();
        Long data = 0L;
        try {
            Context con = createPackageContext("com.cw.game.android", 0);
            SharedPreferences pref = con.getSharedPreferences(
                    "MyPreferences", Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE);
            SharedPreferences.Editor editorgame = pref.edit();
            data = pref.getLong("deltatime", 0);
            data /= 500;
            Toast.makeText(this,"succesfully added " + String.valueOf(data) +" points of naamsestraatrider", Toast.LENGTH_LONG).show();
            SharedPreferences.Editor naamseeditor = pref.edit();
            naamseeditor.putLong("deltatime",0L);
            naamseeditor.commit();

            mypoints += data;
            mymoney += data;
            money.setTitle("Baikoins: "+Integer.toString(mymoney));
            points_score.setText(Integer.toString(mypoints));



            ////PUNTEN NOG TOEVOEGEN EN SHAREDPREFS OP 0 ZETTEN!

        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, e.toString());
        }


        if (activeNetwork != null && activeNetwork.isConnected()) network_connected = true;
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) gps_connected = true;
        Log.i(TAG, "Connecting apiclient");
        mGoogleApiClient.connect();
        mServercomm = new Servercomm();
        Log.i(TAG, "getting score");
        mymoney = personalPreferences.getInt("mymoney", 0);
        mypoints = data.intValue();
        mymoney += mypoints;
        money.setTitle("My Money: " + Integer.toString(mymoney));
        points_score.setText(String.format("%d", mypoints));
        mHandler.postDelayed(changeTargetRunnable, 1000);
        Log.i(TAG, "got score");
        sendLocActual.run();

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        } else if (id == R.id.toolbar_shop) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toolbar_buttons_gamemode, menu);
        return true;
    }

    //Location handling
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);


    }
    public void onConnected(Bundle bundle) {
        Log.i(TAG, "Location services connected.");
        if (network_connected && gps_connected) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            while (location == null) {
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
                location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            }
            handleNewLocation(location);


        } else if (!network_connected) {
            Log.i(TAG, "No network.");
            show_alertdialog_network();
        } else {
            Log.i(TAG, "No GPS.");
            show_alertdialog_gps();


        }
    }
    public void show_alertdialog_network() {
        Log.i(TAG, "show_alertdialog_network");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No network!");
        builder.setMessage("Please turn on wifi or network data.");
        builder.setPositiveButton("To network data", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(
                        "com.android.settings",
                        "com.android.settings.Settings$DataUsageSummaryActivity"));
                startActivity(intent);
            }
        });
        builder.setNegativeButton("To wifi", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                startActivity(intent);
            }
        });
        builder.setNeutralButton("Nahh", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(gameMode.this, "No game for you!", Toast.LENGTH_SHORT).show();
            }
        });
        Dialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        builder.show();
    }
    public void show_alertdialog_gps() {
        Log.i(TAG, "show_alertdialog_gps");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("No gps!");
        builder.setMessage("Please turn on location services.");
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });
        builder.setNegativeButton("Nahh", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(gameMode.this, "No game for you!", Toast.LENGTH_SHORT).show();
            }
        });
        Dialog alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        builder.show();
    }


    public void show_haikuman(){
        ImageView image = new ImageView(this);
        image.setImageResource(R.drawable.haikuman);
        String Haiku = generate_random_Haiku();

        AlertDialog.Builder builder =
                new AlertDialog.Builder(this).
                        setMessage(Haiku).
                        setPositiveButton("Thanks Old Man!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                changeTarget();
                            }
                        }).
                        setView(image);
        builder.create();
        builder.show();
    }

    public String generate_random_Haiku(){
        String haiku;
        Random rand = new Random();
        int randomNum = rand.nextInt(9);
        List<String> haiku_list = new ArrayList<>();
        haiku_list.add("The wren\n" +
                "Earns his living\n" +
                "Noiselessly.\n" +
                "- Kobayahsi Issa");
        haiku_list.add("From time to time\n" +
                "The clouds give rest\n" +
                "To the moon-beholders.\n" +
                "- Matsuo Bashō");
        haiku_list.add("Over-ripe sushi,\n" +
                "The Master\n" +
                "Is full of regret.\n" +
                "- Yosa Buson");
        haiku_list.add("Consider me\n" +
                "As one who loved poetry\n" +
                "And persimmons.\n" +
                "- Masaoaka Shiki");
        haiku_list.add("In the cicada's cry\n" +
                "No sign can foretell\n" +
                "How soon it must die.\n" +
                "- Matsuo Bashō");
        haiku_list.add("Blowing from the west\n" +
                "Fallen leaves gather\n" +
                "In the east.\n" +
                "- Yosa Buson");
        haiku_list.add("Winter seclusion -\n" +
                "Listening, that evening,\n" +
                "To the rain in the mountain.\n" +
                "- Kobayashi Issa");
        haiku_list.add("Don’t weep, insects –\n" +
                "Lovers, stars themselves,\n" +
                "Must part.\n" +
                "- Kobayashi Issa");
        haiku_list.add("My life, -\n" +
                "How much more of it remains?\n" +
                "The night is brief.\n" +
                "- Masaoka Shiki");
        haiku_list.add("An old silent pond...\n" +
                "A frog jumps into the pond,\n" +
                "splash! Silence again.\n" +
                "- Matsuo Bashō");
        haiku = haiku_list.get(randomNum);
        return haiku;
    }
    private void handleNewLocation(Location location) {
        Log.i(TAG, "handleNewLocation");
        loc = new LatLng(location.getLatitude(), location.getLongitude());
        if (!zoomed) {
            Log.i(TAG, "zooming.");
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, zoomlevel));
            zoomed = true;
        }

        if (mymarker != null) {
            mymarker.remove();
        }
        MarkerOptions options = new MarkerOptions()
                .position(loc)
                .title("I am here!")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        mymarker = mMap.addMarker(options);

        if (!TargetID.equals("")) {
            initiateKillmove();
        }
    }
    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Location services suspended. Please reconnect");
    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "onconnectionfailed");
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            } catch (IntentSender.SendIntentException e) {
                e.printStackTrace();
            }
        } else {
            Log.i(TAG, "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }
    @Override
    public void onLocationChanged(Location location) {
        mySpeed = location.getSpeed();
        handleNewLocation(location);


    }
    public void zoombutton(View view) {
        Log.i(TAG, "clicked!");
//        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//
//        Log.i(TAG, String.valueOf(location));
        if (loc != null) {
            Log.i(TAG, "moving camera");
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(loc, zoomlevel));
        }
    }


    //Killmove
    public void initiateKillmove() {
        if (!killmoveinprogress && CalculationByDistance(loc, TargetLocActual) <= 10.0) {
            killMovegenerator();
        }
    }
    public void killMovegenerator() {
        killmoveinprogress = true;
        mHandler.removeCallbacks(targetLocationRequest);
        mHandler.removeCallbacks(changeMessage);
        Log.i(TAG, "generating killmove");
        Random rand = new Random();
        SensorActor sensorsave = new Sensor_SAVE();
        sensorcol.set(sensorsave);
        sensorcol.start(getApplicationContext());
        int random = rand.nextInt(7);
        if (easykill) {
            killMovePressButton();
            easykill = false;
        } else if (hardkill) {
            killMoveSpeed();
            hardkill = false;
        } else {
            switch (random) {
                case 0:
                    // check if compatible
                    if (sensorcol.has_sensor(sensorcol.accelerometer)) {
                        killMoveAccelor();
                    } else {
                        killMovegenerator();
                    }
                    break;
                case 1:
                    // check if compatible -> run
                    if (sensorcol.has_sensor(sensorcol.gyroscoop)) {
                        killMoveGyroscoop();
                    } else {
                        killMovegenerator();
                    }
                    break;
                case 2:
                    // geen sensorcollector nodig -->
                    sensorcol.stop();
                    // check if compatible -> run
                    killMoveGyroscoop();

                    break;
                case 3:
                    sensorcol.stop();
                    killMoveSpeed();
                    break;
                case 4:
                    sensorcol.stop();
                    killMovePressButton();
                    break;
                case 5:
                    if (sensorcol.has_sensor(sensorcol.light)) {
                        killMovelight();
                    } else {
                        killMovegenerator();
                    }
                    break;
                case 6:
                    sensorcol.stop();
                    killMoveSound();
                    break;
            }
        }
    }
    public void killMoveAccelor() {
        killMoveText.setVisibility(View.VISIBLE);
        sensorsave = new Sensor_SAVE();
        sensorcol = new SensorCollector(sensorsave);
        sensorcol.start(getApplicationContext());
        myCountDownTimer = new CountDownTimer(killmovetimer, 200) {


            public void onTick(long millisUntilFinished) {


                killMoveText.setText(killmoveAcellorText + millisUntilFinished / 1000);

                try {
                    if (sensorsave.getMaxNormAccelero() > killmoveAcellorValue) {
                        myCountDownTimer.cancel();
                        sensorcol.stop();
                        killmovefinished(true);
                    }
                } catch (EmptyStackException e) {
                    Log.i(TAG, e.toString());
                }
            }
            public void onFinish() {
                Log.i(TAG, "killmove acceleration ended");
                sensorcol.stop();
                killmovefinished(false);

            }
        };
        myCountDownTimer.start();

    }
    public void killMoveSound() {

        myCountDownTimer = new CountDownTimer(killmovetimer, 200) {
            SoundAct soundact = new SoundAct(0);

            public void onTick(long millisUntilFinished) {
                killMoveText.setVisibility(View.VISIBLE);
                killMoveText.setText(killmoveSoundText + millisUntilFinished / 1000);

                if (soundact.getMaxsound() > killmoveSoundValue) {
                    myCountDownTimer.cancel();
                    killmovefinished(true);

                }
            }

            public void onFinish() {
                Log.i(TAG, "killmove sound ended");
                killmovefinished(false);

            }
        };
        myCountDownTimer.start();

    }
    public void killMoveGyroscoop() {
        killMoveText.setVisibility(View.VISIBLE);
        sensorsave = new Sensor_SAVE();
        sensorcol = new SensorCollector(sensorsave);
        sensorcol.start(getApplicationContext());
        myCountDownTimer = new CountDownTimer(killmovetimer, 200) {

            public void onTick(long millisUntilFinished) {
                killMoveText.setText(killmoveGyroText + millisUntilFinished / 1000);

                try {
                    if (sensorsave.getMaxXgyro() > killmoveGyroValue) {
                        sensorcol.stop();
                        myCountDownTimer.cancel();
                        killmovefinished(true);


                    }
                } catch (EmptyStackException e) {
                    Log.i(TAG, e.toString());
                }
            }

            public void onFinish() {
                Log.i(TAG,"killmove gyroscoop ended");
                sensorcol.stop();
                killmovefinished(false);

            }
        };
        myCountDownTimer.start();

    }
    public void killMovelight() {
        killMoveText.setVisibility(View.VISIBLE);
        sensorsave = new Sensor_SAVE();
        sensorcol = new SensorCollector(sensorsave);
        sensorcol.start(getApplicationContext());
        myCountDownTimer = new CountDownTimer(killmovetimer, 200) {

            public void onTick(long millisUntilFinished) {
                killMoveText.setText(killmovelightText + millisUntilFinished / 1000);
                try {

                    if (sensorsave.getLicht() < killmovelightValue) {
                        sensorcol.stop();
                        myCountDownTimer.cancel();
                        killmovefinished(true);

                    }
                } catch (EmptyStackException e) {
                    Log.i(TAG, e.toString());
                }
            }


            public void onFinish() {
                Log.i(TAG,"killmove light ended");
                sensorcol.stop();
                killmovefinished(false);

            }
        };
        myCountDownTimer.start();

    }
    public void killMoveSpeed() {
        killMoveText.setVisibility(View.VISIBLE);
        myCountDownTimer = new CountDownTimer(killmovetimer, 200) {
            public void onTick(long millisUntilFinished) {
                killMoveText.setText(killmoveSpeedText + millisUntilFinished / 1000);

                if (mySpeed > killmoveSpeedValue) {
                    myCountDownTimer.cancel();
                    killmovefinished(true);
                }
            }

            public void onFinish() {
                Log.i(TAG, "killmove speed ended");
                killmovefinished(false);

            }
        };
        myCountDownTimer.start();
    }
    public void killMoveCounter(View view) {
        kill_button_counter += 1;
    }
    public void killMovePressButton() {
        Log.i(TAG, "killmove started");
        killMoveText.setVisibility(View.VISIBLE);
        kill_button.setVisibility(View.VISIBLE);
        myCountDownTimer = new CountDownTimer(killmovetimer, 200) {

            public void onTick(long millisUntilFinished) {
                Log.i(TAG, "killmove in progress");
                killMoveText.setText(killmovePressButtonText + millisUntilFinished / 1000);
                if (kill_button_counter > killmovePressButtonValue) {
                    Log.i(TAG,"killmove success");
                    kill_button_counter = 0;
                    kill_button.setVisibility(View.INVISIBLE);
                    myCountDownTimer.cancel();
                    killmovefinished(true);
                }
            }

            public void onFinish() {
                Log.i(TAG,"killmove button ended");
                kill_button.setVisibility(View.INVISIBLE);
                kill_button_counter = 0;
                killmovefinished(false);
            }
        };
        myCountDownTimer.start();

    }
    public void record(String sender1) {
        Log.i(TAG,"record");
        final String sender = sender1;
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        mediaRecorder.setOutputFile(audioFilePath);
        mediaRecorder.setMaxDuration(3000);
        //na maxduration...
        mediaRecorder.setOnInfoListener(new MediaRecorder.OnInfoListener() {
            @Override
            public void onInfo(MediaRecorder mr, int what, int extra) {
                if (what == MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED) {
                    Log.i(TAG, "mediarecorder stop");
                    try{
                        mediaRecorder.stop();
                        mediaRecorder.release();
                    } catch (Exception e){
                        Log.i(TAG,e.toString());
                    }

                    verwerkopname(sender);

                }
            }
        });
        try {
            mediaRecorder.prepare();
            Log.i(TAG,"mediaRecorder.start");
            mediaRecorder.start();
        } catch (IOException e) {
            Log.i(TAG, e.toString());
        }
    }
    public void verwerkopname(String sender) {
        Log.i(TAG, "verwerkopname");
        int bytesRead;
        try {
            InputStream is = new FileInputStream(new File(audioFilePath));

            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            byte[] b = new byte[1024];

            while ((bytesRead = is.read(b)) != -1) {

                bos.write(b, 0, bytesRead);


            }
            bytes = bos.toByteArray();

            JSONObject data = new JSONObject();
            data.put("sound", bytes);
            data.put("sender", myusername);
            data.put("receiver",sender);
            data.put("category",locationUpdate);
            data.put("message", "giveNewSound");
            data.put("latitude", loc.latitude);
            data.put("longitude", loc.longitude);
            data.put("points", mypoints);
            Log.i(TAG, "sendsoundmessage");
            mServercomm.sendMessage(data);

        } catch (Exception e) {
            Log.i(TAG, e.toString());
        }
    }
    public void speelsound() {
        Log.i(TAG, "speelsound");
        try {
            FileOutputStream fos;

            File path = File.createTempFile("temp_audio", "3gp", getCacheDir());
            fos = new FileOutputStream(path);
            fos.write(sound);
            fos.close();
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(String.valueOf(path));
            mediaPlayer.prepare();
            mediaPlayer.start();
            Log.i(TAG, "playing");
        } catch (Exception e){
            Log.i(TAG,e.toString());
        }
    }

    //Target interaction
    public void notifyOffline(String receiver){
        JSONObject data = new JSONObject();
        try {
            data.put("sender", myusername);
            data.put("receiver", receiver);
            data.put("category", "");
            data.put("message", NotifyOffline);
            data.put("points", mypoints);
            data.put("latitude", loc.latitude);
            data.put("longitude", loc.longitude);
        } catch (Exception e){
            Log.e(TAG, e.toString());
        }
        mServercomm.sendMessage(data);
    }
    public double CalculationByDistance(LatLng StartP, LatLng EndP) {
        Log.i(TAG, "CalculationByDistance");
        int Radius = 6371000;// radius of earth in Km
        try {
            double lat1 = StartP.latitude;
            double lat2 = EndP.latitude;
            double lon1 = StartP.longitude;
            double lon2 = EndP.longitude;
            double dLat = Math.toRadians(lat2 - lat1);
            double dLon = Math.toRadians(lon2 - lon1);
            double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                    + Math.cos(Math.toRadians(lat1))
                    * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                    * Math.sin(dLon / 2);
            double c = 2 * Math.asin(Math.sqrt(a));
            return Radius * c;
        } catch (NullPointerException e) {
            return 20.0;
        }
    }
    public void changeTarget() {
        try{
            alertDialog.cancel();
        } catch (Exception e){
            Log.i(TAG,e.toString());
        }
        Log.i(TAG, "changeTarget");
        TargetLocActual = null;
        previoustarget = TargetID;
        TargetID = "";
        try{
            markerTarget.remove();
            circleTarget.remove();
        } catch (Exception e){
            Log.i(TAG,e.toString());
        }
        killmoveinprogress = false;
        prioritylevel = -100;
        priorityID = "";
        JSONObject data = new JSONObject();
        try {
            data.put("sender", myusername);
            data.put("receiver", "");
            data.put("category", "");
            data.put("message", getPriorities);
            data.put("points",mypoints);
            data.put("latitude",loc.latitude);
            data.put("longitude",loc.longitude);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mServercomm.sendMessage(data);
        Log.i(TAG, "starting progressdialog");
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Picking target..");
        try{
            progressDialog.show();
        } catch (Exception e) {
            Log.i(TAG, e.toString());
        }

        mHandler.postDelayed(changeMessage, 4000);
    }
    public void notifyHunting() {
        JSONObject data = new JSONObject();
        try {
            data.put("sender", myusername);
            data.put("receiver", TargetID);
            data.put("category", pickedTarget);
            data.put("message", "");
            data.put("points", mypoints);
            data.put("latitude", loc.latitude);
            data.put("longitude", loc.longitude);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mServercomm.sendMessage(data);
    }
    public void show_alertdialog_target(){
        Log.i(TAG, "show_alertdialog_target");
        builder = new AlertDialog.Builder(this);
        builder.setTitle("No target found :(");
        builder.setMessage("Don't quit. We will try again in a minute.");
        builder.setPositiveButton("Wait for\nanother player", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.i(TAG, "Continue playing");
            }
        });
        builder.setNegativeButton("Quit gamemode", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.i(TAG, "Quit gamemode");
                finish();
            }
        });
        alertDialog = builder.create();
        alertDialog.setCanceledOnTouchOutside(true);
        try {
            alertDialog.show();
        } catch (WindowManager.BadTokenException e){
            Log.i(TAG,e.toString());
        }
    }
    public void targetbutton(View view) {
        Log.i(TAG, "Targetbutton pressed.");
        if (!TargetID.equals("")) {
            LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
            boundsBuilder.include(loc);
            boundsBuilder.include(TargetLocMap);
            LatLngBounds bounds = boundsBuilder.build();
            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
        }
    }
    public void requestLocationUpdate(String target) {
        if (target.equals(TargetID)){
            missedLocationUpdates +=1;
        }
        Log.i(TAG, "requestLocationUpdate");
        JSONObject data = new JSONObject();
        try {
            data.put("sender", myusername);
            data.put("receiver",target);
            data.put("category",locationUpdate);
            data.put("message",getNewLocation);
            data.put("points",mypoints);
            data.put("latitude",loc.latitude);
            data.put("longitude",loc.longitude);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mServercomm.sendMessage(data);
    }
    public void sendPriority(String sender, LatLng targetLocation) {
        if (CalculationByDistance(loc, targetLocation) < 5000) {
            Log.i(TAG, "Send priority");
            //TextView points_score = (TextView) findViewById(R.id.points_score);
            //String points_str = (String) points_score.getText();
            //int points_int = Integer.parseInt(points_str);
            double priority = 2 * Math.log10(mypoints+10);
            priority -= pursuers.size();
            JSONObject data = new JSONObject();
            try {
                data.put("sender", myusername);
                data.put("receiver", sender);
                data.put("category", priorityCategory);
                data.put("message", Double.toString(priority));
                data.put("points", mypoints);
                data.put("latitude", loc.latitude);
                data.put("longitude", loc.longitude);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mServercomm.sendMessage(data);
        }
    }
    public void sendLocation(String sender) {
        Log.i(TAG, "sending location");
        JSONObject data = new JSONObject();
        try {
            data.put("sender", myusername);
            data.put("receiver",sender);
            data.put("category",locationUpdate);
            data.put("message",giveNewLocation);
            data.put("latitude",loc.latitude);
            data.put("longitude",loc.longitude);
            data.put("points",mypoints);
        } catch (JSONException e) {
            Log.i(TAG, "sendLocation exception");
            e.printStackTrace();
        }
        mServercomm.sendMessage(data);
    }
    public void updateTargetLocation(LatLng location) {
        if (CalculationByDistance(loc, location) < 10000) {
            Log.i(TAG, "updating target location");
            TargetLocMap = location;
            if (TargetLocMap == null) {
                Log.i(TAG, "nullexception");
            }
            Log.i(TAG, "exception");
            if (markerTarget == null && circleTarget == null) {
                markerTarget = mMap.addMarker(new MarkerOptions().position(TargetLocMap).title(TargetID+"\n"+"points: "+targetpoints));
                circleTarget = mMap.addCircle(new CircleOptions()
                        .center(TargetLocMap)
                        .radius(5)
                        .strokeColor(Color.RED));
            } else {
                assert markerTarget != null;
                markerTarget.remove();
                circleTarget.remove();

                markerTarget = mMap.addMarker(new MarkerOptions().position(TargetLocMap).title(TargetID+"\n"+"points: "+targetpoints));
                circleTarget = mMap.addCircle(new CircleOptions()
                        .center(TargetLocMap)
                        .radius(5)
                        .strokeColor(Color.RED));

            }
        } else {
            changeTarget();
        }
    }

    public void updatePursuerLocation(LatLng location) {
        Log.i(TAG, "updating pursuer location");
        Log.i(TAG, "exception");
        markerTarget2 = mMap.addMarker(new MarkerOptions().position(location).title("PURSUER").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));


    }
    public void respondToMessage() {
        Log.i(TAG2, "starting respondtomessage");
        try {
            JSONObject data = mServercomm.getLastMessage();
            String message = data.getString("message");
            String receiver = data.getString("receiver");
            String category = data.getString("category");
            String sender = data.getString("sender");
            Integer points = data.getInt("points");
            Double latitude = data.getDouble("latitude");
            Double longitude = data.getDouble("longitude");
            targetLocation = new LatLng(latitude, longitude);

            Log.i(TAG2, "message");
            Log.i(TAG2, message);
            Log.i(TAG2, "category");
            Log.i(TAG2, category);
            Log.i(TAG2, "receiver");
            Log.i(TAG2, receiver);
            Log.i(TAG2, "sender");
            Log.i(TAG2, sender);
            Log.i(TAG2, "points");
            Log.i(TAG2, points.toString());

            if (top10.length()<10) {
                try {
                    top10.put(sender, points);
                } catch (JSONException e) {
                    Log.i(TAG, "top10 exception");
                    e.printStackTrace();
                }
            } else {
                for (int i = 0; i < top10.length(); i++) {
                    String name = top10.keys().next();
                    List<Integer> scores = new ArrayList<>();
                    List<String> names = new ArrayList<>();
                    names.add(name);
                    scores.add(top10.getInt(name));
                    int Index = findLowestUser(scores);
                    if (points > scores.get(Index)) {
                        top10.remove(names.get(Index));
                        top10.put(sender, points);
                    }

                }
            }

            if (receiver.equals("") && !(sender.equals(myusername))) {
                    Log.i(TAG2, "Condition 1");
                    if (message.equals(NotifyOffline) && sender.equals(TargetID) && !killmoveinprogress) {
                        Log.i(TAG2, "Condition 1.1");
                        changeTarget();
                    } else if (message.equals(getPriorities) && !sender.equals(TargetID) && !noExtraPursuers) {
                        Log.i(TAG2, "Condition 1.2");
                        sendPriority(sender, targetLocation);
                    } else if (message.equals("HardKill") && sender.equals(TargetID)) {
                        hardkill = true;
                    } else if (category.equals(giveNewLocation) && sender.equals(TargetID)){
                        TargetLocActual = targetLocation;
                    }
                } else if (receiver.equals(myusername)) {
                    Log.i(TAG2, "Condition 2");
                    if (category.equals(eliminated)) {
                        Log.i(TAG2, "Condition 2.1");
                        if (pursuers.contains(sender)) {
                            pursuers.remove(sender);
                        }
                        if (message.equals("success")){
                            Toast.makeText(gameMode.this, "You got killed by "+sender, Toast.LENGTH_SHORT).show();
                            mypoints *=0.8;
                            points_score.setText(Integer.toString(mypoints));
                        } else {
                            Toast.makeText(gameMode.this, sender+" tried to kill you - he failed.", Toast.LENGTH_SHORT).show();
                        }
                    } else if (category.equals(pickedTarget)) {
                        Log.i(TAG2, "Condition 2.2");
                        pursuers.add(sender);
                    } else if (message.equals(NotifyOffline)) {
                        Log.i(TAG2, "Condition 2.3");
                        if (pursuers.contains(sender)) {
                            pursuers.remove(sender);
                        }
                    } else if (category.equals(locationUpdate)) {
                        Log.i(TAG2, "Condition 2.4");
                        if (message.equals(getNewLocation)) {
                            if (stopSignal) {
                                Log.i(TAG2, "Condition 2.4.2");
                                warnStopSignal(sender);
                            } else if (Soundpowerup) {
                                record(sender);
                            } else {
                                Log.i(TAG2, "Condition 2.4.1");
                                sendLocation(sender);
                            }
                        } else if (message.equals(giveNewLocation)) {
                            if (sender.equals(TargetID)) {
                                Log.i(TAG2, "Condition 2.4.2");
                                progressDialog.dismiss();
                                missedLocationUpdates = 0;
                                targetpoints = points;
                                try {
                                    markerTarget.remove();
                                    circleTarget.remove();
                                } catch (Exception e) {
                                    Log.i(TAG, e.toString());
                                }
                                updateTargetLocation(targetLocation);
                            } else if (sender.equals(pursuer)) {
                                try {
                                    markerTarget2.remove();
                                } catch (Exception e) {
                                    Log.i(TAG, e.toString());
                                }
                                updatePursuerLocation(targetLocation);
                            }
                        } else if (message.equals("stopsignal")) {
                            progressDialog.dismiss();
                            missedLocationUpdates = 0;
                            targetpoints = points;
                            Toast.makeText(gameMode.this, "Target blocked signal!", Toast.LENGTH_LONG).show();
                        } else if (message.equals("giveNewSound")) {
                            missedLocationUpdates = 0;
                            try {
                                markerTarget.remove();
                                circleTarget.remove();
                            } catch (Exception e) {
                                Log.i(TAG, e.toString());
                            }
                            sound = (byte[]) data.get("sound");
                            speelsound();
                            Toast.makeText(gameMode.this, "music playing", Toast.LENGTH_SHORT).show();
                        }


                    } else if (category.equals(priorityCategory)) {
                        Log.i(TAG2, "Condition 2.5");
                        Log.i(TAG2, "Condition 2.5 part 2");
                        if (Double.parseDouble(message) > prioritylevel) {
                            Log.i(TAG2, "Condition 2.5.1");
                            if (!sender.equals(previoustarget)) {
                                prioritylevel = Double.parseDouble(message);
                                priorityID = sender;
                            }
                        }
                    }
                } else {
                    Log.i(TAG2, "Condition 3");
                }
        } catch (Exception e) {
            Log.i(TAG, e.toString());
        }
    }
    public void warnStopSignal(String sender){
        JSONObject data = new JSONObject();
        try {
            data.put("sender", myusername);
            data.put("receiver",sender);
            data.put("category",locationUpdate);
            data.put("message","stopsignal");
            data.put("points",mypoints);
            data.put("latitude",loc.latitude);
            data.put("longitude",loc.longitude);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mServercomm.sendMessage(data);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        Log.i(TAG,"menu");
        int id = menuItem.getItemId();
        Log.i(TAG,Integer.toString(id));
        if (id == R.id.powerUpOffensiveUpdateFrequency) {
            if (orderPowerUp(powerUpOffensiveUpdateFrequencyPrice)) {
                frequencyUpdate /= 2;
                Log.i(TAG, "powerupOffensiveUpdateFrequency");
                mHandler.removeCallbacks(targetLocationRequest);
                assignTargetLocationRequest();
                targetLocationRequest.run();
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mHandler.removeCallbacks(targetLocationRequest);
                        frequencyUpdate *= 2;
                        assignTargetLocationRequest();
                        targetLocationRequest.run();
                    }
                },320000);
            }

        } else if (id == R.id.powerUpOffensiveEasyKill) {
            if (orderPowerUp(powerUpOffensiveEasyKillPrice)) {
                easykill = true;
            }
        } else if (id == R.id.powerUpDefensiveStopSignal) {
            if (orderPowerUp(powerUpDefensiveStopSignalPrice)) {
                stopSignal = true;
                mHandler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        stopSignal = false;
                    }
                }, 120000);
            }
        } else if (id == R.id.powerUpDefensiveSound) {
            if (orderPowerUp(powerUpDefensiveSoundPrice)) {
                Log.i(TAG, "soundpowerup activated");
                Soundpowerup = true;
                mHandler.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        Log.i(TAG, "soundpowerup disactivated");
                        Soundpowerup = false;
                    }
                }, 300000);
            }
        } else if (id == R.id.powerUpDefensivePursuer) {
            if (pursuers.size()>0) {
                pursuer = pursuers.get(0);
                if (orderPowerUp(powerUpDefensivePursuerPrice)) {
                    FollowPursuer.run();
                    mHandler.postDelayed(removePursuercallback,320000);
                }
            } else {
                Toast.makeText(gameMode.this, "Nobody is Hunting you.", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.powerUpDefensiveHardKill){
            if (orderPowerUp(powerUpDefensiveHardKillPrice)) {
                sendHardKillMessage();
            }
        } else if (id == R.id.powerUpDefensiveNoExtraPursuers){
            if (orderPowerUp(powerUpDefensiveNoExtraPursuersPrice))
            noExtraPursuers = true;
            mHandler.postDelayed(new Runnable(){
                @Override
                public void run(){
                    noExtraPursuers = false;
                }
            },100000);
        }
        return super.onOptionsItemSelected(menuItem);
    }
    public void sendHardKillMessage(){
        JSONObject data = new JSONObject();
        try {
            data.put("sender", myusername);
            data.put("receiver","");
            data.put("category","");
            data.put("message","HardKill");
            data.put("points",mypoints);
            data.put("latitude",loc.latitude);
            data.put("longitude",loc.longitude);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mServercomm.sendMessage(data);
    }
    @Override
    public void onBackPressed(){
        if (pressquit){
            pressquit = false;
            startActivity(new Intent(this, mainInt.class));
        } else {
            Toast.makeText(gameMode.this, "Press again to quit the game.", Toast.LENGTH_SHORT).show();
            pressquit = true;
            mHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    pressquit = false;
                }
            }, 15000);
        }
    }

    public boolean orderPowerUp(Integer price){
        if (mymoney>price){
            mymoney -= price;
            money.setTitle("Baikoins: "+Integer.toString(mymoney));
            Toast.makeText(gameMode.this, "Power-up activated.", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            Toast.makeText(gameMode.this, "Not enough baikoins..", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    public int findLowestUser(List Scores){
        int minIndex = 0;
        for (int i = 0; i < Scores.size(); i++){
            int Score_1 = (Integer) Scores.get(i);
            int Score_2 = (Integer) Scores.get(minIndex);
            if (Score_1 < Score_2){
                minIndex = i;
            }
        }


        return minIndex;
    }

    public void sendEliminationFailedMessage(){
        JSONObject data = new JSONObject();
        try{
            data.put("sender", myusername);
            data.put("receiver",TargetID);
            data.put("category",eliminated);
            data.put("message","failure");
            data.put("points",mypoints);
            data.put("latitude",loc.latitude);
            data.put("longitude",loc.longitude);
        } catch (JSONException e){
            Log.i(TAG,e.toString());
        }
        mServercomm.sendMessage(data);
    }
    public void sendEliminatedmessage(){
        JSONObject data = new JSONObject();
        try{
            data.put("sender", myusername);
            data.put("receiver",TargetID);
            data.put("category",eliminated);
            data.put("message","success");
            data.put("points",mypoints);
            data.put("latitude",loc.latitude);
            data.put("longitude",loc.longitude);
        } catch (JSONException e){
            Log.i(TAG,e.toString());
        }
        mServercomm.sendMessage(data);
    }

    public void killmovefinished(Boolean killmovesuccess){
        if (killmovesuccess) {
            Log.i(TAG, "killed");
            String killedPointsAddedText = "Points added!";
            killMoveText.setText(killedPointsAddedText);
            killMoveText.setVisibility(View.GONE);
            mypoints += 100;
            mymoney += 100;
            money.setTitle("Baikoins: "+Integer.toString(mymoney));
            sendEliminatedmessage();
            points_score.setText(Integer.toString(mypoints));
        } else {
            Log.i(TAG, "not killed");
            killMoveText.setVisibility(View.GONE);
            sendEliminationFailedMessage();
        }
        show_haikuman();
    }


}



