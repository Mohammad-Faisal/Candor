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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    //widgets
    private EditText mDisplayText;
    private EditText mEmail;
    private EditText mPassword;
    private Button mRegButton;

    //progress dialog
    private ProgressDialog mProgress;
    //toolbar
    Toolbar mToolbar;


    //firebase
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseReference;

    //variables
    String displayName;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        //widget initialize
        mDisplayText = findViewById(R.id.name_text);
        mEmail = findViewById(R.id.email_text_login);
        mPassword = findViewById(R.id.password_text_login);
        mRegButton = findViewById(R.id.login_btn);

        //adding toolbar
        mToolbar = findViewById(R.id.register_toolbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Create An Account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //setting progressbar
        mProgress = new ProgressDialog(this);


        //firebase initialize
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference().child("users");

        //variables initialization
        displayName = "user";

        mRegButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displayName = mDisplayText.getText().toString();
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();

                if( TextUtils.isEmpty(displayName) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password))
                {
                    Toast.makeText(RegisterActivity.this, "you must be missing something bro !", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    mProgress.setTitle("Registering user");
                    mProgress.setMessage("Please wait while we create your account !");
                    mProgress.setCanceledOnTouchOutside(false);
                    mProgress.show();
                    register_user(displayName , email , password);
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    private void register_user(String displayName , String email , String password){

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            mProgress.dismiss();
                            updateUI(user);

                        } else {
                            // If sign in fails, display a message to the user.
                            mProgress.hide();
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if(user==null)
        {
            Toast.makeText(RegisterActivity.this, "Authentication failed may be invalid mail .... try again",
                    Toast.LENGTH_SHORT).show();
        }
        else
        {



            //i want to store some data before moving on to next page
            String mUserID = user.getUid();
            mDatabaseReference = mDatabase.getReference().child("users").child(mUserID);
            String deviceTokenID = FirebaseInstanceId.getInstance().getToken();

            HashMap<String  , String> hashMap = new HashMap<>();
            hashMap.put("name"  , displayName);
            hashMap.put("status" , "hi i am using this awesome app !!");
            hashMap.put("image" , "default");
            hashMap.put("thumb_image" , "default");
            hashMap.put("device_id" , deviceTokenID);


            mDatabaseReference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Intent mainIntent  = new Intent(RegisterActivity.this , MainActivity.class); //go to main activity and finish this one

                        //ei line ta na dile main page theke back dile abar start e chole jabe tai amra sob clear kore dicchi
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK  | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(mainIntent);
                        finish();
                    }
                    else{
                        Toast.makeText(RegisterActivity.this, "Please check your internet connection !", Toast.LENGTH_SHORT).show();
                    }
                }
            });



            Intent mainIntent  = new Intent(RegisterActivity.this , MainActivity.class); //go to main activity and finish this one

            //ei line ta na dile main page theke back dile abar start e chole jabe tai amra sob clear kore dicchi
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK  | Intent.FLAG_ACTIVITY_NEW_TASK);

            startActivity(mainIntent);
            finish();
        }
    }
}
