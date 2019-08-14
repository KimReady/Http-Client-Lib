package com.naver.httpclientlib;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

final class GsonConverterFactory {
    private final Gson gson;

    public static GsonConverterFactory create(GsonBuilder gsonBuilder) {
        return new GsonConverterFactory(gsonBuilder);
    }

    private GsonConverterFactory(GsonBuilder gsonBuilder) {
        this.gson = gsonBuilder.create();
    }

    Converter<?, ?> converter(Type type) {
        if(type == String.class) {
            return new GsonConverter<>(gson);
        } else {
            TypeAdapter<?> typeAdapter = gson.getAdapter(TypeToken.get(type));
            return new GsonConverter<>(gson, typeAdapter);
        }
    }
}