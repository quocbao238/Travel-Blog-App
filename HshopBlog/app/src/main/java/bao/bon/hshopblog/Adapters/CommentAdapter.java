package bao.bon.hshopblog.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import bao.bon.hshopblog.Models.Comment;
import bao.bon.hshopblog.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private Context mContext;
    private List<Comment> mData;

    public CommentAdapter(Context mContext, List<Comment> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View row = LayoutInflater.from(mContext).inflate(R.layout.row_comment,viewGroup,false);
        return new CommentViewHolder(row);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder commentViewHolder, int i) {

        Glide.with(mContext).load(mData.get(i).getUimg()).into(commentViewHolder.img_user);

        commentViewHolder.tv_name.setText(mData.get(i).getUname());
        commentViewHolder.tv_content.setText(mData.get(i).getContent());
//       add Date later
        commentViewHolder.tv_date.setText(timestamptoString((Long) mData.get(i).getTimestamp()));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public class CommentViewHolder extends RecyclerView.ViewHolder{

        CircleImageView img_user;
        TextView tv_name,tv_content,tv_date;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            img_user = itemView.findViewById(R.id.comment_userImg);
            tv_content = itemView.findViewById(R.id.content_comment);
            tv_name = itemView.findViewById(R.id.comment_username);
            tv_date = itemView.findViewById(R.id.content_date);
        }
    }

    private String timestamptoString(long time){
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("hh:mm",calendar).toString();
        return date;
    }
}
