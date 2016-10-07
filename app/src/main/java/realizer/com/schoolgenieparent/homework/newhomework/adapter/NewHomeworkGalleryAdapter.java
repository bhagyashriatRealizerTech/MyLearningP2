package realizer.com.schoolgenieparent.homework.newhomework.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;

import realizer.com.schoolgenieparent.R;
import realizer.com.schoolgenieparent.homework.model.TeacherHomeworkModel;

/**
 * Created by Win on 08/04/2016.
 */
public class NewHomeworkGalleryAdapter extends BaseAdapter
{
    private ArrayList<TeacherHomeworkModel> elementDetails;
    private LayoutInflater mInflater;
    Context context;
    Bitmap decodedByte;
    String data;
    TextView datetext;
    ImageView status;
    public NewHomeworkGalleryAdapter(Context context, ArrayList<TeacherHomeworkModel> results)
    {
        elementDetails = results;
        mInflater = LayoutInflater.from(context);
        this.context=context;
    }

    @Override
    public int getCount() {
        return elementDetails.size();
    }

    public Object getItem(int position)
    {
        return elementDetails.get(position);
    }

    public long getItemId(int position)
    {
        return position;
    }

    public View getView(final int position, View convertView, ViewGroup parent)
    {
        convertView = mInflater.inflate(R.layout.row_multi_photo_item_hw, null);
        ImageView imageview = (ImageView) convertView.findViewById(R.id.imgThumb);
        if(elementDetails.get(position).getPic() != null)
            imageview.setImageBitmap(elementDetails.get(position).getPic());
        /*datetext = (TextView) convertView.findViewById(R.id.event_date);
        status = (ImageView) convertView.findViewById(R.id.uploaded);

        imageview.setImageBitmap(elementDetails.get(position).getBitmap());
        datetext.setText(Config.getMediumDateForImage(elementDetails.get(position).getDate()));
        if(elementDetails.get(position).getStatus().equalsIgnoreCase("true"))
            status.setImageResource(R.drawable.homework_send);
        else
            status.setImageResource(R.drawable.homework_pending);*/

        return convertView;
    }


}
