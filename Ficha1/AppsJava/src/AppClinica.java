import java.sql.*;
import java.util.Scanner;

public class AppClinica {
    private static final String host = "localhost";
    private static final String usrName = "root";
    private static final String password = "12345678";
    private static final String database = "clinica";

    public static int nbyn() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException ex) {
            System.out.println("Driver não disponível");
        }
        String line = "";
        Connection c = null;
        try {
            c = DriverManager.getConnection("jdbc:mysql://localhost:3306/clinica?useTimezone=true&serverTimezone=UTC", usrName, password);
        } catch (Exception e) {
            System.out.println(e);
        }
        Statement st = c.createStatement();
        int opcao = 0;
        while (opcao!=5) {
            System.out.println("Insira 1 para ver estado dos pedidos \n" +
                    "Insira 2 para cancelar pedidos \n" +
                    "Insira 3 para finalizar pedidos \n" +
                    "Insira 4 para escrever e publicar relatório de exames \n" +
                    "Insira 5 para sair \n");
            opcao = new Scanner(System.in).nextInt();
            if (opcao == 1) {
                Integer id;
                String mensagem;
                Date data;
                String estadoP;
                String query = "select * from Marcacao;";
                ResultSet rs = st.executeQuery(query);
                while (rs.next()) {
                    id = (rs.getInt("idMarcacao"));
                    mensagem = (rs.getString("mensagem"));
                    data = rs.getDate("data");
                    estadoP = rs.getString("estadoP");
                    System.out.println("idMarcação: " + id);
                    System.out.println("Mensagem: " + mensagem);
                    System.out.println("Data: " + data);
                    System.out.println("Estado: " + estadoP);
                    System.out.println("-------------------------------------------------------------------");
                }
            } else if (opcao == 2) {
                System.out.println("Insira o id do pedido que pretende cancelar: ");
                int id = new Scanner(System.in).nextInt();
                String s;
                s = "update Marcacao"
                        + " set estadoP = \"Cancelado\" where idMarcacao =" + id + " ;";
                st.executeUpdate(s);
                System.out.println("O exame com o id " + id + " foi cancelado!");
            } else if (opcao == 3) {
                System.out.println("Insira o id do pedido que pretende aceitar: ");
                int id = new Scanner(System.in).nextInt();
                String s;
                s = "update Marcacao"
                        + " set estadoP = \"Aceite\" where idMarcacao =" + id + " ;";
                st.executeUpdate(s);
                System.out.println("O exame com o id " + id + " foi aceite!");
            } else if (opcao == 4) {
                System.out.println("Insira o id do pedido que pretende registar o relatório: ");
                int id = new Scanner(System.in).nextInt();
                System.out.println("Escreva o relatório: ");
                String relatorio = new Scanner(System.in).nextLine();
                String query;
                query = "update Exame set relatorio = \"" + relatorio + "\" where Marcacao_idMarcacao = " + id + ";";
                st.executeUpdate(query);
                System.out.println("O relatório foi escrito com sucesso");
            }
        }
        return  0;

    }

    public static void main(String[] args){
        // int res = run();
        try {
            int nbyn = nbyn();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }
}
