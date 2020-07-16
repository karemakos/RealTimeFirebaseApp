package com.calc.firebaseloginpractice.ui.rooms.roomrRequests;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.calc.firebaseloginpractice.R;
import com.calc.firebaseloginpractice.models.roomRequestModel;
import com.calc.firebaseloginpractice.models.roomsModel;
import com.calc.firebaseloginpractice.models.userModel;
import com.calc.firebaseloginpractice.ui.chats.chatsFragment;
import com.calc.firebaseloginpractice.ui.rooms.roomsChatFragment;
import com.calc.firebaseloginpractice.utils.constants;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class roomsRequestsFragment extends Fragment
{
    private View mainView;
    private RecyclerView recyclerView;
    private List<userModel> roomRequestModels =new ArrayList<>();
    private roomsModel roomsModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_roomsrequests, null);
        return mainView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        constants.initProgress(getContext(),"Please wait...");

        initViews();
       getMyRequests();

    }

    private void getMyRequests()
    {
        constants.showProgress();
            constants.getDatabaseReference().child("RoomsRequests").child(constants.roomsModel.getRoomId()).addValueEventListener(new ValueEventListener()
            {

                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    constants.dismissProgress();
                    roomRequestModels.clear();

                    for (DataSnapshot d : dataSnapshot.getChildren())
                    {
                        if (roomRequestModels!=null)
                        {
                            userModel model= d.getValue(userModel.class);
                            // to make condition when i create a room it will not show on the main rooms, it will only show when i press on my rooms only
                            roomRequestModels.add(model);


                        }

                    }


                    recyclerView.setAdapter(new roomsRequestsFragment.roomsRequestAdapter(roomRequestModels));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError)
                {

                }
            });

    }

    private void initViews()
    {

        recyclerView = mainView.findViewById(R.id.roomsRequest_recycler);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(requireActivity(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        // so, whatever comes where we created it will add here as ArrayList
        roomRequestModels = new ArrayList<>();

    }

    private class roomsRequestAdapter extends RecyclerView.Adapter<roomsRequestAdapter.VH>
    {
        List<userModel> roomRequestModelList;

        public roomsRequestAdapter(List<userModel> roomRequestModelList) {
            this.roomRequestModelList = roomRequestModelList;
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(requireContext()).inflate(R.layout.item_roomsrequests, parent, false);
            return new VH(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final VH holder, int position)
        {
            final userModel model= roomRequestModelList.get(position);

            String roomTitle= constants.roomsModel.getRoomTitle();
            holder.roomName.setText(roomTitle);
           holder.roomName.setText("Room : "+roomTitle);

       String username= model.getName();
       holder.userName.setText(username);
          holder.userName.setText("User: "+username);




            String image=model.getImageUri();
            Picasso.get()
                    .load(image)
                    .into(holder.requesterUserImage);



            holder.requestAccept.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    accept(constants.roomsModel.getRoomId(),model);
                }
            });
          holder.requestRefuse.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View v) {
                  reject(constants.roomsModel.getRoomId(),model);
              }
          });
        }


        void accept(final String roomID, final userModel userModel)
        {
            constants.getDatabaseReference().child("RoomsRequests").child(roomID).child(constants.roomsModel.getuId()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {


                        constants.getDatabaseReference().child("RoomsRequests").child(roomID).child(userModel.getUid()).removeValue();
                        constants.getDatabaseReference().child("Members").child(roomID).child(userModel.getUid()).setValue(true);

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        void reject(final String roomID, final userModel userModel)
        {
            constants.getDatabaseReference().child("RoomsRequests").child(roomID).child(constants.roomsModel.getuId()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {


                        constants.getDatabaseReference().child("RoomsRequests").child(roomID).child(userModel.getUid()).removeValue();
                       // theItemView.setVisibility(View.GONE);



                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


        @Override
        public int getItemCount() {
            return roomRequestModelList.size();
        }




        private class VH extends RecyclerView.ViewHolder
        {

             TextView roomName;
            CircleImageView requesterUserImage;
             TextView userName;
             TextView requestAccept;
             TextView requestRefuse;
            LinearLayout theItemView;

            VH(@NonNull View itemView) {
                super(itemView);

                roomName=itemView.findViewById(R.id.roomsRequest_name);
                requesterUserImage = itemView.findViewById(R.id.requester_user_image);
                userName=itemView.findViewById(R.id.userRequest_name);
                requestAccept=itemView.findViewById(R.id.Accept_Request);
                requestRefuse=itemView.findViewById(R.id.reject_request);
                theItemView = itemView.findViewById(R.id.item_rooms_requests);



            }
        }
    }
}
