package com.tiparo.tripway.views.ui

import android.content.Context
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.tiparo.tripway.R
import kotlinx.android.synthetic.main.dialog_fragment_image_viewer.view.*
import timber.log.Timber


class ImageViewerDialogFragment : Fragment() {
    private val args: ImageViewerDialogFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("onCreate")

        requireActivity().apply {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            window.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Timber.d("onCreateView")
        return inflater.inflate(R.layout.dialog_fragment_image_viewer, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Timber.d("onViewCreated")

        Glide.with(requireContext())
            .load(Uri.parse(args.photoUri))
            .error(R.drawable.trip_card_own_placeholder)
            .into(view.imageView)
    }

    //    override fun onResume() {
//        super.onResume()
//        Timber.d("onResume")
//
//        val params = dialog?.window?.attributes
//        params?.let {
//            dialog?.window?.attributes = params
//        }
    override fun onDestroy() {
        super.onDestroy()
        requireActivity().apply {
            requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            window.decorView.systemUiVisibility = 0
        }
        Timber.d("onDestroy")
    }

//    }
    companion object {

        const val ARG_IMAGE_URI = "imageUri"
        const val TAG_FRAGMENT = "imageViewer"

        fun newInstance(uri: Uri) = ImageViewerDialogFragment().apply {
            //            setStyle(STYLE_NO_TITLE, R.style.DialogFullScreen)
            arguments = Bundle().apply {
                putString(ARG_IMAGE_URI, uri.toString())
            }
        }
    }
}
