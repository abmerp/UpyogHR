package org.egov.tl.report;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URLConnection;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tl.config.TLConfiguration;
import org.egov.tl.repository.ServiceRequestRepository;
import org.egov.tl.service.LicenseService;
import org.egov.tl.util.BPAConstants;
import org.egov.tl.util.BPANotificationUtil;
import org.egov.tl.web.models.LicenseServiceResponseInfo;
import org.egov.tl.web.models.RequestInfoWrapper;
import org.egov.tracer.model.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.MapType;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.jayway.jsonpath.JsonPath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class NewLicenseReport {

	private static Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD,BaseColor.BLACK);
	private static Font blackFont = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.NORMAL, BaseColor.BLACK);
	private static Font blackFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, BaseColor.BLACK);

	@Autowired BPANotificationUtil bPANotificationUtil;
	@Autowired LicenseService licenseService;

	@Autowired public RestTemplate restTemplate;
	@Autowired TLConfiguration config;

	@RequestMapping(value = "new/license/report", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public void jsonToPdf(@ModelAttribute("RequestInfo") RequestInfo requestInfo, HttpServletRequest request,
			HttpServletResponse response, @RequestParam("id") Long id) throws IOException {

		try {
			createNewLicenseReport(requestInfo,id);
		} catch (DocumentException e) {
			e.printStackTrace();
		}

		File file = new File("pdfBoxHelloWorld1.pdf");
		if (file.exists()) {
			String mimeType = URLConnection.guessContentTypeFromName(file.getName());
			if (mimeType == null) {
				mimeType = "application/octet-stream";
			}
			response.setContentType(mimeType);
			response.setHeader("Content-Disposition", String.format("inline; filename=\"" + file.getName() + "\""));

			response.setContentLength((int) file.length());
			InputStream inputStream = new BufferedInputStream(new FileInputStream(file));
			FileCopyUtils.copy(inputStream, response.getOutputStream());
		}
	}

	public void createNewLicenseReport(RequestInfo requestInfo,Long id)
			throws MalformedURLException, IOException, DocumentException {

		
		boolean flag = true;
		LicenseServiceResponseInfo licenseServiceResponceInfo = licenseService.getNewServicesInfoById(id);

		ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
		String json = ow.writeValueAsString(licenseServiceResponceInfo);
		System.out.println("JSON Sting ============> " + json);

		Image img = Image.getInstance("govt.jpg");
		img.scaleAbsolute(80, 80);

		ObjectMapper mapper = new ObjectMapper();
		mapper.enable(SerializationFeature.INDENT_OUTPUT);

		MapType mapType = mapper.getTypeFactory().constructMapType(Map.class, String.class, Object.class);
		Map<String, Object> map = mapper.readValue(json, mapType);

		// generate pretty JSON from map
		String json2 = mapper.writeValueAsString(map);
		// split by system new lines
		String[] strings = json2.split(System.lineSeparator());

		Document doc = new Document();
		PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream("pdfBoxHelloWorld1.pdf"));
		doc.open();
		Paragraph p1 = new Paragraph();
		p1.add("License Detail");
		p1.setFont(catFont);
		p1.setAlignment(Element.ALIGN_CENTER);
		doc.add(img);
		doc.add(p1);

		PdfPTable table = null;

		table = new PdfPTable(2);
		table.setSpacingBefore(10f);
		table.setSpacingAfter(10f);
		table.setWidthPercentage(80f);

		for (String string : strings) {
			if (string.contains("url")) {
				string = string.replaceAll(":", " ");
				string = string.replaceAll("\"", "");
			} else {
				string = string.replaceAll(":", "-");
				string = string.replaceAll("\\p{P}", "");
			}

			String[] splitStr = string.trim().split("\\s+ ");
			if (splitStr.length > 1) {

				for (String split : splitStr) {

					PdfPCell cell1 = null;
					if (flag) {
						String str = fetchContentFromLocalization(requestInfo, "hr", "tl-new-license", split);
						if(str!=null) {
							cell1 = new PdfPCell(new Paragraph(str, blackFont1));
							flag = false;
						}
						
					} else {
						cell1 = new PdfPCell(new Paragraph(split, blackFont));
						flag = true;
					}

					if(cell1!=null) {
						cell1.setPaddingLeft(10);
						cell1.setHorizontalAlignment(Element.ALIGN_LEFT);
						cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
						cell1.setPadding(4);
						cell1.setBorderColor(new BaseColor(109, 109, 109));
						table.addCell(cell1);
					}
				}
			}
		}
		doc.add(table);
		doc.close();
		writer.close();
	}

	private String fetchContentFromLocalization(RequestInfo requestInfo, String tenantId, String module, String code) {

		BPAConstants bapConstants = new BPAConstants();
		String message = null;
		List<String> codes = new ArrayList<>();
		List<String> messages = new ArrayList<>();
		Object result = null;
		String locale = "en_IN";
//	        if(requestInfo.getMsgId().contains("|"))
//	            locale = requestInfo.getMsgId().split("[\\|]")[1];

		StringBuilder uri = new StringBuilder();
		uri.append(config.getLocalizationHost()).append(config.getLocalizationContextPath())
				.append(config.getLocalizationSearchEndpoint());
		uri.append("?tenantId=").append(tenantId.split("\\.")[0]).append("&locale=").append(locale).append("&module=")
				.append(module).append("&code=").append(code);

		Map<String, Object> request = new HashMap<>();
		request.put("RequestInfo", requestInfo);
		try {
			result = restTemplate.postForObject(uri.toString(), request, Map.class);
			codes = JsonPath.read(result, bapConstants.LOCALIZATION_CODES_JSONPATH);
			messages = JsonPath.read(result, bapConstants.LOCALIZATION_MSGS_JSONPATH);
		} catch (Exception e) {
			// log.error("Exception while fetching from localization: " + e);
		}
		if (CollectionUtils.isEmpty(messages)) {
			throw new CustomException("LOCALIZATION_NOT_FOUND", "Localization not found for the code: " + code);
		}
		for (int index = 0; index < codes.size(); index++) {
			if (codes.get(index).equals(code)) {
				message = messages.get(index);
			}
		}
		return message;
	}

}
