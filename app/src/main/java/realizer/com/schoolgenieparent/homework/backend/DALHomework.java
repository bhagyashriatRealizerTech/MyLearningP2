package realizer.com.schoolgenieparent.homework.backend;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import realizer.com.schoolgenieparent.backend.SqliteHelper;
import realizer.com.schoolgenieparent.homework.model.ParentHomeworkListModel;
import realizer.com.schoolgenieparent.homework.model.TeacherHomeworkModel;

/**
 * Created by shree on 1/8/2016.
 */
public class DALHomework {
    SQLiteDatabase db;
    SqliteHelper myhelper;
    Context context;

    public DALHomework(Context context) {
        this.context = context;
        SQLiteOpenHelper myHelper = new SqliteHelper(context);
        this.db = myHelper.getWritableDatabase();
    }

    public long insertHomeworkInfo(String schoolCode,String standard,String division,String givenBy,
                                   String homeworkDate,String hwImage64Lst,String hwTxtLst,String subject,String work,String userid)
    {
        ContentValues conV = new ContentValues();
        conV.put("SchoolCode", schoolCode);
        conV.put("Standard", standard);
        conV.put("Division", division);
        conV.put("GivenBy", givenBy);
        conV.put("HomeworkDate", homeworkDate);
        conV.put("HwImage64Lst", hwImage64Lst);
        conV.put("HwTxtLst", hwTxtLst);
        conV.put("Subject", subject);
        conV.put("Work", work);
        conV.put("UserId", userid);

        long newRowInserted = db.insert("HomeworkInfo", null, conV);
        if(newRowInserted >= 0)
        {
            Log.d("Homework ", "Insert");
        }
        else
        {
            Log.d("Homework ", "Not Insert");
        }
        return newRowInserted;
    }

    public ArrayList<ParentHomeworkListModel> GetHomeworkInfoData(String date,String work,String userid) {

        Cursor c = db.rawQuery("SELECT * FROM HomeworkInfo where HomeworkDate = "+"'"+date+"' AND Work='"+work+"' AND UserId='"+userid+"'", null);
        ArrayList<ParentHomeworkListModel> result = new ArrayList<>();
        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {

                    ParentHomeworkListModel o = new ParentHomeworkListModel();
                    o.setHomework(c.getString(c.getColumnIndex("HwTxtLst")));
                    o.setImage(c.getString(c.getColumnIndex("HwImage64Lst")));
                    o.setSubject(c.getString(c.getColumnIndex("Subject")));
                    result.add(o);
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

    public ArrayList<ParentHomeworkListModel> GetHomeworkAllInfoData(String date) {

        Cursor c = db.rawQuery("SELECT * FROM HomeworkInfo where HomeworkDate = "+"'"+date+"'", null);
        ArrayList<ParentHomeworkListModel> result = new ArrayList<>();
        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {

                    ParentHomeworkListModel o = new ParentHomeworkListModel();

                    o.setSchoolcode(c.getString(c.getColumnIndex("SchoolCode")));
                    o.setHomework(c.getString(c.getColumnIndex("HwTxtLst")));
                    o.setImage(c.getString(c.getColumnIndex("HwImage64Lst")));
                    o.setSubject(c.getString(c.getColumnIndex("Subject")));
                    o.setStandard(c.getString(c.getColumnIndex("Standard")));
                    o.setDivision(c.getString(c.getColumnIndex("Division")));
                    o.setGivenBy(c.getString(c.getColumnIndex("GivenBy")));
                    o.setHwdate(c.getString(c.getColumnIndex("HomeworkDate")));
                    o.setWork(c.getString(c.getColumnIndex("Work")));
                    result.add(o);
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




    public List<String> GetHomeDateTableData() {
        List<String> list = new ArrayList<String>();
        Cursor c = db.rawQuery("SELECT * FROM HomeworkInfo ", null);

        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {
                    list.add(c.getString(c.getColumnIndex("HomeworkDate")));
                }
                while (c.moveToNext());
            }
        }
        c.close();
        return list;
    }


    public byte []GetImageTableData(String herbs) {


        Cursor c = db.rawQuery("SELECT * FROM HomeworkInfo where HomeworkDate = "+"'"+herbs+"'", null);

        byte img[]=new byte[10];
        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {
                    img = c.getBlob(c.getColumnIndex("HwImage64Lst"));
                }
                while (c.moveToNext());
            }
        } else {
            // mToast("Table Has No contain");
        }
        c.close();
        //dbClose(db);
        return img;
    }
    public ParentHomeworkListModel GetHomeworkTableData(String date) {

        Cursor c = db.rawQuery("SELECT * FROM HomeworkInfo where HomeworkDate ="+"'"+date+"'", null);
        //ArrayList<ParentHomeworkListModel> result = new ArrayList<>();
        ParentHomeworkListModel o = new ParentHomeworkListModel();
        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {

                    o.setHomework(c.getString(c.getColumnIndex("HwTxtLst")));
                    o.setImage(c.getString(c.getColumnIndex("HwImage64Lst")));
                    o.setSubject(c.getString(c.getColumnIndex("Subject")));
                    o.setSchoolcode(c.getString(c.getColumnIndex("SchoolCode")));
                    o.setDivision(c.getString(c.getColumnIndex("Division")));
                    o.setHwdate(c.getString(c.getColumnIndex("HomeworkDate")));
                    o.setStandard(c.getString(c.getColumnIndex("Standard")));
                    o.setGivenBy(c.getString(c.getColumnIndex("GivenBy")));
                    //result.add(o);
                    cnt = cnt + 1;
                }
                while (c.moveToNext());
            }
        } /*else {
            mToast("Table Has No contain");
        }*/
        c.close();
        return o ;
    }

    public String GetLastSyncHomeworkDate() {

        Cursor c = db.rawQuery("SELECT HomeworkDate FROM HomeworkInfo ORDER BY HomeworkDate ASC", null);
        String lstdate="";
        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {

                    lstdate = c.getString(c.getColumnIndex("HomeworkDate"));

                    Log.d("SELDATE", lstdate);
                    //result.add(o);
                    cnt = cnt + 1;
                }
                while (c.moveToNext());
            }
        } /*else {
            mToast("Table Has No contain");
        }*/
        c.close();
        return lstdate ;
    }


    private void mToast(String text)
    {
        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();

    }

    public ArrayList<String> GetHWSub(String date,String work) {

        Cursor c = db.rawQuery("SELECT Subject FROM HomeworkInfo where HomeworkDate = " + "'" + date +"' AND Work='"+work+"' ", null);

        ArrayList<String> result = new ArrayList<>();
        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {

                    String sub = c.getString(c.getColumnIndex("Subject"));
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

    public ArrayList<String> GetHWDate() {

        Cursor c = db.query(true, "HomeworkInfo", new String[] { "HomeworkDate" }, null, null, "HomeworkDate",null,"HomeworkDate",null);

        ArrayList<String> result = new ArrayList<>();
        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {

                    String sub = c.getString(c.getColumnIndex("HomeworkDate"));
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

    public ArrayList<TeacherHomeworkModel> GetHomeworkAllData(String date,String work) {

        Cursor c = db.rawQuery("SELECT * FROM Homework WHERE hwDate ="+"'"+date+"' AND Work='"+work+"' ", null);


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

