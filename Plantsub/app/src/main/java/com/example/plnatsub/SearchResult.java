package com.example.plnatsub;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchResult extends AppCompatActivity {

    private TextView result1, result2, result1_percent, result2_percent;
    private MyAPI mMyAPI;

    private final  String TAG = getClass().getSimpleName();

    private final String BASE_URL = "http://5d8cf485a554.ngrok.io"; //url주소


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        LinearLayout layout_result1 = (LinearLayout) findViewById(R.id.layout_result1);
        LinearLayout layout_result2 = (LinearLayout) findViewById(R.id.layout_result2);
        LinearLayout layout_result3 = (LinearLayout) findViewById(R.id.layout_result3);

        Intent intent = getIntent(); //인텐트를 가져옴

        final String search_my_plant_images = intent.getExtras().getString("my_plant_images");

        String plant_first_name = intent.getExtras().getString("frist_txt");
        String plant_first_percent = intent.getExtras().getString("first_percent_txt");

        TextView tv_first_name = new TextView(getApplicationContext());
        tv_first_name.setText(plant_first_name);
        layout_result1.addView(tv_first_name);

        TextView tv_first_percent = new TextView(getApplicationContext());
        tv_first_percent.setText("일치율: "+plant_first_percent+"%");
        layout_result1.addView(tv_first_percent);

        String first_img_txt = intent.getExtras().getString("first_img_txt");

        ImageView flower_img1 = new ImageView(getApplicationContext());
        Picasso.get().load(first_img_txt).into(flower_img1);
        layout_result1.addView(flower_img1);

        final String one = plant_first_name; // 첫번째 버튼에 해당
        Button first_detail_btn = new Button(getApplicationContext());
        first_detail_btn.setText("자세히 보기"); //첫번째 버튼
        layout_result1.addView(first_detail_btn);

        first_detail_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(getApplicationContext(), PlantDetail.class);

                Call<List<AccountItem>> plantconCall = mMyAPI.get_plant_con(one);
                plantconCall.enqueue(new Callback<List<AccountItem>>() {
                    @Override
                    public void onResponse(Call<List<AccountItem>> call, Response<List<AccountItem>> response) {
                        if(response.isSuccessful()){
                            List<AccountItem> versionList =response.body();
                            Log.d(TAG,response.body().toString());
                            String name_txt = "";
                            String flower_txt = "";
                            String content_txt = "";
                            String img_txt = "";

                            for(AccountItem accountItem:versionList){
                                Log.d(TAG,"ㅅ"+one);
                                Log.d(TAG,"ㅎ"+accountItem.getName());

                                name_txt +=""+ accountItem.getName();
                                flower_txt +=" 꽃말: "+accountItem.getFlower();
                                content_txt +=" 꽃 내용: "+accountItem.getContent();
                                img_txt =""+BASE_URL+accountItem.getImage();  //url주소

                            }


                            intent.putExtra("name_txt",name_txt);
                            intent.putExtra("flower_txt",flower_txt);
                            intent.putExtra("content_txt",content_txt);
                            intent.putExtra("img_txt",img_txt);

                            intent.putExtra("search_my_plant_images",search_my_plant_images);
                                startActivity(intent);

                        }else{
                            int StatusCode =response.code();
                            Log.d(TAG,"dd아"+StatusCode);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<AccountItem>> call, Throwable t) {
                        Log.d(TAG,"실패"+t.getMessage());
                    }
                });

            }
        });

        String plant_second_name = intent.getExtras().getString("second_txt");
        String plant_second_percent = intent.getExtras().getString("second_percent_txt");

        String plant_third_name = intent.getExtras().getString("third_txt");
        String plant_third_percent = intent.getExtras().getString("third_percent_txt");






        if (Float.valueOf(plant_second_percent)>0.0 && Float.valueOf(plant_third_percent)>0.0 ) {



            TextView tv_second_name = new TextView(getApplicationContext());
            tv_second_name.setText(plant_second_name);
            layout_result2.addView(tv_second_name);

            TextView tv_second_percent = new TextView(getApplicationContext());
            tv_second_percent.setText("일치율: "+plant_second_percent+"%");
            layout_result2.addView(tv_second_percent);


            TextView tv_third_name = new TextView(getApplicationContext());
            tv_third_name.setText(plant_third_name);
            layout_result3.addView(tv_third_name);

            TextView tv_third_percent = new TextView(getApplicationContext());
            tv_third_percent.setText("일치율: "+plant_third_percent+"%");
            layout_result3.addView(tv_third_percent);



            String second_img_txt = intent.getExtras().getString("second_img_txt");
            String third_img_txt = intent.getExtras().getString("third_img_txt");


            ImageView flower_img2 = new ImageView(getApplicationContext());
            Picasso.get().load(second_img_txt).into(flower_img2);
            layout_result2.addView(flower_img2);

            ImageView flower_img3 = new ImageView(getApplicationContext());
            Picasso.get().load(third_img_txt).into(flower_img3);
            layout_result3.addView(flower_img3);

            final String two = plant_second_name; // 두번째 버튼에 해당
            final String three = plant_third_name;



            Button second_detail_btn = new Button(getApplicationContext());
            second_detail_btn.setText("자세히 보기");
            layout_result2.addView(second_detail_btn);

            Button third_detail_btn = new Button(getApplicationContext());
            third_detail_btn.setText("자세히 보기");
            layout_result3.addView(third_detail_btn);




        second_detail_btn.setOnClickListener(new View.OnClickListener() {  //버튼 클릭시 2등인 정확도 정보나옴
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(getApplicationContext(), PlantDetail.class);

                Call<List<AccountItem>> plantconCall1 = mMyAPI.get_plant_con(two);
                plantconCall1.enqueue(new Callback<List<AccountItem>>() {
                    @Override
                    public void onResponse(Call<List<AccountItem>> call, Response<List<AccountItem>> response) {
                        if(response.isSuccessful()){
                            List<AccountItem> versionList =response.body();
                            Log.d(TAG,response.body().toString());
                            String name_txt = "";
                            String flower_txt = "";
                            String img_txt = "";
                            String content_txt = "";
                            for(AccountItem accountItem:versionList){
                                Log.d(TAG,"ㅅ"+two);
                                Log.d(TAG,"ㅎ"+accountItem.getName());
//
                                name_txt +=""+ accountItem.getName();
                                flower_txt +=" 꽃말: "+accountItem.getFlower();
                                img_txt =""+BASE_URL+accountItem.getImage();   //url주소
                                content_txt +=" 꽃 내용: "+accountItem.getContent();
//
                            }
                            intent.putExtra("name_txt",name_txt);
                            intent.putExtra("flower_txt",flower_txt);
                            intent.putExtra("img_txt",img_txt);
                            intent.putExtra("content_txt",content_txt);

                            intent.putExtra("search_my_plant_images",search_my_plant_images);
                            startActivity(intent);
//                                intent.putExtra("detail_txt",detail2_txt);

                        }else{
                            int StatusCode =response.code();
                            Log.d(TAG,"dd아"+StatusCode);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<AccountItem>> call, Throwable t) {

                    }
                });

//                            startActivity(intent);
            }
        });
        third_detail_btn.setOnClickListener(new View.OnClickListener() {  //버튼 클릭시 2등인 정확도 정보나옴
            @Override
            public void onClick(View view) {
                final Intent intent = new Intent(getApplicationContext(), PlantDetail.class);

                Call<List<AccountItem>> plantconCall2 = mMyAPI.get_plant_con(three);
                plantconCall2.enqueue(new Callback<List<AccountItem>>() {
                    @Override
                    public void onResponse(Call<List<AccountItem>> call, Response<List<AccountItem>> response) {
                        if(response.isSuccessful()){
                            List<AccountItem> versionList =response.body();
                            Log.d(TAG,response.body().toString());
                            String name_txt = "";
                            String flower_txt = "";
                            String img_txt = "";
                            String content_txt = "";
                            for(AccountItem accountItem:versionList){
                                Log.d(TAG,"ㅅ"+two);
                                Log.d(TAG,"ㅎ"+accountItem.getName());
//
                                name_txt +=""+ accountItem.getName();
                                flower_txt +=" 꽃말: "+accountItem.getFlower();
                                img_txt =""+BASE_URL+accountItem.getImage();   //url주소
                                content_txt +=" 꽃 내용: "+accountItem.getContent();
//
                            }
                            intent.putExtra("name_txt",name_txt);
                            intent.putExtra("flower_txt",flower_txt);
                            intent.putExtra("img_txt",img_txt);
                            intent.putExtra("content_txt",content_txt);

                            intent.putExtra("search_my_plant_images",search_my_plant_images);
                            startActivity(intent);
//                                intent.putExtra("detail_txt",detail2_txt);

                        }else{
                            int StatusCode =response.code();
                            Log.d(TAG,"dd아"+StatusCode);
                        }
                    }

                    @Override
                    public void onFailure(Call<List<AccountItem>> call, Throwable t) {

                    }
                });

//                            startActivity(intent);
            }
        });


        initMyAPI(BASE_URL);

        } //if문
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
