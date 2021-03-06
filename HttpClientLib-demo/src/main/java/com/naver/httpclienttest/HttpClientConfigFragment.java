package com.naver.httpclienttest;

import android.content.Context;
import android.content.SharedPreferences;
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

import com.naver.httpclientsdk.R;

import static com.naver.httpclienttest.DefaultTimeout.*;

public class HttpClientConfigFragment extends Fragment {

    private EditText baseUrlText;
    private EditText callTimeoutText;
    private EditText connectTimeoutText;
    private EditText readTimeoutText;
    private EditText writeTimeoutText;
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getContext().getSharedPreferences(
                getContext().getResources().getString(R.string.config_file),
                Context.MODE_PRIVATE
        );
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_httpclient_config, container, false);

        baseUrlText = view.findViewById(R.id.base_url);
        baseUrlText.setText(sharedPreferences.getString(
                getContext().getResources().getString(R.string.base_url),
                getContext().getResources().getString(R.string.default_URL)));

        callTimeoutText = view.findViewById(R.id.call_time);
        callTimeoutText.setText(String.valueOf(
                sharedPreferences.getLong(
                        getContext().getResources().getString(R.string.call_timeout),
                        CALL_TIMEOUT)));

        connectTimeoutText = view.findViewById(R.id.connect_time);
        connectTimeoutText.setText(String.valueOf(
                sharedPreferences.getLong(
                        getContext().getResources().getString(R.string.connect_timeout),
                        CONNECT_TIMEOUT)));

        readTimeoutText = view.findViewById(R.id.read_time);
        readTimeoutText.setText(String.valueOf(
                sharedPreferences.getLong(
                        getContext().getResources().getString(R.string.read_timeout),
                        READ_TIMEOUT)));

        writeTimeoutText = view.findViewById(R.id.write_time);
        writeTimeoutText.setText(String.valueOf(
                sharedPreferences.getLong(
                        getContext().getResources().getString(R.string.write_timeout),
                        WRITE_TIMEOUT)));

        Button saveBtn = view.findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();

                CharSequence baseUrlSeq = baseUrlText.getText();
                if(baseUrlSeq != null) {
                    editor.putString(
                            getContext().getResources().getString(R.string.base_url),
                            baseUrlSeq.toString());
                }

                CharSequence callSeq = callTimeoutText.getText();
                if(callSeq.length() > 0) {
                    editor.putLong(
                            getContext().getResources().getString(R.string.call_timeout),
                            Long.valueOf(callSeq.toString()));
                }

                CharSequence connectSeq = connectTimeoutText.getText();
                if(connectSeq.length() > 0) {
                    editor.putLong(
                            getContext().getResources().getString(R.string.connect_timeout),
                            Long.valueOf(connectSeq.toString()));                }

                CharSequence readSeq = readTimeoutText.getText();
                if(readSeq.length() > 0) {
                    editor.putLong(
                            getContext().getResources().getString(R.string.read_timeout),
                            Long.valueOf(readSeq.toString()));
                }

                CharSequence writeSeq = writeTimeoutText.getText();
                if(writeSeq.length() > 0) {
                    editor.putLong(
                            getContext().getResources().getString(R.string.write_timeout),
                            Long.valueOf(writeSeq.toString()));
                }

                editor.apply();

                replaceFragment(new MainFragment());
            }
        });

        Button crashBtn = view.findViewById(R.id.crash_btn);
        crashBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                throw new AssertionError("Crash!");
            }
        });
        return view;
    }

    private void replaceFragment(Fragment newFragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.fragment_container, newFragment);
        fragmentTransaction.commit();
    }

}
