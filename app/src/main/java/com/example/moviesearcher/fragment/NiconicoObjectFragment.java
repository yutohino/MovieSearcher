package com.example.moviesearcher.fragment;


import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moviesearcher.NiconicoAPI;
import com.example.moviesearcher.NiconicoRecyclerViewAdapter;
import com.example.moviesearcher.R;
import com.example.moviesearcher.models.NiconicoDataModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;


public class NiconicoObjectFragment extends Fragment {

    private RecyclerView recyclerView;
    private NiconicoRecyclerViewAdapter rvAdapter;
    private ArrayList<NiconicoDataModel> niconicoListData;
    private LinearLayoutManager mLinearLayoutManager;
    private int positionIndex;
    private int positionOffset;
    private String keyWord;

    public static NiconicoObjectFragment newInstance() {
        NiconicoObjectFragment fragment = new NiconicoObjectFragment();
        Bundle args = new Bundle();
        args.putString(ResultSearchFragment.KEY_KEYWORD, ResultSearchFragment.searchWord);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_niconico_object, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.rvNiconico);
        keyWord = getArguments().getString(ResultSearchFragment.KEY_KEYWORD);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (niconicoListData == null) {
            new RequestNiconicoAPI().execute();
        } else {
            initList(niconicoListData);
        }

        if (positionIndex != 0 || positionOffset != 0) {
            mLinearLayoutManager.scrollToPositionWithOffset(positionIndex, positionOffset);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        keepScrollPosition();
    }

    private void keepScrollPosition() {
        positionIndex = mLinearLayoutManager.findFirstVisibleItemPosition();
        View startView = recyclerView.getChildAt(0);
        positionOffset = (startView == null) ? 0 : (startView.getTop() - recyclerView.getPaddingTop());
    }

    private void initList(ArrayList<NiconicoDataModel> listData) {
        recyclerView.setHasFixedSize(true);
        rvAdapter = new NiconicoRecyclerViewAdapter(getContext(), listData);
        recyclerView.setAdapter(rvAdapter);
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLinearLayoutManager);
        rvAdapter.notifyDataSetChanged();
    }

    private class RequestNiconicoAPI extends AsyncTask<Void, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            NiconicoAPI api = new NiconicoAPI();
            String urlStr = api.BASE_URL + keyWord + api.TARGETS + api.FIELDS + api.SORT + api.CONTEXT + api.OFFSET + api.LIMIT;
            try {
                URL url = new URL(urlStr);
                HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                InputStream stream = connection.getInputStream();
                String result = is2String(stream);
                connection.disconnect();
                stream.close();
                return result;

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        private String is2String(InputStream stream) throws IOException {
            StringBuilder sb = new StringBuilder();
            BufferedReader br = new BufferedReader(new InputStreamReader(stream));
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
            return sb.toString();
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result != null) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    niconicoListData = parseNiconicoListFromResponse(jsonObject);
                    initList(niconicoListData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        private ArrayList<NiconicoDataModel> parseNiconicoListFromResponse(JSONObject jsonObject) {
            ArrayList<NiconicoDataModel> mList = new ArrayList<>();
            if (jsonObject.has("data")) {
                try {
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject json = jsonArray.getJSONObject(i);
                        String title = json.getString("title");
                        String description = json.getString("description");
                        String update = json.getString("startTime");
                        String thumbnails = json.getString("thumbnailUrl");
                        String videoId = json.getString("contentId");

                        // 「T」以降はいらないので「****-**-**」までを使う。
                        StringBuilder sbUpdate = new StringBuilder(update.substring(0, 10));
                        sbUpdate.replace(4, 5, "年").replace(7, 8, "月").append("日");

                        NiconicoDataModel niconico = new NiconicoDataModel();
                        niconico.setTitle(title);
                        niconico.setDesc(description);
                        niconico.setUpDate(sbUpdate.toString());
                        niconico.setThumb(thumbnails);
                        niconico.setVideoId(videoId);

                        mList.add(niconico);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return mList;
        }
    }
}
