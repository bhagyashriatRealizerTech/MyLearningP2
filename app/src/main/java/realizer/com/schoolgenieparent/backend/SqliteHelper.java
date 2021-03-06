package realizer.com.schoolgenieparent.backend;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


/**
 * Created by Win on 12/21/2015.
 */
public class SqliteHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "SchoolGeniePToP";
    private static final int DATABASE_VERSION =26;
    Context mycontext;
    private static final String STUDINFO =
            "CREATE TABLE StudentInfo(  UserId TEXT," +
                    "Pwd TEXT," +
                    "Std TEXT," +
                    "Division TEXT,"+
                    "ClassRollNo TEXT,"+
                    "RollNo TEXT,"+
                    "FName TEXT,"+
                    "MName TEXT,"+
                    "LName TEXT,"+
                    "Dob TEXT,"+
                    "Bldgrp TEXT,"+
                    "FatherName TEXT,"+
                    "MotherName TEXT,"+
                    "ContactNo TEXT,"+
                    "EmergencyContactNo TEXT,"+
                    "Address TEXT,"+
                    "Hobbies TEXT,"+
                    "Comments TEXT,"+
                    "IsActive TEXT,"+
                    "ActiveTill TEXT,"+
                    "RegistrationCode TEXT,"+
                    "AcademicYear TEXT,"+
                    "SchoolCode TEXT,"+
                    "ThumbnailURL TEXT,"+
                    "MagicWord TEXT"+
                    ")";
    private static final String STDSUBALLOCATE ="CREATE TABLE StdDivSub(Std TEXT,Div TEXT, Sub TEXT)";
    private static final String STUDLIST ="CREATE TABLE StudInfo(Std TEXT,Div TEXT, UserID TEXT,StudArr TEXT)";
    private static final String QUERYINFO =
            "CREATE TABLE QueryInfo(Stndard TEXT,TeacherName TEXT,TeacherId TEXT,Division TEXT,TeacherSubject TEXT,ThumbnailURL TEXT)";
    //    private static final String TEACHERINFO ="CREATE TABLE TeacherInfo(Id TEXT,DispName TEXT, Pic TEXT)";
//    private static final String STDSUBJECT ="CREATE TABLE StdSubject(Std TEXT,Subject TEXT)";
//    private static final String Announcement ="CREATE TABLE Announcement(Year TEXT,AnnounceID INTEGER PRIMARY KEY   AUTOINCREMENT,Std TEXT,Div TEXT,SDate Text,Message Text,SendBy TEXT,Category TEXT,HasSyncedUp TEXT)";
//    private static final String Chatting ="CREATE TABLE Chatting(Year TEXT,ChatID INTEGER PRIMARY KEY   AUTOINCREMENT,SDate Text,Message Text,SendBy TEXT,SendTo TEXT,HasSyncedUp TEXT)";
    private static final String SyncUPQueue ="CREATE TABLE SyncUPQueue(Id INTEGER,Type TEXT,SyncPriority TEXT,Time TEXT)";
    //    private static final String Attendance ="CREATE TABLE Attendance(AttendanceId INTEGER PRIMARY KEY   AUTOINCREMENT,attendanceDate TEXT,SchoolCode TEXT,Std TEXT,Div TEXT,AttendanceBy TEXT,Attendees TEXT,Absenties TEXT,AttendanceCnt INTEGER,AbsentCnt TEXT,HasSyncedUp TEXT)";
    private static final String Query ="CREATE TABLE Query(QueryId INTEGER PRIMARY KEY   AUTOINCREMENT,msgId TEXT,FromUserId TEXT,FromName TEXT,touserId TEXT,msg TEXT,sentTime TEXT,profUrl Text,sentDate INTEGER,HasSyncedUp TEXT)";

    //    private static final String Holiday ="CREATE TABLE Holiday(Id INTEGER PRIMARY KEY   AUTOINCREMENT,holiday TEXT,hsdate TEXT,hedate TEXT)";
//    private static final String GiveStar ="CREATE TABLE GiveStar(GiveStarId INTEGER PRIMARY KEY   AUTOINCREMENT,TeacherLoginId TEXT,StudentLoginId TEXT,Comment TEXT,star TEXT,StarDate TEXT,Subject TEXT,Std TEXT,Div TEXT,StarTime TEXT,HasSyncedUp TEXT)";
    private static final String Homework ="CREATE TABLE Homework(HomeworkId INTEGER PRIMARY KEY   AUTOINCREMENT,Std Text,Div Text,subject TEXT,textlst TEXT,Imglst TEXT,Givenby TEXT,hwDate TEXT,HasSyncedUp TEXT,Work TEXT,HomeworkUUID TEXT)";
    private static final String ExceptionHandler = "CREATE TABLE Exception(ExceptionId INTEGER PRIMARY KEY   AUTOINCREMENT,UserId TEXT,ExceptionDetails TEXT,DeviceModel TEXT,AndroidVersion TEXT,ApplicationSource TEXT,DeviceBrand TEXT,HasSyncedUp TEXT)";
    private static final String HOMEWORKINFO = "CREATE TABLE HomeworkInfo(SchoolCode TEXT,Standard TEXT,Division TEXT,GivenBy TEXT,HomeworkDate TEXT,HwImage64Lst TEXT,HwTxtLst TEXT,Subject TEXT,Work TEXT)";

    //private static final String Classwork ="CREATE TABLE Classwork(ClassworkId INTEGER PRIMARY KEY   AUTOINCREMENT,Std Text,Div Text,subject TEXT,textlst TEXT,Imglst TEXT,Givenby TEXT,hwDate TEXT,HasSyncedUp TEXT)";
    private static final String InitiatedChat ="CREATE TABLE InitiatedChat(Id INTEGER,Useranme TEXT,Initiated TEXT,STD TEXT,Div TEXT,Uid TEXT,UnreadCount INTEGER,ThumbnailURL TEXT)";
    //    private static final String TIMETABLE ="CREATE TABLE TimeTable(TTId INTEGER PRIMARY KEY   AUTOINCREMENT,Std Text,Div Text,TmTbleName TEXT,Imglst TEXT,Givenby TEXT,TTDate TEXT,HasSyncedUp TEXT,Description TEXT)";
//    private static final String FunCenter ="CREATE TABLE EventMaster(Class TEXT,Div TEXT,Event_Id INTEGER PRIMARY KEY   AUTOINCREMENT,Event TEXT,Date TEXT,Thumbnail TEXT,Create_Date TEXT,HasSyncedUp TEXT,AcademicYear INTEGER,EvntuuId TEXT,FileName TEXT)";
//    private static final String FunCenter1 ="CREATE TABLE EventImages(Image_id INTEGER PRIMARY KEY   AUTOINCREMENT,Eventid INTEGER ,Image TEXT,Upload_Date  TEXT,Is_Uploaded TEXT,HasSyncedUp TEXT,AcademicYear INTEGER,SrNo INTEGER,ImageCaption TEXT,Imguuid TEXT,File_Name TEXT)";
//    private static final String TEACHERFULLINFO ="CREATE TABLE TeacherFullInfo(ActiveDate TEXT,ClassTeacherOn TEXT,Name TEXT,Qualification TEXT,ThumbnailURL TEXT,UserId TEXT,ContactNo TEXT,DOB TEXT,EmailId TEXT,IsActive TEXT)";
    private static final String Notification ="CREATE TABLE Notification(ID INTEGER PRIMARY KEY   AUTOINCREMENT,NotificationId INTEGER,Type TEXT,Message TEXT,Date TEXT,AdditionalData1 TEXT,AdditionalData2 TEXT,IsRead TEXT)";

    public SqliteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        Thread.setDefaultUncaughtExceptionHandler(new realizer.com.schoolgenieparent.exceptionhandler.ExceptionHandler(context));
        this.mycontext = context;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(STUDINFO);
        db.execSQL(STDSUBALLOCATE);
        db.execSQL(STUDLIST);
        db.execSQL(ExceptionHandler);
//        db.execSQL(STDSUBJECT);
//        db.execSQL(Announcement);
//        db.execSQL(Chatting);
        db.execSQL(SyncUPQueue);
//        db.execSQL(Attendance);
        db.execSQL(Query);
//        db.execSQL(Holiday);
//        db.execSQL(GiveStar);
        db.execSQL(Homework);
        db.execSQL(QUERYINFO);
        db.execSQL(HOMEWORKINFO);
        db.execSQL(InitiatedChat);
//        db.execSQL(FunCenter);
//        db.execSQL(FunCenter1);
//        db.execSQL(TIMETABLE);
//        db.execSQL(TEACHERFULLINFO);
        db.execSQL(Notification);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {

        db.execSQL("DROP TABLE if exists " + "StudentInfo");
        db.execSQL("DROP TABLE if exists " + "QueryInfo");
        db.execSQL("DROP TABLE if exists " + "StudInfo");
        db.execSQL("DROP TABLE if exists " + "StdDivSub");
        db.execSQL("DROP TABLE if exists " + "TeacherInfo");
        db.execSQL("DROP TABLE if exists " + "StdSubject");
        db.execSQL("DROP TABLE if exists " + "Announcement");
        db.execSQL("DROP TABLE if exists " + "Chatting");
        db.execSQL("DROP TABLE if exists " + "SyncUPQueue");
        db.execSQL("DROP TABLE if exists " + "Attendance");
        db.execSQL("DROP TABLE if exists " + "Query");
        db.execSQL("DROP TABLE if exists " + "Holiday");
        db.execSQL("DROP TABLE if exists " + "GiveStar");
        db.execSQL("DROP TABLE if exists " + "Homework");
        db.execSQL("DROP TABLE if exists " + "InitiatedChat");
        db.execSQL("DROP TABLE if exists " + "EventMaster");
        db.execSQL("DROP TABLE if exists " + "EventImages");
        db.execSQL("DROP TABLE if exists " + "TimeTable");
        db.execSQL("DROP TABLE if exists " + "TeacherFullInfo");
        db.execSQL("DROP TABLE if exists " + "HomeworkInfo");
        db.execSQL("DROP TABLE if exists " + "Exception");
        db.execSQL("DROP TABLE if exists " + "Notification");
        onCreate(db);
    }
}
