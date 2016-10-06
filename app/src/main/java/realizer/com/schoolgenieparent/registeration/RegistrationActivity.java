package realizer.com.schoolgenieparent.registeration;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import realizer.com.schoolgenieparent.R;
import realizer.com.schoolgenieparent.Utils.Config;
import realizer.com.schoolgenieparent.Utils.OnTaskCompleted;
import realizer.com.schoolgenieparent.registeration.asynctask.RegistrationAsyncTaskGet;

/**
 * Created by Win on 22/08/2016.
 */
public class RegistrationActivity extends Activity implements OnTaskCompleted {
    Spinner spnSchoolName, spnDivision, spnStd;
    Button btnContinue;
    EditText edtDivision, edtAddress;
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
    TextView temp;
    ArrayAdapter<String> adapterSchool;
    ArrayAdapter<String> adapterStd;
    ArrayAdapter<String> adapterDiv;
    String jsonString;
    int schoolpos;
    int stdpos;
    String newDivAdded;
    boolean isNewDivAdded=  false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registarion_activity_new);

        btnContinue = (Button) findViewById(R.id.btnContinue);
        spnDivision = (Spinner) findViewById(R.id.spDivision);
        spnStd = (Spinner) findViewById(R.id.spStandard);
        spnSchoolName = (Spinner) findViewById(R.id.spSchoolName);
        txtRegistration = (TextView) findViewById(R.id.txtRegistration);
        edtAddress = (EditText) findViewById(R.id.edtAddress);
        edtDivision = (EditText) findViewById(R.id.edtDiv);
        Typeface face = Typeface.createFromAsset(RegistrationActivity.this.getAssets(), "fonts/font.ttf");
        Typeface face1 = Typeface.createFromAsset(RegistrationActivity.this.getAssets(), "fonts/A Bug s Life.ttf");
        btnContinue.setTypeface(face);
        txtRegistration.setTypeface(face);
        edtAddress.setTypeface(face1);
        schoolName=new ArrayList<String>();
        schoolName.add("Select School Name");
        std=new ArrayList<String>();
        std.add("Select Standard");
        div=new ArrayList<String>();
        div.add("Select Division");

        newDivAdded = "";

        adapterSchool = new ArrayAdapter<String>(this, R.layout.spinner_selected_text__layout, schoolName);
        adapterSchool.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        spnSchoolName.setAdapter(adapterSchool);

        adapterStd = new ArrayAdapter<String>(this, R.layout.spinner_selected_text__layout, std);
        adapterStd.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        spnStd.setAdapter(adapterStd);

        adapterDiv = new ArrayAdapter<String>(this, R.layout.spinner_selected_text__layout, div);
        adapterDiv.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        spnDivision.setAdapter(adapterDiv);

        RegistrationAsyncTaskGet asyntaskGet=new RegistrationAsyncTaskGet(RegistrationActivity.this,RegistrationActivity.this,"getschoollist");
        asyntaskGet.execute();


    }
    public void ContinueClick(View view) {
        Schoolnm = spnSchoolName.getSelectedItem().toString();
        if(div.size()>0)
        division=spnDivision.getSelectedItem().toString();
        else
        division = edtDivision.getText().toString().trim();
        standard=spnStd.getSelectedItem().toString();
        if (Schoolnm.equals("Select School Name")) {
            Toast.makeText(RegistrationActivity.this, "Please Select School Name", Toast.LENGTH_SHORT).show();
        }
        else if(standard.equals("Select Standard"))
        {
            Toast.makeText(RegistrationActivity.this, "Please Select Standard", Toast.LENGTH_SHORT).show();
        }
        else if(division.equals("Select Division") || division.length()<=0)
        {
            Toast.makeText(RegistrationActivity.this, "Please Select Division", Toast.LENGTH_SHORT).show();
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
        try
        {
                JSONArray jsonArray=new JSONArray(s);

                JSONObject jsonObject=jsonArray.getJSONObject(position);
                JSONObject main=jsonObject.getJSONObject("school");
                sadd=main.getString("Address");
                scode=main.getString("Code");
                edtAddress.setText(sadd);

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }
    public void getStandard(int position,String s)
    {
        std = new ArrayList<String>();
        std.add(0,"Select Standard");
        try
        {
            JSONArray jsonArray=new JSONArray(s);
            if(position < jsonArray.length() && position != (-10)) {
                JSONObject jsonObject = jsonArray.getJSONObject(position);
                JSONArray stdlistArray = jsonObject.getJSONArray("stdLst");

                for (int i = 0; i < stdlistArray.length(); i++) {
                    JSONObject stdlistObject = stdlistArray.getJSONObject(i);
                    String standard = stdlistObject.getString("std");
                    if(!standard.isEmpty())
                    std.add((i + 1), standard);
                }
            }
            adapterStd = new ArrayAdapter<String>(this, R.layout.spinner_selected_text__layout, std);
            adapterStd.setDropDownViewResource(R.layout.spinner_dropdown_layout);
            spnStd.setAdapter(adapterStd);

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }
    public void getDivision(int position,int stdposition,String s)
    {
        div = new ArrayList<String>();
        div.add(0,"Select Division");
        try {
            //if (position == (-10) && stdposition == (-10)) {

            //}
          //  else {
                JSONArray jsonArray = new JSONArray(s);
                if (position < jsonArray.length() && stdposition != (-10)) {
                    JSONObject jsonObject = jsonArray.getJSONObject(position);
                    JSONArray stdlistArray = jsonObject.getJSONArray("stdLst");
                    if (stdposition < stdlistArray.length() && stdposition != (-10)) {
                        JSONObject stdlistObject = stdlistArray.getJSONObject(stdposition);
                        JSONArray divArray = stdlistObject.getJSONArray("DivisionLst");

                        for (int i = 0; i < divArray.length(); i++) {
                            div.add((i + 1), divArray.getString(i));
                        }
                        if(!newDivAdded.isEmpty()) {
                            div.add(divArray.length() + 1,newDivAdded);
                            div.add(divArray.length() + 2, "Other");
                            newDivAdded = "";
                            isNewDivAdded = true;
                        }
                        else
                        {
                            div.add(divArray.length() + 1, "Other");
                        }
                    }
                }


                if (div.size() > 1 || stdposition == (-10)) {
                    adapterDiv = new ArrayAdapter<String>(RegistrationActivity.this, R.layout.spinner_selected_text__layout, div);
                    adapterDiv.setDropDownViewResource(R.layout.spinner_dropdown_layout);
                    edtDivision.setVisibility(View.GONE);
                    spnDivision.setVisibility(View.VISIBLE);
                    spnDivision.setAdapter(adapterDiv);
                    if(isNewDivAdded) {
                        isNewDivAdded = false;
                        spnDivision.setSelection(adapterDiv.getCount() - 2);
                    }
                } else {
                    spnDivision.setVisibility(View.GONE);
                    edtDivision.setVisibility(View.VISIBLE);
                }

           // }
        }
            catch(JSONException e)
            {
                e.printStackTrace();
            }

    }


    @Override
    public void onTaskCompleted(String s) {

        jsonString=s;

        getSchoolName(jsonString);
        adapterSchool = new ArrayAdapter<String>(this, R.layout.spinner_selected_text__layout, schoolName);
        adapterSchool.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        spnSchoolName.setAdapter(adapterSchool);

        spnSchoolName.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                if (position != 0) {
                    schoolpos = position - 1;
                    getAddress(position - 1, jsonString);
                    getStandard(position - 1, jsonString);
                    spnStd.setSelection(0);
                    getDivision(position - 1, -10, jsonString);
                    spnDivision.setSelection(0);

                } else {
                    schoolpos = position;
                    edtAddress.setText("");
                    edtAddress.setHint(Config.actionBarTitle("School Address", RegistrationActivity.this));
                    edtAddress.setHintTextColor(getResources().getColor(R.color.greycolor));
                    getDivision(position - 1, -10, jsonString);
                    spnDivision.setSelection(0);
                    getStandard(-10, jsonString);
                    spnStd.setSelection(0);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spnStd.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position != 0) {
                    stdpos = position-1;
                    getDivision(schoolpos, position - 1, jsonString);
                }
                else {
                    stdpos = position;
                    getDivision(position - 1, -10, jsonString);
                }

                    spnDivision.setSelection(0);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spnDivision.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (spnDivision.getSelectedItem().toString().equalsIgnoreCase("Other")) {
                    final Typeface face= Typeface.createFromAsset(getApplicationContext().getAssets(), "fonts/font.ttf");

                    LayoutInflater inflater = getLayoutInflater();
                    View dialoglayout = inflater.inflate(R.layout.insert_div_layout, null);
                    Button submit = (Button)dialoglayout.findViewById(R.id.btn_submit);
                    Button cancel = (Button)dialoglayout.findViewById(R.id.btn_cancel);
                    final EditText divisionedt = (EditText) dialoglayout.findViewById(R.id.edtdiv1);
                    submit.setTypeface(face);
                    cancel.setTypeface(face);
                    final AlertDialog.Builder builder = new AlertDialog.Builder(RegistrationActivity.this);
                    builder.setView(dialoglayout);

                    final AlertDialog alertDialog = builder.create();

                    submit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if(divisionedt.getText().toString().trim().length()>0)
                            {
                                newDivAdded = divisionedt.getText().toString().trim();
                                getDivision(schoolpos,stdpos,jsonString);
                                alertDialog.dismiss();
                            }

                        }
                    });

                    cancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            alertDialog.dismiss();
                        }
                    });

                    alertDialog.show();
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }
}
