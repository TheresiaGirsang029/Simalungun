package com.dicoding.simalungun.fragments


import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.core.content.ContextCompat
import com.dicoding.simalungun.R
import com.dicoding.simalungun.ml.Model
import org.tensorflow.lite.DataType
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private val CAMERA_PERMISSION_CODE = 1000
    private val IMAGE_CAPTURE_CODE = 1001
    private var imageUri: Uri? = null
    private var imageView: ImageView? = null

    private var param1: String? = null
    private var param2: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    private fun requestCameraPermission(): Boolean {
        var permissionGranted = false

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            //val cameraPermissionGranted = checkSelfPermission(activity as Context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
            if (ContextCompat.checkSelfPermission(activity as Context, android.Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED
            ){
                openCameraInterface()
            //val permission = arrayOf(Manifest.permission.CAMERA)

            //requestPermissions(permission, CAMERA_PERMISSION_CODE)
            }
            else{
                permissionGranted = true
            }
        }
        else{
            permissionGranted = true
        }
        return permissionGranted
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.size == 1 && grantResults [0] == PackageManager.PERMISSION_GRANTED){
                openCameraInterface()
            }
            else{
                showAlert("Camera permission was denied. Unable to take a picture");
            }
        }
    }

    private fun openCameraInterface(){
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, R.string.take_picture)
        values.put(MediaStore.Images.Media.DESCRIPTION, R.string.take_picture_description)
        imageUri = activity?.contentResolver?.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)

        startActivityForResult(intent, IMAGE_CAPTURE_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK){
            imageView?.setImageURI(imageUri)
        }
        else {
            showAlert("Failed")
        }
    }

    private fun showAlert(message: String){
        val builder = AlertDialog.Builder(activity as Context)
        builder.setMessage(message)
        builder.setPositiveButton(R.string.ok_button_title, null)

        val dialog = builder.create()
        dialog.show()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imageView = view.findViewById(R.id.iv6)

        view.findViewById<Button>(R.id.btn5).setOnClickListener{
            val permissionGranted = requestCameraPermission()
            if (permissionGranted){
                openCameraInterface()
            }
        }
    }

    private fun onActivityResult(bitmap: Bitmap){
        val model = Model.newInstance(requireContext())
        val resized = Bitmap.createScaledBitmap(bitmap, 224, 224, true)
        val tfbuffer = TensorImage.fromBitmap(resized)
        val byteBuffer = tfbuffer.buffer

        // Creates inputs for reference.
        val inputFeature0 = TensorBuffer.createFixedSize(intArrayOf(1, 224, 224, 3), DataType.FLOAT32)
        inputFeature0.loadBuffer(byteBuffer)

        // Runs model inference and gets result.
        val outputs = model.process(inputFeature0)
        val outputFeature0 = outputs.outputFeature0AsTensorBuffer

        // Releases model resources if no longer used.
        model.close()
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SearchFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SearchFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}