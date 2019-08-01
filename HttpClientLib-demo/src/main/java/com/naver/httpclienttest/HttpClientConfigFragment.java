package com.naver.httpclienttest;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.naver.httpclientsdk.R;

public class HttpClientConfigFragment extends Fragment {

    EditText baseUrlText;
    EditText callTimeoutText;
    EditText connectTimeoutText;
    EditText readTimeoutText;
    EditText writeTimeoutText;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_httpclient_config, container, false);

        baseUrlText = view.findViewById(R.id.base_url);
        callTimeoutText = view.findViewById(R.id.call_time);
        connectTimeoutText = view.findViewById(R.id.connect_time);
        readTimeoutText = view.findViewById(R.id.read_time);
        writeTimeoutText = view.findViewById(R.id.write_time);

        Button saveBtn = view.findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

                MainFragment mainFragment = new MainFragment();
                fragmentTransaction.replace(R.id.fragment_container, mainFragment);
                fragmentTransaction.commit();
            }
        });

        return view;
    }
}
