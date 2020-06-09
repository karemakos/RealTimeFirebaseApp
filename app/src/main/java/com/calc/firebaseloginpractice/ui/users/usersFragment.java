package com.calc.firebaseloginpractice.ui.users;

import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.calc.firebaseloginpractice.R;
import com.calc.firebaseloginpractice.models.postModel;
import com.calc.firebaseloginpractice.models.userModel;
import com.calc.firebaseloginpractice.ui.chats.chatsFragment;
import com.calc.firebaseloginpractice.ui.timeline.posts.newPostFragment;
import com.calc.firebaseloginpractice.ui.timeline.timeLineFragment;
import com.calc.firebaseloginpractice.utils.constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class usersFragment extends Fragment
{
    private View mainView;
    private RecyclerView recyclerView;
    private List<userModel> userModels;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView= inflater.inflate(R.layout.fragment_users,null);
        return mainView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
       // constants.initProgress(getContext(),"Please wait...");
        initVies();
        getUsers();

}

    private void getUsers()
    {

        constants.getDatabaseReference().child("users").addValueEventListener(new ValueEventListener()
        {

            @Override
            // this methods 4 going to the users Node and bring the data from there
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                userModels.clear();

                for (DataSnapshot d : dataSnapshot.getChildren())
                {
                    userModel model= d.getValue(userModel.class);

                    // this to dont see myself on the chat list from the mainpage cuz not logic to chat with myself
                    if (!model.getUid().equals(constants.getUid(requireActivity())))
                    {
                        userModels.add(model);
                    }
                }

                recyclerView.setAdapter(new usersAdapter(userModels));

                constants.dismissProgress();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }

    private void initVies()
    {
        recyclerView=mainView.findViewById(R.id.users_recycler);
        DividerItemDecoration dividerItemDecoration= new DividerItemDecoration(requireActivity(),DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        userModels= new ArrayList<>();

    }


    private class usersAdapter extends RecyclerView.Adapter<usersAdapter.VH>
    {
        List<userModel> userModelsList;

        usersAdapter(List<userModel> userModelsList)
        {

            this.userModelsList = userModelsList;
        }

        @NonNull
        @Override
        public usersAdapter.VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(requireContext()).inflate(R.layout.users_item, parent, false);
            return new usersAdapter.VH(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final VH holder, int position)
        {
            final userModel model= userModelsList.get(position);

            String name= model.getName();
            String address= model.getAddress();
            String image= model.getImageUri();

            holder.userName.setText(name);
            holder.userAddress.setText(address);

            Picasso
                    .get()
                    .load(image)
                    .into(holder.userImage);

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    constants.replaceFragment(usersFragment.this, new chatsFragment(),true);

                    // this is to take the person info that we pressed on it to chat
                    constants.myChats= model;
                }
            });
        }

        @Override
        public int getItemCount() {
            return userModelsList.size();
        }


        private class VH extends RecyclerView.ViewHolder
        {
            private   CircleImageView userImage;
            private    TextView userName;
            private   TextView userAddress;

            VH(@NonNull View itemView) {
                super(itemView);

              userName=itemView.findViewById(R.id.user_name);
              userAddress=itemView.findViewById(R.id.user_address);
              userImage=itemView.findViewById(R.id.user_image);
            }
        }
    }
}