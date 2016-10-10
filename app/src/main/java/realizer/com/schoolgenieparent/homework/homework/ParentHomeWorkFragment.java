package realizer.com.schoolgenieparent.homework.homework;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import realizer.com.schoolgenieparent.DrawerActivity;
import realizer.com.schoolgenieparent.R;
import realizer.com.schoolgenieparent.Utils.Config;
import realizer.com.schoolgenieparent.Utils.ImageStorage;
import realizer.com.schoolgenieparent.Utils.OnBackPressFragment;
import realizer.com.schoolgenieparent.Utils.OnTaskCompleted;
import realizer.com.schoolgenieparent.Utils.Singleton;
import realizer.com.schoolgenieparent.Utils.StoreBitmapImages;
import realizer.com.schoolgenieparent.Utils.Utility;
import realizer.com.schoolgenieparent.backend.DALMyPupilInfo;
import realizer.com.schoolgenieparent.backend.DALQueris;
import realizer.com.schoolgenieparent.backend.DatabaseQueries;
import realizer.com.schoolgenieparent.exceptionhandler.ExceptionHandler;
import realizer.com.schoolgenieparent.homework.TeacherHomeworkNewFragment;
import realizer.com.schoolgenieparent.homework.adapter.ParentHomeworkListAdapter;
import realizer.com.schoolgenieparent.homework.backend.DALHomework;
import realizer.com.schoolgenieparent.homework.model.ParentHomeworkListModel;
import realizer.com.schoolgenieparent.homework.model.TeacherHomeworkListModel;
import realizer.com.schoolgenieparent.homework.model.TeacherHomeworkModel;
import realizer.com.schoolgenieparent.view.FullImageViewActivity;
import realizer.com.schoolgenieparent.view.FullImageViewPager;
import realizer.com.schoolgenieparent.view.ProgressWheel;

/**
 * Created by Win on 11/9/2015.
 */
public class ParentHomeWorkFragment extends Fragment implements View.OnClickListener,OnBackPressFragment {
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
    String userName;
    MessageResultReceiver resultReceiver;
    ProgressWheel loading;
    ArrayList<TeacherHomeworkListModel> homewok;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));
        final View rootView = inflater.inflate(R.layout.parent_homework_layout, container, false);
        setHasOptionsMenu(true);
        datespinner  = (Spinner)rootView.findViewById(R.id.spdate);
        listHomewrok = (ListView) rootView.findViewById(R.id.lsthomework);
        //header = (TextView) rootView.findViewById(R.id.txtheader);
        noHwMsg=(TextView) rootView.findViewById(R.id.tvNoDataMsg);
        txtClassName=(TextView)rootView.findViewById(R.id.txttclassname);
        txtDivName=(TextView) rootView.findViewById(R.id.txttdivname);
        loading = (ProgressWheel)rootView.findViewById(R.id.loading);
        newHomework = (FloatingActionButton) rootView.findViewById(R.id.imgbtnAddHw);
        sharedpreferences= PreferenceManager.getDefaultSharedPreferences(getActivity());
        txtClassName.setText(sharedpreferences.getString("STANDARD", ""));
        txtDivName.setText(sharedpreferences.getString("DIVISION", ""));
        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        userName = sharedpreferences.getString("UidName", "");
         b = getArguments();
        htext = b.getString("HEADERTEXT");
        //header.setText(htext);
        ((DrawerActivity) getActivity()).getSupportActionBar().setTitle(Config.actionBarTitle(htext, getActivity()));
        ((DrawerActivity) getActivity()).getSupportActionBar().show();
        Singleton obj = Singleton.getInstance();
        resultReceiver = new MessageResultReceiver(null);
        obj.setResultReceiver(resultReceiver);
        noHwMsg.setText("No "+ htext +" uploaded for today.");
        DAP=new DALMyPupilInfo(getActivity());
        qr = new DALHomework(getActivity());
        DatabaseQueries db=new DatabaseQueries(getActivity());
        ArrayList<String> datespin = db.GetHWDate();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, datespin);
        adapter.setDropDownViewResource(R.layout.viewstar_subject_spiner);
        datespinner.setAdapter(adapter);
        datespinner.setSelection(datespin.size() - 1);

        DALQueris qrt = new DALQueris(getActivity());
        final ArrayList<String> subjects = qrt.GetAllSub();

        newHomework.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (subjects.size() > 0)
                    {
                        Bundle b = getArguments();
                        String htext = b.getString("HEADERTEXT");
                        TeacherHomeworkNewFragment fragment = new TeacherHomeworkNewFragment();
                        Singleton.setSelectedFragment(fragment);
                        Bundle bundle = new Bundle();
                        bundle.putString("HEADERTEXT",htext);
                        fragment.setArguments(bundle);
                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.frame_container,fragment);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.commit();
                    }
                    else
                    {
                        Toast.makeText(getActivity(), "Subjects are not allocated", Toast.LENGTH_LONG).show();
                    }
                }
            });

        datespinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                loading.setVisibility(View.VISIBLE);
                homewok = GetHomeWorkListNew();

                if (homewok.size()!=0) {
                    listHomewrok.setVisibility(View.VISIBLE);
                    listHomewrok.setAdapter(new ParentHomeworkListAdapter(getActivity(), homewok));
                    noHwMsg.setVisibility(View.GONE);
                    loading.setVisibility(View.GONE);

                    ArrayList<TeacherHomeworkListModel> allImages=new ArrayList<TeacherHomeworkListModel>();
                    for (int i=0;i<homewok.size();i++)
                    {
                        TeacherHomeworkListModel obj=new TeacherHomeworkListModel();
                        obj.setImage(homewok.get(i).getImage());
                        obj.setHomework(homewok.get(i).getHomework());
                        allImages.add(obj);
                    }

                    Singleton.setAllImages(allImages);
                }
                else
                {
                    noHwMsg.setVisibility(View.VISIBLE);
                    if (htext.equalsIgnoreCase("Homework"))
                        noHwMsg.setText("No Homework Provided.");
                    else
                        noHwMsg.setText("No Classwork Provided.");
                    listHomewrok.setVisibility(View.GONE);
                    loading.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        listHomewrok.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(getActivity(),FullImageViewPager.class);
                i.putExtra("HEADERTEXT",htext);
                i.putExtra("POSITION",position);
                startActivity(i);
            }
        });
        return rootView;
    }

    private ArrayList<TeacherHomeworkListModel> GetHomeWorkListNew()
    {
        Bundle b = this.getArguments();
        //ArrayList<TeacherHomeworkModel> hwlst = qr.GetHomeworkData(datespinner.getSelectedItem().toString(), htext, txtClassName.getText().toString(), txtDivName.getText().toString());
        ArrayList<TeacherHomeworkModel> hwlst = qr.GetHomeworkAllData(datespinner.getSelectedItem().toString(),htext);

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

        return results;
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
    public void onResume() {
        super.onResume();
        getActivity().getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }


    //Recive the result when new Message Arrives
    class MessageResultReceiver extends ResultReceiver
    {
        public MessageResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            if(resultCode == 1000){
                getActivity().runOnUiThread(new UpdateUI("RefreshUI"));
            }

        }
    }

    //Update UI
    class UpdateUI implements Runnable {
        String update;

        public UpdateUI(String update) {

            this.update = update;
        }

        public void run() {

            if(update.equals("RefreshUI")) {
                ArrayList<TeacherHomeworkListModel> homewok = GetHomeWorkListNew();

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
        }
    }

}


