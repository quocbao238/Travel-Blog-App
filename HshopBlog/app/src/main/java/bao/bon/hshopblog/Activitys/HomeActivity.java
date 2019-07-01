package bao.bon.hshopblog.Activitys;

import android.Manifest;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.support.v4.view.GravityCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import bao.bon.hshopblog.Fragments.HomeFragment;
import bao.bon.hshopblog.Fragments.ProfileFragment;
import bao.bon.hshopblog.Fragments.SettingFragment;
import bao.bon.hshopblog.Models.Post;
import bao.bon.hshopblog.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FirebaseUser currentUser;
    FirebaseAuth mAuth;
    Dialog popAddPost;
    ImageView popUpPosImage, popUpaddBtn;
    CircleImageView popUpUserImage;
    EditText popUpTitle, popUpDescription;
    ProgressBar popUpClickProgress;
    static int REQUESTCODE = 2;
    static int RqueCode = 2;
    private Uri pickedImageUri = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        getSupportActionBar().setTitle("Home");


        //Innit popup
        innitPopup();
        //Setup popup
        setupPopupImageClick();


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                popAddPost.show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        UpdateNavHeader();

        //set the home fragment as the deafaulone

        getSupportFragmentManager().beginTransaction().replace(R.id.container,new HomeFragment()).commit();
    }

    private void setupPopupImageClick() {
        popUpPosImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAndRequestForPermission();

            }
        });
    }

    private void checkAndRequestForPermission() {

        if (ContextCompat.checkSelfPermission(HomeActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(HomeActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "Please accept for required permission", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(HomeActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, RqueCode);
            }
        } else {
            openGallery();
        }

    }

    private void openGallery() {
        // TODO: open gallery intent and for user to  pick an image !
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUESTCODE);
    }

    private void innitPopup() {
        popAddPost = new Dialog(this);
        popAddPost.setContentView(R.layout.popup_add_post);
        popAddPost.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        popAddPost.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.WRAP_CONTENT);
        popAddPost.getWindow().getAttributes().gravity = Gravity.TOP;


        popUpUserImage = popAddPost.findViewById(R.id.popup_userImage);
        popUpaddBtn = popAddPost.findViewById(R.id.popup_add);
        popUpPosImage = popAddPost.findViewById(R.id.popup_img);
        popUpTitle = popAddPost.findViewById(R.id.popuo_tittle);
        popUpDescription = popAddPost.findViewById(R.id.popup_descreption);
        popUpClickProgress = popAddPost.findViewById(R.id.popup_addrogressBar);

        //Add post click listener

        popUpaddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUpClickProgress.setVisibility(View.VISIBLE);
                popUpaddBtn.setVisibility(View.INVISIBLE);

                //check all is !null

                if (!popUpTitle.getText().toString().isEmpty() && !popUpDescription.getText().toString().isEmpty() && pickedImageUri != null) {

                    //TODO Creat Post Object and add it to Firebase database
                    //Update Image to Storage
                    StorageReference storageReference = FirebaseStorage
                            .getInstance().getReference().child("blog_image");
                    final StorageReference imageFilePath = storageReference.child(pickedImageUri.getLastPathSegment());
                    imageFilePath.putFile(pickedImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imageFilePath.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            String imageDowloadLink = uri.toString();
                                            Post post = new Post(popUpTitle.getText().toString(),
                                                    popUpDescription.getText().toString(),
                                                    imageDowloadLink,
                                                    currentUser.getUid(),
                                                    currentUser.getPhotoUrl().toString());
                                            //Add post to Firebase Database;

                                            addPost(post);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    showMessenge(e.getMessage());
                                    popUpClickProgress.setVisibility(View.INVISIBLE);
                                    popUpaddBtn.setVisibility(View.VISIBLE);
                                }
                            });
                        }
                    });

                } else {
                    showMessenge("Please verify all input field and choose Post Image");
                    popUpClickProgress.setVisibility(View.INVISIBLE);
                    popUpaddBtn.setVisibility(View.VISIBLE);
                }
            }
        });

        //load Cureent user Profile photo

        Glide.with(HomeActivity.this).load(currentUser.getPhotoUrl()).into(popUpUserImage);


    }

    private void addPost(Post post) {

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("Post").push();
        String key = myRef.getKey();
        post.setPostKey(key);
        //add post data to Firebase

        myRef.setValue(post).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                showMessenge("Post add Sucessfully");
                popUpClickProgress.setVisibility(View.INVISIBLE);
                popUpaddBtn.setVisibility(View.VISIBLE);
                popAddPost.dismiss();
            }
        });


    }

    private void showMessenge(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            getSupportActionBar().setTitle("Home");
            getSupportFragmentManager().beginTransaction().replace(R.id.container, new HomeFragment()).commit();
        } else if (id == R.id.nav_profile) {
            getSupportActionBar().setTitle("Profile");
            getSupportFragmentManager().beginTransaction().replace(R.id.container, new ProfileFragment()).commit();
        } else if (id == R.id.nav_setting) {
            getSupportActionBar().setTitle("Setting");
            getSupportFragmentManager().beginTransaction().replace(R.id.container, new SettingFragment()).commit();
        } else if (id == R.id.nav_signout) {
            FirebaseAuth.getInstance().signOut();
            Intent loginActivity = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(loginActivity);
            finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void UpdateNavHeader() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        View hedearView = navigationView.getHeaderView(0);
        TextView navUserName = hedearView.findViewById(R.id.textViewNarName);
        TextView navUserEmail = hedearView.findViewById(R.id.textViewNarEmail);
        ImageView navUserPhoto = hedearView.findViewById(R.id.imgNarUser);
        navUserEmail.setText(currentUser.getEmail());
        navUserName.setText(currentUser.getDisplayName());

        Glide.with(this).load(currentUser.getPhotoUrl()).into(navUserPhoto);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUESTCODE && data != null) {
            //When user has succesfully picked an image ->  Save image to Uri
            pickedImageUri = data.getData();
            popUpPosImage.setImageURI(pickedImageUri);
        }
    }
}
