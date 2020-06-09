package com.calc.firebaseloginpractice.ui.register;

import android.content.Intent;
import android.net.Uri;
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
import com.calc.firebaseloginpractice.models.userModel;
import com.calc.firebaseloginpractice.ui.login.loginFragment;
import com.calc.firebaseloginpractice.utils.constants;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class registerFragment extends Fragment
{
    private View mainView;
    private EditText nameFiled;
    private EditText emailFiled;
    private EditText passwordFiled;
    private EditText passwordConfirmationFiled;
    private EditText mobileFiled;
    private EditText addressFiled;
    private CircleImageView circleImageView;
    private Uri userImage;
    private Button registerBtn;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView=inflater.inflate(R.layout.register_fragment,null);
        return mainView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        constants.initProgress(requireContext(),"Please Wait...");
    }

    private void initView()
    {
        nameFiled=mainView.findViewById(R.id.register_name_filed);
        emailFiled=mainView.findViewById(R.id.register_email_filed);
        passwordFiled=mainView.findViewById(R.id.register_password_filed);
        passwordConfirmationFiled=mainView.findViewById(R.id.register_password_confirmation_filed);
        mobileFiled=mainView.findViewById(R.id.register_mobile_filed);
        addressFiled=mainView.findViewById(R.id.register_address_filed);
        registerBtn=mainView.findViewById(R.id.register_register_btn);
        circleImageView=mainView.findViewById(R.id.pick_user_image);

        circleImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                CropImage.activity().start(requireContext(),registerFragment.this);
            }
        });

        // Ways to of structure get the data into the cloud
        // 1- Get the data

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
              String name = nameFiled.getText().toString();
              String email= emailFiled.getText().toString();
              String password= passwordFiled.getText().toString();
              String passwordConfirmation = passwordConfirmationFiled.getText().toString();
              String mobile= mobileFiled.getText().toString();
              String address= addressFiled.getText().toString();

              if (name.isEmpty()||email.isEmpty()|| password.isEmpty()||mobile.isEmpty()||address.isEmpty())
              {
                  constants.showToast(requireContext(),"please fill your data first");
                  return;
              }
              if (!passwordConfirmation.equals(password))
              {
                  constants.showToast(getContext(),"Passwords not matching");
                  return;
              }
              if (userImage==null)
              {
                  constants.showToast(requireContext(),"Please select picture");
                  return;
              }

              constants.showProgress();
              registerFirebase(name,email,password,mobile,address);
            }
        });
    }

    private void registerFirebase(final String name, final String email, String password, final String mobile, final String address)
    {
        // this method 4 email & pass creation
        //2 - Authenticate ( Email - Password ) By (Auth)
           constants.getAuth().createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                @Override
                public void onComplete(@NonNull Task<AuthResult> task)
                {
                    constants.dismissProgress();
                    if (task.isSuccessful())
                    {
                        // to confirm the mail is true and send verification mail
                        // 3- Get user ID By Task
                        task.getResult().getUser().sendEmailVerification();
                        String uId= task.getResult().getUser().getUid();

                        // 4 - Upload Image into storage By (Storage Ref)
                        // 1- First Step
                        uploadImage(name,email,mobile,address,uId);

                      //  constants.showToast(getContext(),"Your registration is successfully done! ");
                        constants.replaceFragment(registerFragment.this,new loginFragment(),false);
                    } else
                    {
                        constants.showToast(getContext(),task.getException().getMessage());
                    }
                }
            });
    }


    private void uploadImage(final String name, final String email, final String mobile, final String address, final String uId)
    {
        // set file place into storage and file name, (users_images) is the name of the folder that we created in the storage Firebase
        // 2- Second Step By Uploading image Into Storage
        // we use this method together one the final storagerefrance user imageref =... to go inside the file and the other UploadTask to upload the file
        final StorageReference userImageRef = constants.getStorageReference().child("users_images/"+userImage.getLastPathSegment());
        // put file into upload task
        UploadTask uploadTask = userImageRef.putFile(userImage);


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
                    saveNewUser(name, email, mobile, address, uId, imageUrl);
                }
            }
        });

    }

    private void saveNewUser(String name, String email, String mobile, String address, String uId, String imageUrl)
    {
        userModel userModel= new userModel(name,email,mobile,address,imageUrl,uId);
        constants.getDatabaseReference().child("users").child(uId).setValue(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                constants.dismissProgress();

                if (task.isSuccessful())
                {
                    constants.showToast(requireContext(),"Your Register is successfully completed please verify your Mail");
                    constants.replaceFragment(registerFragment.this, new loginFragment(),false);
                }
            }
        });
    }

    // this method to bring anything from out or inside the app to the app
    // By this data will check the photo if its ok or not, so if it's ok, then will do something if it's not then will through Error
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // to check the the CropImage operation
        if (requestCode== CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result= CropImage.getActivityResult(data);
            if (resultCode==RESULT_OK)
            {
                userImage= result.getUri();

                // picasso to get the image and show it in circleImageView
                Picasso
                        .get()
                        .load(userImage)
                        .into(circleImageView);

            } else if (resultCode==CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
            {
                Exception error= result.getError();
                constants.showToast(requireContext(),error.getMessage());
            }
        }

    }

}
