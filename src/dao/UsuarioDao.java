package dao;

import java.sql.SQLException;
import model.Usuario;
import org.mindrot.jbcrypt.BCrypt;

public class UsuarioDao {

   
    public Usuario autenticar(String usuario, String senha) throws SQLException {
        Usuario u = Sql.um("SELECT u.id_usuario, u.id_funcionario, u.usuario, u.senha_hash, u.perfil, u.ativo, "
                + "       f.nome AS funcionario_nome "
                + "FROM Usuario u "
                + "LEFT JOIN Funcionario f ON f.id_funcionario = u.id_funcionario "
                + "WHERE u.usuario = ?",
                rs -> {
                    Usuario x = new Usuario();
                    x.id = rs.getInt("id_usuario");
                    x.idFuncionario = rs.getObject("id_funcionario", Integer.class);
                    x.usuario = rs.getString("usuario");
                    x.senhaHash = rs.getString("senha_hash");
                    x.perfil = rs.getString("perfil");
                    x.ativo = rs.getBoolean("ativo");
                    x.funcionarioNome = rs.getString("funcionario_nome");
                    return x;
                }, usuario);
        if (u == null) {
            return null;
        }
        boolean senhaCorreta;
        try {
            senhaCorreta = BCrypt.checkpw(senha, u.senhaHash);
        } catch (IllegalArgumentException e) {
            throw new SQLException("A senha cadastrada para este usuário não está em formato BCrypt válido.");
        }
        u.senhaHash = null;
        return senhaCorreta ? u : null;
    }
}
