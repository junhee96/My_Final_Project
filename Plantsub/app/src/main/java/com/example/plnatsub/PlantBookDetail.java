package com.example.plnatsub;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PlantBookDetail extends AppCompatActivity {
    private final String BASE_URL = "http://6b95766df9f6.ngrok.io";


    private MyAPI mMyAPI;
    private final  String TAG = getClass().getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plant_bookdetail);

        TextView my_name = (TextView)findViewById(R.id.plantname_detail);
        TextView my_flower = (TextView)findViewById(R.id.plantflower_detail);
        ImageView my_img = (ImageView)findViewById(R.id.plantimg_detail);
        TextView my_content = (TextView)findViewById(R.id.plantcontent_detail);


        Intent intent = getIntent();

        String plantname_detail = intent.getExtras().getString("first_name_datail_txt");
        String plantflower_detail = intent.getExtras().getString("first_flower_detail_txt");
        String plantcontent_detail = intent.getExtras().getString("first_content_detail_txt");
        String plantimg_detail = intent.getExtras().getString("first_img_detail_txt");
        Log.d(TAG,"뭐냐니깐"+plantflower_detail);

        my_name.setText(plantname_detail);
        my_flower.setText(plantflower_detail);
        my_content.setText(plantcontent_detail);

        Picasso.get().load(plantimg_detail).into(my_img);
        initMyAPI(BASE_URL);
    }

    public void onBackPressed() {  //뒤로가기 버튼 이벤트
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        super.onBackPressed();
    }

    public void initMyAPI(String baseUrl){

        Log.d(TAG,"initMyAPI : " + baseUrl);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();


        mMyAPI = retrofit.create(MyAPI.class);
    }
}
