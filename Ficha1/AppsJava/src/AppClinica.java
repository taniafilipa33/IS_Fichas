import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.model.v24.message.ORM_O01;
import ca.uhn.hl7v2.model.v24.message.ORU_R01;
import ca.uhn.hl7v2.parser.Parser;

import java.io.*;
import java.sql.*;
import java.util.Scanner;

public class AppClinica {
    private static final String host = "localhost";
    private static final String usrName = "root";
    private static final String password = "password";
    private static final String database = "clinica";
    private static HapiContext context = new DefaultHapiContext();


    private static void writeMessageToFile(Parser parser, ORU_R01 adtMessage, String outputFilename)
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

    public static void clearScreen() {
        System.out.print("\n\n\n\n\n\n\n\n\n\n");
    }


    public static int nbyn() throws SQLException, IOException, HL7Exception {
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
        clearScreen();
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

                String query = "select mensagem from Pedido where idPedido = "+id+";";
                ResultSet rs = st.executeQuery(query);
                String mensagem = "";
                while (rs.next()) {
                    mensagem = rs.getString("mensagem");
                }
                String response = mensagem.replaceAll("NW", "CA");
                File myObj = new File("FSClinicaCA"+id+".txt");
                FileWriter writer = new FileWriter(myObj);
                writer.write(response);
                writer.flush();

                String query2 = "INSERT IGNORE INTO RegistoHistorico (estadoPedido, mensagem, Pedido_idPedido) VALUES(\""+ estado +"\",\"" + response +"\"," + id+" )";
                st.executeUpdate(query2);

            } else if (opcao == 3) {
                System.out.println("Insira o id do pedido que pretende aceitar: ");
                int id = new Scanner(System.in).nextInt();
                String estadoP = "";
                String query = "select estado from Pedido where idPedido = "+id+" ;";
                ResultSet rs = st.executeQuery(query);
                while (rs.next()) {
                    estadoP = (rs.getString("estado"));
                }
                if (estadoP.contains("Cancelado")){
                    System.out.println("O pedido não pode ser aceite porque foi cancelado!");
                }
                else {
                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                    String estado = "Aceite ";
                    estado += timestamp;
                    String s;
                    s = "update Pedido"
                            + " set estado = \""+estado+"\" where idPedido =" + id + " ;";
                    st.executeUpdate(s);
                    System.out.println("O exame com o id " + id + " foi aceite!");

                    String queryS = "select mensagem from Pedido where idPedido = "+id+";";
                    ResultSet rsS = st.executeQuery(queryS);
                    String mensagem = "";
                    while (rsS.next()) {
                        mensagem = rsS.getString("mensagem");
                    }

                    String response = mensagem.replaceAll("NW", "OK");
                    File myObj = new File("FSClinicaAceite" + id + ".txt");
                    FileWriter writer = new FileWriter(myObj);
                    writer.write(response);
                    writer.flush();

                    String query2 = "INSERT IGNORE INTO RegistoHistorico (estadoPedido, mensagem, Pedido_idPedido) VALUES(\"" + estado + "\",\"" + response + "\"," + id + " )";
                    st.executeUpdate(query2);
                }

            } else if (opcao == 4) {
                Parser pipeParser = context.getPipeParser();
                System.out.println("Insira o id do exame que pretende registar o relatório: ");
                int id = new Scanner(System.in).nextInt();


                String queryS = "select * from Exame where idExame = "+id+";";
                ResultSet rsS = st.executeQuery(queryS);
                int idPedido = 0;
                String medico = "";
                int idPaciente = 0;
                String codigo = "";
                String exame = "";
                while (rsS.next()) {
                    idPedido = rsS.getInt("Pedido_idPedido");
                    medico = rsS.getString("medico");
                    idPaciente = rsS.getInt("Paciente_idPaciente");
                    codigo = rsS.getString("codigo_ato");
                    exame = rsS.getString("ato");
                }

                String query1 = "select * from Paciente where idPaciente = "+idPaciente+";";
                ResultSet rs = st.executeQuery(query1);
                String nome = "";
                int numProcesso = 0;
                String morada = "";
                while (rs.next()) {
                    nome = rs.getString("nome");
                    numProcesso = rs.getInt("numProcesso");
                    morada = rs.getString("morada");

                }

                System.out.println("Escreva o relatório: ");
                String relatorio = new Scanner(System.in).nextLine();
                String query;
                query = "update Pedido set relatorio = \"" + relatorio + "\" where idPedido = " + idPedido + ";";
                st.executeUpdate(query);

                String query3 = "select * from Pedido where idPedido = "+idPedido+";";
                ResultSet rs3 = st.executeQuery(query3);
                String estado = "";
                while (rs3.next()) {
                    estado = rs3.getString("estado");

                }

                ORU_R01 oru_r01 = (ORU_R01) AdtMessageFactory.createMessage("R01",nome, String.valueOf(numProcesso), morada,idPedido,exame,codigo, "NW",1,exame, relatorio, medico );
                String mensagem = pipeParser.encode(oru_r01);
                writeMessageToFile(pipeParser, oru_r01, "FSClinicaRelatorio"+idPedido+".txt");


                String query2 = "INSERT IGNORE INTO RegistoHistorico (estadoPedido, mensagem, Pedido_idPedido) VALUES(\"" + estado + "\",\"" + mensagem + "\"," + idPedido + " )";
                st.executeUpdate(query2);

                System.out.println("O relatório foi escrito com sucesso");
            }
        }
        return  0;

    }

    public static void main(String[] args){
        // int res = run();
        try {
            int nbyn = nbyn();
        } catch (SQLException | IOException | HL7Exception throwables) {
            throwables.printStackTrace();
        }
    }
}
