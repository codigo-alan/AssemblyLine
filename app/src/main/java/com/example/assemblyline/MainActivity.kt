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

        //Worker 1
        repeat(5) {
            CoroutineScope(Dispatchers.IO).launch {
                worker1Async()
                delay(3000)
            }
        }

        //worker 3
        CoroutineScope(Dispatchers.IO).launch {
            worker1Async()
            delay(3000)
        }

        //worker 4 execute 2 and 3 workers works
        CoroutineScope(Dispatchers.Main).launch {
            val worker23 = listOf(worker1Async(), worker3Async())
            val receivedListWorks23 = worker23.awaitAll() //work of 2 and 3 finished

            var packagesDifference = if (receivedListWorks23.first() == receivedListWorks23.last()) {
                0
            }else{
                receivedListWorks23.first() - receivedListWorks23.last()
            }

            if (packagesDifference == 0) {
                binding.resultTv4.text = "Correct packaging done!"
            }else if(packagesDifference < 0){
                binding.resultTv4.text = "Remain $packagesDifference packages"
            }else{
                binding.resultTv4.text = "Left over $packagesDifference packages"
            }

        }

    }

    private suspend fun worker1Async(): Deferred<Int> {
        return CoroutineScope(Dispatchers.IO).async { worker1() }
    }

    private fun worker1(): Int {
        val units = (3..21).random()
        return units
    }

    private fun worker3Async() = CoroutineScope(Dispatchers.IO).async { worker3() }
    private suspend fun worker3(): Int {
        val elapsedTime = (10000..20000 step 1000).shuffled().first().toLong()
        delay(elapsedTime)
        val positionQty = listOf<Int>(2, 3, 4).shuffled().first()
        return positionQty
    }
}