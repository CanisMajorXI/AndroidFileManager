package com.zqw.fileoperation.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zqw.fileoperation.MainActivity;
import com.zqw.fileoperation.R;

/**
 * Created by 51376 on 2018/3/20.
 */

public class BottomPopupMenuFragment extends Fragment {
    MainActivity mainActivity = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_popup_menu, container, false);
        mainActivity = (MainActivity) getActivity();
        return view;
    }

    @Override
    public void onResume() {
        mainActivity.currentFragment = this;
        super.onResume();
    }
}
