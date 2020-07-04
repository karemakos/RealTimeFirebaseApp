package com.calc.firebaseloginpractice.ui.profile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.calc.firebaseloginpractice.R;
import com.calc.firebaseloginpractice.models.userModel;
import com.calc.firebaseloginpractice.ui.welcome.welcomeFragment;
import com.calc.firebaseloginpractice.utils.constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class profileFragment extends Fragment {
    private View mainView;
    private TextView nameFiled;
    private TextView emailFiled;
    private TextView mobileFiled;
    private TextView addressFiled;
    private ImageView userImage;
    private Button sendMessageBtn;
    private Toolbar toolbar;
    private ImageView exit;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_profile, null);
        return mainView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initViews();


    }



    private void initViews() {
        nameFiled = mainView.findViewById(R.id.profileName);
        emailFiled = mainView.findViewById(R.id.profileEmail);
        mobileFiled = mainView.findViewById(R.id.profilePhone);
        addressFiled = mainView.findViewById(R.id.profileAddress);
        userImage = mainView.findViewById(R.id.profile_userImage);
        exit=mainView.findViewById(R.id.exitImageView);



        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                    AlertDialog.Builder builder= new AlertDialog.Builder(requireActivity());
                    builder.setMessage("Are you sure you want to Exit?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {

                                    constants.getAuth().signOut();
                                    constants.saveUid(requireActivity(),"empty");
                                    constants.replaceFragment(profileFragment.this,new welcomeFragment(),false);
                                }
                            }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {
                            dialog.cancel();
                        }
                    });
                    AlertDialog alertDialog=builder.create();
                    alertDialog.show();


                }

        });


        // To get my data from firebase like my  profile photo and my name
        getData();
    }

    private void getData()
    {
        constants.showProgress();
        // First i get my id and put it in String
        String uId= constants.getUid(requireActivity());
        constants.getDatabaseReference().child("users").child(uId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                // as we send the data by user model we will get it by user model as well, that's why we do empty constructor
                userModel model= dataSnapshot.getValue(userModel.class);


                if (model!=null)
                {

                    String name=model.getName();
                    String    email=model.getEmail();
                    String    mobile=model.getMobile();
                    String   address=model.getAddress();
                    String    image=model.getImageUri();

                    nameFiled.setText(name);
                    emailFiled.setText(email);
                    mobileFiled.setText(mobile);
                    addressFiled.setText(address);


                    Picasso
                            .get()
                            .load(image)
                            .into(userImage);

                    constants.dismissProgress();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}