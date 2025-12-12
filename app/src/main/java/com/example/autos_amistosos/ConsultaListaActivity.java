package com.example.autos_amistosos;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ConsultaListaActivity extends AppCompatActivity {

    // URL para el script de listado. Usa 10.0.2.2 para emulador o 192.168.1.87 para celular.
    private static final String LISTADO_URL = "http://192.168.1.87/PROYECTO/autos_api/listar_empleados.php";

    private EditText etBusqueda;
    private RecyclerView recyclerView;
    private VendedorAdapter adapter;
    private List<Vendedor> listaVendedores = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consulta_lista);

        // Inicializar vistas
        etBusqueda = findViewById(R.id.etBusqueda);
        recyclerView = findViewById(R.id.recyclerViewVendedores);

        // Configurar RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar el adaptador con una lista vacía
        adapter = new VendedorAdapter(listaVendedores);
        recyclerView.setAdapter(adapter);

        // Cargar los datos desde el servidor
        cargarVendedores();

        // Implementar el TextWatcher para el filtrado dinámico
        etBusqueda.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No se usa
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // El texto está cambiando, aplicar el filtro
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No se usa
            }
        });
    }

    private void cargarVendedores() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, LISTADO_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            int success = jsonObject.getInt("success");
                            String message = jsonObject.getString("message");

                            if (success == 1) {
                                // Lista obtenida exitosamente
                                JSONArray vendedoresArray = jsonObject.getJSONArray("vendedores");

                                listaVendedores.clear();
                                for (int i = 0; i < vendedoresArray.length(); i++) {
                                    JSONObject v = vendedoresArray.getJSONObject(i);

                                    // Crear objeto Vendedor y agregarlo a la lista
                                    Vendedor vendedor = new Vendedor(
                                            v.getInt("idVendedor"),
                                            v.getString("nombre"),
                                            v.getString("apellido1"),
                                            v.getString("apellido2"),
                                            v.getDouble("salario_base"),
                                            v.getDouble("porcentaje_comision")
                                    );
                                    listaVendedores.add(vendedor);
                                }

                                // Actualizar la lista en el adaptador y forzar el refresco
                                adapter.setListaVendedores(listaVendedores);

                            } else {
                                Toast.makeText(ConsultaListaActivity.this, "Error al cargar: " + message, Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ConsultaListaActivity.this, "Error de JSON al recibir lista: " + response, Toast.LENGTH_LONG).show();
                            Log.e("LISTADO_ERROR", "Respuesta no JSON: " + response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(ConsultaListaActivity.this, "Error de conexión al servidor: " + error.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("LISTADO_ERROR", "Error Volley: " + error.getMessage(), error);
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}