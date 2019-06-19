import java.sql.*;


public class Main {

    public static void main(String[] args)
    throws SQLException {
        Buscador buscador = new Buscador("repositorio.db");
        buscador.buscaTesisPorAutor("Glen Dario","Rodriguez Rafael");



    }
}
