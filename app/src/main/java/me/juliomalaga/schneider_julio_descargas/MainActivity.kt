package me.juliomalaga.schneider_julio_descargas

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import me.juliomalaga.schneider_julio_descargas.databinding.ActivityMainBinding
import me.juliomalaga.schneider_julio_descargas.descarga_asincrona.DescargaAsincrona
import me.juliomalaga.schneider_julio_descargas.divisas_okhttp.DivisasOkHttp

class MainActivity : AppCompatActivity(), View.OnClickListener {

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.btnDescargaAsincrona.setOnClickListener(this)
        binding.btnDivisas.setOnClickListener(this)

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnDescargaAsincrona -> {
                Intent(this, DescargaAsincrona::class.java).also {
                    startActivity(it)
                }
            }

            R.id.btnDivisas -> {
               Intent(this, DivisasOkHttp::class.java).also {
                   startActivity(it)
               }
            }
        }
    }
}




