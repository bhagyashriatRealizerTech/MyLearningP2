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
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

import realizer.com.schoolgenieparent.Notification.NotificationModel;
import realizer.com.schoolgenieparent.ProfilePicAsyncTaskPost;
import realizer.com.schoolgenieparent.Utils.Config;
import realizer.com.schoolgenieparent.Utils.ImageStorage;
import realizer.com.schoolgenieparent.Utils.OnTaskCompleted;
import realizer.com.schoolgenieparent.Utils.Singleton;
import realizer.com.schoolgenieparent.Utils.StoreBitmapImages;
import realizer.com.schoolgenieparent.Utils.Utility;
import realizer.com.schoolgenieparent.backend.DALMyPupilInfo;
import realizer.com.schoolgenieparent.backend.DALQueris;
import realizer.com.schoolgenieparent.backend.DatabaseQueries;
import realizer.com.schoolgenieparent.exceptionhandler.ExceptionAsyncTaskPost;
import realizer.com.schoolgenieparent.exceptionhandler.ExceptionModel;
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
        return START_NOT_STICKY;
    }

    private class BackgroundThread extends Thread{
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
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ManualSyncupService.this);
                DALMyPupilInfo dp=new DALMyPupilInfo(ManualSyncupService.this);
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
        else
        {
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

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (id == Integer.valueOf(onTaskString[2])) {
                            if (count==0)
                            {
                                count++;
                                // Config.alertDialog(Singleton.getContext(), "Manual Sync", "Sync Uploaded Successfully");
                            }
                        }
                    }
                });
            }
            else {
                Config.alertDialog(Singleton.getContext(), "Network Error", "Server Not Responding");
                //Toast.makeText(this, "Server not responding please wait...", Toast.LENGTH_SHORT).show();
            }
        }

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (onTaskString[1].equalsIgnoreCase("Classwork"))
                    if (counter==0)
                    {
                        counter++;
                        Config.alertDialog(Singleton.getContext(), "Manual Sync", "Sync Completed Successfully");
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
                            else  if(type.equals("ProfilePic") && id==1010)
                            {
                                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ManualSyncupService.this);
                                String oldPath=preferences.getString("ThumbnailID","");
                                String newPath=preferences.getString("NewThumbnailID","");
                                String bitmapImg=preferences.getString("ProfilePicPath","");
                                String accessToken=preferences.getString("AccessToken","");
                                String deviceid=preferences.getString("DWEVICEID","");
                                if (!oldPath.equals(newPath))
                                {
                                    ProfilePicAsyncTaskPost uploadimage=new ProfilePicAsyncTaskPost(ManualSyncupService.this,ManualSyncupService.this,preferences.getString("StudentUserID",""),bitmapImg,accessToken,deviceid);
                                    uploadimage.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                                }
                            }
                            else if(type.equals("Exception"))
                            {
                                ExceptionModel o = qr.GetException(id);
                                ExceptionAsyncTaskPost obj = new ExceptionAsyncTaskPost(o, ManualSyncupService.this, ManualSyncupService.this);
                                obj.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
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
               // for (int k = 0; k < subjects.size(); k++) {

                    ParentHomeworkListModel home = new ParentHomeworkListModel();
                    home.setSchoolcode(UserData[2]);
                    home.setStandard(UserData[0]);
                    home.setDivision(UserData[1]);
                    home.setHwdate(currentDate);
                    home.setSubject("All_Sub");
                    HomeworkAsyncTaskPost obj = new HomeworkAsyncTaskPost(home, ManualSyncupService.this, ManualSyncupService.this);
                    obj.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
              //  }

                SyncHomeworkDownload=false;
                SyncClassworkDownload=true;
            }
            else if (SyncClassworkDownload)
            {
                //classwork
               // for (int k = 0; k < subjects.size(); k++) {

                    ParentHomeworkListModel home = new ParentHomeworkListModel();
                    home.setSchoolcode(UserData[2]);
                    home.setStandard(UserData[0]);
                    home.setDivision(UserData[1]);
                    home.setHwdate(currentDate);
                    home.setSubject("All_Sub");
                    ClassworkAsyncTaskPost obj = new ClassworkAsyncTaskPost(home, ManualSyncupService.this, ManualSyncupService.this);
                    obj.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
                //}

                SyncClassworkDownload=false;
            }
        }
    }
}

