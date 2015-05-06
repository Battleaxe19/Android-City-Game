package tallen.edu.weber.citygame;

/**
 * Created by Tyler on 3/23/2015.
 */
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import org.json.JSONObject;
import org.w3c.dom.Comment;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

// implement SensorListener
public class CompassActivity extends Activity implements SensorEventListener, LocationListener {

    final String _logTag = "Monitor variables";

    SQLiteHelper myDbHelper;

    City selectedCity;

    //Alpha value for our low pass filter
    static final float ALPHA = 0.15f; // if ALPHA = 1 OR 0, no filter applies.

    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private Sensor mMagnetometer;
    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetometer = new float[3];
    private boolean mLastAccelerometerSet = false;
    private boolean mLastMagnetometerSet = false;
    private float[] mR = new float[9];
    private float[] mOrientation = new float[3];
    CompassView compView;
    TextView cityView;
    private float finalAzimuth;

    //Latitude and longitude for both current location and city location
    double currLat;
    double currLong;
    double cityLat;
    double cityLong;

    protected LocationManager locationManager;
    protected LocationListener locationListener;


    /**--------------------  ON CREATE  --------------------------------------------------*/
    /**                                                                                   */
    /**-----------------------------------------------------------------------------------*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_compass);
        compView = (CompassView) findViewById(R.id.compassView);
        cityView = (TextView) findViewById(R.id.tvCityName);

        myDbHelper = new SQLiteHelper(this);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

        mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mMagnetometer = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        try {

            myDbHelper.createDataBase();

        } catch (IOException ioe) {

            throw new Error("Unable to create database");

        }

        try {

            myDbHelper.openDataBase();

        }catch(SQLException sqle){

            try {
                throw sqle;
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

        //DATABASE STUFF
        List<City> values = myDbHelper.getAllCities();
        Collections.shuffle(values);

        selectedCity = values.get(0);

        cityLat = selectedCity.getLat();
        cityLong = selectedCity.getLng();
        //cityLat = 40.7608333;
        //cityLong = -111.8902778;

        cityView.setText(selectedCity.getName());

    }

    /**--------------------  REQUIRED METHODS  -------------------------------------------*/
    /**                                                                                   */
    /**-----------------------------------------------------------------------------------*/

    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, mMagnetometer, SensorManager.SENSOR_DELAY_GAME);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this, mAccelerometer);
        mSensorManager.unregisterListener(this, mMagnetometer);
        myDbHelper.close();
    }

    /**--------------------  FUN SENSOR STUFF  -------------------------------------------*/
    /**                                                                                   */
    /**-----------------------------------------------------------------------------------*/
    public void onSensorChanged(SensorEvent event) {
        //If Accelerometer then place the values into mLastAccelerometer and set it to true
        if (event.sensor == mAccelerometer) {
            mLastAccelerometer = lowPass(event.values.clone(), mLastAccelerometer);
            mLastAccelerometerSet = true;

            //If Magnetometer then place the values into mLastMagnetometer and set it to true
        } else if (event.sensor == mMagnetometer) {
            mLastMagnetometer = lowPass(event.values.clone(), mLastMagnetometer);
            mLastMagnetometerSet = true;
        }

        //When both sensors are updated DO FUN STUFF
        if (mLastAccelerometerSet && mLastMagnetometerSet) {
            //places info from rotationmatrix into mR
            SensorManager.getRotationMatrix(mR, null, mLastAccelerometer, mLastMagnetometer);

            //Gets the orientation using mR and passing them into mOrientation
            SensorManager.getOrientation(mR, mOrientation);

            //value[0] from getOrientation is the azimuth. Pass this value into azimuthInRadians
            float azimuthInRadians = mOrientation[0];
            float azimuthInDegrees = (float)(Math.toDegrees(azimuthInRadians)+360)%360;
            Log.d(_logTag, "Monitor variables - AzimuthInDegrees : " + azimuthInDegrees);
            finalAzimuth = azimuthInDegrees;
            compView.setDirection((int) - azimuthInDegrees);

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**--------------------  LOW PASS FILTER  --------------------------------------------*/
    /**                                                                                   */
    /**-----------------------------------------------------------------------------------*/

    //A low pass filter to cut out noise in our sensors making the animation smooth
    protected float[] lowPass( float[] input, float[] output ) {
        if ( output == null ) return input;
        for ( int i=0; i<input.length; i++ ) {
            output[i] = output[i] + ALPHA * (input[i] - output[i]);
        }
        return output;
    }

    /**--------------------  BEARING METHOD  ---------------------------------------------*/
    /**                                                                                   */
    /**-----------------------------------------------------------------------------------*/

    protected double getBearing(double latFrom, double longFrom, double latTo, double longTo) {

        double dLon = (longTo - longFrom);
        double y = Math.sin(dLon) * Math.cos(latTo);
        double x = Math.cos(latFrom) * Math.sin(latTo) - Math.sin(latFrom) * Math.cos(latTo) * Math.cos(dLon);
        double brng = Math.toDegrees((Math.atan2(y, x)));
        brng = (360 - ((brng + 360) % 360));
        return brng;
    }

    /**----------------------------  BUTTON METHOD  --------------------------------------*/
    /**                          CALLED WHEN USER HITS BUTTON                             */
    /**-----------------------------------------------------------------------------------*/
    public void foundIt(View view) {

        double bearing = getBearing(currLat, currLong, cityLat, cityLong);
        int score = (int) Math.abs(bearing - finalAzimuth);
        Log.d(_logTag, "BEARING : " + bearing);
        Intent intent = new Intent(this, ScoreActivity.class);
        intent.putExtra("score", String.valueOf(score));
        startActivity(intent);
    }


    /**------------------------  MORE REQUIRED METHODS  ----------------------------------*/
    /**                                                                                   */
    /**-----------------------------------------------------------------------------------*/

    @Override
    public void onLocationChanged(Location location) {
        currLat = location.getLatitude();
        currLong = location.getLongitude();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}