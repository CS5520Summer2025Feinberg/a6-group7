package edu.northeastern.a6group7;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import android.os.Handler; // Added to schedule repeated tasks for active indicator
import android.view.View;  // Added for View.GONE constant for active indicator

public class Weather extends AppCompatActivity {

    private EditText cityInput;
    private TextView weatherResultText;
    private ImageView weatherIcon;
    private Button fetchWeatherButton;

    private final String API_KEY = "af3bf0c507c794388070eccb96807fc6";

    // Added declared fields below for active indicator
    private Handler handler; //  to help schedule text animation
    private Runnable loadingRunnable; // for dot dot updates
    private int loadingDots = 0; // counter for dots displayed
    private final String LOADING_BASE_TEXT = "Loading weather data"; // The base text "Loading weather data"

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        cityInput = findViewById(R.id.cityInput);
        weatherResultText = findViewById(R.id.weatherResultText);
        weatherIcon = findViewById(R.id.weatherIcon);
        fetchWeatherButton = findViewById(R.id.fetchWeatherButton);

        // added below for active indicator, initializing handler
        handler = new Handler(); // Initialize the Handler here

        fetchWeatherButton.setOnClickListener(view -> {
            String city = cityInput.getText().toString().trim();
            if (!city.isEmpty()) {
                fetchWeather(city);
            } else {
                loadingEnd(); // end loading message
                weatherResultText.setText("Please enter a city name.");
            }
        });
    }

    private void fetchWeather(String city) {
        // replacing redundant, static loading message below for after active loading indicator
        // weatherResultText.setText("Loading weather data...");
        loadingStart();

        new Thread(() -> {
            try {
                String apiUrl = "https://api.openweathermap.org/data/2.5/weather?q="
                        + city + "&appid=af3bf0c507c794388070eccb96807fc6&units=metric";

                URL url = new URL(apiUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }

                JSONObject jsonResponse = new JSONObject(result.toString());
                JSONObject main = jsonResponse.getJSONObject("main");
                JSONObject weather = jsonResponse.getJSONArray("weather").getJSONObject(0);
                JSONObject wind = jsonResponse.getJSONObject("wind");

                String temperature = main.getString("temp");
                String description = weather.getString("description");
                String iconCode = weather.getString("icon");
                String windSpeed = wind.getString("speed");

                String iconUrl = "https://openweathermap.org/img/wn/" + iconCode + "@2x.png";

                runOnUiThread(() -> {
                    loadingEnd(); // added to end loading animation
                    weatherResultText.setText(
                            "City: " + city + "\n" +
                                    "Temperature: " + temperature + "°C\n" +
                                    "Condition: " + description + "\n" +
                                    "Wind Speed: " + windSpeed + " m/s"
                    );
                    Picasso.get().load(iconUrl).into(weatherIcon);
                    weatherIcon.setVisibility(View.VISIBLE); // bring back weather icon
                });

                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> weatherResultText.setText("Failed to fetch weather data."));
            }
        }).start();
    }

    // loading text animation added below
    private void loadingStart() {
        loadingDots = 0; // dots counter
        weatherResultText.setVisibility(View.VISIBLE); // update text to show animation
        weatherIcon.setVisibility(View.GONE); // Hide weather icon when loading

        loadingRunnable = new Runnable() {
            @Override
            public void run() {
                loadingDots = (loadingDots + 1) % 4;
                StringBuilder dots = new StringBuilder();
                for (int i = 0; i < loadingDots; i++) {
                    dots.append("."); // Add a dot to loading
                }
                // Update text with dots by half a second
                weatherResultText.setText(LOADING_BASE_TEXT + dots.toString());

                handler.postDelayed(loadingRunnable, 500);
            }
        };
        // post to trigger animation loop
        handler.post(loadingRunnable);
    }

    private void loadingEnd() {
        if (loadingRunnable != null) {
            // remove animation loop in handler queue
            handler.removeCallbacks(loadingRunnable);
            loadingRunnable = null;
        }
    }
}
