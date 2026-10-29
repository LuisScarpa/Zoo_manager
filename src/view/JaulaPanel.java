package view;

import dao.FuncionarioDao;
import dao.JaulaDao;
import dao.SetorDao;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import model.Jaula;
import model.Opcao;

public class JaulaPanel extends CrudPanel {

    private final JaulaDao dao = new JaulaDao();
    private final SetorDao setorDao = new SetorDao();
    private final FuncionarioDao funcionarioDao = new FuncionarioDao();

    public JaulaPanel() {
        super("Jaulas", new String[] {"Código", "Tipo", "Setor", "Cuidador", "Status", "Capacidade", "Animais"},
                "a jaula", "animais alocados nela", null);
    }

    @Override
    protected void carregar(String filtro) throws SQLException {
        for (Jaula j : dao.listar()) {
            linha(j.id, j.tipo, j.setorNome, j.cuidadorNome, j.status, j.capacidade, j.ocupacao);
        }
    }

    @Override
    protected void abrirFormulario(Integer id) {
        Jaula jaula = new Jaula();
        List<Opcao> setores;
        List<Opcao> funcionarios;
        try {
            if (id != null) {
                jaula = dao.buscar(id);
                if (jaula == null) {
                    Ui.aviso(this, "Registro não encontrado. A lista será atualizada.");
                    atualizar();
                    return;
                }
            }
            setores = setorDao.opcoes();
            funcionarios = funcionarioDao.opcoes();
        } catch (SQLException e) {
            Ui.erro(this, e);
            return;
        }
        if (setores.isEmpty()) {
            Ui.aviso(this, "Cadastre pelo menos um setor antes de cadastrar uma jaula.");
            return;
        }
        final Jaula alvo = jaula;

        JTextField fTipo = Ui.campo();
        fTipo.setText(alvo.tipo);
        JComboBox<Opcao> fSetor = Ui.comboOpcoes(setores, null);
        Ui.selecionar(fSetor, alvo.idSetor);
        JComboBox<Opcao> fCuidador = Ui.comboOpcoes(funcionarios, "Sem cuidador");
        Ui.selecionar(fCuidador, alvo.idCuidador);
        JComboBox<String> fStatus = Ui.comboTexto(true, List.of("Ocupada", "Disponível", "Em manutenção"));
        Ui.selecionarTexto(fStatus, alvo.status);
        JTextField fCapacidade = Ui.campo();
        fCapacidade.setText(id == null ? "" : String.valueOf(alvo.capacidade));

        FormDialog d = new FormDialog(this, id == null ? "Nova jaula" : "Editar jaula");
        d.campo("Setor *", fSetor);
        d.campo("Tipo", fTipo);
        d.campo("Cuidador", fCuidador);
        d.campo("Status", fStatus);
        d.campo("Capacidade *", fCapacidade);

        boolean salvou = d.mostrar(() -> {
            try {
                alvo.idSetor = Ui.idSelecionado(fSetor);
                if (alvo.idSetor == null) {
                    throw new IllegalArgumentException("O campo \"Setor\" é obrigatório.");
                }
                alvo.tipo = Ui.opcional(fTipo.getText(), "Tipo", 100);
                alvo.idCuidador = Ui.idSelecionado(fCuidador);
                alvo.status = Ui.opcional(Ui.textoCombo(fStatus), "Status", 50);
                alvo.capacidade = Ui.inteiro(fCapacidade.getText(), "Capacidade", 1, 10000, true);
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
            concluir(id == null ? "Jaula cadastrada com sucesso." : "Jaula atualizada com sucesso.");
        }
    }

    @Override
    protected void excluirRegistro(int id) throws SQLException {
        dao.excluir(id);
    }
}
