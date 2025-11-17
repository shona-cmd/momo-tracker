package com.momotracker.ai

import android.content.Context
import org.tensorflow.lite.Interpreter
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder

object CategoryClassifier {

    private const val MODEL_PATH = "models/gemini_nano_text_classification.tflite"
    private const val MAX_LEN = 128
    private const val CONFIDENCE_THRESHOLD = 0.7f

    // === UGANDA-SPECIFIC KEYWORDS (case-insensitive) ===
    private val KEYWORDS = mutableMapOf(
        "Airtime" to listOf("airtime", "top up", "bundle", "data", "voice"),
        "Transport" to listOf("boda", "taxi", "uber", "safeboda", "fuel", "stage"),
        "School Fees" to listOf("school", "tuition", "fees", "term", "pta", "nakasero", "kisubi"),
        "Food" to listOf("food", "lunch", "chicken", "chapati", "posho", "matooke", "restaurant"),
        "Rent" to listOf("rent", "house", "landlord", "apartment", "kira", "ntinda"),
        "Medical" to listOf("hospital", "clinic", "medicine", "pharmacy", "mulago", "nsambya"),
        "Savings" to listOf("save", "sacco", "welfare", "group", "contribution")).toMutableMap()

    KEYWORDS["Airtime"] = listOf("airtime", "top up", "bundle", "data", "voice")
    KEYWORDS["School Fees"] = listOf("school", "tuition", "fees", "term", "pta", "nakasero", "kisubi")

    private var interpreter: Interpreter? = null

    fun init(context: Context) {
        interpreter = Interpreter(loadModelFile(context), Interpreter.Options().apply {
            numThreads = 4
        })
    }

    fun classify(text: String): String {
        // 1. Keyword-based classification (fast and simple)
        KEYWORDS.forEach { (category, keywords) ->
            keywords.forEach { keyword ->
                if (text.contains(keyword, ignoreCase = true)) {
                    return category
                }
            }
        }

        // 2. TensorFlow Lite model classification (more accurate but slower)
        // TODO: Implement TFLite model classification

        // 3. Fallback category
        return "Other"
    }

    private fun loadModelFile(context: Context): MappedByteBuffer {
        val fileDescriptor = context.assets.openFd(MODEL_PATH)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }
}
