package com.alinc.gridimagesearch.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.alinc.gridimagesearch.R;
import com.alinc.gridimagesearch.adapters.ImageResultsAdapter;
import com.alinc.gridimagesearch.listeners.EndlessScrollListener;
import com.alinc.gridimagesearch.models.ImageResult;
import com.etsy.android.grid.StaggeredGridView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

public class ImageSearchActivity extends AppCompatActivity {
    private EditText etQuery;
    private StaggeredGridView gvResults;
    private ArrayList<ImageResult> imageResults;
    private ImageResultsAdapter imageResultsAdapter;
    private String imageSize = "";
    private String imageType = "";
    private String colorFilter = "";
    private String siteFilter = "";
    private int startPage = 0;
    private EndlessScrollListener endlessScrollListener;
    private String TAG = ImageSearchActivity.class.getName();
    private String query = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            actionBar.setLogo(R.mipmap.ic_lens);
            getSupportActionBar().setDisplayUseLogoEnabled(true);
        }

        if(isNetworkAvailable()) {
            setContentView(R.layout.activity_image_search);
            setupViews();

            final EditText etSearchKey = (EditText) findViewById(R.id.etQuery);
            etSearchKey.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    final int DRAWABLE_RIGHT = 2;
                    if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                        if (motionEvent.getRawX() >= (etSearchKey.getWidth() - etSearchKey.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                            etSearchKey.setText("");
                            imageResultsAdapter.clear();
                            imageResultsAdapter.notifyDataSetChanged();
                            //return true;
                        }
                    }
                    return false;
                }
            });

            etSearchKey.setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                        startPage = 0;
                        endlessScrollListener.reset(0, true);
                        onImageSearch();
                        return true;
                    }
                    return false;
                }
            });
            imageResults = new ArrayList<ImageResult>();
            imageResultsAdapter = new ImageResultsAdapter(this, imageResults);
            gvResults.setAdapter(imageResultsAdapter);
            gvResults.setOnScrollListener(endlessScrollListener = new EndlessScrollListener() {
                @Override
                public void onLoadMore(int page, int totalItemsCount) {
                    Log.i("PAGE:: ", String.valueOf(page) + " items: " + String.valueOf(totalItemsCount));
                    loadNextPage();
                }
            });
        }
        else
        {

            showErrorDialog(getString(R.string.network_error), getString(R.string.wifi_failure));
        }
    }

    private void setupViews() {
        etQuery = (EditText) findViewById(R.id.etQuery);
        gvResults = (StaggeredGridView) findViewById(R.id.sgvResults);

        gvResults.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Launch image display
                //Create intent
                Intent i = new Intent(ImageSearchActivity.this, ImageDisplayActivity.class);
                //Get the image result to display
                ImageResult imageResult = imageResults.get(position);
                i.putExtra("image", imageResult);
                //launch the new activity
                startActivity(i);
            }
        });
    }

    //Fire Search action
    public void onImageSearch() {
        query = etQuery.getText().toString();
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(5000);
        try {
            query = URLEncoder.encode(query, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String url = "https://ajax.googleapis.com/ajax/services/search/images?v=1.0&q=" + query + "&start=" + startPage + "&rsz=8" + "&imgsz=" + imageSize + "&imgcolor=" + colorFilter + "&imgtype=" + imageType + "&as_sitesearch=" + siteFilter;
        Log.i("DEBUG URL:: ", url);
        client.get(url, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, JSONObject response) {
                //Log.d("DEBUG: ", response.toString());

                try {
                    JSONArray imageResultsJson = response.getJSONObject("responseData").getJSONArray("results");
                    if (startPage == 0) {
                        imageResults.clear(); //clear the existing images from array, when a new search
                    }
                    imageResultsAdapter.addAll(ImageResult.fromJSONArray(imageResultsJson));
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, Throwable throwable, JSONObject errorResponse) {
                super.onFailure(statusCode, headers, throwable, errorResponse);
                //Log.d("DEBUG: ", errorResponse.toString());
                if (throwable.getMessage().contains("UnknownHostException")) {
                    showErrorDialog(getString(R.string.network_error), getString(R.string.server_unavailable));
                }

            }
        });
    }

   @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_image_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.filter_settings) {
            showFilterDialog();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showFilterDialog() {
        LayoutInflater inflater = LayoutInflater.from(ImageSearchActivity.this);
        final View filterView = inflater.inflate(R.layout.filter_fragment, null);

        final Spinner spImageSize = (Spinner) filterView.findViewById(R.id.spImageSize);
        final ArrayAdapter<CharSequence> spImageSizeAdapter = ArrayAdapter.createFromResource(this, R.array.image_size_array, R.layout.spinner_item);
        spImageSizeAdapter.setDropDownViewResource(R.layout.spinner_item);
        spImageSize.setAdapter(spImageSizeAdapter);

        final Spinner spColorFilter = (Spinner) filterView.findViewById(R.id.spColorFilter);
        final ArrayAdapter<CharSequence> spColorFilterAdapter = ArrayAdapter.createFromResource(this, R.array.color_filter_array, R.layout.spinner_item);
        spColorFilterAdapter.setDropDownViewResource(R.layout.spinner_item);
        spColorFilter.setAdapter(spColorFilterAdapter);

        final Spinner spImageType = (Spinner) filterView.findViewById(R.id.spImageType);
        final ArrayAdapter<CharSequence> spImageTypeAdapter = ArrayAdapter.createFromResource(this, R.array.image_type_array, R.layout.spinner_item);
        spImageTypeAdapter.setDropDownViewResource(R.layout.spinner_item);
        spImageType.setAdapter(spImageTypeAdapter);

        final TextView tvSiteFilter = (TextView) filterView.findViewById(R.id.etSiteFilter);


        AlertDialog dialog = new AlertDialog.Builder(ImageSearchActivity.this, R.style.FilterDialog)
                .setTitle("Search Filters")
                .setView(filterView)
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        imageSize = spImageSize.getSelectedItem().toString();
                        imageType = spImageType.getSelectedItem().toString();
                        colorFilter = spColorFilter.getSelectedItem().toString();
                        siteFilter = tvSiteFilter.getText().toString();
                        if (!query.isEmpty()) {
                            onImageSearch();
                            imageResultsAdapter.notifyDataSetChanged();
                        }
                    }
                })
                .setNegativeButton("Cancel", null).create();
        setSpinnerToValue(spImageSize, imageSize);
        setSpinnerToValue(spImageType, imageType);
        setSpinnerToValue(spColorFilter, colorFilter);
        tvSiteFilter.setText(siteFilter);
        dialog.show();
    }

    public void setSpinnerToValue(Spinner spinner, String value) {
        int index = 0;
        SpinnerAdapter adapter = spinner.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (adapter.getItem(i).equals(value)) {
                index = i;
                break; // terminate loop
            }
        }
        spinner.setSelection(index);
    }

    public void loadNextPage() {
        if(startPage < 56) {
            startPage = startPage + 8;
            onImageSearch();
        }
        else {
            Toast.makeText(this, "Maximum load page reached!", Toast.LENGTH_SHORT).show();
        }
    }

    private Boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        //return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
        return activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI;
    }

    public void showErrorDialog(String title, String errorMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(errorMessage);
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}
