package bao.bon.hshopblog.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import bao.bon.hshopblog.Activitys.PostDetailActivity;
import bao.bon.hshopblog.Models.Post;
import bao.bon.hshopblog.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {

    Context mContext;
    List<Post> mData;


    public PostAdapter(Context mContext, List<Post> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View row = LayoutInflater.from(mContext).inflate(R.layout.row_post_item,viewGroup,false);
        return new MyViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, int i) {

        myViewHolder.txtTittle.setText(mData.get(i).getTittle());
        Glide.with(mContext).load(mData.get(i).getPicture()).into(myViewHolder.imgPost);
        Glide.with(mContext).load(mData.get(i).getUserPhoto()).into(myViewHolder.imgPostProfile);

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView txtTittle;
        ImageView imgPost;
        CircleImageView imgPostProfile;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTittle = itemView.findViewById(R.id.txt_rowpost);
            imgPost = itemView.findViewById(R.id.image_rowpost);
            imgPostProfile = itemView.findViewById(R.id.imgprofile_rowpost);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent postDetailActivity = new Intent(mContext, PostDetailActivity.class);
                    int position = getAdapterPosition();

                    postDetailActivity.putExtra("title",mData.get(position).getTittle());
                    postDetailActivity.putExtra("postImage",mData.get(position).getPicture());
                    postDetailActivity.putExtra("description",mData.get(position).getDescription());
                    postDetailActivity.putExtra("postKey",mData.get(position).getPostKey());
                    postDetailActivity.putExtra("userPhoto",mData.get(position).getUserPhoto());
//                    postDetailActivity.putExtra("userName",mData.get(position).getUserName());
                    long timestamp = (long) mData.get(position).getTimeStamp();
                    postDetailActivity.putExtra("postDate",timestamp);
                    mContext.startActivity(postDetailActivity);




                }
            });


        }
    }
}
