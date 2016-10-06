package realizer.com.schoolgenieparent.service;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import realizer.com.schoolgenieparent.Utils.Config;
import realizer.com.schoolgenieparent.Utils.ImageStorage;
import realizer.com.schoolgenieparent.Utils.OnTaskCompleted;
import realizer.com.schoolgenieparent.Utils.Singleton;
import realizer.com.schoolgenieparent.Utils.StoreBitmapImages;
import realizer.com.schoolgenieparent.Utils.Utility;
import realizer.com.schoolgenieparent.backend.DALMyPupilInfo;
import realizer.com.schoolgenieparent.backend.DALQueris;
import realizer.com.schoolgenieparent.backend.DatabaseQueries;
import realizer.com.schoolgenieparent.homework.asynctask.ClassworkAsyncTaskPost;
import realizer.com.schoolgenieparent.homework.asynctask.HomeworkAsyncTaskPost;
import realizer.com.schoolgenieparent.homework.asynctask.TeacherClassworkAsyncTaskPost;
import realizer.com.schoolgenieparent.homework.asynctask.TeacherHomeworkAsyncTaskPost;
import realizer.com.schoolgenieparent.homework.backend.DALHomework;
import realizer.com.schoolgenieparent.homework.model.ParentHomeworkListModel;
import realizer.com.schoolgenieparent.homework.model.TeacherHomeworkModel;
import realizer.com.schoolgenieparent.queue.QueueListModel;

/**
 * Created by Win on 04-10-2016.
 */
public class ManualSyncupService extends Service implements OnTaskCompleted {

    SharedPreferences sharedpreferences;
    String student;
    DatabaseQueries qr;
    DALMyPupilInfo DAP;
    DALQueris qrt;
    String type;
    int id;
    boolean SyncHomeworkDownload=true;
    boolean SyncClassworkDownload=false;
    String UserData[]=new String[10];
    String currentDate="";
    ArrayList<String> subjects;
    Handler handler;
    ProgressDialog dialog;
    AlertDialog.Builder adbdialog;
    ArrayList<QueueListModel> quelist;
    Context mContext;
    static int count=0;
    static int counter=0;
    static int getClasswork=0;
    static int getHomework=0;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("ManualSyncService", "Stop");
        // Toast.makeText(this, "Service Destroy", Toast.LENGTH_LONG).show();
    }



    @Override
    public void onLowMemory() {
        super.onLowMemory();
        //Toast.makeText(this, "Service LowMemory", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(this);
        student = sharedpreferences.getString("UidName", "");
        qr = new DatabaseQueries(this);
        Log.d("ManualSyncService", "Start");
        handler = new Handler();
        BackgroundThread background = new BackgroundThread();
        background.start();
        count=0;
        counter=0;
        getClasswork =0;
        getHomework=0;
        return START_NOT_STICKY;
    }

    private class BackgroundThread extends Thread {
        @Override
        public void run() {
            super.run();
            if(isConnectingToInternet())
            {
                Syncdata();
            }
        }
    }


    @Override
    public void onTaskCompleted(String s) {

        Log.d("String", s);
        //s =s.replace("\"","");
        final String[] onTaskString=s.split("@@@");

        if (onTaskString[1].equalsIgnoreCase("Homework"))
        {
            DALHomework dla = new DALHomework(this);
            JSONObject rootObj = null;
            try {
                rootObj = new JSONObject(onTaskString[0]);
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
                    String[] IMG=new String[img.length()];
                    String[] dateArr=hwdate.split("/");
                    String newDate=dateArr[1]+"/"+dateArr[0]+"/"+dateArr[2];
                    //ArrayList<ParentHomeworkListModel> results=dh.GetHomeworkAllInfoData(hwdate);
                    ArrayList<TeacherHomeworkModel> results = qr.GetHomeworkData(hwdate, onTaskString[1], std, division);
                    boolean isPresent=false;

                    for (int j=0;j<results.size();j++)
                    {
                        if (!text.toString().equals("") )
                        {
                            if (results.get(j).getHwTxtLst().equalsIgnoreCase(text.toString()))
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
                        //n=dla.insertHomeworkInfo(schoolCode, std, division, givenby, hwdate, img.toString(), text.toString(), subject,onTaskString[1],student);
                        long n = qr.insertHomework(givenby, subject, hwdate, text.toString(), img.toString(),std, division, onTaskString[1]);
                        if (n>0)
                        {
                            //Toast.makeText(this, "Homework Downloaded Successfully...", Toast.LENGTH_LONG).show();

                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            getHomework =1;
        }
        else if (onTaskString[1].equalsIgnoreCase("Classwork"))
        {
            DALHomework dla = new DALHomework(this);
            JSONObject rootObj = null;
            try {
                rootObj = new JSONObject(onTaskString[0]);
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
                    String[] IMG=new String[img.length()];
                    //ArrayList<ParentHomeworkListModel> results=dh.GetHomeworkAllInfoData(hwdate);
                    String[] dateArr=hwdate.split("/");
                    String newDate=dateArr[1]+"/"+dateArr[0]+"/"+dateArr[2];
                    ArrayList<TeacherHomeworkModel> results = qr.GetHomeworkData(hwdate, onTaskString[1], std, division);
                    boolean isPresent=false;

                    for (int j=0;j<results.size();j++)
                    {
                        if (!text.toString().equals("") )
                        {
                            if (results.get(j).getHwTxtLst().equalsIgnoreCase(text.toString()))
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

                        //long n=0;
                        //n=dla.insertHomeworkInfo(schoolCode, std, division, givenby, hwdate, img.toString(), text.toString(), subject, onTaskString[1], student);

                        long n = qr.insertHomework(givenby, subject, hwdate, text.toString(), img.toString(),std, division, onTaskString[1]);
                        if (n>0)
                        {
                          //  Toast.makeText(this, "Classwork Downloaded Successfully...", Toast.LENGTH_LONG).show();

                        }
                    }
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }

            getClasswork =1;
        }
        else
        {
            if(onTaskString[0].replace("\"","").equals("success"))
            {
                long n = qr.deleteQueueRow(Integer.valueOf(onTaskString[2]),onTaskString[3]);
                if(n>0) {
                    n = -1;
                    n = qr.updateHomeworkSyncFlag(qr.GetHomework(Integer.valueOf(onTaskString[2])));
                }
                    count =1;

            }
            else {
                Config.alertDialog(Singleton.getContext(), "Network Error", "Server Not Responding");
                //Toast.makeText(this, "Server not responding please wait...", Toast.LENGTH_SHORT).show();
            }
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (counter==quelist.size() && getHomework==1 && getClasswork==1)
                {

                    Config.alertDialog(Singleton.getContext(), "Manual Sync", "Sync Completed Successfully");
                }
                else if(count==1)
                {
                    count=1;
                    counter++;
                }
               /* if (onTaskString[1].equalsIgnoreCase("Homework"))
                    Config.alertDialog(Singleton.getContext(), "Manual Sync", "Sync Downloaded Successfully");*/
                if(Singleton.getManualserviceIntent() != null)
                    stopService(Singleton.getManualserviceIntent());
            }
        });


    }

    private void runOnUiThread(Runnable runnable) {
        handler.post(runnable);
    }

    public void Syncdata()
    {
        qr = new DatabaseQueries(this);
        DAP=new DALMyPupilInfo(this);
        qrt = new DALQueris(this);

        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(this);
        subjects = qrt.GetAllSub();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        currentDate = df.format(calendar.getTime());
        UserData =  DAP.GetSTDDIVData();

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                adbdialog = new AlertDialog.Builder(Singleton.getContext());
                adbdialog.setTitle("Manual Sync");
                adbdialog.setMessage("Sync will be Performed in Background, you will be Notified once sync is Completed.");
                adbdialog.setIcon(android.R.drawable.ic_dialog_info);
                adbdialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        final ArrayList<QueueListModel> lst = qr.GetQueueData();
                        Log.d("TIMER", " " + Calendar.getInstance().getTime() + ": " + lst.size());
                        for(int i=0;i<lst.size();i++)
                        {
                            id = lst.get(i).getId();
                            type = lst.get(i).getType();
                            if(type.equals("Homework"))
                            {

                                TeacherHomeworkModel o = qr.GetHomework(id);
                                if(o.getWork().equalsIgnoreCase("Homework")) {
                                    TeacherHomeworkAsyncTaskPost obj = new TeacherHomeworkAsyncTaskPost(o, ManualSyncupService.this, ManualSyncupService.this, "false");
                                    obj.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                                }

                            }
                            else  if(type.equals("Classwork"))
                            {
                                TeacherHomeworkModel o = qr.GetHomework(id);
                                if(o.getWork().equalsIgnoreCase("Classwork"))
                                {
                                    TeacherClassworkAsyncTaskPost obj = new TeacherClassworkAsyncTaskPost(o, ManualSyncupService.this, ManualSyncupService.this, "false");
                                    obj.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                                }
                            }
                        }

                        downloadData();
                    } });

                adbdialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        stopService(Singleton.getManualserviceIntent());
                    } });
                adbdialog.show();

            }
        });


       /*

       *//* if(lst.size()>0)
        {*//*
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    adbdialog = new AlertDialog.Builder(Singleton.getContext());
                    adbdialog.setTitle("Manual Sync");
                    adbdialog.setMessage("Sync will be Performed in Background, you will be Notified once sync is Completed.");
                    //adbdialog.setIcon(android.R.drawable.ic_dialog_info);
                    adbdialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            final ArrayList<QueueListModel> lst = qr.GetQueueData();
                            Log.d("TIMER", " " + Calendar.getInstance().getTime() + ": " + lst.size());
                            for(int i=0;i<lst.size();i++)
                            {
                                id = lst.get(i).getId();
                                type = lst.get(i).getType();
                                if(type.equals("Homework"))
                                {

                                    TeacherHomeworkModel o = qr.GetHomework(id);
                                    if(o.getWork().equalsIgnoreCase("Homework")) {
                                        TeacherHomeworkAsyncTaskPost obj = new TeacherHomeworkAsyncTaskPost(o, ManualSyncupService.this, ManualSyncupService.this, "false");
                                        obj.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                                    }

                                }
                                else  if(type.equals("Classwork"))
                                {
                                    TeacherHomeworkModel o = qr.GetHomework(id);
                                    if(o.getWork().equalsIgnoreCase("Classwork"))
                                    {
                                        TeacherClassworkAsyncTaskPost obj = new TeacherClassworkAsyncTaskPost(o, ManualSyncupService.this, ManualSyncupService.this, "false");
                                        obj.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                                    }
                                }
                            }

                            downloadData();

                        } });


                    adbdialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            stopService(Singleton.getManualserviceIntent());
                        } });
                    adbdialog.show();
                }
            });
            //Toast.makeText(this,"Sync Start...",Toast.LENGTH_SHORT).show();

       *//* }
        else
        {
            *//**//*runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Config.alertDialog(Singleton.getContext(), "Manual Sync", "There is No Data to Sync");
                    //Toast.makeText(Singlton.getContext(), "No Data to Sync", Toast.LENGTH_SHORT).show();

                }
            });*//**//*
            downloadData();

        }*/
    }

    public boolean isConnectingToInternet(){

        ConnectivityManager connectivity =
                (ConnectivityManager) getApplicationContext().getSystemService(
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

    public void downloadData()
    {
         /* homework and classwork
        downloading*/
        for (int i=1;i<=2;i++)
        {
            if (SyncHomeworkDownload)
            {
                //homework
                for (int k = 0; k < subjects.size(); k++) {

                    ParentHomeworkListModel home = new ParentHomeworkListModel();
                    home.setSchoolcode(UserData[2]);
                    home.setStandard(UserData[0]);
                    home.setDivision(UserData[1]);
                    home.setHwdate(currentDate);
                    home.setSubject(subjects.get(k));
                    HomeworkAsyncTaskPost obj = new HomeworkAsyncTaskPost(home, ManualSyncupService.this, ManualSyncupService.this);
                    obj.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                }

                SyncHomeworkDownload=false;
                SyncClassworkDownload=true;
            }
            else if (SyncClassworkDownload)
            {
                //classwork
                for (int k = 0; k < subjects.size(); k++) {

                    ParentHomeworkListModel home = new ParentHomeworkListModel();
                    home.setSchoolcode(UserData[2]);
                    home.setStandard(UserData[0]);
                    home.setDivision(UserData[1]);
                    home.setHwdate(currentDate);
                    home.setSubject(subjects.get(k));
                    ClassworkAsyncTaskPost obj = new ClassworkAsyncTaskPost(home, ManualSyncupService.this, ManualSyncupService.this);
                    obj.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                }

                SyncClassworkDownload=false;
            }
        }
    }
}

