package com.example.coolweather.gson;

/**
 * 当前空气质量情况
 *
 * @author ALion on 2017/4/14 15:32
 */

public class AQI {

    public City city;

    public class City {

        public String aqi;

        public String pm25;

    }
}
