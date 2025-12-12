package com.example.autos_amistosos;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import java.util.Locale;

public class ActualizarEmpleadoActivity extends AppCompatActivity {


    private static final String CONSULTA_URL = "http://192.168.1.87/PROYECTO/autos_api/consulta_empleado.php";
    private static final String ACTUALIZAR_URL = "http://192.168.1.87/PROYECTO/autos_api/actualizar_empleado.php";

    private EditText etIdVendedor;
    private Button btnBuscarActualizar;

    private LinearLayout layoutDatosActualizar;
    private TextView tvTituloDatos;
    private TextView tvMensaje;

    // Campos editables del vendedor
    private EditText etNombreActualizar, etApellido1Actualizar, etApellido2Actualizar,
            etSalarioBaseActualizar, etPorcentajeComisionActualizar;
    private Button btnGuardarCambios;

    // Almacenamos el ID para usarlo en la actualización
    private int idVendedorSeleccionado = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actualizar_empleado);

        // Inicializar vistas de Búsqueda
        etIdVendedor = findViewById(R.id.etIdVendedor);
        btnBuscarActualizar = findViewById(R.id.btnBuscarActualizar);
        tvMensaje = findViewById(R.id.tvMensaje);

        // Inicializar contenedor de datos (inicialmente oculto en el XML)
        layoutDatosActualizar = findViewById(R.id.layoutDatosActualizar);
        tvTituloDatos = findViewById(R.id.tvTituloDatos);

        // Inicializar campos editables
        etNombreActualizar = findViewById(R.id.etNombreActualizar);
        etApellido1Actualizar = findViewById(R.id.etApellido1Actualizar);
        etApellido2Actualizar = findViewById(R.id.etApellido2Actualizar);
        etSalarioBaseActualizar = findViewById(R.id.etSalarioBaseActualizar);
        etPorcentajeComisionActualizar = findViewById(R.id.etPorcentajeComisionActualizar);
        btnGuardarCambios = findViewById(R.id.btnGuardarCambios);

        // Listeners
        btnBuscarActualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscarVendedor();
            }
        });

        btnGuardarCambios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                guardarCambios();
            }
        });

        // Ocultar al inicio
        ocultarDatos();
    }

    // Método para ocultar el formulario de datos
    private void ocultarDatos() {
        layoutDatosActualizar.setVisibility(View.GONE);
        tvTituloDatos.setVisibility(View.GONE);
        tvMensaje.setText("");
        idVendedorSeleccionado = -1;
    }

    // --- Lógica de Búsqueda (Similar a ConsultaEmpleadoActivity) ---
    private void buscarVendedor() {
        ocultarDatos(); // Limpiar y ocultar datos anteriores
        final String id = etIdVendedor.getText().toString().trim();

        if (id.isEmpty()) {
            Toast.makeText(this, "Ingrese el ID para buscar.", Toast.LENGTH_SHORT).show();
            return;
        }

        tvMensaje.setText("Buscando vendedor...");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, CONSULTA_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            int success = jsonObject.getInt("success");
                            String message = jsonObject.getString("message");
                            tvMensaje.setText(message);

                            if (success == 1) {
                                // Vendedor encontrado, cargar datos al formulario
                                JSONObject vendedor = jsonObject.getJSONObject("vendedor");
                                cargarDatosEnFormulario(vendedor);
                            } else {
                                // Vendedor no encontrado
                                Toast.makeText(ActualizarEmpleadoActivity.this, message, Toast.LENGTH_LONG).show();
                                ocultarDatos();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            tvMensaje.setText("Error de JSON al recibir datos.");
                            Log.e("CONSULTA_ERROR", "Respuesta no JSON: " + response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        tvMensaje.setText("Error de conexión al servidor.");
                        Log.e("CONSULTA_ERROR", "Error Volley: " + error.getMessage(), error);
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                // La clave debe coincidir con la que espera consulta_empleado.php
                params.put("id_vendedor", id);
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    // --- Cargar los datos encontrados al Formulario ---
    private void cargarDatosEnFormulario(JSONObject vendedor) throws JSONException {
        idVendedorSeleccionado = vendedor.getInt("idVendedor");

        // Mapear los datos del JSON a los campos de texto
        etNombreActualizar.setText(vendedor.getString("nombre"));
        etApellido1Actualizar.setText(vendedor.getString("apellido1"));

        // Manejar Apellido2 que puede ser NULL
        String apellido2 = vendedor.getString("apellido2");
        if (vendedor.isNull("apellido2") || apellido2.equals("null")) {
            etApellido2Actualizar.setText("");
        } else {
            etApellido2Actualizar.setText(apellido2);
        }

        // Formatear decimales
        etSalarioBaseActualizar.setText(String.format(Locale.US, "%.2f", vendedor.getDouble("salario_base")));
        etPorcentajeComisionActualizar.setText(String.format(Locale.US, "%.4f", vendedor.getDouble("porcentaje_comision")));

        // Mostrar los datos y actualizar el título
        tvTituloDatos.setText("Datos del Vendedor (ID: " + idVendedorSeleccionado + ")");
        layoutDatosActualizar.setVisibility(View.VISIBLE);
        tvTituloDatos.setVisibility(View.VISIBLE);
    }

    // --- Lógica de Guardar Cambios (Similar a RegistroEmpleadoActivity) ---
    private void guardarCambios() {
        if (idVendedorSeleccionado == -1) {
            Toast.makeText(this, "Busque un vendedor primero.", Toast.LENGTH_SHORT).show();
            return;
        }

        // 1. Obtener y validar los valores (el ID ya está en idVendedorSeleccionado)
        final String nombre = etNombreActualizar.getText().toString().trim();
        final String apellido1 = etApellido1Actualizar.getText().toString().trim();
        final String apellido2 = etApellido2Actualizar.getText().toString().trim();
        final String salarioBase = etSalarioBaseActualizar.getText().toString().trim();
        final String porcentajeComision = etPorcentajeComisionActualizar.getText().toString().trim();

        if (nombre.isEmpty() || apellido1.isEmpty() || salarioBase.isEmpty() || porcentajeComision.isEmpty()) {
            Toast.makeText(this, "Completa Nombre, Apellido1, Salario y Comisión.", Toast.LENGTH_SHORT).show();
            return;
        }

        tvMensaje.setText("Guardando cambios...");

        // Crear la solicitud Volley POST
        StringRequest stringRequest = new StringRequest(Request.Method.POST, ACTUALIZAR_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            int success = jsonObject.getInt("success");
                            String message = jsonObject.getString("message");

                            Toast.makeText(ActualizarEmpleadoActivity.this, message, Toast.LENGTH_LONG).show();
                            tvMensaje.setText(message);

                            if (success == 1) {
                                // Opcional: limpiar la pantalla o volver a cargar el vendedor
                                ocultarDatos();
                                etIdVendedor.setText(""); // Limpia el ID para una nueva búsqueda
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            tvMensaje.setText("Error de JSON al recibir datos de actualización.");
                            Log.e("ACTUALIZAR_ERROR", "Respuesta no JSON: " + response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        tvMensaje.setText("Error de conexión al guardar cambios.");
                        Log.e("ACTUALIZAR_ERROR", "Error Volley: " + error.getMessage(), error);
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() {
                // 2. Crear el mapa de parámetros (claves que espera actualizar_empleado.php)
                Map<String, String> params = new HashMap<>();

                params.put("id_vendedor", String.valueOf(idVendedorSeleccionado));
                params.put("nombre", nombre);
                params.put("apellido1", apellido1);
                params.put("apellido2", apellido2);
                params.put("salario_base", salarioBase);
                params.put("porcentaje_comision", porcentajeComision);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}