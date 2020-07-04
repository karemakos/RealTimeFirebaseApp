package com.calc.firebaseloginpractice.ui.welcome;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.calc.firebaseloginpractice.R;
import com.calc.firebaseloginpractice.ui.login.loginFragment;
import com.calc.firebaseloginpractice.ui.register.registerFragment;
import com.calc.firebaseloginpractice.utils.constants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;

public class welcomeFragment extends Fragment
{
    private View mainView;
    private TextView loginBtn;
    private TextView registerBtn;
    private TextView forget;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView=inflater.inflate(R.layout.welcome_fragment,null);
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        return mainView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        constants.initProgress(requireActivity(),"Sending email...");
        initViews();
    }

    private void initViews()
    {
        loginBtn=mainView.findViewById(R.id.welcome_login_btn);
        registerBtn=mainView.findViewById(R.id.welcome_register_btn);
        forget=mainView.findViewById(R.id.forgetEmail);

        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
              //  constants.replaceFragment(welcomeFragment.this, new passwordRecovery(),true);
                showRecoverPasswordDialog();
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                constants.replaceFragment(welcomeFragment.this, new loginFragment(),true);
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                constants.replaceFragment(welcomeFragment.this, new registerFragment(),true);
            }
        });

    }

    private void showRecoverPasswordDialog()
    {
        //AlertDialog
        AlertDialog.Builder builder=new AlertDialog.Builder(requireActivity());
        builder.setTitle("Recover Password");
        builder.setIcon(R.drawable.notification);
        builder.setCancelable(false);




        // setLayout Linear
        LinearLayout linearLayout= new LinearLayout(requireActivity());
        final EditText emailEt = new EditText(requireActivity());
        emailEt.setHint("Email");
        emailEt.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);

        emailEt.setMinEms(16);

        linearLayout.addView(emailEt);
        linearLayout.setPadding(10,10,10,10);
        linearLayout.isContextClickable();
        builder.setView(linearLayout);


        //Buttons
        builder.setPositiveButton("Recover", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                String email = emailEt.getText().toString().trim();
                if (email.isEmpty())
                {
                    constants.showToast(getContext(),"Please Enter your mail");
                    return;
                } else
                {
                    recoveryMail(email);
                }

            }
        });


        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();
            }
        });
        builder.create().show();

    }

    private void recoveryMail(String email)
    {
        constants.showProgress();
        constants.getAuth().sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task)
            {
                constants.dismissProgress();
                if (task.isSuccessful())
                    constants.showToast(requireActivity(),"Email sent");
                else
                {
                    constants.showToast(requireActivity(),"invalid Email");
                }
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e)
            {
                constants.dismissProgress();
                constants.showToast(requireActivity(), ""+e.getMessage());
            }
        });
    }
}
