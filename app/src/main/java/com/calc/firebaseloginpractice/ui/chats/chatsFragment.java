package com.calc.firebaseloginpractice.ui.chats;

import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import com.calc.firebaseloginpractice.models.myChatsModel;
import com.calc.firebaseloginpractice.models.chatModel;
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

public class chatsFragment extends Fragment
{
    private View mainView;
    private RecyclerView recyclerView;
    private EditText chatFiled;
    private FloatingActionButton sendFab;
    private Toolbar toolbar;
    private TextView chatUsername;
    private CircleImageView chatUserImage;

    private List<chatModel> chatModels;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView= inflater.inflate(R.layout.fragment_chats,null);
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        return mainView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

       initViews();
       getChat();

    }

    private void getChat()
    {

        //get  chats/ my id / whoever id we chosed from users /  get the chat
        constants.getDatabaseReference().child("Chats").child(constants.getUid(requireActivity()))
                .child(constants.myChats.getUid())
                .addValueEventListener(new ValueEventListener()
        {

            @Override
            // this methods 4 going to the users Node and bring the data from there
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                chatModels.clear();

                for (DataSnapshot d : dataSnapshot.getChildren())
                {
                    chatModel model= d.getValue(chatModel.class);
                    chatModels.add(model);
                }

                recyclerView.setAdapter(new chatAdapter(chatModels));

                // to scroll the chat to last position
                recyclerView.scrollToPosition(chatModels.size()-1);
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
        recyclerView=mainView.findViewById(R.id.chat_recycler);
        chatFiled=mainView.findViewById(R.id.message_body_field);
        sendFab=mainView.findViewById(R.id.send_message_fab);
        toolbar=mainView.findViewById(R.id.chat_toolbar);
        chatUsername=mainView.findViewById(R.id.privateChat_user_name);
        chatUserImage=mainView.findViewById(R.id.privateChat_user_image);

        chatModels= new ArrayList<>();

        sendFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                String message = chatFiled.getText().toString();


                if (message.isEmpty())
                {
                    constants.showToast(requireContext(), "Please your Message");
                    return;

                }
                sendMessage(message);

            }
        });


        toolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_white_ios_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().onBackPressed();
            }
        });

        chatUsername.setText(constants.myChats.getName());

        Picasso
                .get()
                .load(constants.myChats.getImageUri())
                .into(chatUserImage);
    }

    private void sendMessage(String message)
    {
        final String senderId =constants.getUid(requireActivity());
        final String receivedId =constants.myChats.getUid();

        // this is hollamy to make the message ID in order to include msg id between two users
        final String key= constants.getDatabaseReference().child("Chats").child(senderId).child(receivedId).push().getKey();
                final chatModel chatModel= new chatModel
                (
                       senderId
                        ,message
                        ,constants.getTime()
                        ,1
                        ,key
                );


        final myChatsModel myChatsModel = new myChatsModel
                (
                        constants.myChats.getName(),
                        constants.myChats.getImageUri()
                );

        // ("Chats") it has to be with same name as the one we get the database from on the method     private void getChat()

        if (key!=null)
        {
            constants.getDatabaseReference().child("Chats").child(senderId).child(receivedId).child(key).setValue(chatModel);
            constants.getDatabaseReference().child("Chats").child(receivedId).child(senderId).child(key).setValue(chatModel);



            // we will not get key here cuz this only to read from the list of the friends like the mainpage of whatsapp
            constants.getDatabaseReference().child("MyChats").child(senderId).child(receivedId).setValue(constants.myChats);
            constants.getDatabaseReference().child("users").child(constants.getUid(requireActivity())).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    userModel userModel=dataSnapshot.getValue(userModel.class);
                    constants.getDatabaseReference().child("MyChats").child(receivedId).child(senderId).setValue(userModel);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


            chatFiled.setText("");

        }

    }


    private class chatAdapter extends RecyclerView.Adapter<chatAdapter.VH>
    {
        List<chatModel> chatModelList;


        chatAdapter(List<chatModel> chatModelList) {
            this.chatModelList = chatModelList;
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
            View view = LayoutInflater.from(requireContext()).inflate(R.layout.item_chat_message, parent, false);
            return new VH(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final VH holder, int position)
        {
            final chatModel model= chatModelList.get(position);

            String message= model.getMessage();
            String id= model.getSenderId();
            long time= model.getTime();


            holder.message.setText(message);

            long now = System.currentTimeMillis();
            CharSequence ago =   DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS);
            holder.time.setText(ago);

            if (id.equals(constants.getUid(requireActivity())))
            {
                holder.chatLinear.setGravity(Gravity.END);
                holder.card.setCardBackgroundColor(getResources().getColor(R.color.green));
                holder.message.setTextColor(getResources().getColor(R.color.white));
                holder.time.setTextColor(getResources().getColor(R.color.white));

            } else if (id.equals(constants.myChats.getUid()))
            {
                holder.chatLinear.setGravity(Gravity.START);
                holder.card.setCardBackgroundColor(getResources().getColor(R.color.colorAccent));
                holder.message.setTextColor(getResources().getColor(R.color.white));
                holder.time.setTextColor(getResources().getColor(R.color.white));
            }


            setSeen(model.getId());
            isSeen(model.getId(),holder.seenOne,holder.seenTwo);

        }


        @Override
        public int getItemCount() {
            return chatModelList.size();
        }



        // for seen purpose
        void setSeen(String id)
        {//Question what the different between the String id and constants.getUid
            constants.getDatabaseReference().child("Seen").child(constants.getUid(requireActivity()))
                    .child(constants.myChats.getUid()).child(id).setValue(true);

        }
        void isSeen(final String id, final ImageView imageView,final ImageView imageViewTwo)
        {
            constants.getDatabaseReference().child("Seen").child(constants.myChats.getUid())
                    .child(constants.getUid(requireActivity())).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    if (!dataSnapshot.hasChild(id))
                    {
                        imageViewTwo.setVisibility(View.GONE);
                        imageView.setVisibility(View.VISIBLE);
                        imageView.setImageResource(R.drawable.ic_check_black_24dp);
                    } else
                    {
                        imageViewTwo.setVisibility(View.VISIBLE);
                        imageView.setVisibility(View.GONE);
                        imageViewTwo.setImageResource(R.drawable.seen);
//                     imageViewTwo.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.like)));

                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError)
                {

        }
                    });}

        private class VH extends RecyclerView.ViewHolder
        {

            private TextView message;
            private   TextView time;
            ImageView seenOne;
            ImageView seenTwo;
            private CardView card;
            private LinearLayout chatLinear;

            VH(@NonNull View itemView) {
                super(itemView);

                message=itemView.findViewById(R.id.chat_message_text);
                time=itemView.findViewById(R.id.chat_time_text);
                card=itemView.findViewById(R.id.chat_card);
                chatLinear=itemView.findViewById(R.id.chat_linear);
                seenOne=itemView.findViewById(R.id.chat_seen_one);
                seenTwo=itemView.findViewById(R.id.chat_seen_two);

            }
        }

    }


}
