package com.example.candor.candor.initialize;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.candor.candor.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private Toolbar mToolbar;

    EditText mEmail;
    EditText mPassword;
    Button mLogin;

    //firebase
    private FirebaseAuth mAuth;

    //progress dialog
    private ProgressDialog mProgress;

    //firebase
    private DatabaseReference mDatabaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //adding toolbar
        mToolbar = findViewById(R.id.login_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Login");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //getting widgets
        mEmail = findViewById(R.id.email_text_login);
        mPassword = findViewById(R.id.password_text_login);
        mLogin = findViewById(R.id.login_btn);

        //setting progressbar
        mProgress = new ProgressDialog(this);

        //firebase initialize
        mAuth = FirebaseAuth.getInstance();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();


        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();

                if( TextUtils.isEmpty(email)  ||TextUtils.isEmpty(password))
                {
                    Toast.makeText(LoginActivity.this, "you must be missing something bro !", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    mProgress.setTitle("Loggin in");
                    mProgress.setMessage("Please wait while we Authenticate your account !");
                    mProgress.setCanceledOnTouchOutside(false);
                    mProgress.show();
                    LoginUser( email , password);
                }
            }
        });



    }

    private void LoginUser(String email , String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            //now we need our current device id to send notificaiton
                            String mUserID = mAuth.getCurrentUser().getUid();
                            String deviceTokenID = FirebaseInstanceId.getInstance().getToken();
                            mDatabaseReference.child("users").child(mUserID).child("device_id").setValue(deviceTokenID).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Log.d(TAG, "signInWithEmail:success");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    mProgress.dismiss();
                                    updateUI(user);
                                }
                            });
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            mProgress.hide();
                            updateUI(null);
                        }
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    private void  updateUI(FirebaseUser mUser){
        if(mUser==null)
        {
            Toast.makeText(LoginActivity.this, "Authentication failed may be invalid email .... try again",
                    Toast.LENGTH_SHORT).show();
        }
        else
        {
            Intent mainIntent  = new Intent(LoginActivity.this , MainActivity.class);

            //ei line ta na dile main page theke back dile abar start e chole jabe tai amra sob clear kore dicchi
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK  | Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(mainIntent);
            finish();
        }
    }
}
