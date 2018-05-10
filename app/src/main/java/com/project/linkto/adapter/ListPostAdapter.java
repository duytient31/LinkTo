package com.project.linkto.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.project.linkto.R;
import com.project.linkto.bean.Like;
import com.project.linkto.bean.Post;
import com.project.linkto.singleton.DataFilter;
import com.project.linkto.singleton.DataHelper;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.project.linkto.Fragment.BaseFragment.mActivity;
import static com.project.linkto.Fragment.HomeFragment.mDatabase;

/**
 * Created by bbouzaiene on 20/04/2018.
 */

public class ListPostAdapter extends RecyclerView.Adapter<ListPostAdapter.MyViewHolder> {


    private final List<Post> postList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView tv_likes;
        public TextView tv_title, tv_body, tv_date;

        public MyViewHolder(View view) {
            super(view);
            tv_title = (TextView) view.findViewById(R.id.tv_title);
            tv_body = (TextView) view.findViewById(R.id.tv_body);
            tv_date = (TextView) view.findViewById(R.id.tv_date);
            tv_likes = (TextView) view.findViewById(R.id.tv_likes);
        }
    }

    public ListPostAdapter(List<Post> postList) {
        this.postList = postList;
    }

    @Override
    public ListPostAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.post_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ListPostAdapter.MyViewHolder holder, int position) {
        try {
            final Post post = postList.get(position);
            holder.tv_title.setText(post.getTitle());
            holder.tv_body.setText(post.getBody());/*
            Date birthDate=new Date(post.getTimestamp());
            SimpleDateFormat dateFormat=new SimpleDateFormat("YYYY MMM DD");*/
            holder.tv_date.setText("" + post.getTimestamp());
            final String userId = DataHelper.getInstance().getmUserbd().getUid();
            holder.tv_likes.setText("" + post.getStarCount() + " likes");
            if(DataFilter.getInstance().liked(post,userId)){
                holder.tv_likes.setTextColor(mActivity.getResources().getColor(R.color.colorAccent));
                holder.tv_likes.setEnabled(false);
            }
            else{
                holder.tv_likes.setTextColor(mActivity.getResources().getColor(R.color.colorHint));
                holder.tv_likes.setEnabled(true);
            }

            holder.tv_likes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String key = mDatabase.child("posts").child(post.getKey()).push().getKey();
                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                    Like like = new Like(userId, post.getKey(), timestamp.toString());
                    Map<String, Object> childLikes = new HashMap<>();
                    childLikes.put("/likes/" + key, like.toMap());


                    mDatabase.child("posts").child(post.getKey()).updateChildren(childLikes);
                    mDatabase.child("posts").child(post.getKey()).child("starCount").setValue(post.getStarCount() + 1);
                    notifyDataSetChanged();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        try {
            return postList.size();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
