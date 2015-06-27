package com.tiltcode.tiltcodemanager.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.FeatureInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.tiltcode.tiltcodemanager.R;

import java.io.File;
import java.net.URI;

/**
 * Created by JSpiner on 2015. 6. 21..
 */
public class RegisterActivity extends Activity {

    ImageButton btnFile;
    ImageButton btnImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        init();

    }


    void init(){

        btnFile = ((ImageButton)findViewById(R.id.imv_register_file));
        btnImage =  ((ImageButton)findViewById(R.id.imv_register_image));

        btnFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("file/*");
                startActivityForResult(intent, 1123);
            }
        });

        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 1124);
            }
        });


    }

    Bitmap getPreview(String uri) {
        File image = new File(uri);

        BitmapFactory.Options bounds = new BitmapFactory.Options();
        bounds.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(image.getPath(), bounds);
        if ((bounds.outWidth == -1) || (bounds.outHeight == -1))
            return null;

        int originalSize = (bounds.outHeight > bounds.outWidth) ? bounds.outHeight
                : bounds.outWidth;

        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = 4;//originalSize / THUMBNAIL_SIZE;
        return BitmapFactory.decodeFile(image.getPath(), opts);
    }

    public static Bitmap decodeFile(String file, int size) {
        //Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file, o);

        //Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;

        //Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeFile(file, o2);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        switch(requestCode){
            case 1123:
                if(resultCode==RESULT_OK){
                    String filePath = data.getData().getPath();
                    Log.d("asdf", "file path : " + filePath);
                    //                    textFile.setText(FilePath);
//                    upload(filePath);
                }
                break;
            case 1124:
                if(resultCode==RESULT_OK){
                    String filePath = data.getData().getPath();
                    Log.d("asdf", "file path : " + filePath);
                    //                    textFile.setText(FilePath);
//                    upload(filePath);
                    btnImage.setImageBitmap(getPreview(filePath));
                }
                break;

        }
    }

}
