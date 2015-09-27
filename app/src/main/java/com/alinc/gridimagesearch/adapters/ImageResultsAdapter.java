package com.alinc.gridimagesearch.adapters;

import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.alinc.gridimagesearch.R;
import com.alinc.gridimagesearch.models.ImageResult;
import com.etsy.android.grid.util.DynamicHeightImageView;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Random;

/**
 * Created by alinc on 9/24/15.
 */
public class ImageResultsAdapter extends ArrayAdapter<ImageResult> {
    private static final SparseArray<Double> sPositionHeightRatios = new SparseArray<Double>();
    private final Random mRandom;


    public ImageResultsAdapter(Context context, List<ImageResult> images) {
        super(context, android.R.layout.simple_list_item_1, images);
        this.mRandom = new Random();
    }

    static class ViewHolder {
        DynamicHeightImageView imgView;
        TextView tvTitle;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder vh;

        ImageResult imageInfo = getItem(position);
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_image_result, parent, false);
            vh = new ViewHolder();
            vh.imgView = (DynamicHeightImageView) convertView.findViewById(R.id.ivImage);
            vh.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
            convertView.setTag(vh);
        } else {
            vh = (ViewHolder) convertView.getTag();
        }

        double positionHeight = getPositionRatio(position);
        float heightRatio=(float)imageInfo.height / (float)imageInfo.width;
        final int viewWidth = imageInfo.width;
        final int viewHeight=(int)(viewWidth * positionHeight);
        vh.imgView.setHeightRatio(heightRatio);

        Picasso.with(getContext()).load(imageInfo.thumbUrl).placeholder(R.drawable.loading).into(vh.imgView);

        //ImageView ivImage = (ImageView) convertView.findViewById(R.id.ivImage);
        //TextView tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);

        vh.tvTitle.setText(Html.fromHtml(imageInfo.title));
        //Picasso.with(getContext()).load(imageInfo.thumbUrl).into(ivImage);

        return convertView;
    }

    private double getPositionRatio(final int position) {
        double ratio = sPositionHeightRatios.get(position, 0.0);
        // if not yet done generate and stash the columns height
        // in our real world scenario this will be determined by
        // some match based on the known height and width of the image
        // and maybe a helpful way to get the column height!
        if (ratio == 0) {
            ratio = getRandomHeightRatio();
            sPositionHeightRatios.append(position, ratio);
            Log.d("RATIO:: ", "getPositionRatio:" + position + " ratio:" + ratio);
        }
        return ratio;
    }

    private double getRandomHeightRatio() {
        return (mRandom.nextDouble() / 2.0) + 1.0; // height will be 1.0 - 1.5 of the width
    }
}
