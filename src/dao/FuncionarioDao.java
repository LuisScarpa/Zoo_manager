package dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import model.Funcionario;
import model.Opcao;

public class FuncionarioDao {

    private static final String BASE =
            "SELECT f.id_funcionario, f.id_setor, f.nome, f.idade, f.cpf, f.funcao, f.turno, s.nome AS setor_nome "
            + "FROM Funcionario f "
            + "LEFT JOIN Setor s ON s.id_setor = f.id_setor ";

    
    public List<Funcionario> listar(String filtro) throws SQLException {
        if (filtro == null || filtro.isEmpty()) {
            return Sql.listar(BASE + "ORDER BY f.nome", FuncionarioDao::mapear);
        }
        String like = "%" + filtro + "%";
        return Sql.listar(BASE + "WHERE f.nome LIKE ? OR f.cpf LIKE ? OR f.funcao LIKE ? ORDER BY f.nome",
                FuncionarioDao::mapear, like, like, like);
    }

    public Funcionario buscar(int id) throws SQLException {
        return Sql.um(BASE + "WHERE f.id_funcionario = ?", FuncionarioDao::mapear, id);
    }

    public void inserir(Funcionario f) throws SQLException {
        Sql.executar("INSERT INTO Funcionario (id_setor, nome, idade, cpf, funcao, turno) VALUES (?, ?, ?, ?, ?, ?)",
                f.idSetor, f.nome, f.idade, f.cpf, f.funcao, f.turno);
    }

    public void atualizar(Funcionario f) throws SQLException {
        Sql.executar("UPDATE Funcionario SET id_setor = ?, nome = ?, idade = ?, cpf = ?, funcao = ?, turno = ? "
                + "WHERE id_funcionario = ?",
                f.idSetor, f.nome, f.idade, f.cpf, f.funcao, f.turno, f.id);
    }

    public void excluir(int id) throws SQLException {
        Sql.executar("DELETE FROM Funcionario WHERE id_funcionario = ?", id);
    }

    public List<Opcao> opcoes() throws SQLException {
        return Sql.listar("SELECT id_funcionario, nome, funcao FROM Funcionario ORDER BY nome",
                rs -> {
                    String funcao = rs.getString("funcao");
                    return new Opcao(rs.getInt("id_funcionario"),
                            rs.getString("nome") + (funcao == null ? "" : " (" + funcao + ")"));
                });
    }

    private static Funcionario mapear(ResultSet rs) throws SQLException {
        Funcionario f = new Funcionario();
        f.id = rs.getInt("id_funcionario");
        f.idSetor = rs.getObject("id_setor", Integer.class);
        f.nome = rs.getString("nome");
        f.idade = rs.getObject("idade", Integer.class);
        f.cpf = rs.getString("cpf");
        f.funcao = rs.getString("funcao");
        f.turno = rs.getString("turno");
        f.setorNome = rs.getString("setor_nome");
        return f;
    }
}
