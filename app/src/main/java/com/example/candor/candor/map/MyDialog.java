package com.example.candor.candor.map;

/**
 * Created by abrar on 11/17/17.
 */

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Toast;

import com.example.candor.candor.R;


public class MyDialog extends Dialog implements View.OnClickListener

{

    public Activity activity;
    public int ifchecked=0;
    public String waterinfo="";

    private RadioButton gorali,hatu,komor,none;

    public MyDialog() {
        super(null);
    }
    public MyDialog(Activity activity) {
        super(activity);
        this.activity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_dialog);
        Button ok=(Button)findViewById(R.id.dialog_ok);

        gorali=(RadioButton) findViewById(R.id.r1);
        hatu=(RadioButton) findViewById(R.id.r2);
        komor=(RadioButton) findViewById(R.id.r3);
        none=(RadioButton) findViewById(R.id.r4);

        checking();

       /* RadioGroup radioGroup = (RadioGroup)findViewById(R.id.radio);


        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
                if(checkedId == R.id.r1) {
                    Toast.makeText(activity.getApplicationContext(), "choice: A",
                            Toast.LENGTH_SHORT).show();
                    ifchecked=1;
                } else if(checkedId == R.id.r2) {
                    Toast.makeText(activity.getApplicationContext(), "choice: B",
                            Toast.LENGTH_SHORT).show();
                    ifchecked=2;
                } else if(checkedId == R.id.r3){
                    Toast.makeText(activity.getApplicationContext(), "choice: C",
                            Toast.LENGTH_SHORT).show();
                    ifchecked=3;
                }
                else if(checkedId == R.id.r4){
                    Toast.makeText(activity.getApplicationContext(), "choice: D",
                            Toast.LENGTH_SHORT).show();
                    ifchecked=4;
                }

            }

        });*/



        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ifchecked==0){
                    Toast.makeText(activity.getApplicationContext(), "Please select " +
                                    "an option",
                            Toast.LENGTH_LONG).show();
                }
                else{
                    if(ifchecked==1){
                        waterinfo="গোড়ালি পর্যন্ত";
                    }
                    else if(ifchecked==2){
                        waterinfo="হাঁটু পর্যন্ত";
                    }
                    else if(ifchecked==3)waterinfo="কোমর পর্যন্ত";
                    else waterinfo="নিশ্চিত নয়";
                }
                dismiss();
            }
        });
    }

    public String getWaterinfo(){
        return waterinfo;
    }

    @Override
    public void onClick(View v) {

        dismiss();
    }

    void checking(){
        gorali.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                gorali.setChecked(true);
                hatu.setChecked(false);
                komor.setChecked(false);
                none.setChecked(false);
               // Toast.makeText(activity.getApplicationContext(), "choice: A",
                 //       Toast.LENGTH_SHORT).show();
                ifchecked=1;
            }
        });

        hatu.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                gorali.setChecked(false);
                hatu.setChecked(true);
                komor.setChecked(false);
                none.setChecked(false);
              //  Toast.makeText(activity.getApplicationContext(), "choice: B",
               //         Toast.LENGTH_SHORT).show();
                ifchecked=2;
            }
        });

        komor.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                gorali.setChecked(false);
                hatu.setChecked(false);
                komor.setChecked(true);
                none.setChecked(false);
               // Toast.makeText(activity.getApplicationContext(), "choice: C",
                //        Toast.LENGTH_SHORT).show();
                ifchecked=3;
            }
        });

        none.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                gorali.setChecked(false);
                hatu.setChecked(false);
                komor.setChecked(false);
                none.setChecked(true);
               // Toast.makeText(activity.getApplicationContext(), "choice: D",
                    //    Toast.LENGTH_SHORT).show();
                ifchecked=4;
            }
        });


    }



}