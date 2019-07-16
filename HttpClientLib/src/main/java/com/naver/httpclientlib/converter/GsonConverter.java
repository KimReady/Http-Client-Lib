package com.naver.httpclientlib.converter;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.naver.httpclientlib.Utils;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.Type;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;

public final class GsonConverter<ReturnType, RequestType> implements Converter<ReturnType, RequestType> {
    private static final MediaType MEDIA_TYPE = MediaType.get("application/json; charset=UTF-8");

    private TypeAdapter<RequestType> requestAdapter;
    private TypeAdapter<ReturnType> responseAdapter;
    private Buffer buffer;

    private Gson gson;

    GsonConverter(Gson gson, TypeAdapter<ReturnType> adapter) {
        this.responseAdapter = adapter;
        this.gson = gson;
        this.buffer = new Buffer();
        this.requestAdapter = gson.getAdapter(new TypeToken<RequestType>(){});
    }

    @Override
    public okhttp3.RequestBody convertRequestBody(RequestType requestObj) throws IOException {
        Writer writer = new OutputStreamWriter(buffer.outputStream(), "UTF-8");
        JsonWriter jsonWriter = gson.newJsonWriter(writer);
        requestAdapter.write(jsonWriter, requestObj);
        jsonWriter.close();
        return RequestBody.create(MEDIA_TYPE, buffer.readByteString());
    }

    @Override
    public ReturnType convertResponseBody(okhttp3.ResponseBody responseBody) throws IOException {
        JsonReader jsonReader = gson.newJsonReader(responseBody.charStream());
        try {
            return responseAdapter.read(jsonReader);
        } finally {
            responseBody.close();
        }
    }


}
