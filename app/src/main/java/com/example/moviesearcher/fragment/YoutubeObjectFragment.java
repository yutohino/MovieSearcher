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

import com.example.moviesearcher.EndlessScrollListener;
import com.example.moviesearcher.R;
import com.example.moviesearcher.YoutubeAPI;
import com.example.moviesearcher.YoutubeRecyclerViewAdapter;
import com.example.moviesearcher.models.YoutubeDataModel;

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


public class YoutubeObjectFragment extends Fragment {

    private RecyclerView recyclerView;
    private YoutubeRecyclerViewAdapter rvAdapter;
    private ArrayList<YoutubeDataModel> youtubeListData;
    private LinearLayoutManager mLinearLayoutManager;
    private int positionIndex;
    private int positionOffset;
    private String keyWord;
    private String nextPageToken = "";
    private Boolean isInitialSearch = true;

    public static YoutubeObjectFragment newInstance() {
        YoutubeObjectFragment fragment = new YoutubeObjectFragment();
        Bundle args = new Bundle();
        args.putString(ResultSearchFragment.KEY_KEYWORD, ResultSearchFragment.searchWord);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_youtube_object, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        recyclerView = view.findViewById(R.id.rvYoutube);
        keyWord = getArguments().getString(ResultSearchFragment.KEY_KEYWORD);

    }

    @Override
    public void onResume() {
        super.onResume();
        if (youtubeListData == null) {
            new RequestYoutubeAPI().execute();
        } else {
            initList(youtubeListData);
        }

        if (positionIndex != 0 || positionOffset != 0) {
            mLinearLayoutManager.scrollToPositionWithOffset(positionIndex, positionOffset);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        isInitialSearch = true;
        keepScrollPosition();
    }

    private void keepScrollPosition() {
        positionIndex = mLinearLayoutManager.findFirstVisibleItemPosition();
        View startView = recyclerView.getChildAt(0);
        positionOffset = (startView == null) ? 0 : (startView.getTop() - recyclerView.getPaddingTop());
    }

    private void initList(ArrayList<YoutubeDataModel> listData) {
        if (isInitialSearch) {
            recyclerView.setHasFixedSize(true);
            rvAdapter = new YoutubeRecyclerViewAdapter(getContext(), listData);
            recyclerView.setAdapter(rvAdapter);
            mLinearLayoutManager = new LinearLayoutManager(getContext());
            recyclerView.setLayoutManager(mLinearLayoutManager);
            recyclerView.addOnScrollListener(new EndlessScrollListener((LinearLayoutManager)
                    recyclerView.getLayoutManager()) {
                @Override
                public void onLoadMore(int currentPage) {
                    new RequestYoutubeAPI().execute();
                }
            });
            rvAdapter.notifyDataSetChanged();
            isInitialSearch = false;
        } else {
            rvAdapter.notifyDataSetChanged();
        }
    }


    private class RequestYoutubeAPI extends AsyncTask<Void, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... voids) {
            YoutubeAPI api = new YoutubeAPI();
            String urlStr = api.BASE_URL + api.search + api.API_KEY + api.max + api.ord + api.query + keyWord + api.part + api.token + nextPageToken;
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
                    if (isInitialSearch) {
                        youtubeListData = parseYoutubeListFromResponse(jsonObject);
                    } else {
                        youtubeListData.addAll(parseYoutubeListFromResponse(jsonObject));
                    }
                    initList(youtubeListData);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        private ArrayList<YoutubeDataModel> parseYoutubeListFromResponse(JSONObject jsonObject) {
            ArrayList<YoutubeDataModel> mList = new ArrayList<>();
            if (jsonObject.has("items")) {
                try {
                    JSONArray jsonArray = jsonObject.getJSONArray("items");
                    nextPageToken = jsonObject.getString("nextPageToken");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject json = jsonArray.getJSONObject(i);
                        if (json.has("id")) {
                            JSONObject jsonId = json.getJSONObject("id");
                            if (jsonId.has("kind")) {
                                if (jsonId.getString("kind").equals("youtube#video")) {
                                    JSONObject jsonSnippet = json.getJSONObject("snippet");
                                    String title = jsonSnippet.getString("title");
                                    String description = jsonSnippet.getString("description");
                                    String publishedAt = jsonSnippet.getString("publishedAt");
                                    String thumbnails = jsonSnippet.getJSONObject("thumbnails").getJSONObject("medium").getString("url");
                                    String videoId = jsonId.getString("videoId");

                                    // 「T」以降はいらないので「****-**-**」までを使う。
                                    StringBuilder sbPublishdAt = new StringBuilder(publishedAt.substring(0, 10));
                                    sbPublishdAt.replace(4, 5, "年").replace(7, 8, "月").append("日");

                                    YoutubeDataModel youtube = new YoutubeDataModel();
                                    youtube.setTitle(title);
                                    youtube.setDesc(description);
                                    youtube.setUpDate(sbPublishdAt.toString());
                                    youtube.setThumb(thumbnails);
                                    youtube.setVideoId(videoId);

                                    mList.add(youtube);
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return mList;
        }
    }
}
