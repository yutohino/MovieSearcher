package com.example.moviesearcher.fragment;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.moviesearcher.R;


public class FormSearchFragment extends Fragment {

    public static FormSearchFragment newInstance() {
        FormSearchFragment fragment = new FormSearchFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final EditText etKeyWord = view.findViewById(R.id.edTextSearchMovie);
        Button btnYoutube = view.findViewById(R.id.btnYoutube);
        Button btnNiconico = view.findViewById(R.id.btnNiconico);

        btnYoutube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transitionFragment(etKeyWord, 0);
            }
        });

        btnNiconico.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                transitionFragment(etKeyWord, 1);
            }
        });
    }

    private void transitionFragment(EditText etKeyWord, int platform) {
        final String keyWord = etKeyWord.getText().toString();
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
}
