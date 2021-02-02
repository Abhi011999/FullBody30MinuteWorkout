package com.example.full_body30minuteworkout.screens.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.full_body30minuteworkout.R
import com.example.full_body30minuteworkout.databinding.HomeFragmentBinding

class HomeFragment : Fragment() {

    // ViewModel
    private lateinit var viewModel: HomeViewModel

    // Binding
    private lateinit var binding: HomeFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Inflate view and obtain an instance of the binding class
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.home_fragment,
            container,
            false
        )

        // Getting ViewModel
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        // Set the ViewModel for DataBinding
        binding.homeViewModel = viewModel

        // Specify the fragment view as the lifecycle owner of the binding.
        // This is used so that the binding can observe LiveData updates
        binding.lifecycleOwner = viewLifecycleOwner

        // Observer for countdown finished event
        viewModel.eventCountdownFinish.observe(viewLifecycleOwner, { hasFinished ->
            if (hasFinished) countdownFinished()
        })

        // Observer for background color
        viewModel.bgColorId.observe(viewLifecycleOwner, {
            changeBackgroundColor(it)
        })

        viewModel.isTimerRunning.observe(viewLifecycleOwner, {
            onTimerStateChanged(it)
        })

        // Returning binded view
        return binding.root
    }

    private fun showToast(str: String) = Toast.makeText(activity, str, Toast.LENGTH_SHORT).show()

    private fun changeBackgroundColor(colorId: Int) {
        binding.homeBackground.setBackgroundColor(ContextCompat.getColor(requireContext(), colorId))
    }

    // Called when countdown is finished
    private fun countdownFinished() {
        showToast("Countdown Finished !")
        // TODO: Display the finished screen
    }

    // Called whenever the timer's state changes
    private fun onTimerStateChanged(isRunning: Boolean) {
        if (isRunning) {
            binding.floatingActionButton.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_stop_24))
            return
        }
        binding.floatingActionButton.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_double_arrow_24))
        changeBackgroundColor(R.color.white)
        showToast("Countdown Cancelled !")
    }
}