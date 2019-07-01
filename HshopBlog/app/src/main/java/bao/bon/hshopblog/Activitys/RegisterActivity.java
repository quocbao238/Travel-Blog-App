package bao.bon.hshopblog.Activitys;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.sql.Ref;

import bao.bon.hshopblog.R;
import de.hdodenhof.circleimageview.CircleImageView;

public class RegisterActivity extends AppCompatActivity {

    CircleImageView imgnewUser;
    static int REQUESTCODE = 1;
    static int RqueCode = 1;
    String email, name, password, password2;


    MaterialEditText edtEmail, edtPassword, edtPassword2, edtName;
    ProgressBar loadingProgressbar;
    Button btnRegister;
    Uri pickedImageUri = null;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        imgnewUser = findViewById(R.id.imageNewUser);
        edtEmail = findViewById(R.id.edt_newEmail);
        edtPassword = findViewById(R.id.edt_newpassword);
        edtPassword2 = findViewById(R.id.edt_newconfpassword);
        edtName = findViewById(R.id.edt_newName);
        loadingProgressbar = findViewById(R.id.regprogressBar);
        btnRegister = findViewById(R.id.btnRegister);
        loadingProgressbar.setVisibility(View.INVISIBLE);

        mAuth = FirebaseAuth.getInstance();


        imgnewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (Build.VERSION.SDK_INT >= 22) {
                    checkAndRequestForPermission();
                } else {
                    openGallery();
                }

            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                btnRegister.setVisibility(View.INVISIBLE);
                loadingProgressbar.setVisibility(View.VISIBLE);
                email = edtEmail.getText().toString();
                password = edtPassword.getText().toString();
                password2 = edtPassword2.getText().toString();
                name = edtName.getText().toString();

                if (email.isEmpty() || name.isEmpty() || password.isEmpty() || !password.equals(password2) || pickedImageUri == null) {
                    btnRegister.setVisibility(View.VISIBLE);
                    loadingProgressbar.setVisibility(View.INVISIBLE);
                    showMessenger("Please Verify all fields");
                } else {
                    CreatUserAccount(email, name, password);
                }
            }
        });


    }

    private void showMessenger(String show) {
        Toast.makeText(this, show, Toast.LENGTH_SHORT).show();
    }

    private void CreatUserAccount(String email, final String name, String password) {

        showMessenger("Runing Creat User Acount");

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            showMessenger("Account Created");
                            updateUserInfo(name, pickedImageUri, mAuth.getCurrentUser());
                        } else {
                            showMessenger("Account Faild" + task.getException().getMessage());
                            btnRegister.setVisibility(View.VISIBLE);
                            loadingProgressbar.setVisibility(View.INVISIBLE);
                        }
                    }
                });


    }

    private void updateUserInfo(final String name, Uri pickedImageUri, final FirebaseUser currentUser) {

        StorageReference mStorage = FirebaseStorage.getInstance().getReference().child("user_photos");
        final StorageReference imageFilePath = mStorage.child(pickedImageUri.getLastPathSegment());
        imageFilePath.putFile(pickedImageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        //Image sucessfuly

                        imageFilePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                UserProfileChangeRequest profileChangeUpdate = new UserProfileChangeRequest.Builder()
                                        .setDisplayName(name)
                                        .setPhotoUri(uri)
                                        .build();

                                currentUser.updateProfile(profileChangeUpdate)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                if (task.isSuccessful()) {
                                                    showMessenger("Register Complete");
                                                    updateUI();
                                                }
                                            }
                                        });
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        showMessenger("Fail to Update Image");
                    }
                });


    }

    private void updateUI() {
        Intent homeIntent = new Intent(getApplicationContext(), HomeActivity.class);
        startActivity(homeIntent);
        finish();
    }

    private void openGallery() {
        // TODO: open gallery intent and for user to  pick an image !
        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, REQUESTCODE);
    }

    private void checkAndRequestForPermission() {

        if (ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(RegisterActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Toast.makeText(this, "Please accept for required permission", Toast.LENGTH_SHORT).show();
            } else {
                ActivityCompat.requestPermissions(RegisterActivity.this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, RqueCode);
            }
        } else {
            openGallery();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUESTCODE && data != null) {
            //When user has succesfully picked an image ->  Save image to Uri
            pickedImageUri = data.getData();
            imgnewUser.setImageURI(pickedImageUri);
        }
    }
}
