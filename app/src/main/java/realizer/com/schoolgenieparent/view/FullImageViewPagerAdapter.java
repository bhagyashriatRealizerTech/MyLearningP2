package realizer.com.schoolgenieparent.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

import realizer.com.schoolgenieparent.R;
import realizer.com.schoolgenieparent.Utils.GetImages;
import realizer.com.schoolgenieparent.Utils.ImageStorage;
import realizer.com.schoolgenieparent.Utils.Utility;
import realizer.com.schoolgenieparent.homework.model.TeacherHomeworkModel;

public class FullImageViewPagerAdapter extends PagerAdapter {

    private static List<TeacherHomeworkModel> attachmentList;
    private Context context;
    private LayoutInflater inflater;
    private ViewHolder holder;
    String[] IMG;

    public FullImageViewPagerAdapter(Context context, List<TeacherHomeworkModel> attachmentList) {
        this.context = context;
        FullImageViewPagerAdapter.attachmentList = attachmentList;
        IMG = new String[attachmentList.size()];
    }

    @Override
    public int getCount() {
        return attachmentList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        //Inflate the view
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = inflater.inflate(R.layout.fullimageview_parent, container, false);

        holder = new ViewHolder();

        holder.imgview = (ImageView) itemView.findViewById(R.id.imageView);
        holder.txtcnt = (TextView) itemView.findViewById(R.id.txtcounter);
        holder.txtcnt.setMovementMethod(new ScrollingMovementMethod());
        //holder.txtcnt.setText("" + (position + 1) + " / " + attachmentList.size());
        holder.txtcnt.setText(attachmentList.get(position).getHwTxtLst());
        if (attachmentList.get(position).getHwImage64Lst().contains("http"))
        {
            String newURL=new Utility().getURLImage(attachmentList.get(position).getHwImage64Lst());
            if(!ImageStorage.checkifImageExists(newURL.split("/")[newURL.split("/").length - 1]))
                new GetImages(newURL,holder.imgview,null,null,newURL.split("/")[newURL.split("/").length-1]).execute(newURL);
            else
            {
                File image = ImageStorage.getImage(newURL.split("/")[newURL.split("/").length - 1]);
                BitmapFactory.Options bmOptions1 = new BitmapFactory.Options();
                Bitmap decodedByte = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions1);
                holder.imgview.setImageBitmap(decodedByte);
            }
        }
        else
        {
            try {
                JSONArray jarr = new JSONArray(attachmentList.get(position).getHwImage64Lst());
                IMG = new String[jarr.length()];
                for(int i=0;i<jarr.length();i++)
                {
                    IMG[i] = jarr.getString(i);
                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    Bitmap decodedByte = BitmapFactory.decodeFile(IMG[i], bmOptions);
                    holder.imgview.setImageBitmap(decodedByte);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        // Add viewpager_item.xml to ViewPager
        (container).addView(itemView);

        return itemView;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // Remove viewpager_item.xml from ViewPager
        container.removeView((LinearLayout) object);
    }

    class ViewHolder {
        TextView txtcnt;
        ImageView imgview;
    }
}
