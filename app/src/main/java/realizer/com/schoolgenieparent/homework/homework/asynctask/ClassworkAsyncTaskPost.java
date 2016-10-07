package realizer.com.schoolgenieparent.homework.homework.asynctask;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import realizer.com.schoolgenieparent.Utils.Config;
import realizer.com.schoolgenieparent.Utils.OnTaskCompleted;
import realizer.com.schoolgenieparent.homework.model.ParentHomeworkListModel;

/**
 * Created by Win on 07-09-2016.
 */
public class ClassworkAsyncTaskPost extends AsyncTask<Void, Void,StringBuilder>
{
    //ProgressDialog dialog;
    StringBuilder resultLogin;
    ParentHomeworkListModel obj ;
    Context myContext;
    private OnTaskCompleted callback;

    public ClassworkAsyncTaskPost(ParentHomeworkListModel o, Context myContext, OnTaskCompleted cb) {
        this.myContext = myContext;
        this.callback = cb;
        obj =o;
    }

    @Override
    protected void onPreExecute() {
        // super.onPreExecute();
        //dialog= ProgressDialog.show(myContext, "", "Please wait Classwork is Loading...");
    }

    @Override
    protected StringBuilder doInBackground(Void... params) {
        resultLogin = new StringBuilder();
        HttpClient httpclient = new DefaultHttpClient();
        String url = Config.URL+"fetchClassWork";
        HttpPost httpPost = new HttpPost(url);

        System.out.println(url);
        String json = "";
        StringEntity se = null;
        JSONObject jsonobj = new JSONObject();
        try {
            jsonobj.put("schoolCode",obj.getSchoolcode());
            jsonobj.put("cwDate",obj.getHwdate());
            jsonobj.put("std",obj.getStandard());
            jsonobj.put("division",obj.getDivision());
            jsonobj.put("subject",obj.getSubject());

            json = jsonobj.toString();
            Log.d("RES", json);
            se = new StringEntity(json);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            httpPost.setEntity(se);
            HttpResponse httpResponse = httpclient.execute(httpPost);
            StatusLine statusLine = httpResponse.getStatusLine();

            int statusCode = statusLine.getStatusCode();
            Log.d("StatusCode", "" + statusCode);
            if(statusCode == 200)
            {
                HttpEntity entity = httpResponse.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while((line=reader.readLine()) != null)
                {
                    resultLogin.append(line);
                }
            }
            else
            {
                // Log.e("Error", "Failed to Login");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return resultLogin;
    }

    @Override
    protected void onPostExecute(StringBuilder stringBuilder) {
        super.onPostExecute(stringBuilder);
        //dialog.dismiss();
        Log.d("RESULTASYNC", stringBuilder.toString());
        //Pass here result of async task
        stringBuilder.append("@@@Classwork");
        callback.onTaskCompleted(stringBuilder.toString());
    }
}
