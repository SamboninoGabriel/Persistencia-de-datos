package com.banco.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "tb_plan")
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @Column(name = "nombre", nullable = false, length = 40)
    private String nombre;

    @Column(name = "datos_gb", nullable = false)
    private Integer datosGB;

    @Column(name = "minutos_incluidos", nullable = false)
    private Integer minutosIncluidos;

    @Column(name = "costo_mensual", nullable = false, precision = 8, scale = 2)
    private BigDecimal costoMensual;

    @Column(name = "estado", nullable = false, length = 15)
    private String estado;

    public Plan() {
    }

    public Plan(Cliente cliente, String nombre, Integer datosGB, Integer minutosIncluidos,
                BigDecimal costoMensual, String estado) {
        this.cliente = cliente;
        this.nombre = nombre;
        this.datosGB = datosGB;
        this.minutosIncluidos = minutosIncluidos;
        this.costoMensual = costoMensual;
        this.estado = estado;
    }

    public Integer getId() {
        return id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public String getNombre() {
        return nombre;
    }

    public Integer getDatosGB() {
        return datosGB;
    }

    public Integer getMinutosIncluidos() {
        return minutosIncluidos;
    }

    public BigDecimal getCostoMensual() {
        return costoMensual;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "ID " + id + " | " + cliente.getNombreCompleto() + " | " + nombre
                + " | " + datosGB + "GB | " + minutosIncluidos + " min | $" + costoMensual + " | " + estado;
    }
}
