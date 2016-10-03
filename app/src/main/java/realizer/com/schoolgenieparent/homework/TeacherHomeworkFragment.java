package realizer.com.schoolgenieparent.homework;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
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
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import realizer.com.schoolgenieparent.DrawerActivity;
import realizer.com.schoolgenieparent.R;
import realizer.com.schoolgenieparent.Utils.Config;
//import com.realizer.schoolgenie.backend.DatabaseQueries;
import realizer.com.schoolgenieparent.Utils.OnBackPressFragment;
import realizer.com.schoolgenieparent.Utils.OnTaskCompleted;
import realizer.com.schoolgenieparent.backend.DatabaseQueries;
import realizer.com.schoolgenieparent.homework.adapter.TeacherHomeworkListAdapter;
import realizer.com.schoolgenieparent.homework.backend.DALHomework;
import realizer.com.schoolgenieparent.homework.model.TeacherHomeworkListModel;
import realizer.com.schoolgenieparent.homework.model.TeacherHomeworkModel;
import realizer.com.schoolgenieparent.view.FullImageViewActivity;
//import realizer.com.schoolgenie.myclass.TeacherMyClassDialogBoxActivity;
//import realizer.com.schoolgenie.view.FullImageViewActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Win on 11/26/2015.
 */

public class TeacherHomeworkFragment extends Fragment implements View.OnClickListener,OnBackPressFragment,OnTaskCompleted {

    DatabaseQueries qr;
    Spinner spinner;
    TextView txtstd ,txtclss ,noHwMsg;
    ListView listHoliday;
    FloatingActionButton newHomework;
    String htext,header;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));
        final View rootView = inflater.inflate(R.layout.teacher_homework_layout, container, false);
        setHasOptionsMenu(true);
        Bundle b = getArguments();
       htext = b.getString("HEADERTEXT");
        header=htext;
        ((DrawerActivity) getActivity()).getSupportActionBar().setTitle(Config.actionBarTitle(htext, getActivity()));
        ((DrawerActivity) getActivity()).getSupportActionBar().show();

        qr = new DatabaseQueries(getActivity());
        spinner = (Spinner) rootView.findViewById(R.id.spLeaveType);
        listHoliday = (ListView) rootView.findViewById(R.id.lstthomework);
       // header = (TextView) rootView.findViewById(R.id.txtheadtext);
        noHwMsg=(TextView) rootView.findViewById(R.id.tvNoDataMsg);
        newHomework = (FloatingActionButton) rootView.findViewById(R.id.imgbtnAddHw);

       // header.setText(htext)
       // Spinner Drop down elements

       FillDates();
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ArrayList<TeacherHomeworkListModel> homewok = GetHomeWorkList();
                if (homewok.size()!=0) {
                    listHoliday.setVisibility(View.VISIBLE);
                    listHoliday.setAdapter(new TeacherHomeworkListAdapter(getActivity(), homewok));
                    noHwMsg.setVisibility(View.GONE);
                }
                else
                {
                    noHwMsg.setVisibility(View.VISIBLE);
                    listHoliday.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        txtstd  = (TextView) rootView.findViewById(R.id.txttclassname);
        txtclss = (TextView) rootView.findViewById(R.id.txttdivname);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        txtstd.setText(preferences.getString("STANDARD", ""));
        txtclss.setText(preferences.getString("DIVISION", ""));

        //populate list
        ArrayList<TeacherHomeworkListModel> homewok = GetHomeWorkList();
            if(homewok.size()>0)
        listHoliday.setAdapter(new TeacherHomeworkListAdapter(getActivity(), homewok));



        listHoliday.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Object o = listHoliday.getItemAtPosition(position);

                TeacherHomeworkListModel homeworkObj = (TeacherHomeworkListModel) o;
                String path = homeworkObj.getImage();
                if (path.equals("NoImage")) {

                } else {
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("ImageString", path);
                    editor.commit();
                    loadPhoto(path);
                }
            }
        });



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
                fragmentTransaction.replace(R.id.frame_container,fragment);
                fragmentTransaction.commit();
            }
        });

       /* TextView txtnew = (TextView) rootView.findViewById(R.id.txtnewhomework);
        txtnew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = getArguments();
                String htext = b.getString("HEADERTEXT");

                TeacherHomeworkNewFragment fragment = new TeacherHomeworkNewFragment();
                Bundle bundle = new Bundle();
                bundle.putString("HEADERTEXTN",htext);
                fragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_container,fragment);
                fragmentTransaction.commit();
            }
        });*/
        return rootView;
    }
    private ArrayList<TeacherHomeworkListModel> GetHomeWorkList()
    {
        Bundle b = this.getArguments();
        ArrayList<TeacherHomeworkModel> hwlst = qr.GetHomeworkData(spinner.getSelectedItem().toString(), header.toString(), txtstd.getText().toString(), txtclss.getText().toString());

        ArrayList<TeacherHomeworkListModel> results = new ArrayList<>();

        for(int i=0;i<hwlst.size();i++)
        {
            TeacherHomeworkListModel hDetail = new TeacherHomeworkListModel();
            TeacherHomeworkModel obj = hwlst.get(i);
            hDetail.setSubject(obj.getSubject());
            hDetail.setGivenBy(obj.getGivenBy());
            hDetail.setHasSync(obj.getIsSync());
            try {

                if(obj.getHwTxtLst().length()==0)
                    hDetail.setHomework("NoText");
                else
                    hDetail.setHomework(obj.getHwTxtLst());

                JSONArray jarr1 = new JSONArray(obj.getHwImage64Lst());
                if(jarr1.length()==0)
                    hDetail.setImage("NoImage");
                else
                    hDetail.setImage(obj.getHwImage64Lst());
                results.add(hDetail);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
       /* ArrayList<TeacherHomeworkListModel> results = new ArrayList<>();
        TeacherHomeworkListModel hDetail = new TeacherHomeworkListModel();
        hDetail.setSubject("English");
        hDetail.setGivenBy("A");
        hDetail.setHasSync("true");
        hDetail.setHomework("Home work for Hindi Subject");
        hDetail.setImage("NoImage");
        results.add(hDetail);

        TeacherHomeworkListModel hDetail1 = new TeacherHomeworkListModel();
        hDetail1.setSubject("Mathematics");
        hDetail1.setGivenBy("A");
        hDetail1.setHasSync("false");
        hDetail1.setHomework("Home work for Mathematics Subject");
        hDetail1.setImage("Image");
        results.add(hDetail1);

        TeacherHomeworkListModel hDetail2 = new TeacherHomeworkListModel();
        hDetail2.setSubject("Histrory");
        hDetail2.setGivenBy("A");
        hDetail2.setHasSync("true");
        hDetail2.setHomework("Home work for History Subject");
        hDetail2.setImage("NoImage");
        results.add(hDetail2);

        TeacherHomeworkListModel hDetail3 = new TeacherHomeworkListModel();
        hDetail3.setSubject("Science");
        hDetail3.setGivenBy("A");
        hDetail3.setHasSync("true");
        hDetail3.setHomework("Home work for Science Subject");
        hDetail3.setImage("Image");
        results.add(hDetail3);
*/
        return results;
    }


        public void FillDates()
        {
            Calendar c = Calendar.getInstance();
            int month = c.get(Calendar.MONTH) + 1;
            int year = c.get(Calendar.YEAR);
            int currentdate = c.get(Calendar.DATE);
            ArrayList<String> listofDate = new ArrayList<>();
            listofDate.add("" + currentdate + "/" + month + "/" + year);
            for (int i = 1; i <= 15; i++) {
                c.add(Calendar.DATE, -1);
                int month1 = c.get(Calendar.MONTH) + 1;
                int year1 = c.get(Calendar.YEAR);
                int currentdate1 = c.get(Calendar.DATE);
                listofDate.add("" + currentdate1 + "/" + month1 + "/" + year1);
                Log.d("DATET", "" + currentdate1 + "/" + month1 + "/" + year1);
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_spinner_item, listofDate);
            //adapter.setDropDownViewResource(R.layout.viewstar_subject_spiner);
            for (int i = 0; i < adapter.toString().length(); i++) {
                spinner.setAdapter(adapter);
                break;
            }
            spinner.setSelection(0);
        }

    private void loadPhoto(String path_lst) {
        Intent i = new Intent(getActivity(),FullImageViewActivity.class);
        i.putExtra("FLAG",1);
        startActivity(i);
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    public void SwitchClass()
    {
         String classList="1.,,2nd,,B,,Hindi@@@2.,,4th,,A,,English@@@3.,,2nd,,A,,English@@@4.,,7th,,B,,History@@@5.,,3rd,,B,,English@@@6.,,6th,,B,,History";
//        TeacherMyClassDialogBoxActivity newTermDialogFragment = new TeacherMyClassDialogBoxActivity();
//        FragmentManager fragmentManager = getFragmentManager();
//        Bundle b =new Bundle();
//        b.putString("StudentClassList", classList);
//        b.putInt("MYCLASS",2);
//        newTermDialogFragment.setArguments(b);
//        newTermDialogFragment.setCancelable(false);
//        newTermDialogFragment.show(fragmentManager, "Dialog!");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.menu_main, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.action_switchclass:
                SwitchClass();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }


    }


    @Override
    public void onClick(View v) {

    }
    @Override
    public void OnBackPress() {
        Intent intent=new Intent(getActivity(),DrawerActivity.class);
        startActivity(intent);

    }

    @Override
    public void onTaskCompleted(String s) {
//        boolean b = false;
//        b = parsData(s);
//        if(b==true)
//        {
//            DALHomework hw= new DALHomework(getActivity());
//            ArrayList<String> datespin = hw.GetHWDate();
//            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
//                    android.R.layout.simple_spinner_item, datespin);
//            adapter.setDropDownViewResource(R.layout.viewstar_subject_spiner);
//            spinner.setAdapter(adapter);
//            spinner.setSelection(datespin.size()-1);
//
//            // Toast.makeText(getActivity(), "Homework inserted Successfully", Toast.LENGTH_LONG).show();
//        }
//
//        else {
//            //Toast.makeText(getActivity(), "Not Homework inserted,Pls Try again!", Toast.LENGTH_LONG).show();
//        }
    }
//    public boolean parsData(String json) {
//        long n =-1;
//        DALHomework dla = new DALHomework(getActivity());
//
//        JSONObject rootObj = null;
//        Log.d("String", json);
//        try {
//            rootObj = new JSONObject(json);
//            JSONObject obj=rootObj.getJSONObject("fetchHomeWorkResult");
//            String schoolCode= obj.getString("SchoolCode");
//            String std= obj.getString("Std");
//            String division= obj.getString("div");
//            String givenby= obj.getString("givenBy");
//            String hwdate= obj.getString("hwDate");
//            JSONArray img  = obj.getJSONArray("hwImage64Lst");
//            JSONArray text  = obj.getJSONArray("hwTxtLst");
//            String subject= obj.getString("subject");
//            if(img.length()==0 && text.length()==0)
//            {
//
//            }
//            else {
//
//                n = dla.insertHomeworkInfo(schoolCode, std, division, givenby, hwdate, img.toString(), text.toString(), subject,htext);
//                if (n >= 0) {
//                    // Toast.makeText(getActivity(), "Homework Done", Toast.LENGTH_SHORT).show();
//                    n = -1;
//                }
//            }
//
//
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//            Log.e("JSON", e.toString());
//            Log.e("HWJ(LocalizedMessage)", e.getLocalizedMessage());
//            Log.e("HWJ(StackTrace)", e.getStackTrace().toString());
//            Log.e("HWJ(Cause)", e.getCause().toString());
//        }
//        return true;
//    }
//    public boolean isConnectingToInternet(){
//
//        ConnectivityManager connectivity =
//                (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
//        if (connectivity != null)
//        {
//            NetworkInfo[] info = connectivity.getAllNetworkInfo();
//            if (info != null)
//                for (int i = 0; i < info.length; i++)
//                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
//                    {
//                        return true;
//                    }
//
//        }
//        return false;
//    }
}
