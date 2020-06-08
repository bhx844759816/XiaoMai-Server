package com.guangzhida.xiaomai.server.view.custom;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.guangzhida.xiaomai.server.R;
import com.ypx.imagepicker.adapter.PickerItemAdapter;
import com.ypx.imagepicker.bean.ImageItem;
import com.ypx.imagepicker.bean.selectconfig.BaseSelectConfig;
import com.ypx.imagepicker.data.ICameraExecutor;
import com.ypx.imagepicker.data.IReloadExecutor;
import com.ypx.imagepicker.data.ProgressSceneEnum;
import com.ypx.imagepicker.presenter.IPickerPresenter;
import com.ypx.imagepicker.views.PickerUiConfig;
import com.ypx.imagepicker.views.PickerUiProvider;
import com.ypx.imagepicker.views.base.PickerControllerView;
import com.ypx.imagepicker.views.base.PickerFolderItemView;
import com.ypx.imagepicker.views.base.PickerItemView;
import com.ypx.imagepicker.views.base.PreviewControllerView;
import com.ypx.imagepicker.views.base.SingleCropControllerView;
import com.ypx.imagepicker.views.wx.WXFolderItemView;
import com.ypx.imagepicker.views.wx.WXTitleBar;

import java.util.ArrayList;

/**
 * Description: 自定义样式,目前mars就采用此样式
 * <p>
 * Author: peixing.yang
 * Date: 2019/2/21
 */
public class CustomImgPickerPresenter implements IPickerPresenter {

    @Override
    public void displayImage(View view, ImageItem item, int size, boolean isThumbnail) {
        if (item.getUri() != null) {
            Glide.with(view.getContext()).load(item.getUri()).apply(new RequestOptions()
                    .format(isThumbnail ? DecodeFormat.PREFER_RGB_565 : DecodeFormat.PREFER_ARGB_8888))
                    .into((ImageView) view);
        } else {
            Glide.with(view.getContext()).load(item.path).apply(new RequestOptions()
                    .format(isThumbnail ? DecodeFormat.PREFER_RGB_565 : DecodeFormat.PREFER_ARGB_8888))
                    .into((ImageView) view);
        }
    }

    @NonNull
    @Override
    public PickerUiConfig getUiConfig(@Nullable Context context) {
        PickerUiConfig uiConfig = new PickerUiConfig();
        uiConfig.setShowStatusBar(true);
        uiConfig.setStatusBarColor(Color.parseColor("#F5F5F5"));
        uiConfig.setPickerBackgroundColor(Color.WHITE);
        uiConfig.setFolderListOpenDirection(PickerUiConfig.DIRECTION_TOP);
        uiConfig.setFolderListOpenMaxMargin(0);

        uiConfig.setPickerUiProvider(new PickerUiProvider() {
            @Override
            public PickerControllerView getTitleBar(Context context) {
                WXTitleBar titleBar = (WXTitleBar) super.getTitleBar(context);
                titleBar.setCompleteText("下一步");
                titleBar.setCompleteBackground(null, null);
                titleBar.setCompleteTextColor(Color.parseColor("#859D7B"),
                        Color.parseColor("#50859D7B"));
                titleBar.centerTitle();
                titleBar.setShowArrow(true);
                titleBar.setCanToggleFolderList(true);
                titleBar.setBackIconID(R.mipmap.picker_icon_close_black);
                return titleBar;
            }

            @Override
            public PickerItemView getItemView(Context context) {
                return new CustomPickerItem(context);
            }

            @Override
            public PickerControllerView getBottomBar(Context context) {
                return null;
            }

            @Override
            public PreviewControllerView getPreviewControllerView(Context context) {
                return new CustomPreviewControllerView(context);
            }

            @Override
            public SingleCropControllerView getSingleCropControllerView(Context context) {
                return new CustomCropControllerView(context);
            }

            @Override
            public PickerFolderItemView getFolderItemView(Context context) {
                WXFolderItemView itemView = (WXFolderItemView) super.getFolderItemView(context);
                itemView.setIndicatorColor(Color.parseColor("#859D7B"));
                return itemView;
            }
        });
        return uiConfig;
    }

    @Override
    public void tip(Context context, String msg) {
    }

    @Override
    public void overMaxCountTip(Context context, int maxCount) {
    }

    @Override
    public DialogInterface showProgressDialog(@Nullable Activity activity, ProgressSceneEnum progressSceneEnum) {
        return ProgressDialog.show(activity, null, progressSceneEnum == ProgressSceneEnum.crop ? "正在剪裁..." : "正在加载...");
    }

    @Override
    public boolean interceptPickerCompleteClick(@Nullable Activity activity, ArrayList<ImageItem> selectedList, BaseSelectConfig selectConfig) {
        return false;
    }

    @Override
    public boolean interceptPickerCancel(@Nullable Activity activity, ArrayList<ImageItem> selectedList) {
        return false;
    }

    @Override
    public boolean interceptItemClick(@Nullable Activity activity, ImageItem imageItem,
                                      ArrayList<ImageItem> selectImageList,
                                      ArrayList<ImageItem> allSetImageList,
                                      BaseSelectConfig selectConfig, PickerItemAdapter adapter,
                                      @Nullable IReloadExecutor reloadExecutor) {
        return false;
    }

    @Override
    public boolean interceptCameraClick(@Nullable Activity activity, ICameraExecutor takePhoto) {
        return false;
    }
}
