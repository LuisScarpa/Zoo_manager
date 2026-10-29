package dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import model.Animal;

public class AnimalDao {

    private static final String BASE =
            "SELECT a.id_animal, a.nome, a.sexo, a.idade, a.especie, a.id_jaula, a.dieta, "
            + "       j.tipo AS jaula_tipo, s.nome AS setor_nome "
            + "FROM Animal a "
            + "LEFT JOIN Jaula j ON j.id_jaula = a.id_jaula "
            + "LEFT JOIN Setor s ON s.id_setor = j.id_setor ";

    
    public List<Animal> listar(String filtro) throws SQLException {
        if (filtro == null || filtro.isEmpty()) {
            return Sql.listar(BASE + "ORDER BY a.nome", AnimalDao::mapear);
        }
        String like = "%" + filtro + "%";
        return Sql.listar(BASE + "WHERE a.nome LIKE ? OR a.especie LIKE ? ORDER BY a.nome",
                AnimalDao::mapear, like, like);
    }

    public Animal buscar(int id) throws SQLException {
        return Sql.um(BASE + "WHERE a.id_animal = ?", AnimalDao::mapear, id);
    }

    public void inserir(Animal a) throws SQLException {
        Sql.executar("INSERT INTO Animal (nome, sexo, idade, especie, id_jaula, dieta) VALUES (?, ?, ?, ?, ?, ?)",
                a.nome, a.sexo, a.idade, a.especie, a.idJaula, a.dieta);
    }

    public void atualizar(Animal a) throws SQLException {
        Sql.executar("UPDATE Animal SET nome = ?, sexo = ?, idade = ?, especie = ?, id_jaula = ?, dieta = ? "
                + "WHERE id_animal = ?",
                a.nome, a.sexo, a.idade, a.especie, a.idJaula, a.dieta, a.id);
    }

    public void excluir(int id) throws SQLException {
        Sql.executar("DELETE FROM Animal WHERE id_animal = ?", id);
    }

    
    public List<String> especies() throws SQLException {
        return Sql.listar("SELECT especie FROM Animal UNION SELECT especie FROM Especie_Alimento ORDER BY especie",
                rs -> rs.getString(1));
    }

    private static Animal mapear(ResultSet rs) throws SQLException {
        Animal a = new Animal();
        a.id = rs.getInt("id_animal");
        a.nome = rs.getString("nome");
        a.sexo = rs.getString("sexo");
        a.idade = rs.getObject("idade", Integer.class);
        a.especie = rs.getString("especie");
        a.idJaula = rs.getObject("id_jaula", Integer.class);
        a.dieta = rs.getString("dieta");
        a.jaulaTipo = rs.getString("jaula_tipo");
        a.setorNome = rs.getString("setor_nome");
        return a;
    }
}
