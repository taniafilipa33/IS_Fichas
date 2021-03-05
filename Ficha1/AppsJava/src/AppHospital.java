import java.sql.*;
import java.util.Scanner;

public class AppHospital {
    private static final String host = "localhost";
    private static final String usrName = "root";
    private static final String password = "12345678";
    private static final String database = "hospital";

    public static int nbyn() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            System.out.println("Driver não disponível");
        }
        String line = "";
        Connection c = null;
        try {
            c = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital?useTimezone=true&serverTimezone=UTC", usrName, password);
        } catch (Exception e) {
            System.out.println(e);
        }
        Statement st = c.createStatement();
        int opcao = 0;
        while (opcao != 6) {
            System.out.println("Insira 1 para visualizar o histórico de consultas \n" +
                    "Insira 2 para efetuar pedido \n" +
                    "Insira 3 para cancelar pedidos \n" +
                    "Insira 4 para visualizar estado dos pedidos \n" +
                    "Insira 5 para visualizar o relatório recebido da clínica \n" +
                    "Insira 6 para sair \n");
            opcao = new Scanner(System.in).nextInt();
            if (opcao ==1) {
                int id, idPs, idPaciente;
                String descricao;
                String query = "select * from consulta;";
                ResultSet rs = st.executeQuery(query);
                while (rs.next()) {
                    id = (rs.getInt("idConsulta"));
                    descricao = (rs.getString("descricao"));
                    idPs = rs.getInt("idPs");
                    idPaciente = rs.getInt("idPaciente");
                    System.out.println("idConsulta: " + id);
                    System.out.println("Descrição: " + descricao);
                    System.out.println("-------------------------------------------------------------------");
                }
            }
            else if (opcao == 2) {
                System.out.println("Insira a mensagem:");
                String mensagem = new Scanner(System.in).nextLine();
                System.out.println("Insira o id da consulta:");
                int idConsulta = new Scanner(System.in).nextInt();
                System.out.println("Insira o número de exames que pretende pedir:");
                int nExames = new Scanner(System.in).nextInt();

                String query = "INSERT IGNORE INTO Pedido (mensagem, estado, data, idConsulta,nExames) VALUES(\""+ mensagem +"\",\"Pendente\", (select CURDATE()),"+ idConsulta +"," + nExames + ")";
                st.executeUpdate(query);
                System.out.println("O seu pedido foi inserido!");
                }
            else if (opcao == 3) {
                System.out.println("Insira o id do pedido que pretende cancelar: ");
                int id = new Scanner(System.in).nextInt();
                String s;
                s = "update Pedido"
                        + " set estado = \"Cancelado\" where idPedido =" + id + " ;";
                st.executeUpdate(s);
                System.out.println("O pedido com o id " + id + "foi cancelado!");
            } else if (opcao == 4) {
                Integer id, nExames, idConsulta;
                String mensagem;
                Date data;
                String estado;
                String query = "select * from Pedido;";
                ResultSet rs = st.executeQuery(query);
                while (rs.next()) {
                    id = (rs.getInt("idPedido"));
                    mensagem = (rs.getString("mensagem"));
                    data = rs.getDate("data");
                    estado = rs.getString("estado");
                    nExames = rs.getInt("nExames");
                    idConsulta = rs.getInt("idConsulta");
                    System.out.println("Pedido: " + id);
                    System.out.println("Mensagem: " + mensagem);
                    System.out.println("Data: " + data);
                    System.out.println("Estado: " + estado);
                    System.out.println("Consulta: " + idConsulta);
                    System.out.println("Número de exames: " + nExames);
                    System.out.println("-------------------------------------------------------------------");
                }
            } else if (opcao == 5) {
                System.out.println("Insira o id do pedido que pretende visualizar o relatório: ");
                int id = new Scanner(System.in).nextInt();
                String query;
                query = "select * from Pedido where idPedido = " + id + ";";
                ResultSet rs = st.executeQuery(query);
                String relatorio;
                while(rs.next()){
                    relatorio = rs.getString("relatorio");
                    System.out.println("Relatório: " + relatorio);
                }
            }
        }
        return 0;

    }

    public static void main(String[] args) {
        // int res = run();
        try {
            int nbyn = nbyn();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}