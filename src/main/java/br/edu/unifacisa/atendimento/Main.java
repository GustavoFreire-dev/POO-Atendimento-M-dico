
package br.edu.unifacisa.atendimento;

import br.edu.unifacisa.atendimento.db.ConexaoBD;
import br.edu.unifacisa.atendimento.model.*;
import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;

import java.sql.*;
import java.util.*;
import java.time.LocalDateTime;

public class Main {

    public static void main(String[] args) {

        try (Connection conn = ConexaoBD.getConnection()) {

            System.out.println("CONEXÃO MYSQL REALIZADA COM SUCESSO!");

            List<Usuario> usuarios = carregarUsuarios(conn);

            System.out.println("\n=== USUÁRIOS (POLIMORFISMO) ===");

            for (Usuario usuario : usuarios) {
                System.out.println(
                    usuario + " | getPerfil(): " + usuario.getPerfil()
                );
            }

            System.out.println("\n=== CONSULTAS ===");

            List<Consulta> consultas = carregarConsultas(conn, usuarios);

            for (Consulta consulta : consultas) {
                System.out.println(consulta);
            }

            System.out.println("\n=== TESTE DE VALIDAÇÃO ===");

            if (!usuarios.isEmpty()) {

                try {
                    usuarios.get(0).setSenha("123");

                } catch (IllegalArgumentException e) {
                    System.out.println(
                        "Validação funcionando: " + e.getMessage()
                    );
                }
            }

        } catch (SQLException | IllegalArgumentException e) {

            System.err.println("ERRO: " + e.getMessage());

        } finally {

            // ENCERRA A THREAD DE LIMPEZA DO MYSQL
            AbandonedConnectionCleanupThread.checkedShutdown();
        }
    }

    // ==========================================
    // CARREGAR USUÁRIOS DO MYSQL
    // ==========================================

    private static List<Usuario> carregarUsuarios(Connection conn)
            throws SQLException {

        List<Usuario> usuarios = new ArrayList<>();

        String sql = """
            SELECT id, nome, email, senha, perfil,
                   crm, especialidade, cpf, telefone, criado_em
            FROM usuarios
            ORDER BY id
            """;

        try (
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                int id = rs.getInt("id");

                String nome = rs.getString("nome");
                String email = rs.getString("email");
                String senha = rs.getString("senha");

                Timestamp t = rs.getTimestamp("criado_em");

                LocalDateTime criado =
                    t == null ? null : t.toLocalDateTime();

                Usuario usuario = switch (
                    Perfil.fromDb(rs.getString("perfil"))
                ) {

                    case MEDICO -> new Medico(
                        id,
                        nome,
                        email,
                        senha,
                        criado,
                        rs.getString("crm"),
                        rs.getString("especialidade")
                    );

                    case SECRETARIA -> new Secretaria(
                        id,
                        nome,
                        email,
                        senha,
                        criado
                    );

                    case PACIENTE -> new Paciente(
                        id,
                        nome,
                        email,
                        senha,
                        criado,
                        rs.getString("cpf"),
                        rs.getString("telefone")
                    );
                };

                usuarios.add(usuario);
            }
        }

        return usuarios;
    }

    // ==========================================
    // CARREGAR CONSULTAS DO MYSQL
    // ==========================================

    private static List<Consulta> carregarConsultas(
            Connection conn,
            List<Usuario> usuarios
    ) throws SQLException {

        Map<Integer, Usuario> porId = new HashMap<>();

        for (Usuario usuario : usuarios) {
            porId.put(usuario.getId(), usuario);
        }

        List<Consulta> consultas = new ArrayList<>();

        String sql = """
            SELECT id, paciente_id, medico_id,
                   data_hora, status, observacoes
            FROM consultas
            ORDER BY id
            """;

        try (
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery()
        ) {

            while (rs.next()) {

                int id = rs.getInt("id");

                Usuario paciente = porId.get(
                    rs.getInt("paciente_id")
                );

                Usuario medico = porId.get(
                    rs.getInt("medico_id")
                );

                if (!(paciente instanceof Paciente)
                        || !(medico instanceof Medico)) {

                    throw new SQLException(
                        "Consulta com perfil inválido: " + id
                    );
                }

                Timestamp dataHora = rs.getTimestamp("data_hora");

                if (dataHora == null) {

                    throw new SQLException(
                        "Consulta sem data e hora: " + id
                    );
                }

                Consulta consulta = new Consulta(
                    id,
                    (Paciente) paciente,
                    (Medico) medico,
                    dataHora.toLocalDateTime(),
                    StatusConsulta.fromDb(rs.getString("status")),
                    rs.getString("observacoes")
                );

                consultas.add(consulta);
            }
        }

        return consultas;
    }
}
