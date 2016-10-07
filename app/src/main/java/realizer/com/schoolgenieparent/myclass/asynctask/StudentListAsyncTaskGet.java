package realizer.com.schoolgenieparent.myclass.asynctask;

import android.app.ProgressDialog;
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
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import realizer.com.schoolgenieparent.Utils.Config;
import realizer.com.schoolgenieparent.Utils.OnTaskCompleted;

public class StudentListAsyncTaskGet extends AsyncTask<Void, Void,StringBuilder>
{
    ProgressDialog dialog;
    StringBuilder resultLogin;
    String std, div;
    Context myContext;
    private OnTaskCompleted callback;
    String schoolcode,deviceId,accesstoken,userID;

    public StudentListAsyncTaskGet(String std, String div,String schoolcode, Context myContext, OnTaskCompleted cb)
    {

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(myContext);
        this.std = std;
        this.div = div;
        this.myContext = myContext;
        this.callback = cb;
        this.schoolcode = schoolcode;
        deviceId = preferences.getString("DWEVICEID","");
        accesstoken = preferences.getString("AccessToken","");
        userID = preferences.getString("UidName","");

    }

    @Override
    protected void onPreExecute() {
       // super.onPreExecute();
     // dialog= ProgressDialog.show(myContext, "", "Downloading Student List...");

    }

    @Override
    protected StringBuilder doInBackground(Void... params) {
        resultLogin = new StringBuilder();

        String my= Config.URL+"GetStudentMetaDataP2PList/"+schoolcode +"/"+std + "/" +div+"/"+userID + "/" +deviceId;
        Log.d("URL", my);
        HttpGet httpGet = new HttpGet(my);
        httpGet.setHeader("AccessToken",accesstoken);
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
               // Log.e("Error", "Failed to Login");
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
      //  dialog.dismiss();
        //Pass here result of async task
         callback.onTaskCompleted(stringBuilder.toString());
    }

}
