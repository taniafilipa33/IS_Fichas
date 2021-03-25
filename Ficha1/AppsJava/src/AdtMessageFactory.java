import java.io.IOException;
import java.util.Date;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;

public class AdtMessageFactory {

    public static Message createMessage(String messageType, String name, String numProcesso, String adress, int idPedido, String exame, String codigo, String estadoPedido, int b) throws HL7Exception, IOException {

        //This patterns enables you to build other message types
        if ( messageType.equals("001") )
        {
            return new MessageBuilder().Build(name, numProcesso, adress, idPedido, codigo, exame, estadoPedido, b);
        }

        //if other types of ADT messages are needed, then implement your builders here
        throw new RuntimeException(String.format("%s message type is not supported yet. Extend this if you need to", messageType));

    }

    public static Message createMessage(String messageType, String name, String numProcesso, String adress, int idPedido, String exame, String codigo, String estadoPedido, int b, String descricao, String relatorio, String medico) throws HL7Exception, IOException {

        //This patterns enables you to build other message types
        if ( messageType.equals("R01") )
        {
            return new MessageBuilder().BuildRelatorio(name, numProcesso, adress, idPedido, codigo, exame, estadoPedido, b, descricao, relatorio, medico);
        }

        //if other types of ADT messages are needed, then implement your builders here
        throw new RuntimeException(String.format("%s message type is not supported yet. Extend this if you need to", messageType));

    }
}