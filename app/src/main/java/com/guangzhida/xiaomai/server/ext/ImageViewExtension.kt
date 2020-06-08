package com.guangzhida.xiaomai.server.ext

import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import androidx.annotation.DrawableRes
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import java.io.File

fun ImageView.loadImage(uri: String?) {
    Glide.with(this).load(uri).into(this)
}

fun ImageView.loadImage(uri: Uri?) {
    Glide.with(this).load(uri).into(this)
}

fun ImageView.loadImage(uri: String?, @DrawableRes holder: Int) {
    Glide.with(this).load(uri).apply(RequestOptions.placeholderOf(holder)).into(this)
}

fun ImageView.loadImage(resID: Int?, @DrawableRes holder: Int) {
    Glide.with(this).load(resID).apply(RequestOptions.placeholderOf(holder)).into(this)
}

fun ImageView.loadImage(uri: String?, requestListener: RequestListener<Drawable?>) {
    Glide.with(this).load(uri).listener(requestListener).into(this)
}

//fun ImageView.loadImage(uri: String?, width: Int, height: Int) {
//    val multi = MultiTransformation(CropTransformation(width, height))
//    Glide.with(this).load(uri).apply(RequestOptions.bitmapTransform(multi).dontAnimate()).into(this)
//}

fun ImageView.loadImageCenterCrop(uri: String?, @DrawableRes holder: Int? = null) {
    Glide.with(this).load(uri)
        .apply(RequestOptions().dontAnimate().dontTransform().centerCrop().apply {
            if (holder != null) {
                this.placeholder(holder)
            }
        }).into(this)
}

//加载圆角矩形
fun ImageView.loadFilletRectangle(
    uri: String?, @DrawableRes holder: Int? = null,
    roundingRadius: Int = 20
) {

    if (uri.isNullOrBlank()) {
        if (holder != null) {
            setImageResource(holder)
        }
    } else if (holder == null) {
        val options = RequestOptions()
            .transform(CenterCrop(), RoundedCorners(roundingRadius))
        Glide.with(this).load(uri).apply(options).into(this)
    } else {
        val options = RequestOptions()
            .placeholder(holder)
            .error(holder)
            .transform(CenterCrop(), RoundedCorners(roundingRadius))
        Glide.with(this).load(uri).apply(options)
            .into(this)
    }
}

fun ImageView.loadCircleImage(uri: String?, @DrawableRes holder: Int? = null) {
    if (uri.isNullOrBlank()) {
        if (holder != null) {
            setImageResource(holder)
        }
    } else if (holder == null) {
        Glide.with(this).load(uri).apply(RequestOptions().circleCrop()).into(this)
    } else {
        Glide.with(this).load(uri).apply(RequestOptions().placeholder(holder).circleCrop())
            .into(this)
    }
}

fun ImageView.loadCircleImage(file: File?, @DrawableRes holder: Int? = null) {
    if (holder == null) {
        Glide.with(this).load(file).apply(RequestOptions().circleCrop()).into(this)
    } else {
        Glide.with(this).load(file).apply(RequestOptions().placeholder(holder).circleCrop())
            .into(this)
    }
}