package realizer.com.schoolgenieparent;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import realizer.com.schoolgenieparent.Notification.NotificationModel;
import realizer.com.schoolgenieparent.Notification.TeacherNotificationListAdapter;
import realizer.com.schoolgenieparent.Utils.Config;
import realizer.com.schoolgenieparent.Utils.GetImages;
import realizer.com.schoolgenieparent.Utils.ImageStorage;
import realizer.com.schoolgenieparent.Utils.OnTaskCompleted;
import realizer.com.schoolgenieparent.Utils.Singleton;
import realizer.com.schoolgenieparent.Utils.Utility;
import realizer.com.schoolgenieparent.backend.DatabaseQueries;
import realizer.com.schoolgenieparent.communication.TeacherQueryViewFragment;
import realizer.com.schoolgenieparent.exceptionhandler.ExceptionHandler;
import realizer.com.schoolgenieparent.homework.ParentHomeWorkFragment;
import realizer.com.schoolgenieparent.invitejoin.InviteToOthersFragment;

import realizer.com.schoolgenieparent.myclass.MyClassStudentFragment;
import realizer.com.schoolgenieparent.myclass.MyPupilInfoFragment;
import realizer.com.schoolgenieparent.trackpupil.TrackShowMap;
import realizer.com.schoolgenieparent.trackpupil.TrackingDialogBoxActivity;
import realizer.com.schoolgenieparent.trackpupil.asynctask.TrackingAsyckTaskGet;
import realizer.com.schoolgenieparent.view.Action;
import realizer.com.schoolgenieparent.view.SwipeDetector;

/**
 * Created by Win on 23/08/2016.
 */
public class ParentDashboardFragment extends Fragment implements View.OnClickListener,OnTaskCompleted
{
    TextView inviteother, homework, viewStar, timeTable, funCenter, communication, trackPupil,alert, publicHoliday, classwork , mypupil,myclass;
    ViewPager Tab;
    ActionBar actionBar;
    ListView notificationList;
    LinearLayout userInfoLayout;
    SwipeDetector swipeDetector;
    ImageView picUser;
    TextView nameUSer,userInitials,textStdDiv;
    ArrayList<NotificationModel> notificationData;
    TeacherNotificationListAdapter notificationAdapter;
    MessageResultReceiver resultReceiver;
    DatabaseQueries qr;
    ArrayList<NotificationModel> FilteredData;

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
        trackPupil.setOnClickListener(this);
        funCenter.setOnClickListener(this);
        communication.setOnClickListener(this);
        alert.setOnClickListener(this);
        publicHoliday.setOnClickListener(this);
        classwork.setOnClickListener(this);
        mypupil.setOnClickListener(this);
        myclass.setOnClickListener(this);

        qr = new DatabaseQueries(getActivity());
        notificationData = new ArrayList<>();
        swipeDetector = new SwipeDetector();
        notificationList.setOnTouchListener(swipeDetector);
        Singleton obj = Singleton.getInstance();
        resultReceiver = new MessageResultReceiver(null);
        obj.setResultReceiver(resultReceiver);

        //showing dp
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        final String urlString = preferences.getString("ThumbnailID","");
        Log.d("Image URL", urlString);
        Typeface face= Typeface.createFromAsset(getActivity().getAssets(), "fonts/font.ttf");
        nameUSer.setText(preferences.getString("DisplayName", ""));
        nameUSer.setTypeface(face,Typeface.NORMAL);
        textStdDiv.setText(preferences.getString("SyncStd", "")+"   "+preferences.getString("SyncDiv", ""));
        nameUSer.setTypeface(face,Typeface.NORMAL);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (urlString.equals("") || urlString.equalsIgnoreCase("null")) {
                    picUser.setVisibility(View.GONE);
                    userInitials.setVisibility(View.VISIBLE);
                    String name[] = nameUSer.getText().toString().split(" ");
                    String fname = name[0].trim().toUpperCase().charAt(0) + "";
                    if (name.length > 1) {
                        String lname = name[1].trim().toUpperCase().charAt(0) + "";
                        userInitials.setText(fname + lname);
                    } else
                        userInitials.setText(fname);

                } else {
                    picUser.setVisibility(View.VISIBLE);
                    userInitials.setVisibility(View.GONE);

                    if (urlString.contains("http")) {
                        String newURL = new Utility().getURLImage(urlString);
                        if (!ImageStorage.checkifImageExists(newURL.split("/")[newURL.split("/").length - 1]))
                            new GetImages(newURL, picUser, userInitials, nameUSer.getText().toString(), newURL.split("/")[newURL.split("/").length - 1]).execute(newURL);
                        else {
                            File image = ImageStorage.getImage(newURL.split("/")[newURL.split("/").length - 1]);
                            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                            Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
                            //  bitmap = Bitmap.createScaledBitmap(bitmap,parent.getWidth(),parent.getHeight(),true);
                            picUser.setImageBitmap(bitmap);
                        }
                    } else {
                        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                        Bitmap bitmap = BitmapFactory.decodeFile(urlString, bmOptions);
                        picUser.setImageBitmap(bitmap);
                    }
                }

            }
        },1500);


        new GetNotificationList().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,1);

        notificationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                if (swipeDetector.swipeDetected()) {

                    if (swipeDetector.getAction() == Action.LR) {
                        // perform any task
                    }
                    else if (swipeDetector.getAction() == Action.RL) {
                        // perform any task


                        final Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.anim_slide_out_left);
                        view.startAnimation(animation);

                        Handler handle = new Handler();
                        handle.postDelayed(new Runnable() {

                            @Override
                            public void run() {

                                if (FilteredData.size() > 0)
                                {
                                    DatabaseQueries qr = new DatabaseQueries(getActivity());
                                    qr.deleteNotificationRow(FilteredData.get(position).getId());
                                    if(FilteredData.size() ==1)
                                        new GetNotificationList().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,3);
                                    else {
                                        FilteredData.remove(position);
                                        notificationAdapter.notifyDataSetChanged();
                                    }
                                }

                            }
                        }, 500);


                    }
                    else if (swipeDetector.getAction() == Action.TB) {
                        // perform any task
                    }
                    else if (swipeDetector.getAction() == Action.BT) {
                        // perform any task
                    }
                    else if (swipeDetector.getAction() == Action.None) {
                        // perform any task

                    }

                }
                else
                {
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    DatabaseQueries qr = new DatabaseQueries(getActivity());

                    if(notificationData.get(position).getNotificationtype().equalsIgnoreCase("Homework")
                            || notificationData.get(position).getNotificationtype().equalsIgnoreCase("Classwork") ||
                            notificationData.get(position).getNotificationtype().equalsIgnoreCase("HomeworkUpload")
                            || notificationData.get(position).getNotificationtype().equalsIgnoreCase("ClassworkUpload")) {

                        qr.deleteNotificationRow(notificationData.get(position).getId());

                        GetHomework(notificationData.get(position).getNotificationtype());
                    }
                    else if(notificationData.get(position).getNotificationtype().equalsIgnoreCase("Message"))
                    {
                        String uid = notificationData.get(position).getAdditionalData2();
                        String urlImage = null;
                        String userData[] = notificationData.get(position).getAdditionalData1().trim().split("@@@");
                        if(userData.length >2 )
                            urlImage = userData[2];

                        qr.updateInitiatechat(preferences.getString("SyncStd",""),preferences.getString("SyncDiv",""),userData[0],"true",uid,0,urlImage);
                        Bundle bundle = new Bundle();
                        bundle.putString("USERID", uid);
                        bundle.putString("SENDERNAME",userData[0]);
                        bundle.putString("Stand",preferences.getString("SyncStd",""));
                        bundle.putString("Divi",preferences.getString("SyncDiv",""));
                        bundle.putString("UrlImage",urlImage);

                        qr.deleteNotificationRow(notificationData.get(position).getId());

                        TeacherQueryViewFragment fragment = new TeacherQueryViewFragment();

                        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                        //bundle.putString("HomeworkList", homewrklist);
                        bundle.putString("HEADERTEXT", "Communication");
                        fragment.setArguments(bundle);
                        fragmentTransaction.addToBackStack(null);
                        fragmentTransaction.replace(R.id.frame_container, fragment);
                        fragmentTransaction.commit();
                        Singleton.setSelectedFragment(fragment);
                        Singleton.setMainFragment(fragment);

                    }
                }
            }
        });

        return rootview;
    }

        @Override
    public void onClick(View v) {

            Fragment frag=new Fragment();
            switch (v.getId())
            {
                case R.id.txtpdashinviteother:
                    frag = InviteOther("Invite to Join");
                    Singleton.setSelectedFragment(frag);
                    Singleton.setMainFragment(frag);
                    break;

                case R.id.txtpdashhomework:
                    frag =GetHomework("Homework");
                    Singleton.setSelectedFragment(frag);
                    Singleton.setMainFragment(frag);
                    break;
                case R.id.txtpdashviewstar:
                    Toast.makeText(getActivity(), "In Progress..", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.txtpdashtimetable:
                    Toast.makeText(getActivity(), "In Progress..", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.txtpdashalert:
                    Toast.makeText(getActivity(), "In Progress..", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.txtpdashfuncenter:
                    Toast.makeText(getActivity(), "In Progress..", Toast.LENGTH_SHORT).show();
                    break;
                case R.id.txtpdashcommunication:
                    frag =Communication("Chat");
                    Singleton.setSelectedFragment(frag);
                    Singleton.setMainFragment(frag);
                    break;
                case R.id.txtpdashpublicholiday:
                    Toast.makeText(getActivity(), "In Progress..", Toast.LENGTH_SHORT).show();
                    break;

                case R.id.txtpdashpupilinfo:
                    frag =MyPupil();
                    Singleton.setSelectedFragment(frag);
                    Singleton.setMainFragment(frag);
                    break;

                case R.id.txtpdashstudentlist:
                    frag =MyClassList();
                    Singleton.setSelectedFragment(frag);
                    Singleton.setMainFragment(frag);
                    break;

                case R.id.txtpdashtrack:

                    SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    SharedPreferences.Editor edit = sharedpreferences.edit();

                    String isfirsttime = sharedpreferences.getString("Tracking","");
                    if (isfirsttime.equals("true") || isfirsttime.equals(""))
                    {
                        TrackPupil("b");
                    }
                    else
                    {
                        String drivername = sharedpreferences.getString("USERNAME","");
                        String driverid = sharedpreferences.getString("USERID","");
                        String accessToken=sharedpreferences.getString("AccessToken","");
                        String deviceid=sharedpreferences.getString("DWEVICEID","");
                        String userId=sharedpreferences.getString("StudentUserID","");
                        TrackingAsyckTaskGet obj = new TrackingAsyckTaskGet(drivername,driverid, getActivity(),accessToken,deviceid,userId,ParentDashboardFragment.this);
                        obj.execute();
                    }

                    break;

                case R.id.txtpdashclasswork:
                    frag =GetHomework("Classwork");
                    Singleton.setSelectedFragment(frag);
                    Singleton.setMainFragment(frag);
                    break;
            }
        }

    public void Controls(View v)
    {
        Typeface face= Typeface.createFromAsset(getActivity().getAssets(), "fonts/font.ttf");

        inviteother=(TextView) v.findViewById(R.id.txtpdashinviteother);
        inviteother.setTypeface(face);
        homework = (TextView) v.findViewById(R.id.txtpdashhomework);
        homework.setTypeface(face);
        viewStar = (TextView) v.findViewById(R.id.txtpdashviewstar);
        viewStar.setTypeface(face);
        timeTable = (TextView) v.findViewById(R.id.txtpdashtimetable);
        timeTable.setTypeface(face);
        funCenter = (TextView) v.findViewById(R.id.txtpdashfuncenter);
        funCenter.setTypeface(face);
        communication = (TextView) v.findViewById(R.id.txtpdashcommunication);
        communication.setTypeface(face);
        alert= (TextView) v.findViewById(R.id.txtpdashalert);
        alert.setTypeface(face);
        trackPupil = (TextView) v.findViewById(R.id.txtpdashtrack);
        trackPupil.setTypeface(face);
        publicHoliday = (TextView) v.findViewById(R.id.txtpdashpublicholiday);
        publicHoliday.setTypeface(face);
        classwork = (TextView) v.findViewById(R.id.txtpdashclasswork);
        classwork.setTypeface(face);
        mypupil = (TextView) v.findViewById(R.id.txtpdashpupilinfo);
        mypupil.setTypeface(face);
        myclass = (TextView) v.findViewById(R.id.txtpdashstudentlist);
        myclass.setTypeface(face);

        notificationList = (ListView) v.findViewById(R.id.lst_notification);
        userInfoLayout = (LinearLayout)v.findViewById(R.id.linuserlayout);
        picUser = (ImageView)v.findViewById(R.id.iv_uImage);
        nameUSer = (TextView)v.findViewById(R.id.txtuName);
        userInitials = (TextView)v.findViewById(R.id.img_user_text_image);
        textStdDiv= (TextView)v.findViewById(R.id.txtStdDiv);
    }

    // For Homework
    public Fragment MyPupil() {
        // Get Output as
        MyPupilInfoFragment fragment = new MyPupilInfoFragment();
        Bundle bundle = new Bundle();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.frame_container, fragment);
        fragmentTransaction.commit();
        return fragment;
    }
    // For Homework
    public Fragment MyClassList() {
        // Get Output as
        MyClassStudentFragment fragment = new MyClassStudentFragment();
        Singleton.setMainFragment(fragment);
        Singleton.setSelectedFragment(fragment);
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.frame_container, fragment);
        fragmentTransaction.commit();
        return fragment;
    }

    public Fragment GetHomework(String res) {
        // Get Output as
        if(res.equalsIgnoreCase("HomeworkUpload"))
        {
            res="Homework";
        }
        else if(res.equalsIgnoreCase("ClassworkUpload"))
        {
            res="Classwork";
        }
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
    public Fragment Communication(String res) {

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
        return  fragment;
    }
    public Fragment InviteOther(String res) {
        // Get Output as
        //String homewrklist = "Marathi,,lesson no 2 and 3 lesson no 2 and 3 lesson no 2 and 3,,NoImage,,20/11/2015_English,,NoText,,Image,,19/11/2015_Hindi,,hindi homework,,NoImage,,18/11/2015_History,,history homework lesson no 2 and 3,,NoImage,,17/11/2015_Math,,Math homework,,Image,,16/11/2015";
        InviteToOthersFragment fragment = new InviteToOthersFragment();
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

    // For Track My Pupil
    public void TrackPupil(String res) {
        FragmentManager fragmentManager = getFragmentManager();
        TrackingDialogBoxActivity fragment = new TrackingDialogBoxActivity();
        fragment.setCancelable(true);
        fragment.show(fragmentManager, "Dialog!");
    }

    @Override
    public void onTaskCompleted(String s) {
        try {
            //JSONObject obj = new JSONObject(s);
            if(s.equalsIgnoreCase("[]"))
            {
                Toast.makeText(getActivity(),"Server Not Responding Please Try After Some Time",Toast.LENGTH_LONG).show();
            }
            else
            {
                JSONArray locList = new JSONArray(s.toString());
                //  for(int i=0;i<locList.length();i++) {
                JSONObject obj1 = locList.getJSONObject(locList.length()-1);
                SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                String drivername = sharedpreferences.getString("USERNAME","");
                String driverid = sharedpreferences.getString("USERID","");

                Intent intent = new Intent(getActivity(), TrackShowMap.class);
                Bundle bundle = new Bundle();
                bundle.putString("USERNAME", drivername);
                bundle.putString("USERID", driverid);
                bundle.putString("LATITUDE", obj1.getString("latitude"));
                bundle.putString("LONGITUDE", obj1.getString("longitude"));
                intent.putExtras(bundle);
                startActivity(intent);

                SharedPreferences.Editor edit = sharedpreferences.edit();
                edit.putString("USERNAME", drivername);
                edit.putString("USERID", driverid);
                edit.putString("Tracking", "false");
                edit.commit();
                Singleton.setIsShowMap(true);
            }

            //  }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //Recive the result when new Message Arrives
    class MessageResultReceiver extends ResultReceiver
    {
        public MessageResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {

            if(resultCode == 1){
                getActivity().runOnUiThread(new UpdateUI("UpdateNotification"));
            }
        }
    }

    class UpdateUI implements Runnable {
        String update;

        public UpdateUI(String update) {

            this.update = update;
        }

        public void run() {

            if (update.equals("UpdateNotification")) {

                new GetNotificationList().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,2);
            }
        }
    }

    public class GetNotificationList extends AsyncTask<Integer, Void,Integer>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //showing dp
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String urlString = preferences.getString("ThumbnailID","");

            if(urlString.equals("") || urlString.equalsIgnoreCase("null"))
            {
                picUser.setVisibility(View.GONE);
                userInitials.setVisibility(View.VISIBLE);
                String name[]=nameUSer.getText().toString().split(" ");
                String fname = name[0].trim().toUpperCase().charAt(0)+"";
                if(name.length>1)
                {
                    String lname = name[1].trim().toUpperCase().charAt(0)+"";
                    userInitials.setText(fname+lname);
                }
                else
                    userInitials.setText(fname);

            }
            else
            {
                picUser.setVisibility(View.VISIBLE);
                userInitials.setVisibility(View.GONE);

                if (urlString.contains("http"))
                {
                    String newURL=new Utility().getURLImage(urlString);
                    if(!ImageStorage.checkifImageExists(newURL.split("/")[newURL.split("/").length - 1]))
                        new GetImages(newURL,picUser,userInitials,nameUSer.getText().toString(),newURL.split("/")[newURL.split("/").length-1]).execute(newURL);
                    else
                    {
                        File image = ImageStorage.getImage(newURL.split("/")[newURL.split("/").length-1]);
                        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                        Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
                        //  bitmap = Bitmap.createScaledBitmap(bitmap,parent.getWidth(),parent.getHeight(),true);
                        picUser.setImageBitmap(bitmap);
                    }
                }
                else
                {
                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    Bitmap bitmap = BitmapFactory.decodeFile(urlString, bmOptions);
                    picUser.setImageBitmap(bitmap);
                }
            }
        }


        @Override
        protected Integer doInBackground(Integer... params) {
            DatabaseQueries qr = new DatabaseQueries(getActivity());
            notificationData = qr.GetNotificationsData();
            return params[0];
        }

        @Override
        protected void onPostExecute(Integer type) {
            super.onPostExecute(type);
            if (type == 1 || type ==2) {
                if(notificationData.size()>0) {
                    FilteredData=new ArrayList<>();
                    int count=0;
                    for (int i=0;i<notificationData.size();i++)
                    {
                        if (notificationData.get(i).getNotificationtype().equalsIgnoreCase("Message"))
                        {
                            count++;
                            if (count==1)
                                FilteredData.add(notificationData.get(i));
                        }
                        else
                        {
                            FilteredData.add(notificationData.get(i));
                        }
                    }

                    notificationList.setVisibility(View.VISIBLE);
                    userInfoLayout.setVisibility(View.GONE);
                    notificationAdapter = new TeacherNotificationListAdapter(getActivity(), FilteredData);
                    notificationList.setAdapter(notificationAdapter);
                    //Utility.setListViewHeightBasedOnChildren(notificationList);
                    notificationAdapter.notifyDataSetChanged();
                }
                else
                {
                    notificationList.setVisibility(View.GONE);
                    userInfoLayout.setVisibility(View.VISIBLE);
                    Typeface face= Typeface.createFromAsset(getActivity().getAssets(), "fonts/font.ttf");
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    nameUSer.setText(preferences.getString("DisplayName", ""));
                    nameUSer.setTypeface(face);
                    textStdDiv.setText(preferences.getString("SyncStd", "")+"   "+preferences.getString("SyncDiv", ""));
                    textStdDiv.setTypeface(face);
                    String urlString = preferences.getString("ThumbnailID","");
                    Log.d("Image URL", urlString);

                    if(urlString.equals("") || urlString.equalsIgnoreCase("null"))
                    {
                        picUser.setVisibility(View.GONE);
                        userInitials.setVisibility(View.VISIBLE);
                        String name[]=nameUSer.getText().toString().split(" ");
                        String fname = name[0].trim().toUpperCase().charAt(0)+"";
                        if(name.length>1)
                        {
                            String lname = name[1].trim().toUpperCase().charAt(0)+"";
                            userInitials.setText(fname+lname);
                        }
                        else
                            userInitials.setText(fname);

                    }
                    else
                    {
                        picUser.setVisibility(View.VISIBLE);
                        userInitials.setVisibility(View.GONE);
                        if (urlString.contains("http"))
                        {
                            String newURL=new Utility().getURLImage(urlString);
                            if(!ImageStorage.checkifImageExists(newURL.split("/")[newURL.split("/").length - 1]))
                                new GetImages(newURL,picUser,userInitials,nameUSer.getText().toString(),newURL.split("/")[newURL.split("/").length-1]).execute(newURL);
                            else
                            {
                                File image = ImageStorage.getImage(newURL.split("/")[newURL.split("/").length - 1]);
                                BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                                Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
                                //  bitmap = Bitmap.createScaledBitmap(bitmap,parent.getWidth(),parent.getHeight(),true);
                                picUser.setImageBitmap(bitmap);
                            }
                        }
                        else
                        {
                            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                            Bitmap bitmap = BitmapFactory.decodeFile(urlString, bmOptions);
                            picUser.setImageBitmap(bitmap);
                        }
                    }
                }
            }
            else  if (type == 3) {
                notificationList.setVisibility(View.GONE);
                userInfoLayout.setVisibility(View.VISIBLE);
                Typeface face= Typeface.createFromAsset(getActivity().getAssets(), "fonts/font.ttf");
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                nameUSer.setText(preferences.getString("DisplayName", ""));
                nameUSer.setTypeface(face);
                textStdDiv.setText(preferences.getString("SyncStd", "")+"   "+preferences.getString("SyncDiv", ""));
                textStdDiv.setTypeface(face);
                String urlString = preferences.getString("ThumbnailID","");
                Log.d("Image URL", urlString);

                if(urlString.equals("") || urlString.equalsIgnoreCase("null"))
                {
                    picUser.setVisibility(View.GONE);
                    userInitials.setVisibility(View.VISIBLE);
                    String name[]=nameUSer.getText().toString().split(" ");
                    String fname = name[0].trim().toUpperCase().charAt(0)+"";
                    if(name.length>1)
                    {
                        String lname = name[1].trim().toUpperCase().charAt(0)+"";
                        userInitials.setText(fname+lname);
                    }
                    else
                        userInitials.setText(fname);

                }
                else
                {
                    picUser.setVisibility(View.VISIBLE);
                    userInitials.setVisibility(View.GONE);
                    if (urlString.contains("http"))
                    {
                        String newURL=new Utility().getURLImage(urlString);
                        if(!ImageStorage.checkifImageExists(newURL.split("/")[newURL.split("/").length - 1]))
                            new GetImages(newURL,picUser,userInitials,nameUSer.getText().toString(),newURL.split("/")[newURL.split("/").length-1]).execute(newURL);
                        else
                        {
                            File image = ImageStorage.getImage(newURL.split("/")[newURL.split("/").length - 1]);
                            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                            Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(),bmOptions);
                            //  bitmap = Bitmap.createScaledBitmap(bitmap,parent.getWidth(),parent.getHeight(),true);
                            picUser.setImageBitmap(bitmap);
                        }
                    }
                    else
                    {
                        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                        Bitmap bitmap = BitmapFactory.decodeFile(urlString, bmOptions);
                        picUser.setImageBitmap(bitmap);
                    }
                }
            }
        }
    }
}
