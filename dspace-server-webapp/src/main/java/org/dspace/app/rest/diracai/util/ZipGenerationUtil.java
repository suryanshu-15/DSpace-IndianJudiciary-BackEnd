package org.dspace.app.rest.diracai.util;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import org.dspace.app.rest.diracai.Repository.FileHashRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.file.*;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Component
public class ZipGenerationUtil {


    @Autowired
    private FileHashRecordRepository fileHashRecordRepository;


    private final String BASE_DIR = "/home/dspace/dspace/jtdr/";

    public File generateZipWithFiles(String cnr,String docType) throws IOException {

        Path cnrFolder = Paths.get(BASE_DIR + cnr);
        Files.createDirectories(cnrFolder);

        String jsonName = cnr + "_doc.json";
        String docName = cnr + ".pdf";
        String docDate = new SimpleDateFormat("dd-MM-yyyy").format(System.currentTimeMillis());
        String jsonContent = "[\n" +
                "  {\n" +
                "    \"docName\": \"" + docName + "\",\n" +
                "    \"docType\": \"" + docType + "\",\n" +
                "    \"docDate\": \"" + docDate + "\"\n" +
                "  }\n" +
                "]";
        Files.write(cnrFolder.resolve(jsonName), jsonContent.getBytes());

        String xmlName = cnr + "_Metadata.xml";
String xmlContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<Jdps:ecourtcase xmlns:Jdps=\"http://www.ndpp.in/2020/eCOURTCASE\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.ndpp.in/2020/eCOURTCASE eCOURTCASE.xsd \">\n" +
        "  <Jdps:e-Filing>\n" +
        "    <Jdps:e-FilingNumber>null</Jdps:e-FilingNumber>\n" +
        "    <Jdps:e-FilingType></Jdps:e-FilingType>\n" +
        "    <Jdps:e-FilingDate></Jdps:e-FilingDate>\n" +
        "    <Jdps:e-FilingYear></Jdps:e-FilingYear>\n" +
        "  </Jdps:e-Filing>\n" +
        "  <Jdps:Caveat>\n" +
        "    <Jdps:CaveatInfo>\n" +
        "      <Jdps:CaveatNumber></Jdps:CaveatNumber>\n" +
        "      <Jdps:RegistrationDate></Jdps:RegistrationDate>\n" +
        "    </Jdps:CaveatInfo>\n" +
        "    <Jdps:CaveateeInfo>\n" +
        "      <Jdps:CaveateeSerialNumber></Jdps:CaveateeSerialNumber>\n" +
        "      <Jdps:CaveateeName></Jdps:CaveateeName>\n" +
        "    </Jdps:CaveateeInfo>\n" +
        "    <Jdps:CaveatorInfo>\n" +
        "      <Jdps:CaveatorSerialNumber></Jdps:CaveatorSerialNumber>\n" +
        "      <Jdps:CaveatorName></Jdps:CaveatorName>\n" +
        "    </Jdps:CaveatorInfo>\n" +
        "  </Jdps:Caveat>\n" +
        "  <Jdps:Case>\n" +
        "    <Jdps:CaseCNRNumber>" + cnr + "</Jdps:CaseCNRNumber>\n" +
        "    <Jdps:CaseTypeCode>1</Jdps:CaseTypeCode>\n" +
        "    <Jdps:CaseTypeName>SPECIAL LEAVE PETITION (CIVIL)</Jdps:CaseTypeName>\n" +
        "    <Jdps:CaseNumber>C.A. No. 9794/2024</Jdps:CaseNumber>\n" +
        "    <Jdps:RegistrationYear></Jdps:RegistrationYear>\n" +
        "    <Jdps:RegistrationDate>2024-08-23 18:12:08</Jdps:RegistrationDate>\n" +
        "    <Jdps:NameOfState></Jdps:NameOfState>\n" +
        "    <Jdps:NameOfDistrict></Jdps:NameOfDistrict>\n" +
        "    <Jdps:NameOfHeadQuarter></Jdps:NameOfHeadQuarter>\n" +
        "    <Jdps:EstablishmentCode></Jdps:EstablishmentCode>\n" +
        "    <Jdps:EstablishmentName></Jdps:EstablishmentName>\n" +
        "    <Jdps:CaseStatus>Pending</Jdps:CaseStatus>\n" +
        "    <Jdps:VirtualCourtCNR></Jdps:VirtualCourtCNR>\n" +
        "    <Jdps:FilingNumber>457872018</Jdps:FilingNumber>\n" +
        "    <Jdps:CaseNature>Civil</Jdps:CaseNature>\n" +
        "    <Jdps:ConnectedCases>M</Jdps:ConnectedCases>\n" +
        "    <Jdps:Litigant>\n" +
        "      <Jdps:Petitioner>\n" +
        "        <Jdps:PetitionerSerialNumber></Jdps:PetitionerSerialNumber>\n" +
        "        <Jdps:PetitionerName>ANITA SINGH</Jdps:PetitionerName>\n" +
        "        <Jdps:PetitionerID></Jdps:PetitionerID>\n" +
        "        <Jdps:PetitionerGender></Jdps:PetitionerGender>\n" +
        "        <Jdps:PetitionerOrganization></Jdps:PetitionerOrganization>\n" +
        "        <Jdps:PetitionerEmailId></Jdps:PetitionerEmailId>\n" +
        "        <Jdps:PetitionerPhoneNumber></Jdps:PetitionerPhoneNumber>\n" +
        "        <Jdps:PetitionerAddress></Jdps:PetitionerAddress>\n" +
        "      </Jdps:Petitioner>\n" +
        "      <Jdps:Respondent>\n" +
        "        <Jdps:RespondentSerialNumber></Jdps:RespondentSerialNumber>\n" +
        "        <Jdps:RespondentName>PRAMILA SINHA</Jdps:RespondentName>\n" +
        "        <Jdps:RespondentID></Jdps:RespondentID>\n" +
        "        <Jdps:RespondentGender></Jdps:RespondentGender>\n" +
        "        <Jdps:RespondentOrganization></Jdps:RespondentOrganization>\n" +
        "        <Jdps:RespondentEmailId></Jdps:RespondentEmailId>\n" +
        "        <Jdps:RespondentPhoneNumber></Jdps:RespondentPhoneNumber>\n" +
        "        <Jdps:RespondentAddress></Jdps:RespondentAddress>\n" +
        "      </Jdps:Respondent>\n" +
        "    </Jdps:Litigant>\n" +
        "    <Jdps:Advocate>\n" +
        "      <Jdps:AdvocateSerialNumber></Jdps:AdvocateSerialNumber>\n" +
        "      <Jdps:AdvocateName>SUSMITA LAL</Jdps:AdvocateName>\n" +
        "      <Jdps:AdvocateType></Jdps:AdvocateType>\n" +
        "      <Jdps:AdvocateBarNumber></Jdps:AdvocateBarNumber>\n" +
        "      <Jdps:PetitionerSerialNumber></Jdps:PetitionerSerialNumber>\n" +
        "    </Jdps:Advocate>\n" +
        "    <Jdps:Subject>\n" +
        "      <Jdps:Subject></Jdps:Subject>\n" +
        "      <Jdps:Category></Jdps:Category>\n" +
        "      <Jdps:SubCategory></Jdps:SubCategory>\n" +
        "      <Jdps:PrayerOfCase></Jdps:PrayerOfCase>\n" +
        "      <Jdps:Keywords></Jdps:Keywords>\n" +
        "    </Jdps:Subject>\n" +
        "    <Jdps:Documents>\n" +
        "      <Jdps:Document>\n" +
        "        <Jdps:DocumentNumber></Jdps:DocumentNumber>\n" +
        "        <Jdps:DateOfDocument></Jdps:DateOfDocument>\n" +
        "        <Jdps:NatureOfDocument></Jdps:NatureOfDocument>\n" +
        "        <Jdps:FiledBySrNoOfPetitioner></Jdps:FiledBySrNoOfPetitioner>\n" +
        "      </Jdps:Document>\n" +
        "    </Jdps:Documents>\n" +
        "    <Jdps:LowerCourt>\n" +
        "      <Jdps:LowerCourtCNRNumber></Jdps:LowerCourtCNRNumber>\n" +
        "    </Jdps:LowerCourt>\n" +
        "  </Jdps:Case>\n" +
        "  <Jdps:FIR>\n" +
        "    <Jdps:FIRInfo>\n" +
        "      <Jdps:FIRNumber></Jdps:FIRNumber>\n" +
        "      <Jdps:FIRDate></Jdps:FIRDate>\n" +
        "      <Jdps:FIRYear></Jdps:FIRYear>\n" +
        "      <Jdps:FIRDistrict></Jdps:FIRDistrict>\n" +
        "      <Jdps:FIRTehsil></Jdps:FIRTehsil>\n" +
        "    </Jdps:FIRInfo>\n" +
        "    <Jdps:ChargesheetInfo>\n" +
        "      <Jdps:ChargeSheetNumber></Jdps:ChargeSheetNumber>\n" +
        "    </Jdps:ChargesheetInfo>\n" +
        "    <Jdps:PolliceStationInfo>\n" +
        "      <Jdps:PoliceStationCode></Jdps:PoliceStationCode>\n" +
        "      <Jdps:PoliceStationName></Jdps:PoliceStationName>\n" +
        "    </Jdps:PolliceStationInfo>\n" +
        "  </Jdps:FIR>\n" +
        "  <Jdps:Act>\n" +
        "    <Jdps:ActInfo>\n" +
        "      <Jdps:Act></Jdps:Act>\n" +
        "      <Jdps:Section></Jdps:Section>\n" +
        "    </Jdps:ActInfo>\n" +
        "  </Jdps:Act>\n" +
        "  <Jdps:Judge>\n" +
        "    <Jdps:JudgInfo>\n" +
        "      <Jdps:JOCode></Jdps:JOCode>\n" +
        "      <Jdps:JudgeName></Jdps:JudgeName>\n" +
        "      <Jdps:JudgeDesignation></Jdps:JudgeDesignation>\n" +
        "    </Jdps:JudgInfo>\n" +
        "  </Jdps:Judge>\n" +
        "  <Jdps:StatusOfCases>\n" +
        "    <Jdps:Purpose></Jdps:Purpose>\n" +
        "    <Jdps:SubPurpose></Jdps:SubPurpose>\n" +
        "    <Jdps:DateOfDisposal></Jdps:DateOfDisposal>\n" +
        "    <Jdps:DisposalType></Jdps:DisposalType>\n" +
        "  </Jdps:StatusOfCases>\n" +
        "  <Jdps:Order>\n" +
        "    <Jdps:OrderType></Jdps:OrderType>\n" +
        "    <Jdps:OrderDate></Jdps:OrderDate>\n" +
        "  </Jdps:Order>\n" +
        "  <Jdps:Digitization>\n" +
        "    <Jdps:RecordNumber></Jdps:RecordNumber>\n" +
        "    <Jdps:DocumentOrientation></Jdps:DocumentOrientation>\n" +
        "    <Jdps:NoOfPages></Jdps:NoOfPages>\n" +
        "    <Jdps:StatusOfDocument></Jdps:StatusOfDocument>\n" +
        "    <Jdps:DocumentType></Jdps:DocumentType>\n" +
        "    <Jdps:ComparisonOfSource></Jdps:ComparisonOfSource>\n" +
        "    <Jdps:LegibilityOfText></Jdps:LegibilityOfText>\n" +
        "    <Jdps:QualityOfImages></Jdps:QualityOfImages>\n" +
        "    <Jdps:DigitizationDateTime></Jdps:DigitizationDateTime>\n" +
        "    <Jdps:MasterFile>\n" +
        "      <Jdps:FileName></Jdps:FileName>\n" +
        "      <Jdps:FileSize></Jdps:FileSize>\n" +
        "      <Jdps:FileFormat></Jdps:FileFormat>\n" +
        "    </Jdps:MasterFile>\n" +
        "    <Jdps:SpecialConsiderations></Jdps:SpecialConsiderations>\n" +
        "    <Jdps:EditsPerformed></Jdps:EditsPerformed>\n" +
        "    <Jdps:IntegrityOfDigitizedRecord></Jdps:IntegrityOfDigitizedRecord>\n" +
        "    <Jdps:AgentName></Jdps:AgentName>\n" +
        "    <Jdps:CaptureDeviceName></Jdps:CaptureDeviceName>\n" +
        "    <Jdps:DateOfLastCalibrationOfDevice></Jdps:DateOfLastCalibrationOfDevice>\n" +
        "    <Jdps:VerifiedBy></Jdps:VerifiedBy>\n" +
        "  </Jdps:Digitization>\n" +
        "  <Jdps:RecordRoom>\n" +
        "    <Jdps:DateOfReceipt></Jdps:DateOfReceipt>\n" +
        "    <Jdps:BlockNo></Jdps:BlockNo>\n" +
        "    <Jdps:AlmiraNo></Jdps:AlmiraNo>\n" +
        "    <Jdps:RackNo></Jdps:RackNo>\n" +
        "    <Jdps:BriefJudgemnt></Jdps:BriefJudgemnt>\n" +
        "  </Jdps:RecordRoom>\n" +
        "  <Jdps:OldCase>\n" +
        "    <Jdps:OldCaseCNRNumber></Jdps:OldCaseCNRNumber>\n" +
        "    <Jdps:OldCaseNumber></Jdps:OldCaseNumber>\n" +
        "  </Jdps:OldCase>\n" +
        "  <Jdps:Fixity>\n" +
        "    <Jdps:Checksum algorithm=\"\"/>\n" +
        "  </Jdps:Fixity>\n" +
        "  <Jdps:DigitalSignatures>\n" +
        "    <Jdps:DigitalSignature>\n" +
        "      <Jdps:Signer>\n" +
        "        <Jdps:CommonName></Jdps:CommonName>\n" +
        "        <Jdps:Email></Jdps:Email>\n" +
        "        <Jdps:State></Jdps:State>\n" +
        "        <Jdps:Location></Jdps:Location>\n" +
        "        <Jdps:OrganizationUnit></Jdps:OrganizationUnit>\n" +
        "        <Jdps:Organization></Jdps:Organization>\n" +
        "        <Jdps:Country></Jdps:Country>\n" +
        "      </Jdps:Signer>\n" +
        "      <Jdps:SigningTime></Jdps:SigningTime>\n" +
        "      <Jdps:Reason></Jdps:Reason>\n" +
        "      <Jdps:Location></Jdps:Location>\n" +
        "      <Jdps:Signature>\n" +
        "        <Jdps:Validity>\n" +
        "          <Jdps:Starts></Jdps:Starts>\n" +
        "          <Jdps:Ends></Jdps:Ends>\n" +
        "        </Jdps:Validity>\n" +
        "      </Jdps:Signature>\n" +
        "      <Jdps:Issuer>\n" +
        "        <Jdps:CommonName></Jdps:CommonName>\n" +
        "        <Jdps:Email></Jdps:Email>\n" +
        "        <Jdps:State></Jdps:State>\n" +
        "        <Jdps:OrganizationUnit></Jdps:OrganizationUnit>\n" +
        "        <Jdps:Organization></Jdps:Organization>\n" +
        "        <Jdps:Country></Jdps:Country>\n" +
        "      </Jdps:Issuer>\n" +
        "    </Jdps:DigitalSignature>\n" +
        "  </Jdps:DigitalSignatures>\n" +
        "  <Jdps:AccessControl>\n" +
        "    <Jdps:Disclosure disclosureClassification=\"PUBLIC\"/>\n" +
        "  </Jdps:AccessControl>\n" +
        "  <Jdps:Provenance>\n" +
        "    <Jdps:Origin>\n" +
        "      <Jdps:Organization></Jdps:Organization>\n" +
        "      <Jdps:GeographicalAddress>\n" +
        "        <Jdps:Street></Jdps:Street>\n" +
        "        <Jdps:Village></Jdps:Village>\n" +
        "        <Jdps:SubDistrict></Jdps:SubDistrict>\n" +
        "        <Jdps:District></Jdps:District>\n" +
        "        <Jdps:State></Jdps:State>\n" +
        "        <Jdps:PIN></Jdps:PIN>\n" +
        "      </Jdps:GeographicalAddress>\n" +
        "      <Jdps:DeviceAddress>\n" +
        "        <Jdps:IPAddress version=\"\"></Jdps:IPAddress>\n" +
        "        <Jdps:MACAddress></Jdps:MACAddress>\n" +
        "      </Jdps:DeviceAddress>\n" +
        "    </Jdps:Origin>\n" +
        "    <Jdps:Migration></Jdps:Migration>\n" +
        "  </Jdps:Provenance>\n" +
        "</Jdps:ecourtcase>";
        Files.write(cnrFolder.resolve(xmlName), xmlContent.getBytes());

        String pdfName = cnr + ".pdf";
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream(cnrFolder.resolve(pdfName).toFile()));
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
        document.open();
        try {
            document.add(new Paragraph("Document Name: " + docName.repeat(100)));
        } catch (DocumentException e) {
            throw new RuntimeException(e);
        }
        document.close();

        String zipFileName = cnr + ".zip";
        File zipFile = new File(BASE_DIR + zipFileName);
        try (ZipOutputStream zos = new ZipOutputStream(new FileOutputStream(zipFile))) {
            for (String fileName : new String[]{jsonName, xmlName, pdfName}) {
                Path filePath = cnrFolder.resolve(fileName);
                ZipEntry entry = new ZipEntry(fileName);
                zos.putNextEntry(entry);
                Files.copy(filePath, zos);
                zos.closeEntry();
            }
        }

        return zipFile;
    }

    public String computeSHA256(File file) throws IOException {

        try (InputStream fis = new FileInputStream(file)) {

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] block = new byte[4096];
            int length;

            while ((length = fis.read(block)) > 0) {

                digest.update(block, 0, length);

            }

            StringBuilder hexString = new StringBuilder();

            for (byte b : digest.digest()) {

                hexString.append(String.format("%02x", b));

            }

            return hexString.toString();

        } catch (Exception e) {

            throw new IOException("Error computing SHA256", e);

        }
    }

}
