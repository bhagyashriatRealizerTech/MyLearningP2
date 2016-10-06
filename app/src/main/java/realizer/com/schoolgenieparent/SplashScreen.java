package realizer.com.schoolgenieparent;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class SplashScreen extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash_screen);

        Thread timerThread = new Thread(){
            public void run(){
                try{
                    sleep(3000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }finally{
                    SharedPreferences sharedpreferences = PreferenceManager.getDefaultSharedPreferences(SplashScreen.this);
                    String getValueBack = sharedpreferences.getString("Login", "");
                    if(getValueBack.length()==0)
                        getValueBack="false";

                    if(getValueBack.equals("false"))
                    {
                        Intent intent = new Intent(SplashScreen.this,RegisterMainActivity.class);
                        startActivity(intent);
                    }
                    else
                    {
                        Intent i = new Intent(SplashScreen.this,DrawerActivity.class);
                        startActivity(i);
                    }

                }
            }
        };
        timerThread.start();

    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }
}
