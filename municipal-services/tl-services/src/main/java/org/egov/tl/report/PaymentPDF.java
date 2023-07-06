package org.egov.tl.report;

import java.io.BufferedInputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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
import org.json.JSONArray;
import org.json.JSONObject;
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
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.Font.FontFamily;


import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
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
//	private static String hindifont = "/opt/UPYOG/municipal-services/tl-services/src/main/resources/font/FreeSans.ttf";
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
		
		
//	        LocalDate date1 = LocalDate.of(1997, 07, 02);
//
//	        LocalDate date2 = LocalDate.of(2023, 07, 01);
//	        Period dfrnc = Period.between(date1, date2);
//
//	        int years = dfrnc.getYears();
//	        int months = dfrnc.getMonths();
//	        int days = dfrnc.getDays();
//
//	        System.out.println("Your age is " + years + " years, " + months + " months, and " + days + " days.");
	        
	        LocalDate date1 = LocalDate.of(1997, 7, 2);
	        LocalDate date2 = LocalDate.of(2023, 7, 1);

	        long years = ChronoUnit.YEARS.between(date1, date2);
	        long months = ChronoUnit.MONTHS.between(date1.plusYears(years), date2);
	        long days = ChronoUnit.DAYS.between(date1.plusYears(years).plusMonths(months), date2);

	        System.out.println("Your age is " + years + " years, " + months + " months, and " + days + " days.");

		
		String billId = jsonData.get("Transaction").get(1).get("billId").asText();
		
		System.out.println(billId);
		
		
		 String jdbcUrl = "jdbc:postgresql://tcp.abm.com:5432/devdb";
	        String username = "postgres";
	        String password = "postgres";
		
	        try (Connection connection = DriverManager.getConnection(jdbcUrl, username, password)) {
	            String query = "select * from public.eg_pg_transactions_dump";
	            Statement statement = connection.createStatement();
	            ResultSet resultSet = statement.executeQuery(query);
	            
	            System.out.println(resultSet);
//	            // Generate the PDF
//	            Document document = new Document(PageSize.A4);
//	            PdfWriter.getInstance(document, new FileOutputStream("output.pdf"));
//	            document.open();
	            
	            JSONArray jsonArray = new JSONArray();

	        
	         ResultSetMetaData metaData = resultSet.getMetaData();
	         int columnCount = metaData.getColumnCount();

	         while (resultSet.next()) {
	             JSONObject jsonObject = new JSONObject();
	             
	             for (int i = 1; i <= columnCount; i++) {
	                 String columnName = metaData.getColumnName(i);
	                 Object columnValue = resultSet.getObject(i);
	                 
	             
	                 jsonObject.put(columnName, columnValue);
	             }
	             
	             
	             jsonArray.put(jsonObject);
	         }

	        
	         String jsonResult = jsonArray.toString();

	        
	         System.out.println(jsonResult);

	            while (resultSet.next()) {
	                // Get the JSON object from the result set
	                JSONObject json = new JSONObject(resultSet);
	                
	                System.out.println(json);

//	                // Extract data from the JSON object
//	                String column1Data = json.getString("column1");
//	                String column2Data = json.getString("column2");
//	                // ... (repeat for other columns)
//
//	                // Add data to the PDF
//	                Paragraph paragraph = new Paragraph();
//	                paragraph.add("Column 1: " + column1Data);
//	                paragraph.add("\nColumn 2: " + column2Data);
//	                // ... (repeat for other columns)
//
//	                paragraph.setAlignment(Element.ALIGN_LEFT);
//	                document.add(paragraph);
	            }
//	            document.close();
	            System.out.println("PDF generated successfully.");
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        
		
//		boolean flag = true;
			LicenseServiceResponseInfo licenseServiceResponceInfo = licenseService.getNewServicesInfoById(applicationNumber, requestInfo);
		Image img = Image.getInstance("Image20.png");
	
		img.scaleAbsolute(150, 150);
		
		 Image watermarkImage = Image.getInstance("govt.jpg");
		 
		

        
		Document doc = new Document(PageSize.A4, 36, 36, 90, 36);
		PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(environment.getProperty("egov.jsontopdf")+applicationNumber+".pdf"));
		doc.open();
		
		
		 PdfContentByte content = writer.getDirectContentUnder();
         PdfGState gs = new PdfGState();
         gs.setFillOpacity(0.2f); // Set the transparency (0.0 to 1.0, 0.0 being fully transparent)
         gs.setStrokeOpacity(0.2f);
         content.setGState(gs);
         watermarkImage.setAbsolutePosition(0,50); 
        
         
         
		 PdfContentByte contentByte = writer.getDirectContentUnder();
         contentByte.addImage(watermarkImage);
		
	
		
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
//		doc.add(p2);
//		doc.add(p);
		doc.add(p1);
		p1.setSpacingAfter(50);
		doc.add(p4);
		doc.add(p3);
		
	
		
		

	PdfPTable table = null;
	
	
	table = new PdfPTable(4);
	table.setSpacingBefore(10f);
	table.setSpacingAfter(10f);
	table.setWidthPercentage(100f);
	
	PdfPCell ce = new PdfPCell(new Phrase("Case Type"));
	ce.setHorizontalAlignment(Element.ALIGN_CENTER);
    table.addCell(ce);
    
    ce = new PdfPCell(new Phrase("SCRUTINY"));
    ce.setHorizontalAlignment(Element.ALIGN_CENTER);
    table.addCell(ce);
    
    ce = new PdfPCell(new Phrase("Application Type"));
    ce.setHorizontalAlignment(Element.ALIGN_CENTER);
    table.addCell(ce);
    
    ce = new PdfPCell(new Phrase("LICENSE"));
    ce.setHorizontalAlignment(Element.ALIGN_CENTER);
    table.addCell(ce);
	
	
	
	
	  table.addCell("Charges Type");
	  
	 
	  table.addCell("SCRUTINY Fee");
	  
	
	  table.addCell("");
	  
	  table.addCell("");
	  
	  doc.add(table);
	  
	  
	  table = new PdfPTable(4);
		table.setSpacingBefore(10f);
		table.setSpacingAfter(20f);
		table.setWidthPercentage(100f);
		
		PdfPCell cq = new PdfPCell(new Phrase("Case Id"));
		cq.setHorizontalAlignment(Element.ALIGN_CENTER);
	    table.addCell(cq);
	    
	    cq = new PdfPCell(new Phrase("LC-5428~139315~5428"));
	    cq.setHorizontalAlignment(Element.ALIGN_CENTER);
	    table.addCell(cq);
	    
	    cq = new PdfPCell(new Phrase("Application Id"));
	    cq.setHorizontalAlignment(Element.ALIGN_CENTER);
	    table.addCell(cq);
	    
	    cq = new PdfPCell(new Phrase("158739~LC-5428A"));
	    cq.setHorizontalAlignment(Element.ALIGN_CENTER);
	    table.addCell(cq);
		
		
		
		
		  table.addCell("Reference No.");
		  
		 
		  table.addCell("");
		  
		
		  table.addCell("");
		  
		  table.addCell("");
		  
		  table.addCell("Mobile No.");
		  
			 
		  table.addCell("9555955563");
		  
		
		  table.addCell("Email Id");
		  
		  table.addCell("meetkumar00@gmail.com");
		  
		  doc.add(table);

//  		if(licenseServiceResponceInfo.getNewServiceInfoData()!=null && licenseServiceResponceInfo.getNewServiceInfoData().size()>0) {
		
			
		
				
//				LicenseDetails licenseDetails = licenseServiceResponceInfo.getNewServiceInfoData().get(i);
//				
				
				
			
					
					table = new PdfPTable(2);
					table.setSpacingBefore(10f);
					table.setSpacingAfter(30f);
					table.setWidthPercentage(100f);
					
					//table.addCell(replaceNullWithNA("Name");
					  //table.addCell(replaceNullWithNA(licenseDetails.getApplicantInfo().getDevDetail().getAddInfo().getName()));
					  
					  table.addCell("Transaction No");
					  
					  PdfPCell cell = new PdfPCell();
			           
			            cell.setHorizontalAlignment(Element.ALIGN_RIGHT);
			            
			            cell.addElement(new Paragraph(jsonData.get("Transaction").get(0).get("txnId").asText(), blackFont1));
					  table.addCell(cell);
					  
					  
					  table.addCell("Transaction Date");
					  table.addCell(jsonData.get("Transaction").get(0).get("txnAmount").asText());	
					  
					  table.addCell("Total Amount");
					  table.addCell(jsonData.get("Transaction").get(0).get("txnAmount").asText());
				
					  
					  
					  
					  table.addCell("Payment Agreegator");
					  table.addCell(jsonData.get("Transaction").get(0).get("bank").asText());
					  
//					  doc.add(table);
					  
					  
					  Paragraph spacing = new Paragraph();
					  spacing.setSpacingAfter(10f);
					  
					  doc.add(new Paragraph("(1)Transaction No.                                                                                   "+ jsonData.get("Transaction").get(0).get("txnId").asText(), blackFont1 ));
					  doc.add(spacing);
					  doc.add(new Paragraph("(2)Transaction Date.                                                                                 "+ "01/05/2023 13:06:33", blackFont1 ));
					  doc.add(spacing);
					  doc.add(new Paragraph("(3)GR No/Txn. No                                                                                     "+ "Failure", blackFont1 ));
					  doc.add(spacing);
					  doc.add(new Paragraph("(4)Status                                                                                              "+ jsonData.get("Transaction").get(0).get("txnStatus").asText(), blackFont1 ));
					  doc.add(spacing);
					 
					  doc.add(new Paragraph("(5)Received Amount Date                                                                                    "+ "02/05/2023 13:06:33", blackFont1 ));
					  doc.add(spacing);
					  doc.add(new Paragraph("(6)Payment Agreegator                                                                                          "+ jsonData.get("Transaction").get(0).get("bank").asText(), blackFont1 ));
					  doc.add(spacing);
					  doc.add(new Paragraph("(7)Total Amount                                                                                      "+ jsonData.get("Transaction").get(0).get("txnId").asText(), blackFont1 ));
					  doc.add(spacing);
					  doc.add(new Paragraph("(8)Remarks                                                                                      " ));
					  doc.add(spacing);
					  
					  
					 
//					  doc.add(new Paragraph("     NOTE1: This is subjected to realization/credit of the payment to Department Account."));
					  
					 
					  Paragraph spacing1 = new Paragraph();
					  spacing1.setSpacingAfter(200f);
					  
//					  doc.add(spacing1);
			            
			            Paragraph l = new Paragraph();
			            l.setFont(new Font(FontFamily.HELVETICA, 12, Font.BOLD));
			            l.add("STEPS TO VERIFY PAYMENT STATUS WITH THE HELP OF QR CODE: \r\n"
			            		+ "1.Install QR scanner app on your mobile,which can be downloaded free from App Store/Play Store.\r\n"
			            		+ "2: Once QR scanner app is installed, open the app and point it to code on the receipt.\r\n"
			            		+ "3: The application will scan the QR code and a page will open with, <Open Website>, <Open URL>.This option is app\r\n"
			            		+ "dependent.\r\n"
			            		+ "4: Click on this option. Payment status Verfication page will open\r\n"
			            		+ "Requirement:\r\n"
			            		+ "1: User needs to have a QR scanner in his mobile. QR scanner apps are free and can be downloaded from the App store\r\n"
			            		+ "on your mobile.\r\n"
			            		+ "2: Internet connection on Mobile");
			           
//			            doc.add(l); 
//			            
//			            addParagraph(doc, "Transaction No. TCP35781023501134311", blackFont1, Element.ALIGN_LEFT);
//

				
				
		
				
		
				
	
					
				
					
					
					
					
					
					
					

				
					

					
                  
                  
 
                
//		      PdfPTable t = new PdfPTable(2);
//	            PdfPCell cellOne = new PdfPCell(new Phrase("Hello"));
//	            cellOne.setBorder(Rectangle.NO_BORDER);
//	            cellOne.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
//	            
//	            
//	            PdfPCell cellTwo = new PdfPCell(new Phrase("World"));
//	            cellTwo.setBorder(Rectangle.NO_BORDER);
//	            cellTwo.setHorizontalAlignment(PdfPCell.ALIGN_LEFT);
//
//	          
//
//	            t.addCell(cellOne);
//	            t.addCell(cellTwo);
//			 
//			  
//	            doc.add(t);
  

					
					
			
			
			
		
		doc.close();
		writer.close();
		
	        
}


	
	
	
	
//private String replaceNullWithNA(String param) {
//	return param==null||param.equals("null")?("N/A"):(param);
//}

}
