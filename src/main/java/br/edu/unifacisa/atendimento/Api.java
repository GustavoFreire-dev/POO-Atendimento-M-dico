
package br.edu.unifacisa.atendimento;

import br.edu.unifacisa.atendimento.db.ConexaoBD;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;

import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.io.IOException;
import java.io.OutputStream;

public class Api {

    public static void main(String[] args) throws Exception {

        // CARREGAR O DRIVER MYSQL
        Class.forName("com.mysql.cj.jdbc.Driver");

        // REGISTRAR O DRIVER NO DRIVER MANAGER
        Driver mysqlDriver = new com.mysql.cj.jdbc.Driver();

        DriverManager.registerDriver(new Driver() {

            @Override
            public Connection connect(String url, java.util.Properties info)
                    throws SQLException {
                return mysqlDriver.connect(url, info);
            }

            @Override
            public boolean acceptsURL(String url)
                    throws SQLException {
                return mysqlDriver.acceptsURL(url);
            }

            @Override
            public DriverPropertyInfo[] getPropertyInfo(
                    String url, java.util.Properties info)
                    throws SQLException {
                return mysqlDriver.getPropertyInfo(url, info);
            }

            @Override
            public int getMajorVersion() {
                return mysqlDriver.getMajorVersion();
            }

            @Override
            public int getMinorVersion() {
                return mysqlDriver.getMinorVersion();
            }

            @Override
            public boolean jdbcCompliant() {
                return mysqlDriver.jdbcCompliant();
            }

            @Override
            public java.util.logging.Logger getParentLogger()
                    throws SQLFeatureNotSupportedException {
                return mysqlDriver.getParentLogger();
            }
        });

        // CRIAR SERVIDOR HTTP
        HttpServer servidor = HttpServer.create(
            new InetSocketAddress("127.0.0.1", 8080), 0
        );

        // ENDPOINTS
        servidor.createContext("/usuarios", exchange ->
            consultar(exchange, "usuarios", null)
        );

        servidor.createContext("/pacientes", exchange ->
            consultar(exchange, "usuarios", "paciente")
        );

        servidor.createContext("/medicos", exchange ->
            consultar(exchange, "usuarios", "medico")
        );

        servidor.createContext("/consultas", exchange ->
            consultar(exchange, "consultas", null)
        );

        servidor.start();

        System.out.println("\n==================================");
        System.out.println(" API JAVA INICIADA COM SUCESSO!");
        System.out.println("==================================");

        System.out.println("GET http://127.0.0.1:8080/usuarios");
        System.out.println("GET http://127.0.0.1:8080/pacientes");
        System.out.println("GET http://127.0.0.1:8080/medicos");
        System.out.println("GET http://127.0.0.1:8080/consultas");
    }

    // ==========================================
    // CONSULTAR DADOS DO MYSQL
    // ==========================================

    private static void consultar(
            HttpExchange exchange,
            String tabela,
            String perfil
    ) throws IOException {

        String caminho = exchange.getRequestURI().getPath();

        if (!exchange.getRequestMethod().equals("GET")) {

            exchange.getResponseHeaders().set("Allow", "GET");

            responder(exchange, 405,
                "{\"erro\":\"Metodo nao permitido\"}");

            return;
        }

        String rota = switch (caminho) {
            case "/usuarios" -> "usuarios";
            case "/pacientes" -> "pacientes";
            case "/medicos" -> "medicos";
            case "/consultas" -> "consultas";
            default -> null;
        };

        if (rota == null) {

            responder(exchange, 404,
                "{\"erro\":\"Rota nao encontrada\"}");

            return;
        }

        String sql;

        if (rota.equals("consultas")) {

            sql = """
                SELECT c.id,
                       p.nome AS paciente,
                       m.nome AS medico,
                       c.data_hora,
                       c.status,
                       c.observacoes
                FROM consultas c
                JOIN usuarios p ON p.id = c.paciente_id
                JOIN usuarios m ON m.id = c.medico_id
                ORDER BY c.id
                """;

        } else {

            sql = """
                SELECT id, nome, email, perfil,
                       crm, especialidade, cpf, telefone
                FROM usuarios
                """;

            if (perfil != null) {
                sql += " WHERE perfil = ?";
            }

            sql += " ORDER BY id";
        }

        try (
            Connection conn = ConexaoBD.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            if (perfil != null) {
                ps.setString(1, perfil);
            }

            try (ResultSet rs = ps.executeQuery()) {

                StringBuilder json = new StringBuilder("[");

                boolean primeiro = true;

                while (rs.next()) {

                    if (!primeiro) {
                        json.append(",");
                    }

                    primeiro = false;

                    if (rota.equals("consultas")) {

                        json.append("{")
                            .append("\"id\":")
                            .append(rs.getInt("id"))

                            .append(",\"paciente\":")
                            .append(valor(rs.getString("paciente")))

                            .append(",\"medico\":")
                            .append(valor(rs.getString("medico")))

                            .append(",\"dataHora\":")
                            .append(valor(rs.getString("data_hora")))

                            .append(",\"status\":")
                            .append(valor(rs.getString("status")))

                            .append(",\"observacoes\":")
                            .append(valor(rs.getString("observacoes")))

                            .append("}");

                    } else {

                        json.append("{")
                            .append("\"id\":")
                            .append(rs.getInt("id"))

                            .append(",\"nome\":")
                            .append(valor(rs.getString("nome")))

                            .append(",\"email\":")
                            .append(valor(rs.getString("email")))

                            .append(",\"perfil\":")
                            .append(valor(rs.getString("perfil")))

                            .append(",\"crm\":")
                            .append(valor(rs.getString("crm")))

                            .append(",\"especialidade\":")
                            .append(valor(rs.getString("especialidade")))

                            .append(",\"cpf\":")
                            .append(valor(rs.getString("cpf")))

                            .append(",\"telefone\":")
                            .append(valor(rs.getString("telefone")))

                            .append("}");
                    }
                }

                json.append("]");

                responder(exchange, 200, json.toString());
            }

        } catch (SQLException e) {

            System.err.println("ERRO MYSQL: " + e.getMessage());

            responder(exchange, 500,
                "{\"erro\":\"Falha ao consultar banco de dados\"}");
        }
    }

    // ==========================================
    // CONVERTER VALORES PARA JSON
    // ==========================================

    private static String valor(String texto) {

        if (texto == null) {
            return "null";
        }

        return "\"" + texto
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
            .replace("\b", "\\b")
            .replace("\f", "\\f")
            + "\"";
    }

    // ==========================================
    // ENVIAR RESPOSTA HTTP
    // ==========================================

    private static void responder(
            HttpExchange exchange,
            int status,
            String resposta
    ) throws IOException {

        byte[] bytes = resposta.getBytes(
            StandardCharsets.UTF_8
        );

        exchange.getResponseHeaders().set(
            "Content-Type",
            "application/json; charset=UTF-8"
        );

        exchange.sendResponseHeaders(status, bytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }
}
