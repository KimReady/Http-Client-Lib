package com.naver.httpclienttest;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.naver.httpclientlib.CallBack;
import com.naver.httpclientlib.CallTask;
import com.naver.httpclientlib.HttpClient;
import com.naver.httpclientlib.Response;
import com.naver.httpclientsdk.R;
import com.naver.httpclienttest.data.Post;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

class MainFragment extends Fragment {
    private static final String LOG_TAG = MainFragment.class.getSimpleName();

    private LinearLayout pathParamLayout;
    private LinearLayout queryLayout;
    private LinearLayout requestBodyLayout;

    private Spinner httpMethodSpinner;
    private EditText headerNameText1;
    private EditText headerNameText2;
    private EditText headerValueText1;
    private EditText headerValueText2;
    private EditText pathparamText;
    private EditText queryNameText1;
    private EditText queryNameText2;
    private EditText queryValueText1;
    private EditText queryValueText2;
    private EditText userIdText;
    private EditText idText;
    private EditText titleText;
    private EditText bodyText;
    private EditText dynamicUrlText;
    private RadioGroup syncAsyncGroup;
    private RadioButton syncBtn;
    private RadioButton asyncBtn;
    private Button dynamicCallBtn;

    private HttpService httpService;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = getContext().getSharedPreferences(
                getContext().getResources().getString(R.string.config_file),
                Context.MODE_PRIVATE);

        String baseUrl = sharedPreferences.getString(
                getContext().getResources().getString(R.string.base_url),
                getContext().getResources().getString(R.string.default_URL));
        long callTimeout = sharedPreferences.getLong(
                getContext().getResources().getString(R.string.call_timeout),
                DefaultTimeout.callTimeout);
        long connectTimeout = sharedPreferences.getLong(
                getContext().getResources().getString(R.string.connect_timeout),
                DefaultTimeout.connectTimeout);
        long readTimeout = sharedPreferences.getLong(
                getContext().getResources().getString(R.string.read_timeout),
                DefaultTimeout.readTimeout);
        long writeTimeout = sharedPreferences.getLong(
                getContext().getResources().getString(R.string.write_timeout),
                DefaultTimeout.writeTimeout);

        HttpClient httpClient = new HttpClient.Builder()
                .baseUrl(baseUrl)
                .callTimeout(callTimeout, TimeUnit.MILLISECONDS)
                .connectTimeout(connectTimeout, TimeUnit.MILLISECONDS)
                .readTimeout(readTimeout, TimeUnit.MILLISECONDS)
                .writeTimeout(writeTimeout, TimeUnit.MILLISECONDS)
                .build();

        httpService = httpClient.create(HttpService.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        pathParamLayout = view.findViewById(R.id.path_param_layout);
        queryLayout = view.findViewById(R.id.query_layout);
        requestBodyLayout = view.findViewById(R.id.request_body_layout);

        Button baseUrlBtn = view.findViewById(R.id.base_url_btn);
        baseUrlBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                replaceFragment(new HttpClientConfigFragment());
            }
        });

        headerNameText1 = view.findViewById(R.id.header_name1);
        headerValueText1 = view.findViewById(R.id.header_value1);
        headerNameText2 = view.findViewById(R.id.header_name2);
        headerValueText2 = view.findViewById(R.id.header_value2);

        httpMethodSpinner = view.findViewById(R.id.http_method_spinner);
        httpMethodSpinner.setAdapter(makeAdapter(R.array.http_method_list));
        httpMethodSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                HttpMethod method = HttpMethod.valueOf(httpMethodSpinner.getItemAtPosition(position).toString());
                switch (method) {
                    case GET:
                    case HEAD:
                        pathParamLayout.setVisibility(View.VISIBLE);
                        queryLayout.setVisibility(View.VISIBLE);
                        requestBodyLayout.setVisibility(View.GONE);
                        break;
                    case POST:
                        pathParamLayout.setVisibility(View.GONE);
                        queryLayout.setVisibility(View.GONE);
                        requestBodyLayout.setVisibility(View.VISIBLE);
                        break;
                    case PUT:
                        pathParamLayout.setVisibility(View.VISIBLE);
                        queryLayout.setVisibility(View.GONE);
                        requestBodyLayout.setVisibility(View.VISIBLE);
                        break;
                    case DELETE:
                        pathParamLayout.setVisibility(View.VISIBLE);
                        queryLayout.setVisibility(View.GONE);
                        requestBodyLayout.setVisibility(View.GONE);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Log.e(LOG_TAG, "Nothing Selected on Method Spinner.");
            }
        });

        pathparamText = view.findViewById(R.id.path_param_text);

        queryNameText1 = view.findViewById(R.id.query_name1);
        queryValueText1 = view.findViewById(R.id.query_value1);

        queryNameText2 = view.findViewById(R.id.query_name2);
        queryValueText2 = view.findViewById(R.id.query_value2);

        userIdText = view.findViewById(R.id.user_id_text);
        idText = view.findViewById(R.id.id_text);
        titleText = view.findViewById(R.id.title_text);
        bodyText = view.findViewById(R.id.body_text);

        dynamicUrlText = view.findViewById(R.id.dynamic_url);

        syncAsyncGroup = view.findViewById(R.id.sync_async_group);
        syncBtn = view.findViewById(R.id.sync_btn);
        asyncBtn = view.findViewById(R.id.async_btn);
        syncBtn.toggle();

        Button callBtn = view.findViewById(R.id.call_btn);
        callBtn.setOnClickListener(callBtnListener);
        dynamicCallBtn = view.findViewById(R.id.dynamic_call_btn);
        dynamicCallBtn.setOnClickListener(callBtnListener);

        return view;
    }

    /**
     * Spinner Adapter
     */
    private ArrayAdapter<CharSequence> makeAdapter(int items) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(),
                items,
                android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        return adapter;
    }

    /**
     * ClickListener for Call/DynamicCall Button
     */
    private final Button.OnClickListener callBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            boolean isDynamicUrl = (v.getId() == dynamicCallBtn.getId());
            String dynamicUrl = dynamicUrlText.getText().toString();
            String pathParam;

            Map<String, String> headerMap = new HashMap<>();
            String headerName1 = headerNameText1.getText().toString();
            String headerValue1 = headerValueText1.getText().toString();
            String headerName2 = headerNameText2.getText().toString();
            String headerValue2 = headerValueText2.getText().toString();

            if (!headerName1.isEmpty() && !headerValue1.isEmpty()) {
                headerMap.put(headerName1, headerValue1);
            }
            if (!headerName2.isEmpty() && !headerValue2.isEmpty()) {
                headerMap.put(headerName2, headerValue2);
            }

            HttpMethod method = HttpMethod.valueOf(String.valueOf(httpMethodSpinner.getSelectedItem()));
            try {
                switch (method) {
                    case GET:
                        pathParam = pathparamText.getText().toString();
                        if (!isDynamicUrl && !pathParam.isEmpty()) {
                            executeCallTask(httpService.getPostsByPathParam(headerMap, Integer.parseInt(pathParam)));
                        } else {
                            Map<String, String> queryMap = new HashMap<>();
                            String queryName1 = queryNameText1.getText().toString();
                            String queryValue1 = queryValueText1.getText().toString();
                            String queryName2 = queryNameText2.getText().toString();
                            String queryValue2 = queryValueText2.getText().toString();

                            if (!queryName1.isEmpty() && !queryValue1.isEmpty()) {
                                queryMap.put(queryName1, queryValue1);
                            }
                            if (!queryName2.isEmpty() && !queryValue2.isEmpty()) {
                                queryMap.put(queryName2, queryValue2);
                            }

                            if (isDynamicUrl) {
                                executeCallTask(httpService.getPostsByDynamicURLWithQuery(headerMap, dynamicUrl, queryMap));
                            } else {
                                executeCallTask(httpService.getPostsByQuery(headerMap, queryMap));
                            }
                        }
                        break;
                    case POST:
                        if (isDynamicUrl) {
                            executeCallTask(httpService.postPostsByDynamicURL(headerMap, dynamicUrl, parseRequestBody()));
                        } else {
                            executeCallTask(httpService.postPosts(headerMap, parseRequestBody()));
                        }
                        break;
                    case PUT:
                        pathParam = pathparamText.getText().toString();
                        if (!isDynamicUrl && !pathParam.isEmpty()) {
                            executeCallTask(httpService.putPostsById(headerMap, Integer.parseInt(pathParam), parseRequestBody()));
                        } else if (isDynamicUrl) {
                            executeCallTask(httpService.putPostsByDynamicURL(headerMap, dynamicUrl, parseRequestBody()));
                        } else {
                            replaceFragment(new LogFragment("You should use PathParam with PUT Method.", false));
                        }
                        break;
                    case DELETE:
                        pathParam = pathparamText.getText().toString();
                        if (!isDynamicUrl && !pathParam.isEmpty()) {
                            executeCallTask(httpService.deletePostById(headerMap, Integer.parseInt(pathParam)));
                        } else if (isDynamicUrl) {
                            executeCallTask(httpService.deletePostByDynamicURL(headerMap, dynamicUrl));
                        } else {
                            replaceFragment(new LogFragment("You should use PathParam with DELETE Method.", false));
                        }
                        break;
                    case HEAD:
                        if (isDynamicUrl) {
                            executeCallTask(httpService.getPostsForHeadMethodByDynamicURL(headerMap, dynamicUrl));
                        } else {
                            executeCallTask(httpService.getPostsForHeadMethod(headerMap));
                        }
                        break;
                }
            } catch(Exception e) { // defensive code for unchecked Exception in SDK.
                replaceFragment(new LogFragment(e.getMessage(), false));
            }
        }

        /**
         * execute callTask synchronously or asynchronously.
         * and then, start LogFragment with Response received for result
         */
        private <T> void executeCallTask(final CallTask<T> callTask) {
            if (syncAsyncGroup.getCheckedRadioButtonId() == syncBtn.getId()) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Response<T> response = callTask.execute();

                            replaceFragment(new LogFragment(response, false));
                        } catch (IOException e) {
                            replaceFragment(new LogFragment(e.getMessage(), false));
                        }
                    }
                }).start();
            } else if (syncAsyncGroup.getCheckedRadioButtonId() == asyncBtn.getId()) {
                callTask.enqueue(new CallBack() {
                    @Override
                    public void onResponse(Response response) throws IOException {
                        LogFragment logFragment = response.isSuccessful() ?
                                new LogFragment(response, true)
                                : new LogFragment("Response Fail", true);
                        replaceFragment(logFragment);
                    }

                    @Override
                    public void onFailure(IOException e) {
                        replaceFragment(new LogFragment(e.getMessage(), true));
                    }
                });
            }
        }

        private Post parseRequestBody() {
            CharSequence userIdSeq = userIdText.getText();
            int userId = userIdSeq.length() > 0 ? Integer.parseInt(userIdSeq.toString()) : 0;
            CharSequence idSeq = idText.getText();
            int id = idSeq.length() > 0 ? Integer.parseInt(idSeq.toString()) : 0;
            String title = titleText.getText().toString();
            String body = bodyText.getText().toString();

            return new Post(userId, id, title, body);
        }
    };

    private void replaceFragment(Fragment newFragment) {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        fragmentTransaction.replace(R.id.fragment_container, newFragment);
        fragmentTransaction.commit();
    }
}
