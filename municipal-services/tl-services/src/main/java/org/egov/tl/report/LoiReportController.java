package org.egov.tl.report;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

import javax.servlet.http.HttpServletResponse;

import org.egov.tl.service.LoiReportService;
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

import lombok.extern.log4j.Log4j2;

@RestController
@Log4j2
public class LoiReportController {

	//private String MY_FILE ;
	
	@Autowired Environment env;
	
	@Autowired
	LoiReportService loiReportService;
	
	@Value("${egov.loireport}")
	private String loireportPath;
	

	@RequestMapping(value = "/loi/report/_create", method = RequestMethod.POST)
	public void createLoiReport(@RequestParam("applicationNumber") String applicationNumber,HttpServletResponse response, @RequestBody RequestLOIReport requestLOIReport) throws IOException {
		loiReportService.createLoiReport(applicationNumber, requestLOIReport);
		log.info("Loi Report has been generated successfully for ApplicationNumber : "+applicationNumber);
		String flocation=loireportPath+"loi-report-"+applicationNumber+".pdf";
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
			log.info("Loi Report has been view successfully for ApplicationNumber : "+applicationNumber);
		}else {
		    log.warn("Loi Report has not found for ApplicationNumber : "+applicationNumber);
		}
	}
	
	@RequestMapping(value = "/loi/report/_preview", method = RequestMethod.POST)
	public void previewLoiReport(@RequestParam("applicationNumber") String applicationNumber,HttpServletResponse response, @RequestBody RequestLOIReport requestLOIReport) throws IOException {
		String flocation=loireportPath+"loi-report-"+applicationNumber+".pdf";
		File file = new File(flocation);
		if(!file.exists()) {
			loiReportService.createLoiReport(applicationNumber, requestLOIReport);
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
			log.info("Loi Report has been view successfully for ApplicationNumber : "+applicationNumber);
		}else {
		    log.warn("Loi Report has not found for ApplicationNumber : "+applicationNumber);
		}
	}
}
