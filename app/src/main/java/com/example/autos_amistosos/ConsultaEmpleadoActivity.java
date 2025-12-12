package com.example.autos_amistosos;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ConsultaEmpleadoActivity extends AppCompatActivity {

    // URL para el script de consulta. Usa 10.0.2.2 para emulador o 192.168.1.87 para celular.
    private static final String CONSULTA_URL = "http://192.168.1.87/PROYECTO/autos_api/consulta_empleado.php";

    private EditText etIdVendedor;
    private Button btnBuscar;
    private TextView tvResultado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulta_empleado);

        // Inicializar vistas
        etIdVendedor = findViewById(R.id.etIdVendedor);
        btnBuscar = findViewById(R.id.btnBuscar);
        tvResultado = findViewById(R.id.tvResultado);

        // Limpiar el resultado al inicio
        tvResultado.setText("");

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Iniciar la consulta
                consultarVendedor();
            }
        });
    }

    private void consultarVendedor() {
        // 1. Obtener el ID
        final String idVendedor = etIdVendedor.getText().toString().trim();

        // Validación
        if (idVendedor.isEmpty()) {
            Toast.makeText(this, "Por favor, ingrese el ID del vendedor.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Limpiar el resultado mientras se espera
        tvResultado.setText("Buscando vendedor...");

        // Crear la solicitud Volley
        StringRequest stringRequest = new StringRequest(Request.Method.POST, CONSULTA_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            int success = jsonObject.getInt("success");
                            String message = jsonObject.getString("message");

                            if (success == 1) {
                                // Vendedor encontrado
                                JSONObject vendedor = jsonObject.getJSONObject("vendedor");

                                // Formatear el resultado para mostrar en el TextView
                                String resultado = "✅ Vendedor Encontrado:\n\n" +
                                        "ID: " + vendedor.getString("idVendedor") + "\n" +
                                        "Nombre: " + vendedor.getString("nombre") + "\n" +
                                        "Apellidos: " + vendedor.getString("apellido1") + " " + vendedor.getString("apellido2") + "\n" +
                                        "Salario Base: $" + vendedor.getString("salario_base") + "\n" +
                                        "Comisión: " + (Double.parseDouble(vendedor.getString("porcentaje_comision")) * 100) + " %";

                                tvResultado.setText(resultado);
                                Toast.makeText(ConsultaEmpleadoActivity.this, message, Toast.LENGTH_SHORT).show();

                            } else {
                                // Vendedor no encontrado o error de PHP
                                tvResultado.setText("❌ " + message);
                                Toast.makeText(ConsultaEmpleadoActivity.this, message, Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            tvResultado.setText("Error de JSON: Respuesta no válida del servidor.");
                            Toast.makeText(ConsultaEmpleadoActivity.this, "Error de JSON. Revise la respuesta: " + response, Toast.LENGTH_LONG).show();
                            Log.e("CONSULTA_ERROR", "Respuesta no JSON: " + response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Error de conexión
                        tvResultado.setText("Error de conexión al servidor.");
                        Toast.makeText(ConsultaEmpleadoActivity.this, "Error de conexión: " + error.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("CONSULTA_ERROR", "Error Volley: " + error.getMessage(), error);
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() {
                // Crear el mapa de parámetros (clave-valor) para el POST
                Map<String, String> params = new HashMap<>();
                // La clave "id_vendedor" DEBE coincidir con la que espera consulta_empleado.php
                params.put("id_vendedor", idVendedor);
                return params;
            }
        };

        // Añadir la solicitud a la cola de Volley
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}