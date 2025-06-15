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

public class Weather extends AppCompatActivity {

    private EditText cityInput;
    private TextView weatherResultText;
    private ImageView weatherIcon;
    private Button fetchWeatherButton;

    private final String API_KEY = "af3bf0c507c794388070eccb96807fc6";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        cityInput = findViewById(R.id.cityInput);
        weatherResultText = findViewById(R.id.weatherResultText);
        weatherIcon = findViewById(R.id.weatherIcon);
        fetchWeatherButton = findViewById(R.id.fetchWeatherButton);

        fetchWeatherButton.setOnClickListener(view -> {
            String city = cityInput.getText().toString().trim();
            if (!city.isEmpty()) {
                fetchWeather(city);
            } else {
                weatherResultText.setText("Please enter a city name.");
            }
        });
    }

    private void fetchWeather(String city) {
        weatherResultText.setText("Loading weather data...");
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
                    weatherResultText.setText(
                            "City: " + city + "\n" +
                                    "Temperature: " + temperature + "°C\n" +
                                    "Condition: " + description + "\n" +
                                    "Wind Speed: " + windSpeed + " m/s"
                    );
                    Picasso.get().load(iconUrl).into(weatherIcon);
                });

                conn.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> weatherResultText.setText("Failed to fetch weather data."));
            }
        }).start();
    }
}
