package view;

import dao.AnimalDao;
import dao.EspecieAlimentoDao;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableModel;
import model.EspecieAlimento;


public class AlimentacaoPanel extends JPanel {

    private final AnimalDao animalDao = new AnimalDao();
    private final EspecieAlimentoDao dao = new EspecieAlimentoDao();
    private final JComboBox<String> comboEspecie = new JComboBox<>();
    private final JLabel lblInfo = new JLabel(" ");
    private final DefaultTableModel modelo =
            new DefaultTableModel(new String[] {"Alimento", "Tipo", "Quantidade diária por animal", "Estoque atual"}, 0) {
                @Override
                public boolean isCellEditable(int linha, int coluna) {
                    return false;
                }
            };
    private boolean carregando = false;

    public AlimentacaoPanel() {
        setLayout(new BorderLayout(0, 16));
        setBackground(Ui.FUNDO);
        setBorder(new EmptyBorder(24, 28, 24, 28));

        JLabel titulo = new JLabel("Alimentação por espécie");
        titulo.setFont(Ui.TITULO);
        titulo.setForeground(Ui.VERDE_ESCURO);

        JLabel rotulo = new JLabel("Espécie:");
        rotulo.setForeground(Ui.TEXTO_SUAVE);
        comboEspecie.setPreferredSize(new java.awt.Dimension(260, 36));
        comboEspecie.setBackground(Color.WHITE);
        comboEspecie.addActionListener(e -> {
            if (!carregando) {
                consultar();
            }
        });
        JButton bAtualizar = Ui.botaoSecundario("Atualizar");
        bAtualizar.addActionListener(e -> atualizar());

        JPanel filtro = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        filtro.setOpaque(false);
        filtro.add(rotulo);
        filtro.add(comboEspecie);
        filtro.add(bAtualizar);

        lblInfo.setForeground(Ui.TEXTO_SUAVE);

        JPanel linha2 = new JPanel(new BorderLayout(0, 8));
        linha2.setOpaque(false);
        linha2.add(filtro, BorderLayout.NORTH);
        linha2.add(lblInfo, BorderLayout.SOUTH);

        JPanel topo = new JPanel(new BorderLayout(0, 14));
        topo.setOpaque(false);
        topo.add(titulo, BorderLayout.NORTH);
        topo.add(linha2, BorderLayout.CENTER);
        add(topo, BorderLayout.NORTH);

        JTable tabela = Ui.tabela(modelo);
        JScrollPane rolagem = new JScrollPane(tabela);
        rolagem.setBorder(new LineBorder(Ui.BORDA));
        rolagem.getViewport().setBackground(Color.WHITE);
        add(rolagem, BorderLayout.CENTER);
    }

   
    public void atualizar() {
        carregando = true;
        try {
            Object anterior = comboEspecie.getSelectedItem();
            comboEspecie.removeAllItems();
            for (String e : animalDao.especies()) {
                comboEspecie.addItem(e);
            }
            if (anterior != null) {
                comboEspecie.setSelectedItem(anterior);
            }
        } catch (SQLException e) {
            Ui.erro(this, e);
        } finally {
            carregando = false;
        }
        consultar();
    }

    private void consultar() {
        modelo.setRowCount(0);
        String especie = (String) comboEspecie.getSelectedItem();
        if (especie == null) {
            lblInfo.setText("Nenhuma espécie cadastrada.");
            return;
        }
        try {
            List<EspecieAlimento> lista = dao.listarPorEspecie(especie);
            for (EspecieAlimento e : lista) {
                modelo.addRow(new Object[] {e.alimentoNome, e.alimentoTipo,
                        String.format(new Locale("pt", "BR"), "%.2f kg", e.quantidadeDiaria), e.estoque + " kg"});
            }
            lblInfo.setText(lista.isEmpty()
                    ? "Nenhum alimento cadastrado para a espécie " + especie + "."
                    : lista.size() + " alimento(s) indicado(s) para " + especie + ".");
        } catch (SQLException e) {
            Ui.erro(this, e);
        }
    }
}
