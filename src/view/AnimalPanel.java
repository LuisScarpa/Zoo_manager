package view;

import dao.AnimalDao;
import dao.JaulaDao;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import model.Animal;
import model.Opcao;

public class AnimalPanel extends CrudPanel {

    private final AnimalDao dao = new AnimalDao();
    private final JaulaDao jaulaDao = new JaulaDao();

    public AnimalPanel() {
        super("Animais", new String[] {"Código", "Nome", "Espécie", "Sexo", "Idade", "Jaula", "Setor", "Dieta"},
                "o animal", "", "nome ou espécie");
    }

    @Override
    protected void carregar(String filtro) throws SQLException {
        for (Animal a : dao.listar(filtro)) {
            String jaula = a.idJaula == null ? "Sem jaula"
                    : "#" + a.idJaula + (a.jaulaTipo == null ? "" : " - " + a.jaulaTipo);
            linha(a.id, a.nome, a.especie, a.sexo, a.idade, jaula, a.setorNome, a.dieta);
        }
    }

    @Override
    protected void abrirFormulario(Integer id) {
        Animal animal = new Animal();
        List<String> especies;
        List<Opcao> jaulas;
        try {
            if (id != null) {
                animal = dao.buscar(id);
                if (animal == null) {
                    Ui.aviso(this, "Registro não encontrado. A lista será atualizada.");
                    atualizar();
                    return;
                }
            }
            especies = dao.especies();
            jaulas = jaulaDao.opcoes();
        } catch (SQLException e) {
            Ui.erro(this, e);
            return;
        }
        final Animal alvo = animal;

        JTextField fNome = Ui.campo();
        fNome.setText(alvo.nome);
        JComboBox<String> fEspecie = Ui.comboTexto(true, especies);
        Ui.selecionarTexto(fEspecie, alvo.especie);
        JComboBox<String> fSexo = Ui.comboTexto(false, List.of("Macho", "Fêmea", "Não informado"));
        Ui.selecionarTexto(fSexo, alvo.sexo == null ? "Não informado" : alvo.sexo);
        JTextField fIdade = Ui.campo();
        fIdade.setText(alvo.idade == null ? "" : String.valueOf(alvo.idade));
        JComboBox<Opcao> fJaula = Ui.comboOpcoes(jaulas, "Sem jaula");
        Ui.selecionar(fJaula, alvo.idJaula);
        JComboBox<String> fDieta = Ui.comboTexto(true, List.of("Carnívoro", "Herbívoro", "Onívoro", "Frugívoro/granívoro"));
        Ui.selecionarTexto(fDieta, alvo.dieta);

        FormDialog d = new FormDialog(this, id == null ? "Novo animal" : "Editar animal");
        d.campo("Nome *", fNome);
        d.campo("Espécie *", fEspecie);
        d.campo("Sexo", fSexo);
        d.campo("Idade (anos)", fIdade);
        d.campo("Jaula", fJaula);
        d.campo("Dieta", fDieta);

        boolean salvou = d.mostrar(() -> {
            try {
                alvo.nome = Ui.obrigatorio(fNome.getText(), "Nome", 100);
                alvo.especie = Ui.obrigatorio(Ui.textoCombo(fEspecie), "Espécie", 100);
                String sexo = Ui.textoCombo(fSexo);
                alvo.sexo = sexo.equals("Não informado") ? null : sexo;
                alvo.idade = Ui.inteiro(fIdade.getText(), "Idade", 0, 150, false);
                alvo.idJaula = Ui.idSelecionado(fJaula);
                alvo.dieta = Ui.opcional(Ui.textoCombo(fDieta), "Dieta", 255);
                if (id == null) {
                    dao.inserir(alvo);
                } else {
                    dao.atualizar(alvo);
                }
                return null;
            } catch (IllegalArgumentException e) {
                return e.getMessage();
            } catch (SQLException e) {
                return Ui.mensagem(e);
            }
        });
        if (salvou) {
            concluir(id == null ? "Animal cadastrado com sucesso." : "Animal atualizado com sucesso.");
        }
    }

    @Override
    protected void excluirRegistro(int id) throws SQLException {
        dao.excluir(id);
    }
}
