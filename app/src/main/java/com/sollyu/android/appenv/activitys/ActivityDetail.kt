package com.sollyu.android.appenv.activitys

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.sollyu.android.appenv.R

class ActivityDetail : ActivityBase() {

    companion object {
        fun launch(activity: Activity) {
            activity.startActivity(Intent(activity, ActivityDetail::class.java))
        }
    }


    override fun onInitView() {
        super.onInitView()
        setContentView(R.layout.activity_detail)
    }
}
