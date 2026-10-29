package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.util.function.Supplier;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;


public class FormDialog extends JDialog {

    private final JPanel campos = new JPanel(new GridBagLayout());
    private final JLabel lblErro = new JLabel(" ");
    private int linha = 0;
    private boolean salvo = false;
    private Supplier<String> acao;

    public FormDialog(Component pai, String titulo) {
        super(SwingUtilities.getWindowAncestor(pai), titulo, Dialog.ModalityType.APPLICATION_MODAL);
        setResizable(false);

        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(Ui.TITULO.deriveFont(18f));
        lblTitulo.setForeground(Ui.VERDE_ESCURO);

        campos.setOpaque(false);
        lblErro.setForeground(Ui.PERIGO);

        JButton salvar = Ui.botao("Salvar", Ui.VERDE);
        JButton cancelar = Ui.botaoSecundario("Cancelar");
        salvar.addActionListener(e -> tentarSalvar());
        cancelar.addActionListener(e -> dispose());

        JPanel botoes = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        botoes.setOpaque(false);
        botoes.add(cancelar);
        botoes.add(salvar);

        JPanel rodape = new JPanel(new BorderLayout(0, 10));
        rodape.setOpaque(false);
        rodape.add(lblErro, BorderLayout.NORTH);
        rodape.add(botoes, BorderLayout.SOUTH);

        JPanel raiz = new JPanel(new BorderLayout(0, 14));
        raiz.setBackground(Color.WHITE);
        raiz.setBorder(new EmptyBorder(20, 24, 16, 24));
        raiz.add(lblTitulo, BorderLayout.NORTH);
        raiz.add(campos, BorderLayout.CENTER);
        raiz.add(rodape, BorderLayout.SOUTH);
        setContentPane(raiz);

        getRootPane().setDefaultButton(salvar);
        getRootPane().registerKeyboardAction(e -> dispose(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
                JComponent.WHEN_IN_FOCUSED_WINDOW);
    }

    public void campo(String rotulo, JComponent componente) {
        JLabel l = new JLabel(rotulo);
        l.setFont(Ui.NEGRITO.deriveFont(13f));
        l.setForeground(Ui.TEXTO);

        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0;
        g.gridy = linha;
        g.anchor = GridBagConstraints.WEST;
        g.insets = new Insets(5, 0, 5, 14);
        campos.add(l, g);

        GridBagConstraints g2 = new GridBagConstraints();
        g2.gridx = 1;
        g2.gridy = linha;
        g2.insets = new Insets(5, 0, 5, 0);
        campos.add(componente, g2);
        linha++;
    }

    
    public boolean mostrar(Supplier<String> salvar) {
        this.acao = salvar;
        pack();
        setLocationRelativeTo(getOwner());
        setVisible(true);
        return salvo;
    }

    private void tentarSalvar() {
        String erro = acao.get();
        if (erro == null) {
            salvo = true;
            dispose();
        } else {
            lblErro.setText(Ui.textoLongo(erro));
            pack();
        }
    }
}
