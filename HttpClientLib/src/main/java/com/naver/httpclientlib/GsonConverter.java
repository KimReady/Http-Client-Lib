package com.naver.httpclientlib;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;

final class GsonConverter<ReturnType, RequestType> implements Converter<ReturnType, RequestType> {
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
    public okhttp3.RequestBody convertRequestBody(MediaType contentType, RequestType requestObj) throws IOException {
        if(contentType == null) {
            contentType = MediaType.get("application/json; charset=UTF-8");
        }
        Writer writer = new OutputStreamWriter(buffer.outputStream(), "UTF-8");
        JsonWriter jsonWriter = gson.newJsonWriter(writer);
        requestAdapter.write(jsonWriter, requestObj);
        jsonWriter.close();
        return RequestBody.create(contentType, buffer.readByteString());
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
