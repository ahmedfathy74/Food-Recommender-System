package com.example.gpdesign;
import androidx.appcompat.app.AppCompatActivity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class reportscreen extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reportscreen);

        firebaseAuth=FirebaseAuth.getInstance();

        firebaseDatabase=FirebaseDatabase.getInstance();
        databaseReference=firebaseDatabase.getReference();

        final TextView textView= (TextView)findViewById(R.id.lose);
        final TextView textView2= (TextView)findViewById(R.id.bmiresult);
        Button button=(Button)findViewById(R.id.button);
        final EditText Weight=(EditText)findViewById(R.id.weight);
        final   String hei = getIntent().getStringExtra("height");
        final   double convert = Double.parseDouble(hei)/100;
        final String Plann=getIntent().getStringExtra("plan");


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentuser = firebaseAuth.getInstance().getCurrentUser().getUid();
                databaseReference.child("USERS").child(currentuser).child("weight").setValue(Weight.getText().toString());
                final double Bmi2 = Integer.parseInt(Weight.getText().toString()) / ((double) Math.pow((convert), 2));
                final String OldWeight=getIntent().getStringExtra("weight");
                final double result = Integer.parseInt(Weight.getText().toString()) - Integer.parseInt(OldWeight);

                if (Plann.equals("Gain Weight")) {
                    if (Bmi2 >= 19 && Bmi2 < 25) {
                        textView.setText("Congratulations ! Your body is perfect !");
                        textView2.setText("You Gained " + result + "KG !");
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(reportscreen.this);
                        builder1.setTitle("CONGRATULATIONS!");
                        builder1.setIcon(R.drawable.gaa);
                        builder1.setMessage("You reached your goal like a champ");
                        builder1.setCancelable(true);
                        builder1.setPositiveButton(
                                "Try Maintain PLan",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // update the plan in the database
                                        String currentuser = firebaseAuth.getInstance().getCurrentUser().getUid();
                                        databaseReference.child("USERS").child(currentuser).child("plan").setValue("Maintain");
                                        double newcal=calcCalories(getIntent().getStringExtra("gender"),"Maintain",getIntent().getStringExtra("Activity"),Integer.parseInt(Weight.getText().toString()), Integer.parseInt(getIntent().getStringExtra("height")),Integer.parseInt(getIntent().getStringExtra("age")));
                                        databaseReference.child("USERS").child(currentuser).child("calories").setValue(newcal);
                                        dialog.cancel();
                                    }
                                });
                        builder1.setNegativeButton(
                                "Quit",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();

                                    }
                                });
                        AlertDialog alert11 = builder1.create();
                        alert11.show();
                    }
                    else if (Bmi2 >= 19 && Bmi2 <= 27) {
                        textView.setText("You gained some extra weight ! But more than expected try doing some exercises !");
                        textView2.setText("You Gained " + result + "KG !");
                    }
                    else if (Bmi2 > 27) {
                        textView2.setText("You Gained " + result + "KG !");
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(reportscreen.this);
                        builder1.setTitle("YOU GAINED SO MUCH !");
                        builder1.setIcon(R.drawable.dddd);
                        builder1.setMessage("Do you want to Loss some Weight ?");
                        builder1.setCancelable(true);
                        builder1.setPositiveButton(
                                "Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        String currentuser = firebaseAuth.getInstance().getCurrentUser().getUid();
                                        databaseReference.child("USERS").child(currentuser).child("plan").setValue("Loss Weight");
                                        double newcal=calcCalories(getIntent().getStringExtra("gender"),"Loss Weight",getIntent().getStringExtra("Activity"),Integer.parseInt(Weight.getText().toString()), Integer.parseInt(getIntent().getStringExtra("height")),Integer.parseInt(getIntent().getStringExtra("age")));
                                        databaseReference.child("USERS").child(currentuser).child("calories").setValue(newcal);
                                        dialog.cancel();
                                    }
                                });
                        builder1.setNegativeButton(
                                "No",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alert11 = builder1.create();
                        alert11.show();

                    }
                    else {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(reportscreen.this);
                        if (result > 0) {
                            textView2.setText("You Gained only  " + Math.abs(result) + "KG !");
                            builder1.setTitle("You gained a little weight !");
                            builder1.setIcon(R.drawable.dddd);
                            builder1.setMessage("Do you want to gain extra weight ?");
                            builder1.setCancelable(true);
                            builder1.setPositiveButton(
                                    "Try Another Gain PLan",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            String currentuser = firebaseAuth.getInstance().getCurrentUser().getUid();
                                            double newCal=Double.parseDouble(getIntent().getStringExtra("calories"))+75;
                                            databaseReference.child("USERS").child(currentuser).child("calories").setValue(newCal);
                                            dialog.cancel();
                                        }
                                    });
                            builder1.setNegativeButton(
                                    "No,Quit",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                            AlertDialog alert11 = builder1.create();
                            alert11.show();
                        }
                        else {
                            {
                                textView2.setText("You Lost " + Math.abs(result) + "KG !");
                                builder1.setTitle("You Lost a little weight !");
                                builder1.setIcon(R.drawable.dddd);
                                builder1.setMessage("Failed to gain weight !");
                                builder1.setCancelable(true);
                                builder1.setPositiveButton(
                                        "Try Another Gain PLan",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                String currentuser = firebaseAuth.getInstance().getCurrentUser().getUid();
                                                double newCal=Double.parseDouble(getIntent().getStringExtra("calories"))+150;
                                                databaseReference.child("USERS").child(currentuser).child("calories").setValue(newCal);
                                                dialog.cancel();
                                            }
                                        });
                                builder1.setNegativeButton(
                                        "No,Quit",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });
                                AlertDialog alert11 = builder1.create();
                                alert11.show();
                            }
                        }
                    }
                }


                else if (Plann.equals("Loss Weight")) {
                    if (Bmi2 >= 19 && Bmi2 < 25) {
                        textView.setText("Congratulations ! Your body is perfect !");
                        textView2.setText("You Lost " + Math.abs(result) + "KG !");
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(reportscreen.this);
                        builder1.setTitle("CONGRATULATIONS!");
                        builder1.setIcon(R.drawable.gaa);
                        builder1.setMessage("You reached your goal like a champ");
                        builder1.setCancelable(true);
                        builder1.setPositiveButton(
                                "Try Maintain PLan",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // update the plan in the database
                                        String currentuser = firebaseAuth.getInstance().getCurrentUser().getUid();
                                        databaseReference.child("USERS").child(currentuser).child("plan").setValue("Maintain");
                                        double newcal=calcCalories(getIntent().getStringExtra("gender"),"Maintain",getIntent().getStringExtra("Activity"),Integer.parseInt(Weight.getText().toString()), Integer.parseInt(getIntent().getStringExtra("height")),Integer.parseInt(getIntent().getStringExtra("age")));
                                        databaseReference.child("USERS").child(currentuser).child("calories").setValue(newcal);
                                        dialog.cancel();
                                    }
                                });
                        builder1.setNegativeButton(
                                "Quit",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alert11 = builder1.create();
                        alert11.show();
                    }
                    else if (Bmi2 >= 19 && Bmi2 <= 27) {
                        textView.setText("Almost perfect body ,try doing some more exercises!");
                        textView2.setText("You Lost " + Math.abs(result) + "KG !");
                    }
                    else if (Bmi2 > 27) {
                        textView2.setText("Your goal is not reached yet ! ");
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(reportscreen.this);
                        builder1.setTitle("Goal not reached !");
                        builder1.setIcon(R.drawable.dddd);
                        builder1.setMessage("Try another diet plan ");
                        builder1.setCancelable(true);
                        builder1.setPositiveButton(
                                "Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        String currentuser = firebaseAuth.getInstance().getCurrentUser().getUid();
                                        double newCal=Double.parseDouble(getIntent().getStringExtra("calories"))-75;
                                        databaseReference.child("USERS").child(currentuser).child("calories").setValue(newCal);
                                        dialog.cancel();
                                    }
                                });
                        builder1.setNegativeButton(
                                "No",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alert11 = builder1.create();
                        alert11.show();

                    }
                    else {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(reportscreen.this);
                        if (result > 0) {
                            textView2.setText("You Gained   " + Math.abs(result) + "KG !");
                            builder1.setTitle("You gained a little Weight !");
                            builder1.setIcon(R.drawable.dddd);
                            builder1.setMessage("Failed to reach the goal !");
                            builder1.setCancelable(true);
                            builder1.setPositiveButton(
                                    "Try Another Diet PLan",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            String currentuser = firebaseAuth.getInstance().getCurrentUser().getUid();
                                            double newCal=Double.parseDouble(getIntent().getStringExtra("calories"))-150;
                                            databaseReference.child("USERS").child(currentuser).child("calories").setValue(newCal);
                                            dialog.cancel();
                                        }
                                    });
                            builder1.setNegativeButton(
                                    "No,Quit",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                            AlertDialog alert11 = builder1.create();
                            alert11.show();
                        }

                        else {
                            {
                                textView2.setText("You Lost " + Math.abs(result) + "KG !");
                                builder1.setTitle("You Lost Extra  weight !");
                                builder1.setIcon(R.drawable.dddd);
                                builder1.setMessage("Lost More Than needed !!");
                                builder1.setCancelable(true);
                                builder1.setPositiveButton(
                                        "Try Gain PLan",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                String currentuser = firebaseAuth.getInstance().getCurrentUser().getUid();
                                                databaseReference.child("USERS").child(currentuser).child("plan").setValue("Gain Weight");
                                                double newcal=calcCalories(getIntent().getStringExtra("gender"),"Gain Weight",getIntent().getStringExtra("Activity"),Integer.parseInt(Weight.getText().toString()), Integer.parseInt(getIntent().getStringExtra("height")),Integer.parseInt(getIntent().getStringExtra("age")));
                                                databaseReference.child("USERS").child(currentuser).child("calories").setValue(newcal);



                                                dialog.cancel();
                                            }
                                        });
                                builder1.setNegativeButton(
                                        "No,Quit",
                                        new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();
                                            }
                                        });
                                AlertDialog alert11 = builder1.create();
                                alert11.show();
                            }
                        }
                    }
                }

                else if (Plann.equals("Maintain"))
                {
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(reportscreen.this);
                    if(Bmi2>=19 && Bmi2<=25)
                    {
                        textView.setText("Congratulations ! Your body is perfect and maintained  !");
                    }

                    else if (Bmi2<19)
                    {
                        if (result > 0) {
                        textView2.setText("Your body is almost good and maintained You gained" + Math.abs(result) + "KG !");
                        builder1.setTitle("You gained a little Weight !");
                        builder1.setIcon(R.drawable.dddd);
                        builder1.setMessage("KG's away for normal BMI!");
                        builder1.setCancelable(true);
                        builder1.setPositiveButton(
                                "Continue Maintaining",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        String currentuser = firebaseAuth.getInstance().getCurrentUser().getUid();
                                        double newcal=Double.parseDouble(getIntent().getStringExtra("calories"))-100;
                                        databaseReference.child("USERS").child(currentuser).child("calories").setValue(newcal);

                                        dialog.cancel();
                                    }
                                });
                        builder1.setNegativeButton(
                                "No,Quit",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alert11 = builder1.create();
                        alert11.show();
                    }
                        else {

                            textView2.setText("You Lost " + Math.abs(result) + "KG !");
                            builder1.setTitle("You Lost a little Weight !");
                            builder1.setIcon(R.drawable.dddd);
                            builder1.setMessage("KG's away for normal BMI!!!");
                            builder1.setCancelable(true);
                            builder1.setPositiveButton(
                                    "Continue Maintaining",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            String currentuser = firebaseAuth.getInstance().getCurrentUser().getUid();
                                            double newcal=Double.parseDouble(getIntent().getStringExtra("calories"))+100;
                                            databaseReference.child("USERS").child(currentuser).child("calories").setValue(newcal);
                                            dialog.cancel();
                                        }
                                    });
                            builder1.setNegativeButton(
                                    "No,Quit",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });
                            AlertDialog alert11 = builder1.create();
                            alert11.show();

                    }
                    }

                    else if (Bmi2>25)
                    {      textView2.setText("You Gained "+ Math.abs(result) + "KG !" );
                        textView.setText("Maintain Plan Failed,Gained Weight");
                        builder1.setTitle("Maintain Plan Failed");
                        builder1.setIcon(R.drawable.dddd);
                        builder1.setMessage("Gained alot of weight !");
                        builder1.setCancelable(true);
                        builder1.setPositiveButton(
                                "Try Another Maintain PLan",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        // update the plan in the database
                                        String currentuser = firebaseAuth.getInstance().getCurrentUser().getUid();
                                        databaseReference.child("USERS").child(currentuser).child("plan").setValue("Loss Weight");
                                        double newcal=calcCalories(getIntent().getStringExtra("gender"),"Loss Weight",getIntent().getStringExtra("Activity"),Integer.parseInt(Weight.getText().toString()), Integer.parseInt(getIntent().getStringExtra("height")),Integer.parseInt(getIntent().getStringExtra("age")));
                                        databaseReference.child("USERS").child(currentuser).child("calories").setValue(newcal);
                                        dialog.cancel();
                                    }
                                });
                        builder1.setNegativeButton(
                                "No,Quit",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alert11 = builder1.create();
                        alert11.show();

                    }
                }

            }

        });
    }

    public double calcCalories(String gender,String plan,String activitylevel,int weight,int height,int age){

        double factor=-1;
        double cal=-1;

        if (activitylevel.equals("Lightly Active")){
            factor=1.375;
        }
        else if(activitylevel.equals("Moderately Active")){
            factor=1.55;
        }
        else if(activitylevel.equals("High Active")){
            factor=1.725;
        }
        if (gender.equals("male")){
            cal=((10*weight)+(6.25*height)-(5*age)+5)*factor;
        }
        else if(gender.equals("female")){
            cal=((10*weight)+(6.25*height)-(5*age)-161)*factor;
        }
        if (plan.equals("Loss Weight")){
            cal-=350;
        }
        else if(plan.equals("Gain Weight")){
            cal+=350;
        }
        return cal;
    }
}
