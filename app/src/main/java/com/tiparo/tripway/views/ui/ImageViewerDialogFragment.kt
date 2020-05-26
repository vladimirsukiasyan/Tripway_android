package com.tiparo.tripway.views.ui

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.tiparo.tripway.R
import kotlinx.android.synthetic.main.dialog_fragment_image_viewer.view.*

class ImageViewerDialogFragment : DialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_fragment_image_viewer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Glide.with(requireContext())
            .load(Uri.parse(requireArguments().getString(ARG_IMAGE_URI)))
            .into(view.imageView)
    }

    override fun onResume() {
        val params = dialog?.window?.attributes
        params?.let {
            dialog?.window?.attributes = params
        }
        super.onResume()
    }

    companion object {
        const val ARG_IMAGE_URI = "imageUri"

        fun newInstance(uri: Uri) = ImageViewerDialogFragment().apply {
            setStyle(STYLE_NO_TITLE, R.style.DialogFullScreen)
            arguments = Bundle().apply {
                putString(ARG_IMAGE_URI, uri.toString())
            }
        }
    }
}
