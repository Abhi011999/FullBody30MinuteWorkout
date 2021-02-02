package com.example.full_body30minuteworkout.screens.home

import android.os.CountDownTimer
import android.text.format.DateUtils
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.full_body30minuteworkout.BuildConfig
import com.example.full_body30minuteworkout.R

class HomeViewModel : ViewModel() {

    // Changing background color
    private val _bgColorId = MutableLiveData<Int>()
    val bgColorId: LiveData<Int>
        get() = _bgColorId

    // The current exercise
    private val _exercise = MutableLiveData<String>()
    val exercise: LiveData<String>
        get() = _exercise

    // Event which triggers the end of the countdown
    private val _eventCountdownFinish = MutableLiveData<Boolean>()
    val eventCountdownFinish: LiveData<Boolean>
        get() = _eventCountdownFinish

    // Is timer running ?
    private val _isTimerRunning = MutableLiveData<Boolean>()
    val isTimerRunning: LiveData<Boolean>
        get() = _isTimerRunning

    // The list of exercises the user can perform
    private val exercisesList: List<String> = listOf(
        "Squat Jumps",
        "Pull Ups",
//            "Press Ups",
//            "Leg Raises",
//            "Weight Lift",
//            "Dips",
//            "Russian Twist",
//            "Mountain Climbers",
//            "Farmer's Squat",
//            "Bicycle"
    )

    private lateinit var _exercisesList: MutableList<String>

    private enum class COUNTDOWN {
        UNIVERSAL,
        EXERCISE,
        SWITCHING,
        BREAK
    }

    companion object {
        // Time when the countdown ends
        private const val DONE = 0L

        // Countdown time interval
        private const val ONE_SECOND = 1000L

        // Total time for the universal countdown
        private const val UNIVERSAL_COUNTDOWN_TIME = 1800000L

        // Total time for the exercise countdown
        private const val EXERCISE_COUNTDOWN_TIME = 20000L

        // Total time for the exercise switching countdown
        private const val SWITCHING_COUNTDOWN_TIME = 10000L

        // Total time for the break countdown
        private const val BREAK_COUNTDOWN_TIME = 90000L

        // Total number of exercises
        private const val TOTAL_EXERCISES = 2

        // Total number of exercise cycles
        private const val TOTAL_EXERCISE_CYCLES = 4
    }

    private val _currentUniversalTime = MutableLiveData<Long>()
    private val currentUniversalTime: LiveData<Long>
        get() = _currentUniversalTime

    private val _currentExerciseTime = MutableLiveData<Long>()
    private val currentExerciseTime: LiveData<Long>
        get() = _currentExerciseTime

    private val _currentSwitchingTime = MutableLiveData<Long>()
    private val currentSwitchingTime: LiveData<Long>
        get() = _currentSwitchingTime

    private val _currentBreakTime = MutableLiveData<Long>()
    private val currentBreakTime: LiveData<Long>
        get() = _currentBreakTime

    val currentUniversalTimeString: LiveData<String> =
        Transformations.map(currentUniversalTime) { time ->
            DateUtils.formatElapsedTime(time)
        }

    var currentCountdownTimeString: LiveData<String> =
        Transformations.map(currentExerciseTime) { time ->
            DateUtils.formatElapsedTime(time)
        }

    private val universalTimer: CountDownTimer

    private val exerciseTimer: CountDownTimer

    private val switchingTimer: CountDownTimer

    private val breakTimer: CountDownTimer

    private var currentExerciseCycle: Int = 0

    private var currentSwitchingCycle: Int = 0

    // Assertion Checks
    init {
        if (BuildConfig.DEBUG && exercisesList.size != TOTAL_EXERCISES) {
            error("Total number of exercises didn't match with the size of exercises list")
        }
    }

    // Exercises Init
//    init {
//        initializeList()
//    }

    // Timers Init
    init {
        universalTimer = object : CountDownTimer(UNIVERSAL_COUNTDOWN_TIME, ONE_SECOND) {

            override fun onTick(millisUntilFinished: Long) {
                _currentUniversalTime.value = millisUntilFinished / ONE_SECOND
                // TODO: Implement blinking ":" in between
            }

            override fun onFinish() {
                _currentUniversalTime.value = DONE
                onCountdownFinish(COUNTDOWN.UNIVERSAL)
            }
        }

        exerciseTimer = object : CountDownTimer(EXERCISE_COUNTDOWN_TIME, ONE_SECOND) {

            override fun onTick(millisUntilFinished: Long) {
                _currentExerciseTime.value = millisUntilFinished / ONE_SECOND
            }

            override fun onFinish() {
                _currentExerciseTime.value = DONE
                onCountdownFinish(COUNTDOWN.EXERCISE)
            }
        }

        switchingTimer = object : CountDownTimer(SWITCHING_COUNTDOWN_TIME, ONE_SECOND) {

            override fun onTick(millisUntilFinished: Long) {
                _currentSwitchingTime.value = millisUntilFinished / ONE_SECOND
            }

            override fun onFinish() {
                _currentSwitchingTime.value = DONE
                onCountdownFinish(COUNTDOWN.SWITCHING)
            }
        }

        breakTimer = object : CountDownTimer(BREAK_COUNTDOWN_TIME, ONE_SECOND) {

            override fun onTick(millisUntilFinished: Long) {
                _currentBreakTime.value = millisUntilFinished / ONE_SECOND
            }

            override fun onFinish() {
                _currentBreakTime.value = DONE
                onCountdownFinish(COUNTDOWN.BREAK)
            }
        }
    }

    private fun initializeList() {
        _exercisesList = exercisesList.toMutableList()
    }

    private fun nextExercise() {
        if (_exercisesList.isNotEmpty())
            _exercise.value = _exercisesList.removeAt(0)
        else {
            currentExerciseCycle = currentExerciseCycle.inc()
            if (currentExerciseCycle >= TOTAL_EXERCISE_CYCLES) {
                // TODO: Cooldown timer
                onEventTimerFinish()
            } else {
                controlSwitchingTimer(false)
                _bgColorId.value = R.color.bg_orange
                controlBreakTimer(true)
                initializeList()
                nextExercise()
            }
        }
    }

    private fun onCountdownFinish(cdType: COUNTDOWN) {
        when (cdType) {
            COUNTDOWN.UNIVERSAL -> {
                onEventTimerFinish()
            }
            COUNTDOWN.EXERCISE -> {
                controlSwitchingTimer(true)
                _bgColorId.value = R.color.bg_yellow
                nextExercise()
            }
            COUNTDOWN.SWITCHING -> {
                currentSwitchingCycle = currentSwitchingCycle.inc()
                if (currentSwitchingCycle >= TOTAL_EXERCISES) {
                    currentSwitchingCycle = 0
                    return
                }
                _bgColorId.value = R.color.bg_green
                controlExerciseTimer(true)
            }
            COUNTDOWN.BREAK -> {
                controlExerciseTimer(true)
            }
        }
    }

    private fun currentCountdownText(countdownTimer: LiveData<Long>) {
        currentCountdownTimeString = Transformations.map(countdownTimer) { time ->
            DateUtils.formatElapsedTime(time)
        }
    }

    private fun controlUniversalTimer(controlValue: Boolean) {
        if (controlValue) {
            currentCountdownText(currentUniversalTime)
            universalTimer.start()
        } else
            universalTimer.cancel()
    }

    private fun controlExerciseTimer(controlValue: Boolean) {
        if (controlValue) {
            currentCountdownText(currentExerciseTime)
            exerciseTimer.start()
        } else
            exerciseTimer.cancel()
    }

    private fun controlSwitchingTimer(controlValue: Boolean) {
        if (controlValue) {
            currentCountdownText(currentSwitchingTime)
            switchingTimer.start()
        } else
            switchingTimer.cancel()
    }

    private fun controlBreakTimer(controlValue: Boolean) {
        if (controlValue) {
            // TODO: Show a "break" hint to user
            currentCountdownText(currentBreakTime)
            breakTimer.start()
        } else
            breakTimer.cancel()
    }

    fun onFabClicked() {
        if (_isTimerRunning.value == true) {
            onEventTimerStop()
            return
        }
        controlUniversalTimer(true)
        controlExerciseTimer(true)
        initializeList()
        nextExercise()
        _isTimerRunning.value = true
    }

    // Method for the countdown completed event
    private fun onEventTimerFinish() {
        cancelAllTimers()
        _isTimerRunning.value = false
        _eventCountdownFinish.value = true
    }

    // Method for the countdown stopped event
    private fun onEventTimerStop() {
        cancelAllTimers()
        _isTimerRunning.value = false
    }

    private fun resetValues() {
        _exercisesList.clear()
        _exercise.value = null
        _currentUniversalTime.value = DONE
        _currentExerciseTime.value = DONE
        _currentSwitchingTime.value = DONE
        _currentBreakTime.value = DONE
        currentExerciseCycle = 0
        currentSwitchingCycle = 0
    }

    private fun cancelAllTimers() {
        controlUniversalTimer(false)
        controlExerciseTimer(false)
        controlSwitchingTimer(false)
        controlBreakTimer(false)
        resetValues()
    }

    override fun onCleared() {
        super.onCleared()
        cancelAllTimers()
    }

}