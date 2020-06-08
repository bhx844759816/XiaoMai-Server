package com.guangzhida.xiaomai.server.ui

import android.Manifest
import android.graphics.Color
import android.os.Environment
import androidx.lifecycle.Observer
import com.guangzhida.xiaomai.ktxlibrary.ext.clickN
import com.guangzhida.xiaomai.ktxlibrary.ext.sharedpreference.putSpValue
import com.guangzhida.xiaomai.server.BaseApplication
import com.guangzhida.xiaomai.server.R
import com.guangzhida.xiaomai.server.USER_MODEL_KEY
import com.guangzhida.xiaomai.server.base.BaseActivity
import com.guangzhida.xiaomai.server.ext.loadCircleImage
import com.guangzhida.xiaomai.server.http.BASE_URL
import com.guangzhida.xiaomai.server.ui.viewmodel.PersonInfoViewModel
import com.guangzhida.xiaomai.server.utils.LogUtils
import com.guangzhida.xiaomai.server.view.custom.CustomImgPickerPresenter
import com.ypx.imagepicker.ImagePicker
import com.ypx.imagepicker.bean.MimeType
import com.ypx.imagepicker.bean.SelectMode
import com.ypx.imagepicker.bean.selectconfig.CropConfig
import kotlinx.android.synthetic.main.activity_person_info_layout.*
import permissions.dispatcher.ktx.withPermissionsCheck
import top.zibin.luban.Luban
import top.zibin.luban.OnCompressListener
import java.io.File

/**
 * 个人信息的界面
 */
class PersonInfoActivity : BaseActivity<PersonInfoViewModel>() {
    private var mPhotoFile: File? = null
    override fun getLayoutId(): Int = R.layout.activity_person_info_layout
    private val mImgSaveDir by lazy {
        getExternalFilesDir("pic")?.absolutePath
            ?: Environment.getExternalStorageDirectory().absolutePath + "/xiaomai-server/pic"
    }

    override fun initView() {
        LogUtils.i("user${BaseApplication.instance().mUserModel}")
        initUserInfo()
    }

    override fun initListener() {
        toolBar.setNavigationOnClickListener {
            finish()
        }
        rlSave.clickN {
            val nickName = etUserNickName.text.toString().trim()
            val sex = if (rbSexBoy.isChecked) "1" else "2"
            val age = etUserAge.text.toString().trim()
            val params = mutableMapOf(
                "id" to BaseApplication.instance().mUserModel!!.id,
                "nickName" to nickName,
                "sex" to sex,
                "age" to age
            )
            mViewModel.uploadPersonInfo(mPhotoFile, params)

        }
        tvUserAvatar.clickN {
            selectPhoto()
        }
        ivUserAvatar.clickN {
            selectPhoto()
        }
    }

    override fun initObserver() {
        //修改用户信息的结果
        mViewModel.mModifyUserInfoResult.observe(this, Observer {
            putSpValue(USER_MODEL_KEY, it)
        })
    }


    /**
     * 选择图片
     */
    private fun selectPhoto() =
        withPermissionsCheck(Manifest.permission.WRITE_EXTERNAL_STORAGE, onShowRationale = {
            it.proceed()
        }) {
            ImagePicker.withMulti(CustomImgPickerPresenter())//指定presenter
                //设置选择的最大数
                .setMaxCount(1)
                //设置列数
                .setColumnCount(4)
                //设置要加载的文件类型，可指定单一类型
                .mimeTypes(MimeType.ofImage())
                .showCamera(true)//显示拍照
                .setPreview(true)//开启预览
                //大图预览时是否支持预览视频
                .setPreviewVideo(false)
                //设置视频单选
                .setVideoSinglePick(false)
                //设置图片和视频单一类型选择
                .setSinglePickImageOrVideoType(true)
                //当单选或者视频单选时，点击item直接回调，无需点击完成按钮
                .setSinglePickWithAutoComplete(false)
                //显示原图
                .setOriginal(true)
                //显示原图时默认原图选项开关
                .setDefaultOriginal(true)
                //设置单选模式，当maxCount==1时，可执行单选（下次选中会取消上一次选中）
                .setSelectMode(SelectMode.MODE_SINGLE)
                .cropSaveInDCIM(false)
                .cropRectMinMargin(100)
                .cropStyle(CropConfig.STYLE_FILL)
                .cropGapBackgroundColor(Color.TRANSPARENT)
                .setCropRatio(1, 1)
                .cropAsCircle()
                .crop(this) {
                    compressImg(it[0].path)
                }
        }

    /**
     * 压缩图片
     */
    private fun compressImg(imagePath: String) {
        Luban.with(this)
            .load(imagePath)
            .ignoreBy(100)
            .setTargetDir(mImgSaveDir)
            .setCompressListener(object : OnCompressListener {
                override fun onSuccess(file: File?) {
                    if (file != null && file.exists()) {
                        mPhotoFile = file
                        ivUserAvatar.loadCircleImage(
                            file,
                            holder = R.mipmap.icon_default_header
                        )
                    }
                }

                override fun onError(e: Throwable?) {
                }

                override fun onStart() {
                }

            }).launch()
    }

    private fun initUserInfo() {
        BaseApplication.instance().mUserModel?.let {
            ivUserAvatar.loadCircleImage(
                BASE_URL.substring(0, BASE_URL.length - 1) + it.headUrl,
                holder = R.mipmap.icon_user_center_header
            )
            etUserNickName.setText(it.nickName)
            if (it.sex == "1") {
                rbSexBoy.isChecked = true
                rbSexGirl.isChecked = false
            } else if (it.sex == "2") {
                rbSexBoy.isChecked = false
                rbSexGirl.isChecked = true
            }
            etUserAge.setText(it.age)
        }
    }
}