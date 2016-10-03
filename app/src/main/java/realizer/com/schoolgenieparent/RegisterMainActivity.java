package realizer.com.schoolgenieparent;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import realizer.com.schoolgenieparent.registeration.RegistrationActivity;

/**
 * Created by Win on 26/08/2016.
 */
public class RegisterMainActivity extends Activity
{
    Button btnMainRegister,btnMainLogin;
    TextView txtWelcomSch,txtSchoolNm;
    LinearLayout LinerRegLayout,LinearLoginLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        TextView SchoolNm= (TextView) findViewById(R.id.txtSchoolNm);
        TextView txtslogan= (TextView) findViewById(R.id.txtSlogan);
        Typeface face= Typeface.createFromAsset(RegisterMainActivity.this.getAssets(), "fonts/font.ttf");
        //txtslogan.setTypeface(face);
        btnMainLogin= (Button) findViewById(R.id.btn_main_login);
        btnMainRegister= (Button) findViewById(R.id.btn_main_register);
        txtWelcomSch= (TextView) findViewById(R.id.txtWelcomeSchGen);
        LinerRegLayout= (LinearLayout) findViewById(R.id.RegisterLayout);
        LinearLoginLayout= (LinearLayout) findViewById(R.id.LoginLayout);
        txtSchoolNm= (TextView) findViewById(R.id.txtSchoolNm);

        txtWelcomSch.setTypeface(face);
        txtWelcomSch.setShadowLayer(25, 0, 0, Color.BLACK);
        btnMainLogin.setTypeface(face);
        btnMainRegister.setTypeface(face);
        Animation animationLeftin = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.left_in);
        Animation animationRightin= AnimationUtils.loadAnimation(getApplicationContext(), R.anim.right_in);
        LinerRegLayout.setAnimation(animationLeftin);
        LinearLoginLayout.setAnimation(animationRightin);

        btnMainRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterMainActivity.this, RegistrationActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnMainLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent2=new Intent(RegisterMainActivity.this,MainActivity.class);
                startActivity(intent2);
                finish();
            }
        });

    }
}
