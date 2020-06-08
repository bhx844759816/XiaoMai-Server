package com.guangzhida.xiaomai.server.view.preview;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.guangzhida.xiaomai.server.R;
import com.ypx.imagepicker.utils.PStatusBarUtil;

import java.util.ArrayList;

/**
 * Time: 2019/11/6 18:24
 * Author:ypx
 * Description:简单的多图预览界面，用于演示ImagePicker.closePickerWithCallback(imageItems);
 */
public class PreviewResultListActivity extends Activity {
    ArrayList<String> imageItems = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PStatusBarUtil.fullScreen(this);
        setContentView(R.layout.activity_image_picker_preview);
        imageItems = getIntent().getStringArrayListExtra("imgUrls");
        int pos = getIntent().getIntExtra("pos", 0);
        ImagesViewPager viewPager = findViewById(R.id.viewpager);
        viewPager.setImageViewList(imageItems, pos);
        viewPager.setImageViewClickCallBack(this::finish);
    }

}
