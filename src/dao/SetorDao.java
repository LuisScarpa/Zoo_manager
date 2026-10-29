package dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import model.Opcao;
import model.Setor;

public class SetorDao {

    private static final String BASE =
            "SELECT s.id_setor, s.nome, s.tipo, s.descricao, s.id_responsavel, f.nome AS responsavel_nome, "
            + "       (SELECT COUNT(*) FROM Jaula j WHERE j.id_setor = s.id_setor) AS total_jaulas "
            + "FROM Setor s "
            + "LEFT JOIN Funcionario f ON f.id_funcionario = s.id_responsavel ";

    public List<Setor> listar() throws SQLException {
        return Sql.listar(BASE + "ORDER BY s.nome", SetorDao::mapear);
    }

    public Setor buscar(int id) throws SQLException {
        return Sql.um(BASE + "WHERE s.id_setor = ?", SetorDao::mapear, id);
    }

    public void inserir(Setor s) throws SQLException {
        Sql.executar("INSERT INTO Setor (nome, tipo, descricao, id_responsavel) VALUES (?, ?, ?, ?)",
                s.nome, s.tipo, s.descricao, s.idResponsavel);
    }

    public void atualizar(Setor s) throws SQLException {
        Sql.executar("UPDATE Setor SET nome = ?, tipo = ?, descricao = ?, id_responsavel = ? WHERE id_setor = ?",
                s.nome, s.tipo, s.descricao, s.idResponsavel, s.id);
    }

    public void excluir(int id) throws SQLException {
        Sql.executar("DELETE FROM Setor WHERE id_setor = ?", id);
    }

    public List<Opcao> opcoes() throws SQLException {
        return Sql.listar("SELECT id_setor, nome FROM Setor ORDER BY nome",
                rs -> new Opcao(rs.getInt("id_setor"), rs.getString("nome")));
    }

    private static Setor mapear(ResultSet rs) throws SQLException {
        Setor s = new Setor();
        s.id = rs.getInt("id_setor");
        s.nome = rs.getString("nome");
        s.tipo = rs.getString("tipo");
        s.descricao = rs.getString("descricao");
        s.idResponsavel = rs.getObject("id_responsavel", Integer.class);
        s.responsavelNome = rs.getString("responsavel_nome");
        s.totalJaulas = rs.getInt("total_jaulas");
        return s;
    }
}
