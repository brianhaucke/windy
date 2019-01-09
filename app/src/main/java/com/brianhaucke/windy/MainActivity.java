package com.brianhaucke.windy;


import android.content.Context;
import android.databinding.DataBindingUtil;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationProvider;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.brianhaucke.windy.databinding.ActivityMainBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    
    //    public LocationManager lm = new LocationManager("");
    //public Location location = new Location("");

    //private FusedLocationProviderClient mFusedLocationClient;


    public static final String TAG = MainActivity.class.getSimpleName();

    private CurrentWeather currentWeather;
    // private imageView iconImageView;

    double latitude = 37.8267; //location.getLatitude();//
    double longitude = -122.4233; //location.getLongitude();//

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getForecast(latitude, longitude);


        Log.d(TAG, "Main UI code is running, hooray!");

    }

    private void getForecast(double latitude, double longitude) {
        final ActivityMainBinding binding = DataBindingUtil.setContentView(MainActivity.this,
                R.layout.activity_main);

        TextView darkSky = findViewById(R.id.darkSkyAttribution);

        darkSky.setMovementMethod(LinkMovementMethod.getInstance());

        String apiKey = "2d1e0c124615eee0580123a6d9e05c97";




        String forecastURL = "https://api.darksky.net/forecast/"
                + apiKey + "/" + latitude + "," + longitude;

        if (isNetworkAvailable()) {


        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(forecastURL)
                .build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String jsonData = response.body().string();
                    Log.v(TAG, jsonData);
                    if (response.isSuccessful()){
                        currentWeather = getCurrentDetails(jsonData);

                        CurrentWeather displayWeather = new CurrentWeather(
                            currentWeather.getLocationLabel(),
                            currentWeather.getIcon(),
                            currentWeather.getTime(),
                            currentWeather.getTemperature(),
                            currentWeather.getHumidity(),
                            currentWeather.getPrecipChance(),
                            currentWeather.getSummary(),
                            currentWeather.getTimeZone()
                        );

                        binding.setWeather(displayWeather);


                    }
                    else {
                        alertUserAboutError();

                    }
                } catch (IOException e) {
                    Log.e(TAG, "IO Exception Caught: ", e);
                } catch (JSONException e) {
                    Log.e(TAG, "JSON Exception Caught: ", e);
                }
            }
        });
        }
    }

    private CurrentWeather getCurrentDetails(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);

        String timezone = forecast.getString("timezone");
        Log.i(TAG, "From JSON: " + timezone);

        JSONObject currently = forecast.getJSONObject("currently");

        CurrentWeather currentWeather = new CurrentWeather();

        currentWeather.setHumidity(currently.getDouble("humidity"));
        currentWeather.setTime(currently.getLong("time"));
        currentWeather.setIcon(currently.getString("icon"));
        currentWeather.setLocationLabel("Alcatraz Island, CA");
        currentWeather.setPrecipChance(currently.getDouble("precipProbability"));
        currentWeather.setSummary(currently.getString("summary"));
        currentWeather.setTemperature(currently.getDouble("temperature"));
        currentWeather.setTimeZone(timezone);

        Log.d(TAG, currentWeather.getFormattedTime());


        return currentWeather;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()){
            isAvailable = true;
        }
        else {
            //Toast.makeText(this, getString(R.string.network_unavailable_message), Toast.LENGTH_LONG).show();
            alertUserAboutError();

        }
        return isAvailable;
    }

    private void alertUserAboutError() {
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(getFragmentManager(), "error_dialog");
    }

    public void refreshOnClick(View view) {
        Toast.makeText(this, "Refreshing data", Toast.LENGTH_SHORT).show();
        getForecast(latitude, longitude);
    }
}
