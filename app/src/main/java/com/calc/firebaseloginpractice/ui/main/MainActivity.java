package com.calc.firebaseloginpractice.ui.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;

import com.calc.firebaseloginpractice.R;
import com.calc.firebaseloginpractice.ui.home.homeFragment;
import com.calc.firebaseloginpractice.ui.welcome.welcomeFragment;
import com.calc.firebaseloginpractice.utils.constants;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        constants.initFirebase();

        if (!constants.getUid(this).equals("empty"))
        {
            startFragment(new homeFragment());
        } else
        {
            startFragment(new welcomeFragment());
        }

    }


    private void startFragment(Fragment fragment)
    {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_container, fragment)
                .disallowAddToBackStack()
                .commit();
    }
}
