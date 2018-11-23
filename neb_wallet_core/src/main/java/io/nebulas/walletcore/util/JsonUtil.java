package io.nebulas.walletcore.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.lang.reflect.Modifier;
import java.lang.reflect.Type;

/**
 * Created by guoping on 2017/4/24.
 */

public class JsonUtil {

    private static Gson sGson = new GsonBuilder()
            .disableHtmlEscaping()
            .excludeFieldsWithModifiers(Modifier.PRIVATE, Modifier.PROTECTED, Modifier.STATIC)
            .create();

    public static String serialize(Object obj) {
        return sGson.toJson(obj);
    }

    public static <T> T deserialize(String json, Type typeOfT) {
        return sGson.fromJson(json, typeOfT);
    }

}
