package view;

import dao.FuncionarioDao;
import dao.SetorDao;
import java.sql.SQLException;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import model.Funcionario;
import model.Opcao;

public class FuncionarioPanel extends CrudPanel {

    private final FuncionarioDao dao = new FuncionarioDao();
    private final SetorDao setorDao = new SetorDao();

    public FuncionarioPanel() {
        super("Funcionários", new String[] {"Código", "Nome", "Idade", "CPF", "Função", "Turno", "Setor"},
                "o funcionário", "setor sob sua responsabilidade, jaulas sob seu cuidado ou usuário de acesso",
                "nome, CPF ou função");
    }

    @Override
    protected void carregar(String filtro) throws SQLException {
        for (Funcionario f : dao.listar(filtro)) {
            linha(f.id, f.nome, f.idade, f.cpf, f.funcao, f.turno, f.setorNome);
        }
    }

    @Override
    protected void abrirFormulario(Integer id) {
        Funcionario func = new Funcionario();
        List<Opcao> setores;
        try {
            if (id != null) {
                func = dao.buscar(id);
                if (func == null) {
                    Ui.aviso(this, "Registro não encontrado. A lista será atualizada.");
                    atualizar();
                    return;
                }
            }
            setores = setorDao.opcoes();
        } catch (SQLException e) {
            Ui.erro(this, e);
            return;
        }
        final Funcionario alvo = func;

        JTextField fNome = Ui.campo();
        fNome.setText(alvo.nome);
        JTextField fIdade = Ui.campo();
        fIdade.setText(alvo.idade == null ? "" : String.valueOf(alvo.idade));
        JTextField fCpf = Ui.campo();
        fCpf.setText(alvo.cpf);
        JComboBox<String> fFuncao = Ui.comboTexto(true, List.of("Cuidador", "Auxiliar", "Administrador"));
        Ui.selecionarTexto(fFuncao, alvo.funcao);
        JComboBox<String> fTurno = Ui.comboTexto(true, List.of("Manhã", "Tarde", "Noite", "Integral"));
        Ui.selecionarTexto(fTurno, alvo.turno);
        JComboBox<Opcao> fSetor = Ui.comboOpcoes(setores, "Sem setor");
        Ui.selecionar(fSetor, alvo.idSetor);

        FormDialog d = new FormDialog(this, id == null ? "Novo funcionário" : "Editar funcionário");
        d.campo("Nome *", fNome);
        d.campo("Idade", fIdade);
        d.campo("CPF", fCpf);
        d.campo("Função", fFuncao);
        d.campo("Turno", fTurno);
        d.campo("Setor", fSetor);

        boolean salvou = d.mostrar(() -> {
            try {
                alvo.nome = Ui.obrigatorio(fNome.getText(), "Nome", 100);
                alvo.idade = Ui.inteiro(fIdade.getText(), "Idade", 14, 100, false);
                alvo.cpf = cpf(fCpf.getText());
                alvo.funcao = Ui.opcional(Ui.textoCombo(fFuncao), "Função", 100);
                alvo.turno = Ui.opcional(Ui.textoCombo(fTurno), "Turno", 50);
                alvo.idSetor = Ui.idSelecionado(fSetor);
                if (id == null) {
                    dao.inserir(alvo);
                } else {
                    dao.atualizar(alvo);
                }
                return null;
            } catch (IllegalArgumentException e) {
                return e.getMessage();
            } catch (SQLException e) {
                return e.getErrorCode() == 1062 ? "Já existe um funcionário cadastrado com este CPF." : Ui.mensagem(e);
            }
        });
        if (salvou) {
            concluir(id == null ? "Funcionário cadastrado com sucesso." : "Funcionário atualizado com sucesso.");
        }
    }

    @Override
    protected void excluirRegistro(int id) throws SQLException {
        dao.excluir(id);
    }

    
    private static String cpf(String texto) {
        String t = texto == null ? "" : texto.trim();
        if (t.isEmpty()) {
            return null; 
        }
        String digitos = t.replaceAll("\\D", "");
        if (!t.matches("[0-9.\\-\\s]+") || digitos.length() != 11) {
            throw new IllegalArgumentException("CPF inválido. Informe 11 dígitos (ex.: 123.456.789-09).");
        }
        return digitos.substring(0, 3) + "." + digitos.substring(3, 6) + "." + digitos.substring(6, 9)
                + "-" + digitos.substring(9);
    }
}
