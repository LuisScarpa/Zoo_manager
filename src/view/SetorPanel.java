package view;

import dao.FuncionarioDao;
import dao.SetorDao;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import model.Opcao;
import model.Setor;

public class SetorPanel extends CrudPanel {

    private final SetorDao dao = new SetorDao();
    private final FuncionarioDao funcionarioDao = new FuncionarioDao();

    public SetorPanel() {
        super("Setores", new String[] {"Código", "Nome", "Tipo", "Descrição", "Responsável", "Jaulas"},
                "o setor", "jaulas ou funcionários do setor", null);
    }

    @Override
    protected void carregar(String filtro) throws SQLException {
        for (Setor s : dao.listar()) {
            linha(s.id, s.nome, s.tipo, s.descricao, s.responsavelNome, s.totalJaulas);
        }
    }

    @Override
    protected void abrirFormulario(Integer id) {
        Setor setor = new Setor();
        List<Opcao> funcionarios;
        try {
            if (id != null) {
                setor = dao.buscar(id);
                if (setor == null) {
                    Ui.aviso(this, "Registro não encontrado. A lista será atualizada.");
                    atualizar();
                    return;
                }
            }
            funcionarios = funcionarioDao.opcoes();
        } catch (SQLException e) {
            Ui.erro(this, e);
            return;
        }
        final Setor alvo = setor;

        JTextField fNome = Ui.campo();
        fNome.setText(alvo.nome);
        JTextField fTipo = Ui.campo();
        fTipo.setText(alvo.tipo);
        JTextField fDescricao = Ui.campo();
        fDescricao.setText(alvo.descricao);
        JComboBox<Opcao> fResponsavel = Ui.comboOpcoes(funcionarios, "Sem responsável");
        Ui.selecionar(fResponsavel, alvo.idResponsavel);

        FormDialog d = new FormDialog(this, id == null ? "Novo setor" : "Editar setor");
        d.campo("Nome *", fNome);
        d.campo("Tipo", fTipo);
        d.campo("Descrição", fDescricao);
        d.campo("Responsável", fResponsavel);

        boolean salvou = d.mostrar(() -> {
            try {
                alvo.nome = Ui.obrigatorio(fNome.getText(), "Nome", 100);
                alvo.tipo = Ui.opcional(fTipo.getText(), "Tipo", 100);
                alvo.descricao = Ui.opcional(fDescricao.getText(), "Descrição", 255);
                alvo.idResponsavel = Ui.idSelecionado(fResponsavel);
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
            concluir(id == null ? "Setor cadastrado com sucesso." : "Setor atualizado com sucesso.");
        }
    }

    @Override
    protected void excluirRegistro(int id) throws SQLException {
        dao.excluir(id);
    }
}
