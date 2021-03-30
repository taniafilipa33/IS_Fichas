import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v24.message.ORM_O01;
import ca.uhn.hl7v2.model.v24.message.ORU_R01;
import ca.uhn.hl7v2.parser.Parser;
import ca.uhn.hl7v2.parser.PipeParser;

import java.io.*;
import java.nio.file.Files;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Scanner;



public class AppHospital {
    private static final String host = "localhost";
    private static final String usrName = "root";
    private static final String password = "root";
    private static final String database = "hospital";
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

            outputStream = new FileOutputStream(file);
            outputStream.write(parser.encode(adtMessage).getBytes());
            outputStream.flush();

        } finally {
            if (outputStream != null) {
                outputStream.close();
            }
        }
    }

    public static void clearScreen() {

        System.out.print("\n\n\n\n\n\n\n\n\n\n");
    }

    public static void readFiles(Connection c) {
        File Folder = new File("relatoriosH");
        File files[] = Folder.listFiles();
        for (int i = 0; i < files.length; i++) {
            byte[] encoded = new byte[0];
            String message = "";
            try {
                encoded = Files.readAllBytes(files[i].getAbsoluteFile().toPath());
                message = new String(encoded, "UTF-8");
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            Statement st = null;
            try {
                st = c.createStatement();
            }
            catch (SQLException throwables) {
                throwables.printStackTrace();
            }
            PipeParser pipeParser = new PipeParser();
            try {
                //parse the message string into a Message object
                Message hl7 = pipeParser.parse(message);

                //if it is an ACK message (as we know it is),  cast it to an
                // ACK object so that it is easier to work with, and change a value
                if (hl7 instanceof ORU_R01) {
                    String query = "select idConsulta from Pedido where mensagem = '"+ message +"';";
                    ResultSet rs = st.executeQuery(query);
                    int idConsulta = 0;
                    while (rs.next()) {
                        idConsulta = rs.getInt("idConsulta");
                    }
                    ORU_R01 oru = (ORU_R01) hl7;
                    java.util.Date data = oru.getMSH().getDateTimeOfMessage().getTimeOfAnEvent().getValueAsDate();
                    String pattern = "yyyy-MM-dd hh:mm:ss";
                    SimpleDateFormat formatter = new SimpleDateFormat(pattern);
                    String mysqlDateString = formatter.format(data);
                    String codigo = oru.getPATIENT_RESULT().getORDER_OBSERVATION().getOBR().getUniversalServiceIdentifier().getCe1_Identifier().toString();
                    String exame = oru.getPATIENT_RESULT().getORDER_OBSERVATION().getOBR().getUniversalServiceIdentifier().getCe2_Text().toString();
                    String estado = "Aceite " + mysqlDateString;
                    String relatorio = oru.getPATIENT_RESULT().getORDER_OBSERVATION().getOBSERVATION(1).getOBX().getObservationValue(0).encode();
                    String query1 = "INSERT IGNORE INTO Pedido (mensagem, estado, data, idConsulta, relatorio, codigoExame, descExame)" +
                                   " VALUES (\"" + message + "\", '" + estado + "', '" + mysqlDateString + "', "+ idConsulta + ", '" + relatorio +
                                   "', '"+ codigo +"'," + " '"+ exame +"')";
                    //System.out.println(relatorio);
                    st.executeUpdate(query1);
                }
                /*String query2 = "INSERT IGNORE INTO RegistoHistorico (estadoPedido, mensagem, Pedido_idPedido) VALUES(\"" + estado + "\",\"" + response + "\"," + id + " )";
                st.executeUpdate(query2);*/

                if (hl7 instanceof ORM_O01) {
                    ORM_O01 orm = (ORM_O01) hl7;
                    java.util.Date data = orm.getMSH().getDateTimeOfMessage().getTimeOfAnEvent().getValueAsDate();
                    String pattern = "yyyy-MM-dd hh:mm:ss";
                    SimpleDateFormat formatter = new SimpleDateFormat(pattern);
                    String mysqlDateString = formatter.format(data);
                    String estado = orm.getORDER().getORC().getOrderControl().encode();
                    if(estado.equals("CA")){
                        estado = "Cancelado " + mysqlDateString;
                    }
                    else if(estado.equals("OK")){
                        estado = "Aceite " + mysqlDateString;
                    }
                    String response = message.replaceAll("CA", "NW");
                    String query = "select idPedido from Pedido where mensagem = '"+ response +"';";
                    ResultSet rs = st.executeQuery(query);
                    int idPedido = 0;
                    while (rs.next()) {
                        idPedido = rs.getInt("idPedido");
                    }

                    String s;
                    s = "update Pedido"
                            + " set estado = \""+ estado +"\", mensagem = '"+ message +"' where idPedido =" + idPedido + ";";
                    st.executeUpdate(s);
                }
            }
            catch (SQLException | HL7Exception e) {
                e.printStackTrace();
            }
            if (files[i].delete()) {
                System.out.println("Deleted the file: " + files[i].getName());
            }
            else {
                System.out.println("Failed to delete the file.");
            }
        }
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
            c = DriverManager.getConnection("jdbc:mysql://localhost:3306/hospital?useTimezone=true&serverTimezone=UTC", usrName, password);
        } catch (Exception e) {
            System.out.println(e);
        }
        Statement st = c.createStatement();
        int opcao = 0;
        clearScreen();
        while (opcao != 6) {
            readFiles(c);
            System.out.println("Insira 1 para visualizar o histórico de consultas \n" +
                    "Insira 2 para efetuar pedido \n" +
                    "Insira 3 para cancelar pedidos \n" +
                    "Insira 4 para visualizar estado dos pedidos \n" +
                    "Insira 5 para visualizar o relatório recebido da clínica \n" +
                    "Insira 6 para sair \n");
            opcao = new Scanner(System.in).nextInt();
            if (opcao ==1) {
                int id;
                String descricao;
                String query = "select * from consulta;";
                ResultSet rs = st.executeQuery(query);
                while (rs.next()) {
                    id = (rs.getInt("idConsulta"));
                    descricao = (rs.getString("descricao"));
                    System.out.println("idConsulta: " + id);
                    System.out.println("Descrição: " + descricao);
                    System.out.println("-------------------------------------------------------------------");
                }
            }
            else if (opcao == 2) {
                Parser pipeParser = context.getPipeParser();

                System.out.println("Insira o identificador do paciente:");
                int idPaciente = new Scanner(System.in).nextInt();
                String select1 = "SELECT * from Paciente where idPaciente = "+idPaciente;
                ResultSet rs1 = st.executeQuery(select1);
                String nome= "";
                int numProcesso = 0;
                String morada ="";
                while (rs1.next()) {
                    nome = rs1.getString("nome");
                    numProcesso = rs1.getInt("numProcesso");
                    morada = rs1.getString("morada");
                }
                String select = "SELECT max(idPedido) from Pedido";
                ResultSet rs = st.executeQuery(select);
                int idPedido = 0;
                while (rs.next()) {
                    idPedido = rs.getInt(1);
                }
                System.out.println("Código do exame a realizar:");
                String codigo = new Scanner(System.in).nextLine();
                System.out.println("Descrição do exame a realizar:");
                String exame = new Scanner(System.in).nextLine();
                ORM_O01 ormMessage = (ORM_O01) AdtMessageFactory.createMessage("001",nome, String.valueOf(numProcesso), morada,idPedido+1,exame,codigo, "NW",0 );
                String mensagem = pipeParser.encode(ormMessage);
                System.out.println("Insira o id da consulta:");
                int idConsulta = new Scanner(System.in).nextInt();

                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                String estado = "Pendente ";
                estado += timestamp;

                String query = "INSERT IGNORE INTO Pedido (mensagem, estado, data, idConsulta,relatorio, codigoExame, descExame) VALUES(\""+ mensagem +"\",\""+estado+"\", (select CURDATE()),"+ idConsulta +",null,\""+codigo+"\","+"\""+exame+"\")";
                st.executeUpdate(query);

                String query2 = "INSERT IGNORE INTO RegistoHistorico (estadoPedido, mensagem, idPedido) VALUES(\""+ estado +"\",\"" + mensagem +"\"," + (idPedido+1)+" )";
                st.executeUpdate(query2);

                System.out.println("O seu pedido foi inserido!");
                writeMessageToFile(pipeParser, ormMessage, "FSHospital"+(idPedido+1)+".txt");
                }
            else if (opcao == 3) {
                Parser pipeParser = context.getPipeParser();

                System.out.println("Insira o id do pedido que pretende cancelar: ");
                int id = new Scanner(System.in).nextInt();
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                String estado = "Cancelado ";
                estado += timestamp;
                String s;


                String select = "SELECT * from Pedido where idPedido =" + id +";";
                ResultSet rs = st.executeQuery(select);
                String exame ="";
                String codigo ="";
                int idConsulta = 0;
                while (rs.next()) {
                    exame = rs.getString("descExame");
                    codigo = rs.getString("codigoExame");
                    idConsulta = rs.getInt("idConsulta");
                }

                String select2 = "SELECT * from Consulta where idConsulta =" + idConsulta +";";
                ResultSet rs2 = st.executeQuery(select2);
                int idPaciente = 0;
                while (rs2.next()) {
                    idPaciente = rs2.getInt("idPaciente");
                }

                String select3 = "SELECT * from Paciente where idPaciente =" + idPaciente +";";
                ResultSet rs3 = st.executeQuery(select3);
                String nome ="";
                String morada = "";
                int numProcesso = 0;
                while (rs3.next()) {
                    nome = rs3.getString("nome");
                    morada = rs3.getString("morada");
                    numProcesso = rs3.getInt("numProcesso");
                }
                ORM_O01 ormMessage = (ORM_O01) AdtMessageFactory.createMessage("001",nome, String.valueOf(numProcesso), morada,id,exame,codigo, "CA",0 );
                writeMessageToFile(pipeParser, ormMessage, "FSHospitalCA"+id+".txt");
                System.out.println("O pedido com o id " + id + " foi cancelado!");
                s = "update Pedido"
                        + " set estado = \""+ estado +"\", mensagem = '"+ pipeParser.encode(ormMessage) +"' where idPedido =" + id + ";";
                st.executeUpdate(s);
                String query2 = "INSERT IGNORE INTO RegistoHistorico (estadoPedido, mensagem, idPedido) VALUES(\""+ estado +"\",\"" + ormMessage +"\"," + id+" )";
                st.executeUpdate(query2);
            } else if (opcao == 4) {
                int id, idConsulta;
                String mensagem;
                Date data;
                String estado;
                String query = "select * from Pedido;";
                ResultSet rs = st.executeQuery(query);
                while (rs.next()) {
                    id = rs.getInt("idPedido");
                    mensagem = rs.getString("mensagem");
                    data = rs.getDate("data");
                    estado = rs.getString("estado");
                    idConsulta = rs.getInt("idConsulta");
                    System.out.println("Pedido: " + id);
                    System.out.println("Mensagem: " + mensagem);
                    System.out.println("Data: " + data);
                    System.out.println("Estado: " + estado);
                    System.out.println("Consulta: " + idConsulta);
                    System.out.println("-------------------------------------------------------------------");
                }
            } else if (opcao == 5) {
                System.out.println("Insira o id do pedido que pretende visualizar o relatório: ");
                int id = new Scanner(System.in).nextInt();
                String query;
                query = "select * from Pedido where idPedido = " + id + ";";
                ResultSet rs = st.executeQuery(query);
                String relatorio = "";
                // NÂO SABEMOS SE É SUPOSTO IR BUSCAR AO FICHEIRO OU À BASE DE DADOS - DEPENDE DO MIRTH
                while(rs.next()){
                    relatorio = rs.getString("relatorio");
                }
                System.out.println("Relatório: " + relatorio);
            }
        }
        return 0;

    }

    public static void main(String[] args) {
        // int res = run();
        try {
            int nbyn = nbyn();
        } catch (SQLException | IOException | HL7Exception throwables) {
            throwables.printStackTrace();
        }
    }

}

