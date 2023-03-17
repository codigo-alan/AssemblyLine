package com.example.assemblyline

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.assemblyline.databinding.ActivityMainBinding
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //worker 3
        val deferredWorker3 = CoroutineScope(Dispatchers.IO).async { worker3() }

        val deferredWorker12 = CoroutineScope(Dispatchers.Main).async {
            //Worker 1
            var worker1IntResultsList = listOf<Int>()
            repeat(5) {
                worker1IntResultsList += worker1()
                delay(3000)
            }
            binding.resultTv1.text = worker1IntResultsList.toString()

            //Worker 2
            val deferredWorker2 = CoroutineScope(Dispatchers.IO).async { worker2(worker1IntResultsList) }
            val resultWorker2 = deferredWorker2.await()
            binding.resultTv2.text = resultWorker2.toString()
            return@async resultWorker2
        }

        CoroutineScope(Dispatchers.Main).launch {
            //Worker 4
            val resultWorker23 = listOf(deferredWorker12, deferredWorker3).awaitAll()

            binding.resultTv3.text = resultWorker23.last().toString()

            var packagesDifference = if (resultWorker23.first() == resultWorker23.last()) {
                0
            } else {
                resultWorker23.first() - resultWorker23.last()
            }

            if (packagesDifference == 0) {
                binding.resultTv4.text = "Correct packaging done!"
            } else if (packagesDifference < 0) {
                binding.resultTv4.text = "Remain $packagesDifference packages"
            } else {
                binding.resultTv4.text = "Left over $packagesDifference packages"
            }
        }

    }

    private fun worker1(): Int {
        return (3..21).random()
    }
    private suspend fun worker2(units: List<Int>): Int {
        val packsList = mutableListOf<Int>()

        units.forEach {
            packsList += (it / 4)
        }

        packsList.forEach {
            repeat(it){ delay(1000) }
        }
        return packsList.sum()
    }
    private suspend fun worker3(): Int {
        val elapsedTime = (10000..20000 step 1000).shuffled().first().toLong()
        delay(elapsedTime)
        return listOf(2, 3, 4).shuffled().first()
    }

}