package com.dbottillo.mtgsearchfree.util

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.dbottillo.mtgsearchfree.GlideApp
import com.dbottillo.mtgsearchfree.R
import com.dbottillo.mtgsearchfree.model.MTGCard
import com.dbottillo.mtgsearchfree.ui.views.MTGLoader

fun MTGCard.loadInto(loader: MTGLoader? = null, imageView: ImageView, retry: View? = null) {
    val second = if (!number.isNullOrEmpty() && set != null && !types.contains("Plane")
            && set?.code?.toUpperCase() != "6ED"
            && set?.code?.toUpperCase() != "RIX") {
        mtgCardsInfoImage
    } else null
    Triple(name, second, gathererImage).loadInto(loader, imageView, retry)
}

fun Triple<String, String?, String>.loadInto(loader: MTGLoader? = null, imageView: ImageView, retry: View? = null) {
    loader?.show()
    retry?.hide()
    imageView.contentDescription = first
    if (second != null) {
        TrackingManager.trackImage(second)
        GlideApp.with(imageView.context)
                .load(second)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(withListener(loader = loader, retryView = retry, hideOnError = false))
                .error(R.drawable.left_debug)
                .error(GlideApp
                        .with(imageView.context)
                        .load(third)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .listener(withListener(loader = loader, retryView = retry, hideOnError = true))
                        .error(R.drawable.left_debug))
                .into(imageView)
    } else {
        GlideApp.with(imageView.context)
                .load(third)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .listener(withListener(loader = loader, retryView = retry, hideOnError = true))
                .error(R.drawable.left_debug)
                .into(imageView)
    }
}

fun withListener(loader: MTGLoader? = null,
                 retryView: View? = null,
                 hideOnError: Boolean): RequestListener<Drawable> {
    return object : RequestListener<Drawable> {
        override fun onLoadFailed(e: GlideException?,
                                  model: Any?,
                                  target: Target<Drawable>?,
                                  isFirstResource: Boolean): Boolean {
            if (hideOnError) {
                loader?.hide()
                retryView?.show()
            }
            return false
        }

        override fun onResourceReady(resource: Drawable?,
                                     model: Any?,
                                     target: Target<Drawable>?,
                                     dataSource: DataSource?,
                                     isFirstResource: Boolean): Boolean {
            loader?.hide()
            return false
        }

    }
}

fun MTGCard.prefetchImage(context: Context) {
    TrackingManager.trackImage(mtgCardsInfoImage)
    GlideApp.with(context)
            .load(mtgCardsInfoImage)
            .listener(object : RequestListener<Drawable> {
                override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                    return false
                }

                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                    TrackingManager.trackImage(gathererImage)
                    GlideApp
                            .with(context)
                            .load(gathererImage)
                            .preload()
                    return true
                }

            })
            .preload()
}
