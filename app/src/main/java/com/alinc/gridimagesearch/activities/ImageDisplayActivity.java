package com.alinc.gridimagesearch.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;

import com.alinc.gridimagesearch.R;
import com.alinc.gridimagesearch.models.ImageResult;
import com.alinc.gridimagesearch.models.TouchImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * Created by alinc on 9/24/15.
 */
public class ImageDisplayActivity  extends AppCompatActivity {
    private ShareActionProvider miShareAction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_display);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            actionBar.setLogo(R.mipmap.ic_lens);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
        }

        //DynamicHeightImageView ivImageResults = (DynamicHeightImageView) findViewById(R.id.imageView);
        TouchImageView ivImageResults = (TouchImageView) findViewById(R.id.imageView);
        ImageResult image = getIntent().getParcelableExtra("image");
        ivImageResults.setMaxZoom(4f);

        DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
        float intendedWidth = displayMetrics.widthPixels / displayMetrics.density;


        // Gets the downloaded image dimensions
        int originalWidth = image.width;
        int originalHeight = image.height;

        // Calculates the new dimensions
        float scale = (float) intendedWidth / originalWidth;
        int newHeight = (int) Math.round(originalHeight * scale);


        Picasso.with(getApplicationContext()).setIndicatorsEnabled(false);
        Picasso.with(this).load(image.fullUrl).centerCrop().resize((int) intendedWidth, newHeight).placeholder(R.drawable.loading).into(ivImageResults, new Callback() {
            @Override
            public void onSuccess() {
                // Setup share intent now that image has loaded
                setupShareIntent();
            }

            @Override
            public void onError() {

            }
        });

        //Load the image using Picasso
        //Picasso.with(this).load(image.fullUrl).centerCrop().resize((int) intendedWidth, newHeight).placeholder(R.drawable.loading).into(ivImageResults);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate menu resource file.
        getMenuInflater().inflate(R.menu.menu_image_search, menu);
        // Locate MenuItem with ShareActionProvider
        MenuItem item = menu.findItem(R.id.menu_item_share);
        // Fetch reference to the share action provider
        miShareAction = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        // Return true to display menu
        return true;
    }

    public void setupShareIntent() {
        TouchImageView ivImage = (TouchImageView) findViewById(R.id.imageView);
        Uri bmpUri = getLocalBitmapUri(ivImage);

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, bmpUri);
        shareIntent.setType("image/*");

        miShareAction.setShareIntent(shareIntent);
    }

    public Uri getLocalBitmapUri(TouchImageView imageView) {

        TouchImageView siv = imageView;
        Drawable mDrawable = siv.getDrawable();
        Bitmap mBitmap = ((BitmapDrawable)mDrawable).getBitmap();
        String path = MediaStore.Images.Media.insertImage(getContentResolver(),
                mBitmap, "Image Description", null);

        Uri uri = Uri.parse(path);
        return uri;

    }

}
