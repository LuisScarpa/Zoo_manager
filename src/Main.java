import java.io.Console;
import java.util.Scanner;
import javax.swing.SwingUtilities;
import org.mindrot.jbcrypt.BCrypt;
import view.LoginFrame;
import view.Ui;

public class Main {

    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("--hash")) {
            gerarComandoUsuario(args);
            return;
        }
        SwingUtilities.invokeLater(() -> {
            Ui.configurar();
            new LoginFrame().setVisible(true);
        });
    }

   
    private static void gerarComandoUsuario(String[] args) {
        String usuario = args.length > 1 ? args[1] : "admin";
        String perfil = args.length > 2 ? args[2].toUpperCase() : "ADMINISTRADOR";
        if (!usuario.matches("[A-Za-z0-9_.-]{1,50}") || !(perfil.equals("ADMINISTRADOR") || perfil.equals("FUNCIONARIO"))) {
            System.out.println("Uso: Main --hash [usuario] [ADMINISTRADOR|FUNCIONARIO]");
            System.out.println("O usuário deve ter só letras, números, ponto, hífen ou sublinhado (até 50 caracteres).");
            return;
        }
        String senha;
        Console console = System.console();
        if (console != null) {
            senha = new String(console.readPassword("Senha para o usuário '%s': ", usuario));
        } else {
            System.out.print("Senha para o usuário '" + usuario + "': ");
            senha = new Scanner(System.in).nextLine();
        }
        if (senha.isEmpty()) {
            System.out.println("A senha não pode ser vazia.");
            return;
        }
        System.out.println();
        System.out.println("Execute no MySQL Workbench (banco zoo_manager):");
        System.out.println();
        System.out.println("INSERT INTO Usuario (id_funcionario, usuario, senha_hash, perfil, ativo)");
        System.out.println("VALUES (NULL, '" + usuario + "', '" + BCrypt.hashpw(senha, BCrypt.gensalt(12)) + "', '"
                + perfil + "', TRUE);");
    }
}
