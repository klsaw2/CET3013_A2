package com.example.cet3013_a2

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.ContextThemeWrapper
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment

class ConfirmationDialog(
    title: String,
    description: String? = null,
    primaryText: String,
    secondaryText: String,
    primaryListener: DialogInterface.OnClickListener,
): DialogFragment() {
    private lateinit var mTitle: String
    private lateinit var mPrimaryText: String
    private lateinit var mSecondaryText: String
    private lateinit var mPrimaryListener: DialogInterface.OnClickListener
    private var mDescription: String? = null

    init {
        mTitle = title
        mPrimaryText = primaryText
        mSecondaryText = secondaryText
        mPrimaryListener = primaryListener
        mDescription = description
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = AlertDialog.Builder(ContextThemeWrapper(requireContext(), R.style.confirmation_dialog))
            .setIcon(ContextCompat.getDrawable(requireContext(), R.drawable.icon_important))
            .setTitle(mTitle)
            .setPositiveButton(mPrimaryText, mPrimaryListener)
            .setNegativeButton(mSecondaryText) { d, w -> }
            .apply {
                if (mDescription != null) {
                    this.setMessage(mDescription)
                }
            }
        return dialog.create()
    }
}