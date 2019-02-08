package com.prv.minutnik.minutnik2

import android.media.MediaPlayer
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import kotlinx.android.synthetic.main.activity_main.*
import com.prv.minutnik.minutnik2.util.PrefUtil

class MainActivity : AppCompatActivity() {

    enum class TimerState{
        Stopped, Paused, Running
    }

    private lateinit var  timer: CountDownTimer
    private lateinit var mp: MediaPlayer
    private var timerLengthSeconds: Long = 0
    private var timerState = TimerState.Stopped
    private var secondsRemaining = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        mp = MediaPlayer.create(this, R.raw.horn)
        mp.isLooping = false

        buttonStart.setOnClickListener{ v ->
            startTimer()
            timerState = TimerState.Running
            updateButtons()
        }

        buttonPause.setOnClickListener{ v ->
            timer.cancel()
            timerState = TimerState.Paused
            updateButtons()
        }

        buttonStop.setOnClickListener { v ->
            timer.cancel()
            onTimerFinished()
        }

        buttonPlus1.setOnClickListener { v ->
            secondsRemaining += 10 * 60
            updateCountdownUI()
        }

        buttonMinus1.setOnClickListener { v ->
            secondsRemaining -= 10 * 60
            updateCountdownUI()
        }

        buttonPlus2.setOnClickListener { v ->
            secondsRemaining += 1 * 60
            updateCountdownUI()
        }

        buttonMinus2.setOnClickListener { v ->
            secondsRemaining -= 1 * 60
            updateCountdownUI()
        }

        buttonPlus3.setOnClickListener { v ->
            secondsRemaining += 10
            updateCountdownUI()
        }

        buttonMinus3.setOnClickListener { v ->
            secondsRemaining -= 10
            updateCountdownUI()
        }

        buttonPlus4.setOnClickListener { v ->
            secondsRemaining += 1
            updateCountdownUI()
        }

        buttonMinus4.setOnClickListener { v ->
            secondsRemaining -= 1
            updateCountdownUI()
        }
    }

    override fun onResume() {
        super.onResume()

        initTimer()
    }

    override fun onPause() {
        super.onPause()

        if (timerState == TimerState.Running){
            timer.cancel()
            //TODO: start background timer and show notification
        }
        else if (timerState == TimerState.Paused){
            //TODO: show notification
        }

        PrefUtil.setPreviousTimerLengthSeconds(timerLengthSeconds, this)
        PrefUtil.setSecondsRemaining(secondsRemaining, this)
        PrefUtil.setTimerState(timerState, this)
    }

    private fun initTimer(){
        timerState = PrefUtil.getTimerState(this)

        //we don't want to change the length of the timer which is already running
        //if the length was changed in settings while it was backgrounded
        if (timerState == TimerState.Stopped)
            setNewTimerLength()
        else
            setPreviousTimerLength()

        secondsRemaining = if (timerState == TimerState.Running || timerState == TimerState.Paused)
            PrefUtil.getSecondsRemaining(this)
        else
            timerLengthSeconds


        //resume where we left off
        if (timerState == TimerState.Running)
            startTimer()

        updateButtons()
        updateCountdownUI()
    }

    private fun onTimerFinished(){
        timerState = TimerState.Stopped

        mp.start()

        setNewTimerLength()

        PrefUtil.setSecondsRemaining(timerLengthSeconds, this)
        secondsRemaining = timerLengthSeconds

        updateButtons()
        updateCountdownUI()
    }

    private fun startTimer(){
        timerState = TimerState.Running

        timer = object : CountDownTimer(secondsRemaining * 1000, 1000) {
            override fun onFinish() = onTimerFinished()

            override fun onTick(millisUntilFinished: Long) {
                secondsRemaining = millisUntilFinished / 1000
                updateCountdownUI()
            }
        }.start()
    }

    private fun setNewTimerLength(){
        val lengthInMinutes = PrefUtil.getTimerLength(this)
        timerLengthSeconds = 10
    }

    private fun setPreviousTimerLength(){
        timerLengthSeconds = PrefUtil.getPreviousTimerLengthSeconds(this)
    }

    private fun updateCountdownUI(){
        val minutesUntilFinished = secondsRemaining / 60
        val secondsInMinuteUntilFinished = secondsRemaining - minutesUntilFinished * 60
        val secondsStr = secondsInMinuteUntilFinished.toString()
        if (secondsRemaining <= 0)
            secondsRemaining = 1
        numberView1.text =  "${minutesUntilFinished/10%10}"
        numberView2.text =  "${minutesUntilFinished%10}"
        numberView3.text = "${secondsInMinuteUntilFinished/10%10}"
        numberView4.text = "${secondsInMinuteUntilFinished%10}"
    }

    private fun updateButtons(){
        when (timerState) {
            TimerState.Running ->{
                buttonStart.isEnabled = false
                buttonPause.isEnabled = true
                buttonStop.isEnabled = true
                buttonPlus1.isEnabled = false
                buttonPlus2.isEnabled = false
                buttonPlus3.isEnabled = false
                buttonPlus4.isEnabled = false
                buttonMinus1.isEnabled = false
                buttonMinus2.isEnabled = false
                buttonMinus3.isEnabled = false
                buttonMinus4.isEnabled = false
            }
            TimerState.Stopped -> {
                buttonStart.isEnabled = true
                buttonPause.isEnabled = false
                buttonStop.isEnabled = false
                buttonPlus1.isEnabled = true
                buttonPlus2.isEnabled = true
                buttonPlus3.isEnabled = true
                buttonPlus4.isEnabled = true
                buttonMinus1.isEnabled = true
                buttonMinus2.isEnabled = true
                buttonMinus3.isEnabled = true
                buttonMinus4.isEnabled = true
            }
            TimerState.Paused -> {
                buttonStart.isEnabled = true
                buttonPause.isEnabled = false
                buttonStop.isEnabled = true
                buttonStart.isEnabled = true
                buttonPause.isEnabled = false
                buttonStop.isEnabled = false
                buttonPlus1.isEnabled = true
                buttonPlus2.isEnabled = true
                buttonPlus3.isEnabled = true
                buttonPlus4.isEnabled = true
                buttonMinus1.isEnabled = true
                buttonMinus2.isEnabled = true
                buttonMinus3.isEnabled = true
                buttonMinus4.isEnabled = true
            }
        }
    }

}
