package com.alinc.gridimagesearch.activities;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.alinc.gridimagesearch.R;
import com.alinc.gridimagesearch.models.ImageResult;
import com.etsy.android.grid.util.DynamicHeightImageView;
import com.squareup.picasso.Picasso;

/**
 * Created by alinc on 9/24/15.
 */
public class ImageDisplayActivity  extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_display);

        DynamicHeightImageView ivImageResults = (DynamicHeightImageView) findViewById(R.id.imageView);
        ImageResult image = getIntent().getParcelableExtra("image");

        DisplayMetrics displayMetrics = getApplicationContext().getResources().getDisplayMetrics();
        float intendedWidth = displayMetrics.widthPixels / displayMetrics.density;


        // Gets the downloaded image dimensions
        int originalWidth = image.width;
        int originalHeight = image.height;

        // Calculates the new dimensions
        float scale = (float) intendedWidth / originalWidth;
        int newHeight = (int) Math.round(originalHeight * scale);

        Picasso.with(this).load(image.fullUrl).centerCrop().resize((int) intendedWidth, newHeight).placeholder(R.drawable.loading).into(ivImageResults);
        //Load the image using Picasso
        //Picasso.with(this).load(image.fullUrl).centerCrop().resize((int) intendedWidth, newHeight).placeholder(R.drawable.loading).into(ivImageResults);


    }
}
