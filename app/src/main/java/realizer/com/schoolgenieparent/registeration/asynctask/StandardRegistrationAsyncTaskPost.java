package realizer.com.schoolgenieparent.registeration.asynctask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

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
import realizer.com.schoolgenieparent.registeration.model.SchoolRegistrationModel;

/**
 * Created by Win on 07/09/2016.
 */
public class StandardRegistrationAsyncTaskPost extends AsyncTask<Void,Void,StringBuilder> {

    StringBuilder resultbuilder;
    SchoolRegistrationModel rgm;
    ProgressDialog dialog;
    Context mycontext;
    private OnTaskCompleted callback;

    public StandardRegistrationAsyncTaskPost(SchoolRegistrationModel rgm, Context mycontext, OnTaskCompleted cb) {
        this.rgm = rgm;
        this.mycontext=mycontext;
        this.callback=cb;
    }

    @Override
    protected void onPreExecute() {
        //super.onPreExecute();
       // dialog=ProgressDialog.show(mycontext,"","Inserting Data...!");
    }

    @Override
    protected StringBuilder doInBackground(Void... params) {
        resultbuilder =new StringBuilder();
        HttpClient httpClient=new DefaultHttpClient();
        String url= Config.URL+"AddStdAndDiv";
        HttpPost httpPost=new HttpPost(url);
        String json="";
        StringEntity se=null;
        JSONObject jsonObject=new JSONObject();
        try
        {
            jsonObject.put("schoolCode",rgm.getCode());
            jsonObject.put("std",rgm.getStd());
            jsonObject.put("div", rgm.getDiv());
           

            json=jsonObject.toString();

            se=new StringEntity(json);
            httpPost.setEntity(se);
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            HttpResponse httpResponse=httpClient.execute(httpPost);
            StatusLine statusLine=httpResponse.getStatusLine();
            int statuscode=statusLine.getStatusCode();
            if (statuscode==200)
            {
                HttpEntity entity=httpResponse.getEntity();
                InputStream content=entity.getContent();
                BufferedReader reader=new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line=reader.readLine())!=null)
                {
                    resultbuilder.append(line);
                }
            }
            else
            {

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return resultbuilder;
    }

    @Override
    protected void onPostExecute(StringBuilder stringBuilder) {
        super.onPostExecute(stringBuilder);
      //  dialog.dismiss();
        String res= stringBuilder.toString()+"@@@STDREG";
        callback.onTaskCompleted(res);
    }

}
