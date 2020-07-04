package com.calc.firebaseloginpractice.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.calc.firebaseloginpractice.R;
import com.calc.firebaseloginpractice.models.userModel;
import com.calc.firebaseloginpractice.ui.chats.chatsFragment;
import com.calc.firebaseloginpractice.ui.users.usersFragment;
import com.calc.firebaseloginpractice.utils.constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class fragmentUsersProfile extends Fragment
{
    private View mainView;
    private TextView nameFiled;
    private TextView addressFiled;
    private ImageView userImage;
    TextView sendMessage;
    private Toolbar toolbar;

    String uId;
    userModel userModels;

    // to get the users from timeline to show it from the profile here
    public fragmentUsersProfile(String getuId)
    {
        this.uId=getuId;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_usersprofile, null);
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        return mainView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initViews();
        getUsersData(uId);
    }

    private void getUsersData(String uId) {
        constants.getDatabaseReference().child("users").child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                 userModels= dataSnapshot.getValue(userModel.class);
                assert userModels != null;
                nameFiled.setText(userModels.getName());
                addressFiled.setText(userModels.getAddress());

                Picasso
                        .get()
                        .load(userModels.getImageUri())
                        .into(userImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void initViews()
    {
        nameFiled = mainView.findViewById(R.id.users_name);
        addressFiled = mainView.findViewById(R.id.users_address);
        userImage = mainView.findViewById(R.id.profile_usersImage);
        toolbar=mainView.findViewById(R.id.usersProfile_toolbar);
        sendMessage = mainView.findViewById(R.id.send_usersMessage);

        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                constants.replaceFragment(fragmentUsersProfile.this, new chatsFragment(),true);

                // this is to take the person info that we pressed on it to chat
                constants.myChats= userModels;
            }
        });

        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_white_ios_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
            }
        });

    }

}
