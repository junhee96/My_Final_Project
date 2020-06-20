package com.example.plnatsub;

import androidx.appcompat.app.AppCompatActivity;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.plnatsub.util.ImageResizeUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.soundcloud.android.crop.Crop;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    Button main_btn1,main_btn2,main_btn3;
    Boolean album = false;
    private final  String TAG = getClass().getSimpleName();
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_ALBUM = 2;
    Uri photoURI,albumURI = null;
    private MyAPI mMyAPI;
    String imageFilePath;
    String formatDate, android_id;
    // server의 url을 적어준다
    private final String BASE_URL = "http://6b95766df9f6.ngrok.io";  //url주소
//    private final String BASE_URL = "http://127.0.0.1:5000/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        main_btn1 = findViewById(R.id.main_btn1);
        main_btn2 = findViewById(R.id.main_btn2);
        main_btn3 = findViewById(R.id.main_btn3);
        //gallery_img = findViewById(R.id.gallery_img);

        // 권한 체크
        TedPermission.with(getApplicationContext())
                .setPermissionListener(permissionListener)
                .setRationaleMessage("카메라 권한이 필요합니다.")
                .setDeniedMessage("거부하셨습니다.")
                .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA)
                .check();

        main_btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), PlnatCarmer.class);
                startActivity(intent);
            }
        });

        main_btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
                intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, PICK_FROM_ALBUM);
            }
        });

        main_btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), PlantBook.class);
                startActivity(intent);

//                Intent intentL = new Intent(getApplicationContext(), Loading.class);
//                startActivity(intentL); //로딩화면 출력

            }
        });
        initMyAPI(BASE_URL);
    }

    PermissionListener permissionListener = new PermissionListener() {
        @Override
        public void onPermissionGranted() {
            Toast.makeText(getApplicationContext(), "권한이 허용됨",Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onPermissionDenied(ArrayList<String> deniedPermissions) {
            Toast.makeText(getApplicationContext(), "권한이 거부됨",Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            Toast.makeText(this, "취소 되었습니다.", Toast.LENGTH_SHORT).show();
        }else{
            switch (requestCode){
                case PICK_FROM_ALBUM:
                    album = true;
                    File albumFile = null;
                    try{
                        albumFile = createImage();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                    if(albumFile != null){
                        albumURI = Uri.fromFile(albumFile);
                    }
                    photoURI = data.getData();
                    Log.d(TAG,"ㅇㅇㅇㅇ : " + photoURI);
                    cropImage();
                case CROP_FROM_ALBUM:
                    Bitmap photo = BitmapFactory.decodeFile(albumURI.getPath());

                    //gallery_img.setImageBitmap(photo); // 사진나옴
                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);

                    if(album == false){
                        mediaScanIntent.setData(photoURI);
                    }else if(album == true){
                        album = false;
                        mediaScanIntent.setData(albumURI);
                    }
                    Log.d(TAG,"ㅇㅇㅇㅅㅂㅈㄱㅇ : " + mediaScanIntent.setData(photoURI));
                    this.sendBroadcast(mediaScanIntent);
                    ImageUpdate();
                    break;
            }
        }
    }

    private File createImage() throws IOException{
//        String imageFileName ="tmp_" + String.valueOf(System.currentTimeMillis())+".jpg";
//        File storageDir = new File(Environment.getExternalStorageDirectory(),imageFileName);
//        mCurrentPhotoPath = storageDir.getAbsolutePath();
//        return storageDir;


        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "TEST_" + timeStamp + "_";
//        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES); // 보안에걸려서 못끄냄
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        imageFilePath = image.getAbsolutePath();

        return image;
    }
    private void cropImage(){
        Intent cropIntent = new Intent("com.android.camera.action.CROP");
        cropIntent.setDataAndType(photoURI,"image/*");
        cropIntent.putExtra("outputX",1080);
        cropIntent.putExtra("outputY",1080);
        cropIntent.putExtra("aspectX",1);
        cropIntent.putExtra("aspectY",1);
        cropIntent.putExtra("scale",true);
        if(album == false){
            cropIntent.putExtra("output",photoURI);
            Log.d(TAG,"ㅇㅇㅇㅅㅂㅈㄱㅇ : " + photoURI);
        }
        else if(album == true){
            cropIntent.putExtra("output",albumURI);
            Log.d(TAG,"ㅇ12521ㄱㅂㅈㅁㄴㅇㄱㅇ : " + albumURI);
        }


        startActivityForResult(cropIntent, CROP_FROM_ALBUM);
    }
    public String getImageNameToUri(Uri data)
    {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(data, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String imgPath = cursor.getString(column_index);
        String imgName = imgPath.substring(imgPath.lastIndexOf("/")+1);
        return imgName;
    }

    public void ImageUpdate(){
        File file = new File(imageFilePath);

        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy.MM.ddHH.mm.ss");
        formatDate = sdfNow.format(date);

        android_id = android.provider.Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);


        Log.d(TAG, "Filename " + file.getName());
        Log.i( "사진이 앨범에 저장되었습니다.3121",file.getName());

        RequestBody mFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part images = MultipartBody.Part.createFormData("images", file.getName(), mFile);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();

        Gson gson = new GsonBuilder().setLenient().create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .build();

        mMyAPI = retrofit.create(MyAPI.class);

        Intent intent = new Intent(getApplicationContext(), Loading.class);
        startActivity(intent); //로딩화면 출력

        Call<AccountItem> call = mMyAPI.upload(images,android_id,formatDate);
        call.enqueue(new Callback<AccountItem>() {
            @Override
            public void onResponse(Call<AccountItem> call, Response<AccountItem> response) {
                Log.i("good", "good");
            }

            @Override
            public void onFailure(Call<AccountItem> call, Throwable t) {
                Log.i(TAG,"Fail msg : " + t.getMessage());



                getplant();



            }
        });

    }

    public void getplant(){
        Log.d(TAG,"안드"+android_id);
        Log.d(TAG,"시간"+formatDate);

        Call<List<AccountItem>> plantCall = mMyAPI.get_plant(""+android_id,""+formatDate);
        plantCall.enqueue(new Callback<List<AccountItem>>() {
            @Override
            public void onResponse(Call<List<AccountItem>> call, Response<List<AccountItem>> response) {
                if(response.isSuccessful()){
                    List<AccountItem> versionList =response.body();
                    Log.d(TAG,"뭐지"+response.body().toString());
                    String first_txt = "";
                    String first_percent_txt = "";
                    String second_txt = "";
                    String second_percent_txt = "";
                    String third_txt = "";
                    String third_percent_txt = "";

                    String my_images = "";

                    for(AccountItem accountItem:versionList){
                        first_txt += accountItem.getFirst_name();
                        first_percent_txt += accountItem.getFirst_percent();
                        second_txt += accountItem.getSecond_name();
                        second_percent_txt += accountItem.getSecond_percent();
                        third_txt += accountItem.getThird_name();
                        third_percent_txt += accountItem.getThird_percent();

                        my_images += ""+BASE_URL+accountItem.getImages();  //url주소
                    }
                    final String one = first_txt;
                    final String two = second_txt;
                    final String three = third_txt;

                    final String my_plant_images = my_images;

                    final Intent intent = new Intent(getApplicationContext(), SearchResult.class);

                    Log.d(TAG,"라라"+one);

                    intent.putExtra("frist_txt",first_txt);
                    intent.putExtra("first_percent_txt",first_percent_txt);

                    intent.putExtra("second_txt",second_txt);
                    intent.putExtra("second_percent_txt",second_percent_txt);

                    intent.putExtra("third_txt",third_txt);
                    intent.putExtra("third_percent_txt",third_percent_txt);

                    intent.putExtra("android_id", android_id);
                    intent.putExtra("formatDate", formatDate);



                    Call<List<AccountItem>> plantconCall = mMyAPI.get_plant_con(one);
                    plantconCall.enqueue(new Callback<List<AccountItem>>() {
                        @Override
                        public void onResponse(Call<List<AccountItem>> call, Response<List<AccountItem>> response) {
                            if (response.isSuccessful()) {
                                List<AccountItem> versionList =response.body();
                                String first_img_txt = "";

                                for(AccountItem accountItem:versionList){
                                    Log.d(TAG,"ㅅ"+one);
                                    Log.d(TAG,"ㅎ"+accountItem.getName());

                                    first_img_txt =""+BASE_URL+accountItem.getImage();  //url주소

                                }
                                intent.putExtra("my_plant_images",my_plant_images);
                                intent.putExtra("first_img_txt",first_img_txt);

                                //startActivity(intent);
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

                    Call<List<AccountItem>> plantconCall1 = mMyAPI.get_plant_con(two);
                    plantconCall1.enqueue(new Callback<List<AccountItem>>() {
                        @Override
                        public void onResponse(Call<List<AccountItem>> call, Response<List<AccountItem>> response) {
                            if (response.isSuccessful()) {
                                List<AccountItem> versionList =response.body();
                                String second_img_txt = "";

                                for(AccountItem accountItem:versionList){
                                    Log.d(TAG,"ㅅ"+two);
                                    Log.d(TAG,"ㅎ"+accountItem.getName());

                                    second_img_txt =""+BASE_URL+accountItem.getImage();  //url주소
                                    Log.d(TAG,"이미지"+second_img_txt);

                                }
                                intent.putExtra("my_plant_images",my_plant_images);
                                intent.putExtra("second_img_txt",second_img_txt);


//                                startActivity(intent);
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

                    Call<List<AccountItem>> plantconCall2 = mMyAPI.get_plant_con(three);
                    plantconCall2.enqueue(new Callback<List<AccountItem>>() {
                        @Override
                        public void onResponse(Call<List<AccountItem>> call, Response<List<AccountItem>> response) {
                            if (response.isSuccessful()) {
                                List<AccountItem> versionList =response.body();
                                String third_img_txt = "";

                                for(AccountItem accountItem:versionList){
                                    Log.d(TAG,"삼"+three);
                                    Log.d(TAG,"셋"+accountItem.getName());

                                    third_img_txt =""+BASE_URL+accountItem.getImage();  //url주소

                                }
                                intent.putExtra("my_plant_images",my_plant_images);
                                intent.putExtra("third_img_txt",third_img_txt);

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
                }else{
                    int StatusCode =response.code();
                    Log.d(TAG,"dd아"+StatusCode);
                }
            }

            @Override
            public void onFailure(Call<List<AccountItem>> call, Throwable t) {}

        });
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
