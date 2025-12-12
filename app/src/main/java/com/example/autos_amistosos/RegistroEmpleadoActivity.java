package com.example.autos_amistosos;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class RegistroEmpleadoActivity extends AppCompatActivity {

    // Cambia esta URL si vuelves a usar tu teléfono (192.168.1.87)
    // Usamos 10.0.2.2 si estás en el emulador.
    private static final String ALTA_URL = "http://192.168.1.87/PROYECTO/autos_api/alta_empleado.php";

    private EditText etNombre, etApellido1, etApellido2, etSalarioBase, etPorcentajeComision;
    private Button btnRegistrar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro_empleado);

        // Inicializar vistas (asegúrate que los IDs coincidan con el XML)
        etNombre = findViewById(R.id.etNombre);
        etApellido1 = findViewById(R.id.etApellido1);
        etApellido2 = findViewById(R.id.etApellido2);
        etSalarioBase = findViewById(R.id.etSalarioBase);
        etPorcentajeComision = findViewById(R.id.etPorcentajeComision);
        btnRegistrar = findViewById(R.id.btnRegistrar);

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Llamar al método de registro cuando se presiona el botón
                registrarVendedor();
            }
        });
    }

    private void registrarVendedor() {
        // 1. Obtener los valores de los campos
        final String nombre = etNombre.getText().toString().trim();
        final String apellido1 = etApellido1.getText().toString().trim();
        final String apellido2 = etApellido2.getText().toString().trim();
        final String salarioBase = etSalarioBase.getText().toString().trim();
        final String porcentajeComision = etPorcentajeComision.getText().toString().trim();

        // 2. Validación mínima de campos requeridos (Nombre, Apellido1, Salario y Comisión)
        if (nombre.isEmpty() || apellido1.isEmpty() || salarioBase.isEmpty() || porcentajeComision.isEmpty()) {
            Toast.makeText(this, "Por favor, completa los campos requeridos.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear la solicitud Volley
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ALTA_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            // Intentar parsear la respuesta JSON
                            JSONObject jsonObject = new JSONObject(response);
                            int success = jsonObject.getInt("success");
                            String message = jsonObject.getString("message");

                            Toast.makeText(RegistroEmpleadoActivity.this, message, Toast.LENGTH_LONG).show();

                            if (success == 1) {
                                // Si el registro fue exitoso, limpiar los campos o cerrar la actividad
                                finish(); // Opcional: regresa al Dashboard
                            }

                        } catch (JSONException e) {
                            // Error de JSON si el servidor no devuelve el formato esperado
                            e.printStackTrace();
                            Toast.makeText(RegistroEmpleadoActivity.this, "Error de JSON al recibir datos: " + response, Toast.LENGTH_LONG).show();
                            Log.e("ALTA_ERROR", "Respuesta no JSON: " + response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Error de conexión (DNS, NoConnectionError, etc.)
                        Toast.makeText(RegistroEmpleadoActivity.this, "Error de conexión: " + error.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("ALTA_ERROR", "Error Volley: " + error.getMessage(), error);
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() {
                // 3. Crear el mapa de parámetros (pares clave-valor) para el POST
                Map<String, String> params = new HashMap<>();

                // Las claves deben coincidir exactamente con las que espera alta_empleado.php
                params.put("nombre", nombre);
                params.put("apellido1", apellido1);
                params.put("apellido2", apellido2);
                params.put("salario_base", salarioBase);
                params.put("porcentaje_comision", porcentajeComision);

                return params;
            }
        };

        // 4. Añadir la solicitud a la cola de Volley
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}