package realizer.com.schoolgenieparent;

import android.app.ActionBar;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import realizer.com.schoolgenieparent.Utils.Config;
import realizer.com.schoolgenieparent.Utils.Singleton;
import realizer.com.schoolgenieparent.communication.TeacherQueryViewFragment;
import realizer.com.schoolgenieparent.exceptionhandler.ExceptionHandler;
import realizer.com.schoolgenieparent.homework.ParentHomeWorkFragment;
import realizer.com.schoolgenieparent.homework.TeacherHomeworkFragment;
import realizer.com.schoolgenieparent.invitejoin.InviteToJoinActivity;

/**
 * Created by Win on 23/08/2016.
 */
public class ParentDashboardFragment extends Fragment implements View.OnClickListener
{
    TextView inviteother, homework, viewStar, timeTable, funCenter, communication, trackPupil,alert, publicHoliday, classwork;
    ViewPager Tab;

    ActionBar actionBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));
        View rootview=inflater.inflate(R.layout.parent_dashboard,container,false);
        Controls(rootview);
        ((DrawerActivity) getActivity()).getSupportActionBar().setTitle(Config.actionBarTitle("DashBoard", getActivity()));
        ((DrawerActivity) getActivity()).getSupportActionBar().show();

        inviteother.setOnClickListener(this);
        homework.setOnClickListener(this);
        viewStar.setOnClickListener(this);
        timeTable.setOnClickListener(this);
        //queries.setOnClickListener(this);
        funCenter.setOnClickListener(this);
        communication.setOnClickListener(this);
        alert.setOnClickListener(this);
        publicHoliday.setOnClickListener(this);
        classwork.setOnClickListener(this);
        //payment.setOnClickListener(this);
//        Tab.setOnPageChangeListener(
//                new ViewPager.SimpleOnPageChangeListener() {
//                    @Override
//                    public void onPageSelected(int position) {
//
//                        actionBar = getActivity().getActionBar();
//                        actionBar.setSelectedNavigationItem(position);
//                    }
//                });

//        actionBar = getActivity().getActionBar();
//        //Enable Tabs on Action Bar
//        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
//        ActionBar.TabListener tabListener = new ActionBar.TabListener(){
//
//            @Override
//            public void onTabReselected(android.app.ActionBar.Tab tab,
//                                        FragmentTransaction ft) {
//                // TODO Auto-generated method stub
//
//            }
//
//            @Override
//            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
//
//                Tab.setCurrentItem(tab.getPosition());
//            }
//
//            @Override
//            public void onTabUnselected(android.app.ActionBar.Tab tab,
//                                        FragmentTransaction ft) {
//                // TODO Auto-generated method stub
//
//            }};
////        Add New Tab
//        actionBar.addTab(actionBar.newTab().setText("Homework").setTabListener(tabListener));
//        actionBar.addTab(actionBar.newTab().setText("Classwork").setTabListener(tabListener));
//        actionBar.addTab(actionBar.newTab().setText("Chat").setTabListener(tabListener));


        return rootview;
    }

        @Override
    public void onClick(View v) {

            Fragment frag=new Fragment();
            switch (v.getId())
            {
                case R.id.txtpdashinviteother:
                    frag = InviteOther("Invite to Join");
                    break;

                case R.id.txtpdashhomework:
                    frag =GetHomework("Homework");
                    break;
                case R.id.txtpdashviewstar:
                    Toast.makeText(getActivity(), "In Progress..", Toast.LENGTH_SHORT).show();
                    //ViewStar("b");
                    break;
                case R.id.txtpdashtimetable:
                    Toast.makeText(getActivity(), "In Progress..", Toast.LENGTH_SHORT).show();
                    //TimeTable("b");
                    break;
                case R.id.txtpdashalert:
                    Toast.makeText(getActivity(), "In Progress..", Toast.LENGTH_SHORT).show();
                    //Queries("b");
                    break;
                case R.id.txtpdashfuncenter:
                    Toast.makeText(getActivity(), "In Progress..", Toast.LENGTH_SHORT).show();
                    //FunCenter("FunCenter");
                    break;
                case R.id.txtpdashcommunication:
                    Communication("Communication");
//                    Toast.makeText(getActivity(), "In Progress..!", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.txtpdashpublicholiday:
                    //PublicHoliday("b");
                    Toast.makeText(getActivity(), "In Progress..", Toast.LENGTH_SHORT).show();
                    break;

                case R.id.txtpdashclasswork:
                    //frag = Homework("Classwork");
                    frag =GetHomework("Classwork");
                    break;
            }
            Singleton.setSelectedFragment(frag);
            Singleton.setMainFragment(frag);


        }

    public void Controls(View v)
    {
        //pupilInfo = (TextView) v.findViewById(R.id.txtpdashmypupil);
        inviteother=(TextView) v.findViewById(R.id.txtpdashinviteother);
        homework = (TextView) v.findViewById(R.id.txtpdashhomework);
        viewStar = (TextView) v.findViewById(R.id.txtpdashviewstar);
        timeTable = (TextView) v.findViewById(R.id.txtpdashtimetable);
        //queries = (TextView) v.findViewById(R.id.txtpdashqueries);
        funCenter = (TextView) v.findViewById(R.id.txtpdashfuncenter);
        communication = (TextView) v.findViewById(R.id.txtpdashcommunication);
        alert= (TextView) v.findViewById(R.id.txtpdashalert);
        //trackPupil = (TextView) v.findViewById(R.id.txtpdashtrackpupil);
        publicHoliday = (TextView) v.findViewById(R.id.txtpdashpublicholiday);
        classwork = (TextView) v.findViewById(R.id.txtpdashclasswork);
        //payment = (TextView) v.findViewById(R.id.txtpdashReport);
    }
    public void MyClass(String res) {

       /* String classList="1.,,2nd,,B,,Hindi@@@2.,,4th,,A,,English@@@3.,,2nd,,A,,English@@@4.,,7th,,B,,History@@@5.,,3rd,,B,,English@@@6.,,6th,,B,,History";
        TeacherMyClassDialogBoxActivity newTermDialogFragment = new TeacherMyClassDialogBoxActivity();
        FragmentManager fragmentManager = getFragmentManager();
        Bundle b =new Bundle();
        b.putString("StudentClassList", classList);
        b.putInt("MYCLASS",1);
        newTermDialogFragment.setArguments(b);
        newTermDialogFragment.setCancelable(false);
        newTermDialogFragment.show(fragmentManager, "Dialog!");*/

        String allStudentList = "1.,,Ajay Shah@@@2.,,Nilam Jadhav@@@3.,,Farhan Bodale@@@4.,,Pravin Jadhav@@@5.,,Ram Magar@@@6.,,Sahil Kadam@@@7.,,Hisha Mulye@@@8.,,Supriya Vichare@@@9.,,Ajay Shah@@@10.,,Nilam Jadhav@@@11.,,Farhan Bodale@@@12.,,Pravin Jadhav@@@13.,,Ram Magar@@@14.,,Sahil Kadam@@@15.,,Hisha Mulye@@@16.,,Supriya Vichare";
        //TeacherMyClassStudentFragment fragment = new TeacherMyClassStudentFragment();
        Bundle bundle = new Bundle();
        //FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        bundle.putString("StudentClassList", allStudentList);
        //fragment.setArguments(bundle);
        //fragmentTransaction.addToBackStack(null);
        //fragmentTransaction.replace(R.id.frame_container, fragment);
        //fragmentTransaction.commit();

    }
    // For Homework
    public Fragment Homework(String res) {
        // Get Output as
        String homewrklist = "Marathi,,lesson no 2 and 3 lesson no 2 and 3 lesson no 2 and 3,,NoImage,,20/11/2015_English,,NoText,,Image,,19/11/2015_Hindi,,hindi homework,,NoImage,,18/11/2015_History,,history homework lesson no 2 and 3,,NoImage,,17/11/2015_Math,,Math homework,,Image,,16/11/2015";
        TeacherHomeworkFragment fragment = new TeacherHomeworkFragment();
        Bundle bundle = new Bundle();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        bundle.putString("HomeworkList", homewrklist);
        bundle.putString("HEADERTEXT", res);
        fragment.setArguments(bundle);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.frame_container, fragment);
        fragmentTransaction.commit();
        return fragment;
    }
    public Fragment GetHomework(String res) {
        // Get Output as
        String homewrklist = "Marathi,,lesson no 2 and 3 lesson no 2 and 3 lesson no 2 and 3,,NoImage,,20/11/2015_English,,NoText,,Image,,19/11/2015_Hindi,,hindi homework,,NoImage,,18/11/2015_History,,history homework lesson no 2 and 3,,NoImage,,17/11/2015_Math,,Math homework,,Image,,16/11/2015";
        ParentHomeWorkFragment fragment = new ParentHomeWorkFragment();
        Bundle bundle = new Bundle();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        bundle.putString("HomeworkList", homewrklist);
        bundle.putString("HEADERTEXT", res);
        fragment.setArguments(bundle);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.frame_container, fragment);
        fragmentTransaction.commit();
        return fragment;
    }
    public void Communication(String res) {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String userId=preferences.getString("UidName", "");
        String sendarname=preferences.getString("Firstname","");
        String urlimage=preferences.getString("ThumbnailID","");
        // Get Output as
        //String homewrklist = "Marathi,,lesson no 2 and 3 lesson no 2 and 3 lesson no 2 and 3,,NoImage,,20/11/2015_English,,NoText,,Image,,19/11/2015_Hindi,,hindi homework,,NoImage,,18/11/2015_History,,history homework lesson no 2 and 3,,NoImage,,17/11/2015_Math,,Math homework,,Image,,16/11/2015";
        TeacherQueryViewFragment fragment = new TeacherQueryViewFragment();
        Bundle bundle = new Bundle();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        //bundle.putString("HomeworkList", homewrklist);
        bundle.putString("HEADERTEXT", res);
        bundle.putString("USERID",userId);
        bundle.putString("SENDERNAME",sendarname);
        bundle.putString("UrlImage",urlimage);
        fragment.setArguments(bundle);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.frame_container, fragment);
        fragmentTransaction.commit();
    }
    public Fragment InviteOther(String res) {
        // Get Output as
        //String homewrklist = "Marathi,,lesson no 2 and 3 lesson no 2 and 3 lesson no 2 and 3,,NoImage,,20/11/2015_English,,NoText,,Image,,19/11/2015_Hindi,,hindi homework,,NoImage,,18/11/2015_History,,history homework lesson no 2 and 3,,NoImage,,17/11/2015_Math,,Math homework,,Image,,16/11/2015";
        InviteToJoinActivity fragment = new InviteToJoinActivity();
        Bundle bundle = new Bundle();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        //bundle.putString("HomeworkList", homewrklist);
        bundle.putString("HEADERTEXT", res);
        fragment.setArguments(bundle);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.frame_container, fragment);
        fragmentTransaction.commit();
        return fragment;
    }

}