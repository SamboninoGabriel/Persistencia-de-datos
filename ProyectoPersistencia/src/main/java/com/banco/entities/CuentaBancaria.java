package com.banco.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "tb_cuenta_bancaria")
public class CuentaBancaria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cb_id", nullable = false)
    private Integer id;

    @Column(name = "cb_numero_cuenta", nullable = false, length = 20, unique = true)
    private String numeroCuenta;

    @Column(name = "cb_saldo", nullable = false, precision = 12, scale = 2)
    private BigDecimal saldo;

    @Column(name = "cb_fecha_apertura", nullable = false)
    private LocalDate fechaApertura;

    // Lado dueño de la relacion uno a uno: aqui vive la columna cliente_id.
    @OneToOne
    @JoinColumn(name = "cb_cliente_id", nullable = false, unique = true)
    private Cliente cliente;

    public CuentaBancaria() {
    }

    public CuentaBancaria(Cliente cliente, String numeroCuenta, BigDecimal saldo) {
        this.cliente = cliente;
        this.numeroCuenta = numeroCuenta;
        this.saldo = saldo;
        this.fechaApertura = LocalDate.now();
    }

    public Integer getId() {
        return id;
    }

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public BigDecimal getSaldo() {
        return saldo;
    }

    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

    public LocalDate getFechaApertura() {
        return fechaApertura;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    @Override
    public String toString() {
        return numeroCuenta + " | saldo $" + saldo;
    }
}
