package edu.northeastern.a6group7;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
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
    private String iconUrl;
    private Switch convertTemp;
    private boolean showFahrenheit = false;
    private Double celsiusTemp = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        cityInput = findViewById(R.id.cityInput);
        weatherResultText = findViewById(R.id.weatherResultText);
        weatherIcon = findViewById(R.id.weatherIcon);
        fetchWeatherButton = findViewById(R.id.fetchWeatherButton);
        convertTemp = findViewById(R.id.toggleTemp);
        convertTemp.setOnCheckedChangeListener(((buttonView, isChecked) ->{

            showFahrenheit = isChecked;
            if (celsiusTemp != null){
                String condition = weatherResultText.getText().toString().split("\n")[2].replace("Condition: ", "");
                String windSpeed = weatherResultText.getText().toString().split("\n")[3].replace("Wind Speed: ", "").replace("m/s", "");

                updateWeatherDisplay(cityInput.getText().toString(), celsiusTemp, condition, windSpeed);
            }
    }));
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

        if (savedInstanceState != null) {
            String savedText = savedInstanceState.getString("savedWeatherText");
            if (savedText != null){
                weatherResultText.setText(savedText);
            }
            String savedIconUrl = savedInstanceState.getString("savedIconUrl");
            if (savedIconUrl != null && !savedIconUrl.isEmpty()) {
                Picasso.get().load(savedIconUrl).into(weatherIcon);
                iconUrl = savedIconUrl;
                weatherIcon.setVisibility(View.VISIBLE);
            }
            showFahrenheit = savedInstanceState.getBoolean("savedToggle");
            convertTemp.setChecked(showFahrenheit);
            convertTemp.setVisibility(View.VISIBLE);
            celsiusTemp = savedInstanceState.getDouble("savedCelsiusTemp");

        }
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("savedWeatherText", weatherResultText.getText().toString());
        outState.putString("savedIconUrl", iconUrl);
        outState.putBoolean("savedToggle", showFahrenheit);
        if (celsiusTemp != null) {
            outState.putDouble("savedCelsiusTemp", celsiusTemp);
        }
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
                conn.setConnectTimeout(2000);
                conn.setReadTimeout(2000);


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

                Double temperature = main.getDouble("temp");
                String description = weather.getString("description");
                String iconCode = weather.getString("icon");
                String windSpeed = wind.getString("speed");
                celsiusTemp = temperature;

                iconUrl = "https://openweathermap.org/img/wn/" + iconCode + "@2x.png";

                runOnUiThread(() -> {
                    loadingEnd(); // added to end loading animation
//
                    updateWeatherDisplay(city, temperature, description, windSpeed);
                    Picasso.get().load(iconUrl).into(weatherIcon);
                    weatherIcon.setVisibility(View.VISIBLE); // bring back weather icon
                    convertTemp.setVisibility(View.VISIBLE); //bring back toggle
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

    private void updateWeatherDisplay(String cityName, Double celsiusTemp, String description, String windSpeed ){
        if (celsiusTemp == null) return;
        String showTemp = showFahrenheit
                ? String.format("%.1f°F", convertToFahrenheit(celsiusTemp))
                : String.format("%.1f°C", celsiusTemp);

        String display = "City: " + cityName + "\n" +
                "Temperature: " + showTemp + "\n" +
                "Condition: " + description + "\n" +
                "Wind Speed: " + windSpeed + "m/s";

        weatherResultText.setText(display);
    }
    // Celsius to Fahrenheit formula
    private double convertToFahrenheit(double celsius) {
        return (celsius * 9 / 5) + 32;
    }
}
