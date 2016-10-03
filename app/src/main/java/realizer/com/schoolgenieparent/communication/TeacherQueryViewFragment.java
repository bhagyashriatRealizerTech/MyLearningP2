package realizer.com.schoolgenieparent.communication;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.ResultReceiver;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.UUID;

import realizer.com.schoolgenieparent.DrawerActivity;
import realizer.com.schoolgenieparent.R;
import realizer.com.schoolgenieparent.Utils.Config;
import realizer.com.schoolgenieparent.Utils.OnBackPressFragment;
import realizer.com.schoolgenieparent.Utils.OnTaskCompleted;
import realizer.com.schoolgenieparent.Utils.Singleton;
import realizer.com.schoolgenieparent.backend.DatabaseQueries;
import realizer.com.schoolgenieparent.communication.adapter.TeacherQueryMessageCenterListAdapter;
import realizer.com.schoolgenieparent.communication.asynctask.TeacherQueryAsyncTaskPost;
import realizer.com.schoolgenieparent.communication.model.TeacherQuerySendModel;
import realizer.com.schoolgenieparent.communication.model.TeacherQueryViewListModel;
import realizer.com.schoolgenieparent.exceptionhandler.ExceptionHandler;


/**
 * Created by Win on 11/26/2015.
 */
public class TeacherQueryViewFragment extends Fragment implements AbsListView.OnScrollListener,OnTaskCompleted,OnBackPressFragment {

    DatabaseQueries qr;
    Timer timer;
    Parcelable state;
    int currentPosition;
    ListView lsttname;
    int qid;
    int mCurrentX ;
    int  mCurrentY;
    TextView send;
    EditText msg;
    int lstsize;
    String stdC;
    String divC;
    String sname,uuid;
    String htext,schoolcode, std, div,userId,username;
    //TextView sendername;
    TeacherQueryMessageCenterListAdapter adapter;
    MessageResultReceiver resultReceiver;
    Context context;
    String urlImag="",uidstud;
    String uid,thumbnailurl;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getActivity()));
        //StrictMode for smooth list scroll
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().detectDiskReads().detectDiskWrites().detectNetwork().penaltyLog().build());
        View rootView = inflater.inflate(R.layout.teacher_queryview_layout, container, false);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE| WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        qr =new DatabaseQueries(getActivity());
        qid=0;

        lsttname = (ListView) rootView.findViewById(R.id.lstviewquery);
        msg = (EditText) rootView.findViewById(R.id.edtmsgtxt);
        send = (TextView) rootView.findViewById(R.id.btnSendText);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        stdC=preferences.getString("SyncStd", "");
        divC = preferences.getString("SyncDiv", "");
        uidstud=preferences.getString("UidName","");

        schoolcode= preferences.getString("SchoolCode","");
        std=preferences.getString("SyncStd","");
        div=         preferences.getString("SyncDiv","");
        userId=     preferences.getString("UidName","");
        username=   preferences.getString("Firstname","");
        // sendername = (TextView) rootView.findViewById(R.id.txtnameq);
        Bundle b = getArguments();
        //sname = b.getString("HEADERTEXT");
        htext=b.getString("HEADERTEXT");
        urlImag=b.getString("UrlImage");

        ((DrawerActivity) getActivity()).getSupportActionBar().setTitle(Config.actionBarTitle(htext, getActivity()));
        ((DrawerActivity) getActivity()).getSupportActionBar().show();
        // sendername.setText(sname);

        ArrayList<TeacherQueryViewListModel> teachernames = GetQuery();
        lstsize = teachernames.size();
        if (teachernames.size() > 0)
        {
            adapter = new TeacherQueryMessageCenterListAdapter(getActivity(),teachernames);
            lsttname.setAdapter(adapter);
            lsttname.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
            //lsttname.setFastScrollEnabled(true);
            //lsttname.setScrollY(lsttname.getCount());
            lsttname.setSelection(lsttname.getCount() - 1);
            //lsttname.smoothScrollToPosition(lsttname.getCount());
            lsttname.setOnScrollListener(this);
            lstsize =  teachernames.size();
        }

        Singleton obj = Singleton.getInstance();
        resultReceiver = new MessageResultReceiver(null);
        obj.setResultReceiver(resultReceiver);



        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(msg.getText().length()!=0)
                {
//                    Bundle b = getArguments();
//                    String uidstud = b.getString("USERID");

                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy kk:mm:ss");
                    String date = df.format(calendar.getTime());
                    Date sendDate =  new Date();
                    try {
                        sendDate = df.parse(date);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    uuid= UUID.randomUUID().toString();
                    SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                    String stud = sharedpreferences.getString("UidName", "");

                    long n = qr.insertQuery(uuid,userId, username, msg.getText().toString(),urlImag, date, "true",sendDate);
                    if (n > 0) {
                        // Toast.makeText(getActivity(), "Query Inserted Successfully", Toast.LENGTH_SHORT).show();
                        n = -1;

                        qid = qr.getQueryId();
                        n = qr.insertQueue(qid, "Query", "1", date);
                        if (n > 0) {
                            //Toast.makeText(getActivity(), "Queue Inserted Successfully", Toast.LENGTH_SHORT).show();
                            n = -1;

                            msg.setText("");
                            if(isConnectingToInternet()) {
                                TeacherQuerySendModel obj = qr.GetQuery(qid);
                                TeacherQueryAsyncTaskPost asyncobj = new TeacherQueryAsyncTaskPost(obj,getActivity(), TeacherQueryViewFragment.this);
                                asyncobj.execute();
                            }
                            else
                            {
                                resultReceiver.send(200,null);
                            }

                        }


                    }
                    Log.d("DIFICULTARR", uidstud);
                }
            }
        });

        return rootView;
    }


    private ArrayList<TeacherQueryViewListModel> GetQuery()
    {

        Bundle b = this.getArguments();
        String uid = b.getString("USERID");
        ArrayList<TeacherQueryViewListModel> results = new ArrayList<>();
        String tp="AM";
        ArrayList<TeacherQuerySendModel> qlst = qr.GetQueuryData(uid);

        for(int i=0;i<qlst.size();i++)
        {
            TeacherQuerySendModel obj = qlst.get(i);
            TeacherQueryViewListModel tDetails = new TeacherQueryViewListModel();
            String datet[] = obj.getSentTime().split(" ");
            tDetails.setSenddate(datet[0]);
            String time[] = datet[1].split(":");
            int t1 = Integer.valueOf(time[0]);
            if(t1>12)
            {
                int t2 = t1-12;
                tp = "PM";
                tDetails.setTime(""+t2+":"+time[1]+" "+tp);
            }
            else
            {
                tp = "AM";
                tDetails.setTime(time[0]+":"+time[1]+" "+tp);
            }

            if(uid.equals(obj.getFromUserId()))
                tDetails.setFlag("T");
            else
                tDetails.setFlag("P");
            tDetails.setMsg(obj.getText());
            tDetails.setTname(obj.getFromUserName());
            tDetails.setProfileImage(obj.getProfUrl());
            results.add(tDetails);
            Log.d("MSGTXT", obj.getText() + "  " + obj.getSentTime());

        }

        return results;
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

        mCurrentX = view.getScrollX();
        mCurrentY = view.getScrollY();
        currentPosition = lsttname.getSelectedItemPosition();
        Log.d("Position", "" + currentPosition);

    }


    @Override
    public void onTaskCompleted(String s) {
        if(s.equals("trueQuery"))
        {
            long n = qr.deleteQueueRow(qid, "Query");

            if(n>0)
            {
                // Toast.makeText(getActivity(), "Queue deleted Successfully", Toast.LENGTH_SHORT).show();
                TeacherQuerySendModel o = qr.GetQuery(qid);
                n=-1;

                n = qr.updateQurySyncFlag(o);

                if(n>0)
                {
                    // Toast.makeText(getActivity(), "Query updated Successfully", Toast.LENGTH_SHORT).show();
                    msg.setText("");
                    resultReceiver.send(200, null);
                }

            }
        }

    }


    public boolean isConnectingToInternet(){

        ConnectivityManager connectivity =
                (ConnectivityManager) getActivity().getSystemService(
                        Context.CONNECTIVITY_SERVICE);
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

    class UpdateUI implements Runnable {
        String update;

        public UpdateUI(String update) {

            this.update = update;
        }

        public void run() {

            if(update.equals("RecieveMessage")) {

//                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
//                String uid1=preferences.getString("ReceiverId", "");
//                String recsname=preferences.getString("ReceiverName","");
//                String receiveUrl=preferences.getString("ReceiverUrl","");
////                Bundle b1 = getArguments();
////                b1.putString("USERID",uid1);


                ArrayList<TeacherQueryViewListModel> teachernames = GetQuery();
                Log.d("SIZEOFLST", "" + teachernames.size());
                if(teachernames.size()>0)
                {
                    Bundle b = getArguments();
//                    String uid= b.getString("USERID");
//                    String sname = b.getString("SENDERNAME");
//                    String thumbnailurl =  b.getString("UrlImage");

                    qr.updateInitiatechat(stdC,divC,sname,"true", uid,0,thumbnailurl);

                    adapter = new TeacherQueryMessageCenterListAdapter(getActivity(), teachernames);
                    lsttname.setAdapter(adapter);
                    lsttname.setFastScrollEnabled(true);
                    lsttname.setScrollY(lsttname.getCount());
                    lsttname.setSelection(lsttname.getCount() - 1);
                    lsttname.smoothScrollToPosition(lsttname.getCount());
                    lstsize =  teachernames.size();

                }
            }

            else if(update.equals("SendMessageMessage")) {
                ArrayList<TeacherQueryViewListModel> teachernames = GetQuery();
                Log.d("SIZEOFLST", "" + teachernames.size());
                if(teachernames.size() !=lstsize)
                {
                    adapter = new TeacherQueryMessageCenterListAdapter(getActivity(), teachernames);
                    lsttname.setAdapter(adapter);
                    lsttname.setFastScrollEnabled(true);
                    lsttname.setScrollY(lsttname.getCount());
                    lsttname.setSelection(lsttname.getCount()-1);
                    lsttname.smoothScrollToPosition(lsttname.getCount());
                    lstsize =  teachernames.size();
                    Singleton.setMessageCenter(null);
                }
            }
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

            if(resultCode == 100){
                getActivity().runOnUiThread(new UpdateUI("RecieveMessage"));
                uid=resultData.getString("ReceiverId","");
                thumbnailurl=resultData.getString("ReceiverUrl","");
            }
            if(resultCode == 200){
                getActivity().runOnUiThread(new UpdateUI("SendMessageMessage"));
            }

        }
    }
}
