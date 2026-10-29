package dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import model.Jaula;
import model.Opcao;

public class JaulaDao {

    private static final String BASE =
            "SELECT j.id_jaula, j.id_setor, j.id_cuidador, j.status, j.tipo, j.capacidade, "
            + "       s.nome AS setor_nome, f.nome AS cuidador_nome, "
            + "       (SELECT COUNT(*) FROM Animal a WHERE a.id_jaula = j.id_jaula) AS ocupacao "
            + "FROM Jaula j "
            + "JOIN Setor s ON s.id_setor = j.id_setor "
            + "LEFT JOIN Funcionario f ON f.id_funcionario = j.id_cuidador ";

    public List<Jaula> listar() throws SQLException {
        return Sql.listar(BASE + "ORDER BY j.id_jaula", JaulaDao::mapear);
    }

    public Jaula buscar(int id) throws SQLException {
        return Sql.um(BASE + "WHERE j.id_jaula = ?", JaulaDao::mapear, id);
    }

    public void inserir(Jaula j) throws SQLException {
        Sql.executar("INSERT INTO Jaula (id_setor, id_cuidador, status, tipo, capacidade) VALUES (?, ?, ?, ?, ?)",
                j.idSetor, j.idCuidador, j.status, j.tipo, j.capacidade);
    }

    public void atualizar(Jaula j) throws SQLException {
        Sql.executar("UPDATE Jaula SET id_setor = ?, id_cuidador = ?, status = ?, tipo = ?, capacidade = ? "
                + "WHERE id_jaula = ?",
                j.idSetor, j.idCuidador, j.status, j.tipo, j.capacidade, j.id);
    }

    public void excluir(int id) throws SQLException {
        Sql.executar("DELETE FROM Jaula WHERE id_jaula = ?", id);
    }

    
    public List<Opcao> opcoes() throws SQLException {
        return Sql.listar("SELECT j.id_jaula, j.tipo, j.capacidade, "
                + "(SELECT COUNT(*) FROM Animal a WHERE a.id_jaula = j.id_jaula) AS ocupacao "
                + "FROM Jaula j ORDER BY j.id_jaula",
                rs -> {
                    String tipo = rs.getString("tipo");
                    return new Opcao(rs.getInt("id_jaula"), "Jaula " + rs.getInt("id_jaula")
                            + (tipo == null ? "" : " - " + tipo)
                            + " (" + rs.getInt("ocupacao") + "/" + rs.getInt("capacidade") + ")");
                });
    }

    private static Jaula mapear(ResultSet rs) throws SQLException {
        Jaula j = new Jaula();
        j.id = rs.getInt("id_jaula");
        j.idSetor = rs.getInt("id_setor");
        j.idCuidador = rs.getObject("id_cuidador", Integer.class);
        j.status = rs.getString("status");
        j.tipo = rs.getString("tipo");
        j.capacidade = rs.getInt("capacidade");
        j.setorNome = rs.getString("setor_nome");
        j.cuidadorNome = rs.getString("cuidador_nome");
        j.ocupacao = rs.getInt("ocupacao");
        return j;
    }
}
