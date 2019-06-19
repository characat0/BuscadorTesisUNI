import java.sql.*;
import java.util.List;


public class Main {

    public static void main(String[] args)
    throws SQLException {
        Buscador buscador = new Buscador("repositorio.db");
        List<Tesis> tesises = buscador.buscaTesisPorAutor("Juan","Perez");
        tesises.forEach((tesis -> {
            System.out.println(tesis.toString());
        }));


    }
}
