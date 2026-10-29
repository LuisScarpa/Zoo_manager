package dao;

import java.sql.SQLException;

public class DashboardDao {

    
    public int[] resumo() throws SQLException {
        return Sql.um("SELECT (SELECT COUNT(*) FROM Animal), "
                + "(SELECT COUNT(DISTINCT especie) FROM Animal), "
                + "(SELECT COUNT(*) FROM Jaula), "
                + "(SELECT COUNT(*) FROM Setor), "
                + "(SELECT COUNT(*) FROM Funcionario), "
                + "(SELECT COUNT(*) FROM Alimento)",
                rs -> {
                    int[] v = new int[6];
                    for (int i = 0; i < v.length; i++) {
                        v[i] = rs.getInt(i + 1);
                    }
                    return v;
                });
    }
}
