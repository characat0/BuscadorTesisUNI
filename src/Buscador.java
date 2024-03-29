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
            if (segunda.indexOf(prueba) != -1)
                interseccion.add(prueba);
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
        Statement stmt = conn.createStatement();
        ResultSet set = stmt.executeQuery(query);
        return set.getInt(1);
    }
    private ResultSet obtenResultados(String table, String condition)
        throws SQLException {
        String query = "SELECT * FROM " + table + " " + condition;
        Statement stmt = conn.createStatement();
        ResultSet set = stmt.executeQuery(query);
        return set;
    }
    private List<Integer> PersonaIdPorApellido (String apellido) throws SQLException {
        List<Integer> ids = new ArrayList<>();
        apellido = quitaTildes(apellido);
        String table = "Personas", condition = "WHERE nombre LIKE '%"+apellido+"%,%'";
        int numero = cuentaResultados(table,condition);
        if (numero > 0) {
            ResultSet set = obtenResultados(table,condition);
            while(set.next()) {
                ids.add(set.getInt("id"));
            }
            set.close();
        }
        return ids;
    }
    private List<Integer> PersonaIdPorNombre (String nombre)
    throws SQLException {
        List<Integer> ids = new ArrayList<Integer>();
        String table = "Personas",condition = "WHERE nombre LIKE '%,%" + nombre + "%'";
        int num = cuentaResultados(table,condition);
        if (num > 0) {
            ResultSet set = obtenResultados(table, condition);
            while(set.next()) {
                ids.add(set.getInt("id"));
            }
            set.close();
        }
        return ids;
    }
    private List<Integer> PersonaIdPorNombreCompleto (String nombre,String apellido)
            throws SQLException {
        nombre = quitaTildes(nombre);
        apellido = quitaTildes(apellido);
        List<Integer> PersonasIDs;
        if (nombre.equals("") && apellido.equals("")) return new ArrayList<Integer>();
        if (nombre.equals("")) {
            PersonasIDs = PersonaIdPorApellido(apellido);
        } else if (apellido.equals("")) {
            PersonasIDs = PersonaIdPorNombre(nombre);
        } else {
            List<Integer> ids1 = PersonaIdPorNombre(nombre), ids2 = PersonaIdPorApellido(apellido);
            PersonasIDs = intersect(ids1,ids2);
        }
        return PersonasIDs;
    }
    private List<Tesis> buscaTesisPorIdAutor(List<Integer> AutoresIDs) throws SQLException {
        List<Tesis> tesisList = new ArrayList<>();
        PreparedStatement stmt;
        String lista = AutoresIDs.toString().replace('[','(').replace(']',')');
        String table = "Tesis",condition = "WHERE (Autor1ID IN "+lista+") OR (Autor2ID IN "+lista+")";
        int numero = cuentaResultados(table,condition);
        if (numero > 0) {
            ResultSet set = obtenResultados(table,condition);
            while (set.next()) {
                Tesis tesis = new Tesis(set);
                tesis.setData(conn);
                tesisList.add(tesis);
            }
            set.close();
        }
        return tesisList;
    }
    private List<Tesis> buscaTesisPorIdAsesor(List<Integer> AutoresIDs) throws SQLException {
        List<Tesis> tesisList = new ArrayList<>();
        PreparedStatement stmt;
        String lista = AutoresIDs.toString().replace('[','(').replace(']',')');
        String table = "Tesis",condition = "WHERE (Asesor1ID IN "+lista+") OR (Asesor2ID IN "+lista+")";
        int numero = cuentaResultados(table,condition);
        if (numero > 0) {
            ResultSet set = obtenResultados(table,condition);
            while (set.next()) {
                Tesis tesis = new Tesis(set);
                tesis.setData(conn);
                tesisList.add(tesis);
            }
            set.close();
        }
        return tesisList;
    }
    public List<Tesis> buscaTesisPorNombreAutor (String nombre) throws SQLException{
        nombre = quitaTildes(nombre);
        List<Integer> AutoresIDs = PersonaIdPorNombre(nombre);
        return buscaTesisPorIdAutor(AutoresIDs);
    }
    public List<Tesis> buscaTesisPorApellidoAutor (String apellido) throws SQLException {
        apellido = quitaTildes(apellido);
        List<Integer> AutoresIDs = PersonaIdPorApellido(apellido);
        return buscaTesisPorIdAutor(AutoresIDs);
    }
    public List<Tesis> buscaTesisPorAutor(String nombre, String apellido) throws SQLException{
        List<Integer> AutoresIDs = PersonaIdPorNombreCompleto(nombre, apellido);
        return buscaTesisPorIdAutor(AutoresIDs);
    }
    public List<Tesis> buscaTesisPorAsesor(String nombre, String apellido) throws SQLException {
        List<Integer> AsesoresIDs = PersonaIdPorNombreCompleto(nombre, apellido);
        return buscaTesisPorIdAsesor(AsesoresIDs);
    }
    public List<Tesis>  buscaTesisPorEspecialidad (String nombre) throws SQLException {
        List<Tesis> tesisList = new ArrayList<>();
        String table = "Especialidades", condition = "WHERE nombre='" + nombre+"'";
        ResultSet set = obtenResultados(table,condition);
        String id = set.getString("id");
        table = "Tesis";
        condition = "WHERE Especialidad='" + id + "'";
        set = obtenResultados(table,condition);
        while (set.next()) {
            Tesis tesis = new Tesis(set);
            tesis.setData(conn);
            tesisList.add(tesis);
        }
        set.close();
        return tesisList;
    }
    public List<Tesis> buscaTesisPorTitulo (String titulo) throws SQLException {
        List<Tesis> tesisList = new ArrayList<>();
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM Tesis WHERE titulo=?");
        stmt.setString(1,titulo);
        ResultSet set = stmt.executeQuery();
        while (set.next()) {
            Tesis tesis = new Tesis(set);
            tesis.setData(conn);
            tesisList.add(tesis);
        }
        return tesisList;
    }
    public List<String> buscaFacultades () throws SQLException {
        String table = "Facultades", condition = "";
        ResultSet set = obtenResultados(table,condition);
        List<String> facultades = new ArrayList<>();
        while(set.next()) {
            facultades.add(set.getString("nombre"));
        }
        set.close();
        return facultades;
    }
    public List<String> buscaEspecialidades (String facultad) throws SQLException {
        PreparedStatement stmt;
        stmt = conn.prepareStatement("SELECT Especialidades.nombre FROM Especialidades,Facultades WHERE Especialidades.facuId=Facultades.id AND Facultades.nombre=?");
        stmt.setString(1,facultad);
        ResultSet set = stmt.executeQuery();
        List<String> especialidades = new ArrayList<>();
        while(set.next()) {
            especialidades.add(set.getString("nombre"));
        }
        set.close();
        return especialidades;
    }

}
