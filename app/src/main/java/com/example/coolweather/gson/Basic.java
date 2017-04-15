package com.example.coolweather.gson;

/**
 * 城市基本信息
 *
 * @author ALion on 2017/4/14 15:20
 */

public class Basic {

    public String city;

    public String id;

    public Update update;

    public class Update {
        public String loc;
    }

}
