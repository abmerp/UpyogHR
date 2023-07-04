package org.egov.tl.report;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

import javax.servlet.http.HttpServletResponse;

import org.egov.tl.service.LicenseService;
import org.egov.tl.service.LoiReportService;
import org.egov.tl.web.models.LicenseServiceResponseInfo;
import org.egov.tl.web.models.RequestLOIReport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfTextExtractor;

import lombok.extern.log4j.Log4j2;

@RestController
@Log4j2
public class LoiReportController {

	// private String MY_FILE ;

	@Autowired
	Environment env;

	@Autowired
	LoiReportService loiReportService;

	@Value("${egov.loireport}")
	private String loireportPath;

	@Autowired
	private LicenseService licenseService;

	@RequestMapping(value = "/loi/report/_create", method = RequestMethod.POST)
	public void createLoiReport(@RequestParam("applicationNumber") String applicationNumber,
			HttpServletResponse response, @RequestBody RequestLOIReport requestLOIReport) throws IOException {
		String flocation = loireportPath + "loi-report-" + applicationNumber + ".pdf";
		File file = new File(flocation);
		
		LicenseServiceResponseInfo licenseServiceResponceInfo = licenseService.getNewServicesInfoById(applicationNumber,
				requestLOIReport.getRequestInfo());
		String lNumber = licenseServiceResponceInfo.getTcpLoiNumber();
		boolean isGenerateLoi = (lNumber.equals("null") || lNumber.equals(null)) ? true : false;
		if(isGenerateLoi) {
			log.info("Loi Report has been generated successfully for ApplicationNumber : " + applicationNumber);
		}else {
			if(!file.exists()) {
				loiReportService.createLoiReport(applicationNumber, licenseServiceResponceInfo, requestLOIReport);
				log.info("Your pdf has deleted from db now has been created new Loi Report for ApplicationNumber : " + applicationNumber);
			}else {
				  PdfReader pdfReader = new PdfReader(flocation);
				  String pageContent =PdfTextExtractor.getTextFromPage(pdfReader,1);
				  if(pageContent.contains("LOI Number : N/A")){
						loiReportService.createLoiReport(applicationNumber, licenseServiceResponceInfo, requestLOIReport);
						log.info("Your final Loi Report has been generated successfully for ApplicationNumber : " + applicationNumber);
				  }
			}
		}
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
			log.info("Loi Report has been view successfully for ApplicationNumber : " + applicationNumber);
		} else {
			log.warn("Loi Report has not found for ApplicationNumber : " + applicationNumber);
		}
	}

	@RequestMapping(value = "/loi/report/_preview", method = RequestMethod.POST)
	public void previewLoiReport(@RequestParam("applicationNumber") String applicationNumber,
			HttpServletResponse response, @RequestBody RequestLOIReport requestLOIReport) throws IOException {
		String flocation = loireportPath + "loi-report-" + applicationNumber + ".pdf";
		File file = new File(flocation);
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
			log.info("Loi Report has been view successfully for ApplicationNumber : " + applicationNumber);
		} else {
			log.warn("Loi Report has not found for ApplicationNumber : " + applicationNumber);
		}
	}
}
