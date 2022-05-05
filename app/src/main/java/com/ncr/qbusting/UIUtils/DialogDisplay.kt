package com.ncr.qbusting.UIUtils

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface

object DialogDisplay {
     fun promptOkDialog(
        dialogTitle: String,
        dialogMessage: String,
        isError: Boolean,
        activity: Context
    ) {
        var builder = AlertDialog.Builder(activity)
        builder.setCancelable(false)
        builder.setTitle(dialogTitle)
        /*if(isError)
            builder.setIcon(R.drawable.ic_error)
        else
            builder.setIcon(R.drawable.ic_info)*/
        builder.setMessage(dialogMessage)

        builder.setPositiveButton("Ok", DialogInterface.OnClickListener{ dialog, id ->
            dialog.cancel()
        })

        var alert = builder.create()
        alert.show()
        alert.getButton(AlertDialog.BUTTON_POSITIVE)
    }
}