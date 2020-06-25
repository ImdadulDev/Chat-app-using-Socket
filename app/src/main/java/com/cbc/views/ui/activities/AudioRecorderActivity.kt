package com.cbc.views.ui.activities

import android.app.Activity
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.SeekBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.transition.TransitionManager
import com.cbc.R
import kotlinx.android.synthetic.main.activity_audio_recorder.*
import kotlinx.io.IOException
import java.io.File

class AudioRecorderActivity : AppCompatActivity(), View.OnClickListener {
    private var mRecorder: MediaRecorder? = null
    private var mPlayer: MediaPlayer? = null
    private var fileName: String? = null
    private var lastProgress = 0
    private val mHandler = Handler()
    private var isPlaying = false
    private var recordFile: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_audio_recorder)

        imageViewback.setOnClickListener { finish() }
        audio_record_send.setOnClickListener {
            Log.d("Rec File name", " : $recordFile")
            if(recordFile.isNotEmpty()){
                val returnIntent = intent
                returnIntent.putExtra("record_audio", recordFile)
                returnIntent.putExtra("record_audio_name", recordFile.substring(recordFile.lastIndexOf("Audios/") + 7))
                setResult(Activity.RESULT_OK, returnIntent);
                finish()
            }else {
                Toast.makeText(this, "Recording file not found", Toast.LENGTH_SHORT).show()
            }

        }

        audio_record_cancel.setOnClickListener { finish() }

        imgViewPause.setOnClickListener {
            if (isPlaying && fileName != null) {
                isPlaying = false
                stopPlaying()
            }
        }

        imgBtRecord.setOnClickListener(this)
        imgBtStop.setOnClickListener(this)
        imgViewPlay.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view!!.id) {
            R.id.imgBtRecord -> {
                prepareRecording()
                startRecording()
            }

            R.id.imgBtStop -> {
                prepareStop()
                stopRecording()
            }

            R.id.imgViewPlay -> {
                if (!isPlaying && fileName != null) {
                    isPlaying = true
                    startPlaying()
                } else {
                    isPlaying = false
                    stopPlaying()
                }
            }
        }
    }

    override fun onBackPressed() {
        finish()
    }

    private fun prepareStop() {
        TransitionManager.beginDelayedTransition(llRecorder)
        imgViewPlay.visibility = View.VISIBLE
        imgBtRecord.visibility = View.VISIBLE
        imgBtStop.visibility = View.GONE
    }


    private fun prepareRecording() {
        try {
            TransitionManager.beginDelayedTransition(llRecorder)
            imgViewPlay.visibility = View.GONE
            imgBtRecord.visibility = View.GONE
            imgBtStop.visibility = View.VISIBLE
            seekBar.visibility = View.GONE
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun stopPlaying() {
        try {
            if(mPlayer != null){
                mPlayer!!.release()
            }


        } catch (e: Exception) {
            e.printStackTrace()
        }

        mPlayer = null
        //showing the play button
        imgViewPlay.visibility = View.VISIBLE
        imgViewPause.visibility = View.GONE

        chronometer.stop()
    }

    private fun startRecording() {
        try {
            mRecorder = MediaRecorder()
            mRecorder!!.setAudioSource(MediaRecorder.AudioSource.MIC)
            // mRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
            mRecorder!!.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)

            //Setting audio name and format to mp3
            val root = Environment.getExternalStorageDirectory()
            val file = File(root.absolutePath + "/CBC/Audios")
            if (!file.exists()) {
                file.mkdirs()
            }

            fileName =
                root.absolutePath + "/CBC/Audios/" + (System.currentTimeMillis().toString() + "_cbc_AudioRecordFile.mp3")
            Log.d("----filename", fileName)
            recordFile = fileName!!
            mRecorder!!.setOutputFile(fileName)
            //mRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            mRecorder!!.setAudioEncoder(MediaRecorder.AudioEncoder.AAC)

            try {
                mRecorder!!.prepare()
                mRecorder!!.start()
            } catch (e: IOException) {
                e.printStackTrace()
            }

            lastProgress = 0
            seekBar.progress = 0
            stopPlaying()
            // making the imageView a stop button starting the chronometer
            chronometer.base = SystemClock.elapsedRealtime()
            chronometer.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }


    private fun stopRecording() {
        try {
            mRecorder!!.stop()
            mRecorder!!.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        mRecorder = null
        //starting the chronometer
        chronometer.stop()
        chronometer.base = SystemClock.elapsedRealtime()

        //showing the play button
        Toast.makeText(this, "Recording saved successfully.", Toast.LENGTH_SHORT).show()

    }


    private fun startPlaying() {
        mPlayer = MediaPlayer()
        try {
            Log.d("----","play file mane: $fileName")
            mPlayer!!.setDataSource(fileName)
            mPlayer!!.prepare()
            mPlayer!!.start()

            recordFile = fileName!!
        } catch (e: IOException) {
            e.printStackTrace()
        }

        //making the imageView pause button
        imgViewPlay.visibility = View.GONE
        imgViewPause.visibility = View.VISIBLE
        seekBar.visibility = View.VISIBLE

        seekBar.progress = lastProgress
        mPlayer!!.seekTo(lastProgress)
        seekBar.max = mPlayer!!.duration
        seekBarUpdate()
        chronometer.start()

        mPlayer!!.setOnCompletionListener {
            imgViewPlay.visibility = View.VISIBLE
            imgViewPause.visibility = View.GONE

            isPlaying = false
            chronometer.stop()
            chronometer.base = SystemClock.elapsedRealtime()
            mPlayer!!.seekTo(0)

        }

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                if (mPlayer != null && fromUser) {
                    mPlayer!!.seekTo(progress)
                    chronometer.base = SystemClock.elapsedRealtime() - mPlayer!!.currentPosition
                    lastProgress = progress
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {}

            override fun onStopTrackingTouch(seekBar: SeekBar) {}
        })
    }

    private var runnable: Runnable = Runnable { seekBarUpdate() }

    private fun seekBarUpdate() {
        if (mPlayer != null) {
            val mCurrentPosition = mPlayer!!.currentPosition
            seekBar.progress = mCurrentPosition
            lastProgress = mCurrentPosition
        }
        mHandler.postDelayed(runnable, 100)
    }


}
