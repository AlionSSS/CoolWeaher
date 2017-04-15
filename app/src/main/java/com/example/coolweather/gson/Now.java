package com.example.coolweather.gson;

/**
 * 当前天气信息
 *
 * @author ALion on 2017/4/14 15:33
 */

public class Now {

    public String tmp;

    public Cond cond;

    public class Cond {
        public String txt;
    }
}
