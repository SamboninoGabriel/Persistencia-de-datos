package com.banco.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cl_id", nullable = false)
    private Integer id;

    @Column(name = "cl_nombre", nullable = false, length = 60)
    private String nombre;

    @Column(name = "cl_apellido", nullable = false, length = 60)
    private String apellido;

    @Column(name = "cl_cedula", nullable = false, length = 10, unique = true)
    private String cedula;

    @Column(name = "cl_celular", nullable = false, length = 10)
    private String celular;

    @Column(name = "cl_correo", nullable = false, length = 100)
    private String correo;

    // Lado "1" de la relacion uno a uno: el dueño (la FK) esta en CuentaBancaria.
    @OneToOne(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private CuentaBancaria cuenta;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TarjetaCredito> tarjetasCredito = new ArrayList<>();

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TarjetaDebito> tarjetasDebito = new ArrayList<>();

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Prestamo> prestamos = new ArrayList<>();

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Plan> planes = new ArrayList<>();

    public Cliente() {
    }

    public Cliente(String nombre, String apellido, String cedula, String celular, String correo) {
        this.nombre = nombre;
        this.apellido = apellido;
        this.cedula = cedula;
        this.celular = celular;
        this.correo = correo;
    }

    public Integer getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getNombreCompleto() {
        return nombre + " " + apellido;
    }

    public String getCedula() {
        return cedula;
    }

    public void setCedula(String cedula) {
        this.cedula = cedula;
    }

    public String getCelular() {
        return celular;
    }

    public void setCelular(String celular) {
        this.celular = celular;
    }

    /** Oculta los ultimos 4 digitos del celular, por ejemplo 0995574443 -> 099557****. */
    public String getCelularEnmascarado() {
        if (celular == null || celular.length() <= 4) {
            return "****";
        }
        return celular.substring(0, celular.length() - 4) + "****";
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public CuentaBancaria getCuenta() {
        return cuenta;
    }

    public void setCuenta(CuentaBancaria cuenta) {
        this.cuenta = cuenta;
    }

    public List<TarjetaCredito> getTarjetasCredito() {
        return tarjetasCredito;
    }

    public List<TarjetaDebito> getTarjetasDebito() {
        return tarjetasDebito;
    }

    public List<Prestamo> getPrestamos() {
        return prestamos;
    }

    public List<Plan> getPlanes() {
        return planes;
    }

    @Override
    public String toString() {
        return getNombreCompleto() + " (CI " + cedula + ")";
    }
}
