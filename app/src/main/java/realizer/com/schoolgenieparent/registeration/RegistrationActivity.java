package realizer.com.schoolgenieparent.registeration;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;

import realizer.com.schoolgenieparent.R;
import realizer.com.schoolgenieparent.Utils.OnTaskCompleted;
import realizer.com.schoolgenieparent.wheelpicker.WheelPicker;
import realizer.com.schoolgenieparent.registeration.asynctask.RegistrationAsyncTaskGet;

/**
 * Created by Win on 22/08/2016.
 */
public class RegistrationActivity extends Activity implements OnTaskCompleted {
    Spinner spnSchoolName, spnDivision, spnStd;
    Button btnContinue;
    EditText edtSchoolCode, edtAddress;
    TextView txtSchoolCode, txtAddress, txtRegistration;
    String standard, division,Schoolnm;
    final String[] array = {"Select School", "JSPM", "Other"};
    String[] arraydiv = {"Select Division", "A", "B", "Other"};
    String[] arraystd = {"Select Standard"};
    static int schoolnamepos;
    String sadd,scode;
    AutoCompleteTextView autoCompleteTextView;
    ProgressDialog dialog;
    StringBuilder builder;
    Context mycontext;
    ArrayList<String> schoolName,std,div;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registarion_activity_new);


        String[] array = getResources().getStringArray(R.array.WheelArrayDefault);
        List<String> list = new ArrayList<String>();
        list = Arrays.asList(array);
        ArrayList<String> arrayList = new ArrayList<String>(list);
        arrayList.add("TTS");
        array = arrayList.toArray(new String[list.size()]);
//        R.array.WheelArrayDefault=array;
//        WheelPicker wheelLeft = (WheelPicker) findViewById(R.id.main_wheel_center);
//        wheelLeft.setOnItemSelectedListener(this);
        btnContinue = (Button) findViewById(R.id.btnContinue);
        spnDivision = (Spinner) findViewById(R.id.spDivision);
        spnStd = (Spinner) findViewById(R.id.spStandard);
        spnSchoolName = (Spinner) findViewById(R.id.spSchoolName);
        //edtSchoolCode = (EditText) findViewById(R.id.edtSchoolCode);
        //txtSchoolCode = (TextView) findViewById(R.id.txtSchoolCode);
        txtRegistration = (TextView) findViewById(R.id.txtRegistration);
        edtAddress = (EditText) findViewById(R.id.edtAddress);
        //txtAddress = (TextView) findViewById(R.id.txtAddress);
        Typeface face = Typeface.createFromAsset(RegistrationActivity.this.getAssets(), "fonts/font.ttf");
        btnContinue.setTypeface(face);
        txtRegistration.setTypeface(face);
        //autoCompleteTextView= (AutoCompleteTextView) findViewById(R.id.actv_reg_schoolname);

        schoolName=new ArrayList<String>();
        schoolName.add("Select School Name");
        std=new ArrayList<String>();
        std.add("Select Standard");
        div=new ArrayList<String>();
        div.add("Select Division");

        RegistrationAsyncTaskGet asyntaskGet=new RegistrationAsyncTaskGet(RegistrationActivity.this,RegistrationActivity.this,"getschoollist");
        asyntaskGet.execute();
//        try {
//            builder = asyntaskGet.execute().get();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            e.printStackTrace();
//        }
    }
    public void ContinueClick(View view) {
        Schoolnm = spnSchoolName.getSelectedItem().toString();
        division=spnDivision.getSelectedItem().toString();
        standard=spnStd.getSelectedItem().toString();
        if (Schoolnm.equals("Select School Name")) {
            Toast.makeText(RegistrationActivity.this, "Select School Name", Toast.LENGTH_SHORT).show();
        }
        else if(standard.equals("Select Standard"))
        {
            Toast.makeText(RegistrationActivity.this, "Select Standard", Toast.LENGTH_SHORT).show();
        }
        else if(division.equals("Select Division"))
        {
            Toast.makeText(RegistrationActivity.this, "Select Division", Toast.LENGTH_SHORT).show();
        }
        else {

            //RegistrationModel rgmodel=new RegistrationModel(Schoolnm,standard,division);
            Bundle bundle = new Bundle();
            bundle.putString("SchoolName",Schoolnm);
            bundle.putString("Standard", standard);
            bundle.putString("Division", division);
            bundle.putString("SchoolCode",scode);
            bundle.putString("SchoolAddress",sadd);
            Intent intent = new Intent(RegistrationActivity.this, Registration2Activity.class);
            intent.putExtras(bundle);
            startActivity(intent);
            finish();
        }
    }
    public void getSchoolName(String s)
    {
        try
        {
            JSONArray jsonArray=new JSONArray(s);
            for (int k=0;k<jsonArray.length();k++)
            {
                JSONObject jsonObject=jsonArray.getJSONObject(k);
                JSONObject main=jsonObject.getJSONObject("school");
                String str=main.getString("name");
                schoolName.add(str);
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }
    public void getAddress(int position,String s)
    {
        position--;
        try
        {
            JSONArray jsonArray=new JSONArray(s);
            for (int k=0;k<jsonArray.length();k++)
            {
                JSONObject jsonObject=jsonArray.getJSONObject(position);
                JSONObject main=jsonObject.getJSONObject("school");
                 sadd=main.getString("Address");
                 scode=main.getString("Code");
                edtAddress.setText(scode+","+sadd);
                //txtSchoolCode.setText();
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }
    public void getStandard(int position,String s)
    {
        position--;
        try
        {
            JSONArray jsonArray=new JSONArray(s);
                JSONObject jsonObject=jsonArray.getJSONObject(position);
                JSONArray stdlistArray=jsonObject.getJSONArray("stdLst");
                for (int i=0;i<stdlistArray.length();i++)
                {
                    JSONObject stdlistObject=stdlistArray.getJSONObject(i);
                    String standard=stdlistObject.getString("std");
                    std.add(standard);
                }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }
    public void getDivision(int position,int stdposition,String s)
    {
        position--;
        stdposition--;
        try
        {
            JSONArray jsonArray=new JSONArray(s);
            JSONObject jsonObject=jsonArray.getJSONObject(position);
            JSONArray stdlistArray=jsonObject.getJSONArray("stdLst");
            JSONObject stdlistObject=stdlistArray.getJSONObject(stdposition);
            JSONArray divArray=stdlistObject.getJSONArray("DivisionLst");
            for (int i=0;i<divArray.length();i++)
            {
                div.add(divArray.getString(i));
            }
            //std.add(standard);

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    String jsonString;
    @Override
    public void onTaskCompleted(String s) {

        jsonString=s;
        getSchoolName(jsonString);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, schoolName);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        spnSchoolName.setAdapter(adapter);
        //autoCompleteTextView.setAdapter(adapter);


        spnSchoolName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                schoolnamepos = position;
                std.removeAll(std);
                std.add("Select Standard");
                getAddress(position,jsonString);
                getStandard(position,jsonString);
                std.removeAll(Arrays.asList(null, ""));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ArrayAdapter<String> adapterdiv = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, div);
        adapterdiv.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        ArrayAdapter<String> adapterstd = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, std);
        adapterstd.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        spnDivision.setAdapter(adapterdiv);
        spnStd.setAdapter(adapterstd);



        spnStd.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                div.removeAll(div);
                div.add("Select Division");
                getDivision(schoolnamepos, position,jsonString);
                div.removeAll(Arrays.asList(null, ""));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
