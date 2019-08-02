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

public class HttpClientConfigFragment extends Fragment {

    EditText baseUrlText;
    EditText callTimeoutText;
    EditText connectTimeoutText;
    EditText readTimeoutText;
    EditText writeTimeoutText;
    SharedPreferences sharedPreferences;

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
                "baseUrl",
                getContext().getResources().getString(R.string.default_URL)));

        callTimeoutText = view.findViewById(R.id.call_time);
        callTimeoutText.setText(sharedPreferences.getString(
                "callTimeout",
                String.valueOf(10000)
        ));
        connectTimeoutText = view.findViewById(R.id.connect_time);
        connectTimeoutText.setText(sharedPreferences.getString(
                "connectTimeout",
                String.valueOf(10000)
                ));
        readTimeoutText = view.findViewById(R.id.read_time);
        readTimeoutText.setText(sharedPreferences.getString(
                "readTimeout",
                String.valueOf(10000)
        ));
        writeTimeoutText = view.findViewById(R.id.write_time);
        writeTimeoutText.setText(sharedPreferences.getString(
                "writeTimeout",
                String.valueOf(10000)
        ));

        Button saveBtn = view.findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = sharedPreferences.edit();

                CharSequence baseUrlSeq = baseUrlText.getText();
                if(baseUrlSeq != null) {
                    editor.putString("baseUrl", baseUrlSeq.toString());
                }

                CharSequence callSeq = callTimeoutText.getText();
                if(callSeq.length() > 0) {
                    editor.putInt("callTimeout", Integer.valueOf(callSeq.toString()));
                }

                CharSequence connectSeq = connectTimeoutText.getText();
                if(connectSeq.length() > 0) {
                    editor.putInt("connectTimeout", Integer.valueOf(connectSeq.toString()));                }

                CharSequence readSeq = readTimeoutText.getText();
                if(readSeq.length() > 0) {
                    editor.putInt("readTimeout", Integer.valueOf(readSeq.toString()));
                }

                CharSequence writeSeq = writeTimeoutText.getText();
                if(writeSeq.length() > 0) {
                    editor.putInt("writeTimeout", Integer.valueOf(writeSeq.toString()));
                }

                editor.apply();

                replaceFragment(new MainFragment());
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
