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
import realizer.com.schoolgenieparent.ProfilePicAsyncTaskPost;
import realizer.com.schoolgenieparent.Utils.ImageStorage;
import realizer.com.schoolgenieparent.Utils.OnTaskCompleted;
import realizer.com.schoolgenieparent.Utils.Singleton;
import realizer.com.schoolgenieparent.Utils.StoreBitmapImages;
import realizer.com.schoolgenieparent.Utils.Utility;
import realizer.com.schoolgenieparent.backend.DALMyPupilInfo;
import realizer.com.schoolgenieparent.backend.DALQueris;
import realizer.com.schoolgenieparent.backend.DatabaseQueries;
import realizer.com.schoolgenieparent.exceptionhandler.ExceptionAsyncTaskPost;
import realizer.com.schoolgenieparent.exceptionhandler.ExceptionHandler;
import realizer.com.schoolgenieparent.exceptionhandler.ExceptionModel;
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
                else  if(type.equals("ProfilePic") && id==1010)
                {
                    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(AutoSyncService.this);
                    String oldPath=preferences.getString("ThumbnailID","");
                    String newPath=preferences.getString("NewThumbnailID","");
                    String bitmapImg=preferences.getString("ProfilePicPath","");
                    String accessToken=preferences.getString("AccessToken","");
                    String deviceid=preferences.getString("DWEVICEID","");
                    if (!oldPath.equals(newPath))
                    {
                        ProfilePicAsyncTaskPost uploadimage=new ProfilePicAsyncTaskPost(AutoSyncService.this,AutoSyncService.this,preferences.getString("StudentUserID",""),bitmapImg,accessToken,deviceid);
                        uploadimage.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                    }
                }
                else if(type.equals("Exception"))
                {
                    ExceptionModel o = qr.GetException(id);
                    ExceptionAsyncTaskPost obj = new ExceptionAsyncTaskPost(o, AutoSyncService.this, AutoSyncService.this);
                    obj.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
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
               // for (int k = 0; k < subjects.size(); k++) {

                ParentHomeworkListModel home = new ParentHomeworkListModel();
                home.setSchoolcode(UserData[2]);
                home.setStandard(UserData[0]);
                home.setDivision(UserData[1]);
                home.setHwdate(currentDate);
                home.setSubject("All_Sub");
                HomewrokAutoAsyncTask obj = new HomewrokAutoAsyncTask(home, AutoSyncService.this, AutoSyncService.this);
                obj.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                //}

                SyncHomeworkDownload=false;
                SyncClassworkDownload=true;
            }
            else if (SyncClassworkDownload)
            {
                //classwork
                //for (int k = 0; k < subjects.size(); k++) {

                    ParentHomeworkListModel home = new ParentHomeworkListModel();
                    home.setSchoolcode(UserData[2]);
                    home.setStandard(UserData[0]);
                    home.setDivision(UserData[1]);
                    home.setHwdate(currentDate);
                    home.setSubject("All_Sub");
                    ClasswrokAutoAsyncTask obj = new ClasswrokAutoAsyncTask(home, AutoSyncService.this, AutoSyncService.this);
                    obj.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                //}

                SyncClassworkDownload=false;
            }
        }
    }

    @Override
    public void onTaskCompleted(String s) {
        Log.d("String", s);

        final String[] onTaskString=s.split("@@@");

        if(onTaskString[1].equalsIgnoreCase("Exception"))
        {
            if (onTaskString[0].equalsIgnoreCase("true")) {
                long n = qr.deleteQueueRow(Integer.valueOf(onTaskString[2]), "Exception");
                if (n >= 0) {
                    ExceptionModel obj = qr.GetException(Integer.valueOf(onTaskString[2]));
                    n = qr.updateException(obj);
                }
            }
        }
        else if (onTaskString[1].equalsIgnoreCase("Homework"))
        {
            DALHomework dla = new DALHomework(this);
            JSONObject rootObj = null;
            try {
                rootObj = new JSONObject(onTaskString[0]);
                JSONArray obj=rootObj.getJSONArray("fetchP2PHomeworkResult");
                for (int j=0;j<obj.length();j++)
                {
                    JSONObject ob = obj.getJSONObject(j);
                    String schoolCode= ob.getString("SchoolCode");
                    String std= ob.getString("Std");
                    String division= ob.getString("div");
                    String givenby= ob.getString("givenBy");
                    String hwdate= ob.getString("hwDate");
                    JSONArray img  = ob.getJSONArray("hwImage64Lst");
                    JSONArray text  = ob.getJSONArray("hwTxtLst");
                    String subject= ob.getString("subject");

                    if (!std.equalsIgnoreCase("null") && !givenby.equalsIgnoreCase("null"))
                    {
                        String[] IMG=new String[img.length()];
                        String[] TEXT=new String[text.length()];
                        for (int i = 0; i < img.length(); i++) {
                            IMG[i] = img.getString(i);
                        }
                        for (int i = 0; i < text.length(); i++) {
                            TEXT[i] = text.getString(i);
                        }

                        ArrayList<TeacherHomeworkModel> results = qr.GetHomeworkData(hwdate, onTaskString[1], std, division);
                        boolean isPresent=false;
                        if (results.size() > 0)
                        {
                            for (int i = 0; i < img.length(); i++) {
                                for (int k=0;k<results.size();k++)
                                {
                                    if (givenby.equals(results.get(k).getGivenBy()) && results.get(k).getHwTxtLst().equalsIgnoreCase(TEXT[0]))
                                    {
                                        isPresent=true;
                                        break;
                                    }
                                    else
                                    {
                                        isPresent=false;
                                    }
                                }
                            }
                        }
                        else
                        {
                            isPresent=false;
                        }
                        long n =0;
                        if (!isPresent)
                        {

                            String hwUUID= String.valueOf(UUID.randomUUID());
                            for (int i = 0; i < IMG.length; i++) {
                                String newPath = new Utility().getURLImage(IMG[i]);
                                if (!ImageStorage.checkifImageExists(newPath.split("/")[newPath.split("/").length - 1])) {
                                    new StoreBitmapImages(newPath, newPath.split("/")[newPath.split("/").length - 1]).execute(newPath);
                                }

                                n= qr.insertHomework(givenby, subject, hwdate, TEXT[0], IMG[i].toString(),std, division, onTaskString[1],hwUUID);
                            }
                            //n=dla.insertHomeworkInfo(schoolCode, std, division, givenby, hwdate, img.toString(), text.toString(), subject,onTaskString[1],student);
                            if (n>0)
                            {
                                // Toast.makeText(this, "Homework Downloaded Successfully...", Toast.LENGTH_LONG).show();
                                Calendar calendar = Calendar.getInstance();
                                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
                                String date = df.format(calendar.getTime());

                                NotificationModel notification1 = new NotificationModel();
                                notification1.setNotificationId(2);
                                notification1.setNotificationDate(date);
                                notification1.setNotificationtype("Homework");
                                notification1.setMessage(subject);
                                notification1.setIsRead("false");
                                notification1.setAdditionalData1(std+" "+division);
                                qr.InsertNotification(notification1);
                                if(Singleton.getResultReceiver() != null)
                                    Singleton.getResultReceiver().send(1,null);
                            }
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

                JSONArray obj=rootObj.getJSONArray("fetchP2PClassworkResult");
                for (int j=0;j<obj.length();j++)
                {
                    JSONObject ob = obj.getJSONObject(j);
                    String schoolCode= ob.getString("SchoolCode");
                    String std= ob.getString("Std");
                    String division= ob.getString("div");
                    String givenby= ob.getString("givenBy");
                    String hwdate= ob.getString("cwDate");
                    JSONArray img  = ob.getJSONArray("cwImage64Lst");
                    JSONArray text  = ob.getJSONArray("cwTxtLst");
                    String subject= ob.getString("subject");

                    if (!std.equalsIgnoreCase("null") && !givenby.equalsIgnoreCase("null"))
                    {
                        String[] IMG=new String[img.length()];
                        String[] TEXT=new String[text.length()];
                        for (int i = 0; i < img.length(); i++) {
                            IMG[i] = img.getString(i);
                        }
                        for (int i = 0; i < text.length(); i++) {
                            TEXT[i] = text.getString(i);
                        }

                        ArrayList<TeacherHomeworkModel> results = qr.GetHomeworkData(hwdate, onTaskString[1], std, division);
                        boolean isPresent=false;
                        if (results.size() > 0)
                        {
                            for (int i = 0; i < img.length(); i++) {
                                for (int k=0;k<results.size();k++)
                                {
                                    if (givenby.equals(results.get(k).getGivenBy()) && results.get(k).getHwTxtLst().equalsIgnoreCase(TEXT[0]))
                                    {
                                        isPresent=true;
                                        break;
                                    }
                                    else
                                    {
                                        isPresent=false;
                                    }
                                }
                            }
                        }
                        else
                        {
                            isPresent=false;
                        }
                        long n =0;
                        if (!isPresent)
                        {

                            String hwUUID= String.valueOf(UUID.randomUUID());
                            for (int i = 0; i < IMG.length; i++) {
                                String newPath = new Utility().getURLImage(IMG[i]);
                                if (!ImageStorage.checkifImageExists(newPath.split("/")[newPath.split("/").length - 1])) {
                                    new StoreBitmapImages(newPath, newPath.split("/")[newPath.split("/").length - 1]).execute(newPath);
                                }

                                n= qr.insertHomework(givenby, subject, hwdate, TEXT[0], IMG[i].toString(),std, division, onTaskString[1],hwUUID);
                            }
                            //n=dla.insertHomeworkInfo(schoolCode, std, division, givenby, hwdate, img.toString(), text.toString(), subject,onTaskString[1],student);
                            if (n>0)
                            {
                                // Toast.makeText(this, "Homework Downloaded Successfully...", Toast.LENGTH_LONG).show();
                                Calendar calendar = Calendar.getInstance();
                                SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
                                String date = df.format(calendar.getTime());

                                NotificationModel notification1 = new NotificationModel();
                                notification1.setNotificationId(2);
                                notification1.setNotificationDate(date);
                                notification1.setNotificationtype("Classwork");
                                notification1.setMessage(subject);
                                notification1.setIsRead("false");
                                notification1.setAdditionalData1(std+" "+division);
                                qr.InsertNotification(notification1);
                                if(Singleton.getResultReceiver() != null)
                                    Singleton.getResultReceiver().send(1,null);
                            }
                        }
                    }

                }
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }
        else if (onTaskString[1].equalsIgnoreCase("ProfilePic"))
        {
            if (onTaskString[0].equalsIgnoreCase("true"))
            {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(AutoSyncService.this);
                DALMyPupilInfo dp=new DALMyPupilInfo(AutoSyncService.this);
                String[] student=dp.GetAllTableData(preferences.getString("StudentUserID",""));
                String newPath=preferences.getString("NewThumbnailID","");
                long n=0;
                n=dp.updateStudentInfo(student[15], student[16], student[3], student[4], student[17], student[5], student[0], student[1], student[2], student[6], student[8], student[9], student[18], student[10],
                        student[19], student[11], student[7], student[20], student[21], student[22],student[23],student[13],student[12],newPath,student[24]);

                if(n>0)
                {
                    SharedPreferences.Editor edit = sharedpreferences.edit();
                    edit.putString("ThumbnailID", newPath);
                    edit.commit();
                }
            }
        }
        else {
            if(onTaskString[0].replace("\"","").equals("success"))
            {
                long n = qr.deleteQueueRow(Integer.valueOf(onTaskString[2]),onTaskString[3]);
                TeacherHomeworkModel homeworkObj = new TeacherHomeworkModel();
                if(n>0) {
                    n =0;
                    homeworkObj = qr.GetHomework(Integer.valueOf(onTaskString[2]));
                    n = qr.updateHomeworkSyncFlag(homeworkObj);
                    NotificationModel obj = new NotificationModel();
                    obj.setNotificationId(homeworkObj.getHid());
                    obj.setNotificationDate(homeworkObj.getHwDate());
                    obj.setNotificationtype(homeworkObj.getWork()+"Upload");
                    obj.setMessage("Uploaded Successfully for");
                    obj.setIsRead("false");
                    obj.setAdditionalData2("");
                    obj.setAdditionalData1(homeworkObj.getStd()+"@@@"+homeworkObj.getDiv()+"@@@"+
                            homeworkObj.getSubject());
                    qr.InsertNotification(obj);
                    Bundle b = new Bundle();
                    b.putInt("NotificationId", homeworkObj.getHid());
                    b.putString("NotificationDate", homeworkObj.getHwDate());
                    b.putString("NotificationType", homeworkObj.getWork()+"Upload");
                    b.putString("NotificationMessage", "Uploaded Successfully for");
                    b.putString("IsNotificationread", "false");
                    b.putString("AdditionalData1", homeworkObj.getStd()+"@@@"+homeworkObj.getDiv()+"@@@"+
                            homeworkObj.getSubject());
                    b.putString("AdditionalData2","");

                    if(Singleton.getResultReceiver() != null)
                        Singleton.getResultReceiver().send(1,null);
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
