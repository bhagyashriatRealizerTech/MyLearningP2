package realizer.com.schoolgenieparent.registeration;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.TypefaceSpan;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import realizer.com.schoolgenieparent.MainActivity;
import realizer.com.schoolgenieparent.R;
import realizer.com.schoolgenieparent.Utils.Config;
import realizer.com.schoolgenieparent.Utils.CustomTypefaceSpan;
import realizer.com.schoolgenieparent.Utils.OnBackPressFragment;
import realizer.com.schoolgenieparent.Utils.OnTaskCompleted;
import realizer.com.schoolgenieparent.registeration.asynctask.RegistrationAsyncTaskPost;
import realizer.com.schoolgenieparent.registeration.model.RegistrationModel;

/**
 * Created by Win on 22/08/2016.
 */
public class Registration2Activity extends Activity implements OnTaskCompleted
{
    Calendar myCalendar;
    DatePickerDialog.OnDateSetListener date;
    Button btnRegister;
    TextView txtStandard,txtDivision,txtRegistration,txtAddress,txtSchoolname,txtRegDob;

    EditText edtPassword,edtConfirmPass,edtStdFname,edtStdLname,edtEmailId,edtPhoneno,edtUserid;
    static String division,standard,schoolname,schoolcode,schooladdress,conpass,pass,studFname,studLname,emailid,phoneno,userId,dob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration2_activity_new);

        btnRegister= (Button) findViewById(R.id.btnRegister);
        edtPassword= (EditText) findViewById(R.id.edt_reg_pass);
        edtConfirmPass= (EditText) findViewById(R.id.edt_reg_confirmpass);
        txtRegistration= (TextView) findViewById(R.id.txtRegistration);
        edtStdFname= (EditText) findViewById(R.id.edt_reg_stdfname);
        edtStdLname= (EditText) findViewById(R.id.edt_reg_stdlname);
        txtSchoolname= (TextView) findViewById(R.id.txt_reg_schoolname);
        txtAddress= (TextView) findViewById(R.id.txt_reg_address);
        txtStandard= (TextView) findViewById(R.id.txt_reg_standard);
        edtUserid= (EditText) findViewById(R.id.edt_reg_userid);
        txtDivision= (TextView) findViewById(R.id.txt_reg_division);
        edtEmailId= (EditText) findViewById(R.id.edt_reg_emailid);
        edtPhoneno= (EditText) findViewById(R.id.edt_reg_phoneno);
        txtRegDob= (TextView) findViewById(R.id.txt_reg_dob);

        final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        Typeface face= Typeface.createFromAsset(Registration2Activity.this.getAssets(), "fonts/font.ttf");
        Typeface face1 = Typeface.createFromAsset(Registration2Activity.this.getAssets(), "fonts/A Bug s Life.ttf");
        btnRegister.setTypeface(face);
        txtRegistration.setTypeface(face);
        Bundle bundle=getIntent().getExtras();
        schoolname=bundle.getString("SchoolName");
        division=bundle.getString("Division");
        standard=bundle.getString("Standard");
        schoolcode=bundle.getString("SchoolCode");
        schooladdress=bundle.getString("SchoolAddress");
        txtSchoolname.setText(schoolname);
        txtAddress.setText(schooladdress);
        txtStandard.setText(standard);
        txtDivision.setText(division);

         myCalendar = Calendar.getInstance();


        edtStdFname.setHint(Config.editTextHint("First Name", Registration2Activity.this));
        edtStdFname.setHintTextColor(getResources().getColor(R.color.greycolor));
        edtStdFname.setTypeface(face1);

        edtStdLname.setHint(Config.editTextHint("Last Name", Registration2Activity.this));
        edtStdLname.setHintTextColor(getResources().getColor(R.color.greycolor));
        edtStdLname.setTypeface(face1);


        edtEmailId.setHint(Config.editTextHint("Email ID", Registration2Activity.this));
        edtEmailId.setHintTextColor(getResources().getColor(R.color.greycolor));
        edtEmailId.setTypeface(face1);


        edtPhoneno.setHint(Config.editTextHint("Contact Number", Registration2Activity.this));
        edtPhoneno.setHintTextColor(getResources().getColor(R.color.greycolor));
        edtPhoneno.setTypeface(face1);


        edtUserid.setHint(Config.editTextHint("User ID", Registration2Activity.this));
        edtUserid.setHintTextColor(getResources().getColor(R.color.greycolor));
        edtUserid.setTypeface(face1);

        edtPassword.setHint(Config.editTextHint("Password",Registration2Activity.this));
        edtPassword.setHintTextColor(getResources().getColor(R.color.greycolor));
        edtPassword.setTypeface(face1);


        edtConfirmPass.setHint(Config.editTextHint("Confirm Password",Registration2Activity.this));
        edtConfirmPass.setHintTextColor(getResources().getColor(R.color.greycolor));
        edtConfirmPass.setTypeface(face1);


         date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                upDateLable();
            }

        };

//        edtDob.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                new DatePickerDialog(Registration2Activity.this, date, myCalendar
//                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
//                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
//            }
//        });


        //RegistrationAsyncTaskGet asyncTaskGet=new RegistrationAsyncTaskGet(Registration2Activity.this,Registration2Activity.this,"StudentLogin");

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetAllData();
                if (studFname.equals(""))
                {
                    Toast.makeText(Registration2Activity.this, "Enter First Name", Toast.LENGTH_SHORT).show();
                }
                else if (studLname.equals(""))
                {
                    Toast.makeText(Registration2Activity.this, "Enter Last Name", Toast.LENGTH_SHORT).show();
                }
                else if (emailid.equals(""))
                {
                    Toast.makeText(Registration2Activity.this, "Enter Email Id", Toast.LENGTH_SHORT).show();
                }
                else if (!emailid.matches(emailPattern))
                {
                    Toast.makeText(Registration2Activity.this, "Enter Valid Emailid", Toast.LENGTH_SHORT).show();
                }
                else if (phoneno.equals(""))
                {
                    Toast.makeText(Registration2Activity.this, "Enter Phone Number", Toast.LENGTH_SHORT).show();
                }
                else if(pass.equals(""))
                {
                    Toast.makeText(Registration2Activity.this, "Enter Password", Toast.LENGTH_SHORT).show();
                }
                else if (conpass.equals(""))
                {
                    Toast.makeText(Registration2Activity.this, "Enter Confirm Password", Toast.LENGTH_SHORT).show();
                }
                else if (pass.equals(conpass))
                {
                    RegistrationModel rgm=new RegistrationModel();
                    rgm.setSchoolName(schoolname);
                    rgm.setStandard(standard);
                    rgm.setDivision(division);
                    rgm.setSchoolCode(schoolcode);
                    rgm.setSchoolAddress(schooladdress);
                    rgm.setDob(dob);
                    rgm.setFname(studFname);
                    rgm.setLname(studLname);
                    rgm.setEmailid(emailid);
                    rgm.setPassword(pass);
                    rgm.setUserId(userId);
                    rgm.setContactNo(phoneno);
                    Boolean connect=isConnectingToInternet();
                    if (connect) {

                        RegistrationAsyncTaskPost asyncTaskPost = new RegistrationAsyncTaskPost(rgm, Registration2Activity.this, Registration2Activity.this);
                        asyncTaskPost.execute();
                    }
                    else
                    {
                        Toast.makeText(Registration2Activity.this, "Please Check Internet Connection", Toast.LENGTH_SHORT).show();
                    }

                }
                else
                {
                    Toast.makeText(Registration2Activity.this, "Password not match..!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public void regdobclick(View view)
    {
        new DatePickerDialog(Registration2Activity.this, date, myCalendar
                .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                myCalendar.get(Calendar.DAY_OF_MONTH)).show();

    }
    public void GetAllData()
    {
        pass=edtPassword.getText().toString();
        conpass=edtConfirmPass.getText().toString();
        studFname=edtStdFname.getText().toString();
        studLname=edtStdLname.getText().toString();
        emailid=edtEmailId.getText().toString();
        phoneno=edtPhoneno.getText().toString();
        userId=edtUserid.getText().toString();

    }

    @Override
    public void onTaskCompleted(String s) {


        if (s.equals("true")) {
            Intent intent = new Intent(Registration2Activity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        else if(s.equals("400"))
        {
            Toast.makeText(Registration2Activity.this, "Username Already Exist.", Toast.LENGTH_SHORT).show();
        }
        else
        {
            Toast.makeText(Registration2Activity.this, "Registration Not done.", Toast.LENGTH_SHORT).show();
        }


//        try {
//            JSONObject rootObj = new JSONObject(s);
//            JSONObject emp=rootObj.getJSONObject("StudentloginResult");
//            String validate  = emp.getString("loginResult");
//            if(validate.equals("valid"))
//            {
//                JSONObject sdlist = emp.getJSONObject("studentDtls");
//                String userId1= sdlist.getString("userId");
//                if(userId1.equals(userId))
//                {
//                    Toast.makeText(Registration2Activity.this, "This user name is available try another.", Toast.LENGTH_SHORT).show();
//                }
//                else
//                {
//                }
//            }
//            else
//            {
//                JSONArray hldList = emp.getJSONArray("Phs");
//                //b=false;
//            }
//
//        } catch (JSONException e) {
//            //num=1;
//            e.printStackTrace();
//            Log.e("Login(LocalizedMessage)", e.getLocalizedMessage());
//            Log.e("Login(StackTrace)", e.getStackTrace().toString());
//            Log.e("Login(Cause)", e.getCause().toString());
//            Log.wtf("Login(Msg)", e.getMessage());
//        }
    }
    public void upDateLable()
    {
        String myFormat = "d/M/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        txtRegDob.setText(sdf.format(myCalendar.getTime()));
        dob=txtRegDob.getText().toString();
    }
    public boolean isConnectingToInternet(){

        ConnectivityManager connectivity =
                (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
