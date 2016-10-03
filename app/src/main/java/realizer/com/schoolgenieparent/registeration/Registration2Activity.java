package realizer.com.schoolgenieparent.registeration;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import realizer.com.schoolgenieparent.MainActivity;
import realizer.com.schoolgenieparent.R;
import realizer.com.schoolgenieparent.Utils.OnTaskCompleted;
import realizer.com.schoolgenieparent.registeration.asynctask.RegistrationAsyncTaskGet;
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
    TextView txtStandard,txtDivision,txtRegistration,txtAddress,txtSchoolname;

    EditText edtPassword,edtConfirmPass,edtStdFname,edtStdLname,edtEmailId,edtPhoneno,edtUserid,edtDob;
    static String division,standard,schoolname,schoolcode,schooladdress,conpass,pass,studFname,studLname,emailid,phoneno,userId,dob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration2_activity);

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
        edtDob= (EditText) findViewById(R.id.edt_reg_dob);

        final String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        Typeface face= Typeface.createFromAsset(Registration2Activity.this.getAssets(), "fonts/font.ttf");
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

        edtDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(Registration2Activity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


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

                    RegistrationAsyncTaskPost asyncTaskPost=new RegistrationAsyncTaskPost(rgm,Registration2Activity.this,Registration2Activity.this);
                    asyncTaskPost.execute();

                }
                else
                {
                    Toast.makeText(Registration2Activity.this, "Password not match..!", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
        dob=edtDob.getText().toString();
    }

    @Override
    public void onTaskCompleted(String s) {

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
                    Intent intent = new Intent(Registration2Activity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
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
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        edtDob.setText(sdf.format(myCalendar.getTime()));
    }
}