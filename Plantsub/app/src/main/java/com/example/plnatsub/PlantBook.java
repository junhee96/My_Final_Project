package com.example.plnatsub;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterInside;
import com.squareup.picasso.Picasso;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.example.plnatsub.R.drawable.btn_carmer;

public class PlantBook  extends AppCompatActivity {

    private final String BASE_URL = "http://6b95766df9f6.ngrok.io"; //주소


    private MyAPI mMyAPI;
    private final  String TAG = getClass().getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plant_book);

//        final TextView book_txt = (TextView) findViewById(R.id.book_txt);
//        final ImageView book_image = (ImageView) findViewById(R.id.book_image);




        initMyAPI(BASE_URL);
        final String android_id = android.provider.Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        Call<List<AccountItem>> versionCall = mMyAPI.get_book_list(""+android_id);
        versionCall.enqueue(new Callback<List<AccountItem>>() {
            @Override
            public void onResponse(Call<List<AccountItem>> call, Response<List<AccountItem>> response) {
                if(response.isSuccessful()){
                    List<AccountItem> versionList =response.body();
                    Log.d(TAG,response.body().toString());
                    String book_list_text = "";
                    String book_list_image = "";

                    String book_text_one = "";
                    String book_image_one = "";

                    String book_number_one = "";
                    String book_list_number = "";
                    for(AccountItem accountItem:versionList){
                        LinearLayout layout = (LinearLayout) findViewById(R.id.layout);
//                        book_list_text +=""+accountItem.getName()+""+accountItem.getFlower()+""+accountItem.getContent()+"\n\n";
                        GridLayout gridLayout = (GridLayout) findViewById(R.id.gridlayout);
                        book_text_one = ""+accountItem.getName();
                        TextView tv_one = new TextView(getApplicationContext());

                        Typeface typeface_tv = Typeface.createFromAsset(getAssets(), "maplestory_bold.ttf");  //제목 텍스트 폰트
                        tv_one.setTypeface(typeface_tv);
                        tv_one.setText(book_text_one);// 첫번째 꽃저장 이름
                        tv_one.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 45); // 텍스트 크기
                        tv_one.setTextColor(Color.BLACK);
                        LinearLayout.LayoutParams params_tv = new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                        params_tv.gravity = Gravity.CENTER;
                        params_tv.setMargins(0, 100, 0, 0);
                        layout.setBackgroundColor(Color.rgb(251,210,228));
                        layout.addView(tv_one, params_tv);



                        book_list_text += accountItem.getName()+"\n";

                        String book_text_last = book_list_text.substring(book_list_text.lastIndexOf("\n"));
                        TextView tv_last = new TextView(getApplicationContext());
                        tv_last.setText(book_text_last);
                        layout.addView(tv_last);
                        Log.d(TAG,"뭐냐"+book_text_last);

                        book_image_one = accountItem.getImage();
                        ImageView iv_one = new ImageView(getApplicationContext());
                        Picasso.get().load(book_image_one).into(iv_one);  // 첫번째 이미지
//                        iv_one.setLayoutParams(new LinearLayout.LayoutParams(500,500));
//                        iv_one.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        LinearLayout.LayoutParams params_iv = new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                        params_iv.gravity = Gravity.CENTER;
                        params_iv.height = 500;
                        params_iv.width = 600;
                        layout.addView(iv_one, params_iv);

                        book_list_image += accountItem.getImage()+"@";

                        String book_image_last = book_list_image.substring(book_list_image.lastIndexOf("@"));
                        ImageView iv_last = new ImageView(getApplicationContext());
                        Picasso.get().load(book_image_last).into(iv_last);  // 2번째 부터 이미지
                        layout.addView(iv_last);

                        book_number_one = ""+accountItem.getId();
                        Button btn_one = new Button(getApplicationContext());
                        LinearLayout.LayoutParams params_btn_info = new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                        params_btn_info.gravity = Gravity.CENTER;

                        Typeface typeface_btn = Typeface.createFromAsset(getAssets(), "maplestory_light.ttf"); //버튼 폰트
                        btn_one.setTypeface(typeface_btn);

                        btn_one.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30); // 텍스트 크기
                        btn_one.setText("자세히 보기"); //첫번째 버튼
                        btn_one.setBackgroundResource(btn_carmer);
                        params_btn_info.setMargins(80, 20,80,0);
                        layout.addView(btn_one, params_btn_info);

                        Log.d(TAG,"번호악"+book_number_one);



                        final String finalBook_number_one = book_number_one;  //정보보기 버튼
                        btn_one.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                final Intent intent = new Intent(getApplicationContext(), PlantBookDetail.class);
                                Call<List<AccountItem>> mydatailCall = mMyAPI.get_my_book_detail(android_id, finalBook_number_one);
                                mydatailCall.enqueue(new Callback<List<AccountItem>>() {
                                    @Override
                                    public void onResponse(Call<List<AccountItem>> call, Response<List<AccountItem>> response) {
                                        if (response.isSuccessful()) {
                                            Log.d(TAG,"번호"+finalBook_number_one);
                                            List<AccountItem> versionList =response.body();
                                            String first_img_detail_txt = "";
                                            String first_name_datail_txt = "";
                                            String first_flower_detail_txt = "";
                                            String first_content_detail_txt = "";
                                            for(AccountItem accountItem:versionList){
                                                Log.d(TAG,"ㅎ"+accountItem.getName());

                                                first_img_detail_txt =""+accountItem.getImage();
                                                first_name_datail_txt = ""+accountItem.getName();
                                                first_flower_detail_txt = ""+accountItem.getFlower();
                                                first_content_detail_txt = ""+accountItem.getContent();
                                                Log.d(TAG,"뭐냐"+first_flower_detail_txt);
                                                Log.d(TAG,"뭐냐 이미지"+first_img_detail_txt);
                                            }
                                            intent.putExtra("first_img_detail_txt",first_img_detail_txt);
                                            intent.putExtra("first_name_datail_txt",first_name_datail_txt);
                                            intent.putExtra("first_flower_detail_txt",first_flower_detail_txt);
                                            intent.putExtra("first_content_detail_txt",first_content_detail_txt);

                                            startActivity(intent);
                                        } else {
                                            int StatusCode = response.code();
                                            Log.d(TAG, "dd아" + StatusCode);
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<List<AccountItem>> call, Throwable t) {
                                        Log.d(TAG, "실패" + t.getMessage());
                                    }

                                });
                            }
                        });

                        Button btn_delete = new Button(getApplicationContext()); //삭제 버튼
                        btn_delete.setText("삭제");
                        LinearLayout.LayoutParams params_btn_delete = new LinearLayout.LayoutParams(
                                ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
                        params_btn_delete.gravity = Gravity.CENTER;

                        btn_delete.setTypeface(typeface_btn); //폰트 설정

                        btn_delete.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 30); // 텍스트 크기
                        btn_delete.setBackgroundResource(btn_carmer);
                        params_btn_delete.setMargins(80, 20,80,0);
                        layout.addView(btn_delete, params_btn_delete);

                        btn_delete.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                final Intent intent = new Intent(getApplicationContext(), PlantBook.class);
                                Call<AccountItem> mydeleteCall = mMyAPI.delete_book_list(finalBook_number_one);
                                mydeleteCall.enqueue(new Callback<AccountItem>() {
                                    @Override
                                    public void onResponse(Call<AccountItem> call, Response<AccountItem> response) {
                                        if (response.isSuccessful()) {
                                            AccountItem versionList =response.body();
                                            Log.d(TAG, "dd마마마ㅏㅏ아" + versionList);
                                            startActivity(intent);
                                        } else {
                                            int StatusCode = response.code();
                                            Log.d(TAG, "dd아" + StatusCode);
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<AccountItem> call, Throwable t) {
                                        Log.d(TAG, "실패" + t.getMessage());
                                    }

                                });
                            }
                        });
                    }

                }else{
                    int StatusCode =response.code();
                    Log.d(TAG,"dd아"+StatusCode);
                }
            }

            @Override
            public void onFailure(Call<List<AccountItem>> call, Throwable t) {

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
