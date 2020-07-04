package com.calc.firebaseloginpractice.ui.rooms.roomrRequests;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.calc.firebaseloginpractice.R;

class requestRoomFragment extends Fragment
{
    private View mainView;
    TextView accept;
    TextView reject;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView= inflater.inflate(R.layout.fragment_roomsrequests,null);
        return mainView;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initViews();
    }

    private void initViews()
    {
        accept=mainView.findViewById(R.id.Accept_Request);
        reject=mainView.findViewById(R.id.reject_request);
    }


}
