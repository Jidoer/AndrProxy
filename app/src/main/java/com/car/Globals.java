package com.car;

import android.content.Context;
import android.os.Environment;

import java.io.File;

public class Globals {

    private static String cacheDir;
    private static String filesDir;
    private static String externalFilesDir;
    private static NaiveConfig naiveConfigInstance;

    public static void Init(Context ctx) {
        cacheDir = ctx.getCacheDir().getAbsolutePath();
        filesDir = ctx.getFilesDir().getAbsolutePath();
        File externalDocDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File ExternalFileDir = new File(externalDocDir, "Naive");
        externalFilesDir = ExternalFileDir.getAbsolutePath();
        naiveConfigInstance = new NaiveConfig();
    }

    public static String getCaCertPath() {
        return PathHelper.combine(cacheDir, "cacert.pem");
    }

    public static String getCountryMmdbPath() {
        return PathHelper.combine(filesDir, "Country.mmdb");
    }

    public static String getClashConfigPath() {
        return PathHelper.combine(filesDir, "config.yaml");
    }

    public static String getNaiveConfigPath() {
        return PathHelper.combine(filesDir, "config.json");
    }

    public static String getNaiveConfigListPath() {
        return PathHelper.combine(filesDir, "config_list.json");
    }

    public static String getPreferencesFilePath() {
        return PathHelper.combine(filesDir, "preferences.txt");
    }

    public static String getExemptedAppListPath() {
        return PathHelper.combine(externalFilesDir, "exempted_app_list.txt");
    }

    public static void setNaiveConfigInstance(NaiveConfig config) {
        naiveConfigInstance = config;
    }

    public static NaiveConfig getNaiveConfigInstance() {
        return naiveConfigInstance;
    }
}
