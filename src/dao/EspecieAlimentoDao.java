package dao;

import java.sql.SQLException;
import java.util.List;
import model.EspecieAlimento;

public class EspecieAlimentoDao {

    
    public List<EspecieAlimento> listarPorEspecie(String especie) throws SQLException {
        return Sql.listar("SELECT ea.especie, ea.id_alimento, ea.quantidade_diaria, "
                + "       al.nome AS alimento_nome, al.tipo AS alimento_tipo, al.quantidade_estoque "
                + "FROM Especie_Alimento ea "
                + "JOIN Alimento al ON al.id_alimento = ea.id_alimento "
                + "WHERE ea.especie = ? ORDER BY al.nome",
                rs -> {
                    EspecieAlimento e = new EspecieAlimento();
                    e.especie = rs.getString("especie");
                    e.idAlimento = rs.getInt("id_alimento");
                    e.quantidadeDiaria = rs.getBigDecimal("quantidade_diaria");
                    e.alimentoNome = rs.getString("alimento_nome");
                    e.alimentoTipo = rs.getString("alimento_tipo");
                    e.estoque = rs.getInt("quantidade_estoque");
                    return e;
                }, especie);
    }
}
