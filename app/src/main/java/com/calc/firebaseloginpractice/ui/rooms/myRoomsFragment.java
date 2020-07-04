package com.calc.firebaseloginpractice.ui.rooms;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.calc.firebaseloginpractice.R;
import com.calc.firebaseloginpractice.models.roomsModel;
import com.calc.firebaseloginpractice.ui.profile.profileFragment;
import com.calc.firebaseloginpractice.ui.welcome.welcomeFragment;
import com.calc.firebaseloginpractice.utils.constants;
import com.google.android.material.datepicker.SingleDateSelector;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


//>>>>>>>>>
public class myRoomsFragment extends Fragment
{
    private View mainView;
    private RecyclerView recyclerView;
    private List<roomsModel> roomsModels =new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_myrooms, null);
        return mainView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
         constants.initProgress(getContext(),"Please wait...");

        initViews();
        getMyRooms();
    }

    private void initViews() {
        recyclerView = mainView.findViewById(R.id.myRooms_recycler);
       DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(requireActivity(), DividerItemDecoration.VERTICAL);
       recyclerView.addItemDecoration(dividerItemDecoration);



        // so, whatever comes where we created it will add here as ArrayList
        roomsModels = new ArrayList<>();
    }

    private void getMyRooms()
    {
        constants.getDatabaseReference().child("Rooms").addValueEventListener(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                roomsModels.clear();

                for (DataSnapshot d : dataSnapshot.getChildren())
                {
                    roomsModel model = d.getValue(roomsModel.class);

                    String id = constants.getUid(requireActivity());
                    if (id.equals(model.getuId()))
                        roomsModels.add(model);
                }

                recyclerView.setAdapter(new roomsAdapter(roomsModels));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }

    private class roomsAdapter extends RecyclerView.Adapter<roomsAdapter.VH>
    {
        List<roomsModel> roomsModelList;

        roomsAdapter(List<roomsModel> roomsModelList)
        {
            this.roomsModelList = roomsModelList;
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(requireContext()).inflate(R.layout.item_myrooms, parent, false);
            return new VH(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final VH holder, int position) {
            final roomsModel model = roomsModelList.get(position);

            String title = model.getRoomTitle();
            String owner = model.getOwnerName();
            String image = model.getRoomImage();


            holder.roomTitle.setText(title);
            holder.roomOwnerName.setText(owner);

            if (image.isEmpty())
            {
                holder.roomOwnerImage.setImageResource(R.drawable.logosp);
            } else
            {
                Picasso.get()
                        .load(image)
                        .into(holder.roomOwnerImage);
            }


            setRoomMembers(model.getRoomId(), holder.roomMembers);
            setRoomRequests(model.getRoomId(), holder.roomRequests);

            if (model.isPrivate()) {
                holder.privateRoom.setVisibility(View.VISIBLE);
            }

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v)
                {
                    constants.getDatabaseReference().child("Rooms").child(model.getRoomId()).removeValue();
                    return false;
                }
            });


            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    if (model.isPrivate())
                    {
                        constants.roomsModel= model;
                        constants.replaceFragment(myRoomsFragment.this,new roomsRequestsFragment(),true);
                    }
                    else
                    {
                        constants.roomsModel= model;
                        constants.replaceFragment(myRoomsFragment.this,new roomsChatFragment(),true);
                    }

                }
            });
        }


            void setRoomMembers(String roomId, final TextView textView)
            {
                constants.getDatabaseReference().child("Members").child(roomId).addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        long count = dataSnapshot.getChildrenCount();
                        textView.setText("Members " + count);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError)
                    {

                    }
                });
            }

            void setRoomRequests(String roomId, final TextView textView)
            {
                String uId = constants.getUid(requireActivity());

                constants.getDatabaseReference().child("RoomsRequests").child(uId).child(roomId).addValueEventListener(new ValueEventListener()
                {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        long count = dataSnapshot.getChildrenCount();

                        //if (count == 0)
                     //   {
                         //  textView.setVisibility(View.GONE);
                     //   } else
                        {
                            textView.setVisibility(View.VISIBLE);
                            textView.setText("requests " + count);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError)
                    {

                    }
                });
            }




        @Override
        public int getItemCount() {
            return roomsModelList.size();
        }


        private class VH extends RecyclerView.ViewHolder
        {

            CircleImageView roomOwnerImage;
            ImageView privateRoom;
            TextView roomTitle;
            TextView roomMembers;
            TextView roomRequests;
            TextView roomOwnerName;

            VH(@NonNull View itemView) {
                super(itemView);

                roomOwnerImage = itemView.findViewById(R.id.room_user_image);
                privateRoom = itemView.findViewById(R.id.private_room);
                roomTitle = itemView.findViewById(R.id.room_title);
                roomMembers = itemView.findViewById(R.id.myRoom_members);
                roomRequests = itemView.findViewById(R.id.myRoom_requests);
                roomOwnerName = itemView.findViewById(R.id.room_owner_name);

            }
        }
    }
}
