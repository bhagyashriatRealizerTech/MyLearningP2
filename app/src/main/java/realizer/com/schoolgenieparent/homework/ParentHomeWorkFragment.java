package realizer.com.schoolgenieparent.homework;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import realizer.com.schoolgenieparent.DrawerActivity;
import realizer.com.schoolgenieparent.R;
import realizer.com.schoolgenieparent.Utils.Config;
import realizer.com.schoolgenieparent.Utils.GetImages;
import realizer.com.schoolgenieparent.Utils.ImageStorage;
import realizer.com.schoolgenieparent.Utils.OnBackPressFragment;
import realizer.com.schoolgenieparent.Utils.OnTaskCompleted;
import realizer.com.schoolgenieparent.Utils.Singleton;
import realizer.com.schoolgenieparent.Utils.StoreBitmapImages;
import realizer.com.schoolgenieparent.Utils.Utility;
import realizer.com.schoolgenieparent.backend.DALMyPupilInfo;
import realizer.com.schoolgenieparent.backend.DALQueris;
import realizer.com.schoolgenieparent.exceptionhandler.ExceptionHandler;
import realizer.com.schoolgenieparent.homework.adapter.ParentHomeworkListAdapter;
import realizer.com.schoolgenieparent.homework.adapter.TeacherHomeworkListAdapter;
import realizer.com.schoolgenieparent.homework.asynctask.ClassworkAsyncTaskPost;
import realizer.com.schoolgenieparent.homework.asynctask.HomeworkAsyncTaskPost;
import realizer.com.schoolgenieparent.homework.backend.DALHomework;
import realizer.com.schoolgenieparent.homework.model.ParentHomeworkListModel;
import realizer.com.schoolgenieparent.homework.model.TeacherHomeworkListModel;
import realizer.com.schoolgenieparent.view.FullImageViewActivity;

/**
 * Created by Win on 11/9/2015.
 */
public class ParentHomeWorkFragment extends Fragment implements View.OnClickListener ,OnTaskCompleted,OnBackPressFragment {
    DALHomework qr;
    Spinner datespinner;
    ParentHomeworkListModel obj ;
    String label,htext;
    ListView listHomewrok;
    TextView header,noHwMsg,txtClassName,txtDivName;
    SharedPreferences sharedpreferences;
    FloatingActionButton newHomework;
    DALMyPupilInfo DAP;
    static int counter=0;
    Bundle b;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));
        final View rootView = inflater.inflate(R.layout.parent_homework_layout, container, false);
        setHasOptionsMenu(true);
        datespinner  = (Spinner)rootView.findViewById(R.id.spdate);
        listHomewrok = (ListView) rootView.findViewById(R.id.lsthomework);
        //header = (TextView) rootView.findViewById(R.id.txtheader);
        noHwMsg=(TextView) rootView.findViewById(R.id.tvNoDataMsg);
        txtClassName=(TextView)rootView.findViewById(R.id.txttclassname);
        txtDivName=(TextView) rootView.findViewById(R.id.txttdivname);
        newHomework = (FloatingActionButton) rootView.findViewById(R.id.imgbtnAddHw);
        sharedpreferences=PreferenceManager.getDefaultSharedPreferences(getActivity());
        txtClassName.setText(sharedpreferences.getString("STANDARD", ""));
        txtDivName.setText(sharedpreferences.getString("DIVISION", ""));

         b = getArguments();
        htext = b.getString("HEADERTEXT");
        //header.setText(htext);
        ((DrawerActivity) getActivity()).getSupportActionBar().setTitle(Config.actionBarTitle(htext, getActivity()));
        ((DrawerActivity) getActivity()).getSupportActionBar().show();

        noHwMsg.setText("No "+ htext +" uploaded for today.");
        DAP=new DALMyPupilInfo(getActivity());
        qr = new DALHomework(getActivity());

        ArrayList<String> datespin = qr.GetHWDate();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, datespin);
        adapter.setDropDownViewResource(R.layout.viewstar_subject_spiner);
        datespinner.setAdapter(adapter);
        datespinner.setSelection(datespin.size() - 1);


        newHomework.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle b = getArguments();
                String htext = b.getString("HEADERTEXT");
                TeacherHomeworkNewFragment fragment = new TeacherHomeworkNewFragment();
                Bundle bundle = new Bundle();
                bundle.putString("HEADERTEXTN", htext);
                fragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_container, fragment);
                fragmentTransaction.commit();
            }
        });

        datespinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                ArrayList<ParentHomeworkListModel> homewok = GetHomeWorkList();

                if (homewok.size()!=0) {
                    listHomewrok.setVisibility(View.VISIBLE);
                    listHomewrok.setAdapter(new ParentHomeworkListAdapter(getActivity(), homewok));
                    noHwMsg.setVisibility(View.GONE);
                }
                else
                {
                    noHwMsg.setVisibility(View.VISIBLE);
                    if (htext.equalsIgnoreCase("Homework"))
                        noHwMsg.setText("No Homework Provided.");
                    else
                        noHwMsg.setText("No Classwork Provided.");
                    listHomewrok.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

//        ArrayList<ParentHomeworkListModel> homewok = GetHomeWorkList();
//        if(homewok.size()>0)
//            listHomewrok.setAdapter(new ParentHomeworkListAdapter(getActivity(), homewok));


        listHomewrok.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Object o = listHomewrok.getItemAtPosition(position);

                ParentHomeworkListModel homeworkObj = (ParentHomeworkListModel) o;
                String path = homeworkObj.getImage();
                String hwText = homeworkObj.getHomework();
                if (path.equals("NoImage")) {

                } else {

                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("HomeworkImage", path);
                    editor.putString("HomeworkText", hwText);
                    editor.commit();
                    loadPhoto(0);
                }



//                Object o = listHomewrok.getItemAtPosition(position);
//                ParentHomeworkListModel homeworkObj = (ParentHomeworkListModel) o;
//
//                TeacherHomeworkDetailFragment fragment = new TeacherHomeworkDetailFragment();
//                Singleton.setSelectedFragment(fragment);
//                Bundle bundle = new Bundle();
//                bundle.putString("HEADERTEXT", b.getString("HEADERTEXT"));
//                bundle.putString("SubjectName", homeworkObj.getSubject());
//                bundle.putString("HomeworkDate", homeworkObj.getHwdate());
//                bundle.putString("TeacherName", homeworkObj.getGivenBy());
//                bundle.putString("Status", "Done");
//                bundle.putString("HomeworkImage", homeworkObj.getImage());
//                bundle.putInt("Imageid", homeworkObj.getImgId());
//                bundle.putString("HomeworkText", homeworkObj.getHomework());
//                fragment.setArguments(bundle);
//                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
//                fragmentTransaction.replace(R.id.frame_container, fragment);
//                fragmentTransaction.addToBackStack(null);
//                fragmentTransaction.commit();
            }
        });
        return rootView;
    }

    public void GetHomWrk()
    {

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("d/M/yyyy");
        String datetody = df.format(calendar.getTime());
        String datedb = qr.GetLastSyncHomeworkDate();

        Log.d("SCHOOLCODE", datetody + "\n" + datedb);

        String UserData[]=  DAP.GetSTDDIVData();

        Log.d("SCHOOLCODE", UserData[0] + "\n" + UserData[1] + "\n" + UserData[2]);


        String[] comp1 = datetody.split("/");
        String[] comp2 = datedb.split("/");

        String month = comp1[1];
        String year = comp1[2];

        int counter = 0;
        int Noval=0;
        if (datedb.length() == 0)
        {
            datedb = datetody;
            comp2 = datedb.split("/");
            counter = 1;
            Noval=1;
        }
        else {
            counter = Integer.valueOf(comp1[0]) - Integer.valueOf(comp2[0]);
            if (counter == 0) {
                counter = 1;
            }
        }

        DALQueris qrt = new DALQueris(getActivity());
        ArrayList<String> subjects = qrt.GetAllSub();
        Log.d("COUNTER", "" + counter);
        for (int i = 0; i < counter; i++) {
            int d;
            if (Noval == 1) {
                d = Integer.valueOf(comp2[0]) + (i);
            }
            else {
                d = Integer.valueOf(comp2[0]) + (i + 1);
            }

            String snddate = "" + month + "/" + d + "/" + year;
            Log.d("Date", snddate);
            ArrayList<String> subj = qr.GetHWSub(snddate,htext);

            int len = subjects.size() - subj.size();

            ArrayList<String> removesub = new ArrayList<>();
            int cnt = 0;

            if (subj.size() == 0) {
                removesub = subjects;
            } else {
                for (int knum = 0; knum < subjects.size(); knum++) {
                    for (int num = 0; num < subj.size(); num++) {
                        if (subj.get(num).equals(subjects.get(knum))) {
                            break;
                        } else {

                            if (num == (subj.size() - 1)) {
                                removesub.add(cnt, subjects.get(knum));
                                // Log.d("SUB ", removesub.get(cnt));
                                cnt = cnt + 1;
                            }

                        }
                    }
                }
            }

            for (int l = 0; l < removesub.size(); l++) {
                Log.d("SUB ", removesub.get(l));
            }

            Log.d("DATEFOR", snddate + "  " + removesub.size() + " " + subj.size() + " " + len);

            for (int k = 0; k < len; k++) {

                ParentHomeworkListModel home = new ParentHomeworkListModel();
                home.setSchoolcode(UserData[2]);
                home.setStandard(UserData[0]);
                home.setDivision(UserData[1]);
                home.setHwdate(snddate);
                home.setSubject(removesub.get(k));
                if(isConnectingToInternet()) {
                    if(htext.equalsIgnoreCase("Homework"))
                    {
                        HomeworkAsyncTaskPost obj1 = new HomeworkAsyncTaskPost(home, getActivity(), ParentHomeWorkFragment.this);
                        obj1.execute();
                    }
                    else if(htext.equalsIgnoreCase("Classwork"))
                    {
                        ClassworkAsyncTaskPost obj1 = new ClassworkAsyncTaskPost(home, getActivity(), ParentHomeWorkFragment.this);
                        obj1.execute();
                    }
                }
                else
                {
                    Toast.makeText(getActivity(), "Please Check Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private ArrayList<ParentHomeworkListModel> GetHomeWorkList()
    {
        ArrayList<ParentHomeworkListModel> hwlst = qr.GetHomeworkInfoData(datespinner.getSelectedItem().toString(), htext);
        ArrayList<ParentHomeworkListModel> results = new ArrayList<>();

        for(int i=0;i<hwlst.size();i++)
        {
            ParentHomeworkListModel hDetail = new ParentHomeworkListModel();
            ParentHomeworkListModel obj = hwlst.get(i);
            hDetail.setSubject(obj.getSubject());
            try {

                if(obj.getHomework().length()==0)
                    hDetail.setHomework("NoText");
                else
                    hDetail.setHomework(obj.getHomework());

                JSONArray jarr1 = new JSONArray(obj.getImage());
                if(jarr1.length()==0)
                    hDetail.setImage("NoImage");
                else
                    hDetail.setImage(obj.getImage());
                results.add(hDetail);
            } catch (JSONException e) {
                e.printStackTrace();
                Log.e("HW(LocalizedMessage)", e.getLocalizedMessage());
                Log.e("HW(StackTrace)", e.getStackTrace().toString());
                Log.e("HW(Cause)", e.getCause().toString());
            }
        }
        return results;
    }

    private void loadPhoto(int pos) {
        Intent i = new Intent(getActivity(),FullImageViewActivity.class);
        i.putExtra("FLAG", 1);
        i.putExtra("HEADERTEXT",htext);
        i.putExtra("POSITION",pos);
        startActivity(i);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public void onTaskCompleted(String s)
    {
        boolean b = false;
        b = parsData(s);
        Log.e("JsonString", s);
        if(b==true)
        {
            DALHomework hw= new DALHomework(getActivity());
            ArrayList<String> datespin = hw.GetHWDate();
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_spinner_item, datespin);
            adapter.setDropDownViewResource(R.layout.viewstar_subject_spiner);
            datespinner.setAdapter(adapter);
            datespinner.setSelection(datespin.size()-1);

            // Toast.makeText(getActivity(), "Homework inserted Successfully", Toast.LENGTH_LONG).show();
        }

        else {
            //Toast.makeText(getActivity(), "Not Homework inserted,Pls Try again!", Toast.LENGTH_LONG).show();
        }
    }
    public boolean parsData(String json) {
        long n =-1;
        DALHomework dla = new DALHomework(getActivity());

        JSONObject rootObj = null;
        Log.d("String", json);

            if(htext.equalsIgnoreCase("Homework"))
            {
                try {
                    rootObj = new JSONObject(json);
                    JSONObject obj=rootObj.getJSONObject("fetchHomeWorkResult");
                    String schoolCode= obj.getString("SchoolCode");
                    String std= obj.getString("Std");
                    String division= obj.getString("div");
                    String givenby= obj.getString("givenBy");
                    String hwdate= obj.getString("hwDate");
                    JSONArray img  = obj.getJSONArray("hwImage64Lst");
                    JSONArray text  = obj.getJSONArray("hwTxtLst");
                    String subject= obj.getString("subject");

                    if (!std.equalsIgnoreCase("null") && !givenby.equalsIgnoreCase("null"))
                    {
                        ParentHomeworkListModel model=new ParentHomeworkListModel();
                        model.setSchoolcode(schoolCode);
                        model.setStandard(std);
                        model.setDivision(division);
                        model.setGivenBy(givenby);
                        model.setHwdate(hwdate);
                        model.setImage(img.toString());
                        model.setHomework(text.toString());
                        model.setSubject(subject);
                        model.setWork(htext);
                        String[] IMG=new String[img.length()];
                        DALHomework dh=new DALHomework(getActivity());
                        ArrayList<ParentHomeworkListModel> results=dh.GetHomeworkAllInfoData(hwdate);
                        boolean isPresent=false;

                        for (int j=0;j<results.size();j++)
                        {
                            if (img.toString().equals("[]") && !text.toString().equals("") )
                            {
                                if (results.get(j).getHomework().equalsIgnoreCase(text.toString()))
                                {
                                    isPresent=true;
                                    break;
                                }
                            }
                            else
                            {
                                if (results.get(j).getImage().equalsIgnoreCase(img.toString()) &&
                                        results.get(j).getHomework().equalsIgnoreCase(text.toString()))
                                {
                                    isPresent=true;
                                    break;
                                }
                            }
                        }

                        if (!isPresent)
                        {
                            for (int i = 0; i < img.length(); i++) {
                                IMG[i] = img.getString(i);
                            }

                            for (int i = 0; i < IMG.length; i++) {
                                String newPath = new Utility().getURLImage(IMG[i]);
                                if (!ImageStorage.checkifImageExists(newPath.split("/")[newPath.split("/").length - 1])) {
                                    new StoreBitmapImages(newPath, newPath.split("/")[newPath.split("/").length - 1]).execute(newPath);
                                }
                            }

                            dla.insertHomeworkInfo(schoolCode, std, division, givenby, hwdate, img.toString(), text.toString(), subject,htext);
                            Toast.makeText(getActivity(), "Homework Downloaded Successfully...", Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }



               /* rootObj = new JSONObject(json);
                JSONObject obj = rootObj.getJSONObject("fetchHomeWorkResult");
                String schoolCode = obj.getString("SchoolCode");
                String std = obj.getString("Std");
                String division = obj.getString("div");
                String givenby = obj.getString("givenBy");
                String hwdate = obj.getString("hwDate");
                JSONArray img = obj.getJSONArray("hwImage64Lst");
                JSONArray text = obj.getJSONArray("hwTxtLst");
                String subject = obj.getString("subject");
                if (img.length() == 0 && text.length() == 0) {

                } else {

                    n = dla.insertHomeworkInfo(schoolCode, std, division, givenby, hwdate, img.toString(), text.toString(), subject, htext);
                    if (n >= 0) {
                        // Toast.makeText(getActivity(), "Homework Done", Toast.LENGTH_SHORT).show();
                        n = -1;
                    }
                }*/
            }
            else  if(htext.equalsIgnoreCase("Classwork"))
            {
                try {
                    rootObj = new JSONObject(json);
                    JSONObject obj = rootObj.getJSONObject("fetchClassWorkResult");
                    String schoolCode = obj.getString("SchoolCode");
                    String std = obj.getString("Std");
                    String division = obj.getString("div");
                    String givenby = obj.getString("givenBy");
                    String hwdate = obj.getString("cwDate");
                    JSONArray img = obj.getJSONArray("cwImage64Lst");
                    JSONArray text = obj.getJSONArray("CwTxtLst");
                    String subject = obj.getString("subject");

                    if (!std.equalsIgnoreCase("null") && !givenby.equalsIgnoreCase("null")) {
                        ParentHomeworkListModel model = new ParentHomeworkListModel();
                        model.setSchoolcode(schoolCode);
                        model.setStandard(std);
                        model.setDivision(division);
                        model.setGivenBy(givenby);
                        model.setHwdate(hwdate);
                        model.setImage(img.toString());
                        model.setHomework(text.toString());
                        model.setSubject(subject);
                        model.setWork(htext);
                        String[] IMG = new String[img.length()];

                        DALHomework dh=new DALHomework(getActivity());
                        ArrayList<ParentHomeworkListModel> results=dh.GetHomeworkAllInfoData(hwdate);
                        boolean isPresent=false;

                        for (int j=0;j<results.size();j++)
                        {
                            if (img.toString().equals("[]") && !text.toString().equals("") )
                            {
                                if (results.get(j).getHomework().equalsIgnoreCase(text.toString()))
                                {
                                    isPresent=true;
                                    break;
                                }
                            }
                            else
                            {
                                if (results.get(j).getImage().equalsIgnoreCase(img.toString()))
                                {
                                    isPresent=true;
                                    break;
                                }
                            }
                        }

                        if (!isPresent)
                        {
                            for (int i = 0; i < img.length(); i++) {
                                IMG[i] = img.getString(i);
                            }

                            for (int i = 0; i < IMG.length; i++) {
                                String newPath = new Utility().getURLImage(IMG[i]);
                                if (!ImageStorage.checkifImageExists(newPath.split("/")[newPath.split("/").length - 1])) {
                                    new StoreBitmapImages(newPath, newPath.split("/")[newPath.split("/").length - 1]).execute(newPath);
                                }
                            }

                            dla.insertHomeworkInfo(schoolCode, std, division, givenby, hwdate, img.toString(), text.toString(), subject, htext);
                            Toast.makeText(getActivity(), "Classwork Downloaded Successfully...", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                catch (JSONException e)
                {
                    e.printStackTrace();
                }
                /*rootObj = new JSONObject(json);
                JSONObject obj = rootObj.getJSONObject("fetchClassWorkResult");
                String schoolCode = obj.getString("SchoolCode");
                String std = obj.getString("Std");
                String division = obj.getString("div");
                String givenby = obj.getString("givenBy");
                String hwdate = obj.getString("cwDate");
                JSONArray img = obj.getJSONArray("cwImage64Lst");
                JSONArray text = obj.getJSONArray("CwTxtLst");
                String subject = obj.getString("subject");
                if (img.length() == 0 && text.length() == 0) {

                } else {

                    n = dla.insertHomeworkInfo(schoolCode, std, division, givenby, hwdate, img.toString(), text.toString(), subject, htext);
                    if (n >= 0) {
                        // Toast.makeText(getActivity(), "Homework Done", Toast.LENGTH_SHORT).show();
                        n = -1;
                    }
                }*/
            }
        return true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.menu_download, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_download:
                if(htext.toString().equals("Homework"))
                {
                    GetHomWrk1();
                }
                else if(htext.toString().equals("Classwork"))
                {
                    GetHomWrk1();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean isConnectingToInternet(){

        ConnectivityManager connectivity =
                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
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
    public void OnBackPress() {
        Intent intent=new Intent(getActivity(),DrawerActivity.class);
        startActivity(intent);
    }


    public void GetHomWrk1()
    {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("MM/d/yyyy");
        String datetody = df.format(calendar.getTime());
        String UserData[]=  DAP.GetSTDDIVData();
        DALQueris qrt = new DALQueris(getActivity());
        ArrayList<String> subjects = qrt.GetAllSub();

        for (int k = 0; k < subjects.size(); k++) {

            ParentHomeworkListModel home = new ParentHomeworkListModel();
            home.setSchoolcode(UserData[2]);
            home.setStandard(UserData[0]);
            home.setDivision(UserData[1]);
            home.setHwdate(datetody);
            home.setSubject(subjects.get(k));
            if(isConnectingToInternet()) {
                if(htext.equalsIgnoreCase("Homework"))
                {
                    HomeworkAsyncTaskPost obj1 = new HomeworkAsyncTaskPost(home, getActivity(), ParentHomeWorkFragment.this);
                    obj1.execute();
                }
                else if(htext.equalsIgnoreCase("Classwork"))
                {
                    ClassworkAsyncTaskPost obj1 = new ClassworkAsyncTaskPost(home, getActivity(), ParentHomeWorkFragment.this);
                    obj1.execute();
                }
            }
            else
            {
                Toast.makeText(getActivity(), "Please Check Internet Connection", Toast.LENGTH_SHORT).show();
            }
        }
    }
}


