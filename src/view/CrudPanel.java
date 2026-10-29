package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.SQLException;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;


public abstract class CrudPanel extends JPanel {

    protected final DefaultTableModel modelo;
    protected final JTable tabela;
    private final JTextField campoBusca;
    private final String artigoNome; 
    private final String vinculos;   

    
    protected CrudPanel(String titulo, String[] colunas, String artigoNome, String vinculos, String busca) {
        this.artigoNome = artigoNome;
        this.vinculos = vinculos;
        setLayout(new BorderLayout(0, 16));
        setBackground(Ui.FUNDO);
        setBorder(new EmptyBorder(24, 28, 24, 28));

        modelo = new DefaultTableModel(colunas, 0) {
            @Override
            public boolean isCellEditable(int linha, int coluna) {
                return false;
            }

            @Override
            public Class<?> getColumnClass(int coluna) {
                for (int i = 0; i < getRowCount(); i++) {
                    Object v = getValueAt(i, coluna);
                    if (v != null) {
                        return v.getClass();
                    }
                }
                return Object.class;
            }
        };
        tabela = Ui.tabela(modelo);
        tabela.getColumnModel().getColumn(0).setPreferredWidth(70); 
        tabela.getColumnModel().getColumn(0).setMaxWidth(90);
        tabela.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editar();
                }
            }
        });

        
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(Ui.TITULO);
        lblTitulo.setForeground(Ui.VERDE_ESCURO);
        JPanel topo = new JPanel(new BorderLayout());
        topo.setOpaque(false);
        topo.add(lblTitulo, BorderLayout.WEST);

        if (busca != null) {
            campoBusca = Ui.campo();
            campoBusca.setPreferredSize(new Dimension(260, 36));
            campoBusca.setToolTipText("Pesquisar por " + busca);
            campoBusca.addActionListener(e -> atualizar());
            JButton bBuscar = Ui.botao("Pesquisar", Ui.VERDE);
            bBuscar.addActionListener(e -> atualizar());
            JButton bLimpar = Ui.botaoSecundario("Limpar");
            bLimpar.addActionListener(e -> {
                campoBusca.setText("");
                atualizar();
            });
            JLabel dica = new JLabel("Buscar por " + busca + ":");
            dica.setForeground(Ui.TEXTO_SUAVE);
            JPanel painelBusca = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
            painelBusca.setOpaque(false);
            painelBusca.add(dica);
            painelBusca.add(campoBusca);
            painelBusca.add(bBuscar);
            painelBusca.add(bLimpar);
            topo.add(painelBusca, BorderLayout.EAST);
        } else {
            campoBusca = null;
        }
        add(topo, BorderLayout.NORTH);

        // Centro: tabela
        JScrollPane rolagem = new JScrollPane(tabela);
        rolagem.setBorder(new LineBorder(Ui.BORDA));
        rolagem.getViewport().setBackground(Color.WHITE);
        add(rolagem, BorderLayout.CENTER);

        // Rodapé: botões
        JButton bNovo = Ui.botao("Novo", Ui.VERDE);
        JButton bEditar = Ui.botaoSecundario("Editar");
        JButton bExcluir = Ui.botao("Excluir", Ui.PERIGO);
        JButton bAtualizar = Ui.botaoSecundario("Atualizar");
        bNovo.addActionListener(e -> abrirFormulario(null));
        bEditar.addActionListener(e -> editar());
        bExcluir.addActionListener(e -> excluir());
        bAtualizar.addActionListener(e -> atualizar());
        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        botoes.setOpaque(false);
        botoes.add(bNovo);
        botoes.add(bEditar);
        botoes.add(bExcluir);
        botoes.add(bAtualizar);
        add(botoes, BorderLayout.SOUTH);
    }

    
    protected abstract void carregar(String filtro) throws SQLException;

    
    protected abstract void abrirFormulario(Integer id);

    protected abstract void excluirRegistro(int id) throws SQLException;

    protected void linha(Object... valores) {
        modelo.addRow(valores);
    }

    
    public void atualizar() {
        modelo.setRowCount(0);
        try {
            carregar(campoBusca == null ? null : campoBusca.getText().trim());
        } catch (SQLException e) {
            Ui.erro(this, e);
        }
    }

   
    protected void concluir(String mensagem) {
        Ui.info(this, mensagem);
        atualizar();
    }

    private void editar() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            Ui.aviso(this, "Selecione um registro na tabela para editar.");
            return;
        }
        abrirFormulario((Integer) modelo.getValueAt(tabela.convertRowIndexToModel(linha), 0));
    }

    private void excluir() {
        int linha = tabela.getSelectedRow();
        if (linha < 0) {
            Ui.aviso(this, "Selecione um registro na tabela para excluir.");
            return;
        }
        int m = tabela.convertRowIndexToModel(linha);
        int id = (Integer) modelo.getValueAt(m, 0);
        Object nome = modelo.getValueAt(m, 1);
        String descricao = artigoNome + " \"" + nome + "\" (código " + id + ")";

        if (!Ui.confirmarExclusao(this, "Excluir " + descricao + "?\nEsta ação não pode ser desfeita.")) {
            return;
        }
        try {
            excluirRegistro(id);
            Ui.info(this, "Registro excluído com sucesso.");
            atualizar();
        } catch (SQLException e) {
            if (e.getErrorCode() == 1451) {
                Ui.erro(this, "Não foi possível excluir " + descricao + ".\nExistem registros vinculados a ele ("
                        + vinculos + "). Remova ou altere esses vínculos antes de excluir.");
            } else {
                Ui.erro(this, e);
            }
        }
    }
}
