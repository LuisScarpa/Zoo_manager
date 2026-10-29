package conexao;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


public final class Conexao {

    private static final String ARQUIVO = "config/db.properties";
    private static Properties config;

    private Conexao() {
    }

    public static Connection getConnection() throws SQLException {
        Properties p = carregarConfig();
        return DriverManager.getConnection(
                p.getProperty("url"), p.getProperty("usuario"), p.getProperty("senha", ""));
    }

    private static Properties carregarConfig() throws SQLException {
        if (config == null) {
            Properties p = new Properties();
            try (InputStreamReader in = new InputStreamReader(new FileInputStream(ARQUIVO), StandardCharsets.UTF_8)) {
                p.load(in);
            } catch (IOException e) {
                throw new SQLException("Arquivo '" + ARQUIVO + "' não encontrado. Copie config/db.properties.example "
                        + "para config/db.properties e informe o usuário e a senha do MySQL.");
            }
            if (vazio(p.getProperty("url")) || vazio(p.getProperty("usuario"))) {
                throw new SQLException("Preencha 'url' e 'usuario' em " + ARQUIVO + ".");
            }
            config = p;
        }
        return config;
    }

    private static boolean vazio(String s) {
        return s == null || s.trim().isEmpty();
    }
}
