package realizer.com.schoolgenieparent.trackpupil.asynctask;

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

/**
 * Created by shree on 11/17/2015.
 */
public class TrackingAsyncTaskAuto extends AsyncTask<Void, Void,StringBuilder> {

    //Declare controls
    ProgressDialog dialog;

    // Declare Variables
    StringBuilder resultLogin;
    String DriverUserId;
    String UniqueNo;
    Context myContext;
    String accessToken,deviceid,userId;
    private OnTaskCompleted listener;

    public TrackingAsyncTaskAuto(OnTaskCompleted listener, String username,String userid, Context _myContext) {

        this.listener = listener;
        this.DriverUserId = username;
        this.UniqueNo = userid;
        this.myContext = _myContext;
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(_myContext);
        accessToken=preferences.getString("AccessToken","");
        deviceid=preferences.getString("DWEVICEID","");
        userId=preferences.getString("StudentUserID","");
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        //dialog=ProgressDialog.show(myContext,"","loading Data ...");
    }

    @Override
    protected StringBuilder doInBackground(Void... params) {
        resultLogin = new StringBuilder();
        String my= Config.URL+"retrievePupilLocation/"+DriverUserId+"/"+UniqueNo+"/"+userId+"/"+deviceid;
        HttpGet httpGet = new HttpGet(my);
        httpGet.setHeader("AccessToken",accessToken);
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
        listener.onTaskCompleted(stringBuilder.toString());
    }

}

