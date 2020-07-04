package com.calc.firebaseloginpractice.ui.rooms;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.calc.firebaseloginpractice.R;
import com.calc.firebaseloginpractice.models.roomsModel;
import com.calc.firebaseloginpractice.models.userModel;
import com.calc.firebaseloginpractice.ui.register.registerFragment;
import com.calc.firebaseloginpractice.utils.constants;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


public class newRoomFragment extends Fragment {
    private View mainView;
    private EditText titleField;
    private CheckBox privateBox;
    private Toolbar toolbar;
    private CircleImageView roomImage;
    private Uri image;
    private boolean isPrivate = false;



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
    }
    private void initViews()
    {
        titleField=mainView.findViewById(R.id.newRoom_filed);
        TextView addRoom=mainView.findViewById(R.id.addNewRoom_btn);
        privateBox = mainView.findViewById(R.id.private_box);
        roomImage=mainView.findViewById(R.id.pick_room_image);
        toolbar=mainView.findViewById(R.id.newRoom_toolbar);

        roomImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                CropImage.activity().start(requireContext(), newRoomFragment.this);
            }
        });


        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_white_ios_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
            }
        });

        addRoom.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                String title = titleField.getText().toString();

                if (title.isEmpty())
                {
                    constants.showToast(requireContext(), "invalid data");
                    return;
                }

                if (privateBox.isChecked())
                {
                    isPrivate = true;
                }

                constants.showProgress();
                getUserData(title);
            }
        });
    }

    private void getUserData(final String title)
    {
        final String uId = constants.getUid(requireActivity());
        constants.getDatabaseReference().child("users").child(uId).addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                userModel userModel = dataSnapshot.getValue(userModel.class);

                if (userModel != null)
                {
                    if (image==null)
                    {
                        addRoom(uId, userModel.getName(), "", title);
                    }
                    else
                    {
                        uploadImage(uId, userModel.getName(), title);
                    }

                } else
                {
                    constants.dismissProgress();
                    constants.showToast(requireContext(), "null");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }

    private void uploadImage(final String uId, final String name, final String title)
    {

            // set file place into storage and file name
            final StorageReference userImageRef = constants.getStorageReference().child("rooms_images/"+image.getLastPathSegment());

            // put file into upload task
            UploadTask uploadTask = userImageRef.putFile(image);

            Task<Uri> task = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>()
            {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task)
                {
                    return userImageRef.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>()
            {
                @Override
                public void onComplete(@NonNull Task<Uri> task)
                {
                    if (task.isSuccessful())
                    {
                        Uri downloadUri = task.getResult();
                        String imageUrl = downloadUri.toString();

                        addRoom(uId, name, imageUrl, title);

                    }
                }
            });
        }




    private void addRoom(String uId, String name, String imageUrl, String title)
    {
        String key = constants.getDatabaseReference().child("Rooms").push().getKey();

        if (key != null)
        {
            roomsModel roomModel = new roomsModel(uId,name,imageUrl, key,title, isPrivate);

            constants.getDatabaseReference().child("Rooms").child(key).setValue(roomModel).addOnCompleteListener(new OnCompleteListener<Void>()
            {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {

                    constants.showToast(requireContext(), "created");
                    constants.dismissProgress();
                    requireActivity().onBackPressed();
                }
            });
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // to check the the CropImage operation
        if (requestCode== CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result= CropImage.getActivityResult(data);
            if (resultCode==RESULT_OK)
            {
                image= result.getUri();

                // picasso to get the image and show it in circleImageView
                Picasso
                        .get()
                        .load(image)
                        .into(roomImage);

            } else if (resultCode==CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
            {
                Exception error= result.getError();
                constants.showToast(requireContext(),error.getMessage());
            }
            else
            {
                roomImage.setVisibility(View.GONE);
            }
        }

    }

}
