package view;

import dao.UsuarioDao;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.sql.SQLException;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import model.Usuario;

public class LoginFrame extends JFrame {

    private final JTextField campoUsuario = Ui.campo();
    private final JPasswordField campoSenha = Ui.senha();
    private final JLabel lblErro = new JLabel(" ");

    public LoginFrame() {
        super("ZooManager - Login");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(520, 620);
        setLocationRelativeTo(null);

        JPanel fundo = new JPanel(new GridBagLayout());
        fundo.setBackground(Ui.VERDE_ESCURO);

        Ui.Painel cartao = new Ui.Painel();
        cartao.setLayout(new BoxLayout(cartao, BoxLayout.Y_AXIS));
        cartao.setBorder(new EmptyBorder(34, 38, 34, 38));
        cartao.setPreferredSize(new Dimension(400, 440));

        JLabel titulo = new JLabel("ZooManager");
        titulo.setFont(Ui.TITULO.deriveFont(32f));
        titulo.setForeground(Ui.VERDE_ESCURO);
        JLabel subtitulo = new JLabel("Sistema de gerenciamento de zoológico");
        subtitulo.setForeground(Ui.TEXTO_SUAVE);

        JButton entrar = Ui.botao("Entrar", Ui.VERDE);
        entrar.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        entrar.addActionListener(e -> entrar());
        campoUsuario.addActionListener(e -> campoSenha.requestFocusInWindow());
        campoSenha.addActionListener(e -> entrar());
        campoUsuario.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        campoSenha.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        lblErro.setForeground(Ui.PERIGO);

        adicionar(cartao, titulo, 4);
        adicionar(cartao, subtitulo, 28);
        adicionar(cartao, rotulo("Usuário"), 6);
        adicionar(cartao, campoUsuario, 14);
        adicionar(cartao, rotulo("Senha"), 6);
        adicionar(cartao, campoSenha, 10);
        adicionar(cartao, lblErro, 14);
        adicionar(cartao, entrar, 0);

        fundo.add(cartao);
        setContentPane(fundo);
        getRootPane().setDefaultButton(entrar);
    }

    private static JLabel rotulo(String texto) {
        JLabel l = new JLabel(texto);
        l.setFont(Ui.NEGRITO.deriveFont(13f));
        l.setForeground(Ui.TEXTO);
        return l;
    }

    private static void adicionar(JPanel painel, javax.swing.JComponent c, int espacoDepois) {
        c.setAlignmentX(Component.LEFT_ALIGNMENT);
        painel.add(c);
        if (espacoDepois > 0) {
            painel.add(javax.swing.Box.createVerticalStrut(espacoDepois));
        }
    }

    private void entrar() {
        String usuario = campoUsuario.getText().trim();
        String senha = new String(campoSenha.getPassword());
        if (usuario.isEmpty() || senha.isEmpty()) {
            mostrarErro("Informe o usuário e a senha.");
            return;
        }
        try {
            Usuario u = new UsuarioDao().autenticar(usuario, senha);
            if (u == null) {
                mostrarErro("Usuário ou senha incorretos.");
                campoSenha.setText("");
                return;
            }
            if (!u.ativo) {
                mostrarErro("Este usuário está inativo. Procure um administrador.");
                return;
            }
            dispose();
            new MainFrame(u).setVisible(true);
        } catch (SQLException e) {
            mostrarErro(Ui.mensagem(e));
        }
    }

    private void mostrarErro(String msg) {
        lblErro.setText("<html><body style='width:300px'>" + msg.replace("&", "&amp;").replace("<", "&lt;") + "</body></html>");
    }
}
