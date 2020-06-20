package com.example.plnatsub;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PlantDetail extends AppCompatActivity {

    private final String BASE_URL = "http://6b95766df9f6.ngrok.io";  //url주소

    private MyAPI mMyAPI;
    private final  String TAG = getClass().getSimpleName();
    Button addGallery,goGallery;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_plant);

        TextView name = (TextView)findViewById(R.id.name);
        TextView flower_flower = (TextView)findViewById(R.id.flower_flower);
        ImageView flower_img = (ImageView)findViewById(R.id.flower_img);
        TextView flower_content = (TextView)findViewById(R.id.flower_content);
        addGallery = findViewById(R.id.addGallery);
        goGallery = findViewById(R.id.goGallery);


        Intent intent = getIntent();

        final String plant_name = intent.getExtras().getString("name_txt");
        final String plant_flower = intent.getExtras().getString("flower_txt");
        final String img_txt = intent.getExtras().getString("img_txt");
        final String plant_content = intent.getExtras().getString("content_txt");

        final String detail_my_plant_images = intent.getExtras().getString("search_my_plant_images");


        name.setText(plant_name);
        Picasso.get().load(img_txt).into(flower_img);
        flower_flower.setText(plant_flower);
        flower_content.setText(plant_content);
        initMyAPI(BASE_URL);

        final String android_id = android.provider.Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        addGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("아나", android_id);

                Toast.makeText(getApplicationContext(),"도감에 추가 완료", Toast.LENGTH_SHORT).show();

                Gson gson = new GsonBuilder().setLenient().create();

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create(gson))
                        .build();
                mMyAPI = retrofit.create(MyAPI.class);


                Call<AccountItem> call = mMyAPI.book_posts(""+android_id,""+plant_name,""+plant_flower+"",""+plant_content,""+detail_my_plant_images);
                call.enqueue(new Callback<AccountItem>() {
                    @Override
                    public void onResponse(Call<AccountItem> call, Response<AccountItem> response) {
                        Log.i("도감 good", "good");
//                        Booklist();

                    }

                    @Override
                    public void onFailure(Call<AccountItem> call, Throwable t) {
                        Log.i(TAG,"도감 Fail msg : " + t.getMessage());
//                        Booklist();

                    }
                });
            }
        });

        goGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), PlantBook.class);
                startActivity(intent);
            }
        });
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
