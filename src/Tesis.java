import java.sql.*;

public class Tesis {
    private String titulo,resumen,nivel,URI, autor1,autor2,asesor1,asesor2, especialidadID,especialidad,facultad;
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
        especialidadID = entrada.getString("Especialidad");

    }

    public void setData (Connection conn) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT Nombre FROM Personas WHERE id=?");
        stmt.setInt(1,autor1ID);
        ResultSet rs;
        rs = stmt.executeQuery();
        autor1 = rs.getString("Nombre");
        if (autor2ID > 0) {
            stmt.setInt(1,autor2ID);
            rs = stmt.executeQuery();
            autor2 = rs.getString("Nombre");
        }
        stmt.setInt(1,asesor1ID);
        rs = stmt.executeQuery();
        asesor1 = rs.getString("Nombre");
        if (asesor2ID > 0) {
            stmt.setInt(1,asesor2ID);
            rs = stmt.executeQuery();
            asesor2 = rs.getString("Nombre");
        }
        stmt.close();
        stmt = conn.prepareStatement("SELECT * FROM Especialidades WHERE id=?");
        stmt.setString(1,especialidadID);
        rs = stmt.executeQuery();
        especialidad = rs.getString("nombre");
        facultad = rs.getString("facuId");
    }

    public String toString () {
        String autores = autor2 != null ? autor1 + "; " + autor2 : autor1;
        String asesores = asesor2 != null ? asesor1 + "; " + asesor2 : asesor1;
        String cadena = "-Titulo: " + titulo;
        cadena = cadena + '\n' + "-Nivel: " + nivel;
        cadena = cadena + '\n' + "-Autor: " + autores;
        cadena = cadena + '\n' + "-Facultad: " + facultad;
        cadena = cadena + '\n' + "-Especialidad: " + especialidad;
        cadena = cadena + '\n' + "-Asesores: " + asesores;
        cadena = cadena + '\n' + "-URI: " + URI;
        cadena = cadena + '\n' + "-Resumen: " + resumen;
        cadena = cadena + '\n';
        return cadena;
    }

    public boolean equals (Tesis comparacion) {
        return (id ==comparacion.id);
    }
}
