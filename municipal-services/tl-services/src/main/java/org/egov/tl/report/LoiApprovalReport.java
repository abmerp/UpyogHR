//package org.egov.tl.report;
//
//import java.io.BufferedInputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.net.URLConnection;
//import java.util.Arrays;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.Map;
//
//import javax.servlet.http.HttpServletResponse;
//
//import org.egov.common.contract.request.RequestInfo;
//import org.egov.tl.service.LicenseService;
//import org.egov.tl.service.dao.LicenseServiceDao;
//import org.egov.tl.util.ConvertNumberToWord;
//import org.egov.tl.web.models.ApplicantInfo;
//
//import org.egov.tl.web.models.LicenseServiceResponseInfo;
//import org.egov.tl.web.models.FeesAndCharges;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.core.env.Environment;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.MediaType;
//import org.springframework.http.ResponseEntity;
//import org.springframework.util.FileCopyUtils;
//import org.springframework.util.LinkedMultiValueMap;
//import org.springframework.util.MultiValueMap;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;
//import org.springframework.web.client.RestTemplate;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.itextpdf.text.BaseColor;
//import com.itextpdf.text.Chapter;
//import com.itextpdf.text.Chunk;
//import com.itextpdf.text.Document;
//import com.itextpdf.text.DocumentException;
//import com.itextpdf.text.Element;
//import com.itextpdf.text.Font;
//import com.itextpdf.text.List;
//import com.itextpdf.text.ListItem;
//import com.itextpdf.text.Paragraph;
//import com.itextpdf.text.Phrase;
//import com.itextpdf.text.pdf.PdfPCell;
//import com.itextpdf.text.pdf.PdfPTable;
//import com.itextpdf.text.pdf.PdfWriter;
//import com.itextpdf.text.pdf.draw.LineSeparator;
//
//@RestController
//public class LoiApprovalReport {
//	@Value("${land-services.host}")
//	private String landServiceHost;
//	
//	@Value("${land-services.path}")
//	private String landServicePath;
//	
//	// private static String FILE = "D:\\Volume-E\\FirstPdf.pdf";
//
//	private String MY_FILE;
//	private static Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
//	private static Font redFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL, BaseColor.RED);
//	private static Font subFont = new Font(Font.FontFamily.TIMES_ROMAN, 16, Font.BOLD);
//	private static Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
//	private static Font normal = new Font(Font.FontFamily.TIMES_ROMAN, 12);
//
//	@Autowired
//	LicenseService newServiceInfoService;
//	@Autowired
//	Environment env;
//	@Autowired
//	LicenseService licenseService;
//
//	@Autowired
//	public RestTemplate restTemplate;
//
////	@RequestMapping(value = "/loi/report/generate", method = RequestMethod.POST)
////	public ResponseEntity<Map<String, Object>> generateLoiReport(
////			@RequestParam("applicationNumber") String applicationNumber, @RequestParam("userId") String userId,
////			@RequestBody RequestInfo requestInfo) throws IOException {
////		Map<String, Object> map = new HashMap<>();
////		createReport(applicationNumber, userId, requestInfo);
////		map.put("status", "success");
////		return new ResponseEntity<>(map, HttpStatus.OK);
////	}
////
////	@RequestMapping(value = "/loi/report/view", method = RequestMethod.GET)
////	public void viewLoiReport(@RequestParam("applicationNumber") String applicationNumber, HttpServletResponse response)
////			throws IOException {
////
////		this.MY_FILE = env.getProperty("egov.loireport");
////		System.out.println(MY_FILE);
////		File file = new File(MY_FILE + "loi-report-" + applicationNumber + ".pdf");
////
////		if (file.exists()) {
////			String mimeType = URLConnection.guessContentTypeFromName(file.getName());
////			if (mimeType == null) {
////				mimeType = "application/octet-stream";
////			}
////			response.setContentType(mimeType);
////			response.setHeader("Content-Disposition", String.format("inline; filename=\"" + file.getName() + "\""));
////
////			response.setContentLength((int) file.length());
////			InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
////			FileCopyUtils.copy(inputStream, response.getOutputStream());
////		}
////
////	}
//
//	@RequestMapping(value = "/loi/report/_create", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
//	public void createLoiReport(@RequestParam("applicationNumber") String applicationNumber,
//			HttpServletResponse response, @RequestParam("userId") String userId, @RequestBody RequestInfo requestInfo)
//			throws IOException {
//
//		this.MY_FILE = env.getProperty("egov.loireport");
//		System.out.println(MY_FILE);
//		createReport(applicationNumber, userId, requestInfo);
//
//		File file = new File(MY_FILE + "loi-report-" + applicationNumber + ".pdf");
//
//		if (file.exists()) {
//			String mimeType = URLConnection.guessContentTypeFromName(file.getName());
//			if (mimeType == null) {
//				mimeType = "application/octet-stream";
//			}
//			response.setContentType(mimeType);
//			response.setHeader("Content-Disposition", String.format("inline; filename=\"" + file.getName() + "\""));
//
//			response.setContentLength((int) file.length());
//			InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
//			FileCopyUtils.copy(inputStream, response.getOutputStream());
//		}
////		
////		int length = (int) file.length();
////		BufferedInputStream reader = new BufferedInputStream(new FileInputStream(file));
////		byte[] bytes = new byte[length];
////		reader.read(bytes, 0, length);
////		reader.close();
////
////		HttpHeaders responseHeaders = new HttpHeaders();
////		responseHeaders.put("Content-Disposition", Collections.singletonList("inline; filename=sample.pdf"));
////
////		Map<String, byte[]> map = new HashMap<String, byte[]>();
////		map.put("data", bytes);
////		return new ResponseEntity<Map<String, byte[]>>(map, HttpStatus.OK);
////		/*
////		 * .headers(responseHeaders) .contentType(MediaType.APPLICATION_PDF)
////		 * .contentLength(byteArr.length) .body(byteArr);
////		 */
//	}
//
//	public void createReport(String applicationNumber, String userId, RequestInfo requestInfo) {
//
//		Document doc = new Document();
//
//		LicenseServiceDao newServiceInfo = null;
//
//		// boolean result = newServiceInfoService.existsByLoiNumber("1212");
//		boolean result = newServiceInfoService.existsByApplicationNumber(applicationNumber);
////		LicenseServiceDao result = newServiceInfoService.findEgLicenceApplicationNumber(applicationNumber);
//		if (result) {
////			if (result!=null) {
////			newServiceInfo = getLatestNewServicesInfo("1212");
//			newServiceInfo = newServiceInfoService.findNewServicesInfoById(applicationNumber);
//		} else {
//			System.out.println("False");
//		}
//		try {
//			this.MY_FILE = env.getProperty("egov.loireport");
//			File file = new File(MY_FILE);
//			if (!file.exists()) {
//				file.mkdirs();
//			}
//			LicenseServiceResponseInfo licenseServiceResponceInfo = licenseService
//					.getNewServicesInfoById(applicationNumber, requestInfo);
//
//			PdfWriter writer = PdfWriter.getInstance(doc,
//					new FileOutputStream(MY_FILE + "loi-report-" + applicationNumber + ".pdf"));
//			doc.open();
//			addMetaData(doc);
//			addTitlePage(doc);
//			addMainPart(doc, applicationNumber, requestInfo, licenseServiceResponceInfo);
//			addContent(doc, newServiceInfo, licenseServiceResponceInfo);
//			loiReport(doc,applicationNumber,userId, requestInfo);
//
//			doc.close();
//			writer.close();
//
//		} catch (FileNotFoundException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		} catch (DocumentException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		} catch (JsonProcessingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}
//
//	private void loiReport(Document doc,String applicationNumber, String userId, RequestInfo requestInfo) {
//		try {
////		LineSeparator ls0 = new LineSeparator();
////		doc.add(new Chunk(ls0));
//
////		Paragraph p1 = new Paragraph();
////		p1.add("LOI Detail");
////		p1.setFont(catFont);
////		p1.setAlignment(Element.ALIGN_CENTER);
//////		doc.add(img);
////		doc.add(p1);
////		LineSeparator ls = new LineSeparator();
////		doc.add(new Chunk(ls));
//			float[] columnWidths = { 1, 11 };
//			PdfPTable table = new PdfPTable(columnWidths);
//			table.setWidthPercentage(100);
//			HttpHeaders headers = new HttpHeaders();
//			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
//			HttpEntity<RequestInfo> entity = new HttpEntity<RequestInfo>(requestInfo, headers);
//			/*
//			 * MultiValueMap<String,String> params = new LinkedMultiValueMap<>();
//			 * params.put("applicationNumber",applicationNumber);
//			 * params.put("userId",Collections.singletonList(userId));
//			 */
//				
//			int colIndex = 0;
//			java.util.List<String> hList = Arrays.asList("Sr No", "Description");
//			for (String columnTitle : hList) {
//				colIndex = colIndex + 1;
//
//				PdfPCell header = new PdfPCell();
//				header.setBackgroundColor(BaseColor.LIGHT_GRAY);
//				header.setBorderWidth(2);
////		        if(colIndex==1) {
////		            header.setRowspan(2);
////		        }else {
////		            header.setRowspan(10);
////		        }
//				header.setPhrase(new Phrase(columnTitle));
//				table.addCell(header);
//
//			}
//			try {
//			 String url =landServiceHost+landServicePath;
//				Map<String, Object> rest1 = restTemplate.exchange(
//						url+"?applicationNumber="+applicationNumber+"&userId="+userId,HttpMethod.POST, entity, HashMap.class).getBody();
//
//			java.util.List<Map<String, Object>> rest2 = (java.util.List<Map<String, Object>>) rest1.get("egScrutiny");
//		
//			int index = 1;
//			
//			if(rest2!=null&rest2.size()>0) {
//			for (Iterator iterator = rest2.iterator(); iterator.hasNext();) {
//				Map<String, Object> map = (Map<String, Object>) iterator.next();
//				System.out.println(map);
//				if (map.get("isLOIPart") != null ? (map.get("isLOIPart").toString().contains("true")) : (false)) {
//
//					table.addCell(index + "");
//					table.addCell(map.get("fieldIdL") != null ? map.get("fieldIdL") + "" : "N/A");
//					index++;
//				}
//
//			}
//		    	doc.add(table);
//			}
//			}catch (NullPointerException e) {
//				table.addCell("Not Found");			
//				doc.add(table);
//			}
//			
//			
//		} catch (DocumentException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
////		}catch (JsonProcessingException e) {
////			// TODO Auto-generated catch block
////			e.printStackTrace();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//
//	private static void addMetaData(Document document) {
//		document.addTitle("My first PDF");
//		document.addSubject("Using iText");
//		document.addKeywords("Java, PDF, iText");
//		document.addAuthor("Lars Vogel");
//		document.addCreator("Lars Vogel");
//	}
//
//	private static void addTitlePage(Document document) throws DocumentException {
//		Paragraph preface = new Paragraph();
//
//		preface.add(new Paragraph("Directorate of Town & Country Planning, Haryana", catFont));
//
//		preface.add(new Paragraph("SCO-71-75, 2nd Floor, Sector 17 C, Chandigarh ", smallBold));
//		preface.add(new Paragraph("Phone: 0172-2549349 e-mail:tcpharyana7@gmail.com", smallBold));
//		preface.add(new Paragraph("website:-http://tcpharyana.gov.in", smallBold));
//		LineSeparator ls = new LineSeparator();
//
//		preface.setAlignment(Element.ALIGN_CENTER);
//		document.add(preface);
//		document.add(new Chunk(ls));
//
//	}
//
//	private void addMainPart(Document document, String applicationNumber, RequestInfo requestInfo,
//			LicenseServiceResponseInfo licenseServiceResponceInfo) throws DocumentException {
//
////		AddInfo appInfo=licenseServiceResponceInfo.getNewServiceInfoData().get(0).getApplicantInfo();
//
//		Paragraph preface1 = new Paragraph();
//		// Lets write a big header
//		preface1.add(new Paragraph("To", smallBold));
//		document.add(preface1);
//
//		Paragraph preface2 = new Paragraph();
//		// preface2.
////		preface2.add(new Paragraph("_________  Pvt. Ltd.,"));
////		preface2.add(new Paragraph("5, Green Avenue, Vasant Kunj,"));
////		preface2.add(new Paragraph("New Delhi-110070."));
//		try {
//			ApplicantInfo applicationInfo = licenseServiceResponceInfo.getNewServiceInfoData().get(0)
//					.getApplicantInfo();
//			preface2.add(new Paragraph(applicationInfo.getDevDetail().getAddInfo().getCompanyName()));
//			preface2.add(new Paragraph(applicationInfo.getDevDetail().getAddInfo().getRegisteredAddress()));
//			preface2.add(new Paragraph(applicationInfo.getState() + "-"
//					+ applicationInfo.getDevDetail().getLicenceDetails().getPincode()));
//		} catch (Exception e) {
//			preface2.add(new Paragraph("_________  Pvt. Ltd.,"));
//			preface2.add(new Paragraph("--, -----------, ----------,"));
//			preface2.add(new Paragraph("---------------."));
//			e.printStackTrace();
//		}
//		addEmptyLine(preface2, 1);
//		Chunk chunk1 = new Chunk(
//				"Memo No. LC-_________-JE (VA)/-_____/ 		                                    Dated:");
//		preface2.add(new Paragraph());
//
//		document.add(preface2);
//		document.add(chunk1);
//
//	}
//
//	private void addContent(Document document, LicenseServiceDao newServiceInfo,
//			LicenseServiceResponseInfo licenseServiceResponceInfo) throws DocumentException, JsonProcessingException {
//		String address = "N/A";
//		try {
//			ApplicantInfo applicationInfo = licenseServiceResponceInfo.getNewServiceInfoData().get(0)
//					.getApplicantInfo();
//			address = (applicationInfo.getAuthorizedAddress() + ",village-" + applicationInfo.getVillage() + ", tehsil-"
//					+ applicationInfo.getTehsil() + " ,district-" + applicationInfo.getDistrict() + " ,state-"
//					+ applicationInfo.getState());
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//		List list = new List(List.ORDERED);
//		List subList = new List(List.ORDERED);
//		subList.setIndentationLeft(30f);
//		List subList1 = new List();
//		subList1.setIndentationLeft(30f);
//
//		String content = "Your request for grant of licence under section 3 of the Haryana Development and Regulation of Urban Areas Act, 1975 and Rules, 1976 framed there under for the development of a Residential Plotted Colony under New Integrated Licensing Policy-2016 over an area measuring 0.00 acres in the revenue estate of '"
//				+ " address :" + address
//				+ "' has been examined/considered by the Department. You are therefore, called upon to fulfill the following requirements/pre-requisites laid down in Rule-11 of the Haryana Development and Regulation of Urban Areas Rules, 1976 within a period of 60 days from the date of issue of this notice, failing which the grant of licence shall be refused: ";
////					+ (newServiceInfo!=null?(newServiceInfo.getNewServiceInfoData().get(0).getApplicantInfo().getVillage()):(" N/A "))
////					+ "', Sector-00, _________ _________ has been examined/considered by the Department. You are therefore, called upon to fulfill the following requirements/pre-requisites laid down in Rule-11 of the Haryana Development and Regulation of Urban Areas Rules, 1976 within a period of 60 days from the date of issue of this notice, failing which the grant of licence shall be refused: ";
//		Paragraph para = new Paragraph();
//		addEmptyLine(para, 3);
//
//		Paragraph para1 = new Paragraph(content, normal);
//		para1.setSpacingBefore(60f);
//		// para1.add(new Chunk(content));
//		addEmptyLine(para1, 1);
//
//		list.add(new ListItem(
//				"To furnish 25% bank guarantee on account of internal development works and external development charges for the amount calculated as under: - ",
//				normal));
//		// para1.add(new Chunk("2. To furnish 25% bank guarantee on account of internal
//		// development works and external development charges for the amount calculated
//		// as under: - "));
//
////			addEmptyLine(para1, 1);
//		document.add(para1);
//
//		subList1.add(new ListItem("Area under Commercial Component 0.62563 acres", normal));
//		subList1.add(new ListItem("Area under Plotted 00.000 acres	 ", normal));
//		subList1.add(new ListItem("Cost of community site ", normal));
//		subList1.add(new ListItem("Total cost of Internal Development Works", normal));
//		subList1.add(new ListItem("25% B.G. on account of IDW", normal));
//
//		subList1.add(new ListItem("Total Plotted area", normal));
//		subList1.add(new ListItem("Total Area under Comm. Component", normal));
//		subList1.add(new ListItem("Total EDC Charges (i)+(ii)	", normal));
//		subList1.add(new ListItem("25% bank guarantee required", normal));
//
//		subList.add(new ListItem("INTERNAL DEVELOPMENT WORKS:"));
//		subList.add(subList1);
//
//		subList.add(new ListItem("EXTERNAL DEVELOPMENT WORKS:"));
//		subList.add(subList1);
//
//		list.add(subList);
////			document.add(list);
//
//		Chunk p = new Chunk(
//				"It is made clear that the bank guarantee of internal development works has been worked out on the interim rates"
//						+ " and you will have to submit the additional bank guarantee if any, required at the time of approval of service plan/estimates according to the approved layout plan/building plan"
//						+ ". With an increase in the cost of construction and an increase in the number of facilities in the layout plan, you would be required to furnish an additional bank guarantee within 30"
//						+ " days of demand. ",
//				normal);
//		// p.setSpacingAfter(70f);
//		// document.add(p);3.4.5.6.7.8.9.
//		// list.add(new Paragraph("dsadasdasdasdasd"));
////			List list2 = new List(List.ORDERED);
//		List subList2 = new List();
//		subList2.setIndentationLeft(15f);
////			subList2.setAlignindent(true);
//
//		Integer totalFees = 0;
//		String totalFee = "0";
//		String totalFeeInWord = "N/A";
//		try {
////			if(newServiceInfo!=null){
//			FeesAndCharges feesAndCharges = licenseServiceResponceInfo.getNewServiceInfoData().get(0)
//					.getFeesAndCharges();
//			String scrutinyFee = feesAndCharges.getScrutinyFee();
//			String licenseFee = feesAndCharges.getLicenseFee();
//			String adjustFee = feesAndCharges.getAdjustFee();
//			if (isNumeric(scrutinyFee)) {
//				totalFees += Integer.parseInt(scrutinyFee);
//			}
//			if (isNumeric(licenseFee)) {
//				totalFees += Integer.parseInt(licenseFee);
//			}
//			if (isNumeric(adjustFee)) {
//				totalFees += Integer.parseInt(adjustFee);
//			}
////			}
//			totalFeeInWord = ConvertNumberToWord.numberToWords(totalFees.toString());
//			totalFee = ConvertNumberToWord.numberToComa(totalFees.toString());
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
//		list.add(new ListItem("To deposit an amount of " + totalFee + "/-(" + totalFeeInWord
//				+ " only) on account of balance license fee, deposited online on website i.e. www.tcpharyana.gov.in in favor"
//				+ "of Director, Town and Country Planning, Haryana.", normal));
//
////			list2.add(new ListItem("	To deposit an amount of   '" + totalFee
////					+ "'/-(------------------ only) on account of balance license fee, deposited online on website i.e. www.tcpharyana.gov.in in favor"
////					+ " of Director, Town and Country Planning, Haryana.", normal));
////			list2.add(new ListItem(
////					"4.	To deposit an amount of ₹ _________/- (------------------ only) on account of conversion charges, deposited online on website i.e. www.tcpharyana.gov.in in favor of Director,"
////							+ " Town and Country Planning, Haryana.",
////					normal));
//		list.add(new ListItem("	To submit an undertaking on non-judicial stamp paper of ₹ 10/- to the effect that:-	",
//				normal));
//
//		subList2.add(new ListItem(new ListItem("i)	That you will pay the Infrastructure Development Charges amounting "
//				+ totalFee + "- (" + totalFeeInWord + " only) @ "
//				+ "₹ 625 x 5/7 per Sqm  for plotted colony & ₹ 1000/- for commercial component in two equal installments. First installment shall be payable within sixty days of grant of licence and second "
//				+ "installment within six months from the date of grant of licence, failing which 18% PA interest will be charged.",
//				normal)));
//
//		subList2.add(new ListItem(new ListItem(
//				"ii)	To execute two agreements i.e. LC-IV and Bilateral Agreement on non-judicial stamp paper of ₹ 100/-. Copies of specimen of the said"
//						+ " agreements are enclosed herewith for necessary action.",
//				normal)));
//		subList2.add(new ListItem(new ListItem(
//				"iii)	That you shall maintain and upkeep all roads, open spaces, public parks and public health services for a period of five years from the date of issue"
//						+ " of the completion certificate unless earlier relieved of this responsibility and thereupon to transfer all such roads, open spaces, public parks and public health services free"
//						+ " of cost to the Government or the local authority, as the case may be, in accordable with the provisions of Section 3(3)(a)(iii) of the Haryana Development and Regulation of Urban Areas Act, 1975. ",
//				normal)));
//		subList2.add(new ListItem(new ListItem(
//				"iii)	That you shall maintain and upkeep all roads, open spaces, public parks and public health services for a period of five years from the date of issue"
//						+ " of the completion certificate unless earlier relieved of this responsibility and thereupon to transfer all such roads, open spaces, public parks and public health services free"
//						+ " of cost to the Government or the local authority, as the case may be, in accordable with the provisions of Section 3(3)(a)(iii) of the Haryana Development and Regulation of Urban Areas Act, 1975. ",
//				normal)));
//		subList2.add(new ListItem(new ListItem(
//				"iv)	That area coming under the sector roads and restricted belt/green belt which forms part of licensed area and in lieu of which benefit to the extent permissible"
//						+ " as per policy towards plotable are is being granted, shall be transferred free of cost of the Govt.",
//				normal)));
//
//		subList2.add(new ListItem(new ListItem(
//				"v)	That if any external development works are provided at any stage by HUDA/Government, then applicant shall have to "
//						+ "pay the proportionate development charges.",
//				normal)));
//
//		subList2.add(new ListItem(new ListItem(
//				"vi)	To submit an undertaking that you will integrate the services with the HSVP services as per the approved service plans"
//						+ " and as and when made available.",
//				normal)));
//
//		subList2.add(new ListItem(new ListItem(
//				"vii)	That you shall submit NOC as required under notification dated 14.09.2006 issued by Ministry of"
//						+ " Environment and Forest, Govt. of India before executing development works at site. ",
//				normal)));
//
//		subList2.add(new ListItem(new ListItem(
//				"viii)	That you shall seek approval from the Competent Authority under the Punjab "
//						+ "Land Preservation Act, 1900 or any other clearance required under any other law. ",
//				normal)));
//
//		subList2.add(new ListItem(new ListItem(
//				"ix)	To submit an undertaking to the effect that you shall make arrangement for water supply, sewerage drainage etc to the satisfaction"
//						+ " of DTCP till these services are made available from external infrastructure to be laid by HSVP.",
//				normal)));
//
//		subList2.add(new ListItem(
//				new ListItem("x)	That you shall provide the rain water harvesting system as per Central Ground Water"
//						+ " Authority Norms/Haryana Govt. notification as applicable.", normal)));
//
//		subList2.add(new ListItem(new ListItem(
//				" xi)	That you shall use only LED lamps fitting for internal lighting as well as campus lighting.",
//				normal)));
//
//		subList2.add(new ListItem(new ListItem(
//				"xii)	That you shall ensure the installation of Solar Photovoltaic Power Plant as per provision of notification no."
//						+ " _________ power dated __________ of Haryana Government Renewable Energy Department if required",
//				normal)));
//
//		subList2.add(new ListItem(new ListItem(
//				"xiii)	That you shall convey Ultimate Power Load Requirement of the project to the concerned power utility, with a copy to the Director, with in two month period from the date of grant of licence to enable provision of site in your"
//						+ " land for Transformers/Switching Station/ Electric Sub-Stations as per the norms prescribed by the power utility in the zoning plan of the project. ",
//				normal)));
//
//		subList2.add(new ListItem(new ListItem(
//				"xiv)	That it is understood that the development / construction cost of 24/18 m wide road/major internal road is not included in the EDC rates and you will pay the proportionate cost for acquisition of land, if any alongwith"
//						+ " the construction cost of 24/18 m wide road/major internal road as and when finalized and demanded by the Director, Town & Country Planning, Haryana. ",
//				normal)));
//
//		subList2.add(new ListItem(new ListItem(
//				"xv)	That you shall arrange electric connection from outside source for electrification of his colony from HVPN and shall install the electricity "
//						+ "distribution infrastructure as per the peak load requirement of the colony for which he shall get the electrical (distribution) service plan /estimates approved from the agency"
//						+ " responsible for installation of external electric services i.e. HVPN/UHBVNL/DHBVNL Haryana and complete the same before obtaining completion certificate for the colony.  ",
//				normal)));
//
//		subList2.add(new ListItem(new ListItem(
//				"xvi)	That you shall permit the Director or any other officer authorized by him to inspect the execution of the layout and the development works in the colony and to carry out"
//						+ " all directions issued by him for ensuring due compliance of the execution of the layout and development works in accordance with the license granted.",
//				normal)));
//
//		subList2.add(new ListItem(new ListItem(
//				"xvii)	That you shall deposit thirty per centum of the amount realised, from time to time, by him, from the plot holders within a period of ten days of its realization in a separate account"
//						+ " to be maintained in a scheduled bank. This amount shall only be utilized by him towards meeting the cost of internal development works in the colony.",
//				normal)));
//
//		subList2.add(new ListItem(new ListItem(
//				"xviii)	That you shall abide for paying the labour cess as per policy instructions issued by Haryana Government vide Memo No. Misc. 2057-5/25/2008/2TCP dated 25.02.2010. ",
//				normal)));
//
//		subList2.add(new ListItem(new ListItem(
//				"xix)	That you shall keep pace of construction atleast in accordance with sale agreement executed with the buyers of the flats as and when scheme is launched. ",
//				normal)));
//
//		subList2.add(new ListItem(new ListItem(
//				"xx)	That you shall not give any marketing and selling rights to any other company other than the collaborator company",
//				normal)));
//
//		subList2.add(new ListItem(new ListItem(
//				"xxi)	That no claim shall lie against HSVP till non-provision of EDC services, during next five years.",
//				normal)));
//
//		subList2.add(new ListItem(new ListItem(
//				"xxii)	To submit an undertaking for compliance of Rule -24,26,27 & 28 of Rules 1976 & Section -5 of Haryana Development and Regulation of Urban Areas Act, 1975, you shall inform account number & full "
//						+ "particulars of the scheduled bank wherein you have to deposit thirty percentum of the amount from buyers for meeting the cost of internal development works in the colony. ",
//				normal)));
//
//		subList2.add(new ListItem(new ListItem(
//				"xxiii)	That you shall transfer 10% area of the licenced colony free of cost to the Government for provision of community sites.",
//				normal)));
//
//		subList2.add(new ListItem(new ListItem(
//				"xxiv)	That you shall transfer 12% of the colony area free of cost to the Government earmarked for EWS and NPNL housing in the layout plan.",
//				normal)));
//
//		subList2.add(new ListItem(new ListItem(
//				"xxv)	That you have not filed any other application for grant of licence/Change of Land use for the above said land.",
//				normal)));
//
//		list.add(subList2);
//
//		list.add(new ListItem(
//				"	That you shall submit a certificate from District Revenue Authority stating that there is no further sale of the land applied for license till date and the applicants are owner of the land.. ",
//				normal));
//		list.add(new ListItem(
//				"	That you shall abide with stipulations mentioned in the policy dated 26.10.2015, 09.02.2016 & 05.12.2018 for New Integrated Licensing Policy.",
//				normal));
//		list.add(new ListItem(
//				"	That you shall withdraw the _____ acres land from the CLU granted permission and submit the requisite permission from the GMDA before the grant of license.",
//				normal));
//		list.add(new ListItem(
//				"   As the applied site is divided into two parts due to bandh passes through the site and you shall submit the NOC/permission from the competent authority for connecting the site by constructing the road over the bandh.",
//				normal));
//		list.add(new ListItem(
//				"	That you shall submit the NOC from the Divisional Forest Officer regarding applicability any forest law/ notification on the applied site. ",
//				normal));
//		list.add(new ListItem(
//				"	That you shall submit an indemnity bond indemnifying DTCP from any loss, if occurs due to submission of undertaking submitted in respect of non-creation of third party rights on the applied land.",
//				normal));
//		list.add(new ListItem(
//				"	The above demand for fee and charges is subject to audit and reconciliation of accounts.", normal));
//
////			
////			subList2.add(new ListItem(new ListItem(
////					"xxv)	That you have not filed any other application for grant of licence/Change of Land use for the above said land.",
////					normal)));
//
//		document.add(list);
//
////			List loiContent = new List(List.ALIGN_BASELINE);
////			loiContent.setIndentationLeft(10f);
////			
////			loiContent.add("	You are requested to complete the following shortcomings immediately.");
////			loiContent.add(new ListItem(" "));
////			document.add("");
////			document.add(loiContent);
//		addEmptyLine(para1, 1);
//		Paragraph para12 = new Paragraph("You are requested to complete the following shortcomings immediately:",
//				normal);
//		para12.setSpacingBefore(10f);
//		Paragraph para13 = new Paragraph("".toUpperCase(), normal);
//		para13.setSpacingBefore(10f);
////			para1.setFont(Font.BOLD);
//		document.add(para12);
//		document.add(para13);
//
////		}
//
//	}
//
//	private static void addEmptyLine(Paragraph paragraph, int number) {
//		for (int i = 0; i < number; i++) {
//			paragraph.add(new Paragraph(" "));
//		}
//	}
//
//	public boolean isNumeric(String strNum) {
//		if (strNum == null) {
//			return false;
//		}
//		try {
//			double d = Double.parseDouble(strNum);
//		} catch (NumberFormatException nfe) {
//			return false;
//		}
//		return true;
//	}
//
//	public LicenseServiceDao getLatestNewServicesInfo(String loiNumber) {
//
//		LicenseServiceDao newServiceInfo = newServiceInfoService.findByLoiNumber(loiNumber);
//		System.out.println("new service info size : " + newServiceInfo.getNewServiceInfoData().size());
//		for (int i = 0; i < newServiceInfo.getNewServiceInfoData().size(); i++) {
//			if (newServiceInfo.getCurrentVersion() == newServiceInfo.getNewServiceInfoData().get(i).getVer()) {
//				newServiceInfo.setNewServiceInfoData(Arrays.asList(newServiceInfo.getNewServiceInfoData().get(i)));
//			}
//		}
//		return newServiceInfo;
//
//	}
//
//}
