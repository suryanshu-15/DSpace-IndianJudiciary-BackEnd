package org.dspace.app.rest.diracai.service;

import com.amazonaws.util.IOUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import in.cdac.hcdc.jtdr.metadata.JTDRMetadataSchema;
import in.cdac.hcdc.jtdr.metadata.schema.*;
import org.dspace.app.rest.diracai.Repository.FileHashRecordRepository;
import org.dspace.content.Bitstream;
import org.dspace.content.Bundle;
import org.dspace.content.Diracai.FileHashRecord;
import org.dspace.content.Item;
import org.dspace.content.MetadataValue;
import org.dspace.content.service.BitstreamService;
import org.dspace.content.service.ItemService;
import org.dspace.core.Context;
import org.dspace.services.ConfigurationService;
import org.dspace.storage.bitstore.service.BitstreamStorageService;
import java.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class ZipExportService {

    @Autowired
    private ItemService itemService;

    @Autowired
    private BitstreamService bitstreamService;

    @Autowired
    private BitstreamStorageService bitstreamStorageService;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private FileHashRecordRepository fileHashRecordRepository;

    public File generateZipForItem(Context context, Item item) throws Exception {
        String cino = getCinoFromMetadata(item,"dc","cino",null);
        String caseType = getCinoFromMetadata(item,"dc","casetype",null);
        String caseNumber = getCinoFromMetadata(item,"dc","case","number");
        String petitionerName = getCinoFromMetadata(item,"dc","pname",null);
        String respondentName = getCinoFromMetadata(item,"dc","rname",null);
        String advocateName = getCinoFromMetadata(item,"dc","paname",null);
        String judgeName = getCinoFromMetadata(item,"dc","contributor","author");
        String disposalDate = getCinoFromMetadata(item,"dc","date","disposal");
        String district = getCinoFromMetadata(item,"dc","district",null);
        String caseYear = getCinoFromMetadata(item,"dc","caseyear",null);
        String scanDate = getCinoFromMetadata(item,"dc","date","scan");
        String verifiedBy = getCinoFromMetadata(item,"dc","verified-by",null);
        String dateVerification = getCinoFromMetadata(item,"dc","date","verification");
        String batchNumber = getCinoFromMetadata(item,"dc","batch-number",null);
        String barcodeNumber = getCinoFromMetadata(item,"dc","barcode",null);
        String fileSize = getCinoFromMetadata(item,"dc","size",null);
        String characterCount = getCinoFromMetadata(item,"dc","char-count",null);
        String noOfPages = getCinoFromMetadata(item,"dc","pages",null);
        String title = getCinoFromMetadata(item,"dc","title",null);
        //String docType = getCinoFromMetadata(item,"dc","doc","type");
        String docType = getDocumentType(item);

        System.out.println("Document type: " + docType);



        if (cino == null) throw new Exception("CINO not found in metadata");

        String dspaceDir = configurationService.getProperty("dspace.dir");

        File baseDir = new File(dspaceDir, "jtdr");
        if (!baseDir.exists()) baseDir.mkdirs();

        File cinoDir = new File(baseDir, cino);
        if (!cinoDir.exists()) cinoDir.mkdirs();

        List<Map<String, String>> docList = new ArrayList<>();
        for (Bundle bundle : item.getBundles("ORIGINAL")) {
            for (Bitstream bitstream : bundle.getBitstreams()) {
                InputStream is = bitstreamService.retrieve(context, bitstream);
                File file = new File(cinoDir, bitstream.getName());
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    IOUtils.copy(is, fos);
                }
                docList.add(Map.of(
                        "docName", bitstream.getName(),
                        "docType",docType,
                        "docDate", LocalDate.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                ));
            }

        }

        // Step 3: Write <CINO>_doc.json
        File jsonFile = new File(cinoDir, cino + "_doc.json");
        new ObjectMapper().writerWithDefaultPrettyPrinter()
                .writeValue(jsonFile, docList);

        // Step 4: Write <CINO>_Metadata.xml
        File xmlFile = new File(cinoDir, cino + "_Metadata.xml");
        generateMetadataXml(item, cino ,caseType, caseNumber,petitionerName,respondentName,advocateName, judgeName ,disposalDate,district, caseYear,scanDate,verifiedBy,dateVerification,barcodeNumber,fileSize,characterCount,noOfPages,title,docType,xmlFile);


        // Step 5: Create zip file
        File zipFile = new File(baseDir, cino + ".zip");
        try (FileOutputStream fos = new FileOutputStream(zipFile);
             ZipOutputStream zos = new ZipOutputStream(fos)) {
            zipFolder(cinoDir, cinoDir.getName(), zos);
        }

        FileHashRecord fileHashRecord = new FileHashRecord();
        fileHashRecord.setFileName(caseType+"_"+title+"_"+caseYear);
        fileHashRecord.setBatchName(batchNumber);
        fileHashRecord.setCaseType(caseType);
        fileHashRecord.setCaseNo(caseNumber);
        fileHashRecord.setStatus("Zip File Created");
        fileHashRecord.setCreatedAt(LocalDateTime.now());
        fileHashRecord.setCinoNumber(cino);
        fileHashRecord.setFileCount(docList.size());
        fileHashRecord.setCreatedBy(context.getCurrentUser().getName());
        fileHashRecordRepository.save(fileHashRecord);
        return zipFile;
    }


    private String getCinoFromMetadata(Item item,String schema, String element , String qualifier) {
        List<MetadataValue> values = itemService.getMetadata(item, schema, element, qualifier, Item.ANY);
        return values.isEmpty() ? null : values.get(0).getValue();
    }

    private String getDocumentType(Item item) {
        List<MetadataValue> docTypeList = itemService.getMetadata(item, "dc", "type", "document", Item.ANY);
        if (docTypeList != null && !docTypeList.isEmpty()) {
            return docTypeList.get(0).getValue();
        }
        return "Unknown Document Type"; // fallback if missing
    }

    private void generateMetadataXml(
            Item item,
            String cino,
            String caseType,
            String caseNumber,
            String petitionerName,
            String respondentName,
            String advocateName,
            String judgeName,
            String disposalDate,
            String district,
            String caseYear,
            String scanDate,
            String verifiedBy,
            String dateVerification,
            String barcodeNumber,
            String fileSize,
            String characterCount,
            String noOfPages,
            String title,
            String docType,
            File xmlOutputFile
    )

    {
        ECourtCaseType eCourtCaseType = new ECourtCaseType();

        System.out.println(
                "cino=" + cino +
                        ", caseType=" + caseType +
                        ", caseNumber=" + caseNumber +
                        ", petitionerName=" + petitionerName +
                        ", respondentName=" + respondentName +
                        ", advocateName=" + advocateName +
                        ", judgeName=" + judgeName +
                        ", disposalDate=" + disposalDate +
                        ", district=" + district +
                        ", caseYear=" + caseYear +
                        ", scanDate=" + scanDate +
                        ", verifiedBy=" + verifiedBy +
                        ", dateVerification=" + dateVerification +
                        ", barcodeNumber=" + barcodeNumber +
                        ", fileSize=" + fileSize +
                        ", characterCount=" + characterCount +
                        ", noOfPages=" + noOfPages +
                        ", title=" + title
//                        ", docType=" + docType
        );

        CaseTypeInformation caseTypeInformation = new CaseTypeInformation();
        caseTypeInformation.setCaseCNRNumber(cino);
        caseTypeInformation.setCaseNature(caseType);
        caseTypeInformation.setCaseNumber(title);
        caseTypeInformation.setCaseTypeName(caseType);
        caseTypeInformation.setNameOfDistrict(district);
        caseTypeInformation.setRegistrationYear(caseYear);
        caseTypeInformation.setRegistrationDate(caseYear+"-01-01 00:00:00");
        caseTypeInformation.setRegistrationNumber(title);


        eCourtCaseType.setCase(caseTypeInformation);

        LitigantTypeInformation litigant = new LitigantTypeInformation();
        PetitionerTypeInformation petitioner = new PetitionerTypeInformation();
        petitioner.setPetitionerName(petitionerName);
        litigant.getPetitioner().add(petitioner);


        RespondentTypeInformation respondent = new RespondentTypeInformation();
        respondent.setRespondentName(respondentName);
        litigant.getRespondent().add(respondent);

        caseTypeInformation.setLitigant(litigant);

        JudgeTypeInformation judgeTypeInformation = new JudgeTypeInformation();

        JudgeInformation judgeInformation = new JudgeInformation();
        judgeInformation.setJudgeName(judgeName);
        judgeTypeInformation.getJudgeInfo().add(judgeInformation);
        eCourtCaseType.setJudge(judgeTypeInformation);


        StatusOfCasesTypeInformation statusOfCasesTypeInformation = new StatusOfCasesTypeInformation();
        statusOfCasesTypeInformation.setDateOfDisposal(disposalDate);
        eCourtCaseType.setStatusOfCases(statusOfCasesTypeInformation);


        DigitizationTypeInformation digitizationTypeInformation = new DigitizationTypeInformation();
        MasterFileType masterFileType = new MasterFileType();
        masterFileType.setFileSize(fileSize);
        digitizationTypeInformation.setMasterFile(masterFileType);
        digitizationTypeInformation.setVerifiedBy(verifiedBy);
//        digitizationTypeInformation.setDocumentType(docType);
        digitizationTypeInformation.setDocumentType(docType); // <-- set document type


        eCourtCaseType.setDigitization(digitizationTypeInformation);





        AdvocateTypeInformation advocate = new AdvocateTypeInformation();
        advocate.setAdvocateName(advocateName);
        caseTypeInformation.getAdvocate().add(advocate);


        JTDRMetadataSchema.createXML(eCourtCaseType, xmlOutputFile.getAbsolutePath());
    }



    private void zipFolder(File folder, String basePath, ZipOutputStream zos) throws Exception {
        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                zipFolder(file, basePath + "/" + file.getName(), zos);
            } else {
                try (FileInputStream fis = new FileInputStream(file)) {
                    ZipEntry entry = new ZipEntry(basePath + "/" + file.getName());
                    zos.putNextEntry(entry);
                    IOUtils.copy(fis, zos);
                    zos.closeEntry();
                }
            }
        }
    }
}
