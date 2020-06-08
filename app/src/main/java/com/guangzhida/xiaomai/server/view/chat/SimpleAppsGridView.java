package com.guangzhida.xiaomai.server.view.chat;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.GridView;
import android.widget.RelativeLayout;

import com.guangzhida.xiaomai.server.R;

import java.util.ArrayList;


public class SimpleAppsGridView extends RelativeLayout {

    protected View view;
    private ChattingAppsAdapter mAdppsAdapter;
    public static final String FUNC_PLAY_PHOTO = "拍照";
    public static final String FUNC_SELECT_PHOTO = "图片";

    public SimpleAppsGridView(Context context) {
        this(context, null);
    }

    public SimpleAppsGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.layout_chat_simple_app_view_apps, this);
        init();
    }

    protected void init() {
        GridView gv_apps = (GridView) view.findViewById(R.id.gv_apps);
        gv_apps.setSelector(new ColorDrawable(Color.TRANSPARENT));
        gv_apps.setNumColumns(2);
        ArrayList<AppBean> mAppBeanList = new ArrayList<>();
        mAppBeanList.add(new AppBean(R.mipmap.chatting_photo, FUNC_SELECT_PHOTO));
        mAppBeanList.add(new AppBean(R.mipmap.chatting_camera, FUNC_PLAY_PHOTO));
        mAdppsAdapter = new ChattingAppsAdapter(getContext(), mAppBeanList);
        gv_apps.setAdapter(mAdppsAdapter);
    }

    /**
     * 设置点击回调
     */
    public void setOnAppsAdapterCallBack(ChattingAppsAdapter.OnChattingAppsCallBack callBack) {
        if (mAdppsAdapter != null) {
            mAdppsAdapter.setOnChattingAppsCallBack(callBack);
        }
    }
}
