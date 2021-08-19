package com.tbadhit.mybackgroundthread

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.lifecycleScope
import com.tbadhit.mybackgroundthread.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.Executors

// Update "activity_main.xml"
// Mencoba untuk menjalankan suatu proses yang lama di UI thread dan melihat bagaimana efeknya (terjadi freeze) :
// add code "MainActivtiy" (1)

// Menggunakan Executor untuk membuat thread baru dan Handler untuk update hasil dari proses ke komponen UI berupa TextView :
// update code "MainActivity" (2)

// Mencoba me-refactor kode yang sudah dibuat dengan Executor + Handler menjadi menggunakan Coroutine (simple) :
// add library "build.gradle (module)" (1)
// update code "MainActivity" (3)
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // (1)
        val btnStart = binding.btnStart
        val tvStatus = binding.tvStatus
        //-----

        // (2)
        /*
        (newSingleThreadExecutor) = yang artinya hanya satu thread yang Anda buat. Sehingga ketika
        Anda klik tombol berkali-kali hanya satu proses yang dijalankan dan proses lainnya akan
        dieksekusi setelahnya proses sebelumnya selesai
         */
        val executor = Executors.newSingleThreadExecutor()
        /*
        (getMainLooper) = karena ingin proses yang di dalam Handler dijalankan di main/ui thread.
        Pada kasus lain, jika ingin Handler berjalan dengan thread yang sama dengan sebelumnya
        bisa menggunakan myLooper
         */
        val handler = Handler(Looper.getMainLooper())
        //-----

        // (1)
        btnStart.setOnClickListener {

            // (3) with kotlin corountine
            /*
            (lifecycleScope) = merupakan scope yang sudah disediakan library lifecycle-runtime-ktx
            untuk menjalankan coroutine pada Activity yang sudah aware dengan lifecycle. Dengan begitu
            instance coroutine akan otomatis dihapus ketika aplikasi dalam keadaan onDestroy sehingga
            aplikasi tidak mengalami memory leak (kebocoran memori)
             */
            lifecycleScope.launch(Dispatchers.Default) {
                //simulate process in background thread
                for (i in 0..10) {
                    delay(500)
                    val percentage = i * 10
                    withContext(Dispatchers.Main) {
                        //update ui in main thread
                        if (percentage == 100) {
                            tvStatus.setText(R.string.task_completed)
                        } else {
                            tvStatus.text = String.format(getString(R.string.compressing), percentage)
                        }
                    }
                }
            }

            // (2) contoh langkah 2 (executor & handler)
//            executor.execute {
//                try {
//                    // simulate process compressing
//                    for (i in 0..10) {
//                        Thread.sleep(500)
//                        val percentage = i * 10
//                        handler.post {
//                            //update ui in main thread
//                            if (percentage == 100) {
//                                tvStatus.setText(R.string.task_completed)
//                            } else {
//                                tvStatus.text =
//                                    String.format(getString(R.string.compressing), percentage)
//                            }
//                        }
//                    }
//                } catch (e: InterruptedException) {
//                    e.printStackTrace()
//                }
//            }
            //-----

            // (1) contoh langkah satu (freeze)
//            try {
//                // simulate process compressing
//                for (i in 0..10) {
//                    Thread.sleep(500)
//                    val percentage = i * 10
//                    if (percentage == 100) {
//                        tvStatus.setText(R.string.task_completed)
//                    } else {
//                        tvStatus.text = String.format(getString(R.string.compressing), percentage)
//                    }
//                }
//            } catch (e: InterruptedException){
//                e.printStackTrace()
//            }
        }
        //-----
    }
}