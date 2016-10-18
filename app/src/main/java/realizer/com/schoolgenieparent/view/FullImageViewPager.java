package realizer.com.schoolgenieparent.view;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import java.lang.reflect.Field;
import java.util.List;

import realizer.com.schoolgenieparent.R;
import realizer.com.schoolgenieparent.backend.DatabaseQueries;
import realizer.com.schoolgenieparent.homework.model.TeacherHomeworkModel;

public class FullImageViewPager extends FragmentActivity {

	public Context mContext;
	// Declare Variable
	String uuid;
    int position=0;
	FullImageViewPagerAdapter pageradapter;
	String activityName;
    DatabaseQueries db;
	List<TeacherHomeworkModel> chatDownloadedThumbnailList;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Get the view from view_pager.xml
		setContentView(R.layout.fragment_page);
        db=new DatabaseQueries(FullImageViewPager.this);
		// Retrieve data from MainActivity on item click event
        Bundle bundle = getIntent().getExtras();
        uuid = bundle.getString("HWUUID");
        activityName = bundle.getString("HEADERTEXT");
		mContext = FullImageViewPager.this;

		final ViewPager viewpager = (ViewPager) findViewById(R.id.pager1);

		chatDownloadedThumbnailList=db.GetHomeworkAllImages(activityName);
      //getting current image position
        for (int i=0;i<chatDownloadedThumbnailList.size();i++)
        {
            if (uuid.equals(chatDownloadedThumbnailList.get(i).getHwUUID()))
            {
                position=i;
                break;
            }
        }
		// Set the images into ViewPager
		pageradapter = new FullImageViewPagerAdapter(mContext, chatDownloadedThumbnailList);
		viewpager.setAdapter(pageradapter);
		// Show images following the position
		viewpager.setCurrentItem(position);
	}

}