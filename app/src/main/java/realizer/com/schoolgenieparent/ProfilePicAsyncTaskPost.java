package realizer.com.schoolgenieparent;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
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
 * Created by Win on 1/4/2016.
 */
public class ProfilePicAsyncTaskPost extends AsyncTask<Void, Void,StringBuilder>
{
    ProgressDialog dialog;
    StringBuilder resultUpload;
    String userId,profileImage;
    Context myContext;
    private OnTaskCompleted callback;

    public ProfilePicAsyncTaskPost(Context myContext,OnTaskCompleted cb,String userId,String profileImage) {
        this.myContext = myContext;
        this.callback = cb;
        this.userId=userId;
        this.profileImage=profileImage;
    }

    @Override
    protected void onPreExecute() {
         //super.onPreExecute();
        dialog= ProgressDialog.show(myContext, "", "Please wait Uploading image ...");
    }

    @Override
    protected StringBuilder doInBackground(Void... params) {
        resultUpload = new StringBuilder();
        HttpClient httpclient = new DefaultHttpClient();
        String url = Config.URL+"uploadStudentThumbNail";
        HttpPost httpPost = new HttpPost(url);

        System.out.println(url);
        String json = "";
        StringEntity se = null;
        JSONObject jsonobj = new JSONObject();
        try {
            jsonobj.put("UserId",userId);
            jsonobj.put("Base64", profileImage);

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
                    resultUpload.append(line);
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
        return resultUpload;
    }

    @Override
    protected void onPostExecute(StringBuilder stringBuilder) {
        super.onPostExecute(stringBuilder);
        dialog.dismiss();
        Log.d("RESULTASYNC", stringBuilder.toString());
        //Pass here result of async task
        callback.onTaskCompleted(stringBuilder.toString());
    }
}
