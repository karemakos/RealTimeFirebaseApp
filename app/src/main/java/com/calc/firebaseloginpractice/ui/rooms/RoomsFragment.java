package com.calc.firebaseloginpractice.ui.rooms;

import android.app.Activity;
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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.calc.firebaseloginpractice.R;
import com.calc.firebaseloginpractice.models.roomsModel;
import com.calc.firebaseloginpractice.models.userModel;
import com.calc.firebaseloginpractice.ui.chats.chatsFragment;
import com.calc.firebaseloginpractice.utils.constants;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RoomsFragment extends Fragment {
    private View mainView;
    private RecyclerView recyclerView;
    private FloatingActionButton addRoom;
    private List<roomsModel> roomsModels;
    private userModel userModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView = inflater.inflate(R.layout.fragment_rooms, null);
        return mainView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
  // constants.initProgress(getContext(),"Please wait...");
        initViews();
        getRooms();
        getUser();
    }

    private void getUser()
    {
        String uId = constants.getUid(requireActivity());

        constants.getDatabaseReference().child("users").child(uId).addListenerForSingleValueEvent(new ValueEventListener()
        {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                userModel = dataSnapshot.getValue(userModel.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }


    private void getRooms()
    {

        constants.getDatabaseReference().child("Rooms").addValueEventListener(new ValueEventListener()
        {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
             //   constants.showProgress();
                roomsModels.clear();

                for (DataSnapshot d : dataSnapshot.getChildren())
                {
                    roomsModel model= d.getValue(roomsModel.class);

                    if (model!=null)
                    {
                        if (!constants.getUid(getActivity()).equals(model.getuId()))
                        {
                            roomsModels.add(model);
                            //  constants.dismissProgress();
                        }
                    }
                    // to make condition when i create a room it will not show on the main rooms, it will only show when i press on my rooms only

                }

                recyclerView.setAdapter(new RoomsFragment.roomsAdapter(roomsModels));


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError)
            {

            }
        });
    }


    private void initViews() {
        recyclerView = mainView.findViewById(R.id.rooms_recycler);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(requireActivity(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        addRoom=mainView.findViewById(R.id.add_room_fab);

        roomsModels = new ArrayList<>();
        addRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                constants.replaceFragment(RoomsFragment.this,new newRoomFragment (),true);
            }
        });
    }

    private class roomsAdapter extends RecyclerView.Adapter<roomsAdapter.VH>
    {
        List<roomsModel> roomsModelsList;

        roomsAdapter(List<roomsModel> roomsModelsList)
        {

            this.roomsModelsList = roomsModelsList;
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(requireContext()).inflate(R.layout.item_rooms, parent, false);
            return new roomsAdapter.VH(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final roomsAdapter.VH holder, final int position)
        {
            final roomsModel model= roomsModelsList.get(position);

            String roomTitle= model.getRoomTitle();
            String ownerName= model.getOwnerName();
            String image= model.getRoomImage();



            holder.roomName.setText(roomTitle);
            holder.roomCreator.setText(ownerName);


            if (image.isEmpty())
            {
                holder.roomImage.setImageResource(R.drawable.logosp);

            }
            else
            {
                Picasso
                        .get()
                        .load(image)
                        .into(holder.roomImage);
            }




            if (model.isPrivate())
            {
                holder.privateRoom.setVisibility(View.VISIBLE);
            }



            // to leave the room
            holder.itemView.setOnLongClickListener(new View.OnLongClickListener()
            {
                @Override
                public boolean onLongClick(View v)
                {
                    Activity activity= new Activity();
                    if (activity!=null)
                    {
                        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(requireActivity());
                        builder.setMessage("Are you sure to exit this room?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog, int id)
                                    {

                                        String uId = constants.getUid(requireActivity());
                                        constants.getDatabaseReference().child("Members").child(model.getRoomId()).child(uId).removeValue();


                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener()
                                {
                                    public void onClick(DialogInterface dialog, int id)
                                    {
                                        // User cancelled the dialog
                                        dialog.dismiss();

                                    }

                                });
                        AlertDialog dialog = builder.create();
                        dialog.show();
                    }




                    return false;
                }
            });

            setRoomMembers(model.getRoomId(), holder.roomMembers);

            isMember(holder.itemView,holder.unlockPrivateRoom,holder.privateRoom,model);


        }
        void setRoomMembers(String roomId, final TextView textView) {
            constants.getDatabaseReference().child("Members").child(roomId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    long count = dataSnapshot.getChildrenCount();
                    textView.setText(String.valueOf(count));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        @Override
        public int getItemCount() {
            return roomsModelsList.size();
        }


        //>>>>>>>>>>>>>>>>>> check
        void isMember(final View view, final ImageView unlocked, final ImageView locked, final roomsModel model)
        {

            constants.getDatabaseReference().child("Members").child(model.getRoomId()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild(constants.getUid(requireActivity())))
                    {

                      view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v)
                            {

                                  constants.roomsModel= model;
                                   constants.replaceFragment(RoomsFragment.this,new roomsChatFragment(),true);

                                   //constants.dismissProgress();

                            }
                        });
                    } else
                    {



                        view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v)
                            {

                                final String uId = constants.getUid(requireActivity());

                              if (!model.isPrivate())
                              {
                              constants.roomsModel= model;
                               constants.getDatabaseReference().child("Members").child(model.getRoomId()).child(uId).setValue(true);
                                constants.replaceFragment(RoomsFragment.this,new roomsChatFragment(),true);
                               // constants.dismissProgress();

                              } else
                            {

                                constants.showToast(requireActivity(),"Your request has been sent to the Room owner");
                              constants.roomsModel= model;
                                constants.getDatabaseReference().child("RoomsRequests").child(model.getRoomId()).child(uId).setValue(userModel);

                               }
                            }

                        });

                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

            constants.getDatabaseReference().child("RoomsRequests").child(model.getRoomId()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    Activity activity= new Activity();
                    if (activity != null)
                    {
                        if (dataSnapshot.hasChild(constants.getUid(requireActivity())))
                        {

                            unlocked.setVisibility(View.GONE);
                            locked.setVisibility(View.VISIBLE);

                        } else if (!dataSnapshot.hasChild(constants.getUid(requireActivity())))
                        {
                            locked.setVisibility(View.GONE);
                            unlocked.setVisibility(View.VISIBLE);

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

            private TextView roomName;
            private   TextView roomCreator;
            TextView roomMembers;
            private CircleImageView roomImage;
            ImageView privateRoom;
            ImageView unlockPrivateRoom;

            VH(@NonNull View itemView) {
                super(itemView);

                roomName=itemView.findViewById(R.id.room_name);
                roomCreator=itemView.findViewById(R.id.room_creator);
                roomImage=itemView.findViewById(R.id.room_image);
                privateRoom = itemView.findViewById(R.id.mainRooms_private_room);
                unlockPrivateRoom = itemView.findViewById(R.id.opened_private_room);
                roomMembers = itemView.findViewById(R.id.room_members);
            }
        }
    }
}