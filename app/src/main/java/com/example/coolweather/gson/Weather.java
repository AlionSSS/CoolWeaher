package com.example.coolweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * 总的天气实例
 *
 * @author ALion on 2017/4/14 15:48
 */

public class Weather {

    @SerializedName("HeWeather5")
    public ArrayList<HeWeather> heWeather;

    public class HeWeather {

        public String status;

        public Basic basic;

        public AQI aqi;

        public Now now;

        public Suggestion suggestion;

        @SerializedName("daily_forecast")
        public ArrayList<DailyForecast> dailyForecastList;

    }

}
