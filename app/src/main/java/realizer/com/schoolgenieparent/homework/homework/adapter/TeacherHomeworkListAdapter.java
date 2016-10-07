package realizer.com.schoolgenieparent.homework.homework.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import realizer.com.schoolgenieparent.R;
import realizer.com.schoolgenieparent.homework.model.TeacherHomeworkListModel;

public class TeacherHomeworkListAdapter extends BaseAdapter {


        private static ArrayList<TeacherHomeworkListModel> hList;
        private LayoutInflater mhomeworkdetails;
        private Context context1;
        boolean isImageFitToScreen;
        View convrtview;



        public TeacherHomeworkListAdapter(Context context, ArrayList<TeacherHomeworkListModel> homeworklist) {
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
    public int getViewTypeCount() {
        return hList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        convrtview = convertView;

        if (convertView == null) {
            convertView = mhomeworkdetails.inflate(R.layout.teacher_homework_list_layout, null);
            holder = new ViewHolder();
            holder.subject = (TextView) convertView.findViewById(R.id.txthomeworksubject);
            holder.homework = (TextView) convertView.findViewById(R.id.txthomework);
            holder.image = (ImageView) convertView.findViewById(R.id.imghomework);
            holder.homeworktext = (TextView) convertView.findViewById(R.id.txthomework1);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        if (position % 2 == 0) {
            convertView.setBackgroundColor(Color.parseColor("#87CEFF"));
        } else {
            convertView.setBackgroundColor(Color.parseColor("#ffffff"));
        }


        if(hList.get(position).getHasSync().equalsIgnoreCase("true")) {
           holder.image.setImageResource(R.drawable.homework_send);
        }
        else
            holder.image.setImageResource(R.drawable.homework_pending);


        //holder.subject.setText(hList.get(position).getSubject()+" : "+hList.get(position).getGivenBy());
        holder.subject.setText(hList.get(position).getSubject());


        ViewGroup.LayoutParams layoutParams1 = holder.homework.getLayoutParams();
        layoutParams1.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams1.height =  ViewGroup.LayoutParams.WRAP_CONTENT;
        holder.homework.setLayoutParams(layoutParams1);
        holder.homework.setPadding(15, 0, 5, 0);

        ViewGroup.LayoutParams layoutParams = holder.homeworktext.getLayoutParams();
        layoutParams.width = ViewGroup.LayoutParams.MATCH_PARENT;
        layoutParams.height =  ViewGroup.LayoutParams.WRAP_CONTENT;
        holder.homeworktext.setLayoutParams(layoutParams);
        holder.homeworktext.setPadding(15, 0, 5, 0);

        if(hList.get(position).getHomework().equals("NoText"))
        {
            holder.homeworktext.setText("");
        }
        else {
            holder.homeworktext.setText(hList.get(position).getHomework());
        }

        if(hList.get(position).getImage().equals("NoImage"))
        {
            holder.homework.setText("");
        }
        else {
            String s =holder.homework.getText().toString();
            holder.homework.setText("Click Here To View Image");
            holder.homework.setPaintFlags(   holder.homework.getPaintFlags() |   Paint.UNDERLINE_TEXT_FLAG);

        }


        return convertView;
    }

    static class ViewHolder
        {
            TextView subject;
            TextView homework;
            TextView homeworktext;
            ImageView image;

        }
    }

