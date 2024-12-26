package ru.marinalyamina.vetclinic.fragments

import android.app.Dialog
import android.graphics.Bitmap
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import ru.marinalyamina.vetclinic.R

class ImageDialogFragment(private val bitmap: Bitmap) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.dialog_image_fullscreen, container, false)
        val imageView = view.findViewById<ImageView>(R.id.fullscreenImageView)
        imageView.setImageBitmap(bitmap)
        return view
    }
}
