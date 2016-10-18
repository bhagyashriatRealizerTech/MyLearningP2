package realizer.com.schoolgenieparent.backend;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Date;

import realizer.com.schoolgenieparent.Notification.NotificationModel;
import realizer.com.schoolgenieparent.communication.model.TeacherQuery1model;
import realizer.com.schoolgenieparent.communication.model.TeacherQuerySendModel;
import realizer.com.schoolgenieparent.exceptionhandler.ExceptionModel;
import realizer.com.schoolgenieparent.homework.model.TeacherHomeworkModel;
import realizer.com.schoolgenieparent.queue.QueueListModel;

//import com.realizer.schoolgenie.chat.model.TeacherQuerySendModel;
//import com.realizer.schoolgenie.funcenter.model.TeacherFunCenterEventModel;
//import com.realizer.schoolgenie.funcenter.model.TeacherFunCenterGalleryModel;
//import com.realizer.schoolgenie.funcenter.model.TeacherFunCenterImageModel;
//import com.realizer.schoolgenie.funcenter.model.TeacherFunCenterModel;
//import com.realizer.schoolgenie.generalcommunication.model.TeacherGeneralCommunicationListModel;
//import com.realizer.schoolgenie.holiday.model.TeacherPublicHolidayListModel;
//import com.realizer.schoolgenie.myclass.model.TeacherAttendanceListModel;
//import com.realizer.schoolgenie.myclass.model.TeacherMyClassModel;
//import com.realizer.schoolgenie.queue.QueueListModel;
//import com.realizer.schoolgenie.selectstudentdialog.model.TeacherQuery1model;
//import com.realizer.schoolgenie.star.model.TeacherGiveStarModel;
//import com.realizer.schoolgenie.timetable.model.TeacherTimeTableExamListModel;

/**
 * Created by Win on 12/21/2015.
 */
public class DatabaseQueries {

    SQLiteDatabase db;
    Context context;
    String scode,std,div;
    String UserD[];

    public DatabaseQueries(Context context) {

        this.context = context;
        SQLiteOpenHelper myHelper = new SqliteHelper(context);
        this.db = myHelper.getWritableDatabase();

        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(context);
        scode= sharedpreferences.getString("SchoolCode", "");
        std= sharedpreferences.getString("SyncStd", "");
        div= sharedpreferences.getString("SyncDiv", "");
    }

    public void deleteAllData()
    {
        long deleterow =0;

        deleterow = db.delete("StudentInfo",null, null);
        deleterow = db.delete("QueryInfo",null, null);
        deleterow = db.delete("Homework",null, null);
        deleterow = db.delete("Query", null, null);
        deleterow = db.delete("SyncUPQueue", null, null);
        deleterow = db.delete("Exception", null, null);
        deleterow = db.delete("InitiatedChat",null, null);
        deleterow = db.delete("Notification",null, null);
        deleterow = db.delete("StdDivSub", null, null);
    }

    //Insert Student Information
    public long insertStudInfo(String standard,String division,String Studarr,String UserId)
    {
        //deleteTable();
        ContentValues conV = new ContentValues();
        conV.put("Std", standard);
        conV.put("Div", division);
        conV.put("StudArr", Studarr);
        conV.put("UserID", UserId);
        long newRowInserted = db.insert("StudInfo", null, conV);

        return newRowInserted;
    }

    //Insert Student Information
    public long updateStudInfo(String standard,String division,String Studarr,String UserId)
    {
        //deleteTable();
        ContentValues conV = new ContentValues();
        conV.put("Std", standard);
        conV.put("Div", division);
        conV.put("StudArr", Studarr);
        conV.put("UserID", UserId);
        long newRowInserted = db.update("StudInfo", conV, "UserID='" + UserId + "'", null);

        return newRowInserted;
    }



    //Insert Teacher Information
    public long insertTeacherInfo(String standard,String division,String Studarr)
    {

        ContentValues conV = new ContentValues();
        conV.put("Id", standard);
        conV.put("DispName", division);
        conV.put("Pic", Studarr);
        long newRowInserted = db.insert("TeacherInfo", null, conV);

        return newRowInserted;
    }

    //Insert Detail Teacher Info
    public long insertDetailTeacherInfo(String adate,String classon,String tname,String qual,String thumb,String uid,
                                        String cno,String dob,String emailid,String isactive)
    {
        ContentValues conV = new ContentValues();
        conV.put("ActiveDate", adate);
        conV.put("ClassTeacherOn", classon);
        conV.put("Name", tname);
        conV.put("Qualification", qual);
        conV.put("ThumbnailURL", thumb);
        conV.put("UserId", uid);
        conV.put("ContactNo", cno);
        conV.put("DOB", dob);
        conV.put("EmailId", emailid);
        conV.put("IsActive", isactive);
        long newRowInserted = db.insert("TeacherFullInfo", null, conV);
        return newRowInserted;
    }




    //Insert Subject Allocation Information
    public long insertStdSubjcet(String standard,String subject)
    {

        ContentValues conV = new ContentValues();
        conV.put("Std", standard);
        conV.put("Subject", subject);
        long newRowInserted = db.insert("StdSubject", null, conV);

        return newRowInserted;
    }


    //Select All Data From StudInfo
    public String GetAllTableData(String userid) {

        Cursor c = db.rawQuery("SELECT * FROM StudInfo WHERE UserID = '"+userid+"'", null);
        String Stud = null;

        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {
                    Stud = c.getString(c.getColumnIndex("StudArr"));
                }
                while (c.moveToNext());
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return Stud;
    }


    // Select queue Information
    public ArrayList<QueueListModel> GetQueueData() {
        Cursor c = db.rawQuery("SELECT * FROM SyncUPQueue ORDER BY SyncPriority ASC ", null);
        ArrayList<QueueListModel> result = new ArrayList<>();

        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {

                    QueueListModel o = new QueueListModel();
                    o.setId(c.getInt(c.getColumnIndex("Id")));
                    o.setType(c.getString(c.getColumnIndex("Type")));
                    o.setPriority(c.getString(c.getColumnIndex("SyncPriority")));
                    o.setTime(c.getString(c.getColumnIndex("Time")));

                    result.add(o);
                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return result;
    }

    public ArrayList<String> GetHWDate() {

        Cursor c = db.query(true, "Homework", new String[]{"hwDate"}, null, null, "hwDate", null, "hwDate", null);

        ArrayList<String> result = new ArrayList<>();
        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {

                    String sub = c.getString(c.getColumnIndex("hwDate"));
                    result.add(sub);
                    cnt = cnt + 1;

                }
                while (c.moveToNext());
            }
        } /*else {
            mToast("Table Has No contain");
        }*/
        c.close();
        return result;
    }


    //     Insert Subject Allocation Information
    public long insertSubInfo(String standard,String division,String sub)
    {
        ContentValues conV = new ContentValues();
        conV.put("Std", standard);
        conV.put("Div", division);
        conV.put("Sub", sub);
        long newRowInserted = db.insert("StdDivSub", null, conV);


        return newRowInserted;
    }

    //Select Allocated Std And Div from  STDDivSub
    public ArrayList<String> GetSub(String std,String div) {
        Cursor c = db.rawQuery("SELECT Sub FROM StdDivSub WHERE Std='"+std+"' and Div='"+div+"' ", null);
        ArrayList<String> result = new ArrayList<>();

        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {

                    String Sub = c.getString(0);
                    result.add(Sub);
                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return result;
    }




    //Insert Queue Infromation
    public long insertQueue(int id,String type,String priority,String time) {
        ContentValues conV = new ContentValues();
        conV.put("Id", id);
        conV.put("Type", type);
        conV.put("SyncPriority", priority);
        conV.put("Time", time);

        long newRowInserted = db.insert("SyncUPQueue", null, conV);


        return newRowInserted;
    }

/*
    // Select queue Information
    public ArrayList<QueueListModel> GetQueueData() {
        Cursor c = db.rawQuery("SELECT * FROM SyncUPQueue ORDER BY SyncPriority ASC ", null);
        ArrayList<QueueListModel> result = new ArrayList<>();

        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {

                    QueueListModel o = new QueueListModel();
                    o.setId(c.getInt(c.getColumnIndex("Id")));
                    o.setType(c.getString(c.getColumnIndex("Type")));
                    o.setPriority(c.getString(c.getColumnIndex("SyncPriority")));
                    o.setTime(c.getString(c.getColumnIndex("Time")));

                    result.add(o);
                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return result;
    }*/


    //Delete Row from Queue

    public long deleteQueueRow(int id,String type)
    {
        long deleterow = db.delete("SyncUPQueue","Id="+id+" and Type='"+type+"'",null);
        return deleterow;
    }

    //Insert Query Information
    public long insertQuery(String msgId,String fromUserId,String fromName,String touserid,String text,String profUrl,String dtime,String flag,Date sentDate)
    {
        ContentValues conV = new ContentValues();
        conV.put("msgId", msgId);
        conV.put("FromUserId", fromUserId);
        conV.put("FromName", fromName);
        conV.put("msg", text);
        conV.put("profUrl",profUrl);
        conV.put("sentTime", dtime);
        conV.put("touserId",touserid);
        conV.put("HasSyncedUp", flag);
        conV.put("sentDate" , sentDate.getTime());
        long newRowInserted = db.insert("Query", null, conV);
        return newRowInserted;
    }
    // get ID
    public int getQueryId() {
        Cursor c = db.rawQuery("SELECT QueryId FROM Query ", null);
        int cnt = 1;
        int att=0;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {

                    att = c.getInt(0);
                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        }
        c.close();
        return att;
    }
    //select Query
    public TeacherQuerySendModel GetQuery(int id) {
        Cursor c = db.rawQuery("SELECT * FROM Query WHERE QueryId="+id, null);
        TeacherQuerySendModel o = new TeacherQuerySendModel();
        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {
                    o.setConversationId(c.getInt(c.getColumnIndex("QueryId")));
                    o.setSentTime(c.getString(c.getColumnIndex("sentTime")));
                    o.setMsgId(c.getString(c.getColumnIndex("msgId")));
                    o.setFromUserId(c.getString(c.getColumnIndex("FromUserId")));
                    o.setFromUserName(c.getString(c.getColumnIndex("FromName")));
                    o.setText(c.getString(c.getColumnIndex("msg")));
                    o.setHassync(c.getString(c.getColumnIndex("HasSyncedUp")));
                    o.setDiv(div);
                    o.setStd(std);
                    o.setSchoolCode(scode);
                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        }
        c.close();
        return o;
    }
    // Select queue Information
    public ArrayList<TeacherQuerySendModel> GetQueuryData(String uid) {
        Cursor c = db.rawQuery("SELECT * FROM Query WHERE touserId='"+uid+"' ORDER BY sentTime ASC", null);
        ArrayList<TeacherQuerySendModel> result = new ArrayList<>();
        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {
                    TeacherQuerySendModel o = new TeacherQuerySendModel();
                    o.setConversationId(c.getInt(c.getColumnIndex("QueryId")));
                    o.setSentTime(c.getString(c.getColumnIndex("sentTime")));
                    o.setMsgId(c.getString(c.getColumnIndex("msgId")));
                    o.setFromUserId(c.getString(c.getColumnIndex("FromUserId")));
                    o.setFromUserName(c.getString(c.getColumnIndex("FromName")));
                    o.setText(c.getString(c.getColumnIndex("msg")));
                    o.setProfUrl(c.getString(c.getColumnIndex("profUrl")));
                    o.setHassync(c.getString(c.getColumnIndex("HasSyncedUp")));
                    o.setSchoolCode(scode);
                    result.add(o);
                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        }
        c.close();
        return result;
    }

    //Update Attendance Syncup Flag
    public long updateQurySyncFlag(TeacherQuerySendModel o) {
        ContentValues conV = new ContentValues();
        conV.put("msgId", o.getMsgId());
        conV.put("FromUserId", o.getFromUserId());
        conV.put("FromName", o.getFromUserName());
        conV.put("msg", o.getText());
        conV.put("sentTime", o.getSentTime());
        conV.put("HasSyncedUp", "true");
        long newRowUpdate = db.update("Query",conV,"QueryId="+o.getConversationId(),null);
        return newRowUpdate;
    }

    public long updateInitiatechat(String std,String div,String uname,String initiate,String uid,int unreadcount,String url) {
        ContentValues conV = new ContentValues();
        conV.put("Std", std);
        conV.put("Div", div);
        conV.put("Useranme", uname);
        conV.put("Initiated", initiate);
        conV.put("Uid",uid);
        conV.put("UnreadCount", unreadcount);
        conV.put("ThumbnailURL", url);
        long newRowUpdate = db.update("InitiatedChat", conV, "Uid='" + uid + "'", null);


        return newRowUpdate;
    }
    //Insert Homework data
    public long insertHomework(String givenby,String subject,String hdate,String txtlst,String imglst,String std,String div,String work,String msguuid)
    {

        ContentValues conV = new ContentValues();
        conV.put("Std", std);
        conV.put("Div", div);
        conV.put("subject", subject);
        conV.put("textlst", txtlst);
        conV.put("Imglst", imglst);
        conV.put("Givenby", givenby);
        conV.put("hwDate", hdate);
        conV.put("HasSyncedUp","false");
        conV.put("Work", work);
        conV.put("HomeworkUUID", msguuid);
        long newRowInserted = db.insert("Homework", null, conV);

        return newRowInserted;
    }

    // get ID
    public int getHomeworkId() {
        Cursor c = db.rawQuery("SELECT HomeworkId FROM Homework ORDER BY HomeworkId ASC;", null);
        int cnt = 1;
        int att=0;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {

                    att = c.getInt(0);
                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return att;
    }

    //select Query
    public TeacherHomeworkModel GetHomework(int id)
    {
        Cursor c = db.rawQuery("SELECT * FROM Homework WHERE HomeworkId="+id, null);
        TeacherHomeworkModel o = new TeacherHomeworkModel();
        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");

                do {

                    o.setHid(c.getInt(c.getColumnIndex("HomeworkId")));
                    o.setSubject(c.getString(c.getColumnIndex("subject")));
                    o.setStd(c.getString(c.getColumnIndex("Std")));
                    o.setDiv(c.getString(c.getColumnIndex("Div")));
                    o.setHwTxtLst(c.getString(c.getColumnIndex("textlst")));
                    o.setHwImage64Lst(c.getString(c.getColumnIndex("Imglst")));
                    o.setGivenBy(c.getString(c.getColumnIndex("Givenby")));
                    o.setHwDate(c.getString(c.getColumnIndex("hwDate")));
                    o.setWork(c.getString(c.getColumnIndex("Work")));
                    o.setHwUUID(c.getString(c.getColumnIndex("HomeworkUUID")));
                    cnt = cnt+1;

                }
                while (c.moveToNext());
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return o;
    }

    //Select all Holidays

    public ArrayList<TeacherHomeworkModel> GetHomeworkData( String date,String work,String std,String div) {

        Cursor c = db.rawQuery("SELECT * FROM Homework WHERE hwDate='"+date+"' AND Work='"+work+"' " +
                "AND Std= '"+std+"' AND Div= '"+div+"'", null);


        ArrayList<TeacherHomeworkModel> result = new ArrayList<>();

        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {

                    TeacherHomeworkModel o = new TeacherHomeworkModel();
                    o.setHid(c.getInt(c.getColumnIndex("HomeworkId")));
                    o.setSubject(c.getString(c.getColumnIndex("subject")));
                    o.setStd(c.getString(c.getColumnIndex("Std")));
                    o.setDiv(c.getString(c.getColumnIndex("Div")));
                    o.setHwTxtLst(c.getString(c.getColumnIndex("textlst")));
                    o.setHwImage64Lst(c.getString(c.getColumnIndex("Imglst")));
                    o.setGivenBy(c.getString(c.getColumnIndex("Givenby")));
                    o.setHwDate(c.getString(c.getColumnIndex("hwDate")));
                    o.setWork(c.getString(c.getColumnIndex("Work")));
                    o.setIsSync(c.getString(c.getColumnIndex("HasSyncedUp")));
                    o.setHwUUID(c.getString(c.getColumnIndex("HomeworkUUID")));
                    result.add(o);
                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return result;
    }

    public long updateHomeworkSyncFlag(TeacherHomeworkModel o)
    {

        ContentValues conV = new ContentValues();
        conV.put("Std", o.getStd());
        conV.put("Div", o.getDiv());
        conV.put("subject", o.getSubject());
        conV.put("textlst", o.getHwTxtLst());
        conV.put("Imglst", o.getHwImage64Lst());
        conV.put("Givenby", o.getGivenBy());
        conV.put("hwDate", o.getHwDate());
        conV.put("HasSyncedUp","true");
        conV.put("Work", o.getWork());

        long newRowUpdate = db.update("Homework", conV, "HomeworkId=" + o.getHid(), null);

        return newRowUpdate;
    }

    //Insert Homework data
    public long insertInitiatechat(String uname,String initiated,String uid,int unreadcount,String url)
    {

        ContentValues conV = new ContentValues();
        conV.put("Useranme", uname);
        conV.put("Initiated", initiated);
        conV.put("Uid",uid);
        conV.put("UnreadCount", unreadcount);
        conV.put("ThumbnailURL", url);
        long newRowInserted = db.insert("InitiatedChat", null, conV);

        return newRowInserted;
    }

//    public long updateInitiatechat(String std,String div,String uname,String initiate,String uid,int unreadcount) {
//        ContentValues conV = new ContentValues();
//        conV.put("Std", std);
//        conV.put("Div", div);
//        conV.put("Useranme", uname);
//        conV.put("Initiated", initiate);
//        conV.put("Uid",uid);
//        conV.put("UnreadCount", unreadcount);
//
//        long newRowUpdate = db.update("InitiatedChat", conV, "Uid='" + uid + "'", null);
//
//
//        return newRowUpdate;
//    }


    public ArrayList<TeacherQuery1model> GetInitiatedChat(String ini) {

        Cursor c = db.rawQuery("SELECT * FROM InitiatedChat WHERE Initiated='"+ini+"' ", null);
        ArrayList<TeacherQuery1model> result = new ArrayList<>();

        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {
                    TeacherQuery1model obj = new TeacherQuery1model();
                    obj.setUname(c.getString(c.getColumnIndex("Useranme")));
                    obj.setUid(c.getString(c.getColumnIndex("Uid")));
                    obj.setUnreadCount(c.getInt(c.getColumnIndex("UnreadCount")));
                    obj.setProfileImg(c.getString(c.getColumnIndex("ThumbnailURL")));
                    result.add(obj);

                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return result;
    }

    public int GetUnreadCount(String Uid) {

        Cursor c = db.rawQuery("SELECT * FROM InitiatedChat WHERE Uid='"+Uid+"' ", null);
        int result = 0;

        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {
                    result = c.getInt(c.getColumnIndex("UnreadCount"));
                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return result;
    }
//    public ArrayList<TeacherQuery1model> GetstudList( String std,String div)
//    {
//
//        Cursor c = db.rawQuery("SELECT * FROM InitiatedChat WHERE Std='"+std+"' AND Div='"+div+"'", null);
//        ArrayList<TeacherQuery1model> result = new ArrayList<>();
//
//        int cnt = 1;
//        if (c != null) {
//            if (c.moveToFirst()) {
//                System.out.print("while moving  - C != null");
//                do {
//                    TeacherQuery1model obj = new TeacherQuery1model();
//                    obj.setUname(c.getString(c.getColumnIndex("Useranme")));
//                    obj.setUid(c.getString(c.getColumnIndex("Uid")));
//
//                    result.add(obj);
//
//                    cnt = cnt+1;
//                }
//                while (c.moveToNext());
//            }
//        } else {
//            // mToast("Table Has No contain");
//        }
//        c.close();
//        //dbClose(db);
//        return result;
//    }


//    public String Getuname( String uid) {
//
//        Cursor c = db.rawQuery("SELECT * FROM InitiatedChat WHERE Uid='"+uid+"' ", null);
//        int cnt = 1;
//        String temp ="";
//        if (c != null) {
//            if (c.moveToFirst()) {
//                System.out.print("while moving  - C != null");
//                do {
//
//                    temp = c.getString(c.getColumnIndex("Useranme"));
//
//                    cnt = cnt+1;
//                }
//                while (c.moveToNext());
//            }
//        } else {
//            // mToast("Table Has No contain");
//        }
//        c.close();
//        //dbClose(db);
//        return temp;
//    }


    /*=====End======*/

    public void deleteTable()
    {
        db.delete("StdDivSub",null,null);
        db.delete("StudInfo",null,null);
        db.delete("TeacherInfo",null,null);
        //db.delete("Announcement",null,null);
        db.delete("Attendance",null,null);
        //db.delete("SyncUPQueue",null,null);
        db.delete("Holiday",null,null);
        db.delete("GiveStar",null,null);
        db.delete("InitiatedChat", null, null);
        db.delete("Query", null, null);
        //db.delete("Homework",null,null);

    }


   /*  //Select queue Information
    public ArrayList<QueueListModel> GetQueueData() {
        Cursor c = db.rawQuery("SELECT * FROM SyncUPQueue ORDER BY SyncPriority ASC ", null);
        ArrayList<QueueListModel> result = new ArrayList<>();

        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {

                    QueueListModel o = new QueueListModel();
                    o.setId(c.getInt(c.getColumnIndex("Id")));
                    o.setType(c.getString(c.getColumnIndex("Type")));
                    o.setPriority(c.getString(c.getColumnIndex("SyncPriority")));
                    o.setTime(c.getString(c.getColumnIndex("Time")));

                    result.add(o);
                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return result;
    }*/

    //Insert Homework data
    public long insertException(ExceptionModel obj)
    {
        ContentValues conV = new ContentValues();
        conV.put("UserId", obj.getUserId());
        conV.put("ExceptionDetails", obj.getExceptionDetails());
        conV.put("DeviceModel",obj.getDeviceModel());
        conV.put("AndroidVersion", obj.getAndroidVersion());
        conV.put("ApplicationSource", obj.getApplicationSource());
        conV.put("DeviceBrand", obj.getDeviceBrand());
        conV.put("HasSyncedUp","false");
        long newRowInserted = db.insert("Exception", null, conV);

        return newRowInserted;
    }

    public long updateException(ExceptionModel obj)
    {

        ContentValues conV = new ContentValues();
        conV.put("ExceptionId", obj.getExceptionID());
        conV.put("UserId", obj.getUserId());
        conV.put("ExceptionDetails", obj.getExceptionDetails());
        conV.put("DeviceModel",obj.getDeviceModel());
        conV.put("AndroidVersion", obj.getAndroidVersion());
        conV.put("ApplicationSource", obj.getApplicationSource());
        conV.put("DeviceBrand", obj.getDeviceBrand());
        conV.put("HasSyncedUp","true");

        long newRowUpdate = db.update("Exception", conV, "ExceptionId=" + obj.getExceptionID(), null);

        return newRowUpdate;
    }

    public ExceptionModel GetException(int ID) {

        Cursor c = db.rawQuery("SELECT * FROM Exception WHERE ExceptionId="+ID+"", null);
        ExceptionModel result = new ExceptionModel();

        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {
                    ExceptionModel obj = new ExceptionModel();
                    obj.setExceptionID(c.getInt(c.getColumnIndex("ExceptionId")));
                    obj.setUserId(c.getString(c.getColumnIndex("UserId")));
                    obj.setExceptionDetails(c.getString(c.getColumnIndex("ExceptionDetails")));
                    obj.setDeviceModel(c.getString(c.getColumnIndex("DeviceModel")));
                    obj.setAndroidVersion(c.getString(c.getColumnIndex("AndroidVersion")));
                    obj.setApplicationSource(c.getString(c.getColumnIndex("ApplicationSource")));
                    obj.setDeviceBrand(c.getString(c.getColumnIndex("DeviceBrand")));
                    result = obj;

                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return result;
    }

    public int getExceptionId() {
        Cursor c = db.rawQuery("SELECT ExceptionId FROM Exception ORDER BY ExceptionId DESC LIMIT 1;", null);
        int cnt = 1;
        int att=0;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {

                    att = c.getInt(0);
                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return att;
    }


    public  void delete()
    {
        db.delete("EventImages",null,null);
        db.delete("EventMaster",null,null);
    }

//==================================================  Notification ======================================================== //

    //Insert Event Image info
    public long InsertNotification(NotificationModel obj)
    {
        //  delete();
        ContentValues conV = new ContentValues();

        conV.put("NotificationId", obj.getNotificationId());
        conV.put("Type", obj.getNotificationtype());
        conV.put("Message",obj.getMessage());
        conV.put("Date", obj.getNotificationDate());
        conV.put("AdditionalData1", obj.getAdditionalData1());
        conV.put("AdditionalData2",obj.getAdditionalData2());
        conV.put("IsRead", obj.isRead());
        long newRow1 = db.insert("Notification", null, conV);

        return newRow1;
    }

    //Update Images
    public long UpdateNotification(NotificationModel obj)
    {
        ContentValues conV = new ContentValues();
        conV.put("ID", obj.getId());
        conV.put("NotificationId", obj.getNotificationId());
        conV.put("Type", obj.getNotificationtype());
        conV.put("Message",obj.getMessage());
        conV.put("Date", obj.getNotificationDate());
        conV.put("AdditionalData1", obj.getAdditionalData1());
        conV.put("AdditionalData2",obj.getAdditionalData2());
        conV.put("IsRead", obj.isRead());

        long newRowUpdate = db.update("Notification", conV, "ID=" + obj.getId(), null);
        return newRowUpdate;
    }

    //Delete Row from Queue

    public long deleteNotificationRow(int id)
    {
        long deleterow = db.delete("Notification", "ID=" + id, null);
        return deleterow;
    }

    // Select queue Information
    public ArrayList<NotificationModel> GetNotificationsData() {
        Cursor c = db.rawQuery("SELECT * FROM Notification ORDER BY ID DESC ", null);
        ArrayList<NotificationModel> result = new ArrayList<>();

        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {

                    NotificationModel o = new NotificationModel();
                    o.setId(c.getInt(c.getColumnIndex("ID")));
                    o.setNotificationId(c.getInt(c.getColumnIndex("NotificationId")));
                    o.setNotificationDate(c.getString(c.getColumnIndex("Date")));
                    o.setNotificationtype(c.getString(c.getColumnIndex("Type")));
                    o.setMessage(c.getString(c.getColumnIndex("Message")));
                    o.setIsRead(c.getString(c.getColumnIndex("IsRead")));
                    o.setAdditionalData1(c.getString(c.getColumnIndex("AdditionalData1")));
                    o.setAdditionalData2(c.getString(c.getColumnIndex("AdditionalData2")));

                    result.add(o);
                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return result;
    }

    // Select queue Information
    public NotificationModel GetNotificationById(int id) {
        Cursor c = db.rawQuery("SELECT * FROM Notification WHERE ID ="+id, null);
        int cnt = 1;
        NotificationModel result = new NotificationModel();
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {

                    NotificationModel o = new NotificationModel();
                    o.setId(c.getInt(c.getColumnIndex("ID")));
                    o.setNotificationId(c.getInt(c.getColumnIndex("NotificationId")));
                    o.setNotificationDate(c.getString(c.getColumnIndex("Date")));
                    o.setNotificationtype(c.getString(c.getColumnIndex("Type")));
                    o.setMessage(c.getString(c.getColumnIndex("Message")));
                    o.setIsRead(c.getString(c.getColumnIndex("IsRead")));
                    o.setAdditionalData1(c.getString(c.getColumnIndex("AdditionalData1")));
                    o.setAdditionalData2(c.getString(c.getColumnIndex("AdditionalData2")));

                    result = o;
                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return result;
    }
    // Select queue Information
    public NotificationModel GetNotificationByUserId(String Uid) {
        Cursor c = db.rawQuery("SELECT * FROM Notification WHERE AdditionalData2 = '"+Uid+"'", null);
        int cnt = 1;
        NotificationModel result = new NotificationModel();
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {

                    NotificationModel o = new NotificationModel();
                    o.setId(c.getInt(c.getColumnIndex("ID")));
                    o.setNotificationId(c.getInt(c.getColumnIndex("NotificationId")));
                    o.setNotificationDate(c.getString(c.getColumnIndex("Date")));
                    o.setNotificationtype(c.getString(c.getColumnIndex("Type")));
                    o.setMessage(c.getString(c.getColumnIndex("Message")));
                    o.setIsRead(c.getString(c.getColumnIndex("IsRead")));
                    o.setAdditionalData1(c.getString(c.getColumnIndex("AdditionalData1")));
                    o.setAdditionalData2(c.getString(c.getColumnIndex("AdditionalData2")));

                    result = o;
                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        } else {
            result.setNotificationId(-1);
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return result;
    }

    public ArrayList<TeacherHomeworkModel> GetHomeworkAllImages(String work) {

        Cursor c = db.rawQuery("SELECT * FROM Homework WHERE Work='"+work+"'", null);


        ArrayList<TeacherHomeworkModel> result = new ArrayList<>();

        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {

                    TeacherHomeworkModel o = new TeacherHomeworkModel();
                    o.setHid(c.getInt(c.getColumnIndex("HomeworkId")));
                    o.setSubject(c.getString(c.getColumnIndex("subject")));
                    o.setStd(c.getString(c.getColumnIndex("Std")));
                    o.setDiv(c.getString(c.getColumnIndex("Div")));
                    o.setHwTxtLst(c.getString(c.getColumnIndex("textlst")));
                    o.setHwImage64Lst(c.getString(c.getColumnIndex("Imglst")));
                    o.setGivenBy(c.getString(c.getColumnIndex("Givenby")));
                    o.setHwDate(c.getString(c.getColumnIndex("hwDate")));
                    o.setWork(c.getString(c.getColumnIndex("Work")));
                    o.setIsSync(c.getString(c.getColumnIndex("HasSyncedUp")));
                    o.setHwUUID(c.getString(c.getColumnIndex("HomeworkUUID")));
                    result.add(o);
                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return result;
    }
}
