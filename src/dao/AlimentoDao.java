package dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import model.Alimento;

public class AlimentoDao {

    private static final String BASE = "SELECT id_alimento, nome, tipo, quantidade_estoque FROM Alimento ";

    public List<Alimento> listar() throws SQLException {
        return Sql.listar(BASE + "ORDER BY nome", AlimentoDao::mapear);
    }

    public Alimento buscar(int id) throws SQLException {
        return Sql.um(BASE + "WHERE id_alimento = ?", AlimentoDao::mapear, id);
    }

    public void inserir(Alimento a) throws SQLException {
        Sql.executar("INSERT INTO Alimento (nome, tipo, quantidade_estoque) VALUES (?, ?, ?)",
                a.nome, a.tipo, a.quantidadeEstoque);
    }

    public void atualizar(Alimento a) throws SQLException {
        Sql.executar("UPDATE Alimento SET nome = ?, tipo = ?, quantidade_estoque = ? WHERE id_alimento = ?",
                a.nome, a.tipo, a.quantidadeEstoque, a.id);
    }

    public void excluir(int id) throws SQLException {
        Sql.executar("DELETE FROM Alimento WHERE id_alimento = ?", id);
    }

    private static Alimento mapear(ResultSet rs) throws SQLException {
        Alimento a = new Alimento();
        a.id = rs.getInt("id_alimento");
        a.nome = rs.getString("nome");
        a.tipo = rs.getString("tipo");
        a.quantidadeEstoque = rs.getInt("quantidade_estoque");
        return a;
    }
}
