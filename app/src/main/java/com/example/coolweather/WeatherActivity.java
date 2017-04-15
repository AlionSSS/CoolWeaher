package com.example.coolweather;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.coolweather.gson.DailyForecast;
import com.example.coolweather.gson.Weather;
import com.example.coolweather.util.HttpUtil;
import com.example.coolweather.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class WeatherActivity extends AppCompatActivity {

    private ScrollView scrollView;
    private TextView tvCity;
    private TextView tvUpdateTime;
    private TextView tvDegree;
    private TextView tvWeatherInfo;
    private LinearLayout llForecast;
    private TextView tvAqi;
    private TextView tvPm25;
    private TextView tvComfort;
    private TextView tvCarWash;
    private TextView tvSport;
    private ImageView ivBing;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        setContentView(R.layout.activity_weather);

        initView();
        initData();
    }

    private void initView() {
        ivBing = (ImageView) findViewById(R.id.iv_bing);
        scrollView = (ScrollView) findViewById(R.id.scroll_view);
        tvCity = (TextView) findViewById(R.id.tv_city);
        tvUpdateTime = (TextView) findViewById(R.id.tv_update_time);
        tvDegree = (TextView) findViewById(R.id.tv_degree);
        tvWeatherInfo = (TextView) findViewById(R.id.tv_weather_info);
        llForecast = (LinearLayout) findViewById(R.id.ll_forecast);
        tvAqi = (TextView) findViewById(R.id.tv_aqi);
        tvPm25 = (TextView) findViewById(R.id.tv_pm25);
        tvComfort = (TextView) findViewById(R.id.tv_comfort);
        tvCarWash = (TextView) findViewById(R.id.tv_car_wash);
        tvSport = (TextView) findViewById(R.id.tv_sport);
    }

    private void initData() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //背景处理
        String bingPic = prefs.getString("bing_pic", null);
        if (bingPic != null) {
            Glide.with(this).load(bingPic).into(ivBing);
        } else {
            loadBingPic();
        }
        //数据处理
        String weatherString = prefs.getString("weather", null);
        if (weatherString != null) {
            //有缓存时，直接解析天气数据
            Weather weather = Utility.handleWeatherResponse(weatherString);
            showWeatherInfo(weather);
        } else {
            //没缓存时，去服务器查询天气
            String weatherId = getIntent().getStringExtra("weather_id");
            scrollView.setVisibility(View.INVISIBLE);
            requestWeather(weatherId);
        }
    }

    /**
     * 加载bing每日一图
     */
    private void loadBingPic() {
        String bingPicUrl = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(bingPicUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseString = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager
                        .getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic", responseString);
                editor.apply();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(responseString).into(ivBing);
                    }
                });
            }
        });
    }

    /**
     * 处理并展示Weather实体类中的数据
     *
     * @param weather 实体类
     */
    private void showWeatherInfo(Weather weather) {
        Weather.HeWeather heWeather = weather.heWeather.get(0);

        tvCity.setText(heWeather.basic.city);
        tvUpdateTime.setText(heWeather.basic.update.loc.split(" ")[1]);
        tvDegree.setText(heWeather.now.tmp + "℃");
        tvWeatherInfo.setText(heWeather.now.cond.txt);

        llForecast.removeAllViews();

        for (DailyForecast forecast : heWeather.dailyForecastList) {
            View view = LayoutInflater.from(this).inflate(R.layout.item_forecast, llForecast, false);
            ViewHolder holder = new ViewHolder(view);
            holder.tvDate.setText(forecast.date);
            holder.tvInfo.setText(forecast.cond.txt_d);
            holder.tvMax.setText(forecast.tmp.max);
            holder.tvMin.setText(forecast.tmp.min);
            llForecast.addView(view);
        }

        if (heWeather.aqi != null) {
            tvAqi.setText(heWeather.aqi.city.aqi);
            tvPm25.setText(heWeather.aqi.city.pm25);
        }

        tvComfort.setText("舒适度：" + heWeather.suggestion.comf.txt);
        tvCarWash.setText("洗车指数：" + heWeather.suggestion.cw.txt);
        tvSport.setText("运动建议：" + heWeather.suggestion.sport.txt);

        scrollView.setVisibility(View.VISIBLE);
    }

    private class ViewHolder {

        private final TextView tvDate;
        private final TextView tvInfo;
        private final TextView tvMax;
        private final TextView tvMin;

        ViewHolder(View view) {
            tvDate = (TextView) view.findViewById(R.id.tv_date);
            tvInfo = (TextView) view.findViewById(R.id.tv_info);
            tvMax = (TextView) view.findViewById(R.id.tv_max);
            tvMin = (TextView) view.findViewById(R.id.tv_min);
        }
    }


    /**
     * 根据天气id请求城市天气信息
     *
     * @param weatherId 天气id
     */
    private void requestWeather(String weatherId) {
        String weatherUrl = "http://guolin.tech/api/weather?cityid="
                + weatherId + "&key=6d2738cab4d64502abd536967be7f2d2";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseString = response.body().string();
                final Weather weather = Utility.handleWeatherResponse(responseString);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && weather.heWeather.get(0).status.equals("ok")) {
                            SharedPreferences.Editor editor = PreferenceManager
                                    .getDefaultSharedPreferences(WeatherActivity.this).edit();
                            editor.putString("weather", responseString);
                            editor.apply();

                            showWeatherInfo(weather);
                        } else {
                            Toast.makeText(WeatherActivity.this, "获取天气信息失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
