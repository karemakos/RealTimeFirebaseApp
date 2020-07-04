package com.calc.firebaseloginpractice.ui.timeline.posts;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.calc.firebaseloginpractice.R;
import com.calc.firebaseloginpractice.models.postModel;
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

import java.lang.reflect.Type;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class newPostFragment extends Fragment
{
    private View mainView;

    private CircleImageView post_user_image;
    private ImageView post_image;
    private TextView postUserName;
    private TextView postPickImage;
    private TextView postDeleteImage;
    private EditText postText;
    private Button postBtn;
    private String name;
    private String image;
    private String uId;

    private Uri selectedPostImage;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView= inflater.inflate(R.layout.fragment_new_post,null);
        return mainView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        constants.initProgress(requireContext(),"Please Wait...");
        initVies();
        // to get my data from firebase like my  profile photo and my name
        getData();
    }



    // To get my data from firebase like my  profile photo and my name
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
                    name=model.getName();
                    image=model.getImageUri();

                    postUserName.setText(name);

                    Picasso
                            .get()
                            .load(image)
                            .into(post_user_image);

                    constants.dismissProgress();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void initVies()
    {
        post_user_image=mainView.findViewById(R.id.new_post_user_image);
        post_image=mainView.findViewById(R.id.new_post_image);
        postUserName=mainView.findViewById(R.id.new_post_user_name);
        postPickImage=mainView.findViewById(R.id.new_post_pick_image);
        postDeleteImage=mainView.findViewById(R.id.new_post_delete_image);
        postText=mainView.findViewById(R.id.new_post_text);
        postBtn=mainView.findViewById(R.id.new_post_btn);

        postPickImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                CropImage.activity().start(requireContext(), newPostFragment.this);
            }
        });

        postDeleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                post_image.setVisibility(View.GONE);
                postDeleteImage.setVisibility(View.GONE);
                selectedPostImage=null;
            }
        });
        // to take our post to send it to the database
        postBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                long time= constants.getTime();
                String text= postText.getText().toString();

                if (text.isEmpty())
                {
                    constants.showToast(requireContext(),"Please type a post!");
                    return;
                }
                constants.showProgress();
                if (selectedPostImage==null)
                {
                    savePost(name,image,time,text,"",0);
                } else
                {
                    uploadImage(name,image,time,text,selectedPostImage,1);
                }
            }
        });

    }
    // first we uploadImage and then we save this post into savePost
    private void uploadImage(final String name, final String image, final long time, final String text, Uri selectedPostImage, final int type)
    {


        // set file place into storage and file name, (users_images) is the name of the folder that we created in the storage Firebase
        // 2- Second Step By Uploading image Into Storage
        // we use this method together one the final storagerefrance user imageref =... to go inside the file and the other UploadTask to upload the file
        final StorageReference userImageRef = constants.getStorageReference().child("posts_images/"+selectedPostImage.getLastPathSegment());
        // put file into upload task
        UploadTask uploadTask = userImageRef.putFile(selectedPostImage);


        // 5- Download  image URL  By Storage REF inorder to LINK to the user who has upload it
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

                    // this data that i want to send it Under the UID (User ID)
                    savePost(name, image, time,  text, imageUrl, type);
                }
            }
        });

    }

    private void savePost(String name, String image, long time, String text, String imageUrl , int type)
    {
        // i have already created postId in the postModel in order to make id for everyId to be easy to get it whenever i want
        String postId= constants.getDatabaseReference().child("Posts").push().getKey();
        //>>>>>>


        postModel model= new postModel(image,name,time,text,imageUrl,type,postId,constants.getUid(requireActivity()));


        if (postId!=null)
        {
            constants.getDatabaseReference().child("Posts").child(postId).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task)
                {
                    constants.dismissProgress();
                    requireActivity().onBackPressed();

                }
            });

        }


    }

    // to get the photo from the device to the app
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // to check the the CropImage operation
        if (requestCode== CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result= CropImage.getActivityResult(data);
            if (resultCode==RESULT_OK)
            {
                selectedPostImage= result.getUri();

                post_image.setVisibility(View.VISIBLE);
                // to show it once when we will add photo
                postDeleteImage.setVisibility(View.VISIBLE);
                // picasso to get the image and show it in circleImageView
                Picasso
                        .get()
                        .load(selectedPostImage)
                        .into(post_image);

            } else if (resultCode==CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
            {
                Exception error= result.getError();
                constants.showToast(requireContext(),error.getMessage());
            }
        }

    }

}
