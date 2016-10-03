package realizer.com.schoolgenieparent;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

//
//import com.google.android.gcm.GCMRegistrar;
//import com.realizer.schoolgenie.parent.backend.DatabaseQueries;
//import com.realizer.schoolgenie.parent.backend.SqliteHelper;
//import com.realizer.schoolgenie.parent.chat.backend.DALQueris;
//import com.realizer.schoolgenie.parent.exceptionhandler.ExceptionHandler;
//import com.realizer.schoolgenie.parent.generalcommunication.backend.DALGeneralCommunication;
//import com.realizer.schoolgenie.parent.holiday.backend.DALHoliday;
//import com.realizer.schoolgenie.parent.pupil.backend.DALMyPupilInfo;
//import com.realizer.schoolgenie.parent.service.BackgroundSyncupService;
//import com.realizer.schoolgenie.parent.service.TestAnnouncementService;
//import com.realizer.schoolgenie.parent.utils.Config;
//import com.realizer.schoolgenie.parent.utils.OnTaskCompleted;

import com.google.android.gcm.GCMRegistrar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import realizer.com.schoolgenieparent.Utils.Config;
import realizer.com.schoolgenieparent.Utils.OnTaskCompleted;
import realizer.com.schoolgenieparent.backend.DALMyPupilInfo;
import realizer.com.schoolgenieparent.backend.DALQueris;
import realizer.com.schoolgenieparent.backend.DatabaseQueries;
import realizer.com.schoolgenieparent.exceptionhandler.ExceptionHandler;

public class LoginActivity extends Activity implements OnTaskCompleted {

    EditText userName, password;
    Button loginButton;
    CheckBox checkBox;
    TextView forgotpwd;
    Intent gpsTrackerIntent;
    String roll_no,Std,schoolCode,Year,Division;
    String getValueBack;
    int num;

    SharedPreferences sharedpreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(this));
        setContentView(R.layout.login_activity);

        //dbqr = new DatabaseQueries(getApplicationContext());

        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        String android_id = telephonyManager.getDeviceId();

        userName = (EditText) findViewById(R.id.edtEmpId);
        password = (EditText) findViewById((R.id.edtPassword));
        forgotpwd = (TextView) findViewById((R.id.txtForgetPswrd));
        loginButton = (Button) findViewById(R.id.btnLogin);
        Typeface face= Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/font.ttf");
        loginButton.setTypeface(face);
        checkBox = (CheckBox) findViewById(R.id.checkBox1);
        num=0;

        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = sharedpreferences.edit();
        edit.putString("DWEVICEID", android_id);
        edit.commit();


//        userName.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//                String result = s.toString().replaceAll(" ", "");
//                if (!s.toString().equals(result)) {
//                    userName.setText(result);
//                    userName.setSelection(result.length());
//                    // alert the user
//                }
//            }
//        });
//
//        password.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//
//                String result = s.toString().replaceAll(" ", "");
//                if (!s.toString().equals(result)) {
//                    password.setText(result);
//                    password.setSelection(result.length());
//                    // alert the user
//                }
//            }
//        });


//        checkBox.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int num = dbqr.GetRememberMeCount();
//                if (checkBox.isChecked()) {
//
//                    if(num==0)
//                    {
//                        long n = dbqr.insertRememberMe(userName.getText().toString().trim(),password.getText().toString().trim(),"true");
//                    }
//
//                    else
//                    {
//                        long n = dbqr.updateRememberMe(userName.getText().toString().trim(), password.getText().toString().trim(), "true");
//                    }
//                }
//                else {
//                    if(num==0)
//                    {
//                        long n = dbqr.insertRememberMe(userName.getText().toString().trim(),password.getText().toString().trim(),"false");
//                    }
//
//                    else
//                    {
//                        long n = dbqr.updateRememberMe(userName.getText().toString().trim(), password.getText().toString().trim(), "false");
//                    }
//
//                }
//            }
//        });



//        int num = dbqr.GetRememberMeCount();
//        if(num==0)
//        {
//
//        }
//        else
//        {
//
//            String chk[] = dbqr.GetRememberMe();
//            if(chk[2].equals("true"))
//            {
//                checkBox.setChecked(true);
//                userName.setText(chk[0]);
//                password.setText(chk[1]);
//            }
//            else
//            {
//                checkBox.setChecked(false);
//                userName.setText("");
//                password.setText("");
//            }
//        }



        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences.Editor edit = sharedpreferences.edit();
                edit.putString("UidName", userName.getText().toString().trim());
                edit.putString("UserName", userName.getText().toString().trim());
                edit.putString("Password", password.getText().toString().trim());
                edit.commit();

                boolean res = isConnectingToInternet();
                if (res == false) {
                    Toast.makeText(LoginActivity.this, "No Internet Connection", Toast.LENGTH_SHORT).show();
                } else
                if (userName.getText().toString().equals("") && password.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Please Enter Username/Password", Toast.LENGTH_LONG).show();
                } else if (userName.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Please Enter Username", Toast.LENGTH_LONG).show();
                } else if (password.getText().toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "Please Enter Password", Toast.LENGTH_LONG).show();
                } else {
//                    Intent intent=new Intent(LoginActivity.this,DrawerActivity.class);
//                    startActivity(intent);

                    LoginAsyncTaskGet obj = new LoginAsyncTaskGet(userName.getText().toString(), password.getText().toString(), LoginActivity.this, LoginActivity.this);
                    obj.execute();
                }
            }
        });

        forgotpwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Under Implementing!!!", Toast.LENGTH_SHORT).show();
            }
        });
    }


    @Override
    public void onTaskCompleted(String s)  {
        boolean b = false;

        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor edit = sharedpreferences.edit();
        edit.commit();
        String logchk = sharedpreferences.getString("LogChk","");
        if (logchk=="")
        {
            logchk="true";
        }

        if(logchk.equals("true"))
        {
            try {
                JSONObject rootObj = new JSONObject(s);
                JSONObject emp=rootObj.getJSONObject("StudentloginResult");
                String validate  = emp.getString("loginResult");
                if(validate.equals("valid"))
                {
                    b=true;
                    parsData(s);
                }
                else
                {
                    JSONArray hldList = emp.getJSONArray("Phs");
                    b=false;
                }

            } catch (JSONException e) {
                num=1;
                e.printStackTrace();
                Log.e("Login(LocalizedMessage)", e.getLocalizedMessage());
                Log.e("Login(StackTrace)", e.getStackTrace().toString());
                Log.e("Login(Cause)", e.getCause().toString());
                Log.wtf("Login(Msg)", e.getMessage());
            }
        }
        else
        {
            b = parsData(s);
        }

        if(b==true) {
//            GCMReg();
            edit.putString("Login", "true");
            edit.commit();

//            Intent i1 = new Intent(getApplicationContext(), TestAnnouncementService.class);
//            startService(i1);
//
//            Intent i2 = new Intent(getApplicationContext(), BackgroundSyncupService.class);
//            startService(i2);
            GCMReg();

            Intent i = new Intent(LoginActivity.this, DrawerActivity.class);
            i.putExtra("FragName", "NoValue");
            startActivity(i);
        }

        else {
            if(num==0)
                Toast.makeText(getApplicationContext(), "Invalid credentials, Pls Try again!", Toast.LENGTH_LONG).show();
            else if(num==1)
                Toast.makeText(getApplicationContext(), "Server Not Responding Please Try After Some Time", Toast.LENGTH_SHORT).show();
        }
    }

    public void GCMReg()
    {
        registerReceiver(mHandleMessageReceiver,
                new IntentFilter(Config.DISPLAY_MESSAGE_ACTION));
        GCMRegistrar.register(LoginActivity.this, Config.SENDER_ID);
    }

    private final BroadcastReceiver mHandleMessageReceiver =
            new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    String newMessage = intent.getExtras().getString(Config.EXTRA_MESSAGE);
                    //Toast.makeText(context,newMessage,Toast.LENGTH_SHORT).show();
                }
            };

    public boolean parsData(String json) {
        DALMyPupilInfo qr = new DALMyPupilInfo(getApplicationContext());
        DatabaseQueries dr=new DatabaseQueries(getApplicationContext());
        DALQueris Qdal = new DALQueris(getApplicationContext());
//        DALHoliday Hdal = new DALHoliday(getApplicationContext());
//        DALGeneralCommunication dla = new DALGeneralCommunication(getApplicationContext());

        String validate="";
        JSONObject rootObj = null;
        Log.d("String", json);
        try {

            rootObj = new JSONObject(json);
            JSONObject emp=rootObj.getJSONObject("StudentloginResult");
            validate = emp.getString("loginResult");

            SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor edit = sharedpreferences.edit();

            JSONObject sdlist = emp.getJSONObject("studentDtls");
            String userId= sdlist.getString("userId");
            String pwd= sdlist.getString("pwd");
            String std= sdlist.getString("std");
            String division= sdlist.getString("division");
            String classrollno= sdlist.getString("classRollNo");
            String rollno= sdlist.getString("rollNo");
            String fname= sdlist.getString("fName");
            String mname= sdlist.getString("mName");
            String lname= sdlist.getString("lName");
            String dob= sdlist.getString("dob");
            String bldgrp= sdlist.getString("bldGrp");
            String fathername= sdlist.getString("fatherName");
            String mothername= sdlist.getString("motherName");
            String contactno= sdlist.getString("contactNo");
            String emergencycontactno= sdlist.getString("emergencyContactNo");
            String address= sdlist.getString("address");
            String hobbies= sdlist.getString("hobbies");
            String comment= sdlist.getString("comments");
            String isactive= sdlist.getString("isActive");
            String Activetill= sdlist.getString("ActiveTill");
            String registrationcode= sdlist.getString("RegistrationCodes");
            String acdyear= sdlist.getString("academicYear");
            String schoolcode= sdlist.getString("schoolCode");
            String thumbnailurl= sdlist.getString("ThumbnailURL");

            edit.putInt("SyncRNO", Integer.valueOf(rollno));
            edit.putString("SyncStd", std);
            edit.putString("SyncDiv", division);
            edit.putString("SyncScode", schoolcode);
            edit.putString("SyncAyear", acdyear);
            edit.putString("DisplayName",fname+" "+lname);
            edit.putString("ThumbnailID", thumbnailurl);

            edit.putString("Firstname",fname);
            edit.putString("STANDARD",std);
            edit.putString("DIVISION",division);
            edit.putString("SchoolCode",schoolcode);
            edit.commit();


            long n =qr.insertStudInfo(userId, pwd, std, division, classrollno, rollno, fname, mname, lname, dob, bldgrp, fathername, mothername, contactno,
                    emergencycontactno, address, hobbies, comment, isactive, Activetill,registrationcode,acdyear,schoolcode);
            if(n>=0)
            {
                Log.d("Student", " Done!!!");
            }else {
                Log.d("Student", " Not Done!!!");
            }


            JSONObject stddiv = emp.getJSONObject("sa");
            JSONArray subAllocation = stddiv.getJSONArray("subjAllocation");
            String teacherid="",Stndard="",division1="",teachername="",subject="",teacherthumbnailurl="";
            int i=subAllocation.length();
            DatabaseQueries qr1 = new DatabaseQueries(getApplicationContext());
            //qr1.deleteTable();
            /*DALHomework h = new DALHomework(getApplicationContext());
            h.deltehomework();*/
            DatabaseQueries chat = new DatabaseQueries(getApplicationContext());
            for(int j=0;j<i;j++)
            {
                JSONObject obj = subAllocation.getJSONObject(j);
                teacherid = obj.getString("TeacherUserId");
                Stndard=obj.getString("Std");
                division1=obj.getString("division");
                teachername=obj.getString("TeacherName");
                subject=obj.getString("subject");
                teacherthumbnailurl=obj.getString("ThumbNailURL");
                long n1=dr.insertSubInfo(Stndard,division1,subject);
                if(n1>=0)
                {
                    n1=-1;
                    long n2= Qdal.insertTeacherSubInfo(Stndard, teachername, teacherid, division1, subject,teacherthumbnailurl);
                }else {
                    Log.d("Teacher", " Not Done!!!");
                }
            }

//            JSONArray hldList = emp.getJSONArray("Phs");
//            for(int k =0;k<hldList.length();k++)
//            {
//                JSONObject obj = hldList.getJSONObject(k);
//                String createdby= obj.getString("CreatedBy");
//                String holiday= obj.getString("Holiday");
//                String enddate= obj.getString("PHEndDate");
//                String startdate= obj.getString("PHStartDate");
//
//                long n2 =Hdal.insertHolidayInfo(createdby, holiday, enddate, startdate);
//                if(n2>=0)
//                {
//                    Log.d("Holiday", " Done!!!");
//                }else {
//                    Log.d("Holiday", " Not Done!!!");
//                }
//            }

        } catch (JSONException e) {
            e.printStackTrace();
            num = 1;
            Log.e("JSON", e.toString());
            Log.e("Login.JLocalizedMessage", e.getLocalizedMessage());
            Log.e("Login(JStackTrace)", e.getStackTrace().toString());
            Log.e("Login(JCause)", e.getCause().toString());
            Log.wtf("Login(JMsg)", e.getMessage());
        }

        if(validate.equals("valid"))
        {return true;}
        else
        {return false;}
    }

    public boolean isConnectingToInternet(){

        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }
        }
        return false;
    }


}
