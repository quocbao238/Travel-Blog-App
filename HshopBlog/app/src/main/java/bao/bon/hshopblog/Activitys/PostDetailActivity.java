package bao.bon.hshopblog.Activitys;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import bao.bon.hshopblog.Adapters.CommentAdapter;
import bao.bon.hshopblog.Models.Comment;
import bao.bon.hshopblog.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class PostDetailActivity extends AppCompatActivity {

    ImageView imgPost,imgUserPost;
    CircleImageView imgCurrentUser;
    TextView txtPostDecs,txtPostDateName,txtPostTitle;
    EditText edtComment;
    ImageView btnaddComment;
    String PostKey;

    FirebaseAuth mAuth;
    FirebaseUser firebaseUser;
    FirebaseDatabase firebaseDatabase;
    RecyclerView Rv_comment;
    CommentAdapter commentAdapter;

    List<Comment> listComment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        Window w = getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
//
//        getSupportActionBar().hide();


        //Ini View
        Rv_comment = findViewById(R.id.rv_comment);
        imgPost = findViewById(R.id.post_detail_img);
        imgUserPost = findViewById(R.id.post_detail_imgUser);
        imgCurrentUser = findViewById(R.id.post_detail_currentuser);
        txtPostDateName = findViewById(R.id.post_detail_dateUpdate);
        txtPostTitle = findViewById(R.id.post_detail_tittle);
        edtComment = findViewById(R.id.post_detail_commant);
        btnaddComment = findViewById(R.id.post_detail_addcomment);
        txtPostDecs = findViewById(R.id.post_detail_descrip);


        // Get Data

        String postImage = getIntent().getExtras().getString("postImage");
        Glide.with(this).load(postImage).into(imgPost);

        String postTitle = getIntent().getExtras().getString("title");
        txtPostTitle.setText(postTitle);

        String userPostImage = getIntent().getExtras().getString("userPhoto");
        Glide.with(this).load(userPostImage).into(imgUserPost);

        String postDescreption = getIntent().getExtras().getString("description");
        txtPostDecs.setText(postDescreption);

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = mAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();

        //add Comment Button Click
        btnaddComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatabaseReference commentReference = firebaseDatabase.getReference("Comment").
                        child(PostKey).push();
                String comment_content = edtComment.getText().toString();
                String uid = firebaseUser.getUid();
                String uname = firebaseUser.getDisplayName();
                String uimg = firebaseUser.getPhotoUrl().toString();
                Comment comment = new Comment(comment_content,uid,uimg,uname);

                commentReference.setValue(comment).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        showMessage("comment added");
                        edtComment.setText("");
                        btnaddComment.setVisibility(View.VISIBLE);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showMessage("fail to add comment"+e.getMessage());
                    }
                });

            }
        });


        //Set comment user image;
        Glide.with(this).load(firebaseUser.getPhotoUrl()).into(imgCurrentUser);

        PostKey = getIntent().getExtras().getString("postKey");
        String date = timestamptoString(getIntent().getExtras().getLong("postDate"));
        txtPostDateName.setText(date);

        //Ini Recycle view
        iniRvComment();

    }

    private void iniRvComment() {

        Rv_comment.setLayoutManager(new LinearLayoutManager(this));

        DatabaseReference commentRef = firebaseDatabase.getReference("Comment").child(PostKey);
        commentRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listComment = new ArrayList<>();
                for(DataSnapshot snap: dataSnapshot.getChildren()){
                    Comment comment = snap.getValue(Comment.class);
                    listComment.add(comment);
                }

                commentAdapter = new CommentAdapter(getApplication(),listComment);
                Rv_comment.setAdapter(commentAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showMessage(String txt) {
        Toast.makeText(this, txt, Toast.LENGTH_SHORT).show();
    }

    private String timestamptoString(long time){
        Calendar calendar = Calendar.getInstance(Locale.ENGLISH);
        calendar.setTimeInMillis(time);
        String date = DateFormat.format("dd-MM-yyyy",calendar).toString();
        return date;
    }
}
