package com.car.common.constants;

public abstract class Constants {
    public static final String PREFERENCE_AUTHORITY = "com.car.naive";
    public static final String PREFERENCE_PATH = "preferences";
    public static final String PREFERENCE_URI = "content://" + PREFERENCE_AUTHORITY + "/" + PREFERENCE_PATH;
    public static final String PREFERENCE_KEY_ENABLE_CLASH = "enable_clash";
    public static final String PREFERENCE_KEY_FIRST_START = "first_start";
}
