package com.example.stanl.hackathonproject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.util.Arrays;
import java.util.List;

public class ImageListActivity extends AppCompatActivity {
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        final String className = getIntent().getExtras().getString("classname");
        toolbar.setTitle("Images for " + className);

        String[] imageFilesArr = getIntent().getExtras().getStringArray("classImages");
        if (null != imageFilesArr) {
            final List<String> imageArrayList = Arrays.asList(imageFilesArr);
            ListView imageList = (ListView) findViewById(R.id.imageFileList);
            imageList.setAdapter(new ArrayAdapter<>(this, R.layout.activity_listview, imageArrayList));

            imageList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String fullImgPath = getExternalFilesDir(Environment.DIRECTORY_DCIM) +
                            File.separator + className + File.separator + imageArrayList.get(position);

                    Intent dispIntent = new Intent(Intent.ACTION_VIEW);
                    dispIntent.setDataAndType(Uri.fromFile(new File(fullImgPath)), "image/jpeg");
                    startActivity(dispIntent);
                }
            });
        }
    }
}