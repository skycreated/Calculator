package com.example.calculator



import android.os.Bundle
import android.view.HapticFeedbackConstants
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.calculator.databinding.ActivityMainBinding
import java.util.Locale


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var firstNumber: Double = 0.0;
    private var currentOp: String = "";
    private var isNewInput: Boolean = true;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()


    }

    private fun setupClickListeners(){
        val numButtons = listOf(
            binding.btn0, binding.btn2, binding.btn1, binding.btn3,
            binding.btn4, binding.btn5, binding.btn6,
            binding.btn7, binding.btn8, binding.btn9,
            binding.btn0
        )

        numButtons.forEach { btn ->
            btn.setOnClickListener {
                animateButton(it)
                onNumberClicked(btn.text.toString())
            }
        }

        binding.apply {
            btnAdd.setOnClickListener { animateButton(it); prepareOperator("+") }
            btnSub.setOnClickListener { animateButton(it); prepareOperator("-") }
            btnMultiply.setOnClickListener { animateButton(it); prepareOperator("x") }
            btnDivide.setOnClickListener { animateButton(it); prepareOperator("/") }

        }

        binding.apply {
            btnEquals.setOnClickListener {
                animateButton(it)
                performCalculation()
                currentOp = ""
            }
            btnAC.setOnClickListener {
                animateButton(it)
                resetCalculator()
            }
            btnDot.setOnClickListener {
                animateButton(it)
                appendDot()
            }
            btnPercent.setOnClickListener {
                animateButton(it)
                val value = binding.tvDisplay.text.toString().toDoubleOrNull() ?:0.0
                updateUI(value / 100, false)
            }
            btnPlusMin.setOnClickListener {
                animateButton(it)
                val value = binding.tvDisplay.text.toString().toDoubleOrNull() ?:0.0
                updateUI(value - (value*2), false)
            }
        }
    }

    private fun appendDot(){
        val currentText = binding.tvDisplay.text.toString()
        val digitCount = currentText.filter { it.isDigit() }.length
        if(!currentText.contains(".")&&digitCount<7){
            binding.tvDisplay.append(".")
            isNewInput = false
        }
    }

    private fun resetCalculator(){
        binding.tvDisplay.text = "0"
        firstNumber = 0.0
        currentOp = ""
        isNewInput = true
    }

    private fun prepareOperator(op: String){
        if(currentOp.isNotEmpty() && !isNewInput){
            performCalculation()
        }
        firstNumber = binding.tvDisplay.text.toString().toDoubleOrNull() ?:0.0
        currentOp = op
        isNewInput = true
    }

    private fun performCalculation(){
        val secondNumString = binding.tvDisplay.text.toString()
        if(currentOp.isEmpty() || secondNumString.isEmpty())
            return
        val secondNumber = secondNumString.toDoubleOrNull() ?: 0.0
        var result = 0.0
        var hasError = false

        when (currentOp){
            "+" -> result = firstNumber + secondNumber
            "-" -> result = firstNumber - secondNumber
            "x" -> result = firstNumber * secondNumber
            "/" -> {
                if(secondNumber == 0.0) hasError = true
                else result = firstNumber / secondNumber
            }

        }
        updateUI(result, hasError)
    }

    private fun updateUI(result: Double, hasError: Boolean){
        if(hasError){
            binding.tvDisplay.text = "Error"
            firstNumber = 0.0
        }else{
            val formatted = if (result % 1 == 0.0){
                result.toLong().toString()
            }else{
                String.format(Locale.US, "%.4f",result).toDouble().toString()
            }
            binding.tvDisplay.text = formatted
            firstNumber = result
        }
        isNewInput = true
    }

    private fun onNumberClicked(number: String){
        val currentText = binding.tvDisplay.text.toString()

        if (isNewInput || currentText == "Error"){
            binding.tvDisplay.text = number
            isNewInput = false
        }else{
            val digitCount = currentText.filter { it.isDigit() }.length
            if(digitCount<7){
                binding.tvDisplay.append(number)
            }else{
                binding.tvDisplay.performHapticFeedback(HapticFeedbackConstants.REJECT)
            }
        }

    }

    private fun animateButton(view: View) {
        view.animate()
            .scaleX(0.9f)
            .scaleY(0.9f)
            .setDuration(50)
            .withEndAction {
                view.animate().scaleX(1.0f).scaleY(1.0f).setDuration(50).start()

            }
            .start()
    }
}