import java.sql.*;

public class Tesis {
    private String titulo,resumen,nivel,URI, autor1,autor2,asesor1,asesor2,especialidad;
    private int autor1ID,autor2ID,asesor1ID,asesor2ID,anno,id;
    public Tesis() {

    }
    public Tesis(ResultSet entrada) throws SQLException {
        id = entrada.getInt("id");
        titulo = entrada.getString("Titulo");
        resumen = entrada.getString("Resumen");
        nivel = entrada.getString("Nivel");
        URI = entrada.getString("URI");
        anno = entrada.getInt("Anno");
        autor1ID = entrada.getInt("autor1ID");
        autor2ID = entrada.getInt("autor2ID");
        asesor1ID = entrada.getInt("asesor1ID");
        asesor2ID = entrada.getInt("asesor2ID");
        especialidad = entrada.getString("Especialidad");

    }

    public void setPersonas (Connection conn) throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs;
        rs = stmt.executeQuery("SELECT Nombre FROM Personas WHERE id="+autor1ID);
        autor1 = rs.getString("Nombre");
        if (autor2ID > 0) {
            rs = stmt.executeQuery("SELECT Nombre FROM Personas WHERE id="+autor2ID);
            autor2 = rs.getString("Nombre");
        }
        rs = stmt.executeQuery("SELECT Nombre FROM Personas WHERE id="+asesor1ID);
        asesor1 = rs.getString("Nombre");
        if (asesor2ID > 0) {
            rs = stmt.executeQuery("SELECT Nombre FROM Personas WHERE id="+asesor2ID);
            asesor2 = rs.getString("Nombre");
        }
    }

    public String toString () {
        String autores = autor2 != null ? autor1 + "; " + autor2 : autor1;
        String asesores = asesor2 != null ? asesor1 + "; " + asesor2 : asesor1;
        String cadena = "-Titulo: " + titulo;
        cadena = cadena + '\n' + "-Nivel: " + nivel;
        cadena = cadena + '\n' + "-Autor: " + autores;
        cadena = cadena + '\n' + "-Especialidad: " + especialidad;
        cadena = cadena + '\n' + "-Asesores: " + asesores;
        cadena = cadena + '\n' + "-URI: " + URI;
        cadena = cadena + '\n' + "-Resumen: " + resumen;
        return cadena;
    }

    public boolean equals (Tesis comparacion) {
        return (id ==comparacion.id);
    }
}
