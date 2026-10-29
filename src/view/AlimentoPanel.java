package view;

import dao.AlimentoDao;
import java.sql.SQLException;
import javax.swing.JTextField;
import model.Alimento;

public class AlimentoPanel extends CrudPanel {

    private final AlimentoDao dao = new AlimentoDao();

    public AlimentoPanel() {
        super("Alimentos", new String[] {"Código", "Nome", "Tipo", "Estoque (kg)"},
                "o alimento", "alimentação por espécie", null);
    }

    @Override
    protected void carregar(String filtro) throws SQLException {
        for (Alimento a : dao.listar()) {
            linha(a.id, a.nome, a.tipo, a.quantidadeEstoque);
        }
    }

    @Override
    protected void abrirFormulario(Integer id) {
        Alimento alimento = new Alimento();
        if (id != null) {
            try {
                alimento = dao.buscar(id);
            } catch (SQLException e) {
                Ui.erro(this, e);
                return;
            }
            if (alimento == null) {
                Ui.aviso(this, "Registro não encontrado. A lista será atualizada.");
                atualizar();
                return;
            }
        }
        final Alimento alvo = alimento;

        JTextField fNome = Ui.campo();
        fNome.setText(alvo.nome);
        JTextField fTipo = Ui.campo();
        fTipo.setText(alvo.tipo);
        JTextField fEstoque = Ui.campo();
        fEstoque.setText(id == null ? "0" : String.valueOf(alvo.quantidadeEstoque));

        FormDialog d = new FormDialog(this, id == null ? "Novo alimento" : "Editar alimento");
        d.campo("Nome *", fNome);
        d.campo("Tipo", fTipo);
        d.campo("Estoque (kg) *", fEstoque);

        boolean salvou = d.mostrar(() -> {
            try {
                alvo.nome = Ui.obrigatorio(fNome.getText(), "Nome", 100);
                alvo.tipo = Ui.opcional(fTipo.getText(), "Tipo", 100);
                alvo.quantidadeEstoque = Ui.inteiro(fEstoque.getText(), "Estoque (kg)", 0, 100000000, true);
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
            concluir(id == null ? "Alimento cadastrado com sucesso." : "Alimento atualizado com sucesso.");
        }
    }

    @Override
    protected void excluirRegistro(int id) throws SQLException {
        dao.excluir(id);
    }
}
