package com.calc.firebaseloginpractice.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.calc.firebaseloginpractice.R;
import com.calc.firebaseloginpractice.ui.myChats.myChatsFragment;
import com.calc.firebaseloginpractice.ui.profile.profileFragment;
import com.calc.firebaseloginpractice.ui.rooms.RoomsFragment;
import com.calc.firebaseloginpractice.ui.rooms.myRoomsFragment;
import com.calc.firebaseloginpractice.ui.rooms.roomrRequests.roomsRequestsFragment;
import com.calc.firebaseloginpractice.ui.timeline.timeLineFragment;
import com.calc.firebaseloginpractice.ui.users.usersFragment;
import com.calc.firebaseloginpractice.ui.welcome.welcomeFragment;
import com.calc.firebaseloginpractice.utils.constants;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

public class homeFragment extends Fragment
{
    private View mainView;
    private ViewPager2 viewPager2;
    private TabLayout tabLayout;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView= inflater.inflate(R.layout.fragment_home,null);
        return mainView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initViews();
    }

    private void initViews()
    {
        viewPager2= mainView.findViewById(R.id.viewPager);
        tabLayout= mainView.findViewById(R.id.tabs);

        //2- Tablayout Adapter to link it
        List<Fragment> fragments= new ArrayList<>();
        fragments.add(new timeLineFragment());
        fragments.add(new usersFragment());
        fragments.add(new RoomsFragment());
        fragments.add(new myRoomsFragment());
        fragments.add(new profileFragment());
        fragments.add(new myChatsFragment());



        // 2-to link the TabLayout to the ViewPager2
        final List<String> names= new ArrayList<>();
       names.add("Home");
       names.add("Users");
       names.add("Rooms");
       names.add("My Rooms");
       names.add("Profile");
       names.add("My chats");



        tabsAdapter adapter= new tabsAdapter(this,  fragments);
        viewPager2.setAdapter(adapter);



        // 1- to link the TabLayout to the ViewPager2
        new TabLayoutMediator(tabLayout, viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position)
            {
                tab.setText(names.get(position));
//                if (position==0)
//                {
//                    tab.setIcon(R.drawable.ic_home_black_24dp);
//
//                } else if (position==1)
//                {
//                    tab.setIcon(R.drawable.ic_chat_black_24dp);
//                } else
//                {
//                    tab.setIcon(R.drawable.ic_person_black_24dp);
//                }
//                tab.setText(names.get(position));


            }
        }).attach();

        // if i want to open in specific tab
        //tabLayout.getTabAt(whatever number of the tabs).select();
    }

// 1- for TabLayout we create the adapter
public class tabsAdapter extends FragmentStateAdapter
{
    private List<Fragment> fragmentList;

    tabsAdapter(Fragment fragment, List<Fragment> fragments) {
        super(fragment);
        this.fragmentList=fragments;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {

        return fragmentList.get(position);

    }

    @Override
    public int getItemCount() {
        return fragmentList.size();
    }
}
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu,menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item)
    {
        if (item.getItemId() == R.id.main_logout_btn)
        {
            constants.getAuth().signOut();
            constants.saveUid(requireActivity(),"empty");
            constants.replaceFragment(homeFragment.this, new welcomeFragment(),false);


        } else  if (item.getItemId() == R.id.main_settings_btn)
        {
            //constants.replaceFragment(homeFragment.this,new settingsFragment(),true);
        }
        return true;
    }

}
