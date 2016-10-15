package realizer.com.schoolgenieparent.communication.asynctask;

import android.app.ProgressDialog;
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
import realizer.com.schoolgenieparent.communication.model.TeacherQuerySendModel;

/**
 * Created by Win on 1/4/2016.
 */
public class TeacherQueryAsyncTaskPost extends AsyncTask<Void, Void,StringBuilder>
{
    ProgressDialog dialog;
    StringBuilder resultLogin;
    TeacherQuerySendModel obj ;
    Context myContext;
    String deviceid;
    String accessToken;
    private OnTaskCompleted callback;

    public TeacherQueryAsyncTaskPost(TeacherQuerySendModel o, Context myContext, OnTaskCompleted cb, String deviceid, String accessToken) {
        this.myContext = myContext;
        this.callback = cb;
        this.deviceid=deviceid;
        this.accessToken=accessToken;
        obj =o;
    }

    @Override
    protected void onPreExecute() {
        // super.onPreExecute();

        //dialog=ProgressDialog.show(myContext,"","Message Sending...");

    }

    @Override
    protected StringBuilder doInBackground(Void... params) {
        resultLogin = new StringBuilder();
        HttpClient httpclient = new DefaultHttpClient();
        String url = Config.URL+"SendMessageInGroupChat";
        HttpPost httpPost = new HttpPost(url);
        System.out.println(url);

        String json = "";
       /* Calendar calendar = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
        String date = df.format(calendar.getTime());*/
        StringEntity se = null;
        JSONObject jsonobj = new JSONObject();
        try {
            jsonobj.put("DeviceId",deviceid);
            jsonobj.put("SchoolCode", obj.getSchoolCode());
            jsonobj.put("Std", obj.getStd());
            jsonobj.put("div", obj.getDiv());
            jsonobj.put("msgId", obj.getMsgId());
            jsonobj.put("FromUserId", obj.getFromUserId());
            jsonobj.put("FromName", obj.getFromUserName());
            jsonobj.put("msg", obj.getText());

            json = jsonobj.toString();
            Log.d("RES", json);
            se = new StringEntity(json);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");
            httpPost.setHeader("AccessToken",accessToken);

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
        String res= stringBuilder.toString()+"Query";
        callback.onTaskCompleted(stringBuilder.toString()+"Query");

    }
}
