package me.juliomalaga.schneider_julio_descargas.divisas_okhttp

import android.os.Bundle
import android.text.TextWatcher
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.juliomalaga.schneider_julio_descargas.R
import me.juliomalaga.schneider_julio_descargas.databinding.ActivityDivisasOkHttpBinding
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.net.URL

class DivisasOkHttp : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityDivisasOkHttpBinding
    private var ratio = 0.0
    private var isUpdating = false
    private var dolaresWatcher: TextWatcher? = null
    private var eurosWatcher: TextWatcher? = null
    private var url = URL("https://juliomalaga.me/ficheros/ratio.txt")
    private var correcto = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDivisasOkHttpBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        correcto = comprobacionesIniciales(url)


        if (correcto) {
            mostrarRespuesta("Descargando ratio")
            okHTTPdownload(url)
        }else{
            mostrarRespuesta("URL no válida")
            // deshabilitar los editText
            binding.tvDolares.isEnabled = false
            binding.tvEuros.isEnabled = false
            binding.btnErase.isEnabled = false
        }
        binding.btnErase.setOnClickListener(this)

        dolaresWatcher = binding.tvDolares.addTextChangedListener {
            if (!isUpdating && it.toString()
                    .isNotEmpty() && it.toString() != "." && it.toString() != ","
            ) {
                isUpdating = true
                val euros = it.toString().toDouble() * ratio
                binding.tvEuros.setText(String.format("%.2f", euros))
                isUpdating = false
                binding.btnErase.setOnClickListener(this)
            }
        }

        eurosWatcher = binding.tvEuros.addTextChangedListener {
            if (!isUpdating && it.toString()
                    .isNotEmpty() && it.toString() != "." && it.toString() != ","
            ) {
                isUpdating = true
                val dolares = it.toString().toDouble() / ratio
                binding.tvDolares.setText(String.format("%.2f", dolares))
                isUpdating = false
                binding.btnErase.setOnClickListener(this)
            }
        }

    }

    private fun comprobacionesIniciales(url: URL): Boolean {
        // comprobamos que la url esté bien formada
        var correcto = false
        if (url.toString().isEmpty()) {
            mostrarRespuesta("URL vacía")
        }else if (url.protocol != "https" && url.protocol != "http") {
            mostrarRespuesta("URL no válida")
        }else if (url.host.isEmpty()) {
            mostrarRespuesta("URL no válida")
        }else if (url.path.isEmpty()) {
            mostrarRespuesta("URL no válida")
        }else if (url.query != null) {
            mostrarRespuesta("URL no válida")
        }else if (url.ref != null) {
            mostrarRespuesta("URL no válida")
        }else {
            correcto = true
        }
        return correcto
    }


    private fun okHTTPdownload(web: URL) {
        val client = OkHttpClient()
        val request = Request.Builder().url(web).build()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("Error: ", e.message!!)
                mostrarRespuesta ("Fallo: " + e.message)
            }
            @Throws(IOException::class)
            override fun onResponse(call: Call, response: Response) {
                try {
                    if (!response.isSuccessful){
                        mostrarRespuesta("Fallo: $response")
                    }else{
                        val body = response.body?.string()
                        ratio = body?.toDouble() ?: 0.0
                        if (ratio == 0.0) {
                            mostrarRespuesta("No se pudo obtener el ratio")
                        } else {
                            mostrarRespuesta("1 USD = $ratio EUR")
                        }
                    }
                } catch (e: Error) {
                    Log.e("Error when executing get request: ", e.localizedMessage!!)
                }
            }
        })

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btnErase -> {
                binding.tvDolares.removeTextChangedListener(dolaresWatcher)
                binding.tvEuros.removeTextChangedListener(eurosWatcher)
                binding.tvDolares.setText("")
                binding.tvEuros.setText("")
                binding.tvDolares.addTextChangedListener(dolaresWatcher)
                binding.tvEuros.addTextChangedListener(eurosWatcher)
            }

        }
    }

    private fun mostrarRespuesta(message: String) {
        lifecycleScope.launch(Dispatchers.Main) {
            binding.txtRatioActual.text = message
        }
    }

}