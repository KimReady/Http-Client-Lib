package com.naver.httpclientlib.converter;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.naver.httpclientlib.annotation.SkipThis;

import java.lang.reflect.Type;

public class GsonConverterFactory {
    private final Gson gson;

    public static GsonConverterFactory create() {
        return new GsonConverterFactory();
    }

    private GsonConverterFactory() {
        this.gson = new GsonBuilder()
                .setExclusionStrategies(new SkipThisExclustionStrategy())
                .create();
    }

    public Converter<?, ?> converter(Type type) {
        TypeAdapter<?> typeAdapter = gson.getAdapter(TypeToken.get(type));
        return new GsonConverter<>(gson, typeAdapter);
    }

    private class SkipThisExclustionStrategy implements ExclusionStrategy {
        @Override
        public boolean shouldSkipField(FieldAttributes fieldAttributes) {
            return fieldAttributes.getAnnotation(SkipThis.class) != null;
        }

        @Override
        public boolean shouldSkipClass(Class<?> aClass) {
            return aClass.getAnnotation(SkipThis.class) != null;
        }
    }
}