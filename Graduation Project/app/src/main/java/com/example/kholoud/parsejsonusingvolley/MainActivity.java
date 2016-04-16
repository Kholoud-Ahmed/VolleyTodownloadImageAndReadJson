package com.example.kholoud.parsejsonusingvolley;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends Activity implements View.OnClickListener{

    TextView textView;
    Button button;
    RequestQueue requestQueue;
    NetworkImageView networkImageView;
    ImageLoader imageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initComponents();
        addListeners();
    }

    private void addListeners() {
        button.setOnClickListener(this);
        requestQueue = Volley.newRequestQueue(this);
    }

    private void initComponents() {
        textView = (TextView) findViewById(R.id.tvMainActivity);
        button = (Button) findViewById(R.id.bMainActivity);
        networkImageView = (NetworkImageView) findViewById(R.id.networkImageViewMainActivity);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bMainActivity:
                //the request will submitted by default with POST
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest("http://cblunt.github.io/blog-android-volley/response.json",null,
                        new Response.Listener<JSONObject>(){

                    @Override
                    public void onResponse(JSONObject response) {
                        textView.setText(response.toString());
                        try {
                            String message = response.getString("message");
                            String code = response.getString("code");
                            String images = response.getString("images");

                            imageLoader = new ImageLoader(requestQueue, new ImageLoader.ImageCache() {
                                private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(10);
                                public void putBitmap(String url, Bitmap bitmap) {
                                    mCache.put(url, bitmap);
                                }
                                public Bitmap getBitmap(String url) {
                                    return mCache.get(url);
                                }
                            });

                            networkImageView.setImageUrl("http://assets.chrisblunt.com/wp-content/uploads/2012/12/IMG_20120619_202506-e1356946615784.jpg",imageLoader);
                            networkImageView.setDefaultImageResId(R.drawable.plus);
                           // networkImageView.setImageUrl(response.getString("images"), new ImageLoader(requestQueue, null));
//                            JSONArray jsonArray = response.getJSONArray("images");
//                            for(int i = 0 ; i< jsonArray.length() ; i++){
//                                JSONObject jsonObject = jsonArray.getJSONObject(i);
//                                String url = jsonObject.getString("myString");
//                            }


                            textView.setText(message+"\n"+code+"\n"+images);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new MyErrorListener()
                );
                requestQueue.add(jsonObjectRequest);
                break;
        }
    }

    class MyErrorListener implements Response.ErrorListener{
        @Override
        public void onErrorResponse(VolleyError error) {
            Log.i("++++++", "Error: " + error.getMessage());
        }
    }
}
