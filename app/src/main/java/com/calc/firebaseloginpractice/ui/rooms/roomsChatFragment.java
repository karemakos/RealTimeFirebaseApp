package com.calc.firebaseloginpractice.ui.rooms;

import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.calc.firebaseloginpractice.R;
import com.calc.firebaseloginpractice.models.roomsChatsModel;
import com.calc.firebaseloginpractice.models.userModel;
import com.calc.firebaseloginpractice.ui.profile.profileFragment;
import com.calc.firebaseloginpractice.ui.welcome.welcomeFragment;
import com.calc.firebaseloginpractice.utils.constants;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class roomsChatFragment extends Fragment
{
    private View mainView;
    private RecyclerView recyclerView;
    private EditText chatFiled;
    private FloatingActionButton sendFab;
    private Toolbar toolbar;
    private String roomChatUsername;
    private String roomChatUserImage;

    private List<roomsChatsModel> roomsChatsModels;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView= inflater.inflate(R.layout.fragment_roomschats,null);
        return mainView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

     constants.initProgress(getContext(),"Please wait...");
        initViews();
        getData();
        getChatRooms();


}
    private void getChatRooms()
    {

        // we get all messages inside the rooms
        constants.getDatabaseReference().child("RoomsChats").child(constants.roomsModel.getRoomId())
                .addValueEventListener(new ValueEventListener()
                {

                    @Override
                    // this methods 4 going to the users Node and bring the data from there
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        constants.dismissProgress();

                        roomsChatsModels.clear();
                        for (DataSnapshot d : dataSnapshot.getChildren())
                        {
                            roomsChatsModel model= d.getValue(roomsChatsModel.class);
                            roomsChatsModels.add(model);
                        }

                        recyclerView.setAdapter(new roomschatAdapter(roomsChatsModels));

                        // to scroll the chat to last position
                        recyclerView.scrollToPosition(roomsChatsModels.size()-1);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError)
                    {

                    }
                });
    }


    private void initViews()
    {
        recyclerView=mainView.findViewById(R.id.roomschat_recycler);
        chatFiled=mainView.findViewById(R.id.roomschat_message_body_field);
        sendFab=mainView.findViewById(R.id.roomChat_sendFab);
        toolbar=mainView.findViewById(R.id.roomschat_toolbar);

        roomsChatsModels= new ArrayList<>();

        sendFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                String message = chatFiled.getText().toString();


                if (!message.isEmpty())
                {
                    sendMessage(message);
                }

            }
        });

        toolbar.inflateMenu(R.menu.main_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item)
            {
                switch (item.getItemId())
                {
                    case R.id.main_logout_btn:
                        constants.getAuth().signOut();
                        constants.saveUid(requireActivity(),"empty");
                        constants.replaceFragment(roomsChatFragment.this,new welcomeFragment(),false);
                        break;
                    case R.id.main_settings_btn:
                        constants.replaceFragment(roomsChatFragment.this,new profileFragment(),true);

                        break;
                }
                return false;
            }
        });

        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_white_ios_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                requireActivity().onBackPressed();

            }
        });

       // constants.dismissProgress();

    }


    private void getData()
    {

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

                   roomChatUsername=model.getName();
                     roomChatUserImage=model.getImageUri();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void sendMessage(String message)
    {
        final String senderId =constants.getUid(requireActivity());
        String roomid=constants.roomsModel.getRoomId();

        // this is hollamy to make the message ID in order to include msg id between two users
        final String key= constants.getDatabaseReference().child("RoomsChats").child(roomid).push().getKey();
        final roomsChatsModel roomsChatsModel= new roomsChatsModel
                (
                      roomid,key,message,senderId, constants.getTime(),1,roomChatUsername,roomChatUserImage
                );

        if (key!=null)
        {
            constants.getDatabaseReference().child("RoomsChats").child(constants.roomsModel.getRoomId()).child(key).setValue(roomsChatsModel);

            chatFiled.setText("");

        }

    }

    private class roomschatAdapter extends RecyclerView.Adapter<roomschatAdapter.VH> {
        List<roomsChatsModel> roomsChatsModelList;


        public roomschatAdapter(List<roomsChatsModel> roomsChatsModelList) {
            this.roomsChatsModelList = roomsChatsModelList;
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(requireContext()).inflate(R.layout.item_roomschat, parent, false);
            return new roomschatAdapter.VH(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final roomschatAdapter.VH holder, int position) {
            final roomsChatsModel model = roomsChatsModelList.get(position);

            String messageId = model.getMessageId();
            String message = model.getMessage();
            String id = model.getSenderID();
            String chatRoomUserName=model.getChatRoomUserName();
            String chatRoomUserImage=model.getChatRoomUserImage();



            holder.message.setText(message);



            Picasso.get().load(chatRoomUserImage).into(holder.roomChatUserImage);
            holder.roomChatUserName.setText(chatRoomUserName);

            if (id.equals(constants.getUid(requireActivity()))) {
                holder.chatLinear.setGravity(Gravity.END);
                holder.card.setCardBackgroundColor(getResources().getColor(R.color.green));
                holder.message.setTextColor(getResources().getColor(R.color.white));
                holder.roomChatUserName.setVisibility(View.GONE);
                holder.roomChatUserImage.setVisibility(View.GONE);
            }




         //   setSeen(model.getMessageId());
            //isSeen(model.getMessageId(), holder.seenOne, holder.seenTwo);

        }


        @Override
        public int getItemCount() {
            return roomsChatsModelList.size();
        }

        private class VH extends RecyclerView.ViewHolder {

            private TextView message;
            private CardView card;
            private LinearLayout chatLinear;
            private TextView roomChatUserName;
            private CircleImageView roomChatUserImage ;

            VH(@NonNull View itemView) {
                super(itemView);

                message = itemView.findViewById(R.id.roomschat_message_text);
                card = itemView.findViewById(R.id.roomschat_card);
                chatLinear = itemView.findViewById(R.id.roomschat_linear);
                roomChatUserName=itemView.findViewById(R.id.roomchat_user_name);
                roomChatUserImage=itemView.findViewById(R.id.roomchat_user_image);

            }
        }

    }
}