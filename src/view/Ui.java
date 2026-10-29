package view;

import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.plaf.FontUIResource;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableModel;
import model.Opcao;


public final class Ui {

    
    public static final Color VERDE_ESCURO = new Color(0x1B4332);
    public static final Color VERDE = new Color(0x2D6A4F);
    public static final Color VERDE_SUAVE = new Color(0xB7E4C7);
    public static final Color FUNDO = new Color(0xF4F7F4);
    public static final Color BORDA = new Color(0xD5DDD8);
    public static final Color TEXTO = new Color(0x22302A);
    public static final Color TEXTO_SUAVE = new Color(0x6B7A72);
    public static final Color PERIGO = new Color(0xA94438);

    public static final Font FONTE = new Font("SansSerif", Font.PLAIN, 14);
    public static final Font NEGRITO = FONTE.deriveFont(Font.BOLD);
    public static final Font TITULO = FONTE.deriveFont(Font.BOLD, 24f);

    private Ui() {
    }

    

    public static void configurar() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception ignorada) {
            
        }
        for (Object chave : Collections.list(UIManager.getDefaults().keys())) {
            if (UIManager.get(chave) instanceof FontUIResource) {
                UIManager.put(chave, new FontUIResource(FONTE));
            }
        }
        UIManager.put("OptionPane.yesButtonText", "Sim");
        UIManager.put("OptionPane.noButtonText", "Não");
        UIManager.put("OptionPane.okButtonText", "OK");
        UIManager.put("OptionPane.cancelButtonText", "Cancelar");
        UIManager.put("OptionPane.background", Color.WHITE);
        for (String c : new String[] {"TextField", "PasswordField", "ComboBox", "List", "Table"}) {
            UIManager.put(c + ".selectionBackground", VERDE_SUAVE);
            UIManager.put(c + ".selectionForeground", TEXTO);
        }
    }

    

    
    public static class Painel extends JPanel {
        public Painel() {
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.WHITE);
            g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
            g2.setColor(BORDA);
            g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    private static Color mix(Color a, Color b, float f) {
        return new Color(Math.round(a.getRed() * (1 - f) + b.getRed() * f),
                Math.round(a.getGreen() * (1 - f) + b.getGreen() * f),
                Math.round(a.getBlue() * (1 - f) + b.getBlue() * f));
    }

    public static JButton botao(String texto, Color fundo) {
        return criarBotao(texto, fundo, Color.WHITE);
    }

    public static JButton botaoSecundario(String texto) {
        return criarBotao(texto, VERDE_SUAVE, VERDE_ESCURO);
    }

    private static JButton criarBotao(String texto, Color fundo, Color letra) {
        JButton b = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Color base = getBackground();
                Color cor = !isEnabled() ? mix(base, Color.WHITE, 0.5f)
                        : getModel().isPressed() ? mix(base, Color.BLACK, 0.2f)
                        : getModel().isRollover() ? mix(base, Color.WHITE, 0.15f) : base;
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(cor);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setBackground(fundo);
        b.setForeground(letra);
        b.setFont(NEGRITO);
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setOpaque(false);
        b.setRolloverEnabled(true);
        b.setBorder(new EmptyBorder(9, 20, 9, 20));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }


    public static JButton botaoMenu(String texto) {
        JButton b = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (Boolean.TRUE.equals(getClientProperty("ativo"))) {
                    g2.setColor(VERDE);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                } else if (getModel().isRollover()) {
                    g2.setColor(mix(VERDE_ESCURO, Color.WHITE, 0.10f));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        b.setHorizontalAlignment(SwingConstants.LEFT);
        b.setForeground(Color.WHITE);
        b.setFont(FONTE.deriveFont(15f));
        b.setContentAreaFilled(false);
        b.setBorderPainted(false);
        b.setFocusPainted(false);
        b.setOpaque(false);
        b.setRolloverEnabled(true);
        b.setBorder(new EmptyBorder(11, 16, 11, 16));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        return b;
    }

    public static void marcarMenu(JButton botao, boolean ativo) {
        botao.putClientProperty("ativo", ativo);
        botao.repaint();
    }

    private static void estilo(JTextField t) {
        t.setFont(FONTE);
        t.setForeground(TEXTO);
        t.setBackground(Color.WHITE);
        t.setCaretColor(VERDE);
        t.setBorder(new CompoundBorder(new LineBorder(BORDA, 1, true), new EmptyBorder(5, 9, 5, 9)));
        t.setPreferredSize(new Dimension(340, 36));
    }

    public static JTextField campo() {
        JTextField t = new JTextField();
        estilo(t);
        return t;
    }

    public static JPasswordField senha() {
        JPasswordField t = new JPasswordField();
        estilo(t);
        return t;
    }

    public static JTable tabela(TableModel modelo) {
        JTable t = new JTable(modelo) {
            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer r, int linha, int coluna) {
                Component c = super.prepareRenderer(r, linha, coluna);
                if (!isRowSelected(linha)) {
                    c.setBackground(linha % 2 == 0 ? Color.WHITE : new Color(0xF4F8F5));
                    c.setForeground(TEXTO);
                }
                if (c instanceof JComponent) {
                    ((JComponent) c).setBorder(new EmptyBorder(0, 10, 0, 10));
                }
                return c;
            }
        };
        t.setRowHeight(32);
        t.setShowVerticalLines(false);
        t.setShowHorizontalLines(false);
        t.setIntercellSpacing(new Dimension(0, 0));
        t.setFillsViewportHeight(true);
        t.setAutoCreateRowSorter(true);
        t.setSelectionBackground(VERDE_SUAVE);
        t.setSelectionForeground(TEXTO);

        JTableHeader h = t.getTableHeader();
        h.setReorderingAllowed(false);
        h.setPreferredSize(new Dimension(0, 36));
        h.setDefaultRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tb, Object v, boolean sel, boolean foco, int l, int c) {
                super.getTableCellRendererComponent(tb, v, false, false, l, c);
                setBackground(VERDE_ESCURO);
                setForeground(Color.WHITE);
                setFont(NEGRITO);
                setHorizontalAlignment(SwingConstants.LEFT);
                setBorder(new EmptyBorder(0, 10, 0, 10));
                return this;
            }
        });
        return t;
    }

  

    public static JComboBox<Opcao> comboOpcoes(List<Opcao> itens, String semValor) {
        DefaultComboBoxModel<Opcao> modelo = new DefaultComboBoxModel<>();
        if (semValor != null) {
            modelo.addElement(new Opcao(null, semValor));
        }
        for (Opcao o : itens) {
            modelo.addElement(o);
        }
        JComboBox<Opcao> c = new JComboBox<>(modelo);
        estiloCombo(c);
        return c;
    }

    public static void selecionar(JComboBox<Opcao> combo, Integer id) {
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (Objects.equals(combo.getItemAt(i).id, id)) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }

    public static Integer idSelecionado(JComboBox<Opcao> combo) {
        Opcao o = (Opcao) combo.getSelectedItem();
        return o == null ? null : o.id;
    }

    public static JComboBox<String> comboTexto(boolean editavel, List<String> itens) {
        JComboBox<String> c = new JComboBox<>(itens.toArray(new String[0]));
        c.setEditable(editavel);
        estiloCombo(c);
        return c;
    }

    public static void selecionarTexto(JComboBox<String> combo, String valor) {
        if (valor == null) {
            if (combo.isEditable()) {
                combo.setSelectedItem(null);
            }
            return;
        }
        if (!combo.isEditable()) {
            boolean existe = false;
            for (int i = 0; i < combo.getItemCount(); i++) {
                existe |= valor.equals(combo.getItemAt(i));
            }
            if (!existe) {
                combo.addItem(valor);
            }
        }
        combo.setSelectedItem(valor);
    }

  
    public static String textoCombo(JComboBox<String> combo) {
        Object v = combo.isEditable() ? combo.getEditor().getItem() : combo.getSelectedItem();
        return v == null ? "" : v.toString().trim();
    }

    private static void estiloCombo(JComboBox<?> c) {
        c.setBackground(Color.WHITE);
        c.setForeground(TEXTO);
        c.setPreferredSize(new Dimension(340, 36));
    }

   

    public static String obrigatorio(String valor, String campo, int max) {
        String v = valor == null ? "" : valor.trim();
        if (v.isEmpty()) {
            throw new IllegalArgumentException("O campo \"" + campo + "\" é obrigatório.");
        }
        if (v.length() > max) {
            throw new IllegalArgumentException("O campo \"" + campo + "\" aceita no máximo " + max + " caracteres.");
        }
        return v;
    }

    
    public static String opcional(String valor, String campo, int max) {
        String v = valor == null ? "" : valor.trim();
        return v.isEmpty() ? null : obrigatorio(v, campo, max);
    }

    public static Integer inteiro(String valor, String campo, int min, int max, boolean obrigatorio) {
        String v = valor == null ? "" : valor.trim();
        if (v.isEmpty()) {
            if (obrigatorio) {
                throw new IllegalArgumentException("O campo \"" + campo + "\" é obrigatório.");
            }
            return null;
        }
        int n;
        try {
            n = Integer.parseInt(v);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("O campo \"" + campo + "\" deve ser um número inteiro.");
        }
        if (n < min || n > max) {
            throw new IllegalArgumentException("O campo \"" + campo + "\" deve estar entre " + min + " e " + max + ".");
        }
        return n;
    }

   

    private static String html(String texto) {
        return "<html><body style='width:340px'>"
                + texto.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\n", "<br>")
                + "</body></html>";
    }

    
    public static String textoLongo(String texto) {
        return html(texto);
    }

    public static void info(Component pai, String msg) {
        JOptionPane.showMessageDialog(pai, html(msg), "Sucesso", JOptionPane.INFORMATION_MESSAGE);
    }

    public static void aviso(Component pai, String msg) {
        JOptionPane.showMessageDialog(pai, html(msg), "Atenção", JOptionPane.WARNING_MESSAGE);
    }

    public static void erro(Component pai, String msg) {
        JOptionPane.showMessageDialog(pai, html(msg), "Erro", JOptionPane.ERROR_MESSAGE);
    }

    public static void erro(Component pai, SQLException e) {
        erro(pai, mensagem(e));
    }

    public static boolean confirmarExclusao(Component pai, String msg) {
        Object[] opcoes = {"Excluir", "Cancelar"};
        return JOptionPane.showOptionDialog(pai, html(msg), "Confirmar exclusão", JOptionPane.DEFAULT_OPTION,
                JOptionPane.WARNING_MESSAGE, null, opcoes, opcoes[1]) == 0;
    }

   
    public static String mensagem(SQLException e) {
        String estado = e.getSQLState() == null ? "" : e.getSQLState();
        switch (e.getErrorCode()) {
            case 1451:
                return "Não é possível concluir: este registro está vinculado a outros registros.";
            case 1452:
                return "O setor, jaula, funcionário ou alimento selecionado não existe mais. Atualize a lista e tente novamente.";
            case 1062:
                return "Já existe um registro com este valor (campo que não pode se repetir).";
            case 1406:
                return "Algum texto é maior do que o permitido.";
            case 3819:
            case 4025:
                return "Algum valor informado viola as regras do banco (verifique números negativos ou zero).";
            case 1045:
                return "O MySQL recusou o usuário ou a senha de config/db.properties.";
            case 1049:
                return "O banco 'zoo_manager' não foi encontrado no servidor MySQL.";
            default:
                break;
        }
        if ("45000".equals(estado)) {
            return e.getMessage(); 
        }
        if (estado.startsWith("08")) {
            return "Não foi possível conectar ao MySQL. Verifique se o servidor está ligado e se a 'url' em "
                    + "config/db.properties está correta.";
        }
        return e.getSQLState() == null ? e.getMessage() : "Erro no banco de dados: " + e.getMessage();
    }
}
