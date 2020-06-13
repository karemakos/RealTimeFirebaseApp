package com.calc.firebaseloginpractice.ui.rooms;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.calc.firebaseloginpractice.R;
import com.calc.firebaseloginpractice.models.roomsModel;
import com.calc.firebaseloginpractice.models.userModel;
import com.calc.firebaseloginpractice.utils.constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;


public class newRoomFragment extends Fragment {
    private View mainView;
    private EditText roomName;
    private Button roomBtn;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_new_room, null);
        return mainView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        constants.initProgress(requireContext(), "Please Wait...");
        initViews();
        // to get my data from firebase like my  profile photo and my name
    }

    private void initViews()
    {
        roomName=mainView.findViewById(R.id.newRoom_filed);
        roomBtn=mainView.findViewById(R.id.addNewRoom_btn);

        roomBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                String name=roomName.getText().toString();

                if (name.isEmpty())
                {
                    constants.showToast(getContext(),"please write A room name");
                    return;
                }
                constants.showProgress();
                    saveNewRoom(name);


            }
        });


    }

    private void saveNewRoom(final String name)
    {


        constants.getDatabaseReference().child("users").child(constants.getUid(requireActivity())).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                final String roomId= constants.getDatabaseReference().child("Rooms").push().getKey();

                userModel userModel= dataSnapshot.getValue(userModel.class);
                roomsModel model= new roomsModel(name,userModel.getName(),roomId);


                if (roomId!=null)
                {
                    constants.getDatabaseReference().child("Rooms").child(roomId).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task)
                        {
                            constants.dismissProgress();
                            requireActivity().onBackPressed();

                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }
}
