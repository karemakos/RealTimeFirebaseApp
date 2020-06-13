package com.calc.firebaseloginpractice.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.calc.firebaseloginpractice.R;
import com.calc.firebaseloginpractice.models.postModel;
import com.calc.firebaseloginpractice.models.userModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public  class constants
{
    //we create this empty cuz we will full it once when we press on any user we wanna chat wz
    public static userModel myChats;


    // this is to use it for the comments
    public static postModel postModel;


    private static ProgressDialog progressDialog;

    // for logging by firebase
    private static FirebaseAuth auth;

    // to initialise firebase database
    private static FirebaseDatabase firebaseDatabase;

    // to initialise firebase storage
    private static FirebaseStorage firebaseStorage;

    // to  move the database
    private static DatabaseReference databaseReference;

    // to  move the storage
    private static StorageReference storageReference;

    // for the save the data in the device itself
    private static SharedPreferences sharedPreferences;
    private static SharedPreferences.Editor editor;


    public static void replaceFragment(Fragment from, Fragment to, boolean save)
    {

        if (save){


        from
                .requireActivity()
                .getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container,to)
                .addToBackStack(null)
                .commit();

    } else
        {
            from
                    // to clear the backstack
                    .requireActivity()
                    .getSupportFragmentManager()
                    .popBackStack();

            from
                    .requireActivity()
                    .getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container,to)
                    .disallowAddToBackStack()
                    .commit();
        }
    }

    public static void showToast(Context context,String body)
    {
        Toast.makeText(context, body, Toast.LENGTH_SHORT).show();
    }


    public static void initProgress(Context context,String body)
    {
        progressDialog= new ProgressDialog(context);
        progressDialog.setMessage(body);
        progressDialog.setCancelable(false);
    }
    public static void showProgress()
    {
        progressDialog.show();
    }
    public static void dismissProgress()
    {
        progressDialog.dismiss();
    }


    public static void initFirebase()
    {
        auth=FirebaseAuth.getInstance();

        firebaseDatabase= FirebaseDatabase.getInstance();

        // this method to make it move inside the firebase and if we dont to specifiy something inside like notes or something
        // we will leave it like that, but if we want to make it move only inside 4example notes then we have to use this method
        //        databaseReference=firebaseDatabase.getReference().chaild("the name of the notes");
        databaseReference=firebaseDatabase.getReference();


        firebaseStorage= FirebaseStorage.getInstance();
        storageReference= firebaseStorage.getReference();
    }
    public static FirebaseAuth getAuth()
    {
        return auth;
    }
    public static DatabaseReference getDatabaseReference()
    {
        return databaseReference;
    }
    public static StorageReference getStorageReference()
    {
        return storageReference;
    }



    public static void initPref(FragmentActivity activity)
    {
        sharedPreferences= activity.getSharedPreferences("SOCIAL",Context.MODE_PRIVATE);
    }
    public static void saveUid(FragmentActivity activity,String id)
    {
        sharedPreferences= activity.getSharedPreferences("SOCIAL",Context.MODE_PRIVATE);
        editor=sharedPreferences.edit();
        editor.putString("UID",id);
        editor.apply();
    }
    public static String getUid(FragmentActivity activity)
    {
        sharedPreferences=activity.getSharedPreferences("SOCIAL",0);
        return sharedPreferences.getString("UID","empty");
    }


    // to set the time
    public static long getTime()
    {
        return System.currentTimeMillis();
    }
}
