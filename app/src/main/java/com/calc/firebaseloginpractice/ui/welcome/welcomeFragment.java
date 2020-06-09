package com.calc.firebaseloginpractice.ui.welcome;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.calc.firebaseloginpractice.R;
import com.calc.firebaseloginpractice.ui.login.loginFragment;
import com.calc.firebaseloginpractice.ui.register.registerFragment;
import com.calc.firebaseloginpractice.utils.constants;

public class welcomeFragment extends Fragment
{
    private View mainView;
    private Button loginBtn;
    private Button registerBtn;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView=inflater.inflate(R.layout.welcome_fragment,null);
        return mainView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initViews();
    }

    private void initViews()
    {
        loginBtn=mainView.findViewById(R.id.welcome_login_btn);
        registerBtn=mainView.findViewById(R.id.welcome_register_btn);

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
}
