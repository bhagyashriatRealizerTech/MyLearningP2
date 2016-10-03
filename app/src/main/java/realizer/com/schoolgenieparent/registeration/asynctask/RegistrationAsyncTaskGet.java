package realizer.com.schoolgenieparent.registeration.asynctask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import realizer.com.schoolgenieparent.Utils.OnTaskCompleted;

/**
 * Created by Win on 02/09/2016.
 */
public class RegistrationAsyncTaskGet extends AsyncTask<Void,Void,StringBuilder>
{
    ProgressDialog dialog;
    StringBuilder builder;
    Context mycontext;
    String functionName;
    ArrayList<String> schoolName;
    private OnTaskCompleted callback;

    public RegistrationAsyncTaskGet(Context mycontext,OnTaskCompleted cb,String functionName) {
        this.mycontext = mycontext;
        this.callback=cb;
        this.functionName=functionName;
    }

    @Override
    protected void onPreExecute() {
        //super.onPreExecute();
         dialog=ProgressDialog.show(mycontext,"","Loading Data...");
    }

    @Override
    protected StringBuilder doInBackground(Void... params) {
        builder=new StringBuilder();

        HttpGet httpGet=new HttpGet("http://realizer.realizertech.com/sjrestwcf/svcemp.svc/"+functionName);
        HttpClient client=new DefaultHttpClient();
        try
        {
            HttpResponse response=client.execute(httpGet);
            StatusLine statusLine=response.getStatusLine();
            int statusCode=statusLine.getStatusCode();
            if (statusCode==200)
            {
                HttpEntity entity=response.getEntity();
                InputStream content=entity.getContent();
                BufferedReader reader=new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line=reader.readLine())!=null)
                {
                    builder.append(line);
                }
            }
            else
            {
                Toast.makeText(mycontext, "Failed To download", Toast.LENGTH_SHORT).show();
            }
        }
        catch (ClientProtocolException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return builder;
    }

    @Override
    protected void onPostExecute(StringBuilder stringBuilder) {
        super.onPostExecute(stringBuilder);
        dialog.dismiss();

        callback.onTaskCompleted(stringBuilder.toString());

    }
}
