package view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import model.Usuario;


public class MainFrame extends JFrame {

    private final CardLayout cartas = new CardLayout();
    private final JPanel conteudo = new JPanel(cartas);
    private final JPanel menu = new JPanel();
    private final Map<String, JButton> botoes = new HashMap<>();
    private final Map<String, Runnable> atualizadores = new HashMap<>();

    public MainFrame(Usuario usuario) {
        super("ZooManager");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1260, 760);
        setMinimumSize(new Dimension(1050, 640));
        setLocationRelativeTo(null);

        // Menu lateral
        JLabel marca = new JLabel("ZooManager");
        marca.setFont(Ui.TITULO);
        marca.setForeground(Color.WHITE);
        JLabel marcaSub = new JLabel("Gestão do zoológico");
        marcaSub.setForeground(Ui.VERDE_SUAVE);
        JPanel cabecalho = new JPanel(new BorderLayout(0, 2));
        cabecalho.setOpaque(false);
        cabecalho.setBorder(new EmptyBorder(26, 24, 22, 24));
        cabecalho.add(marca, BorderLayout.NORTH);
        cabecalho.add(marcaSub, BorderLayout.SOUTH);

        menu.setOpaque(false);
        menu.setLayout(new BoxLayout(menu, BoxLayout.Y_AXIS));
        menu.setBorder(new EmptyBorder(0, 12, 0, 12));

        JLabel lblUsuario = new JLabel(usuario.usuario);
        lblUsuario.setFont(Ui.NEGRITO);
        lblUsuario.setForeground(Color.WHITE);
        JLabel lblPerfil = new JLabel(usuario.perfil);
        lblPerfil.setForeground(Ui.VERDE_SUAVE);
        JButton sair = Ui.botaoSecundario("Sair");
        sair.addActionListener(e -> {
            dispose();
            new LoginFrame().setVisible(true);
        });
        JPanel rodape = new JPanel();
        rodape.setOpaque(false);
        rodape.setLayout(new BoxLayout(rodape, BoxLayout.Y_AXIS));
        rodape.setBorder(new EmptyBorder(16, 24, 24, 24));
        rodape.add(lblUsuario);
        rodape.add(lblPerfil);
        rodape.add(Box.createVerticalStrut(12));
        rodape.add(sair);

        JPanel lateral = new JPanel(new BorderLayout());
        lateral.setBackground(Ui.VERDE_ESCURO);
        lateral.setPreferredSize(new Dimension(250, 0));
        lateral.add(cabecalho, BorderLayout.NORTH);
        lateral.add(menu, BorderLayout.CENTER);
        lateral.add(rodape, BorderLayout.SOUTH);

        // Telas
        conteudo.setBackground(Ui.FUNDO);
        DashboardPanel dashboard = new DashboardPanel(usuario);
        AnimalPanel animais = new AnimalPanel();
        JaulaPanel jaulas = new JaulaPanel();
        SetorPanel setores = new SetorPanel();
        FuncionarioPanel funcionarios = new FuncionarioPanel();
        AlimentoPanel alimentos = new AlimentoPanel();
        AlimentacaoPanel alimentacao = new AlimentacaoPanel();

        adicionar("Dashboard", dashboard, dashboard::atualizar);
        adicionar("Animais", animais, animais::atualizar);
        adicionar("Jaulas", jaulas, jaulas::atualizar);
        adicionar("Setores", setores, setores::atualizar);
        adicionar("Funcionários", funcionarios, funcionarios::atualizar);
        adicionar("Alimentos", alimentos, alimentos::atualizar);
        adicionar("Alimentação por espécie", alimentacao, alimentacao::atualizar);

        add(lateral, BorderLayout.WEST);
        add(conteudo, BorderLayout.CENTER);
        mostrar("Dashboard");
    }

    private void adicionar(String nome, JPanel painel, Runnable atualizar) {
        JButton b = Ui.botaoMenu(nome);
        b.addActionListener(e -> mostrar(nome));
        botoes.put(nome, b);
        atualizadores.put(nome, atualizar);
        menu.add(b);
        menu.add(Box.createVerticalStrut(4));
        conteudo.add(painel, nome);
    }

    private void mostrar(String nome) {
        botoes.forEach((n, b) -> Ui.marcarMenu(b, n.equals(nome)));
        cartas.show(conteudo, nome);
        atualizadores.get(nome).run(); 
    }
}
