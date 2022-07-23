package com.example.image_post

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.image_post.databinding.ActivityMainBinding
import com.filestack.Config
import com.filestack.FileLink
import com.filestack.Sources
import com.filestack.android.FilestackPicker
import com.filestack.android.FsConstants
import com.filestack.android.Selection
import com.filestack.android.Theme
import java.util.*

class MainActivity : AppCompatActivity() {

    private val TAG = MainActivity::class.java.simpleName
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private val API_KEY = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val intentFilter = IntentFilter(FsConstants.BROADCAST_UPLOAD)
        LocalBroadcastManager.getInstance(this).registerReceiver(uploadReceiver, intentFilter)
        binding.selectFab.setOnClickListener {
            launchPicker()

        }
    }
    private fun launchPicker(){
        val mimeTypes = mutableListOf("application/pdf","image/*","video/*")
        val config = Config(API_KEY)
        val sources= mutableListOf(
            Sources.CAMERA,
            Sources.DEVICE,
            Sources.GOOGLE_DRIVE,
            Sources.GOOGLE_PHOTOS)

        val theme = Theme.Builder()
            .accentColor(ContextCompat.getColor(this, R.color.white))
            .backgroundColor(
                ContextCompat.getColor(this,
                    R.color.purple_700)
            )
            .textColor(ContextCompat.getColor(this, R.color.white))
            .build()
        val picker = FilestackPicker.Builder()
            .config(config)
            .sources(sources)
            .autoUploadEnabled(true)
            .mimeTypes(mimeTypes)
            .multipleFilesSelectionEnabled(false)
            .theme(theme)
            .build()
        picker.launch(this)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (FilestackPicker.canReadResult(requestCode, resultCode)){
           val selections = FilestackPicker.getSelectedFiles(data)
            val selection = selections[0]
            val name = String.format(Locale.ROOT, selection.name)

            Log.i(TAG, "$name has been selected")
        }
    }
    private val uploadReceiver: BroadcastReceiver = object : BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {

            val status = intent?.getStringExtra(FsConstants.EXTRA_STATUS)
            val selection = intent?.getParcelableExtra<Selection>(FsConstants.EXTRA_SELECTION)
            val fileLink = intent?.getSerializableExtra(FsConstants.EXTRA_FILE_LINK) as FileLink

            val name = selection?.name
            val handle = if (!fileLink.handle.isNullOrBlank()) fileLink.handle else "No Link found"

            Log.d(TAG, "$handle and $name and ${selection?.mimeType} has been received")
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(uploadReceiver)
    }
}