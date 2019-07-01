package bao.bon.hshopblog.Activitys;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.rengwuxian.materialedittext.MaterialEditText;

import bao.bon.hshopblog.R;

public class  LoginActivity extends AppCompatActivity {

    private MaterialEditText edtEmail,edtPassWord;
    private Button btnLogin;
    private ProgressBar loginProgessBar;

    private FirebaseAuth mAuth;
    private Intent HomeAcitivty;
    TextView txtRegister;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        edtEmail = findViewById(R.id.edt_email);
        edtPassWord  = findViewById(R.id.edt_password);
        btnLogin = findViewById(R.id.btnLogin);
        loginProgessBar = findViewById(R.id.loginprogressBar);
        loginProgessBar.setVisibility(View.VISIBLE);
        txtRegister = findViewById(R.id.txt_Register);

        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(registerIntent);
                finish();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        HomeAcitivty =  new Intent(this, bao.bon.hshopblog.Activitys.HomeActivity.class);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginProgessBar.setVisibility(View.VISIBLE);
                btnLogin.setVisibility(View.INVISIBLE);
                final String email = edtEmail.getText().toString();
                final String password = edtPassWord.getText().toString();
                if(email.isEmpty() || password.isEmpty()){
                    showMessage("Please Verify All Field");
                    btnLogin.setVisibility(View.VISIBLE);
                }else{
                    signIn(email,password);
                }
            }
        });


    }

    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email,password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            loginProgessBar.setVisibility(View.INVISIBLE);
                            btnLogin.setVisibility(View.VISIBLE);
                            updateUI();
                        }else{
                            showMessage(task.getException().getMessage());
                            btnLogin.setVisibility(View.VISIBLE);
                        }
                    }
                });
    }

    private void updateUI() {

        startActivity(HomeAcitivty);
        finish();

    }

    private void showMessage(String please_verify_all_field) {
        Toast.makeText(this, please_verify_all_field, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if(user != null){
            updateUI();
        }
    }
}
