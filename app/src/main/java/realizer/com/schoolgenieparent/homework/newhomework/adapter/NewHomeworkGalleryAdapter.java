package realizer.com.schoolgenieparent.homework.newhomework.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;

import realizer.com.schoolgenieparent.R;
import realizer.com.schoolgenieparent.Utils.Singleton;
import realizer.com.schoolgenieparent.homework.model.TeacherHomeworkModel;
import realizer.com.schoolgenieparent.homework.newhomework.CustomPhotoGalleryActivity;
import realizer.com.schoolgenieparent.homework.newhomework.customgallery.multiselectgallery.PhotoAlbumActivity;

/**
 * Created by Win on 08/04/2016.
 */
public class NewHomeworkGalleryAdapter extends BaseAdapter
{
    private ArrayList<TeacherHomeworkModel> elementDetails;
    private ArrayList<String> imageList;
    private LayoutInflater mInflater;
    Context context;
    Bitmap decodedByte;
    String data;
    TextView datetext;
    ImageView status;
    public NewHomeworkGalleryAdapter(Context context, ArrayList<TeacherHomeworkModel> results,ArrayList<String> imageList)
    {
        elementDetails = results;
        mInflater = LayoutInflater.from(context);
        this.context=context;
        this.imageList = imageList;
        Singleton.setFialbitmaplist(elementDetails);
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

    public View getView(final int position, View convertView, final ViewGroup parent)
    {
        convertView = mInflater.inflate(R.layout.row_multi_photo_item_hw, null);
        ImageView imageview = (ImageView) convertView.findViewById(R.id.imgThumb);
        ImageButton imgdelete = (ImageButton)convertView.findViewById(R.id.chkImage);
        imgdelete.setTag(position);
        if(elementDetails.get(position).getPic() != null)
            imageview.setImageBitmap(elementDetails.get(position).getPic());

        if(elementDetails.get(position).getHwTxtLst().equalsIgnoreCase("NoIcon")) {
            imgdelete.setVisibility(View.GONE);
            imageview.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Singleton.setImageList(imageList);
                    Intent intent = new Intent(context, PhotoAlbumActivity.class);
                    context.startActivity(intent);
                }
            });
        }

        imgdelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int pos = (Integer)v.getTag();

                elementDetails.remove(pos);
                Singleton.setFialbitmaplist(elementDetails);
                imageList.remove(pos);
                notifyDataSetChanged();

            }
        });




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
