import java.sql.*;
import java.util.List;


public class Main {

    public static void main(String[] args)
    throws SQLException {
        Buscador buscador = new Buscador("repositorio.db");
        List<Tesis> tesises;
        tesises = buscador.buscaTesisPorTitulo("Reconocimiento de voz");
        tesises.forEach((tesis -> {
            System.out.println(tesis.toString());
        }));


    }
}
