package com.banco.entities;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDate;

/**
 * Campos y relaciones comunes a toda tarjeta (de crédito o débito).
 * No es una entidad por sí misma: @MappedSuperclass solo aporta sus
 * columnas a cada subclase, cada una con su propia tabla.
 */
@MappedSuperclass
public abstract class Tarjeta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    protected Integer id;

    @Column(name = "ultimos_cuatro", nullable = false, length = 4)
    protected String ultimosCuatro;

    @Column(name = "fecha_vencimiento", nullable = false)
    protected LocalDate fechaVencimiento;

    @Column(name = "estado", nullable = false, length = 15)
    protected String estado;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    protected Cliente cliente;

    protected Tarjeta() {
    }

    protected Tarjeta(Cliente cliente, String ultimosCuatro, LocalDate fechaVencimiento, String estado) {
        this.cliente = cliente;
        this.ultimosCuatro = ultimosCuatro;
        this.fechaVencimiento = fechaVencimiento;
        this.estado = estado;
    }

    public Integer getId() {
        return id;
    }

    public String getUltimosCuatro() {
        return ultimosCuatro;
    }

    public void setUltimosCuatro(String ultimosCuatro) {
        this.ultimosCuatro = ultimosCuatro;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
}
