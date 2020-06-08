package com.guangzhida.xiaomai.server.ui

import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import androidx.work.*
import com.ashokvarma.bottomnavigation.BottomNavigationBar
import com.guangzhida.xiaomai.ktxlibrary.ext.startKtxActivity
import com.guangzhida.xiaomai.server.BaseApplication
import com.guangzhida.xiaomai.server.R
import com.guangzhida.xiaomai.server.base.BaseActivity
import com.guangzhida.xiaomai.server.chat.ChatHelper
import com.guangzhida.xiaomai.server.event.LiveDataBus
import com.guangzhida.xiaomai.server.event.LiveDataBusKey
import com.guangzhida.xiaomai.server.event.messageCountChangeLiveData
import com.guangzhida.xiaomai.server.task.UpdateUserInfoTask
import com.guangzhida.xiaomai.server.ui.fragment.ConversationFragment
import com.guangzhida.xiaomai.server.ui.fragment.HomeFragment
import com.guangzhida.xiaomai.server.ui.viewmodel.MainViewModel
import com.guangzhida.xiaomai.server.utils.ToastUtils
import kotlinx.android.synthetic.main.activity_main.*
import java.util.concurrent.TimeUnit


/**
 * 主界面
 */
class MainActivity : BaseActivity<MainViewModel>() {
    private val mFragments = listOf(
        HomeFragment(),
        ConversationFragment()
    )
    private var mOldPos = 0
    private var exitTime: Long = 0

    override fun getLayoutId(): Int = R.layout.activity_main

    override fun initView() {
        viewPager.adapter = MyFragmentPageAdapter()
        viewPager.isUserInputEnabled = BaseApplication.instance().mUserModel != null
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                id_bottomNavigationBar.selectTab(position)
            }
        })
        startUpdateUserModelTask()

    }

    override fun initListener() {
        id_bottomNavigationBar.setTabSelectedListener(object :
            BottomNavigationBar.OnTabSelectedListener {
            override fun onTabReselected(position: Int) {
            }

            override fun onTabUnselected(position: Int) {
            }

            override fun onTabSelected(position: Int) {
                if (position == 1) {
                    if (BaseApplication.instance().mUserModel == null) {
                        ToastUtils.toastShort("请先登录")
                        startKtxActivity<LoginActivity>()
                        id_bottomNavigationBar.selectTab(mOldPos, false)
                    } else {
                        changeTab(position)
                    }
                } else {
                    changeTab(position)
                }
            }
        })
    }

    private fun changeTab(pos: Int) {
        viewPager.currentItem = pos
        mOldPos = pos
    }

    /**
     * 开启定时更新用户的个人信息的任务
     */
    private fun startUpdateUserModelTask() {
        val constraints: Constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true)
            .build()
        //立即执行一次
        val updateUserInfoTaskOne =
            OneTimeWorkRequest.Builder(UpdateUserInfoTask::class.java)
                .addTag("UpdateUserInfoTask")
                .setConstraints(constraints) //设置触发条件
                .build()
        //然后周期性在执行
        val updateUserInfoTaskPeriodic =
            PeriodicWorkRequest.Builder(UpdateUserInfoTask::class.java, 15, TimeUnit.MINUTES)
                .addTag("UpdateUserInfoTask")
                .setConstraints(constraints)
                .build()
        val list = listOf(updateUserInfoTaskOne, updateUserInfoTaskPeriodic)
        WorkManager.getInstance(this).cancelAllWorkByTag("UpdateUserInfoTask")
        WorkManager.getInstance(this).enqueue(updateUserInfoTaskOne)
    }

    override fun initObserver() {
        messageCountChangeLiveData.observe(this, Observer {
            id_bottomNavigationBar.setUnReadMessageCount(ChatHelper.getUnReadMessageCount())
        })
        LiveDataBus.with(LiveDataBusKey.LOGIN_KEY, Boolean::class.java).observe(this, Observer {
            viewPager.isUserInputEnabled = BaseApplication.instance().mUserModel != null
        })
        LiveDataBus.with(LiveDataBusKey.EXIT_LOGIN_KEY, Boolean::class.java)
            .observe(this, Observer {
                viewPager.isUserInputEnabled = BaseApplication.instance().mUserModel != null
            })
    }


    override fun onResume() {
        super.onResume()
        id_bottomNavigationBar.setUnReadMessageCount(ChatHelper.getUnReadMessageCount())
    }

    override fun onBackPressed() {
        if (System.currentTimeMillis() - exitTime > 2000) {
            ToastUtils.toastShort("再按一次退出程序")
            exitTime = System.currentTimeMillis()
        } else {
            moveTaskToBack(false)
        }
    }


    inner class MyFragmentPageAdapter : FragmentStateAdapter(this) {
        override fun getItemCount(): Int = mFragments.size

        override fun createFragment(position: Int): Fragment {
            return mFragments[position]
        }
    }
}