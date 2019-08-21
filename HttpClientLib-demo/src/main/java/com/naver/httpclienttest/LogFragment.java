package com.naver.httpclienttest;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.naver.httpclientlib.Request;
import com.naver.httpclientlib.Response;
import com.naver.httpclientsdk.R;

public class LogFragment extends Fragment {
    private Response<?> response;
    private String errorMessage;

    private EditText requestLog;
    private EditText responseLog;

    private boolean isAsync;

    LogFragment(Response<?> response, boolean isAsync) {
        this.response = response;
        this.isAsync = isAsync;
    }

    LogFragment(String errorMessage, boolean isAsync) {
        this.errorMessage = errorMessage;
        this.isAsync = isAsync;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_log, container, false);

        Button backBtn = view.findViewById(R.id.back_btn);
        backBtn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                replaceFragment(new MainFragment());
            }
        });

        requestLog = view.findViewById(R.id.request_log);
        loggingRequest();

        responseLog = view.findViewById(R.id.response_log);
        loggingResponse();

        return view;
    }

    private void loggingRequest() {
        requestLog.append("Call : " + (!isAsync ? "Synchronous Call\n" : "Asynchronous Call\n"));
        if(response != null) {
            Request request = response.request();
            requestLog.append("URL : " + request.getUrl() + "\n");
            requestLog.append("Method : " + request.getMethod() + "\n");
            requestLog.append("Headers : " + request.getHeaders() + "\n");
            if(request.getContentType() != null) {
                requestLog.append("Content-type : " + request.getContentType() + "\n");
            }
            requestLog.setSelection(0);
        }
    }

    private void loggingResponse() {
        if(response != null) {
            responseLog.append("code : " + response.code() + "\n");
            responseLog.append("date : " + response.header("Date") + "\n");
            responseLog.append("content-type : " + response.header("Content-Type") + "\n");
            if(response.body() != null) {
                responseLog.append("body : " + response.body().toString());
            }
            responseLog.setSelection(0);
        } else {
            responseLog.append("Failed to Response : " + errorMessage);
        }
    }

    private void replaceFragment(Fragment newFragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.fragment_container, newFragment);
        fragmentTransaction.commit();
    }
}
