package com.duolun.upprice.ui

import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.MotionEvent
import android.widget.SeekBar
import com.duolun.upprice.R
import com.duolun.upprice.app.BaseActivity
import com.duolun.upprice.custom.MagnifierView
import com.duolun.upprice.databinding.MagnifierActivityBinding
import com.blankj.utilcode.util.ScreenUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.luck.picture.lib.config.PictureMimeType
import com.tson.text.seekbar.event.Down
import com.tson.text.seekbar.event.Event
import com.tson.text.seekbar.event.Move
import com.tson.text.seekbar.event.Up
import com.tson.text.seekbar.listener.SeekBarViewOnChangeListener


class MagnifierActivity : BaseActivity<MagnifierActivityBinding>() {

    private var mv: MagnifierView? = null

    override fun onBuildBinding(): MagnifierActivityBinding {
        return MagnifierActivityBinding.inflate(layoutInflater)
    }

    override fun onInitView() {
        val path = intent.getStringExtra("path")
        Glide.with(this@MagnifierActivity)
            .load(if (PictureMimeType.isContent(path)) Uri.parse(path) else path)
            .centerCrop()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>, isFirstResource: Boolean): Boolean {
                    return true
                }

                override fun onResourceReady(
                    resource: Drawable,
                    model: Any,
                    target: Target<Drawable>?,
                    dataSource: DataSource,
                    isFirstResource: Boolean,
                ): Boolean {
                    binding.coverImg.setImageDrawable(resource)
                    reset(1.0f)
                    return true
                }
            })
            .into(binding.coverImg)

        binding.root.postDelayed({

        }, 300)

        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.seekBar.addOnChangeListener(object : SeekBarViewOnChangeListener {
            override fun touch(percent: Float, eventType: Event) {
                when (eventType) {
                    Up -> { // 手指触发抬起
                    }

                    Move -> { // 进度变更 or 手指滑动
                    }

                    Down -> { // 手指按下
                    }
                }
                var scaleFloat = ((percent.toFloat()) / 100)
                if (scaleFloat < 1) {
                    scaleFloat = 1f
                }
                if (scaleFloat > 8) {
                    scaleFloat = 8.0f
                }
                reset(scaleFloat)
            }
        })
    }

    private fun reset(scale: Float) {
        if (mv == null) {
            val rctWidth = ScreenUtils.getScreenWidth()
            val height = ScreenUtils.getScreenHeight()
            val rctHeight = ScreenUtils.getScreenHeight() / 3
            mv = MagnifierView.Builder(this@MagnifierActivity)
                .intiLT(rctWidth / 2 - rctHeight / 2, height / 2 - rctHeight / 2)
                .viewWH(rctHeight, rctHeight)
                .scale(scale)
                .alpha(32)
                .rootVg(binding.touchLayout)
                .color("#ffffff")
                .build()
            //打开放大镜
            mv?.startViewToRoot()
        } else {
            mv?.setScaleZoom(scale)
            mv?.invalidate()
        }
    }

    override fun onStop() {
        super.onStop()
        if (mv != null) {
            //关闭放大镜
            mv?.closeViewToRoot()
        }
    }

    private var downX: Float = 0f
    private var downY: Float = 0f

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.getAction()) {
            MotionEvent.ACTION_DOWN -> {
                downX = event.getRawX()
                downY = event.getRawY()
            }

            MotionEvent.ACTION_UP -> {
                val upX = event.getRawX()
                val upY = event.getRawY()
                if (Math.abs(upX - downX) < 10 && Math.abs(upY - downY) < 10) {
                    mv?.setXY(upX, upY)
                }
            }
        }
        return super.onTouchEvent(event)
    }
}