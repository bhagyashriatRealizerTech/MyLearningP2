package realizer.com.schoolgenieparent.homework.newhomework;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

import realizer.com.schoolgenieparent.DrawerActivity;
import realizer.com.schoolgenieparent.R;
import realizer.com.schoolgenieparent.Utils.Config;

/**
 * Created by Bhagyashri on 10/6/2016.
 */
public class NewHomeworkActivity extends Fragment {

    String htext;
    TextView datetext;
    EditText homeworktext;
    GridView gridView;
    ImageButton addImage;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.new_homework_layout, container, false);

        Bundle b = getArguments();
        htext = b.getString("HEADERTEXT");

        ((DrawerActivity) getActivity()).getSupportActionBar().setTitle(Config.actionBarTitle(htext, getActivity()));
        ((DrawerActivity) getActivity()).getSupportActionBar().show();


        initiateView(rootView);

        new GetImagesForEvent().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        return rootView;
    }

    public void initiateView(View rootview)
    {
        addImage = (ImageButton) rootview.findViewById(R.id.addimage);
        gridView= (GridView) rootview.findViewById(R.id.gallerygridView);
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public class GetImagesForEvent extends AsyncTask<Void, Void,Void>
    {


        //ArrayList<TeacherFunCenterGalleryModel> allData = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           // loading.setVisibility(View.VISIBLE);
        }


        @Override
        protected Void doInBackground(Void... params) {

           /* allData=qr.GetImage(getid);

            for(int i=0;i<allData.size();i++)
            {

                String image1 =allData.get(i).getImage();
                File file = ImageStorage.getEventImage(image1);

                if(file != null) {
                    // BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    //  Bitmap bitmap = BitmapFactory.decodeFile(image1, bmOptions);
                    Bitmap bitmap = ImageStorage.decodeSampledBitmapFromPath(image1,150,150);
                    TeacherFunCenterGalleryModel obj = new TeacherFunCenterGalleryModel();
                    obj = allData.get(i);
                    obj.setBitmap(bitmap);
                    allData.set(i,obj);
                }
            }*/

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            /*if(allData.size()>0) {
                noDataText.setVisibility(View.GONE);
                adapter1 = new TeacherFunCenterGalleryAdapter(getActivity(), allData);
                gridView.setAdapter(adapter1);
                gridView.setFastScrollEnabled(true);
            }
            else
            {
                noDataText.setVisibility(View.VISIBLE);
            }
*/
            //loading.setVisibility(View.GONE);
        }


    }
}
