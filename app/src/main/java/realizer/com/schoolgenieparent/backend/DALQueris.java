package realizer.com.schoolgenieparent.backend;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

//import com.realizer.schoolgenie.parent.chat.model.ParentQueriesTeacherNameListModel;

import java.util.ArrayList;

import realizer.com.schoolgenieparent.communication.model.ParentQueriesTeacherNameListModel;

/**
 * Created by shree on 1/7/2016.
 */
public class DALQueris {
    SQLiteDatabase db;
    SqliteHelper myhelper;
    Context context;

    public DALQueris(Context context) {
        this.context = context;
        SQLiteOpenHelper myHelper = new SqliteHelper(context);
        this.db = myHelper.getWritableDatabase();
    }

    public long insertTeacherSubInfo(String Stndard,String teachername,String teacherid,String division,String teachersubject,String thumbnail)
    {
        ContentValues conV = new ContentValues();
        conV.put("Stndard", Stndard);
        conV.put("TeacherName", teachername);
        conV.put("TeacherId", teacherid);
        conV.put("Division", division);
        conV.put("TeacherSubject", teachersubject);
        conV.put("ThumbnailURL", thumbnail);
        long newRowInserted = db.insert("QueryInfo", null, conV);
        return newRowInserted;
    }

//    public ArrayList<ParentQueriesTeacherNameListModel> GetQueryTableData() {
//        Cursor c = db.rawQuery("SELECT * FROM QueryInfo ", null);
//        ArrayList<ParentQueriesTeacherNameListModel> result = new ArrayList<>();
//        int cnt = 1;
//        if (c != null) {
//            if (c.moveToFirst()) {
//                System.out.print("while moving  - C != null");
//                do {
//                    String subject = c.getString(c.getColumnIndex("TeacherSubject"));
//                    String teacher = c.getString(c.getColumnIndex("TeacherName"));
//                    String tid = c.getString(c.getColumnIndex("TeacherId"));
//
//                    ParentQueriesTeacherNameListModel o = new ParentQueriesTeacherNameListModel();
//                    o.setSubname(subject);
//                    o.setName(teacher);
//                    o.setTeacherid(tid);
//                    result.add(o);
//                    cnt = cnt+1;
//                }
//                while (c.moveToNext());
//            }
//        }
//        c.close();
//        return result;
//    }
    public ArrayList<String> GetAllSub()
    {

        Cursor c = db.rawQuery("SELECT TeacherSubject FROM QueryInfo ", null);
        ArrayList<String> result = new ArrayList<>();

        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {
                    String subject = c.getString(c.getColumnIndex("TeacherSubject"));

                    result.add(subject);
                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        }
        c.close();
        return  result;
    }
    public ParentQueriesTeacherNameListModel GetQueryTableData(String teacherid) {
        Cursor c = db.rawQuery("SELECT * FROM QueryInfo WHERE TeacherId='"+teacherid+"' ", null);
        ParentQueriesTeacherNameListModel result = new ParentQueriesTeacherNameListModel();
        int cnt = 1;
        if (c != null) {
            if (c.moveToFirst()) {
                System.out.print("while moving  - C != null");
                do {

                    result.setSubname(c.getString(c.getColumnIndex("TeacherSubject")));
                    result.setName( c.getString(c.getColumnIndex("TeacherName")));
                    result.setTeacherid(c.getString(c.getColumnIndex("TeacherId")));
                    result.setThumbnail(c.getString(c.getColumnIndex("ThumbnailURL")));
                    cnt = cnt+1;
                }
                while (c.moveToNext());
            }
        }
        c.close();
        return result;
    }
}
