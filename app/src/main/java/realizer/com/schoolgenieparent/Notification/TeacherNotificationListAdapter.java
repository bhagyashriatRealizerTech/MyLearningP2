package realizer.com.schoolgenieparent.Notification;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import realizer.com.schoolgenieparent.R;
import realizer.com.schoolgenieparent.Utils.Config;
import realizer.com.schoolgenieparent.Utils.GetImages;
import realizer.com.schoolgenieparent.Utils.ImageStorage;
import realizer.com.schoolgenieparent.Utils.SwipeLayout;
import realizer.com.schoolgenieparent.Utils.Utility;
import realizer.com.schoolgenieparent.backend.DALQueris;
import realizer.com.schoolgenieparent.backend.DatabaseQueries;
import realizer.com.schoolgenieparent.communication.model.ParentQueriesTeacherNameListModel;

public class TeacherNotificationListAdapter extends BaseAdapter {

   private static ArrayList<NotificationModel> notifications;
   private LayoutInflater mNotification;
   private Context context1;
   boolean isImageFitToScreen;
   public SwipeLayout prevSwipedLayout;
   public SwipeLayout swipeLayout;
   View convrtview;
   float x1, x2;
    DatabaseQueries qr;

    public TeacherNotificationListAdapter(Context context, ArrayList<NotificationModel> notificationList) {
            notifications = notificationList;
            mNotification = LayoutInflater.from(context);
            context1 = context;
            swipeLayout = null;
            prevSwipedLayout = null;
            qr = new DatabaseQueries(context);
        }

        @Override
        public int getCount() {
            return notifications.size();
        }

        @Override
        public Object getItem(int position) {

            return notifications.get(position);
        }

        @Override
        public long getItemId(int position) {

            return position;
        }

    @Override
    public int getViewTypeCount() {
        return notifications.size();
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
            convertView = mNotification.inflate(R.layout.teacher_notification_list_layout, null);
            holder = new ViewHolder();
            holder.notificationText = (TextView) convertView.findViewById(R.id.txtmessage);
            holder.notificationDate = (TextView) convertView.findViewById(R.id.txtdate);
            holder.type = (TextView) convertView.findViewById(R.id.txtnotificationtype);
            holder.unreadCount = (TextView) convertView.findViewById(R.id.txtunreadcount);
            holder.notificationImage = (ImageView) convertView.findViewById(R.id.img_user_image);
            holder.txtinitial = (TextView) convertView.findViewById(R.id.txtinitial);
            holder.outerLayer = (LinearLayout) convertView.findViewById(R.id.outerLayer);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        String notificationData = "";
        String date = notifications.get(position).getNotificationDate();
        holder.unreadCount.setVisibility(View.INVISIBLE);

        if(notifications.get(position).getNotificationtype().equalsIgnoreCase("Homework") || notifications.get(position).getNotificationtype().equalsIgnoreCase("Classwork"))
        {
            holder.outerLayer.setVisibility(View.VISIBLE);
            DALQueris dbQ=new DALQueris(context1);
            ParentQueriesTeacherNameListModel result=dbQ.GetQueryTableData(notifications.get(position).getAdditionalData1());
            notificationData = "Downloaded "+notifications.get(position).getNotificationtype()+" For "+
                    notifications.get(position).getMessage()+" From "+result.getName();
            holder.notificationImage.setImageResource(R.drawable.homework_icon);
           /* notificationData = notifications.get(position).getAdditionalData1().split("@@@")[2]+" "+
                    notifications.get(position).getNotificationtype()+" "+notifications.get(position).getMessage()
                    +" "+notifications.get(position).getAdditionalData1().split("@@@")[0]+" "+
                    notifications.get(position).getAdditionalData1().split("@@@")[1];

            holder.notificationImage.setImageResource(R.drawable.homework_icon);*/
        }
        else  if(notifications.get(position).getNotificationtype().equalsIgnoreCase("HomeworkUpload") || notifications.get(position).getNotificationtype().equalsIgnoreCase("ClassworkUpload"))
        {
            holder.outerLayer.setVisibility(View.VISIBLE);
            notificationData = notifications.get(position).getAdditionalData1().split("@@@")[2]+" "+
                    notifications.get(position).getNotificationtype()+" "+notifications.get(position).getMessage()
                    +" "+notifications.get(position).getAdditionalData1().split("@@@")[0]+" "+
                    notifications.get(position).getAdditionalData1().split("@@@")[1];

            holder.notificationImage.setImageResource(R.drawable.homework_icon);
        }
        else if(notifications.get(position).getNotificationtype().equalsIgnoreCase("Message"))
        {
            if (position==0)
            {
                holder.outerLayer.setVisibility(View.VISIBLE);
                notificationData = "Recieved Message From "+
                        notifications.get(position).getAdditionalData2()+"\nMessage : "+notifications.get(position).getMessage();

                String imageurl[]= notifications.get(position).getAdditionalData1().trim().split("@@@");
                if(imageurl.length == 3) {
                    if (imageurl[0] != null && !imageurl[0].equals("") && !imageurl[0].equalsIgnoreCase("null")) {
                        String urlString = imageurl[0];

                        String newURL = Utility.getURLImage(urlString);
                        holder.notificationImage.setVisibility(View.VISIBLE);
                        if (!ImageStorage.checkifImageExists(newURL.split("/")[newURL.split("/").length - 1]))
                            new GetImages(newURL, holder.notificationImage,holder.txtinitial,notifications.get(position).getMessage(), newURL.split("/")[newURL.split("/").length - 1]).execute(newURL);
                        else {
                            File image = ImageStorage.getImage(newURL.split("/")[newURL.split("/").length - 1]);
                            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                            Bitmap bitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);
                            holder.notificationImage.setImageBitmap(bitmap);
                        }
                    } else
                        holder.notificationImage.setImageResource(R.drawable.chat_icon);
                }
                else
                    holder.notificationImage.setImageResource(R.drawable.chat_icon);

                if(notifications.get(position).getAdditionalData1().split("@@@")[1].equals("0"))
                {
                    holder.unreadCount.setVisibility(View.INVISIBLE);
                }
                else
                {
                    holder.unreadCount.setVisibility(View.VISIBLE);
                    holder.unreadCount.setText(notifications.get(position).getAdditionalData1().split("@@@")[1]);
                }
                date = notifications.get(position).getNotificationDate().trim().split(" ")[0];
                String sdate[]=date.split("/");
                String newDate=sdate[1]+"/"+sdate[0]+"/"+sdate[2];

                holder.type.setText(notifications.get(position).getNotificationtype());
                holder.notificationDate.setText(Config.getDate(newDate, "D"));
                holder.notificationText.setText(notificationData);

            }
            else
            {
                holder.outerLayer.setVisibility(View.GONE);
                qr.deleteNotificationRow(notifications.get(position).getId());
            }

          /*  if (notifications.size()>1 && notifications.get(position).getNotificationtype().equalsIgnoreCase("Message"))
            {
                qr.deleteNotificationRow(notifications.get(notifications.size()-1).getId());
            }*/
        }

        return convertView;
    }

        static class ViewHolder
        {
            TextView notificationText,notificationDate,unreadCount,type,txtinitial;
            ImageView notificationImage;
            SwipeLayout swipeLayout;
            LinearLayout outerLayer;
        }
    }

