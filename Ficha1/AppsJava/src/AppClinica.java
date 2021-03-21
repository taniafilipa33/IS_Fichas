import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.model.v24.message.ORM_O01;
import ca.uhn.hl7v2.parser.Parser;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.Scanner;

public class AppClinica {
    private static final String host = "localhost";
    private static final String usrName = "root";
    private static final String password = "password";
    private static final String database = "clinica";
    private static HapiContext context = new DefaultHapiContext();

    private static void writeMessageToFile(Parser parser, ORM_O01 adtMessage, String outputFilename)
            throws IOException, FileNotFoundException, HL7Exception {
        OutputStream outputStream = null;
        try {

            // Remember that the file may not show special delimiter characters when using
            // plain text editor
            File file = new File(outputFilename);

            // quick check to create the file before writing if it does not exist already
            if (!file.exists()) {
                file.createNewFile();
            }

            System.out.println("Serializing message to file...");
            outputStream = new FileOutputStream(file);
            outputStream.write(parser.encode(adtMessage).getBytes());
            outputStream.flush();

            System.out.printf("Message serialized to file '%s' successfully", file);
            System.out.println("\n");
        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }


    public static int nbyn() throws SQLException, IOException {
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
                String encoding = "UTF-8";
                byte[] encoded = Files.readAllBytes(Paths.get("FSHospital"+id+".txt"));
                String mensagem = new String(encoded, encoding);

                String response = mensagem.replaceAll("NW", "CA");
                System.out.println(response);
                FileWriter writer = new FileWriter("FSHospital"+id+".txt");
                writer.write(response);
                writer.flush();

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
        } catch (SQLException | IOException throwables) {
            throwables.printStackTrace();
        }
    }
}
