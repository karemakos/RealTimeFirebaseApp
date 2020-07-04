package com.calc.firebaseloginpractice.ui.timeline;

import android.graphics.Color;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.contentcapture.DataRemovalRequest;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.calc.firebaseloginpractice.R;
import com.calc.firebaseloginpractice.models.chatModel;
import com.calc.firebaseloginpractice.models.postModel;
import com.calc.firebaseloginpractice.models.userModel;
import com.calc.firebaseloginpractice.ui.chats.chatsFragment;
import com.calc.firebaseloginpractice.ui.profile.fragmentUsersProfile;
import com.calc.firebaseloginpractice.ui.profile.profileFragment;
import com.calc.firebaseloginpractice.ui.timeline.posts.newPostFragment;
import com.calc.firebaseloginpractice.ui.timeline.posts.postComments.postComments;
import com.calc.firebaseloginpractice.utils.constants;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class timeLineFragment extends Fragment
{
    private View mainView;
    private RecyclerView recyclerView;
    private FloatingActionButton addPost;
    private TextView addComments;
    private List<postModel> postModels;
    private TextView nameFiled;
    private TextView emailFiled;
    private TextView mobileFiled;
    private TextView addressFiled;
    private ImageView userImage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainView= inflater.inflate(R.layout.fragment_timeline,null);
        return mainView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        constants.initProgress(getContext(),"Please wait...");
        initViews();
        getPosts();

    }

    private void getPosts()
    {

        constants.showProgress();
        constants.getDatabaseReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                // we have to clear the post cuz if we have 4 posts and we wanna add one then it will become 5 not 9
                postModels.clear();

                // this to get all the posts to show on the timeline, that's why we put for each loop cuz its not only one post will show it's a punch of posts
                for (DataSnapshot d : dataSnapshot.getChildren())
                {
                    postModel model= d.getValue(postModel.class);

                    postModels.add(model);
                }

                recyclerView.setAdapter(new postsAdapter(postModels));

                // we have to put if condition to dont give error if there are no posts

                if (postModels.size()!=0)
                {
                    recyclerView.smoothScrollToPosition(postModels.size()-1);
                }


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
        recyclerView=mainView.findViewById(R.id.posts_recycler);
        addPost=mainView.findViewById(R.id.add_post_fab);
        addComments=mainView.findViewById(R.id.timeline_add_comment);

        postModels= new ArrayList<>();
        addPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                constants.replaceFragment(timeLineFragment.this, new newPostFragment(),true);
            }
        });

    }


    private class postsAdapter extends RecyclerView.Adapter<postsAdapter.VH>
    {
        List<postModel> postModelList;

        postsAdapter(List<postModel> postModelList) {
            this.postModelList = postModelList;
        }

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(requireContext()).inflate(R.layout.item_post, parent, false);
            return new VH(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final VH holder, final int position)
        {
             final postModel model= postModelList.get(position);

            // to say if this post has image or no, we have to put this int type
            int type = model.getType();
            // if post without photo in the post to share
            if (type == 0)
            {
                holder.post_image.setVisibility(View.GONE);

                String name = model.getUsername();
                final long time = model.getPostTime();
                final String userImage = model.getUserImage();
                String text = model.getPostText();



                holder.postUserName.setText(name);
                holder.postUserName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v)
                    {
                        constants.postModel= model;
                        constants.replaceFragment(timeLineFragment.this,new profileFragment(),true);
                    }
                });

                long now = System.currentTimeMillis();

                CharSequence ago =   DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS);

                holder.postTime.setText(String.valueOf(ago));
                holder.postText.setText(text);

                Picasso
                        .get()
                        .load(userImage)
                        .into(holder.post_user_image);

                setLikesCount(model.getPostId(),holder.postLikeCount);
                isLike(model.getPostId(), holder.postLike);

                holder.postComment.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        constants.postModel= model;
                        constants.replaceFragment(timeLineFragment.this,new postComments(),true);
                    }
                });


            } else if (type == 1)
            {
                String name = model.getUsername();
                final long time = model.getPostTime();
                String userImage = model.getUserImage();
                String text = model.getPostText();
                String image = model.getPostImage();




                long now = System.currentTimeMillis();

               holder.postUserName.setText(name);
                CharSequence ago =   DateUtils.getRelativeTimeSpanString(time, now, DateUtils.MINUTE_IN_MILLIS);


                holder.postTime.setText(String.valueOf(ago));
                holder.postText.setText(text);

                Picasso
                        .get()
                        .load(userImage)
                        .into(holder.post_user_image);

                Picasso
                        .get()
                        .load(image)
                        .into(holder.post_image);


                setLikesCount(model.getPostId(),holder.postLikeCount);

                // we call this method to change the color once when we click on it
                isLike(model.getPostId(), holder.postLike);

            }
            holder.linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    constants.replaceFragment(timeLineFragment.this,new fragmentUsersProfile(model.getuId()),true);
                }
            });

            holder.postComment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    constants.postModel= model;
                    constants.replaceFragment(timeLineFragment.this,new postComments(),true);
                }
            });

            holder.postUserName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {

                    String uId= constants.myChats.getUid();
                    constants.replaceFragment(timeLineFragment.this,new profileFragment(),true);


                }
            });
        }

        @Override
        public int getItemCount() {
            return postModelList.size();

        }

        // we create this method for the likes
//     void setLike (String postId)
//    {
//        // So, this will create on firebase file called Likes and down to it, the PostId and down to the postId myID+whatever id has done like
//       //   likesModel model= new likesModel(constants.getUid(requireActivity()));
//
//            constants.getDatabaseReference().child("Likes").child(postId).child(constants.getUid(requireActivity())).setValue(true);
//
//    }

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
                      if (textView!=null)
                      {
                          // to change the color once when we click like
                          textView.setCompoundDrawableTintList(ContextCompat.getColorStateList(requireActivity(),R.color.like));
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
           private   CircleImageView post_user_image;
           LinearLayout linearLayout;
            private  ImageView post_image;
            private    TextView postUserName;
            private   TextView postTime;
            private    TextView postText;
            private    TextView postLikeCount;
            private     TextView postLike;
            private    TextView postComment;
            private   TextView postShare;
            boolean isTextViewClicked = false;



            VH(@NonNull View itemView) {
                super(itemView);

                post_user_image=itemView.findViewById(R.id.post_user_image);
                linearLayout=itemView.findViewById(R.id.user_info_linear);
                post_image=itemView.findViewById(R.id.post_image);
                postUserName=itemView.findViewById(R.id.post_user_name);
                postTime=itemView.findViewById(R.id.post_time);
                postText=itemView.findViewById(R.id.post_text);
                postLikeCount=itemView.findViewById(R.id.post_likeCount);
                postLike=itemView.findViewById(R.id.post_like);
                postComment=itemView.findViewById(R.id.timeline_add_comment);
                postShare=itemView.findViewById(R.id.post_share);



                // to expand the text view
                postText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (isTextViewClicked)
                        {
                            postText.setMaxLines(2);
                            isTextViewClicked=false;
                        } else
                        {
                            postText.setMaxLines(Integer.MAX_VALUE);
                            isTextViewClicked=true;
                        }
                    }
                });
            }
        }
    }
}
