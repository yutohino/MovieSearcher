package com.example.moviesearcher.fragment;


import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.example.moviesearcher.R;
import com.example.moviesearcher.ResultSearchPagerAdapter;
import com.example.moviesearcher.application.GetResourceApplication;
import com.google.android.material.tabs.TabLayout;


public class ResultSearchFragment extends Fragment {

    public static final String KEY_KEYWORD = GetResourceApplication.context.getString(R.string.string_key_keyword);
    private static final String KEY_PLATFORM = GetResourceApplication.context.getString(R.string.string_key_platform);

    public static String searchWord;
    private static String keyWord;


    static ResultSearchFragment newInstance(String keyWord, int platform) {
        ResultSearchFragment fragment = new ResultSearchFragment();
        Bundle args = new Bundle();
        args.putString(KEY_KEYWORD, keyWord);
        args.putInt(KEY_PLATFORM, platform);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_result_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final EditText etKeyWord = view.findViewById(R.id.edTextSearchMovie);

        searchWord = getArguments().getString(KEY_KEYWORD);
        etKeyWord.setText(searchWord);

        // キーボードのエンターキーで検索を実行。（YouTube＆ニコニコ両方のボタンを用意できそうにないから。）
        etKeyWord.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent keyEvent) {
                if ((keyEvent.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    hideKeyboard();
                    keyWord = etKeyWord.getText().toString();
                    transitionFragment(keyWord, 0);
                    return true;
                }
                return false;
            }
        });

        ResultSearchPagerAdapter mResultSearchPagerAdapter = new ResultSearchPagerAdapter(getChildFragmentManager());
        ViewPager viewPager = view.findViewById(R.id.pagerResultSearchMovie);
        viewPager.setOffscreenPageLimit(2);
        viewPager.setAdapter(mResultSearchPagerAdapter);
        int platform = getArguments().getInt(KEY_PLATFORM, 1);
        viewPager.setCurrentItem(platform);

        TabLayout tabLayout = view.findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void transitionFragment(String keyWord, int platform) {
        if (!keyWord.isEmpty()) {
            FragmentTransaction fTransaction = getFragmentManager().beginTransaction();
            fTransaction.addToBackStack(null);
            fTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
            fTransaction.replace(R.id.container, ResultSearchFragment.newInstance(keyWord, platform));
            fTransaction.commit();
        } else {
            Toast.makeText(getContext(), "なんか入れろ", Toast.LENGTH_SHORT).show();
        }
    }

    private void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager manager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
