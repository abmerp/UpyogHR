package org.egov.tl.report;

import java.io.BufferedInputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URLConnection;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tl.config.TLConfiguration;
import org.egov.tl.service.LicenseService;
import org.egov.tl.util.BPANotificationUtil;
import org.egov.tl.web.models.AddRemoveAuthoizedUsers;
import org.egov.tl.web.models.AppliedLandDetails;
import org.egov.tl.web.models.DirectorsInformation;
import org.egov.tl.web.models.DirectorsInformationMCA;
import org.egov.tl.web.models.FeesAndCharges;
import org.egov.tl.web.models.GISDeatils;
import org.egov.tl.web.models.LicenseDetails;
import org.egov.tl.web.models.LicenseServiceResponseInfo;
import org.egov.tl.web.models.PurposeDetails;
import org.egov.tl.web.models.ResponseTransaction;
import org.egov.tl.web.models.ShareholdingPattens;
import org.json.simple.JSONObject;
import org.objectweb.asm.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Font.FontFamily;


import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;


@RestController
public class PaymentPDF {
	

	private static Font catFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLDITALIC,BaseColor.BLUE);
	private static Font blackFont = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLDITALIC, BaseColor.BLACK);
	private static Font blackFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, BaseColor.BLACK);

//    private static String hindifont = "D:\\Bikash_UPYOG\\UPYOG\\municipal-services\\tl-services\\src\\main\\resources\\font\\FreeSans.ttf";
//	private static String hindifont = "D:\\upyog code\\UPYOG1\\UPYOG\\municipal-services\\tl-services\\src\\main\\resources\\font\\FreeSans.ttf";
	private static String hindifont = "/opt/UPYOG/municipal-services/tl-services/src/main/resources/font/FreeSans.ttf";
//	private static String hindifont ="D:\\Workspace_27-04-2023\\UPYOG\\municipal-services\\tl-services\\src\\main\\resources\\font\\\\FreeSans.ttf";

    
	@Autowired BPANotificationUtil bPANotificationUtil;
	@Autowired LicenseService licenseService;
	
	@Value("${egov.pg-service.host}")
	private String pgHost;
	
    @Value("${egov.pg-service.search.path}")
	private String pgSearchPath;

	@Autowired public RestTemplate restTemplate;
	@Autowired TLConfiguration config;
	
	@Autowired
	Environment environment;
	
	@Autowired
	RestTemplate rest;
	
	@Autowired
	ObjectMapper mapper;
	

	
	
	
	@RequestMapping(value = "payment/pdf", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public void jsonToPdf(@ModelAttribute("RequestInfo") RequestInfo requestInfo, HttpServletRequest request,
			HttpServletResponse response, @RequestParam("applicationNumber") String applicationNumber) throws IOException {

		try {
			createNewLicensePDF(requestInfo,applicationNumber);
		} catch (DocumentException e) {
			e.printStackTrace();
		}

		File file = new File(environment.getProperty("egov.jsontopdf")+applicationNumber+".pdf");
				if (file.exists()) {
			String mimeType = URLConnection.guessContentTypeFromName(file.getName());
			if (mimeType == null) {
				mimeType = "application/pdf";
			}
			response.setContentType(mimeType);
			response.setHeader("Content-Disposition", String.format("inline; filename=\"" + file.getName() + "\""));

			response.setContentLength((int) file.length());
			InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
			FileCopyUtils.copy(inputStream, response.getOutputStream());
		}
	}


	private void createNewLicensePDF(RequestInfo requestInfo, String applicationNumber) 
			throws MalformedURLException, IOException, DocumentException {
		Map<String, Object> request = new HashMap<>();
		request.put("txnId", "");
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, httpHeaders);
		Object paymentSearch = null;

	
		String uri = pgHost + pgSearchPath;
		paymentSearch = rest.postForObject(uri, entity, Map.class);
		
		String data = null;
		try {
			data = mapper.writeValueAsString(paymentSearch);
		} catch (JsonProcessingException e) { // TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("data:--"+data);

		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode jsonData = objectMapper.readTree(data);

		System.out.println(jsonData);
		
	//	JSONObject json = new JSONObject();
		
		
		
		String billId = jsonData.get("Transaction").get(1).get("billId").asText();
		
		System.out.println(billId);
		

		
//		boolean flag = true;
			LicenseServiceResponseInfo licenseServiceResponceInfo = licenseService.getNewServicesInfoById(applicationNumber, requestInfo);
		Image img = Image.getInstance("govt.jpg");
		img.scaleAbsolute(200, 100);
		
		Document doc = new Document(PageSize.A4, 36, 36, 90, 36);
		PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(environment.getProperty("egov.jsontopdf")+applicationNumber+".pdf"));
		doc.open();
	
		
		 Paragraph p2 = new Paragraph("Department of Town & Country Planning, Haryana", new
				 Font(FontFamily.HELVETICA, 18, Font.BOLDITALIC, new BaseColor(0, 0, 255)) );
		 
//		  ColumnText.showTextAligned(writer.getDirectContentUnder(),
//	                Element.ALIGN_CENTER, new Phrase("AccessInfotechIndia.com", blackFont),
//	                297.5f, 421, writer.getPageNumber() % 2 == 1 ? 45 : -45);
		 
		
//		 String imagePath = "path/to/your/image.png";
//		 Image watermarkImage = Image.getInstance(imagePath);

		 
		 final ZonedDateTime now = ZonedDateTime.now(ZoneId.of(environment.getProperty("egov.timeZoneName")));
		
		 DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a");
		 String formattedDateTime = now.format(formatter);
		 
		
		 Paragraph p3 = new Paragraph(formattedDateTime);
		 
		Paragraph p1 = new Paragraph();
		Paragraph p = new Paragraph();
		Paragraph p4 = new Paragraph();
	
		p.setFont(blackFont);
		p.add("Application Number:-");
		p.add(applicationNumber);
		p1.setFont(blackFont);
		p1.add("[E-Payment Receipt]");
		p4.add("(To be retained by applicant)");
		
		img.setAlignment(Element.ALIGN_CENTER);
		p1.setAlignment(Element.ALIGN_CENTER);
		p2.setAlignment(Element.ALIGN_CENTER);
		p4.setAlignment(Element.ALIGN_CENTER);
		p3.setAlignment(Element.ALIGN_RIGHT);
		doc.add(img);
		p2.setSpacingAfter(50);
		doc.add(p2);
		doc.add(p);
		doc.add(p1);
		p1.setSpacingAfter(50);
		doc.add(p4);
		doc.add(p3);
		
	
		
		

	PdfPTable table = null;
	
	
  		if(licenseServiceResponceInfo.getNewServiceInfoData()!=null && licenseServiceResponceInfo.getNewServiceInfoData().size()>0) {
		
			
			for(int i=0;i<licenseServiceResponceInfo.getNewServiceInfoData().size();i++) {
				
				LicenseDetails licenseDetails = licenseServiceResponceInfo.getNewServiceInfoData().get(i);
				
				
				
			
					
					table = new PdfPTable(2);
					table.setSpacingBefore(10f);
					table.setSpacingAfter(10f);
					table.setWidthPercentage(100f);
					
					//table.addCell(replaceNullWithNA("Name");
					  //table.addCell(replaceNullWithNA(licenseDetails.getApplicantInfo().getDevDetail().getAddInfo().getName()));
					  
					  table.addCell("Transaction No");
					  table.addCell(jsonData.get("Transaction").get(0).get("txnId").asText());
					  
					  table.addCell("Total Amount");
					  table.addCell(jsonData.get("Transaction").get(0).get("txnAmount").asText());
				
					  
					  table.addCell("Payment Agreegator");
					  table.addCell(jsonData.get("Transaction").get(0).get("bank").asText());
					  
					  
					  table.addCell("Developer Type");
					  table.addCell((licenseDetails.getApplicantInfo().getDevDetail().getAddInfo().getShowDevTypeFields()));
					  
					  table.addCell("Cin_Number");
					  table.addCell((licenseDetails.getApplicantInfo().getDevDetail().getAddInfo().getCin_Number()));
					  
					 
					  
					  
					doc.add(table);
				
				
		
				
		
				
	
					
				
					
					
					
					
					
					
					

				
					

					
                  
                  
 
                }
  

					
					
			
			
			
		
		doc.close();
		writer.close();
		}
		
}
	
	
	
//private String replaceNullWithNA(String param) {
//	return param==null||param.equals("null")?("N/A"):(param);
//}

}
