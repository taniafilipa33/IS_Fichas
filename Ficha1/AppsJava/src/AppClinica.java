import java.sql.*;
import java.util.Scanner;

public class AppClinica {
    private static final String host = "localhost";
    private static final String usrName = "root";
    private static final String password = "password";
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
                    "Insira 3 para aceitar pedidos \n" +
                    "Insira 4 para escrever e publicar relatório de exames \n" +
                    "Insira 5 para sair \n");
            opcao = new Scanner(System.in).nextInt();
            if (opcao == 1) {
                int id;
                String mensagem;
                Date data;
                String estado;
                String relatorio;
                String query = "select * from Pedido;";
                ResultSet rs = st.executeQuery(query);
                while (rs.next()) {
                    id = (rs.getInt("idPedido"));
                    mensagem = (rs.getString("mensagem"));
                    data = rs.getDate("data");
                    estado = rs.getString("estado");
                    relatorio = rs.getString("relatorio");
                    System.out.println("idPedido: " + id);
                    System.out.println("Mensagem: " + mensagem);
                    System.out.println("Data: " + data);
                    System.out.println("Estado: " + estado);
                    System.out.println("Relatório: " + relatorio);
                    System.out.println("-------------------------------------------------------------------");
                }
            } else if (opcao == 2) {
                System.out.println("Insira o id do pedido que pretende cancelar: ");
                int id = new Scanner(System.in).nextInt();
                String s;
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                String estado = "Cancelado ";
                estado += timestamp;
                s = "update Pedido"
                        + " set estado = \""+estado+"\" where idPedido =" + id + " ;";
                st.executeUpdate(s);
                System.out.println("O exame com o id " + id + " foi cancelado!");
            } else if (opcao == 3) {
                System.out.println("Insira o id do pedido que pretende aceitar: ");
                int id = new Scanner(System.in).nextInt();
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                String estado = "Aceite ";
                estado += timestamp;
                String s;
                s = "update Pedido"
                        + " set estado = \""+estado+"\" where idPedido =" + id + " ;";
                st.executeUpdate(s);
                System.out.println("O exame com o id " + id + " foi aceite!");
            } else if (opcao == 4) {
                System.out.println("Insira o id do pedido que pretende registar o relatório: ");
                int id = new Scanner(System.in).nextInt();
                System.out.println("Escreva o relatório: ");
                String relatorio = new Scanner(System.in).nextLine();
                String query;
                query = "update Pedido set relatorio = \"" + relatorio + "\" where idPedido = " + id + ";";
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
