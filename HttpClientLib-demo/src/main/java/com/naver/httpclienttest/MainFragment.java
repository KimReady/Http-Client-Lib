package com.naver.httpclienttest;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.naver.httpclientsdk.R;

public class MainFragment extends Fragment {
    Spinner responseItemSpinner;
    Spinner httpMethodSpinner;
    Button baseUrlBtn;
    Button queryBtn;
    Button callBtn;
    Button dynamicCallBtn;
    RadioGroup syncAsyncGroup;
    RadioButton syncBtn;
    RadioButton asyncBtn;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        baseUrlBtn = view.findViewById(R.id.base_url_btn);
        baseUrlBtn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {
                replaceFragment(new HttpClientConfigFragment());
            }
        });

        responseItemSpinner = view.findViewById(R.id.response_item_spinner);
        responseItemSpinner.setAdapter(makeAdapter(R.array.response_item_list));

        queryBtn = view.findViewById(R.id.query_btn);
        queryBtn.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v) {

            }
        });

        httpMethodSpinner = view.findViewById(R.id.http_method_spinner);
        httpMethodSpinner.setAdapter(makeAdapter(R.array.http_method_list));

        syncAsyncGroup = view.findViewById(R.id.sync_async_group);
        syncBtn = view.findViewById(R.id.sync_btn);
        asyncBtn = view.findViewById(R.id.async_btn);
        syncBtn.toggle();

        callBtn = view.findViewById(R.id.call_btn);
        callBtn.setOnClickListener(callBtnListener);
        dynamicCallBtn = view.findViewById(R.id.dynamic_call_btn);
        dynamicCallBtn.setOnClickListener(callBtnListener);

        return view;
    }

    private ArrayAdapter<CharSequence> makeAdapter(int items) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(),
                items,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        return adapter;
    }

    private final Button.OnClickListener callBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(v.getId() == dynamicCallBtn.getId()) {

            }
            replaceFragment(new LogFragment());
        }
    };

    private void replaceFragment(Fragment newFragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.fragment_container, newFragment);
        fragmentTransaction.commit();
    }
}
