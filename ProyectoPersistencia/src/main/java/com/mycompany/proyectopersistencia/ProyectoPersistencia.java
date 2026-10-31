package com.mycompany.proyectopersistencia;

import com.banco.entities.Cliente;
import com.banco.entities.CuentaBancaria;
import com.banco.entities.Plan;
import com.banco.entities.Prestamo;
import com.banco.entities.TarjetaCredito;
import com.banco.entities.TarjetaDebito;
import com.banco.entities.TipoTarjetaDebito;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class ProyectoPersistencia {

    private static final Scanner IN = new Scanner(System.in);
    private static final Random AZAR = new Random();
    private static final String REGEX_CORREO = "^[\\w.+-]+@[\\w-]+\\.[a-zA-Z]{2,}$";
    private static EntityManager manager;

    /** Catalogo fijo de planes: nombre, datos en GB, minutos incluidos, costo mensual. */
    private static final Object[][] CATALOGO_PLANES = {
        {"Plan Basico", 2, 100, new BigDecimal("9.99")},
        {"Plan Plus", 5, 250, new BigDecimal("15.99")},
        {"Plan Premium", 10, 500, new BigDecimal("24.99")},
        {"Plan Ilimitado", 20, 1000, new BigDecimal("34.99")},
    };

    public static void main(String[] args) {
        try (EntityManagerFactory emf = Persistence.createEntityManagerFactory("com.mycompany_ProyectoPersistencia_jar_1.0-SNAPSHOTPU")) {
            manager = emf.createEntityManager();
            String opcion;
            do {
                System.out.println("\n--- Sistema financiero ---");
                System.out.println("1. Clientes");
                System.out.println("2. Tarjetas de credito");
                System.out.println("3. Tarjetas de debito");
                System.out.println("4. Prestamos");
                System.out.println("5. Planes");
                System.out.println("0. Salir");
                opcion = leer("Opcion: ");
                try {
                    switch (opcion) {
                        case "1" -> menuClientes();
                        case "2" -> menuTarjetaCredito();
                        case "3" -> menuTarjetaDebito();
                        case "4" -> menuPrestamo();
                        case "5" -> menuPlanes();
                        case "0" -> System.out.println("Hasta pronto.");
                        default -> System.out.println("Opcion no valida.");
                    }
                } catch (DateTimeParseException e) {
                    System.out.println("Fecha no valida. Use el formato AAAA-MM-DD.");
                } catch (NumberFormatException e) {
                    System.out.println("Numero no valido.");
                } catch (RuntimeException e) {
                    System.out.println("Error: " + e.getMessage());
                }
            } while (!opcion.equals("0"));
            manager.close();
        }
    }

    private static void enTransaccion(Runnable accion) {
        manager.getTransaction().begin();
        try {
            accion.run();
            manager.getTransaction().commit();
        } catch (RuntimeException e) {
            if (manager.getTransaction().isActive()) {
                manager.getTransaction().rollback();
            }
            throw e;
        }
    }

    /** Lista los clientes y devuelve el que el usuario elija por ID, o null si cancela o no hay ninguno. */
    private static Cliente seleccionarCliente() {
        List<Cliente> clientes = manager
                .createQuery("select c from Cliente c order by c.id", Cliente.class)
                .getResultList();
        if (clientes.isEmpty()) {
            System.out.println("No hay clientes registrados. Cree uno primero en el menu Clientes.");
            return null;
        }
        System.out.println("Clientes disponibles:");
        for (Cliente c : clientes) {
            System.out.println("  " + c.getId() + " - " + c.getNombreCompleto() + " (CI " + c.getCedula() + ")");
        }
        Integer id = Integer.valueOf(leer("ID del cliente: "));
        Cliente cliente = manager.find(Cliente.class, id);
        if (cliente == null) {
            System.out.println("No existe un cliente con ese ID.");
        }
        return cliente;
    }

    // ------------------------------------------------------------------
    // Clientes y cuentas
    // ------------------------------------------------------------------

    private static void menuClientes() {
        String opcion;
        do {
            System.out.println("\n-- Clientes --");
            System.out.println("1. Crear cliente y cuenta");
            System.out.println("2. Listar");
            System.out.println("3. Actualizar celular/correo");
            System.out.println("4. Eliminar");
            System.out.println("0. Volver");
            opcion = leer("Opcion: ");
            switch (opcion) {
                case "1" -> crearClienteYCuenta();
                case "2" -> listarClientes();
                case "3" -> actualizarCliente();
                case "4" -> eliminarCliente();
                case "0" -> { }
                default -> System.out.println("Opcion no valida.");
            }
        } while (!opcion.equals("0"));
    }

    private static void crearClienteYCuenta() {
        String nombre = leer("Nombre: ");
        if (nombre.isEmpty()) {
            System.out.println("El nombre es obligatorio.");
            return;
        }
        String apellido = leer("Apellido: ");
        if (apellido.isEmpty()) {
            System.out.println("El apellido es obligatorio.");
            return;
        }
        if (nombre.toLowerCase().endsWith(apellido.toLowerCase())) {
            nombre = nombre.substring(0, nombre.length() - apellido.length()).trim();
        }
        String cedula = leer("Cedula (10 digitos): ");
        if (!cedula.matches("\\d{10}")) {
            System.out.println("La cedula debe tener exactamente 10 digitos.");
            return;
        }
        String celular = leer("Celular (10 digitos): ");
        if (!celular.matches("\\d{10}")) {
            System.out.println("El celular debe tener exactamente 10 digitos.");
            return;
        }
        String correo = leer("Correo: ");
        if (!correo.matches(REGEX_CORREO)) {
            System.out.println("Correo no valido. Use un correo con formato valido, por ejemplo nombre@gmail.com o nombre@yahoo.com.");
            return;
        }
        BigDecimal saldoInicial;
        try {
            saldoInicial = new BigDecimal(leer("Saldo inicial: "));
        } catch (NumberFormatException ex) {
            System.out.println("El saldo inicial no es un numero valido.");
            return;
        }
        if (saldoInicial.signum() < 0) {
            System.out.println("El saldo inicial no puede ser negativo.");
            return;
        }
        Cliente cliente = new Cliente(nombre, apellido, cedula, celular, correo);
        String numeroCuenta = "PICH" + String.format("%08d", AZAR.nextInt(100_000_000));
        CuentaBancaria cuenta = new CuentaBancaria(cliente, numeroCuenta, saldoInicial);
        cliente.setCuenta(cuenta);
        // persist() en el cliente basta: el cascade = ALL de Cliente guarda tambien la cuenta.
        enTransaccion(() -> manager.persist(cliente));
        System.out.println("Cliente creado con ID " + cliente.getId() + " y cuenta " + numeroCuenta);
    }

    private static void listarClientes() {
        List<Cliente> clientes = manager
                .createQuery("select c from Cliente c order by c.id", Cliente.class)
                .getResultList();
        if (clientes.isEmpty()) {
            System.out.println("No hay clientes registrados.");
            return;
        }
        for (Cliente c : clientes) {
            String cuentaTxt = c.getCuenta() == null ? "sin cuenta"
                    : c.getCuenta().getNumeroCuenta() + " / $" + c.getCuenta().getSaldo();
            System.out.println("ID " + c.getId() + " | " + c.getNombreCompleto() + " | CI " + c.getCedula()
                    + " | " + c.getCelularEnmascarado() + " | " + c.getCorreo() + " | " + cuentaTxt);
        }
    }

    private static void actualizarCliente() {
        Cliente cliente = seleccionarCliente();
        if (cliente == null) {
            return;
        }
        String celular = leer("Nuevo celular (10 digitos): ");
        if (!celular.matches("\\d{10}")) {
            System.out.println("El celular debe tener exactamente 10 digitos.");
            return;
        }
        String correo = leer("Nuevo correo: ");
        if (!correo.matches(REGEX_CORREO)) {
            System.out.println("Correo no valido. Use un correo con formato valido, por ejemplo nombre@gmail.com o nombre@yahoo.com.");
            return;
        }
        enTransaccion(() -> {
            cliente.setCelular(celular);
            cliente.setCorreo(correo);
        });
        System.out.println("Cliente actualizado.");
    }

    private static void eliminarCliente() {
        Cliente cliente = seleccionarCliente();
        if (cliente == null) {
            return;
        }
        if (!leer("Esto tambien elimina su cuenta, tarjetas, prestamos y planes. Escriba SI para confirmar: ")
                .equalsIgnoreCase("SI")) {
            System.out.println("Operacion cancelada.");
            return;
        }
        enTransaccion(() -> manager.remove(cliente));
        System.out.println("Cliente eliminado.");
    }

    // ------------------------------------------------------------------
    // Tarjetas de credito
    // ------------------------------------------------------------------

    private static void menuTarjetaCredito() {
        String opcion;
        do {
            System.out.println("\n-- Tarjetas de credito --");
            System.out.println("1. Crear");
            System.out.println("2. Listar");
            System.out.println("3. Actualizar cupo y estado");
            System.out.println("4. Eliminar");
            System.out.println("5. Listar tarjetas de un cliente (JPQL)");
            System.out.println("0. Volver");
            opcion = leer("Opcion: ");
            switch (opcion) {
                case "1" -> crearTarjetaCredito();
                case "2" -> listarTarjetasCredito();
                case "3" -> actualizarTarjetaCredito();
                case "4" -> eliminarTarjetaCredito();
                case "5" -> listarTarjetasCreditoPorCliente();
                case "0" -> { }
                default -> System.out.println("Opcion no valida.");
            }
        } while (!opcion.equals("0"));
    }

    private static void crearTarjetaCredito() {
        Cliente cliente = seleccionarCliente();
        if (cliente == null) {
            return;
        }
        String ultimos = leer("Ultimos 4 digitos: ");
        if (!ultimos.matches("\\d{4}")) {
            System.out.println("Deben ser exactamente 4 digitos.");
            return;
        }
        LocalDate vencimiento = LocalDate.parse(leer("Vencimiento (AAAA-MM-DD): "));
        BigDecimal cupo = new BigDecimal(leer("Cupo: "));
        if (cupo.signum() < 0) {
            System.out.println("El cupo no puede ser negativo.");
            return;
        }
        TarjetaCredito tarjeta = new TarjetaCredito(cliente, ultimos, vencimiento, cupo, "ACTIVA");
        enTransaccion(() -> manager.persist(tarjeta));
        System.out.println("Tarjeta de credito creada con ID " + tarjeta.getId());
    }

    private static void listarTarjetasCredito() {
        List<TarjetaCredito> tarjetas = manager
                .createQuery("select t from TarjetaCredito t order by t.id", TarjetaCredito.class)
                .getResultList();
        if (tarjetas.isEmpty()) {
            System.out.println("No hay tarjetas de credito registradas.");
            return;
        }
        tarjetas.forEach(System.out::println);
    }

    private static void listarTarjetasCreditoPorCliente() {
        String email = leer("Email del cliente: ");
        List<TarjetaCredito> tarjetas = manager
                .createQuery("select t from TarjetaCredito t where t.cliente.correo = :email order by t.id",
                        TarjetaCredito.class)
                .setParameter("email", email)
                .getResultList();
        if (tarjetas.isEmpty()) {
            System.out.println("Ese cliente no tiene tarjetas de credito.");
            return;
        }
        tarjetas.forEach(System.out::println);
    }

    private static void actualizarTarjetaCredito() {
        Integer id = Integer.valueOf(leer("ID de la tarjeta: "));
        TarjetaCredito tarjeta = manager.find(TarjetaCredito.class, id);
        if (tarjeta == null) {
            System.out.println("No existe una tarjeta de credito con ese ID.");
            return;
        }
        System.out.println("Actual: " + tarjeta);
        BigDecimal cupo = new BigDecimal(leer("Nuevo cupo: "));
        if (cupo.signum() < 0) {
            System.out.println("El cupo no puede ser negativo.");
            return;
        }
        String estado = leerEstadoTarjeta();
        if (estado == null) {
            return;
        }
        enTransaccion(() -> {
            tarjeta.setCupo(cupo);
            tarjeta.setEstado(estado);
        });
        System.out.println("Tarjeta de credito actualizada.");
    }

    private static void eliminarTarjetaCredito() {
        Integer id = Integer.valueOf(leer("ID de la tarjeta: "));
        TarjetaCredito tarjeta = manager.find(TarjetaCredito.class, id);
        if (tarjeta == null) {
            System.out.println("No existe una tarjeta de credito con ese ID.");
            return;
        }
        enTransaccion(() -> manager.remove(tarjeta));
        System.out.println("Tarjeta de credito eliminada.");
    }

    // ------------------------------------------------------------------
    // Tarjetas de debito
    // ------------------------------------------------------------------

    private static void menuTarjetaDebito() {
        String opcion;
        do {
            System.out.println("\n-- Tarjetas de debito --");
            System.out.println("1. Crear");
            System.out.println("2. Listar");
            System.out.println("3. Actualizar saldo y estado");
            System.out.println("4. Eliminar");
            System.out.println("0. Volver");
            opcion = leer("Opcion: ");
            switch (opcion) {
                case "1" -> crearTarjetaDebito();
                case "2" -> listarTarjetasDebito();
                case "3" -> actualizarTarjetaDebito();
                case "4" -> eliminarTarjetaDebito();
                case "0" -> { }
                default -> System.out.println("Opcion no valida.");
            }
        } while (!opcion.equals("0"));
    }

    private static void crearTarjetaDebito() {
        Cliente cliente = seleccionarCliente();
        if (cliente == null) {
            return;
        }
        String ultimos = leer("Ultimos 4 digitos: ");
        if (!ultimos.matches("\\d{4}")) {
            System.out.println("Deben ser exactamente 4 digitos.");
            return;
        }
        LocalDate vencimiento = LocalDate.parse(leer("Vencimiento (AAAA-MM-DD): "));
        String cuenta = leer("Numero de cuenta asociada: ");
        if (cuenta.isEmpty()) {
            System.out.println("El numero de cuenta es obligatorio.");
            return;
        }
        BigDecimal saldo = new BigDecimal(leer("Saldo disponible: "));
        if (saldo.signum() < 0) {
            System.out.println("El saldo no puede ser negativo.");
            return;
        }
        String tipoTexto = leer("Tipo (FISICA o VIRTUAL): ").toUpperCase();
        TipoTarjetaDebito tipo;
        try {
            tipo = TipoTarjetaDebito.valueOf(tipoTexto);
        } catch (IllegalArgumentException ex) {
            System.out.println("Tipo no valido.");
            return;
        }
        TarjetaDebito tarjeta = new TarjetaDebito(cliente, ultimos, vencimiento, cuenta, saldo, tipo, "ACTIVA");
        enTransaccion(() -> manager.persist(tarjeta));
        System.out.println("Tarjeta de debito creada con ID " + tarjeta.getId());
    }

    private static void listarTarjetasDebito() {
        List<TarjetaDebito> tarjetas = manager
                .createQuery("select t from TarjetaDebito t order by t.id", TarjetaDebito.class)
                .getResultList();
        if (tarjetas.isEmpty()) {
            System.out.println("No hay tarjetas de debito registradas.");
            return;
        }
        tarjetas.forEach(System.out::println);
    }

    private static void actualizarTarjetaDebito() {
        Integer id = Integer.valueOf(leer("ID de la tarjeta: "));
        TarjetaDebito tarjeta = manager.find(TarjetaDebito.class, id);
        if (tarjeta == null) {
            System.out.println("No existe una tarjeta de debito con ese ID.");
            return;
        }
        System.out.println("Actual: " + tarjeta);
        BigDecimal saldo = new BigDecimal(leer("Nuevo saldo: "));
        if (saldo.signum() < 0) {
            System.out.println("El saldo no puede ser negativo.");
            return;
        }
        String estado = leerEstadoTarjeta();
        if (estado == null) {
            return;
        }
        enTransaccion(() -> {
            tarjeta.setSaldoDisponible(saldo);
            tarjeta.setEstado(estado);
        });
        System.out.println("Tarjeta de debito actualizada.");
    }

    private static void eliminarTarjetaDebito() {
        Integer id = Integer.valueOf(leer("ID de la tarjeta: "));
        TarjetaDebito tarjeta = manager.find(TarjetaDebito.class, id);
        if (tarjeta == null) {
            System.out.println("No existe una tarjeta de debito con ese ID.");
            return;
        }
        enTransaccion(() -> manager.remove(tarjeta));
        System.out.println("Tarjeta de debito eliminada.");
    }

    private static String leerEstadoTarjeta() {
        String estado = leer("Nuevo estado (ACTIVA, BLOQUEADA, CANCELADA): ").toUpperCase();
        if (!estado.matches("ACTIVA|BLOQUEADA|CANCELADA")) {
            System.out.println("Estado no valido.");
            return null;
        }
        return estado;
    }

    // ------------------------------------------------------------------
    // Prestamos
    // ------------------------------------------------------------------

    private static void menuPrestamo() {
        String opcion;
        do {
            System.out.println("\n-- Prestamos --");
            System.out.println("1. Crear");
            System.out.println("2. Listar");
            System.out.println("3. Actualizar estado");
            System.out.println("4. Eliminar");
            System.out.println("0. Volver");
            opcion = leer("Opcion: ");
            switch (opcion) {
                case "1" -> crearPrestamo();
                case "2" -> listarPrestamos();
                case "3" -> actualizarPrestamo();
                case "4" -> eliminarPrestamo();
                case "0" -> { }
                default -> System.out.println("Opcion no valida.");
            }
        } while (!opcion.equals("0"));
    }

    private static void crearPrestamo() {
        Cliente cliente = seleccionarCliente();
        if (cliente == null) {
            return;
        }
        BigDecimal monto = new BigDecimal(leer("Monto del prestamo: "));
        if (monto.signum() <= 0) {
            System.out.println("El monto debe ser mayor que cero.");
            return;
        }
        Integer plazo = Integer.valueOf(leer("Plazo en meses: "));
        if (plazo <= 0) {
            System.out.println("El plazo debe ser mayor que cero.");
            return;
        }
        BigDecimal tasa = new BigDecimal(leer("Tasa de interes anual (%): "));
        if (tasa.signum() < 0) {
            System.out.println("La tasa no puede ser negativa.");
            return;
        }
        Prestamo prestamo = new Prestamo(cliente, monto, plazo, tasa, "ACTIVO");
        enTransaccion(() -> manager.persist(prestamo));
        System.out.println("Prestamo creado con ID " + prestamo.getId());
    }

    private static void listarPrestamos() {
        List<Prestamo> prestamos = manager
                .createQuery("select p from Prestamo p order by p.id", Prestamo.class)
                .getResultList();
        if (prestamos.isEmpty()) {
            System.out.println("No hay prestamos registrados.");
            return;
        }
        prestamos.forEach(System.out::println);
    }

    private static void actualizarPrestamo() {
        Integer id = Integer.valueOf(leer("ID del prestamo: "));
        Prestamo prestamo = manager.find(Prestamo.class, id);
        if (prestamo == null) {
            System.out.println("No existe un prestamo con ese ID.");
            return;
        }
        System.out.println("Actual: " + prestamo);
        String estado = leer("Nuevo estado (ACTIVO, PAGADO, VENCIDO): ").toUpperCase();
        if (!estado.matches("ACTIVO|PAGADO|VENCIDO")) {
            System.out.println("Estado no valido.");
            return;
        }
        enTransaccion(() -> prestamo.setEstado(estado));
        System.out.println("Prestamo actualizado.");
    }

    private static void eliminarPrestamo() {
        Integer id = Integer.valueOf(leer("ID del prestamo: "));
        Prestamo prestamo = manager.find(Prestamo.class, id);
        if (prestamo == null) {
            System.out.println("No existe un prestamo con ese ID.");
            return;
        }
        enTransaccion(() -> manager.remove(prestamo));
        System.out.println("Prestamo eliminado.");
    }

    // ------------------------------------------------------------------
    // Planes
    // ------------------------------------------------------------------

    private static void menuPlanes() {
        String opcion;
        do {
            System.out.println("\n-- Planes --");
            System.out.println("1. Asignar un plan del catalogo");
            System.out.println("2. Listar");
            System.out.println("3. Actualizar estado");
            System.out.println("4. Eliminar");
            System.out.println("0. Volver");
            opcion = leer("Opcion: ");
            switch (opcion) {
                case "1" -> crearPlan();
                case "2" -> listarPlanes();
                case "3" -> actualizarPlan();
                case "4" -> eliminarPlan();
                case "0" -> { }
                default -> System.out.println("Opcion no valida.");
            }
        } while (!opcion.equals("0"));
    }

    private static void crearPlan() {
        Cliente cliente = seleccionarCliente();
        if (cliente == null) {
            return;
        }
        System.out.println("Catalogo de planes:");
        for (int i = 0; i < CATALOGO_PLANES.length; i++) {
            Object[] p = CATALOGO_PLANES[i];
            System.out.println("  " + (i + 1) + ". " + p[0] + " - " + p[1] + "GB, " + p[2] + " min, $" + p[3] + "/mes");
        }
        int indice = Integer.parseInt(leer("Elija un plan (numero): ")) - 1;
        if (indice < 0 || indice >= CATALOGO_PLANES.length) {
            System.out.println("Opcion de plan no valida.");
            return;
        }
        Object[] elegido = CATALOGO_PLANES[indice];
        Plan plan = new Plan(cliente, (String) elegido[0], (Integer) elegido[1], (Integer) elegido[2],
                (BigDecimal) elegido[3], "ACTIVO");
        enTransaccion(() -> manager.persist(plan));
        System.out.println("Plan asignado con ID " + plan.getId());
    }

    private static void listarPlanes() {
        List<Plan> planes = manager
                .createQuery("select p from Plan p order by p.id", Plan.class)
                .getResultList();
        if (planes.isEmpty()) {
            System.out.println("No hay planes registrados.");
            return;
        }
        planes.forEach(System.out::println);
    }

    private static void actualizarPlan() {
        Integer id = Integer.valueOf(leer("ID del plan: "));
        Plan plan = manager.find(Plan.class, id);
        if (plan == null) {
            System.out.println("No existe un plan con ese ID.");
            return;
        }
        System.out.println("Actual: " + plan);
        String estado = leer("Nuevo estado (ACTIVO, CANCELADO): ").toUpperCase();
        if (!estado.matches("ACTIVO|CANCELADO")) {
            System.out.println("Estado no valido.");
            return;
        }
        enTransaccion(() -> plan.setEstado(estado));
        System.out.println("Plan actualizado.");
    }

    private static void eliminarPlan() {
        Integer id = Integer.valueOf(leer("ID del plan: "));
        Plan plan = manager.find(Plan.class, id);
        if (plan == null) {
            System.out.println("No existe un plan con ese ID.");
            return;
        }
        enTransaccion(() -> manager.remove(plan));
        System.out.println("Plan eliminado.");
    }

    private static String leer(String mensaje) {
        System.out.print(mensaje);
        return IN.nextLine().trim();
    }
}