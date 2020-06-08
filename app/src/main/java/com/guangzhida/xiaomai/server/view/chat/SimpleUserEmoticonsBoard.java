package com.guangzhida.xiaomai.server.view.chat;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.guangzhida.xiaomai.server.R;

import github.ll.emotionboard.EmoticonsBoard;
import github.ll.emotionboard.utils.EmoticonsKeyboardUtils;

public class SimpleUserEmoticonsBoard extends EmoticonsBoard {

    public final int APPS_HEIGHT = 120;
    private OnSoundRecordCallBack mSoundRecordCallBack;
    private ChatSoundRecordPressedView mPressedView;

    public SimpleUserEmoticonsBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPressedView = (ChatSoundRecordPressedView) getBtnVoice();
        mPressedView.setCallback(new ChatSoundRecordPressedView.PressCallback() {
            @Override
            public void onStartRecord() {
                if (mSoundRecordCallBack != null) {
                    mSoundRecordCallBack.onStartRecord();
                }
            }

            @Override
            public void onStopRecord() {
                //录音完成，结束录音
                if (mSoundRecordCallBack != null) {
                    mSoundRecordCallBack.onStopRecord();
                }
            }


            @Override
            public void onCancelRecord() {
                //取消录音
                if (mSoundRecordCallBack != null) {
                    mSoundRecordCallBack.onCancelRecord();
                }
            }
        });
    }

    @Override
    protected void inflateKeyboardBar() {
        inflater.inflate(R.layout.view_keyboard_userdef, this);
    }

    @Override
    protected View inflateFunc() {
        return inflater.inflate(R.layout.view_func_emoticon_userdef, null);
    }

    @Override
    public void reset() {
        EmoticonsKeyboardUtils.closeSoftKeyboard(getContext());
        funcLayout.hideAllFuncView();
        btnFace.setImageResource(R.mipmap.chatting_emoticons);
    }

    @Override
    public void onFuncChange(int key) {
        if (FUNC_TYPE_EMOTION == key) {
            btnFace.setImageResource(R.mipmap.chatting_softkeyboard);
        } else {
            btnFace.setImageResource(R.mipmap.chatting_emoticons);
        }
        checkVoice();
    }

    @Override
    public void onSoftKeyboardClose() {
        super.onSoftKeyboardClose();
        if (funcLayout.getCurrentFuncKey() == FUNC_TYPE_APPPS) {
            setFuncViewHeight(EmoticonsKeyboardUtils.dip2px(getContext(), APPS_HEIGHT));
        }
    }

    @Override
    protected void showText() {
        emoticonsEditText.setVisibility(VISIBLE);
        btnFace.setVisibility(VISIBLE);
        btnVoice.setVisibility(GONE);
    }

    @Override
    protected void showVoice() {
        emoticonsEditText.setVisibility(GONE);
        btnFace.setVisibility(GONE);
        btnVoice.setVisibility(VISIBLE);
        reset();
    }

    @Override
    protected void checkVoice() {
        if (btnVoice.isShown()) {
            btnVoiceOrText.setImageResource(R.mipmap.chatting_softkeyboard);
        } else {
            btnVoiceOrText.setImageResource(R.mipmap.chatting_vodie);
        }
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == github.ll.emotionboard.R.id.btn_voice_or_text) {
            if (emoticonsEditText.isShown()) {
                btnVoiceOrText.setImageResource(R.mipmap.chatting_softkeyboard);
                showVoice();
                EmoticonsKeyboardUtils.closeSoftKeyboard(emoticonsEditText);
            } else {
                showText();
                btnVoiceOrText.setImageResource(R.mipmap.chatting_vodie);
                EmoticonsKeyboardUtils.openSoftKeyboard(emoticonsEditText);
            }
        } else if (i == github.ll.emotionboard.R.id.btn_face) {
            toggleFuncView(FUNC_TYPE_EMOTION);
        } else if (i == github.ll.emotionboard.R.id.btn_multimedia) {
            toggleFuncView(FUNC_TYPE_APPPS);
            setFuncViewHeight(EmoticonsKeyboardUtils.dip2px(getContext(), APPS_HEIGHT));
        }
    }

    /**
     * 设置音量
     */
    public void setVolume(int volume) {
        mPressedView.setVolume(volume);
    }

    /**
     * 设置录音的回调
     */
    public void setOnSoundRecordCallBack(OnSoundRecordCallBack callBack) {
        mSoundRecordCallBack = callBack;
    }

    /**
     * 设置是否需要显示录音的Dialog
     */
    public void setChatSoundRecordPressedViewShowDialog(boolean isShow) {
         mPressedView.setIsShowDialog(isShow);
    }

    /**
     * 录音回调
     */
    public interface OnSoundRecordCallBack {
        void onStartRecord();

        void onStopRecord();

        void onCancelRecord();
    }
}
