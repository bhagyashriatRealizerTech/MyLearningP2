package realizer.com.schoolgenieparent.homework.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import realizer.com.schoolgenieparent.R;
import realizer.com.schoolgenieparent.homework.model.ParentHomeworkListModel;

public class ParentHomeworkListAdapter extends BaseAdapter {


        private static ArrayList<ParentHomeworkListModel> hList;
        private LayoutInflater mhomeworkdetails;
        private Context context1;
        boolean isImageFitToScreen;
        View convrtview;



        public ParentHomeworkListAdapter(Context context, ArrayList<ParentHomeworkListModel> homeworklist) {
            hList = homeworklist;
            mhomeworkdetails = LayoutInflater.from(context);
            context1 = context;
        }
        @Override
        public int getCount() {
            return hList.size();
        }

        @Override
        public Object getItem(int position) {

            return hList.get(position);
        }

        @Override
        public long getItemId(int position) {

            return position;
        }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        convrtview = convertView;

        if (convertView == null) {
            convertView = mhomeworkdetails.inflate(R.layout.parent_homework_list_layout, null);
            holder = new ViewHolder();
            holder.subject = (TextView) convertView.findViewById(R.id.txthomeworksubject);
            holder.homework = (TextView) convertView.findViewById(R.id.txthomework);
            holder.image = (ImageView) convertView.findViewById(R.id.imghomework);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (position % 2 == 0) {
            convertView.setBackgroundColor(Color.parseColor("#87CEFF"));
        } else {
            convertView.setBackgroundColor(Color.parseColor("#ffffff"));
        }

        holder.subject.setText(hList.get(position).getSubject());

       if(hList.get(position).getHomework().equals("NoText"))
       {
           holder.homework.setText("");
       }
        else
       {

           ViewGroup.LayoutParams layoutParams = holder.homework.getLayoutParams();
           layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
           layoutParams.height =  ViewGroup.LayoutParams.WRAP_CONTENT;
           holder.homework.setLayoutParams(layoutParams);
           holder.homework.setPadding(5,5,5,5);
           try{
               JSONArray arr = new JSONArray(hList.get(position).getHomework());
               holder.homework.setText(arr.getString(0));
           } catch (JSONException e) {
               e.printStackTrace();
           }

       }


        if(hList.get(position).getImage().equals("NoImage"))
        {

        }
        else {
            String s =holder.homework.getText().toString();
            holder.homework.setText(s+"\n Click Here To View Image");

        }

        return convertView;
    }


    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }
    static class ViewHolder
        {
            TextView subject;
            TextView homework;
            ImageView image;

        }
    }

