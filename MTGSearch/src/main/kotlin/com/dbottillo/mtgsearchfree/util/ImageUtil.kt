package com.dbottillo.mtgsearchfree.util

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.target.Target
import com.bumptech.glide.request.transition.Transition
import com.dbottillo.mtgsearchfree.GlideApp
import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.ui.views.MTGLoader

fun MTGCard.loadInto(loader: MTGLoader? = null, imageView: ImageView, retry: View? = null) {
    Pair(name, gathererImage).loadInto(loader, imageView, retry)
}

fun Pair<String, String>.loadInto(loader: MTGLoader? = null, imageView: ImageView, retry: View? = null) {
    loader?.show()
    retry?.hide()
    imageView.contentDescription = first
    val context = imageView.context
    if (context is Activity && (context.isFinishing || context.isDestroyed)) {
        return
    }
    GlideApp.with(context)
            .load(second)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .listener(withListener(loader = loader, retryView = retry, hideOnError = true))
            .error(R.drawable.left_debug)
            .into(imageView)
}

fun withListener(
    loader: MTGLoader? = null,
    retryView: View? = null,
    hideOnError: Boolean
): RequestListener<Drawable> {
    return object : RequestListener<Drawable> {
        override fun onLoadFailed(
            e: GlideException?,
            model: Any?,
            target: Target<Drawable>?,
            isFirstResource: Boolean
        ): Boolean {
            if (hideOnError) {
                loader?.hide()
                retryView?.show()
            }
            return false
        }

        override fun onResourceReady(
            resource: Drawable?,
            model: Any?,
            target: Target<Drawable>?,
            dataSource: DataSource?,
            isFirstResource: Boolean
        ): Boolean {
            loader?.hide()
            return false
        }
    }
}

fun MTGCard.prefetchImage(context: Context) {
    TrackingManager.trackImage(gathererImage)
    GlideApp.with(context)
            .load(gathererImage)
            .preload()
}

fun MTGCard.getBitmap(context: Context, callback: (Bitmap) -> Unit) {
    GlideApp.with(context)
            .asBitmap()
            .load(gathererImage)
            .into(object : SimpleTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    callback(resource)
                }
            })
}