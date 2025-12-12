package com.example.autos_amistosos;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Bloque estándar para el manejo de insets (barras de sistema)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // 1. Encontrar el botón por su ID
        Button empezarButton = findViewById(R.id.get_started_button);

        // 2. Definir la acción al hacer clic
        empezarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear un Intent: Define que queremos ir de MainActivity a LoginActivity
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);

                // Iniciar la nueva actividad (pantalla)
                startActivity(intent);

                // Opcional: Cerrar la MainActivity si no quieres que el usuario pueda volver al splash screen
                // finish();
            }
        });
    }
}