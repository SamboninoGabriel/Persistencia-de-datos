package com.banco.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "tb_tarjeta_credito")
public class TarjetaCredito extends Tarjeta {

    @Column(name = "cupo", nullable = false, precision = 12, scale = 2)
    private BigDecimal cupo;

    public TarjetaCredito() {
    }

    public TarjetaCredito(Cliente cliente, String ultimosCuatro, LocalDate fechaVencimiento,
                          BigDecimal cupo, String estado) {
        super(cliente, ultimosCuatro, fechaVencimiento, estado);
        this.cupo = cupo;
    }

    public BigDecimal getCupo() {
        return cupo;
    }

    public void setCupo(BigDecimal cupo) {
        this.cupo = cupo;
    }

    @Override
    public String toString() {
        return "ID " + id + " | " + cliente.getNombre() + " | **** **** **** " + ultimosCuatro
                + " | vence " + fechaVencimiento + " | cupo $" + cupo + " | " + estado;
    }
}
