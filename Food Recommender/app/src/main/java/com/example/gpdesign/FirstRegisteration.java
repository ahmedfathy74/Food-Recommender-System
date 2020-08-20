package com.example.gpdesign;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;

public class FirstRegisteration extends AppCompatActivity {
    EditText regName,regEmail,regPassword,regAge;
    RadioButton male,female;
    Button next;
    ImageView backtomain;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_registeration);

        regName=findViewById(R.id.regName);
        regEmail=findViewById(R.id.regEmail);
        regPassword=findViewById(R.id.regPassword);
        regAge=findViewById(R.id.regAge);
        male=findViewById(R.id.Male);
        female=findViewById(R.id.Female);
        next = findViewById(R.id.Next1);
        backtomain=findViewById(R.id.backTomain);
        male.setChecked(true);

        //===========Check if This Activity Start from Second Activity of Registration==================/**/
        /**/    if(getIntent().getStringExtra("avtivitykey").equals("fromsecondregactivity")){   /**/
        /**/        regName.setText(getIntent().getStringExtra("regName"));                      /**/
        /**/        regEmail.setText(getIntent().getStringExtra("regEmail"));                    /**/
        /**/        regPassword.setText(getIntent().getStringExtra("regPassword"));              /**/
        /**/        regAge.setText(getIntent().getStringExtra("regAge"));                        /**/
        /**/        if(getIntent().getStringExtra("gender").equals("male")){                     /**/
        /**/            male.setChecked(true);                                                         /**/
        /**/        }                                                                                  /**/
        /**/        else {                                                                             /**/
        /**/            female.setChecked(true);                                                       /**/
        /**/        }                                                                                  /**/
        /**/    }                                                                                      /**/
        //=============================================================================================/**/




        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(regName.length()==0){
                    regName.setError("Please Enter Your Name");
                }
                else if(regEmail.length()==0){
                    regEmail.setError("Please Enter Your Email");
                }
                else if(regPassword.length()==0){
                    regPassword.setError("Please Enter Your Password");
                }
                else if(regAge.length()==0){
                    regAge.setError("Please Enter Your Age");
                }
                else {

                    //================Send My Data To Second Activity OF Registration====================/**/
                    /**/ Intent intent=new Intent(getApplicationContext(),SecondRegisteration.class);    /**/
                    /**/ intent.putExtra("myolddata","first");                              /**/
                    /**/ intent.putExtra("name",regName.getText().toString().trim());             /**/
                    /**/ intent.putExtra("email",regEmail.getText().toString().trim());           /**/
                    /**/ intent.putExtra("pass",regPassword.getText().toString().trim());         /**/
                    /**/ intent.putExtra("age",regAge.getText().toString().trim());               /**/
                    /**/ String gender="";                                                              /**/
                    /**/ if (male.isChecked()){                                                         /**/
                    /**/    gender="male";                                                              /**/
                    /**/ }                                                                              /**/
                    /**/ else if(female.isChecked()){                                                   /**/
                    /**/    gender="female";                                                            /**/
                    /**/ }                                                                              /**/
                    /**/ intent.putExtra("gender",gender);                                        /**/
                    //=================================================================================/**/




                    //======Send Data of Second Activity of Registration if i get here from it============================/**/
                    /**/ if (getIntent().getStringExtra("secondactivitydata").equals("olddatatofirstactivity")){   /**/
                    /**/    intent.putExtra("myolddata","data");                                            /**/
                    /**/    intent.putExtra("weight",getIntent().getStringExtra("regWeig"));                /**/
                    /**/    intent.putExtra("height",getIntent().getStringExtra("regHeig"));                /**/
                    /**/    intent.putExtra("plan",getIntent().getStringExtra("plan"));                    /**/
                    /**/    intent.putExtra("activity",getIntent().getStringExtra("activity"));            /**/
                    /**/    intent.putExtra("foodtype",getIntent().getStringExtra("foodtype"));            /**/
                    /**/ }                                                                                              /**/
                    //====================================================================================================
                    startActivity(intent);
                    finish();
                }

            }
        });
        backtomain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
