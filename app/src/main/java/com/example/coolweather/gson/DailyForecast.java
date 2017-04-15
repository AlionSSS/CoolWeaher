package com.example.coolweather.gson;

/**
 * 未来几天的天气信息
 *
 * @author ALion on 2017/4/14 15:40
 */

public class DailyForecast {

    public String date;

    public Temperature tmp;

    public class Temperature {
        public String max;

        public String min;
    }

    public Cond cond;

    public class Cond {
        public String txt_d;
    }



}
