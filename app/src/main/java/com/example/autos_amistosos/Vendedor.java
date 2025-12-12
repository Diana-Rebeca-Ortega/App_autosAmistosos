package com.example.autos_amistosos;

// Clase modelo para representar un objeto Vendedor
public class Vendedor {
    private int idVendedor;
    private String nombre;
    private String apellido1;
    private String apellido2;
    private double salarioBase;
    private double porcentajeComision;

    // Constructor
    public Vendedor(int idVendedor, String nombre, String apellido1, String apellido2, double salarioBase, double porcentajeComision) {
        this.idVendedor = idVendedor;
        this.nombre = nombre;
        this.apellido1 = apellido1;
        this.apellido2 = apellido2;
        this.salarioBase = salarioBase;
        this.porcentajeComision = porcentajeComision;
    }

    // Getters
    public int getIdVendedor() { return idVendedor; }
    public String getNombre() { return nombre; }
    public String getApellido1() { return apellido1; }
    public String getApellido2() { return apellido2; }
    public double getSalarioBase() { return salarioBase; }
    public double getPorcentajeComision() { return porcentajeComision; }
}