package realizer.com.schoolgenieparent.homework.asynctask;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
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
public class HomeworkAsyncTaskPost extends AsyncTask<Void, Void,StringBuilder>
{
    //ProgressDialog dialog;
    StringBuilder resultLogin;
    ParentHomeworkListModel obj ;
    Context myContext;
    private OnTaskCompleted callback;

    public HomeworkAsyncTaskPost(ParentHomeworkListModel o, Context myContext, OnTaskCompleted cb) {
        this.myContext = myContext;
        this.callback = cb;
        obj =o;
    }

    @Override
    protected void onPreExecute() {
        // super.onPreExecute();
        //dialog= ProgressDialog.show(myContext, "", "Please wait Homework is Loading...");
    }

    @Override
    protected StringBuilder doInBackground(Void... params) {
        resultLogin = new StringBuilder();
        SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(myContext);
        String userid = sharedpreferences.getString("UidName", "");
        String deviceid = sharedpreferences.getString("DWEVICEID", "");
        String accesstoken = sharedpreferences.getString("AccessToken", "");

        String date = obj.getHwdate();
        String date1[] = date.split("/");
        String url = Config.URL+"fetchP2PHomework/"+obj.getSchoolcode()+"/"+obj.getStandard()+"/"+obj.getDivision()+"/"+obj.getHwdate().split("/")[2]+"/"+
                obj.getHwdate().split("/")[0]+"/"+obj.getHwdate().split("/")[1]+"/"+userid+"/"+deviceid;
        HttpGet httpGet = new HttpGet(url);
        httpGet.setHeader("AccessToken", accesstoken);
        HttpClient client = new DefaultHttpClient();
        try
        {
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();

            int statusCode = statusLine.getStatusCode();
            if(statusCode == 200)
            {
                HttpEntity entity = response.getEntity();
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
                Log.e("Error", "Failed to Login");
            }
        }
        catch(ClientProtocolException e)
        {
            e.printStackTrace();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            client.getConnectionManager().closeExpiredConnections();
            client.getConnectionManager().shutdown();
        }
        return resultLogin;
    }

    @Override
    protected void onPostExecute(StringBuilder stringBuilder) {
        super.onPostExecute(stringBuilder);
        //dialog.dismiss();
        Log.d("RESULTASYNC", stringBuilder.toString());
        //Pass here result of async task
        stringBuilder.append("@@@Homework");
        callback.onTaskCompleted(stringBuilder.toString());
    }
}
