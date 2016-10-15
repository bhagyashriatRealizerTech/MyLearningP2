package realizer.com.schoolgenieparent.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import realizer.com.schoolgenieparent.Notification.NotificationModel;
import realizer.com.schoolgenieparent.Utils.ImageStorage;
import realizer.com.schoolgenieparent.Utils.OnTaskCompleted;
import realizer.com.schoolgenieparent.Utils.Singleton;
import realizer.com.schoolgenieparent.Utils.StoreBitmapImages;
import realizer.com.schoolgenieparent.Utils.Utility;
import realizer.com.schoolgenieparent.backend.DALMyPupilInfo;
import realizer.com.schoolgenieparent.backend.DALQueris;
import realizer.com.schoolgenieparent.backend.DatabaseQueries;
import realizer.com.schoolgenieparent.exceptionhandler.ExceptionHandler;
import realizer.com.schoolgenieparent.homework.autosyncasynctask.ClasswrokAutoAsyncTask;
import realizer.com.schoolgenieparent.homework.autosyncasynctask.HomewrokAutoAsyncTask;
import realizer.com.schoolgenieparent.homework.autosyncasynctask.UploadClassworkAutoAsyncTask;
import realizer.com.schoolgenieparent.homework.autosyncasynctask.UploadHomeworkAutoAsyncTask;
import realizer.com.schoolgenieparent.homework.backend.DALHomework;
import realizer.com.schoolgenieparent.homework.model.ParentHomeworkListModel;
import realizer.com.schoolgenieparent.homework.model.TeacherHomeworkModel;
import realizer.com.schoolgenieparent.queue.QueueListModel;

public class AutoSyncService extends Service implements OnTaskCompleted {
    SharedPreferences sharedpreferences;
    String student;
    int roll_no;
    DatabaseQueries qr;
    DALMyPupilInfo DAP;
    DALQueris qrt;
    int qid;
    boolean SyncHomeworkDownload=true;
    boolean SyncClassworkDownload=false;
    String type;
    int id;
    String UserData[]=new String[10];
    String currentDate="";
    ArrayList<String> subjects;

    public AutoSyncService() {
    }
    @Override
    public void onCreate() {
        super.onCreate();
        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler(getApplicationContext()));
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("Test", "onStartCommand start");
        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(this);
        student = sharedpreferences.getString("UidName", "");

        qr = new DatabaseQueries(this);

        int roll_min=60000*roll_no;
        int total_min=3600000+roll_min;
        Timer timer = new Timer();

        timer.scheduleAtFixedRate(new AutoSyncServerDataTrack(), 10000, total_min);

        return START_NOT_STICKY;
    }

    class AutoSyncServerDataTrack extends TimerTask
    {
        @Override
        public void run() {
            if(isConnectingToInternet()) {
                initializeTimerTask();
            }
        }
    }

    public void  initializeTimerTask() {
        Log.d("AutoSync", "Start");
        //Toast.makeText(TestAnnouncementService.this,"Auto Sync Start",Toast.LENGTH_LONG).show();
        qr = new DatabaseQueries(this);
        DAP=new DALMyPupilInfo(this);
        qrt = new DALQueris(this);

        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(this);
        subjects = qrt.GetAllSub();

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy");
        currentDate = df.format(calendar.getTime());
        UserData =  DAP.GetSTDDIVData();

       /* homework and classwork
        uploading*/
        ArrayList<QueueListModel> lst = qr.GetQueueData();
        Log.d("TIMER", " " + Calendar.getInstance().getTime() + ": " + lst.size());
        if(lst.size()>0)
        {
            //Toast.makeText(this,"Sync Start...",Toast.LENGTH_SHORT).show();
            for(int i=0;i<lst.size();i++)
            {
                id = lst.get(i).getId();
                type = lst.get(i).getType();
                if(type.equals("Homework"))
                {

                    TeacherHomeworkModel o = qr.GetHomework(id);
                    if(o.getWork().equalsIgnoreCase("Homework")) {
                        UploadHomeworkAutoAsyncTask obj = new UploadHomeworkAutoAsyncTask(o, AutoSyncService.this, AutoSyncService.this, "false");
                        obj.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                    }

                }
                else  if(type.equals("Classwork"))
                {
                    TeacherHomeworkModel o = qr.GetHomework(id);
                    if(o.getWork().equalsIgnoreCase("Classwork"))
                    {
                        UploadClassworkAutoAsyncTask obj = new UploadClassworkAutoAsyncTask(o, AutoSyncService.this, AutoSyncService.this, "false");
                        obj.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                    }
                }
            }
        }

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
                    HomewrokAutoAsyncTask obj = new HomewrokAutoAsyncTask(home, AutoSyncService.this, AutoSyncService.this);
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
                    ClasswrokAutoAsyncTask obj = new ClasswrokAutoAsyncTask(home, AutoSyncService.this, AutoSyncService.this);
                    obj.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                }

                SyncClassworkDownload=false;
            }
        }
    }

    @Override
    public void onTaskCompleted(String s) {
        Log.d("String", s);

        String[] onTaskString=s.split("@@@");

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
                        String hwUUID= String.valueOf(UUID.randomUUID());
                        long n = qr.insertHomework(givenby, subject, hwdate, text.toString(), img.toString(),std, division, onTaskString[1],hwUUID);
                        if (n>0)
                        {
                            Toast.makeText(this, "Homework Downloaded Successfully...", Toast.LENGTH_LONG).show();
                            Calendar calendar = Calendar.getInstance();
                            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
                            String date = df.format(calendar.getTime());

                            NotificationModel notification1 = new NotificationModel();
                            notification1.setNotificationId(2);
                            notification1.setNotificationDate(date);
                            notification1.setNotificationtype("Homework");
                            notification1.setMessage(subject);
                            notification1.setIsRead("false");
                            notification1.setAdditionalData1(givenby);
                            qr.InsertNotification(notification1);
                            if(Singleton.getResultReceiver() != null)
                                Singleton.getResultReceiver().send(1,null);
                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
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
                        String hwUUID= String.valueOf(UUID.randomUUID());
                        long n = qr.insertHomework(givenby, subject, hwdate, text.toString(), img.toString(),std, division, onTaskString[1],hwUUID);
                        if (n>0)
                        {
                            Toast.makeText(this, "Classwork Downloaded Successfully...", Toast.LENGTH_LONG).show();
                            Calendar calendar = Calendar.getInstance();
                            SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
                            String date = df.format(calendar.getTime());

                            NotificationModel notification1 = new NotificationModel();
                            notification1.setNotificationId(3);
                            notification1.setNotificationDate(date);
                            notification1.setNotificationtype("Classwork");
                            notification1.setMessage(subject);
                            notification1.setIsRead("false");
                            notification1.setAdditionalData1(givenby);
                            qr.InsertNotification(notification1);
                            if(Singleton.getResultReceiver() != null)
                                Singleton.getResultReceiver().send(1,null);
                        }
                    }
                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        else {
            s = s.replace("\"", "");
            if (onTaskString[0].equals("success")) {
                long n = qr.deleteQueueRow(Integer.valueOf(onTaskString[2]), onTaskString[3]);
                TeacherHomeworkModel homeworkObj = new TeacherHomeworkModel();
                if (n > 0) {
                    n = 0;
                    homeworkObj = qr.GetHomework(Integer.valueOf(onTaskString[2]));
                    n = qr.updateHomeworkSyncFlag(homeworkObj);
                    NotificationModel obj = new NotificationModel();
                    obj.setNotificationId(homeworkObj.getHid());
                    obj.setNotificationDate(homeworkObj.getHwDate()+"Upload");
                    obj.setNotificationtype(homeworkObj.getWork());
                    obj.setMessage("Uploaded Successfully for");
                    obj.setIsRead("false");
                    obj.setAdditionalData2("");
                    obj.setAdditionalData1(homeworkObj.getStd() + "@@@" + homeworkObj.getDiv() + "@@@" +
                            homeworkObj.getSubject());
                    qr.InsertNotification(obj);
                    Bundle b = new Bundle();
                    b.putInt("NotificationId", homeworkObj.getHid());
                    b.putString("NotificationDate", homeworkObj.getHwDate());
                    b.putString("NotificationType", homeworkObj.getWork()+"Upload");
                    b.putString("NotificationMessage", "Uploaded Successfully for");
                    b.putString("IsNotificationread", "false");
                    b.putString("AdditionalData1", homeworkObj.getStd() + "@@@" + homeworkObj.getDiv() + "@@@" +
                            homeworkObj.getSubject());
                    b.putString("AdditionalData2", "");

                    if (Singleton.getResultReceiver() != null)
                        Singleton.getResultReceiver().send(1, null);
                }
            }
        }
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
}
