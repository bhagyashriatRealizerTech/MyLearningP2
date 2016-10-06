package realizer.com.schoolgenieparent;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.Spanned;
import android.widget.EditText;

/**
 * Created by Bhagyashri on 10/6/2016.
 */
public class SchoolRegistrationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.school_registartion_activity);

        EditText schoolname = (EditText) findViewById(R.id.edt_school_name);

       /* schoolname.setFilters(new InputFilter[]{
                new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence cs, int start,
                                               int end, Spanned spanned, int dStart, int dEnd) {
                        // TODO Auto-generated method stub
                        if (cs.equals("")) { // for backspace
                            return cs;
                        }
                        if (cs.toString().matches("[0-9_$&+,:;=?@#|'<>.^*()%!-]+")) {
                            return cs;
                        }
                        return "";
                    }
                }
        });*/

    }
}
