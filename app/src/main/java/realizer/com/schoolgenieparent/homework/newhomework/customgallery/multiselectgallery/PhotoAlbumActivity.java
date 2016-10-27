package realizer.com.schoolgenieparent.homework.newhomework.customgallery.multiselectgallery;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;

import realizer.com.schoolgenieparent.R;
import realizer.com.schoolgenieparent.homework.newhomework.NewHomeworkActivity;
import realizer.com.schoolgenieparent.homework.newhomework.customgallery.adapters.PhotoAlbumAdapter;
import realizer.com.schoolgenieparent.homework.newhomework.customgallery.utils.MediaStoreHelperMethods;
import realizer.com.schoolgenieparent.homework.newhomework.customgallery.utils.MediaStorePhoto;


public class PhotoAlbumActivity extends AppCompatActivity {

    private ArrayList<MediaStorePhoto> selectedPhotoList = new ArrayList<>();

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private LinearLayoutManager mLayoutManager;

    private ArrayList<MediaStorePhoto> bucketItemList = new ArrayList<>();
    private ArrayList<Integer> bucketTotalImageCount = new ArrayList<>();
    private ArrayList<Integer> bucketSelectedImageCount = new ArrayList<>();

    private FloatingActionButton fab;

    private ActionBar ab;

    private int photoType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_album);

        photoType = 1;

        ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setHomeButtonEnabled(true);
        }

        fab = (FloatingActionButton) findViewById(R.id.fab);
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new PhotoAlbumAdapter(bucketItemList, selectedPhotoList, this);
        mLayoutManager = new GridLayoutManager(this, 2);

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(mLayoutManager);

        fab.setImageResource(R.drawable.ic_next);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedPhotoList.size() != 0)
                    try {
                        Class resultClass = Class.forName(getIntent().getStringExtra("following_class"));
                        Intent mIntent = new Intent(PhotoAlbumActivity.this, NewHomeworkActivity.class);
                        mIntent.putParcelableArrayListExtra("selected_photo_list", selectedPhotoList);
                        mIntent.putExtra("photo_type", photoType);
                        startActivityForResult(mIntent, 3000);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                else new AlertDialog.Builder(PhotoAlbumActivity.this)
                        .setTitle("Nothing selected")
                        .setMessage("Your have not selected any pictures. Please select at least one picture to continue!")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        })
                        .setIcon(R.drawable.ic_cancel_dark)
                        .show();
            }
        });

        getPhotoAlbumData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == android.R.id.home) {
            if (selectedPhotoList.size() != 0) new AlertDialog.Builder(PhotoAlbumActivity.this)
                    .setTitle("Cancel selection?")
                    .setMessage("Your selection will be lost. Do you want to continue?")
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(R.drawable.ic_cancel_dark)
                    .show();
            else finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void getPhotoAlbumData() {
        bucketItemList.clear();
        bucketTotalImageCount.clear();
        bucketSelectedImageCount.clear();

        bucketTotalImageCount = getBucketTotalImageCount();
        bucketSelectedImageCount = getBucketSelectedImageCount();

        ArrayList<MediaStorePhoto> mList = new ArrayList<>(MediaStoreHelperMethods.getBucketCoverItems(getContentResolver()));

        for (int i = 0; i < mList.size(); i++) {
            MediaStorePhoto photo = mList.get(i);
            photo.setBucket("(" + bucketSelectedImageCount.get(i) + "/" + bucketTotalImageCount.get(i) + ") " + photo.getBucket());
            bucketItemList.add(photo);
        }

        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000 && resultCode == RESULT_OK) {
            selectedPhotoList.clear();
            ArrayList<MediaStorePhoto> photoList = data.getParcelableArrayListExtra("selected_photo_list");
            for (MediaStorePhoto mPhoto : photoList) {
                selectedPhotoList.add(mPhoto);
            }
            ab.setTitle(String.valueOf(selectedPhotoList.size()) + " Photos Selected");
            getPhotoAlbumData();
        }
        if (requestCode == 3000 && resultCode == RESULT_OK) {
            Intent intent = new Intent();
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    public ArrayList<Integer> getBucketTotalImageCount() {
        ArrayList<Integer> countList = new ArrayList<>();
        for (MediaStorePhoto photo : MediaStoreHelperMethods.getBucketCoverItems(getContentResolver())) {
            countList.add(MediaStoreHelperMethods.getAllPhotosInBucket(photo.getBucketId(), getContentResolver()).size());
        }
        return countList;
    }

    public ArrayList<Integer> getBucketSelectedImageCount() {
        ArrayList<Integer> countList = new ArrayList<>();
        for (MediaStorePhoto photo : MediaStoreHelperMethods.getBucketCoverItems(getContentResolver())) {
            int count = 0;
            for (MediaStorePhoto mPhoto : selectedPhotoList) {
                if (photo.getBucketId().equals(mPhoto.getBucketId()))
                    count++;
            }
            countList.add(count);
        }
        return countList;
    }
}
