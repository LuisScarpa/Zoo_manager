package dao;

import conexao.Conexao;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


final class Sql {

    interface Mapeador<T> {
        T mapear(ResultSet rs) throws SQLException;
    }

    private Sql() {
    }

    static int executar(String sql, Object... params) throws SQLException {
        try (Connection c = Conexao.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            preencher(ps, params);
            return ps.executeUpdate();
        }
    }

    static <T> List<T> listar(String sql, Mapeador<T> mapeador, Object... params) throws SQLException {
        try (Connection c = Conexao.getConnection(); PreparedStatement ps = c.prepareStatement(sql)) {
            preencher(ps, params);
            try (ResultSet rs = ps.executeQuery()) {
                List<T> lista = new ArrayList<>();
                while (rs.next()) {
                    lista.add(mapeador.mapear(rs));
                }
                return lista;
            }
        }
    }

    static <T> T um(String sql, Mapeador<T> mapeador, Object... params) throws SQLException {
        List<T> lista = listar(sql, mapeador, params);
        return lista.isEmpty() ? null : lista.get(0);
    }

    private static void preencher(PreparedStatement ps, Object[] params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            ps.setObject(i + 1, params[i]);
        }
    }
}
