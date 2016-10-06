package realizer.com.schoolgenieparent;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Text;

import realizer.com.schoolgenieparent.Utils.Config;
import realizer.com.schoolgenieparent.Utils.OnTaskCompleted;
import realizer.com.schoolgenieparent.registeration.RegistrationActivity;
import realizer.com.schoolgenieparent.registeration.asynctask.SchoolRegistrationAsyncTaskPost;
import realizer.com.schoolgenieparent.registeration.asynctask.StandardRegistrationAsyncTaskPost;
import realizer.com.schoolgenieparent.registeration.model.SchoolRegistrationModel;
import realizer.com.schoolgenieparent.view.ProgressWheel;

/**
 * Created by Bhagyashri on 10/6/2016.
 */
public class SchoolRegistrationActivity extends AppCompatActivity implements OnTaskCompleted{

    EditText edtSchoolName,edtSchoolAddress,edtContactNumber,edtStd,edtDiv;
    Button btnRegister;
    ProgressWheel loading;
    TextView txtRegistration;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.school_registartion_activity);
        initiateView();

    }

    public void initiateView()
    {

        getSupportActionBar().hide();
        btnRegister= (Button) findViewById(R.id.btnRegister);
        edtSchoolName= (EditText) findViewById(R.id.edt_school_name);
        edtSchoolAddress= (EditText) findViewById(R.id.edt_school_address);
        edtContactNumber= (EditText) findViewById(R.id.edt_contact_number);
        edtStd= (EditText) findViewById(R.id.edt_std);
        edtDiv= (EditText) findViewById(R.id.edt_div);
        txtRegistration= (TextView) findViewById(R.id.txtRegistration);
        loading = (ProgressWheel) findViewById(R.id.loading);

        Typeface face= Typeface.createFromAsset(SchoolRegistrationActivity.this.getAssets(), "fonts/font.ttf");
        Typeface face1 = Typeface.createFromAsset(SchoolRegistrationActivity.this.getAssets(), "fonts/A Bug s Life.ttf");
        btnRegister.setTypeface(face);
        txtRegistration.setTypeface(face);

        edtSchoolName.setHint(Config.editTextHint("Enter School Name", SchoolRegistrationActivity.this));
        edtSchoolName.setHintTextColor(getResources().getColor(R.color.greycolor));
        edtSchoolName.setTypeface(face1);

        edtSchoolAddress.setHint(Config.editTextHint("Enter School Address", SchoolRegistrationActivity.this));
        edtSchoolAddress.setHintTextColor(getResources().getColor(R.color.greycolor));
        edtSchoolAddress.setTypeface(face1);


        edtContactNumber.setHint(Config.editTextHint("Enter School Contact Number", SchoolRegistrationActivity.this));
        edtContactNumber.setHintTextColor(getResources().getColor(R.color.greycolor));
        edtContactNumber.setTypeface(face1);


        edtStd.setHint(Config.editTextHint("Enter Standard", SchoolRegistrationActivity.this));
        edtStd.setHintTextColor(getResources().getColor(R.color.greycolor));
        edtStd.setTypeface(face1);


        edtDiv.setHint(Config.editTextHint("Enter Division", SchoolRegistrationActivity.this));
        edtDiv.setHintTextColor(getResources().getColor(R.color.greycolor));
        edtDiv.setTypeface(face1);

       /* edtContactNumber.setFilters(new InputFilter[]{
                new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence cs, int start,
                                               int end, Spanned spanned, int dStart, int dEnd) {
                        // TODO Auto-generated method stub
                        if (cs.equals("")) { // for backspace
                            return cs;
                        }
                        if (cs.toString().matches("[0-9]")) {
                            return cs;
                        }
                        return "";
                    }
                }
        });*/

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(edtSchoolName.getText().toString().trim().length()<10)
                {
                    Config.alertDialog(SchoolRegistrationActivity.this,"Error","School Name should be minimum 10 characters");
                }
                else if(edtSchoolAddress.getText().toString().trim().length()<10)
                {
                    Config.alertDialog(SchoolRegistrationActivity.this,"Error","School Address should be minimum 10 characters");
                }
                else if(edtContactNumber.getText().toString().trim().length()<10)
                {
                    Config.alertDialog(SchoolRegistrationActivity.this,"Error","School contact number should be minimum 10 digits");
                }
                else if(edtStd.getText().toString().trim().length()<1)
                {
                    Config.alertDialog(SchoolRegistrationActivity.this,"Error","Please Enter Standard");
                }
                else if(edtDiv.getText().toString().trim().length()<1)
                {
                    Config.alertDialog(SchoolRegistrationActivity.this,"Error","Please Enter Division");
                }
                else
                {
                    if (Config.isConnectingToInternet(SchoolRegistrationActivity.this)) {
                       /* loading.setVisibility(View.GONE);
                        SchoolRegistrationModel obj = new SchoolRegistrationModel();
                        obj.setName(edtSchoolName.getText().toString().trim());
                        obj.setAddress(edtSchoolAddress.getText().toString().trim());
                        obj.setContactNo(edtContactNumber.getText().toString().trim());
                        obj.setStd(edtStd.getText().toString().trim());
                        obj.setDiv(edtDiv.getText().toString().trim());
                        obj.setCode("");
                        obj.setCommentsForCommunication("");
                        obj.setContactPerson("");
                        obj.setEmailId("");
                        new SchoolRegistrationAsyncTaskPost(obj,SchoolRegistrationActivity.this,SchoolRegistrationActivity.this).execute();*/
                    }
                }
            }
        });
    }

    @Override
    public void onTaskCompleted(String s) {
        final Typeface face= Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/font.ttf");

        LayoutInflater inflater = getLayoutInflater();
        View dialoglayout = inflater.inflate(R.layout.successfull_reg_layout, null);
        Button submit = (Button)dialoglayout.findViewById(R.id.btn_submit);
        TextView txtview = (TextView)dialoglayout.findViewById(R.id.infodialog);
        txtview.setText("Congratulations, your school has been registered successfully.");

        submit.setTypeface(face);

        final AlertDialog.Builder builder = new AlertDialog.Builder(SchoolRegistrationActivity.this);
        builder.setView(dialoglayout);
        builder.setCancelable(false);

        final AlertDialog alertDialog = builder.create();

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                Intent intent = new Intent(SchoolRegistrationActivity.this, RegistrationActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
