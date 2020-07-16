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
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.calc.firebaseloginpractice.R;
import com.calc.firebaseloginpractice.models.chatModel;
import com.calc.firebaseloginpractice.models.userModel;
import com.calc.firebaseloginpractice.ui.chats.chatsFragment;
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
    private List<chatModel> chatModelList;


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
        initViews();
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

                    if (getActivity()!=null)
                    {
                        if (model!=null)
                        {
                            // this to dont see myself on the chat list from the mainpage cuz not logic to chat with myself
                            if (!model.getUid().equals(constants.getUid(requireActivity())))
                            {
                                userModels.add(model);
                            }
                        }

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

    private void initViews()
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
            View view = LayoutInflater.from(requireContext()).inflate(R.layout.item_users, parent, false);
            return new usersAdapter.VH(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final VH holder, int position)
        {
            final userModel model= userModelsList.get(position);

            String name= model.getName();
           // String address= model.getAddress();
            String image= model.getImageUri();

            holder.userName.setText(name);
         //   holder.userAddress.setText(address);





            setTime(model,holder.time);
            showMessage(model,holder.userLastMessage);

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


        void setTime(userModel userModel, final TextView textView)
        {
            constants.getDatabaseReference().child("Chats").child(constants.getUid(requireActivity())).child(userModel.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    List<chatModel> chatModelsList = new ArrayList<>();

                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                    {
                        chatModel chatModel= dataSnapshot1.getValue(chatModel.class);
                        chatModelsList.add(chatModel);
                    }

                    for(int i = 0 ; i < chatModelsList.size() ; i ++)
                    {
                        if (i == chatModelsList.size() - 1)
                        {
                            long now = System.currentTimeMillis();
                            CharSequence ago =   DateUtils.getRelativeTimeSpanString(chatModelsList.get(i).getTime(), now, DateUtils.MINUTE_IN_MILLIS);
                            textView.setText(ago);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


        void showMessage(userModel userModel, final TextView textView)
        {
            constants.getDatabaseReference().child("Chats").child(constants.getUid(requireActivity())).child(userModel.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    List<chatModel> chatModelsList = new ArrayList<>();

                    for(DataSnapshot dataSnapshot1 : dataSnapshot.getChildren())
                    {
                        chatModel model= dataSnapshot1.getValue(chatModel.class);
                        chatModelsList.add(model);
                    }

                    for(int i = 0 ; i < chatModelsList.size() ; i ++)
                    {
                        if (i == chatModelsList.size() - 1)
                        {
                            String message= chatModelsList.get(i).getMessage();
                            textView.setText(message);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }




        private class VH extends RecyclerView.ViewHolder
        {
            private   CircleImageView userImage;
            private    TextView userName;
            private   TextView userLastMessage;
            ImageView seenOne;
            ImageView seenTwo;
            private   TextView time;
            boolean isTextViewClicked = false;


            VH(@NonNull View itemView) {
                super(itemView);

              userName=itemView.findViewById(R.id.user_name);
                userLastMessage=itemView.findViewById(R.id.user_lastMessage);
              userImage=itemView.findViewById(R.id.user_image);
                seenOne=itemView.findViewById(R.id.users_seen_one);
                seenTwo=itemView.findViewById(R.id.users_seen_two);
                time=itemView.findViewById(R.id.users_time_text);



                if (isTextViewClicked) {
                    userLastMessage.setMaxLines(1);
                    isTextViewClicked = false;
                }
            }
        }
    }
}

