package org.egov.tl.report;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Stream;


import com.itextpdf.text.pdf.draw.DottedLineSeparator;

import javax.lang.model.element.ExecutableElement;
import javax.servlet.http.HttpServletResponse;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tl.repository.ServiceRequestRepository;
import org.egov.tl.service.LicenseService;
import org.egov.tl.service.LoiReportService;
import org.egov.tl.service.TradeLicenseService;
import org.egov.tl.service.dao.LicenseServiceDao;
import org.egov.tl.util.ConvertUtil;
import org.egov.tl.web.models.AddInfo;
import org.egov.tl.web.models.ApplicantInfo;
import org.egov.tl.web.models.ApplicantPurpose;
import org.egov.tl.web.models.AppliedLandDetails;
import org.egov.tl.web.models.DetailsAppliedLand;
import org.egov.tl.web.models.DetailsAppliedLandPlot;
import org.egov.tl.web.models.DetailsofAppliedLand;
import org.egov.tl.web.models.Developerdetail;
import org.egov.tl.web.models.LicenceDetails;
import org.egov.tl.web.models.LicenseDetails;
import org.egov.tl.web.models.LicenseServiceResponseInfo;
import org.egov.tl.web.models.RequestLOIReport;
import org.egov.tl.web.models.TradeLicense;
import org.egov.tl.web.models.TradeLicenseResponse;
import org.egov.tl.web.models.User;
import org.egov.tl.web.models.UserResponse;
import org.egov.tl.web.models.UserSearchCriteria;
import org.egov.tl.web.models.FeesAndCharges;
import org.egov.tl.web.models.LandSchedule;

//import org.egov.land.abm.newservices.entity.LicenseServiceDao;
//import org.egov.land.abm.service.LicenseService;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chapter;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.List;
import com.itextpdf.text.ListItem;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfDiv;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.PdfDiv.FloatType;
import com.itextpdf.text.pdf.draw.LineSeparator;

import lombok.extern.log4j.Log4j2;

import com.itextpdf.text.pdf.PdfTemplate;

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
	

}
