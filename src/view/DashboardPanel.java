package view;

import dao.DashboardDao;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.sql.SQLException;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import model.Usuario;

public class DashboardPanel extends JPanel {

    private static final String[] ROTULOS = {"Animais", "Espécies", "Jaulas", "Setores", "Funcionários", "Alimentos"};

    private final DashboardDao dao = new DashboardDao();
    private final JLabel[] valores = new JLabel[ROTULOS.length];

    public DashboardPanel(Usuario usuario) {
        setLayout(new BorderLayout(0, 24));
        setBackground(Ui.FUNDO);
        setBorder(new EmptyBorder(24, 28, 24, 28));

        JLabel titulo = new JLabel("Dashboard");
        titulo.setFont(Ui.TITULO);
        titulo.setForeground(Ui.VERDE_ESCURO);
        String nome = usuario.funcionarioNome != null ? usuario.funcionarioNome : usuario.usuario;
        JLabel subtitulo = new JLabel("Olá, " + nome + ". Este é o resumo do zoológico.");
        subtitulo.setForeground(Ui.TEXTO_SUAVE);
        JPanel topo = new JPanel(new BorderLayout(0, 4));
        topo.setOpaque(false);
        topo.add(titulo, BorderLayout.NORTH);
        topo.add(subtitulo, BorderLayout.SOUTH);
        add(topo, BorderLayout.NORTH);

        JPanel grade = new JPanel(new GridLayout(2, 3, 20, 20));
        grade.setOpaque(false);
        for (int i = 0; i < ROTULOS.length; i++) {
            Ui.Painel cartao = new Ui.Painel();
            cartao.setLayout(new BorderLayout(0, 8));
            cartao.setBorder(new EmptyBorder(22, 26, 22, 26));
            cartao.setPreferredSize(new Dimension(200, 130));

            JLabel rotulo = new JLabel(ROTULOS[i].toUpperCase());
            rotulo.setFont(Ui.NEGRITO.deriveFont(12f));
            rotulo.setForeground(Ui.TEXTO_SUAVE);
            valores[i] = new JLabel("-");
            valores[i].setFont(Ui.TITULO.deriveFont(42f));
            valores[i].setForeground(Ui.VERDE);

            cartao.add(rotulo, BorderLayout.NORTH);
            cartao.add(valores[i], BorderLayout.CENTER);
            grade.add(cartao);
        }
        JPanel centro = new JPanel(new BorderLayout());
        centro.setOpaque(false);
        centro.add(grade, BorderLayout.NORTH);
        add(centro, BorderLayout.CENTER);
    }

    public void atualizar() {
        try {
            int[] resumo = dao.resumo();
            for (int i = 0; i < valores.length; i++) {
                valores[i].setText(String.valueOf(resumo[i]));
            }
        } catch (SQLException e) {
            Ui.erro(this, e);
        }
    }
}
