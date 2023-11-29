package com.example.cet3013_a2

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.WindowManager
import android.view.animation.PathInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnStart
import androidx.core.content.ContextCompat
import androidx.core.view.isGone
import com.example.cet3013_a2.databinding.ActivitySplashScreenBinding

@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {
    private lateinit var binding: ActivitySplashScreenBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Binding
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.statusBarColor = ContextCompat.getColor(this, R.color.blue3)

        // Get shared preferences for the first time
        val sharedPref = getSharedPreferences("SplashScreen", MODE_PRIVATE)

        // Check if it is the first time
        if (sharedPref.getBoolean("cb_do_not_show_again", false)) {
            val intent = Intent(this@SplashScreen, MainActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            // Create a new CountDownTimer
            val countdownTimer = object : CountDownTimer(11000,  1000) {
                override fun onTick(millisUntilFinished: Long) {
                    binding.tvCountdown.text = (millisUntilFinished / 1000).toString()
                    if (millisUntilFinished / 1000 == 10L) {
                        startAnimation()
                    }
                }
                override fun onFinish() {
                    val intent = Intent(this@SplashScreen, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }

            countdownTimer.start()
        }

        // Set onCheckedChangeListener for the checkbox
        binding.cbDoNotShowAgain.setOnCheckedChangeListener { _, isChecked ->
            // Save the state of the checkbox if it is checked
            with(sharedPref.edit()) {
                putBoolean("cb_do_not_show_again", isChecked)
                apply()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        startAnimation()
    }

    private fun startAnimation() {
        val squareXLeft = binding.iv2.left.toFloat()
        val squareXRight = binding.iv2.right.toFloat()
        val squareYTop = binding.iv2.top.toFloat()
//        val squareYBottom = binding.iv2.bottom.toFloat()
        val squareSize = binding.iv2.width.toFloat()
        val duration = 500L

        // ================== Animation set 1 ==================
        // Set reveal animation
        val reveal1: ObjectAnimator = ObjectAnimator
            .ofFloat(binding.iv2, "x", squareXLeft, squareXRight)
            .setDuration(duration)
        val exit1: ObjectAnimator = ObjectAnimator
//            .ofFloat(binding.iv2, "x", squareXLeft - squareSize, squareXLeft)
            .ofFloat(binding.iv1, "alpha", 1f, 0f)
            .setDuration(duration)
        reveal1.interpolator = PathInterpolator(1f, 0f, 0f, 1f)
        exit1.interpolator = PathInterpolator(1f, 0f, 0f, 1f)

        // ================== Animation set 2 ==================
        val reveal2 = ObjectAnimator
            .ofFloat(binding.iv2, "y", squareYTop, squareYTop - squareSize)
            .setDuration(duration)
        val exit2 = ObjectAnimator
//            .ofFloat(binding.iv2, "y", squareYBottom, squareYTop)
            .ofFloat(binding.iv1, "alpha", 1f, 0f)
            .setDuration(duration)

        reveal2.interpolator = PathInterpolator(1f, 0f, 0f, 1f)
        exit2.interpolator = PathInterpolator(1f, 0f, 0f, 1f)

        reveal2.doOnStart {
            binding.iv1.alpha = 1f
            binding.iv1.setImageResource(R.drawable.splash_a2)
            binding.iv2.x = squareXLeft
            binding.iv2.y = squareYTop
        }

        // ================== Animation set 3 ==================
        val reveal3 = ObjectAnimator
            .ofFloat(binding.iv2, "x", squareXLeft, squareXLeft - squareSize)
            .setDuration(duration)
        val exit3 = ObjectAnimator
            .ofFloat(binding.iv1, "alpha", 1f, 0f)
//            .ofFloat(binding.iv2, "x", squareXRight, squareXLeft)
            .setDuration(duration)

        reveal3.interpolator = PathInterpolator(1f, 0f, 0f, 1f)
        exit3.interpolator = PathInterpolator(1f, 0f, 0f, 1f)

        reveal3.doOnStart {
            binding.iv1.alpha = 1f
            binding.iv1.setImageResource(R.drawable.splash_a3)
            binding.iv2.x = squareXLeft
            binding.iv2.y = squareYTop
        }

        // ================== Animation set 4 ==================
        val reveal4 = ObjectAnimator
            .ofFloat(binding.iv2, "y", squareYTop, squareYTop - squareSize)
            .setDuration(duration)
        val exit4 = ObjectAnimator
            .ofFloat(binding.iv1, "alpha", 1f, 0f)
//            .ofFloat(binding.iv2, "x", squareXRight, squareXLeft)
            .setDuration(duration)

        reveal4.interpolator = PathInterpolator(1f, 0f, 0f, 1f)
        exit4.interpolator = PathInterpolator(1f, 0f, 0f, 1f)

        reveal4.doOnStart {
            binding.iv1.alpha = 1f
            binding.iv1.setImageResource(R.drawable.splash_a4)
            binding.iv2.x = squareXLeft
            binding.iv2.y = squareYTop
        }

        // ================== Animation set 5 ================== Logo Reveal
        val reveal5a = ObjectAnimator
            .ofFloat(binding.iv1, "alpha", 0f, 1f)
            .setDuration(duration * 2)
        reveal5a.interpolator = PathInterpolator(1f, 0f, 0f, 1f)
        reveal5a.doOnStart {
            binding.iv2.isGone = true
            binding.iv1.setImageResource(R.drawable.logo_full)
        }
        val reveal5b = ObjectAnimator
            .ofFloat(binding.iv1, "scaleX", 0f, 1f)
            .setDuration(duration * 2)
        val reveal5c = ObjectAnimator
            .ofFloat(binding.iv1, "scaleY", 0f, 1f)
            .setDuration(duration * 2)
        reveal5a.interpolator = PathInterpolator(1f, 0f, 0f, 1f)
        reveal5b.interpolator = PathInterpolator(1f, 0f, 0f, 1f)
        reveal5c.interpolator = PathInterpolator(1f, 0f, 0f, 1f)

        val reveal5 = AnimatorSet()
        reveal5.play(reveal5a).with(reveal5b).with(reveal5c)


        val animatorSet = AnimatorSet()
        animatorSet.playSequentially(
            reveal1,
            exit1,
            reveal2,
            exit2,
            reveal3,
            exit3,
            reveal4,
            exit4,
            reveal5
        )
        animatorSet.start()
    }
}