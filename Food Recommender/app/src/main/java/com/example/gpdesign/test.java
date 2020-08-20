package com.example.gpdesign;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;


import android.content.DialogInterface;
import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class test extends AppCompatActivity {
    ImageView breakfastImage,lunchImage,dinnerImage,breakfastChange,lunchChange,dinnerChange;
    TextView breakfastName,lunchName,dinnerName,breakfastDesc,lunchDesc,dinnerDesc,breakfastCal,lunchCal,dinnerCal;

    InputStream prefstream;
    String[] datapref;
    InputStream inputStream;
    String[] PrefReadLine;
    String[] data;
    int RecordCounter=0;
    List<List<String>> userpreflist = new ArrayList<>();
    String [] allmeals;
    List<String>recommmendation;
    List<String> breakfastlist ;
    List<String>lunchlist ;
    List<String>dinnerlist ;
    List<String>other ;
    List<String>resFinal;
    String breakk,lunchh,dinnerr;
    int breakcal,lunchcal,dinnercall;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        breakfastlist=new ArrayList<>();
        lunchlist = new ArrayList<>();
        dinnerlist = new ArrayList<>();
        other = new ArrayList<>();

        //Loading User Preferences
        final ArrayList<String> UserPrefArray=new ArrayList<String>();
        String PR=getIntent().getStringExtra("UserPref");
        String []t=PR.split(",");
        for (int i=0;i<t.length;i++){
            UserPrefArray.add(t[i]);
        }
        //==========================

        //LOADING Food Data
        final HashMap<String, String[]> fooddata=new HashMap<String, String[]>();
        List<String> ql=new ArrayList<>();
        inputStream=getResources().openRawResource(R.raw.fooddata2);
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        ArrayList<Integer> UserMealNames=new ArrayList<>();
        try{
            String csvLine;
            int q=0;
            while((csvLine = reader.readLine()) !=null)
            {
                data=csvLine.split(",");
                String []temp = new String[15];
                temp[0]=data[1];
                temp[1]=data[2];
                temp[2]=data[3];
                temp[3]=data[4];
                temp[4]=data[5];
                temp[5]=data[6];
                temp[6]=data[7];
                temp[7]=data[8];
                temp[8]=data[9];
                temp[9]=data[10];
                temp[10]=data[11];
                temp[11]=data[12];
                temp[12]=data[13];
                temp[13]=data[14];
                temp[14]=data[15];
                ql.add(data[0]);
                fooddata.put(data[0].trim(),temp);
                if(UserPrefArray.contains(data[0])){
                    UserMealNames.add(q-1);
                }
                q++;
                try{
                }catch (Exception e)
                {
                    Log.e("problem",e.toString());
                }
            }

        }catch (IOException ex)
        {
            throw new  RuntimeException("Error"+ex);
        }
        //==========================


        //LOADING Users Rating
        List<String[]>UsersRating = new ArrayList<>();
        prefstream=getResources().openRawResource(R.raw.rawanonymized);
        int counter=0;
        BufferedReader readpref = new BufferedReader(new InputStreamReader(prefstream));
        try{
            String prefline;

            if ((prefline = readpref.readLine() )!=null)
            {
                PrefReadLine=prefline.split(",");

                counter++;
            }
            while((prefline = readpref.readLine()) !=null && counter!=0)
            {

                datapref=prefline.split(",");
                UsersRating.add(datapref);
                try{


                }catch (Exception e)
                {
                    Log.e("problem",e.toString());
                }
            }

        }catch (IOException ex)
        {
            throw new  RuntimeException("Error"+ex);
        }
        //==========================

        //Applay collaborative algo
        ql.remove(0);
        int[] MatchedUser=collaborativealgo(UserMealNames,UsersRating);
        List<String> qlll=new ArrayList<>();
        for (int i=0;i<MatchedUser.length;i++){
            String[]temp=UsersRating.get(MatchedUser[i]);
            for (int j=0;j<temp.length;j++){
                if(Integer.parseInt(temp[j])>=5){
                    String mealname=ql.get(j);
                    if(!qlll.contains(mealname)){
                        qlll.add(mealname);
                    }
                }
            }
        }
        //==========================





        ///////////////////Filling pref list from csv//////////////////////
        for (int i=0;i<UsersRating.size();i++)
        {
            List<String>temppref=new ArrayList<>();
            for (int j=0;j<PrefReadLine.length;j++)
            {
                if ((Integer.parseInt(UsersRating.get(i)[j])>=5))
                {
                    temppref.add(PrefReadLine[j]);
                }
                userpreflist.add(i,temppref);
            }
            RecordCounter++;
        }
        //================================================================


        HashMap  <String,Integer> res = new HashMap<String,Integer>();
        for (int i=0;i<UserPrefArray.size();i++){
            res = contentbasealgo(UserPrefArray.get(i),userpreflist);
            res = sortHashMapByValuesascending(res);
            int coun=0;
            resFinal= new ArrayList<String>();
            Iterator it = res.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                resFinal.add(coun,(pair.getKey().toString()));
                coun++;
            }
            for(int j=0;j<qlll.size();j++){
                if (resFinal.contains(qlll.get(j))){
                    qlll.remove(j);
                }
                else {
                    resFinal.add(qlll.get(j));
                    qlll.remove(j);
                }
            }
            Collections.reverse(resFinal);
            filteringbycluster(fooddata,getIntent().getStringExtra("cluster"));
        }



        //====================================Breakfast=============================================
        breakfastImage=findViewById(R.id.breakfastMealImage);
        breakfastChange=findViewById(R.id.changeBreakfast);
        breakfastName=findViewById(R.id.breakfastMealName);
        breakfastDesc=findViewById(R.id.breakfastMealDescribtion);
        breakfastCal=findViewById(R.id.breakfastMealCalories);
        breakk=outputBreakfast(Double.parseDouble(getIntent().getStringExtra("calories")),breakfastlist,other,fooddata);
        breakk=breakk.substring(0,breakk.length()-2);
        breakfastCal.setText("Calories: "+String.valueOf(breakcal));
        breakfastDesc.setText(breakk);
        breakfastChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                breakk=breakk.substring(0,breakk.length()-2);
                String[]arr=breakk.split(",");
                for (int i=0;i<arr.length-1;i++){
                    if(breakfastlist.contains(arr[i])) {
                        breakfastlist.remove(arr[i]);
                        breakfastlist.add(arr[i]);
                    }
                }
                breakk=outputBreakfast(Double.parseDouble(getIntent().getStringExtra("calories")),breakfastlist,other,fooddata);
                breakfastCal.setText("Calories: "+String.valueOf(breakcal));
                breakfastDesc.setText(breakk);

            }
        });


        //======================================Lunch===============================================
        lunchImage=findViewById(R.id.lunchMealImage);
        lunchChange=findViewById(R.id.changeLunch);
        lunchName=findViewById(R.id.lunchMealName);
        lunchDesc=findViewById(R.id.lunchMealDescribtion);
        lunchCal=findViewById(R.id.lunchMealCalories);
        lunchh=outputLunch(Double.parseDouble(getIntent().getStringExtra("calories")),lunchlist,other,fooddata,getIntent().getStringExtra("diseases"));
        lunchh=lunchh.substring(0,lunchh.length()-2);
        lunchCal.setText("Calories: "+String.valueOf(lunchcal));
        lunchDesc.setText(lunchh);
        lunchChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lunchh=lunchh.substring(1,lunchh.length()-2);
                String[]arr=lunchh.split(",");
                for (int i=0;i<arr.length-1;i++){
                    if(lunchlist.contains(arr[i])) {
                        lunchlist.remove(arr[i]);
                        lunchlist.add(arr[i]);
                    }
                }
                lunchh=outputLunch(Double.parseDouble(getIntent().getStringExtra("calories")),lunchlist,other,fooddata,getIntent().getStringExtra("diseases"));
                lunchCal.setText("Calories: "+String.valueOf(lunchcal));
                lunchDesc.setText(lunchh);

            }
        });

        //======================================Dinner==============================================
        dinnerImage=findViewById(R.id.dinnerMealImage);
        dinnerChange=findViewById(R.id.changeDinner);
        dinnerName=findViewById(R.id.dinnerMealName);
        dinnerDesc=findViewById(R.id.dinnerMealDescribtion);
        dinnerCal=findViewById(R.id.dinnerMealCalories);
        dinnerr=outputDinner(Double.parseDouble(getIntent().getStringExtra("calories")),dinnerlist,other,fooddata);
        dinnerr=dinnerr.substring(0,dinnerr.length()-2);
        dinnerCal.setText("Calories: "+String.valueOf(dinnercall));
        dinnerDesc.setText(dinnerr);

        dinnerChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dinnerr=dinnerr.substring(0,dinnerr.length()-2);
                String[]arr=dinnerr.split(",");
                for (int i=0;i<arr.length-1;i++){
                    if(dinnerlist.contains(arr[i])) {
                        dinnerlist.remove(arr[i]);
                        dinnerlist.add(arr[i]);
                    }
                }
                dinnerr=outputDinner(Double.parseDouble(getIntent().getStringExtra("calories")),dinnerlist,other,fooddata);
                dinnerCal.setText("Calories: "+String.valueOf(dinnercall));
                dinnerDesc.setText(dinnerr);
            }
        });

    }

    //Sorting
    public LinkedHashMap<String, Integer> sortHashMapByValuesascending(HashMap<String, Integer> passedMap) {
        List<String> mapKeys = new ArrayList<>(passedMap.keySet());
        List<Integer> mapValues = new ArrayList<>(passedMap.values());
        Collections.sort(mapValues);
        Collections.sort(mapKeys);

        LinkedHashMap<String, Integer> sortedMap =
                new LinkedHashMap<>();
        Iterator<Integer> valueIt = mapValues.iterator();
        while (valueIt.hasNext()) {
            int val = valueIt.next();
            Iterator<String> keyIt = mapKeys.iterator();
            while (keyIt.hasNext()) {
                String key = keyIt.next();
                int comp1 = passedMap.get(key);
                int comp2 = val;

                if (comp1==(comp2)) {
                    keyIt.remove();
                    sortedMap.put(key, val);
                    break;
                }
            }
        }
        return sortedMap;
    }
    public LinkedHashMap<String, Integer> sortHashMapByValuesdecending(HashMap<String, Integer> passedMap) {
        List<String> mapKeys = new ArrayList<>(passedMap.keySet());
        List<Integer> mapValues = new ArrayList<>(passedMap.values());
        Collections.sort(mapValues);
        Collections.reverse(mapValues);
        Collections.sort(mapKeys);
        Collections.reverse(mapKeys);

        LinkedHashMap<String, Integer> sortedMap =
                new LinkedHashMap<>();
        Iterator<Integer> valueIt = mapValues.iterator();
        while (valueIt.hasNext()) {
            int val = valueIt.next();
            Iterator<String> keyIt = mapKeys.iterator();
            while (keyIt.hasNext()) {
                String key = keyIt.next();
                int comp1 = passedMap.get(key);
                int comp2 = val;

                if (comp1==(comp2)) {
                    keyIt.remove();
                    sortedMap.put(key, val);
                    break;
                }
            }
        }
        return sortedMap;
    }
    //================================================================

    //content based algorithm
    public  HashMap <String,Integer> contentbasealgo(String listitem,List<List<String>> userlist){
        recommmendation = new ArrayList<>();
        for(int i=0;i<RecordCounter;i++){
            if(userlist.get(i).contains(listitem)){
                recommmendation.addAll(userlist.get(i));
                recommmendation.remove(listitem);
            }
        }
        int maxcount = 1;
        java.util.Collections.sort(recommmendation);
        final HashMap <String,Integer> Sortmap = new HashMap<>();

        for (int i=0;i<recommmendation.size()-1;i++)
        {
            if (recommmendation.get(i).equals(recommmendation.get(i+1)))
            {
                maxcount++;
            }
            else
            {
                Sortmap.put(recommmendation.get(i),maxcount);
                maxcount=1;
            }
        }
        Sortmap.put(recommmendation.get(recommmendation.size()-1),maxcount);
        return Sortmap;
    }
    //================================================================

    //collaborative algorithm
    public int[] collaborativealgo(ArrayList<Integer> MealNames,List<String[]>mypreflist){

        HashMap<String, Integer> pp=new HashMap<>();
        for (int i=0;i<mypreflist.size();i++){


            String []temp=mypreflist.get(i);

            int dom=0;
            for (int k=0;k<temp.length;k++){
                dom+=Math.pow(Integer.parseInt(temp[k]),2);
            }

            int nom=0;int dom2=0;
            for(int j=0;j<MealNames.size();j++){
                nom+=8*(Integer.parseInt(temp[MealNames.get(j)]));
                dom2+=Math.pow(8,2);
            }
            int res= (int) (nom/(Math.sqrt(dom))*(Math.sqrt(dom2)));

            pp.put(String.valueOf(i), res);
        }

        pp=sortHashMapByValuesdecending(pp);

        int z=0;
        int []indeX=new int[5];
        String key="default";
        Iterator myVeryOwnIterator = pp.keySet().iterator();
        while(myVeryOwnIterator.hasNext()) {
            if(z==5)
                break;
            key=(String)myVeryOwnIterator.next();
            indeX[z]=Integer.parseInt(key);
            z++;
        }
        return indeX;
    }

    //================================================================

    ///Filtering food By Cluster
    public void filteringbycluster( HashMap<String, String[]> foddata,String cluserID){


        int counter =0;
        for (int i=0;i<resFinal.size();i++) {
            String item = resFinal.get(i).trim();

            String[] arr = foddata.get(item);
            String typepfmeal = arr[2];

            String cat = arr[1];
            if (!arr[5].contains(cluserID)) {
                resFinal.remove(i);
            } else {
                if (typepfmeal.equals("any") && !cat.equals("Fruit") && !cat.equals("Vegeitables")) {
                    counter++;
                    if (!breakfastlist.contains(item)) {
                        breakfastlist.add(item);
                    }
                    if (!lunchlist.contains(item)) {
                        lunchlist.add(item);
                    }
                    if (!dinnerlist.contains(item)) {
                        dinnerlist.add(item);
                    }
                } else if (cat.equals("Fruit") || cat.equals("Vegeitables")) {
                    if (!other.contains(item)) {
                        other.add(item);
                    }
                } else {
                    allmeals = typepfmeal.split("/");
                    for (int j = 0; j < allmeals.length; j++) {
                        if (allmeals[j].equals("Launch") && !cat.equals("Fruit") && !cat.equals("Vegeitables")) {
                            if (!lunchlist.contains(item)) {
                                lunchlist.add(item);
                            }
                        } else if (allmeals[j].equals("Dinner") && !cat.equals("Fruit") && !cat.equals("Vegeitables")) {
                            if (!dinnerlist.contains(item)) {
                                dinnerlist.add(item);
                            }
                        } else if (allmeals[j].equals("Breakfast") && !cat.equals("Fruit") && !cat.equals("Vegeitables")) {
                            if (!breakfastlist.contains(item)) {
                                breakfastlist.add(item);
                            }
                        }
                    }
                }
            }
        }
        complete(cluserID,foddata);
    }
    //================================================================

    public String outputBreakfast(double calories, List<String> breakfast, List<String> other , HashMap<String,String []> food )
    {

        double calneeded=calories/4;
        double caloriesMAin=calneeded*0.85;
        double caloriesOther=calneeded*(0.15);
        String[] breakfastmeal;
        String output="";
        breakcal=0;
        int c=0;
        while (c<breakfast.size()||caloriesMAin<0){
            breakfastmeal=food.get(breakfast.get(c));
            if (caloriesMAin - (Double.parseDouble(breakfastmeal[0])) > 0){
                output+=breakfast.get(c)+ ",";
                caloriesMAin -= Double.parseDouble(breakfastmeal[0]);
                breakcal+=Integer.parseInt(breakfastmeal[0]);
            }
            c++;
        }
        if (caloriesMAin>0){
            caloriesOther+=caloriesMAin;
        }

        while(caloriesOther>0){
            double z=Math.random()*other.size();
            breakfastmeal=food.get(other.get((int)z));
            breakcal+=Integer.parseInt(breakfastmeal[0]);
            caloriesOther-=Double.parseDouble(breakfastmeal[0]);
            output+=other.get((int)z)+ ",";
        }
        return output;
    }


    public String outputLunch(double calories, List<String> lunch, List<String> other  ,HashMap<String,String []> food,String diseasename)
    {

        lunchcal=0;
        String w;
        String q="";
        double caloriesB = calories/2;
        double caloriesBMAin=caloriesB*0.85;
        String[] Lunch;
        int c=0,quant=0;

        if(!diseasename.equals("No Thing")){
            if(diseasename.equals("Gout")){

                if(caloriesBMAin<=400.00){
                    while (true) {
                        Lunch = food.get(lunch.get(c));
                        if (Lunch[6].equals("1")) {
                            w = lunch.get(c);
                            while (caloriesBMAin - (Double.parseDouble(Lunch[0])) > 0) {
                                quant++;
                                caloriesBMAin = caloriesBMAin - (Double.parseDouble(Lunch[0]));
                                lunchcal += Integer.parseInt(Lunch[0]);
                            }
                            break;
                        } else
                            c++;
                    }
                    q += quant + "" + w + ",";
                    for (int i = 0; i < lunch.size(); i++) {
                        Lunch = food.get(lunch.get(i));

                        if (Lunch[6].equals("0") && caloriesBMAin - (Double.parseDouble(Lunch[0])) > 0) {
                            caloriesBMAin = caloriesBMAin - (Double.parseDouble(Lunch[0]));
                            lunchcal += Integer.parseInt(Lunch[0]);
                            q += lunch.get(i) + ",";
                        }
                    }
                }

                else{
                    double nee400d=400.0;
                    while (true) {
                        Lunch = food.get(lunch.get(c));
                        if (Lunch[6].equals("1")) {
                            w = lunch.get(c);
                            while (nee400d - (Double.parseDouble(Lunch[0])) > 0) {
                                quant++;
                                nee400d = nee400d - (Double.parseDouble(Lunch[0]));
                                lunchcal += Integer.parseInt(Lunch[0]);
                            }
                            break;
                        } else
                            c++;
                    }
                    q += quant + "" + w + ",";
                    caloriesBMAin=caloriesBMAin-nee400d;
                    for (int i = 0; i < lunch.size(); i++) {
                        Lunch = food.get(lunch.get(i));

                        if (Lunch[6].equals("0") && caloriesBMAin - (Double.parseDouble(Lunch[0])) > 0) {
                            caloriesBMAin = caloriesBMAin - (Double.parseDouble(Lunch[0]));
                            lunchcal += Integer.parseInt(Lunch[0]);
                            q += lunch.get(i) + ",";
                        }
                    }
                }
            }

            else if(diseasename.equals("Heart Disease")){

            }
        }

        else if(diseasename.equals("No Thing")){
            while (true) {
                Lunch = food.get(lunch.get(c));
                if (Lunch[6].equals("1")) {
                    w = lunch.get(c);
                    while (caloriesBMAin - (Double.parseDouble(Lunch[0])) > 0) {
                        quant++;
                        caloriesBMAin = caloriesBMAin - (Double.parseDouble(Lunch[0]));
                        lunchcal += Integer.parseInt(Lunch[0]);
                    }
                    break;
                } else
                    c++;
            }
            q += quant + "" + w + ",";
            for (int i = 0; i < lunch.size(); i++) {
                Lunch = food.get(lunch.get(i));

                if (Lunch[6].equals("0") && caloriesBMAin - (Double.parseDouble(Lunch[0])) > 0) {
                    caloriesBMAin = caloriesBMAin - (Double.parseDouble(Lunch[0]));
                    lunchcal += Integer.parseInt(Lunch[0]);
                    q += lunch.get(i) + ",";
                }
            }
        }


        double caloriesOther=caloriesB*(0.15);
        while(caloriesOther>0){
            double z=Math.random()*other.size();
            Lunch=food.get(other.get((int)z));
            caloriesOther-=Double.parseDouble(Lunch[0]);
            q+=other.get((int)z)+ ",";
        }
        return q;
    }


    public String outputDinner(double calories, List<String> dinner, List<String> other , HashMap<String,String []> food  )
    {

        double calneeded=calories/4;
        double caloriesMAin=calneeded*0.85;
        double caloriesOther=calneeded*(0.15);
        String[] Dinnerfastmeal;
        String output="";
        int c=0;dinnercall=0;
        while (c<dinner.size()||caloriesMAin<0){
            Dinnerfastmeal=food.get(dinner.get(c));
            if (caloriesMAin - (Double.parseDouble(Dinnerfastmeal[0])) > 0){
                output+=dinner.get(c)+ ",";
                caloriesMAin -= Double.parseDouble(Dinnerfastmeal[0]);
                dinnercall+=Integer.parseInt(Dinnerfastmeal[0]);
            }
            c++;
        }
        if (caloriesMAin>0){
            caloriesOther+=caloriesMAin;
        }

        while(caloriesOther>0){
            double z=Math.random()*other.size();
            Dinnerfastmeal=food.get(other.get((int)z));
            dinnercall+=Integer.parseInt(Dinnerfastmeal[0]);
            caloriesOther-=Double.parseDouble(Dinnerfastmeal[0]);
            output+=other.get((int)z)+ ",";
        }
        return output;
    }

    public void complete(String cluserID,HashMap<String, String[]> foddata){
        Iterator it = foddata.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            String key=pair.getKey().toString();
            String []val=foddata.get(key);
            if (val[5].contains(cluserID)&&!breakfastlist.contains(key)&&val[2].contains("Breakfast")&&!val[1].equals("Fruit")&&!val[1].equals("Vegeitables")){
                breakfastlist.add(key);
            }
            if (val[5].contains(cluserID)&&!dinnerlist.contains(key)&&val[2].contains("Dinner")&&!val[1].equals("Fruit")&&!val[1].equals("Vegeitables")){
                dinnerlist.add(key);
            }
            if (val[5].contains(cluserID)&&!lunchlist.contains(key)&&val[2].contains("Launch")&&!val[1].equals("Fruit")&&!val[1].equals("Vegeitables")){
                lunchlist.add(key);
            }
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onStart() {
        super.onStart();
        String olddate=getIntent().getStringExtra("date");
        String pattern = "dd-MM-yyyy";
        final String currentdate =new SimpleDateFormat(pattern).format(new Date());
        try {
            SimpleDateFormat myFormat = new SimpleDateFormat("dd-MM-yyyy");
            Date date1 = myFormat.parse(olddate);
            Date date2 = myFormat.parse(currentdate);
            int diff = (int) (date2.getTime() - date1.getTime());
            int d= (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);

            if(d==8){
                AlertDialog.Builder builder1 = new AlertDialog.Builder(test.this);
                builder1.setTitle("Report for this week");
                builder1.setIcon(R.drawable.th);
                builder1.setMessage("Do you want to see your report?");
                builder1.setCancelable(true);
                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id) {
                                String pattern = "dd-MM-yyyy";
                                String dateInString =new SimpleDateFormat(pattern).format(new Date());
                                firebaseAuth=FirebaseAuth.getInstance();
                                String currentuser = firebaseAuth.getInstance().getCurrentUser().getUid();
                                FirebaseDatabase db=FirebaseDatabase.getInstance();
                                DatabaseReference def=db.getReference();
                                def.child("USERS").child(currentuser).child("date").setValue(dateInString);
                                Intent i=new Intent(getApplicationContext(),reportscreen.class);
                                i.putExtra("height",getIntent().getStringExtra("height"));
                                i.putExtra("weight",getIntent().getStringExtra("weight"));
                                i.putExtra("plan",getIntent().getStringExtra("plan"));
                                i.putExtra("Activity",getIntent().getStringExtra("Activity"));
                                i.putExtra("gender",getIntent().getStringExtra("gender"));
                                i.putExtra("age",getIntent().getStringExtra("age"));
                                i.putExtra("calories",getIntent().getStringExtra("calories"));
                                startActivity(i);
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

        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu1,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.TrainingButt:
                String gender=getIntent().getStringExtra("gender");
                String DataBase="";
                if (gender.equals("male")){DataBase="MaleTraining";}
                else{DataBase="FemaleTraining";}
                databaseReference=FirebaseDatabase.getInstance().getReference(DataBase);
                databaseReference.addListenerForSingleValueEvent(valueEventListener);
            case R.id.LogoutButt:
                Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                finish();
        }
        return true;
    }

    ValueEventListener valueEventListener=new ValueEventListener() {

        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if(dataSnapshot.exists()){
                String []names=new String[15];String []Url=new String[15];String []Type=new String[15]; int i=0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()){
                    Training training=snapshot.getValue(Training.class);
                    names[i]=training.getTrainingName();
                    Url[i]=training.getTrainingURL();
                    Type[i]=training.getTrainingType();
                    i++;
                }

                String plan=getIntent().getStringExtra("plan");
                Intent intent=new Intent(getApplicationContext(),DisplayTraining.class);
                intent.putExtra("plan",plan);
                intent.putExtra("NameList",names);
                intent.putExtra("UrlList",Url);
                intent.putExtra("Type",Type);
                startActivity(intent);
            }

        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
        }
    };
}