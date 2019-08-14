package com.naver.httpclientlib;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;

final class GsonConverter<ReturnType, RequestType> implements Converter<ReturnType, RequestType> {
    private TypeAdapter<RequestType> requestAdapter;
    private TypeAdapter<ReturnType> responseAdapter;
    private Buffer buffer;

    private Gson gson;
    private boolean isString;

    GsonConverter(Gson gson) {
        this.gson = gson;
        this.buffer = new Buffer();
        this.isString = true;
    }

    GsonConverter(Gson gson, TypeAdapter<ReturnType> adapter) {
        this.responseAdapter = adapter;
        this.gson = gson;
        this.buffer = new Buffer();
        this.requestAdapter = gson.getAdapter(new TypeToken<RequestType>(){});
        this.isString = false;
    }

    @Override
    public okhttp3.RequestBody convertRequestBody(MediaType contentType, RequestType requestObj) throws IOException {
        if(contentType == null) {
            contentType = MediaType.get("application/json; charset=UTF-8");
        }
        Writer writer = new OutputStreamWriter(buffer.outputStream(), StandardCharsets.UTF_8);
        JsonWriter jsonWriter = gson.newJsonWriter(writer);
        requestAdapter.write(jsonWriter, requestObj);
        jsonWriter.close();
        return RequestBody.create(contentType, buffer.readByteString());
    }

    @Override
    public ReturnType convertResponseBody(okhttp3.ResponseBody responseBody) throws IOException {
        if(isString) {
            StringBuilder builder = new StringBuilder();
            BufferedReader br = new BufferedReader(responseBody.charStream());
            String line;
            while((line = br.readLine()) != null) {
                builder.append(line);
            }
            return (ReturnType) builder.toString();
        }

        JsonReader jsonReader = gson.newJsonReader(responseBody.charStream());
        try {
            return responseAdapter.read(jsonReader);
        } finally {
            jsonReader.close();
            responseBody.close();
        }
    }
}
