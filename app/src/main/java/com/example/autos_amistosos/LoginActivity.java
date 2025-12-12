package com.example.autos_amistosos;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

// Importaciones necesarias para Volley y JSON
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import java.util.HashMap;
import java.util.Map;
import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextUser;
    private EditText editTextPassword;
    private Button buttonIngresar;
    private static final String LOGIN_URL = "http://192.168.1.87/PROYECTO/autos_api/login.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // ... (Tu código onCreate existente, incluyendo la inicialización de vistas y el setOnClickListener) ...
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // Bloque estándar para el manejo de insets (barras de sistema)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_login), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 1. Inicialización de los componentes:
        editTextUser = findViewById(R.id.edit_text_user);
        editTextPassword = findViewById(R.id.edit_text_password);
        buttonIngresar = findViewById(R.id.button_ingresar);

        // 2. Establecer el Listener de clic en el botón "INGRESAR"
        buttonIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptLogin();
            }
        });
    }

    private void attemptLogin() {
        final String user = editTextUser.getText().toString().trim();
        final String password = editTextPassword.getText().toString().trim();

        if (user.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "Ingrese usuario y contraseña.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Crear una nueva solicitud de Volley (Tipo POST)
        StringRequest stringRequest = new StringRequest(Request.Method.POST, LOGIN_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Manejar la respuesta del servidor (el JSON de login.php)
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            int success = jsonObject.getInt("success");
                            String message = jsonObject.getString("message");

                            if (success == 1) {
                                // Éxito en el Login
                                String rol = jsonObject.getString("rol");
                                Toast.makeText(LoginActivity.this, "¡Login Exitoso! Rol: " + rol, Toast.LENGTH_LONG).show();
                            //  'dueño' usan este panel:
                                if (rol.equals("dueno") || rol.equals("administrador")) {
                                    Intent intent = new Intent(LoginActivity.this, DashboardDueñoActivity.class);
                                    startActivity(intent);
                                    finish(); // Cierra el login para que el usuario no pueda volver con el botón 'Atrás'
                                } else {
                                    // Ejemplo para otros roles (vendedor)
                                    Toast.makeText(LoginActivity.this, "Acceso no implementado para este rol: " + rol, Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                // Login Fallido (Credenciales incorrectas)
                                Toast.makeText(LoginActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(LoginActivity.this, "Error de JSON al recibir datos.", Toast.LENGTH_LONG).show();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Manejar errores de red o servidor
                        Toast.makeText(LoginActivity.this, "Error de conexión: " + error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            // Este método define qué datos se enviarán por POST al PHP
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user", user);      // El nombre 'user' debe coincidir con $_POST['user'] en login.php
                params.put("password", password); // El nombre 'password' debe coincidir con $_POST['password'] en login.php
                return params;
            }
        };

        // Agregar la solicitud a la cola de Volley para que se ejecute
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}