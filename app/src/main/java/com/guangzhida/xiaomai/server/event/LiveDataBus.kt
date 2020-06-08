package com.guangzhida.xiaomai.server.event

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer


object LiveDataBus {
    private val bus = mutableMapOf<String, BusMutableLiveData<Any>>()
    fun <T> with(
        key: String,
        type: Class<T>
    ): MutableLiveData<T> {
        if (!bus.containsKey(key)) {
            bus[key] = BusMutableLiveData()
        }
        return bus[key] as MutableLiveData<T>
    }

    fun with(key: String): MutableLiveData<Any> {
        return with(key, Any::class.java)
    }

    class BusMutableLiveData<T> : MutableLiveData<T>() {
        private val observerMaps = mutableMapOf<Observer<in T>, Observer<in T>>()


        override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
            super.observe(owner, observer)
            try { // 设置observer的version和LiveData一致
                hook(observer)
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }

        }

        override fun observeForever(observer: Observer<in T>) {
            if (!observerMaps.containsKey(observer)) {
                observerMaps[observer] = ObserverWrap(observer)
            }
            super.observeForever(observerMaps[observer]!!)
        }

        override fun removeObserver(observer: Observer<in T>) {
            var realObserver: Observer<in T>? = null
            realObserver = if (observerMaps.containsKey(observer)) {
                observerMaps.remove(observer)
            } else {
                observer
            }
            super.removeObserver(realObserver!!)
        }


        @Throws(Exception::class)
        private fun hook(observer: Observer<in T>) { // 获取livedata的class对象
            val classLiveData =
                LiveData::class.java
            // 获取   LiveData类的mObservers对象 （Map对象）的 Field对象
            val fieldObservers =
                classLiveData.getDeclaredField("mObservers")
            // 将mObservers 的private设置为 public
            fieldObservers.isAccessible = true
            //  获取当前livedata的mObservers对象(map)
            val objectObservers = fieldObservers[this]
            // 拿到mObservers（map）的class对象
            val classObservers: Class<*> = objectObservers.javaClass
            // 通过map的class对象拿到get（）的method对象
            val methodGet =
                classObservers.getDeclaredMethod("get", Any::class.java)
            methodGet.isAccessible = true
            // 通过map 的 get Method对象 拿到值 （Entry）  （arg1：map ，arg2：key ）
            val objectWrapperEntry = methodGet.invoke(objectObservers, observer)
            // 拿到wrapper
            var objectWrapper: Any? = null
            if (objectWrapperEntry is Map.Entry<*, *>) {
                objectWrapper = objectWrapperEntry.value
            }
            if (objectWrapper == null) {
                throw NullPointerException("Wrapper can not be bull!")
            }
            // 反射wrapper对象
            val classObserverWrapper: Class<*> = objectWrapper.javaClass.superclass
            // 拿到wrapper的version
            val fieldLastVersion =
                classObserverWrapper.getDeclaredField("mLastVersion")
            fieldLastVersion.isAccessible = true
            //get livedata's version
            val fieldVersion = classLiveData.getDeclaredField("mVersion")
            fieldVersion.isAccessible = true
            val objectVersion = fieldVersion[this]
            //set wrapper's version
            fieldLastVersion[objectWrapper] = objectVersion
        }
    }


    class ObserverWrap<T>(observer: Observer<T>) : Observer<T> {
        private var observer: Observer<T>? = observer

        override fun onChanged(t: T) {
            if (observer != null) {
                if (isCallOnObserve()) {
                    return
                }
                observer!!.onChanged(t)
            }
        }

        private fun isCallOnObserve(): Boolean {
            val stackTrace =
                Thread.currentThread().stackTrace
            if (stackTrace != null && stackTrace.isNotEmpty()) {
                stackTrace.forEach { element ->
                    if ("android.arch.lifecycle.LiveData" == element.className
                        && "observeForever" == element.methodName
                    ) {
                        return true
                    }
                }
            }
            return false
        }
    }
}