package com.calc.firebaseloginpractice.ui.timeline.posts.postComments;

import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.calc.firebaseloginpractice.R;
import com.calc.firebaseloginpractice.models.commentModel;
import com.calc.firebaseloginpractice.models.postModel;
import com.calc.firebaseloginpractice.models.userModel;
import com.calc.firebaseloginpractice.ui.chats.chatsFragment;
import com.calc.firebaseloginpractice.ui.profile.profileFragment;
import com.calc.firebaseloginpractice.ui.timeline.timeLineFragment;
import com.calc.firebaseloginpractice.ui.welcome.welcomeFragment;
import com.calc.firebaseloginpractice.utils.constants;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class postComments extends Fragment
{

    private View mainView;
    private RecyclerView recyclerView;
    private EditText chatFiled;
    private TextView userNameFiled;
    private TextView userImageFiled;
    private FloatingActionButton sendFab;
    private Toolbar toolbar;

    private String commentName;
    private String commentImage;

    private List<commentModel> commentModels;


    private TextView commentPostText;
    private ImageView commentPostImage;
    private    TextView postLikeCount;
    private    TextView postLike;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView= inflater.inflate(R.layout.comments_post,null);
        return mainView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);

        initViews();
        getComment();

        // to get the data of the user who will leave the comment
        getUserData();
    }


    void setLikesCount( final String idPost, final TextView textView)
    {
        constants.getDatabaseReference().child("Likes").child(idPost).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                long count = dataSnapshot.getChildrenCount();
                if (count>0)
                {
                    textView.setVisibility(View.VISIBLE);
                    textView.setText(count+"likes");
                } else
                {
                    textView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    void isLike(final String postId, final TextView textView)
    {
        constants.getDatabaseReference().child("Likes").child(postId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if (dataSnapshot.hasChild(constants.getUid(requireActivity()))) {
                    // to change the color once when we click like

                    textView.setCompoundDrawableTintList(ContextCompat.getColorStateList(getContext(), R.color.like));
                    //textView.setText("Dislike");
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v)
                        {
                            constants.getDatabaseReference().child("Likes").child(postId).child(constants.getUid(requireActivity())).removeValue();

                        }
                    });
                } else
                {
                    textView.setCompoundDrawableTintList(ContextCompat.getColorStateList(getContext(), R.color.dislike));
                    textView.setText("Like");
                    textView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v)
                        {
                            constants.getDatabaseReference().child("Likes").child(postId).child(constants.getUid(requireActivity())).setValue(true);

                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }



    // get posts and show it on the comment at the top, not related to the writing comment down!
   // private void getPosts()
    {

//        String postId= constants.postModel.getPostId();
//        constants.getDatabaseReference().child("Posts").child(postId).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
//            {
//
//                    postModel model = dataSnapshot.getValue(postModel.class);
//
////                    String originalPostUserName=model.getUsername();
////                    String originalPostUserImage=model.getUserImage();
//
//
//                int type = model.getType();
//
//                 if(type==0)
//                 {
//
//                     String originalPostUserPostText=model.getPostText();
//                     commentPostText.setText(originalPostUserPostText);
//
//                 } else if (type == 1)
//                 {
//                     String originalPostUserPostText=model.getPostText();
//                     commentPostText.setText(originalPostUserPostText);
//                     String originalPostUserPostImage=model.getPostImage();
//
//
//                     Picasso
//                             .get()
//                             .load(originalPostUserPostImage)
//                             .into(commentPostImage);
//                 }
//
//
//

//                }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError)
//            {
//
//            }
//        });
    }

    private void getUserData()
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
                    commentName=model.getName();
                    commentImage=model.getImageUri();

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }




    private void getComment()
    {

        //get  chats/ my id / whoever id we chosed from users /  get the chat
        constants.getDatabaseReference().child("Comments").child(constants.postModel.getPostId())


                .addValueEventListener(new ValueEventListener()
                {

                    @Override
                    // this methods 4 going to the users Node and bring the data from there
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                    {
                        commentModels.clear();

                        for (DataSnapshot d : dataSnapshot.getChildren())
                        {
                            commentModel model= d.getValue(commentModel.class);
                            commentModels.add(model);
                        }

                        recyclerView.setAdapter(new commentAdapter(commentModels));

                        // to scroll the chat to last position
                        recyclerView.scrollToPosition(commentModels.size()-1);

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError)
                    {

                    }
                });
    }

    private void initViews()
    {
        recyclerView=mainView.findViewById(R.id.comment_recycler);
        chatFiled=mainView.findViewById(R.id.comment_body_field);
        userNameFiled=mainView.findViewById(R.id.comment_user_name);
        userImageFiled=mainView.findViewById(R.id.comment_user_image);
        sendFab=mainView.findViewById(R.id.send_comment_fab);
        toolbar=mainView.findViewById(R.id.comments_toolbar);

        commentPostText=mainView.findViewById(R.id.post_text_comment);
        commentPostImage=mainView.findViewById(R.id.post_image_comment);

        postLikeCount=mainView.findViewById(R.id.post_like_comment_countfragment);
        postLike=mainView.findViewById(R.id.post_like_commentframent);

        // this for the getPost, we have already in constants initialised  the post model in this method public static postModel postModel;
        // we have to send the   constants.postModel= model; in on click comment method
        int type = constants.postModel.getType();
        if (type==0)
        {

            commentPostImage.setVisibility(View.GONE);
            // this to get the post text local without getPost method
            commentPostText.setText(constants.postModel.getPostText());

            setLikesCount(constants.postModel.getPostId(), postLikeCount);
            isLike(constants.postModel.getPostId(), postLike);


        } else
        {
            // this to get the post text local without getImage method
            Picasso.get().load(constants.postModel.getPostImage()).into(commentPostImage);
            commentPostText.setText(constants.postModel.getPostText());

            setLikesCount(constants.postModel.getPostId(), postLikeCount);
            isLike(constants.postModel.getPostId(), postLike);
        }

        // commentUsernameImage=itemView.findViewById(R.id.post_user_image);
        
        commentModels= new ArrayList<>();

        sendFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {

                String comment = chatFiled.getText().toString();



                if (!comment.isEmpty())
                {
                    sendComment(comment);
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
                        constants.replaceFragment(postComments.this,new welcomeFragment(),false);
                        break;
                    case R.id.main_settings_btn:
                        constants.replaceFragment(postComments.this,new profileFragment(),true);

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



    }

    private void sendComment(String comment)
    {

        // this is to push key for each comment i want to write at same post, we have to do it
        final String key= constants.getDatabaseReference().child("Comments").child(constants.postModel.getPostId()).push().getKey();
        final commentModel commentModel= new commentModel
                (
                        constants.getUid(requireActivity())
                        ,comment
                        ,commentName
                        ,commentImage
                        ,constants.getTime()
                        ,0
                        ,key
                );

        // ("Chats") it has to be with same name as the one we get the database from on the method     private void getChat()

        if (key!=null)
        {
            constants.getDatabaseReference().child("Comments").child(constants.postModel.getPostId()).child(key).setValue(commentModel);

            chatFiled.setText("");

        }

    }

    private class commentAdapter extends RecyclerView.Adapter<commentAdapter.VH>
    {
        List<commentModel> commentModelList;


        commentAdapter(List<commentModel> commentModelList) {
            this.commentModelList = commentModelList;
        }

        @NonNull
        @Override
        public commentAdapter.VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
        {
            View view = LayoutInflater.from(requireContext()).inflate(R.layout.comment_item,parent, false);
            return new commentAdapter.VH(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final commentAdapter.VH holder, int position)
        {
            final commentModel model= commentModelList.get(position);

            String comment= model.getMessage();
            String userName= model.getUserName();
            String userImage=model.getUserImage();

            Picasso
                    .get()
                    .load(userImage)
                    .into(holder.commentUserImage);


            long time= model.getTime();
            long now = System.currentTimeMillis();
            CharSequence ago =   DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS);
            holder.time.setText(ago);

            holder.comment.setText(comment);
            holder.commentUserName.setText(userName);


            }


        @Override
        public int getItemCount() {
            return commentModelList.size();
        }



        void isLike(final String postId, final TextView textView)
        {
            constants.getDatabaseReference().child("Likes").child(postId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                {
                    if (dataSnapshot.hasChild(constants.getUid(requireActivity())))
                    {
                        if (textView!=null)
                        {
                            // to change the color once when we click like
                            textView.setCompoundDrawableTintList(ContextCompat.getColorStateList(getContext(), R.color.like));
                            textView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v)
                                {
                                    constants.getDatabaseReference().child("Likes").child(postId).child(constants.getUid(requireActivity())).removeValue();

                                }
                            });
                        }

                    } else
                    {
                        textView.setCompoundDrawableTintList(ContextCompat.getColorStateList(getContext(), R.color.dislike));
                        textView.setText("Like");
                        textView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v)
                            {
                                constants.getDatabaseReference().child("Likes").child(postId).child(constants.getUid(requireActivity())).setValue(true);

                            }
                        });
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        private class VH extends RecyclerView.ViewHolder
        {


            private TextView comment;
            private   TextView time;
            private TextView commentUserName;
            private ImageView commentUserImage;



            VH(@NonNull View itemView) {
                super(itemView);


                comment=itemView.findViewById(R.id.comment_message_text);
                time=itemView.findViewById(R.id.comment_time);
                commentUserImage=itemView.findViewById(R.id.comment_user_image);
                commentUserName=itemView.findViewById(R.id.comment_user_name);




            }

        }


    }
}
