package com.desynatdealer.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.Window
import com.desynatdealer.R

class ProgressDialogUtils {

    private var mDialog: Dialog? = null

    companion object {
        private var progressDialog: ProgressDialogUtils? = null
        fun getInstance(): ProgressDialogUtils {
            if (progressDialog == null) {
                progressDialog = ProgressDialogUtils()
            }
            return progressDialog!!
        }
    }

    fun showProgress(context: Context, cancelable: Boolean = false) {
        // Only show if context is an Activity and not finishing/destroyed
        val activity = context as? Activity

        if (activity != null && (activity.isFinishing || activity.isDestroyed)) return

        if (mDialog != null && mDialog?.isShowing == true) return


        mDialog = Dialog(context)
        mDialog?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        mDialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mDialog?.setContentView(R.layout.custom_progress_dailog)
        mDialog?.setCancelable(cancelable)
        mDialog?.setCanceledOnTouchOutside(cancelable)
        mDialog?.show()
    }

    fun hideProgress() {
        try {
            mDialog?.let { dialog ->
                if (dialog.isShowing) {
                    if (dialog.window?.decorView?.windowToken != null) {
                        dialog.dismiss()
                    }
                    mDialog = null
                }
            }
        } catch (e: Exception) {
            mDialog = null
        }
    }

    fun dispose() {
        mDialog = null
        progressDialog = null
    }
}
