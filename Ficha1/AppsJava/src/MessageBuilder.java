import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.v24.datatype.*;
import ca.uhn.hl7v2.model.v24.group.ORU_R01_ORDER_OBSERVATION;
import ca.uhn.hl7v2.model.v24.message.ADT_A01;
import ca.uhn.hl7v2.model.v24.message.ORM_O01;
import ca.uhn.hl7v2.model.v24.message.ORU_R01;
import ca.uhn.hl7v2.model.v24.segment.*;

public class MessageBuilder {

    private static ORM_O01 _ormMessage;

    /*
     * You can pass in a domain object as a parameter when integrating with data
     * from your application here and I will leave that to you to explore on your
     * own. I will use fictional data here for illustration
     */

    public ORM_O01 Build(String name, String numProcesso, String adress, int idPedido, String exame, String codigo) throws HL7Exception, IOException {
        String currentDateTimeString = getCurrentTimeStamp();
        _ormMessage = new ORM_O01();
        _ormMessage.initQuickstart("ORM", "001", "P");
        createMshSegment(currentDateTimeString);
       // createEvnSegment(currentDateTimeString);
        createPidSegment(name, numProcesso, adress);
        createPv1Segment();
        createORCSegment();
        createOBR1Segment(idPedido, codigo, exame);
        return _ormMessage;
    }

    private void createMshSegment(String currentDateTimeString) throws DataTypeException {
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
/*
    private void createEvnSegment(String currentDateTimeString) throws DataTypeException {
        EVN evn = _ormMessage.
        evn.getEventTypeCode().setValue("A01");
        evn.getRecordedDateTime().getTimeOfAnEvent().setValue(currentDateTimeString);
    }
*/
    private void createPidSegment(String name, String numProcesso, String adress) throws DataTypeException {
        PID pid = _ormMessage.getPATIENT().getPID();
        XPN patientName = pid.getPatientName(0);
        patientName.getGivenName().setValue(name);
        pid.getPatientIdentifierList(0).getID().setValue(numProcesso);
        XAD patientAddress = pid.getPatientAddress(0);
        patientAddress.getStreetAddress().getStreetOrMailingAddress().setValue(adress);
    }

    private void createPv1Segment() throws DataTypeException {
        PV1 pv1 = _ormMessage.getPATIENT().getPATIENT_VISIT().getPV1();
        pv1.getPatientClass().setValue("O"); // to represent an 'Outpatient'
        PL assignedPatientLocation = pv1.getAssignedPatientLocation();
        assignedPatientLocation.getFacility().getNamespaceID().setValue("Some Treatment Facility Name");
        assignedPatientLocation.getPointOfCare().setValue("Some Point of Care");
        pv1.getAdmissionType().setValue("ALERT");
        XCN referringDoctor = pv1.getReferringDoctor(0);
        referringDoctor.getIDNumber().setValue("99999999");
        referringDoctor.getFamilyName().getSurname().setValue("Smith");
        referringDoctor.getGivenName().setValue("Jack");
        referringDoctor.getIdentifierTypeCode().setValue("456789");
        pv1.getAdmitDateTime().getTimeOfAnEvent().setValue(getCurrentTimeStamp());
    }
/*
    private void createIN1Segment() throws DataTypeException {
        IN1 in1 = _ormMessage.getPATIENT().getINSURANCE().getIN1();
        in1.
    }*/

    private void createORCSegment() throws DataTypeException {
        ORC orc = _ormMessage.getORDER().getORC();
        orc.getOrderControl().setValue("NW"); //new order
        orc.getDateTimeOfTransaction().getTimeOfAnEvent().setValue(getCurrentTimeStamp());
    }

    private void createOBR1Segment(int idPedido, String exame, String codigo) throws HL7Exception, IOException {
        OBR obr = _ormMessage.getORDER().getORDER_DETAIL().getOBR();
        obr.getSetIDOBR().setValue(String.valueOf(idPedido));
        obr.getUniversalServiceIdentifier().getCe1_Identifier().setValue(codigo);
        obr.getUniversalServiceIdentifier().getCe2_Text().setValue(exame);
    }

    private String getCurrentTimeStamp() {
        return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    }

    private String getSequenceNumber() {
        String facilityNumberPrefix = "1234"; // some arbitrary prefix for the facility
        return facilityNumberPrefix.concat(getCurrentTimeStamp());
    }

}