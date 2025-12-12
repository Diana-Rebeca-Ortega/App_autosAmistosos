package com.example.autos_amistosos;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;

public class DashboardDueñoActivity extends AppCompatActivity {

    private Button btnRegistrarEmpleado;
    private Button btnConsultarEmpleado;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_dueno);

        btnRegistrarEmpleado = findViewById(R.id.btn_registrar_empleado);
        btnConsultarEmpleado = findViewById(R.id.btn_consultar_empleado); // <-- ¡Inicializar!
        // Listener para el botón de Alta de Empleado
        btnRegistrarEmpleado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navegar a la Activity del formulario de registro
                Intent intent = new Intent(DashboardDueñoActivity.this, RegistroEmpleadoActivity.class);
                startActivity(intent);
            }
        });
        // Listener para Consultar (¡Nuevo!)
        btnConsultarEmpleado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DashboardDueñoActivity.this, ConsultaEmpleadoActivity.class);
                startActivity(intent);
            }
        });
    }
}