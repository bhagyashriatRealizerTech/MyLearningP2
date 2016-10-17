package realizer.com.schoolgenieparent.homework.asynctask;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Base64;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import realizer.com.schoolgenieparent.Utils.Config;
import realizer.com.schoolgenieparent.Utils.ImageStorage;
import realizer.com.schoolgenieparent.Utils.OnTaskCompleted;
import realizer.com.schoolgenieparent.homework.model.TeacherHomeworkModel;

/**
 * Created by Win on 1/4/2016.
 */
public class TeacherHomeworkAsyncTaskPost extends AsyncTask<Void, Void,StringBuilder> {
    ProgressDialog dialog;
    StringBuilder resultLogin;
    TeacherHomeworkModel obj;
    Context myContext;
    String Userid;
    ArrayList<String> presence;
    ArrayList<String> absent;
    int precount, abcount;
    private OnTaskCompleted callback;
    String flag;
    JSONArray uploadimage;

    public TeacherHomeworkAsyncTaskPost(TeacherHomeworkModel o, Context myContext, OnTaskCompleted cb, String flag) {

        this.myContext = myContext;
        this.callback = cb;
        obj = o;
        this.flag = flag;
        uploadimage = new JSONArray();
        try {
            JSONArray arr = new JSONArray(obj.getHwImage64Lst());
            if(arr.length()>0) {
                for (int i = 0; i < arr.length(); i++) {
                    File file = ImageStorage.getEventImage(arr.getString(i).toString());
                    String imagebse64 = "";
                    if (file != null) {
                        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                        Bitmap bitmap = BitmapFactory.decodeFile(arr.getString(i), bmOptions);
                        ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos1); //bm is the bitmap object
                        byte[] b1 = baos1.toByteArray();
                        imagebse64 = Base64.encodeToString(b1, Base64.DEFAULT);
                        uploadimage.put(i, imagebse64);
                    }
                }
            }
            else
            {
                uploadimage = arr;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onPreExecute() {
        // super.onPreExecute();
       if(flag.equals("true")) {
           dialog = ProgressDialog.show(myContext, "", "Please Wait saving " + obj.getWork() + " ....");
       }

    }

    @Override
    protected StringBuilder doInBackground(Void... params) {
        resultLogin = new StringBuilder();
        HttpClient httpclient = new DefaultHttpClient();
        String url = Config.URL + "uploadP2PHomeWork";
        HttpPost httpPost = new HttpPost(url);

        System.out.println(url);

        String json = "";
        StringEntity se = null;

        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(myContext);
        String scode = sharedpreferences.getString("SchoolCode", "");
        String accesstoken = sharedpreferences.getString("AccessToken", "");


        JSONObject jobj = new JSONObject();
        try {
            jobj.put("SchoolCode", scode);
            String date = obj.getHwDate();
            String date1[] = date.split("/");
            String resdate = date1[0] + "/" + date1[1] + "/" + date1[2];
            jobj.put("hwDate", resdate);
            jobj.put("Std", obj.getStd());
            jobj.put("div", obj.getDiv());
            jobj.put("subject", obj.getSubject());
            jobj.put("givenBy", obj.getGivenBy());

            jobj.put("hwImage64Lst", uploadimage);

            JSONArray arr1 = new JSONArray();
            arr1.put(0, obj.getHwTxtLst());
            jobj.put("hwTxtLst", arr1);


            json = jobj.toString();

            Log.d("STRINGOP", json);
            se = new StringEntity(json);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            httpPost.setHeader("AccessToken", accesstoken);

            httpPost.setEntity(se);
            HttpResponse httpResponse = httpclient.execute(httpPost);
            StatusLine statusLine = httpResponse.getStatusLine();

            int statusCode = statusLine.getStatusCode();
            Log.d("StatusCode", "" + statusCode);
            if (statusCode == 200) {
                HttpEntity entity = httpResponse.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    resultLogin.append(line);
                }

            } else {
                // Log.e("Error", "Failed to Login");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        return resultLogin;
    }

    @Override
    protected void onPostExecute(StringBuilder stringBuilder) {
        super.onPostExecute(stringBuilder);
        if(flag.equals("true")) {
            dialog.dismiss();
        }
        Log.d("RESULTASYNC", stringBuilder.toString());
        //Pass here result of async task
        stringBuilder.append("@@@UploadHW@@@"+ Integer.valueOf(obj.getHid())+"@@@Homework");
        callback.onTaskCompleted(stringBuilder.toString());

    }
}
