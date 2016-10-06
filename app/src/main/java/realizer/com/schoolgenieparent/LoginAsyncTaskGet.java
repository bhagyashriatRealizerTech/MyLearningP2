package realizer.com.schoolgenieparent;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import realizer.com.schoolgenieparent.Utils.OnTaskCompleted;
import realizer.com.schoolgenieparent.Utils.Config;


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

public class LoginAsyncTaskGet extends AsyncTask<Void, Void,StringBuilder>
{
    ProgressDialog dialog;
    StringBuilder resultLogin;
    String uName, password;
    String deviceID;
    Context myContext;
    private OnTaskCompleted callback;

    public LoginAsyncTaskGet(String uName, String password,String deviceID ,Context myContext, OnTaskCompleted cb)
    {
        this.uName = uName;
        this.password = password;
        this.myContext = myContext;
        this.callback = cb;
        this.deviceID = deviceID;
    }

    @Override
    protected void onPreExecute() {
       // super.onPreExecute();
     // dialog= ProgressDialog.show(myContext, "", "Authenticating credentials...");

    }

    @Override
    protected StringBuilder doInBackground(Void... params) {
        resultLogin = new StringBuilder();

        //String my= Config.URL+"StudentLogin/"+ uName + "/" +password+ "/" +deviceID;
        String my= Config.URL+"StudentLogin/"+ uName + "/" +password;
        Log.d("URL", my);
        HttpGet httpGet = new HttpGet(my);
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
         stringBuilder.append("@@@LoginIN");
         callback.onTaskCompleted(stringBuilder.toString());
    }

}
