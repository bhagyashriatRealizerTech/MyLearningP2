/*
 * Copyright 2012 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package realizer.com.schoolgenieparent;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;
import com.google.android.gcm.GCMRegistrar;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import realizer.com.schoolgenieparent.Notification.NotificationModel;
import realizer.com.schoolgenieparent.Utils.Config;
import realizer.com.schoolgenieparent.Utils.Singleton;
import realizer.com.schoolgenieparent.backend.DatabaseQueries;
import realizer.com.schoolgenieparent.communication.model.TeacherQuery1model;
import realizer.com.schoolgenieparent.communication.model.TeacherQuerySendModel;


/**
 * IntentService responsible for handling GCM messages.
 */
public class GCMIntentService extends GCMBaseIntentService {
    private static final String KEY_TEXT_REPLY = "key_text_reply";
    SharedPreferences sharedpreferences;
    String useridglob;
    static int numChatMessages = 0;
    static int numStarMessages = 0;
    static int numAttendanceMessages = 0;
    static int numAnnouncementMessages = 0;
    static int notificatinChatID=001;
    static int notificatinViewStarID=002;
    static int notificatinAttendanceID=0;
    static int notificatinAnnouncementID=004;
    final static String GROUP_KEY_PARENT = "SchoolGenieParent";
    @SuppressWarnings("hiding")
    private static final String TAG = "GCMIntentService";

    public GCMIntentService() {
        super(Config.SENDER_ID);
    }

    @Override
    protected void onRegistered(Context context, String registrationId) {

        Log.d(TAG, "Device registered: regId = " + registrationId);
        //displayMessage(context, getString(R.string.gcm_registered));
        sharedpreferences = PreferenceManager.getDefaultSharedPreferences(context);
        useridglob = sharedpreferences.getString("UidName", "");
        String empID =useridglob;
        ServerUtilities.register(context, registrationId, empID);
    }

    @Override
    protected void onUnregistered(Context context, String registrationId) {
        //Log.i(TAG, "Device unregistered");
        //displayMessage(context, getString(R.string.gcm_unregistered));
        //SharedPreferences Pref =
        if (GCMRegistrar.isRegisteredOnServer(context)) {
            ServerUtilities.unregister(context, registrationId);
            Log.d("GCMUnregister","Done");
        } else {
            Log.d("GCMUnregister","Not Done");
        }
    }

    @Override
    protected void onMessage(Context context, Intent intent) {
        // Log.i(TAG, "Received message");
        String message = intent.getStringExtra("message");
        Log.d("Message GCM",message);
        // displayMessage(context, message);
        // notifies user
        generateNotification(context, message);
    }

    @Override
    protected void onDeletedMessages(Context context, int total) {
        // Log.i(TAG, "Received deleted messages notification");
        String message = getString(R.string.gcm_deleted, total);
        // displayMessage(context, message);
        // notifies user
        generateNotification(context, message);
    }

    @Override
    public void onError(Context context, String errorId) {
        // Log.i(TAG, "Received error: " + errorId);
        //displayMessage(context, getString(R.string.gcm_error, errorId));
    }

    @Override
    protected boolean onRecoverableError(Context context, String errorId) {
        // log message
         Log.i(TAG, "Received recoverable error: " + errorId);
        //displayMessage(context, getString(R.string.gcm_recoverable_error,
        //errorId))
        return super.onRecoverableError(context, errorId);
    }

    /**
     * Issues a notification to inform the user that server has sent a message.
     */
    private static void generateNotification(Context context, String message) {
        DatabaseQueries qr = new DatabaseQueries(context);
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        String date = df.format(calendar.getTime());
        int icon = R.mipmap.ic_launcher;
        String[] msg = message.split("@@@");
        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(context);
        Log.d("Feature Type=", msg[0]);


        if (msg[0].equals("GroupConversation")) {
            Bundle b = new Bundle();
            String studid = sharedpreferences.getString("UidName", "");
            if (msg[6].equals(studid)) {

            NotificationManagerCompat notificationManager =
                    NotificationManagerCompat.from(context);
            Log.d("Message=", message);
            Notification notification ;
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);

            String title = context.getString(R.string.app_name);
            Intent notificationIntent = new Intent(context, DrawerActivity.class);
           //// notificationIntent.putExtra("FragName","Chat");
            // set intent so it does not start a new activity
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            SharedPreferences.Editor edit = sharedpreferences.edit();
            edit.putString("FragName", "Chat");
            edit.commit();

            PendingIntent intent =
                    PendingIntent.getActivity(context, notificatinViewStarID, notificationIntent, 0);
            int num=++numStarMessages;
            builder.setAutoCancel(true);
            builder.setContentTitle("Chat");
            if (num==1) {
                builder.setContentText(msg[6]);
            }
            else {
                builder.setContentText("You have received "+num+" Messages.");
            }

            builder.setSmallIcon(icon);
            builder.setContentIntent(intent);
            builder.setOngoing(false);  //API level 16
            builder.setNumber(num);
            builder.setDefaults(Notification.DEFAULT_SOUND);
            builder.setDefaults(Notification.DEFAULT_VIBRATE);
            builder.build();
            notification = builder.getNotification();

            notificationManager.notify(notificatinViewStarID, notification);


            DatabaseQueries qr1 = new DatabaseQueries(context);
            edit.putString("ReceiverId", msg[2]);
            edit.putString("ReceiverName", msg[3]);
            edit.putString("ReceiverUrl", msg[5]);


            b.putString("ReceiverId", msg[2]);
            b.putString("ReceiverName", msg[3]);
            b.putString("ReceiverUrl", msg[5]);
            String std = sharedpreferences.getString("SyncStd", "");
            String div = sharedpreferences.getString("SyncDiv", "");


            SimpleDateFormat df1 = new SimpleDateFormat("dd/MM/yyyy kk:mm:ss");
            String date1 = df1.format(calendar.getTime());
            Date sendDate = new Date();
            try {
                sendDate = df.parse(date1);
            } catch (ParseException e) {
                e.printStackTrace();
            }

                ArrayList<TeacherQuerySendModel> allChat = qr.GetQueuryData(msg[2]);
                boolean isPresentNot = false;
                for (int i = 0; i < allChat.size(); i++) {
                    if (allChat.get(i).getText().equals(msg[4])) {
                        isPresentNot = true;
                        break;
                    } else {
                        isPresentNot = false;
                    }
                }
                long n = 0;
                if (!isPresentNot) {
                    n = qr1.insertQuery(msg[1], msg[2], msg[3], msg[6], msg[4], msg[5], date1, "true", sendDate);
                    if (n >= 0) {
                        ArrayList<TeacherQuery1model> temp = qr.GetInitiatedChat("true");

                        boolean isPresent = false;
                        for (int i = 0; i < temp.size(); i++) {
                            if (temp.get(i).getUid().equals(msg[2])) {
                                isPresent = true;
                                break;
                            }
                        }
                        int unread = qr1.GetUnreadCount(msg[2]);
                        if (isPresent) {
                            qr1.updateInitiatechat(std, div, msg[3], "true", msg[2], unread + 1, msg[5]);
                        } else {
                            long m = 0;
                            m = qr1.insertInitiatechat(msg[3], "true", msg[2], 0, msg[5]);
                            if (m > 0)
                                Log.d("Group Conversation", " Done!!!");
                            else
                                Log.d("Group Conversation", "Not Done!!!");
                        }

                        if (n > 0) {
                            NotificationModel obj = qr.GetNotificationByUserId(msg[2]);
                            if (obj.getId() == 0) {
                                n = 0;
                                NotificationModel notification1 = new NotificationModel();
                                notification1.setNotificationId(9);
                                notification1.setNotificationDate(date);
                                notification1.setNotificationtype("Message");
                                notification1.setMessage(msg[4]);
                                notification1.setIsRead("false");
                                notification1.setAdditionalData2(msg[3]);
                                notification1.setAdditionalData1(msg[5] + "@@@" + (unread + 1));
                                n = qr.InsertNotification(notification1);
                                if (Singleton.getResultReceiver() != null)
                                    Singleton.getResultReceiver().send(1, null);
                            } else {
                                n = 0;
                                obj.setMessage(msg[4]);
                                obj.setNotificationDate(date);
                                obj.setAdditionalData1(msg[5] + "@@@" + (unread + 1));

                                n = qr.UpdateNotification(obj);

                                Bundle b1 = new Bundle();
                                b1.putInt("NotificationId", 1);
                                b1.putString("NotificationDate", date);
                                b1.putString("NotificationType", "Query");
                                b1.putString("NotificationMessage", msg[4]);
                                b1.putString("IsNotificationread", "false");
                                b1.putString("AdditionalData1", msg[5] + "@@@" + (unread + 1));
                                b1.putString("AdditionalData2", msg[3]);

                                if (Singleton.getResultReceiver() != null)
                                    Singleton.getResultReceiver().send(1, b);
                            }
                        }
                        Log.d("Conversation", " Done!!!");
                    } else {
                        Log.d("Conversation", " Not Done!!!");
                    }
                }

            }

            Singleton obj = Singleton.getInstance();
            if (obj.getResultReceiver() != null) {
                obj.getResultReceiver().send(100, b);
            }
        }
    }
}
