package com.assistne.mywallet.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by assistne on 15/9/17.
 */
public class GlobalUtils {

    public static final String LOG_TAG = "test utils";

/* 将float转换成价格字符串
 *  price: float类型
 *  isSplit: 是否用‘,’分割,如 1234.00 -> 1,234.00
 *  */
    public static String formatPrice(float price, boolean isSplit) {
        DecimalFormat format = new DecimalFormat(isSplit ? "##,##0.00" : "####0.00");
        return format.format(price);
    }


    public static void updateLocation(final Context context) {
        Log.d(LOG_TAG, "update location");
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        List<String> providerList = locationManager.getProviders(true);
        String provider;
        if (providerList.contains(LocationManager.NETWORK_PROVIDER)) {
            provider = LocationManager.NETWORK_PROVIDER;
        } else if (providerList.contains(LocationManager.GPS_PROVIDER)) {
            provider = LocationManager.GPS_PROVIDER;
        } else {
            // TODO: 15/9/23
            Log.d(LOG_TAG, "return");
            return;
        }
        Location location = locationManager.getLastKnownLocation(provider);
        if (location != null) {
            saveAddress(context, location);
        }

        locationManager.requestLocationUpdates(provider, 10000, 100, new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                saveAddress(context, location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        });
    }

    public static String PREF_LOCATION = "pref_location";
    public static void saveAddress(final Context context, final Location location) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                String address = "";
                try {
                    StringBuilder url = new StringBuilder();
                    String baidu = "http://api.map.baidu.com/geocoder/v2/?ak=cqbWIupHRdCRZcHtaKq0aDsk&callback=renderReverse&location=";
                    url.append(baidu);
                    url.append(location.getLatitude()).append(",");
                    url.append(location.getLongitude());
                    url.append("&output=xml&pois=1");
                    connection = (HttpURLConnection)new URL(url.toString()).openConnection();
                    Log.d(LOG_TAG, connection.getResponseCode()+"");
                    if (connection.getResponseCode() == 200) {
                        InputStream in = connection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }
                        XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();
                        parser.setInput(new StringReader(response.toString()));
                        int eventType = parser.getEventType();
                        String street = "";
                        String district = "";
                        while (eventType != XmlPullParser.END_DOCUMENT) {
                            String nodeName = parser.getName();
                            switch (eventType) {
                                case XmlPullParser.START_TAG: {
                                    if ("street".equals(nodeName)) {
                                        street = parser.nextText();
                                    } else if ("district".equals(nodeName)) {
                                        district = parser.nextText();
                                    }
                                    break;
                                }
                                default:
                                    break;
                            }
                            if (!street.equals("") && !district.equals("")) {
                                break;
                            }
                            eventType = parser.next();
                        }
                        address = district + street;
                        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString(PREF_LOCATION, address);
                        editor.apply();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (connection != null) {
                        connection.disconnect();
                    }
                }
            }
        }
        ).start();
    }

    public static String getLocation(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(PREF_LOCATION, "");
    }

    public static String getCurrentDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd kk:mm", Locale.CHINA);
        return df.format(c.getTime());
    }

    public static long getCurrentDate(String date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy.MM.dd kk:mm", Locale.CHINA);
        try {
            return df.parse(date).getTime();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public static Date getDateFromMills(long mills) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(mills);
        return calendar.getTime();
    }

}
