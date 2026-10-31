package com.banco.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "tb_tarjeta_debito")
public class TarjetaDebito extends Tarjeta {

    @Column(name = "numero_cuenta", nullable = false, length = 20)
    private String numeroCuenta;

    @Column(name = "saldo_disponible", nullable = false, precision = 12, scale = 2)
    private BigDecimal saldoDisponible;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 10)
    private TipoTarjetaDebito tipo;

    public TarjetaDebito() {
    }

    public TarjetaDebito(Cliente cliente, String ultimosCuatro, LocalDate fechaVencimiento,
                         String numeroCuenta, BigDecimal saldoDisponible, TipoTarjetaDebito tipo, String estado) {
        super(cliente, ultimosCuatro, fechaVencimiento, estado);
        this.numeroCuenta = numeroCuenta;
        this.saldoDisponible = saldoDisponible;
        this.tipo = tipo;
    }

    public String getNumeroCuenta() {
        return numeroCuenta;
    }

    public void setNumeroCuenta(String numeroCuenta) {
        this.numeroCuenta = numeroCuenta;
    }

    public BigDecimal getSaldoDisponible() {
        return saldoDisponible;
    }

    public void setSaldoDisponible(BigDecimal saldoDisponible) {
        this.saldoDisponible = saldoDisponible;
    }

    public TipoTarjetaDebito getTipo() {
        return tipo;
    }

    public void setTipo(TipoTarjetaDebito tipo) {
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        return "ID " + id + " | " + cliente.getNombreCompleto() + " | " + tipo + " | **** **** **** " + ultimosCuatro
                + " | cuenta " + numeroCuenta + " | saldo $" + saldoDisponible + " | " + estado;
    }
}
