import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.Varies;
import ca.uhn.hl7v2.model.v24.datatype.*;
import ca.uhn.hl7v2.model.v24.group.ORU_R01_ORDER_OBSERVATION;
import ca.uhn.hl7v2.model.v24.message.ADT_A01;
import ca.uhn.hl7v2.model.v24.message.ORM_O01;
import ca.uhn.hl7v2.model.v24.message.ORU_R01;
import ca.uhn.hl7v2.model.v24.segment.*;

public class MessageBuilder {

    private static ORM_O01 _ormMessage;
    private static ORU_R01 _oruMessage;

    /*
     * You can pass in a domain object as a parameter when integrating with data
     * from your application here and I will leave that to you to explore on your
     * own. I will use fictional data here for illustration
     */

    public ORM_O01 Build(String name, String numProcesso, String adress, int idPedido, String exame, String codigo, String estadoPedido, int b) throws HL7Exception, IOException {
        String currentDateTimeString = getCurrentTimeStamp();
        _ormMessage = new ORM_O01();
        _ormMessage.initQuickstart("ORM", "001", "P");
        createMshSegment(currentDateTimeString,b);
       // createEvnSegment(currentDateTimeString);
        createPidSegment(name, numProcesso, adress,b);
        createPv1Segment(b);
        createORCSegment(estadoPedido,b);
        createOBR1Segment(idPedido, codigo, exame,b);
        return _ormMessage;
    }

    public ORU_R01 BuildRelatorio(String name, String numProcesso, String adress, int idPedido, String exame, String codigo, String estadoPedido, int b, String descricao, String relatorio, String medico) throws HL7Exception, IOException {
        String currentDateTimeString = getCurrentTimeStamp();
        _oruMessage = new ORU_R01();
        _oruMessage.initQuickstart("ORU", "R01", "P");
        createMshSegment(currentDateTimeString, b);
        // createEvnSegment(currentDateTimeString);
        createPidSegment(name, numProcesso, adress, b);
        createPv1Segment(b);
        createORCSegment(estadoPedido,b);
        createOBR1Segment(idPedido, codigo, exame,b);
        createOBX1Segmente(descricao, relatorio, medico);
        return _oruMessage;
    }

    private void createMshSegment(String currentDateTimeString,int b ) throws DataTypeException {
        if(b == 0) {
            MSH mshSegment = _ormMessage.getMSH();
            mshSegment.getFieldSeparator().setValue("|");
            mshSegment.getEncodingCharacters().setValue("^~\\&");
            mshSegment.getSendingApplication().getNamespaceID().setValue("AppHospital");
            mshSegment.getSendingFacility().getNamespaceID().setValue("Hospital");
            mshSegment.getReceivingApplication().getNamespaceID().setValue("AppClinica");
            mshSegment.getReceivingFacility().getNamespaceID().setValue("Clinica");
            mshSegment.getDateTimeOfMessage().getTimeOfAnEvent().setValue(currentDateTimeString);
            mshSegment.getMessageControlID().setValue(getSequenceNumber());
            mshSegment.getVersionID().getVersionID().setValue("2.4");
        }
        else{
            MSH mshSegment = _oruMessage.getMSH();
            mshSegment.getFieldSeparator().setValue("|");
            mshSegment.getEncodingCharacters().setValue("^~\\&");
            mshSegment.getSendingApplication().getNamespaceID().setValue("AppHospital");
            mshSegment.getSendingFacility().getNamespaceID().setValue("Hospital");
            mshSegment.getReceivingApplication().getNamespaceID().setValue("AppClinica");
            mshSegment.getReceivingFacility().getNamespaceID().setValue("Clinica");
            mshSegment.getDateTimeOfMessage().getTimeOfAnEvent().setValue(currentDateTimeString);
            mshSegment.getMessageControlID().setValue(getSequenceNumber());
            mshSegment.getVersionID().getVersionID().setValue("2.4");
        }
    }
/*
    private void createEvnSegment(String currentDateTimeString) throws DataTypeException {
        EVN evn = _ormMessage.
        evn.getEventTypeCode().setValue("A01");
        evn.getRecordedDateTime().getTimeOfAnEvent().setValue(currentDateTimeString);
    }
*/
    private void createPidSegment(String name, String numProcesso, String adress, int b) throws DataTypeException {
        if(b==0) {
            PID pid = _ormMessage.getPATIENT().getPID();
            XPN patientName = pid.getPatientName(0);
            patientName.getGivenName().setValue(name);
            pid.getPatientIdentifierList(0).getID().setValue(numProcesso);
            XAD patientAddress = pid.getPatientAddress(0);
            patientAddress.getStreetAddress().getStreetOrMailingAddress().setValue(adress);
        }
        else{
            PID pid = _oruMessage.getPATIENT_RESULT().getPATIENT().getPID();
            XPN patientName = pid.getPatientName(0);
            patientName.getGivenName().setValue(name);
            pid.getPatientIdentifierList(0).getID().setValue(numProcesso);
            XAD patientAddress = pid.getPatientAddress(0);
            patientAddress.getStreetAddress().getStreetOrMailingAddress().setValue(adress);
        }
    }

    private void createPv1Segment(int b) throws DataTypeException {
        if(b==0) {
            PV1 pv1 = _ormMessage.getPATIENT().getPATIENT_VISIT().getPV1();
            pv1.getPatientClass().setValue("O"); // to represent an 'Outpatient'
        }
        else{
            PV1 pv1 = _oruMessage.getPATIENT_RESULT().getPATIENT().getVISIT().getPV1();
            pv1.getPatientClass().setValue("O"); // to represent an 'Outpatient'
        }
    }
/*
    private void createIN1Segment() throws DataTypeException {
        IN1 in1 = _ormMessage.getPATIENT().getINSURANCE().getIN1();
        in1.
    }*/

    private void createORCSegment(String estadoPedido, int b) throws DataTypeException {
        if(b==0) {
            ORC orc = _ormMessage.getORDER().getORC();
            orc.getOrderControl().setValue(estadoPedido); //new order
            orc.getDateTimeOfTransaction().getTimeOfAnEvent().setValue(getCurrentTimeStamp());
        }
        else{
            ORC orc = _oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION().getORC();
            orc.getOrderControl().setValue(estadoPedido); //new order
            orc.getDateTimeOfTransaction().getTimeOfAnEvent().setValue(getCurrentTimeStamp());
        }
    }

    private void createOBR1Segment(int idPedido, String exame, String codigo, int b) throws HL7Exception, IOException {
        if(b==0) {
            OBR obr = _ormMessage.getORDER().getORDER_DETAIL().getOBR();
            obr.getSetIDOBR().setValue(String.valueOf(idPedido));
            obr.getUniversalServiceIdentifier().getCe1_Identifier().setValue(codigo);
            obr.getUniversalServiceIdentifier().getCe2_Text().setValue(exame);
        }
        else{
            OBR obr = _oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION().getOBR();
            obr.getSetIDOBR().setValue(String.valueOf(idPedido));
            obr.getUniversalServiceIdentifier().getCe1_Identifier().setValue(codigo);
            obr.getUniversalServiceIdentifier().getCe2_Text().setValue(exame);
        }
    }

    private  void createOBX1Segmente(String descricao, String relatorio, String medico)throws HL7Exception, IOException {
        OBX obx = _oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION().getOBSERVATION(0).getOBX();
        obx.getSetIDOBX().setValue("1");
        obx.getValueType().setValue("TX");
        TX tx = new TX(_oruMessage);
        tx.setValue(descricao);
        obx.getObservationValue(0).setData(tx);

        OBX obx1 = _oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION().getOBSERVATION(1).getOBX();
        obx1.getSetIDOBX().setValue("2");
        obx1.getValueType().setValue("TX");
        TX tx1 = new TX(_oruMessage);
        tx1.setValue(relatorio);
        obx1.getObservationValue(0).setData(tx1);

        OBX obx2 = _oruMessage.getPATIENT_RESULT().getORDER_OBSERVATION().getOBSERVATION(2).getOBX();
        String real = "Relatório validado por: ";
        real += medico;
        obx2.getSetIDOBX().setValue("3");
        obx2.getValueType().setValue("TX");
        TX tx2 = new TX(_oruMessage);
        tx2.setValue(real);
        obx2.getObservationValue(0).setData(tx2);
    }



    private String getCurrentTimeStamp() {
        return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    }

    private String getSequenceNumber() {
        String facilityNumberPrefix = "1234"; // some arbitrary prefix for the facility
        return facilityNumberPrefix.concat(getCurrentTimeStamp());
    }

}