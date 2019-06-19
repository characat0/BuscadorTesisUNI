import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class Buscador {
    private Connection conn;
    public Buscador (String archivo) throws SQLException {
        conn = DriverManager.getConnection("jdbc:sqlite:" + archivo);
    }
    public Buscador (Connection c) {
        conn = c;
    }
    private List<Integer> intersect (List<Integer> primera, List<Integer> segunda) {
        List<Integer> interseccion = new ArrayList<>();
        for (int i = 0; i < primera.size(); i++) {
            int prueba = primera.get(i);
            if (segunda.indexOf(prueba) != -1) {
                interseccion.add(prueba);
            }
        }
        return interseccion;
    }
    private String quitaTildes (String cadena) {
        return cadena.replaceAll("á","a")
            .replaceAll("é","e")
            .replaceAll("í","i")
            .replaceAll("ó","o")
            .replaceAll("ú","u")
            .replaceAll("ñ","n")
            .replaceAll("Á","A")
            .replaceAll("É","E")
            .replaceAll("Í","I")
            .replaceAll("Ó","O")
            .replaceAll("Ú","U")
            .replaceAll("Ñ","N");
    }
    private int cuentaResultados(String table, String condition)
    throws SQLException {
        //PreparedStatement stmt;
        String query = "SELECT COUNT(*) FROM " + table + " " + condition;
        System.out.println(query);
        Statement stmt = conn.createStatement();

        //stmt = conn.prepareStatement(query);
        ResultSet set = stmt.executeQuery(query);
        return set.getInt(1);
    }
    private ResultSet obtenResultados(String table, String condition)
        throws SQLException {
        String query = "SELECT * FROM " + table + " " + condition;
        Statement stmt = conn.createStatement();
        ResultSet set = stmt.executeQuery(query);
        stmt.close();
        return set;
    }
    public List<Integer> PersonaIdPorNombre (String nombre)
    throws SQLException {
        List<Integer> ids = new ArrayList<Integer>();
        int numero = cuentaResultados("Personas", "WHERE nombre LIKE '%" + nombre + "%'");
        if (numero > 0) {
            PreparedStatement stmt;
            stmt = conn.prepareStatement("SELECT id FROM Personas WHERE Nombre LIKE '%" + nombre + "%'");
            ResultSet set = stmt.executeQuery();
            while(set.next()) {
                ids.add(set.getInt("id"));
            }
        }
        return ids;
    }
    public List<Integer> PersonaIdPorNombre (String nombre,String apellido)
            throws SQLException {
        List<Integer> ids1 = new ArrayList<Integer>();
        List<Integer> ids2 = new ArrayList<Integer>();
        int numero1 = cuentaResultados("Personas", "WHERE nombre LIKE '%,%" + nombre + "%'");
        int numero2 = cuentaResultados("Personas","WHERE nombre LIKE '%"+apellido+"%'");
        if (numero1 > 0 && numero2 > 0) {
            PreparedStatement stmt;
            ResultSet set;
            stmt = conn.prepareStatement("SELECT id FROM Personas WHERE nombre LIKE '%" + nombre + "%'");
            set = stmt.executeQuery();
            while(set.next()) {
                ids1.add(set.getInt("id"));
            }
            stmt = conn.prepareStatement("SELECT id FROM Personas WHERE nombre LIKE '%,%"+apellido+"%'");
            set = stmt.executeQuery();
            while(set.next()) {
                ids2.add(set.getInt("id"));
            }
        }
        return intersect(ids1,ids2);
    }
    public void buscaTesisPorAutor (String nombre) throws SQLException{
        PreparedStatement stmt;
        nombre = quitaTildes(nombre);
        List<Integer> AutoresIDs = PersonaIdPorNombre(nombre);
        String lista = AutoresIDs.toString().replace('[','(').replace(']',')');
        int numero = cuentaResultados("Tesis","WHERE (Autor1ID IN "+lista+") OR (Autor2ID IN "+lista+")");
        if (numero > 0) {
            stmt = conn.prepareStatement("SELECT * FROM Tesis WHERE (Autor1ID IN "+lista+") OR (Autor2ID IN "+lista+")");
            ResultSet set = stmt.executeQuery();
            while (set.next()) {
                Tesis prueba = new Tesis(set);
                prueba.setPersonas(conn);
                System.out.println(prueba.toString());
            }
        }
    }
    public void buscaTesisPorAutor (String nombre, String apellido) throws SQLException{
        PreparedStatement stmt;
        nombre = quitaTildes(nombre);
        apellido = quitaTildes(apellido);
        List<Integer> AutoresIDs = PersonaIdPorNombre(nombre,apellido);
        String lista = AutoresIDs.toString().replace('[','(').replace(']',')');
        int numero = cuentaResultados("Tesis","WHERE (Autor1ID IN "+lista+") OR (Autor2ID IN "+lista+")");
        if (numero > 0) {
            stmt = conn.prepareStatement("SELECT * FROM Tesis WHERE (Autor1ID IN "+lista+") OR (Autor2ID IN "+lista+")");
            ResultSet set = stmt.executeQuery();
            while (set.next()) {
                Tesis prueba = new Tesis(set);
                prueba.setPersonas(conn);
                System.out.println(prueba.toString());
            }
        }
    }
}
