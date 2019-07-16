package com.naver.httpclientlib.converter;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class GsonConverterFactory {
    private Gson gson;

    public static GsonConverterFactory create() {
        return create(new Gson());
    }

    public static GsonConverterFactory create(Gson gson) {
        return new GsonConverterFactory(gson);
    }
    private GsonConverterFactory(Gson gson) {
        this.gson = gson;
    }

    public Converter<?, ?> converter(Type type) {
        TypeAdapter<?> typeAdapter = gson.getAdapter(TypeToken.get(type));
        return new GsonConverter<>(gson, typeAdapter);
    }
}