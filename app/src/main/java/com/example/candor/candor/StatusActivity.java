package com.example.candor.candor;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {


    private android.support.v7.widget.Toolbar mToolbar;


    //widgets
    TextInputLayout text_input ;
    Button save_status;
    private ProgressDialog mProgress;

    //variables
    String mUserID;
    private String status;

    private FirebaseDatabase mDatabase;
    private DatabaseReference mDatabaseReference;
    private FirebaseUser mUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        status = bundle.getString("current_status");

        //setting the toolbar
        mToolbar = findViewById(R.id.status_app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //firebase
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mUserID = mUser.getUid();
        mDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mDatabase.getReference().child("users").child(mUserID);

        //widdgets
        text_input = findViewById(R.id.status_text_input);
        save_status =findViewById(R.id.status_save_button);

        text_input.getEditText().setText(status);



        save_status.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //showing the progress dialog
                mProgress = new ProgressDialog(StatusActivity.this);
                mProgress.setTitle("Updating Status...");
                mProgress.setMessage("please wait while we update your status");
                mProgress.show();


                status = text_input.getEditText().getText().toString();
                mDatabaseReference.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful()){
                            mProgress.dismiss();
                            finish();
                        }
                        else {
                            mProgress.hide();
                            Toast.makeText(StatusActivity.this, "there was some error !", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });




    }


}
