package com.example.util;

import com.squareup.javapoet.ClassName;

/**
 * Created by hxh on 2017/7/5.
 */
public class TypeUtil {
    public static ClassName ANDROID_VIEW = ClassName.get("android.view","View");
    public static ClassName ANDROID_ACTIVITY = ClassName.get("android.app","Activity");
    public static ClassName ANDROID_ACTIVITYCOMPAT = ClassName.get("android.app","Activity");
    public static ClassName ANDROID_INTENT = ClassName.get("android.content","Intent");


    public static ClassName ANDROID_CONTEXT = ClassName.get("android.content","Context");
    public static ClassName ANDROID_PARCEABLE = ClassName.get("android.os","Parcelable");
    public static ClassName ANDROID_SERIALIZABLE = ClassName.get("java.io","Serializable");
}
