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
import org.egov.tl.web.models.LandScheduleDetails;
import org.egov.tl.web.models.LicenseDetails;
import org.egov.tl.web.models.LicenseServiceResponseInfo;
import org.egov.tl.web.models.PurposeDetails;
import org.egov.tl.web.models.ShareholdingPattens;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Font.FontFamily;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@RestController
public class NewLicensePDF {
	
	
	private static Font catFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLDITALIC,BaseColor.BLUE);
	private static Font blackFont = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLDITALIC, BaseColor.BLACK);
	private static Font blackFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, BaseColor.BLACK);

 //   private static String hindifont = "D:\\UPYOG-bikash\\UPYOG\\municipal-services\\tl-services\\src\\main\\resources\\font\\FreeSans.ttf";
//	private static String hindifont = "D:\\upyog code\\UPYOG1\\UPYOG\\municipal-services\\tl-services\\src\\main\\resources\\font\\FreeSans.ttf";
	private static String hindifont = "/opt/UPYOG/municipal-services/tl-services/src/main/resources/font/FreeSans.ttf";

	@Autowired BPANotificationUtil bPANotificationUtil;
	@Autowired LicenseService licenseService;

	@Autowired public RestTemplate restTemplate;
	@Autowired TLConfiguration config;
	
	@Autowired
	Environment environment;
	
	@Autowired
	ObjectMapper objectMapper;
	
	@RequestMapping(value = "new/license/pdf", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public void jsonToPdf(@ModelAttribute("RequestInfo") RequestInfo requestInfo, HttpServletRequest request,
			HttpServletResponse response, @RequestParam("applicationNumber") String applicationNumber) throws IOException {

		try {
			createNewLicensePDF(requestInfo,applicationNumber);
		} catch (DocumentException e) {
			e.printStackTrace();
		}

		File file = new File(applicationNumber+".pdf");
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
		
		
	//	boolean flag = true;
		
		LicenseServiceResponseInfo licenseServiceResponceInfo = licenseService.getNewServicesInfoById(applicationNumber, requestInfo);
		
		System.out.println(licenseServiceResponceInfo);

		Image img = Image.getInstance("govt.jpg");
		img.scaleAbsolute(200, 100);
		
		Document doc = new Document(PageSize.A4.rotate());
		PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(applicationNumber+".pdf"));
		doc.open();
	
		
		 Paragraph p2 = new Paragraph("Department of Town & Country Planning, Haryana", new
				 Font(FontFamily.HELVETICA, 18, Font.BOLDITALIC, new BaseColor(0, 0, 255)) );
		 
		 
		 

		 
		 final ZonedDateTime now = ZonedDateTime.now(ZoneId.of(environment.getProperty("egov.timeZoneName")));
		
		 DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a");
		 String formattedDateTime = now.format(formatter);
		 
		
		 Paragraph p3 = new Paragraph(formattedDateTime);
		 
		Paragraph p1 = new Paragraph();
		Paragraph p = new Paragraph();
		Paragraph l = new Paragraph();
		l.setFont(blackFont);
		l.add("Service ID:-");
		l.add("Licence");
		p.setFont(blackFont);
		p.add("Application Number:-");
		p.add(applicationNumber);
		p1.setFont(blackFont);
		p1.add("License Detail");
		img.setAlignment(Element.ALIGN_CENTER);
		p1.setAlignment(Element.ALIGN_LEFT);
		p2.setAlignment(Element.ALIGN_CENTER);
		p3.setAlignment(Element.ALIGN_RIGHT);
		doc.add(img);
		p2.setSpacingAfter(50);
		doc.add(p2);
		doc.add(p);
		doc.add(l);
		doc.add(p3);
		
	
		
		

	PdfPTable table = null;
	
	
  		if(licenseServiceResponceInfo.getNewServiceInfoData()!=null && licenseServiceResponceInfo.getNewServiceInfoData().size()>0) {
			doc.add(p1);
			
			for(int i=0;i<licenseServiceResponceInfo.getNewServiceInfoData().size();i++) {
				
				LicenseDetails licenseDetails = licenseServiceResponceInfo.getNewServiceInfoData().get(i);
				
				Paragraph pversion = new Paragraph();
				pversion.add("version :"+licenseDetails.getVer());
				doc.add(pversion);
				
				//new Deepak sir
				if(licenseDetails.getApplicantInfo()!= null) {
					Paragraph papplicantinfo = new Paragraph();
					papplicantinfo.setFont(blackFont1);
					Paragraph papplicantinfo1 = new Paragraph();
					papplicantinfo.add("Applicant Info");
					doc.add(papplicantinfo);
					papplicantinfo1.add("Developer Information");
					doc.add(papplicantinfo1);
					
					table = new PdfPTable(2);
					table.setSpacingBefore(10f);
					table.setSpacingAfter(10f);
					table.setWidthPercentage(100f);
					
					//table.addCell("Name");
					  //table.addCell(licenseDetails.getApplicantInfo().getDevDetail().getAddInfo().getName());
					  
					  table.addCell("Name");
					  table.addCell(licenseDetails.getApplicantInfo().getDevDetail().getAddInfo().getCompanyName());
					  
					  table.addCell("Address");
					  table.addCell(licenseDetails.getApplicantInfo().getDevDetail().getAddInfo().getRegisteredAddress());
					  
					  table.addCell("EmailId");
					  table.addCell(licenseDetails.getApplicantInfo().getDevDetail().getAddInfo().getEmailId());
					  
					  
					  table.addCell("Developer Type");
					  table.addCell(licenseDetails.getApplicantInfo().getDevDetail().getAddInfo().getShowDevTypeFields());
					  
					  table.addCell("Cin_Number");
					  table.addCell(licenseDetails.getApplicantInfo().getDevDetail().getAddInfo().getCin_Number());
					  
					 
					  
					  
					doc.add(table);
				}
				
				
		if(licenseDetails.getApplicantInfo().getDevDetail().getAddInfo().getDirectorsInformationMCA()!=null && licenseDetails.getApplicantInfo().getDevDetail().getAddInfo().getDirectorsInformationMCA().size()>0) {
					
					List<DirectorsInformationMCA> f= licenseDetails.getApplicantInfo().getDevDetail().getAddInfo().getDirectorsInformationMCA();
	                   
					Paragraph pald = new Paragraph();
					pald.add("Director Information as per MCA");
					doc.add(pald);
					
					table = new PdfPTable(3);
					table.setSpacingBefore(10f);
					table.setSpacingAfter(10f);
					table.setWidthPercentage(100f);
					
					PdfPCell c2 = new PdfPCell(new Phrase("DIN Number"));
			        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
			        table.addCell(c2);
			        
			        c2 = new PdfPCell(new Phrase("Name"));
			        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
			        table.addCell(c2);
			        
			        c2 = new PdfPCell(new Phrase("Contact Number"));
			        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
			        table.addCell(c2);
					
					for(int j=0;j<f.size();j++) {
						DirectorsInformationMCA g =  f.get(j);
					
					  table.addCell(g.getDin());
					  
					 
					  table.addCell(g.getName());
					  
					
					  table.addCell(g.getContactNumber());
					  
				
					
	                  
	                   }
	                   doc.add(table);
				}
				
				if(licenseDetails.getApplicantInfo().getDevDetail().getAddInfo().getDirectorsInformation()!=null && licenseDetails.getApplicantInfo().getDevDetail().getAddInfo().getDirectorsInformation().size()>0) {
				
				List<DirectorsInformation> f= licenseDetails.getApplicantInfo().getDevDetail().getAddInfo().getDirectorsInformation();
                   
				Paragraph pald = new Paragraph();
				pald.add("Directors Information as per developer");
				doc.add(pald);
				
				table = new PdfPTable(3);
				table.setSpacingBefore(5f);
				table.setSpacingAfter(5f);
				table.setWidthPercentage(100f);
				
				PdfPCell c2 = new PdfPCell(new Phrase("DIN Number"));
		        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
		        table.addCell(c2);
		        
		        c2 = new PdfPCell(new Phrase("Name"));
		        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
		        table.addCell(c2);
		        
		        c2 = new PdfPCell(new Phrase("Contact Number"));
		        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
		        table.addCell(c2);
				
				for(int j=0;j<f.size();j++) {
             	   DirectorsInformation g =  f.get(j);
				
				  table.addCell(g.getDin());
				  
				 
				  table.addCell(g.getName());
				  
				
				  table.addCell(g.getContactNumber());
				  
			
				
                  
                   }
                   doc.add(table);
			}
				
				
		
				
	if(licenseDetails.getApplicantInfo().getDevDetail().getAddInfo().getShareHoldingPatterens()!=null && licenseDetails.getApplicantInfo().getDevDetail().getAddInfo().getShareHoldingPatterens().size()>0) {
					
					List<ShareholdingPattens> f= licenseDetails.getApplicantInfo().getDevDetail().getAddInfo().getShareHoldingPatterens();
	                   
					Paragraph pald = new Paragraph();
					pald.add("Shareholding Patterns");
					doc.add(pald);
					
					table = new PdfPTable(3);
					table.setSpacingBefore(10f);
					table.setSpacingAfter(10f);
					table.setWidthPercentage(100f);
					
					PdfPCell c2 = new PdfPCell(new Phrase("	Name"));
			        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
			        table.addCell(c2);
			        
			        c2 = new PdfPCell(new Phrase("Designition"));
			        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
			        table.addCell(c2);
			        
			        c2 = new PdfPCell(new Phrase("Percentage"));
			        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
			        table.addCell(c2);
					
					for(int j=0;j<f.size();j++) {
						ShareholdingPattens g =  f.get(j);
					
					  table.addCell(g.getName());
					  
					 
					  table.addCell(g.getDesignition());
					  
					
					  table.addCell(g.getPercentage());
					  
				
					
	                  
	                   }
	                   doc.add(table);
				}
	
             	if(licenseDetails.getApplicantInfo()!= null) {
	                	Paragraph papplicantinfo = new Paragraph();
	                    papplicantinfo.add("Authorized Person Information");
	                 	doc.add(papplicantinfo);
		                
	                  	table = new PdfPTable(2);
	                 	table.setSpacingBefore(10f);
	                  	table.setSpacingAfter(10f);
		                table.setWidthPercentage(100f);
		
	                
		                if(licenseDetails.getApplicantInfo().getDevDetail().getAurthorizedUserInfoArray()!=null && licenseDetails.getApplicantInfo().getDevDetail().getAurthorizedUserInfoArray().size()>0) {
							List<AddRemoveAuthoizedUsers> f= licenseDetails.getApplicantInfo().getDevDetail().getAurthorizedUserInfoArray();
	                           for(int j=0;j<f.size();j++) {
	                        	   AddRemoveAuthoizedUsers g =  f.get(j);
								
								
							
							table.addCell("Name");
							  table.addCell(g.getName());
							  
							  table.addCell("Mobile No");
							  table.addCell(g.getMobileNumber());
							  
							  table.addCell("Emailid for Authorized Signatory");
							  table.addCell(g.getEmailId());
							  
							  table.addCell("Pan No");
							  table.addCell(g.getPan());
	                          
	                           }
						}
		  
		 
		  
		  
		           doc.add(table);
	}
	
				
				
					//Applicant Purpose
					if(licenseDetails.getApplicantPurpose()!= null) {
						Paragraph papplicanturpose = new Paragraph();
						papplicanturpose.setFont(blackFont1);
						papplicanturpose.add("Applicant Purpose");
						doc.add(papplicanturpose);
						
						table = new PdfPTable(2);
						table.setSpacingBefore(10f);
						table.setSpacingAfter(10f);
						table.setWidthPercentage(100f);
						
						
							
							  table.addCell("Purpose");
							  table.addCell(licenseDetails.getApplicantPurpose().getPurpose());
							  
							 // table.addCell("State");
							  //table.addCell(licenseDetails.getApplicantPurpose().getState());
							  
							  table.addCell("TotalArea");
							  table.addCell(licenseDetails.getApplicantPurpose().getTotalArea());
						        
						
						
						
						doc.add(table);
						
					}
						//Applied Land Details
						if(licenseDetails.getApplicantPurpose().getAppliedLandDetails()!=null && licenseDetails.getApplicantPurpose().getAppliedLandDetails().size()>0) {
							
							Paragraph pald = new Paragraph();
							pald.setFont(blackFont1);
							pald.add("Applied Land Details");
							doc.add(pald);
							
						for(int j=0;j<licenseDetails.getApplicantPurpose().getAppliedLandDetails().size();j++) {
																				
//							table = new PdfPTable(27);
//							table.setSpacingBefore(5f);
//							table.setSpacingAfter(5f);
//							table.setWidthPercentage(100f);
							
							table = new PdfPTable(2);
							table.setSpacingBefore(10f);
							table.setSpacingAfter(10f);
							table.setWidthPercentage(100f);
							
							
							System.out.println(licenseDetails.getApplicantPurpose().getAppliedLandDetails().size());							
							AppliedLandDetails appliedLandDetails = licenseDetails.getApplicantPurpose().getAppliedLandDetails().get(j);
							
							String s1 = appliedLandDetails.getLandOwner();
							
							
														
							 BaseFont unicode = BaseFont.createFont(hindifont, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
							 Font font=new Font(unicode,12,Font.NORMAL,new BaseColor(51,0,255));
							 
							 System.out.println(s1);
							 
							 if(s1.isEmpty()) {
							 }
							 else {
							 Paragraph lo = new Paragraph();
							 lo.setFont(blackFont);
							 lo.add("Name of Land Owner:");
							 doc.add(lo);
							 Paragraph p6 = new Paragraph(s1, font);
							 
						     doc.add(p6);
							 }

//					        c2 = new PdfPCell(new Phrase("Sarsai"));
//					        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
//					        table.addCell(c2);
//					        
//					        c2 = new PdfPCell(new Phrase("Kanal"));
//					        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
//					        table.addCell(c2);
//					        
//					        c2 = new PdfPCell(new Phrase("Marla"));
//					        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
//					        table.addCell(c2);
//					        
//					        c2 = new PdfPCell(new Phrase("Hadbast No"));
//					        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
//					        table.addCell(c2);
//					        
//					        c2 = new PdfPCell(new Phrase("Bigha"));
//					        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
//					        table.addCell(c2);
//					        
//					        c2 = new PdfPCell(new Phrase("Biswansi"));
//					        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
//					        table.addCell(c2);
//					        
//					        c2 = new PdfPCell(new Phrase("Biswa"));
//					        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
//					        table.addCell(c2);
//					        
//					       /** c2 = new PdfPCell(new Phrase("LandOwner"));
//					        PdfPCell  c3 = new PdfPCell(new Phrase(s1, font));
//					        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
//					        table.addCell(c2);*/
//					        
//					        c2 = new PdfPCell(new Phrase("Collaboration"));
//					        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
//					        table.addCell(c2);
//					        
//					        c2 = new PdfPCell(new Phrase("Developer Company"));
//					        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
//					        table.addCell(c2);
//					        
//					        c2 = new PdfPCell(new Phrase("AgreementValid From"));
//					        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
//					        table.addCell(c2);
//					        
//					        c2 = new PdfPCell(new Phrase("Validity Date"));
//					        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
//					        table.addCell(c2);
//					        
//					        c2 = new PdfPCell(new Phrase("Agreement Irrevocialble"));
//					        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
//					        table.addCell(c2);
//					        
//					        c2 = new PdfPCell(new Phrase("AuthSignature"));
//					        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
//					        table.addCell(c2);
//					        
//					        c2 = new PdfPCell(new Phrase("Name AuthSign"));
//					        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
//					        table.addCell(c2);
//					       
//					        c2 = new PdfPCell(new Phrase("Registering Authority"));
//					        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
//					        table.addCell(c2);
//					        
//					        c2 = new PdfPCell(new Phrase("Registering AuthorityDoc"));
//					        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
//					        table.addCell(c2);
//					        
//					        c2 = new PdfPCell(new Phrase("Khewats"));
//					        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
//					        table.addCell(c2);
//					        
//					        c2 = new PdfPCell(new Phrase("Consolidated Total"));
//					        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
//					        table.addCell(c2);
//					        
//					        c2 = new PdfPCell(new Phrase("NonConsolidated Total"));
//					        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
//					        table.addCell(c2);
//					        
//					        c2 = new PdfPCell(new Phrase("Edit Khewats"));
//					        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
//					        table.addCell(c2);
//					        
//					        c2 = new PdfPCell(new Phrase("Edit RectangleNo"));
//					        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
//					        table.addCell(c2);
//					        
//					        c2 = new PdfPCell(new Phrase("TypeLand"));
//					        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
//					        table.addCell(c2);








					       

					       
					        table.addCell("District");
					        table.addCell(appliedLandDetails.getDistrict().getLabel());
					        table.addCell("Potential");
					        table.addCell(appliedLandDetails.getPotential());
					        table.addCell("Zone");
					        table.addCell(appliedLandDetails.getZone());
					        table.addCell("Development Plan");
					        table.addCell(appliedLandDetails.getDevelopmentPlan().getLabel());
					        table.addCell("Sector");
					        table.addCell(appliedLandDetails.getSector());
					        table.addCell("Tehsil");
						    table.addCell(appliedLandDetails.getTehsil().getLabel());
					        table.addCell("Revenue Estate");
					        table.addCell(appliedLandDetails.getRevenueEstate().getLabel());
					        table.addCell("Mustil");
					        table.addCell(appliedLandDetails.getMustil());
					        table.addCell("Consolidation Type");
					        table.addCell(appliedLandDetails.getConsolidationType());
					        table.addCell("Sarsai");
					        table.addCell(appliedLandDetails.getSarsai());
					        table.addCell("Kanal");
					        table.addCell(appliedLandDetails.getKanal());
					        table.addCell("Marla");
					        table.addCell(appliedLandDetails.getMarla());
					        table.addCell("HadbastNo");
					        table.addCell(appliedLandDetails.getHadbastNo());
					        table.addCell("Bigh");
					        table.addCell(appliedLandDetails.getBigha());
					        table.addCell("Biswansi");
					        table.addCell(appliedLandDetails.getBiswansi());
					        table.addCell("Biswa");
					        table.addCell(appliedLandDetails.getBiswa());
					        table.addCell("LandOwner Registry");
					        table.addCell(appliedLandDetails.getLandOwnerRegistry());
					        table.addCell("Collaboration");
					        table.addCell(appliedLandDetails.getCollaboration());
					        table.addCell("Developer ompany");
					        table.addCell(appliedLandDetails.getDeveloperCompany());
					        table.addCell("Agreement Valid From");
					        table.addCell(appliedLandDetails.getAgreementValidFrom());
					        table.addCell("AgreementValidTo");
					        table.addCell(appliedLandDetails.getAgreementValidTo());
					        table.addCell("Agreement Irrevocialble");
					        table.addCell(appliedLandDetails.getAgreementIrrevocialble());
					        table.addCell("AuthSignature");
					        table.addCell(appliedLandDetails.getAuthSignature());
					        table.addCell("Name Auth Sign");
					        table.addCell(appliedLandDetails.getNameAuthSign());
					        table.addCell("Registering Authority");
					        table.addCell(appliedLandDetails.getRegisteringAuthority());
					        table.addCell("Registering Authority Doc");
					        table.addCell(appliedLandDetails.getRegisteringAuthorityDoc());
					        table.addCell("Khewats");
					        table.addCell(appliedLandDetails.getKhewats());
					        table.addCell("Consolidated Total");
					        table.addCell(appliedLandDetails.getConsolidatedTotal());
					        table.addCell("Non Consolidated Total");
					        table.addCell(appliedLandDetails.getNonConsolidatedTotal());
					        table.addCell("Edit Khewats");
					        table.addCell(appliedLandDetails.getEditKhewats());
					        table.addCell("Edit Rectangle No");
					        table.addCell(appliedLandDetails.getEditRectangleNo());
					        table.addCell("TypeLand");
					        table.addCell(appliedLandDetails.getTypeLand().getLabel());
					        table.addCell("RectangleNo");
					        table.addCell(appliedLandDetails.getRectangleNo());
					        table.addCell("Non ConsolidationType");
					        table.addCell(appliedLandDetails.getNonConsolidationType());
					        table.addCell("LandOwnerSPAGPADoc");
					        table.addCell(appliedLandDetails.getLandOwnerSPAGPADoc());
					        table.addCell("DeveloperSPAGPADoc");
					        table.addCell(appliedLandDetails.getDeveloperSPAGPADoc());
					        
					        
					        
						 
					        doc.add(table);
							   
							
					
						}
						
						
						}
				
				
				
					if(licenseDetails.getLandSchedule()!= null) {
						Paragraph pls = new Paragraph();
						pls.setFont(blackFont1);
						pls.add("Land Schedule");
						Paragraph pls1 = new Paragraph();
						pls1.setFont(catFont);
						pls1.add("1:- Applied additional Area");
						doc.add(pls);
						doc.add(pls1);
						
						table = new PdfPTable(2);
						table.setSpacingBefore(10f);
						table.setSpacingAfter(10f);
						table.setWidthPercentage(100f);
						
						System.out.println(licenseDetails.getLandSchedule().getLicenseApplied());
						String t = licenseDetails.getLandSchedule().getLicenseApplied();
						System.out.println(t);
						
						 if(licenseDetails.getLandSchedule().getLicenseApplied()!= null) {
							 
						
							  table.addCell("License Applied");
							  table.addCell(licenseDetails.getLandSchedule().getLicenseApplied());
						 }
						 else {
								
							 table.addCell("License Applied");
							  table.addCell(licenseDetails.getLandSchedule().getLicenseApplied());
							
						}
						
							  
							  if(licenseDetails.getLandSchedule().getLicenseNumber()!= null) {
									
								  table.addCell("License Number");
								  table.addCell(licenseDetails.getLandSchedule().getLicenseNumber());
							        
								
							}
							else {
								
								 table.addCell("License Number");
								  table.addCell("NULL");
								
							}
							
							if(licenseDetails.getLandSchedule().getPotential()!= null) {
								
								  table.addCell("Potential");
								  table.addCell(licenseDetails.getLandSchedule().getPotential());
							        
								
							}
							else {
								
								 table.addCell("Potential");
								  table.addCell("NULL");
								
							}
							
							if(licenseDetails.getLandSchedule().getSiteLoc()!= null) {
								
								  table.addCell("SiteLoc");
								  table.addCell(licenseDetails.getLandSchedule().getSiteLoc());
							        
								
							}
							else {
								
								 table.addCell("SiteLoc");
								  table.addCell("NULL");
								
							}

							
							if(licenseDetails.getLandSchedule().getApproachType()!= null) {
								
								  table.addCell("Approach Type");
								  table.addCell(licenseDetails.getLandSchedule().getApproachType());
							        
								
							}
							else {
								
								 table.addCell("Approach Type");
								  table.addCell("NULL");
								
							}

							
							if(licenseDetails.getLandSchedule().getApproachRoadWidth()!= null) {
								
								  table.addCell("Approach RoadWidth");
								  table.addCell(licenseDetails.getLandSchedule().getApproachRoadWidth());
							        
								
							}
							else {
								
								 table.addCell("Approach RoadWidth");
								  table.addCell("NULL");
								
							}
							
							if(licenseDetails.getLandSchedule().getSpecify()!= null) {
								
								  table.addCell("Specify");
								  table.addCell(licenseDetails.getLandSchedule().getSpecify());
							        
								
							}
							else {
								
								 table.addCell("Specify");
								  table.addCell("NULL");
								
							}

							if(licenseDetails.getLandSchedule().getTypeLand()!= null) {
								
								  table.addCell("TypeLand");
								  table.addCell(licenseDetails.getLandSchedule().getTypeLand());
							        
								
							}
							else {
								
								 table.addCell("TypeLand");
								  table.addCell("NULL");
								
							}

							if(licenseDetails.getLandSchedule().getThirdParty()!= null) {
								
								  table.addCell("Third Party");
								  table.addCell(licenseDetails.getLandSchedule().getThirdParty());
							        
								
							}
							else {
								
								 table.addCell("Third Party");
								  table.addCell("NULL");
								
							}

							if(licenseDetails.getLandSchedule().getThirdPartyRemark()!= null) {
								
								  table.addCell("Third Party Remark");
								  table.addCell(licenseDetails.getLandSchedule().getThirdPartyRemark());
							        
								
							}
							else {
								
								 table.addCell("Third Party Remark");
								  table.addCell("NULL");
								
							}

							if(licenseDetails.getLandSchedule().getThirdPartyDoc()!= null) {
								
								  table.addCell("Third PartyDoc");
								  table.addCell(licenseDetails.getLandSchedule().getThirdPartyDoc());
							        
								
							}
							else {
								
								 table.addCell("Third PartyDoc");
								  table.addCell("NULL");
								
							}
							
						
						
						
					
						doc.add(table);
						
					}
					
					
					if(licenseDetails.getLandSchedule()!= null) {
						
						Paragraph pls2 = new Paragraph();
						pls2.setFont(catFont);
						pls2.add("2:- Applied Under migration Policy");
						
						doc.add(pls2);
						
						table = new PdfPTable(2);
						table.setSpacingBefore(10f);
						table.setSpacingAfter(10f);
						table.setWidthPercentage(100f);
						
						
						if(licenseDetails.getLandSchedule().getMigrationLic()!= null) {
							
							  table.addCell("MigrationLic");
							  table.addCell(licenseDetails.getLandSchedule().getMigrationLic());
						        
							
						}
						else {
							
							 table.addCell("MigrationLic");
							  table.addCell("NULL");
							
						}

						
						if(licenseDetails.getLandSchedule().getAreaUnderMigration()!= null) {
							
							  table.addCell("AreaUnder Migration");
							  table.addCell(licenseDetails.getLandSchedule().getAreaUnderMigration());
						        
							
						}
						else {
							
							 table.addCell("AreaUnder Migration");
							  table.addCell("NULL");
							
						}


						if(licenseDetails.getLandSchedule().getPurposeParentLic()!= null) {
							
							  table.addCell("PurposeParentLic");
							  table.addCell(licenseDetails.getLandSchedule().getPurposeParentLic());
						        
							
						}
						else {
							
							 table.addCell("PurposeParentLic");
							  table.addCell("NULL");
							
						}

						if(licenseDetails.getLandSchedule().getLicNo()!= null) {
							
							  table.addCell("Lic No");
							  table.addCell(licenseDetails.getLandSchedule().getLicNo());
						        
							
						}
						else {
							
							 table.addCell("Lic No");
							  table.addCell("NULL");
							
						}

						if(licenseDetails.getLandSchedule().getAreaofParentLic()!= null) {
							
							  table.addCell("Area of ParentLic");
							  table.addCell(licenseDetails.getLandSchedule().getAreaofParentLic());
						        
							
						}
						else {
							
							 table.addCell("Area of ParentLic");
							  table.addCell("NULL");
							
						}
						
						if(licenseDetails.getLandSchedule().getValidityOfParentLic()!= null) {
							
							  table.addCell("Validity Of ParentLic");
							  table.addCell(licenseDetails.getLandSchedule().getValidityOfParentLic());
						        
							
						}
						
						else {
							
							 table.addCell("Validity Of ParentLic");
							  table.addCell("NULL");
							
						}
						
						if(licenseDetails.getLandSchedule().getRenewalFee()!= null) {
							
							  table.addCell("Renewal Fee");
							  table.addCell(licenseDetails.getLandSchedule().getRenewalFee());
						        
							
						}
						else {
							
							 table.addCell("Renewal Fee");
							  table.addCell("NULL");
							
						}
						
						if(licenseDetails.getLandSchedule().getFreshlyApplied()!= null) {
							
							  table.addCell("Freshly Applied");
							  table.addCell(licenseDetails.getLandSchedule().getFreshlyApplied());
						        
							
						}
						else {
							
							 table.addCell("Freshly Applied");
							  table.addCell("NULL");
							
						}
						
						if(licenseDetails.getLandSchedule().getApprovedLayoutPlan()!= null) {
							
							  table.addCell("Approved LayoutPlan");
							  table.addCell(licenseDetails.getLandSchedule().getApprovedLayoutPlan());
						        
							
						}
						else {
							
							 table.addCell("Approved LayoutPlan");
							  table.addCell("NULL");
							
						}
						if(licenseDetails.getLandSchedule().getUploadPreviouslyLayoutPlan()!= null) {
							
							  table.addCell("UploadPreviously LayoutPlan");
							  table.addCell(licenseDetails.getLandSchedule().getUploadPreviouslyLayoutPlan());
						        
							
						}
						else {
							
							 table.addCell("UploadPreviously LayoutPlan");
							  table.addCell("NULL");
							
						}
						
						
	                doc.add(table);
						
					}
					
					
					if(licenseDetails.getLandSchedule().getLandScheduleDetails()!=null && licenseDetails.getLandSchedule().getLandScheduleDetails().size()>0) {
						
						 List<LandScheduleDetails> f= licenseDetails.getLandSchedule().getLandScheduleDetails();
		                   
						Paragraph lsd = new Paragraph();
						lsd.add("LandSchedule Details");
						doc.add(lsd);
						
						table = new PdfPTable(9);
						table.setSpacingBefore(10f);
						table.setSpacingAfter(10f);
						table.setWidthPercentage(100f);
						
						/**	List value
						   if(licenseDetails.getLandSchedule().getLandScheduleDetails()!= null) {
								
								  table.addCell("LandScheduleDetails");
								  table.addCell(licenseDetails.getLandSchedule().getLandScheduleDetails());
							      
							}
							else {
								
								 table.addCell("LandScheduleDetails");
								  table.addCell("NULL");
								
							}*/
							
						
						PdfPCell c2 = new PdfPCell(new Phrase("Previous Licensenumber"));
				        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
				        table.addCell(c2);
				        
				        c2 = new PdfPCell(new Phrase("Area Of ParentLicence"));
				        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
				        table.addCell(c2);
				        
				        c2 = new PdfPCell(new Phrase("Purpose Of ParentLicence"));
				        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
				        table.addCell(c2);
				        
				        c2 = new PdfPCell(new Phrase("Validity"));
				        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
				        table.addCell(c2);
				        
				        c2 = new PdfPCell(new Phrase("Date"));
				        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
				        table.addCell(c2);
				        
				        c2 = new PdfPCell(new Phrase("Area Applied Migration"));
				        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
				        table.addCell(c2);
				        
				        c2 = new PdfPCell(new Phrase("Khasra Number"));
				        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
				        table.addCell(c2);
				        
				        c2 = new PdfPCell(new Phrase("Area"));
				        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
				        table.addCell(c2);
				        
				        c2 = new PdfPCell(new Phrase("Balance Of ParentLicence"));
				        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
				        table.addCell(c2);
						
						for(int j=0;j<f.size();j++) {
							LandScheduleDetails g =  f.get(j);
						
						  table.addCell(g.getPreviousLicensenumber());
						  
						 
						  table.addCell(g.getAreaOfParentLicence());
						  
						
						  table.addCell(g.getPurposeOfParentLicence());
						  
						  table.addCell(g.getValidity());
						  
						  table.addCell(g.getDate());
						  
						  table.addCell(g.getAreaAppliedmigration());
						  
						  table.addCell(g.getKhasraNumber());
						  
						  table.addCell(g.getArea());
						  
						  table.addCell(g.getBalanceOfParentLicence());
						  
					
						
		                  
		                   }
		                   doc.add(table);
					}
					
                      if(licenseDetails.getLandSchedule()!= null) {
						
						Paragraph pls3 = new Paragraph();
						pls3.setFont(catFont);
						pls3.add("3:- Any Encumburance");
						
						doc.add(pls3);
						
						table = new PdfPTable(2);
						table.setSpacingBefore(10f);
						table.setSpacingAfter(10f);
						table.setWidthPercentage(100f);
					
					
						
						if(licenseDetails.getLandSchedule().getEncumburance()!= null) {
							
							  table.addCell("Encumburance");
							  table.addCell(licenseDetails.getLandSchedule().getEncumburance());
						        
							
						}
						else {
							
							 table.addCell("Encumburance");
							  table.addCell("NULL");
							
						}
						
						if(licenseDetails.getLandSchedule().getEncumburanceOther()!= null) {
							
							  table.addCell("Encumburance Other");
							  table.addCell(licenseDetails.getLandSchedule().getEncumburanceOther());
						        
							
						}
						else {
							
							 table.addCell("Encumburance Other");
							  table.addCell("NULL");
							
						}
						if(licenseDetails.getLandSchedule().getEncumburanceDoc()!= null) {
							
							  table.addCell("Encumburance Doc");
							  table.addCell("ATTACHED");
						        
							
						}
						else {
							
							 table.addCell("Encumburance Doc");
							  table.addCell("NULL");
							
						}
						if(licenseDetails.getLandSchedule().getLitigation()!= null) {
							
							  table.addCell("Litigation");
							  table.addCell(licenseDetails.getLandSchedule().getLitigation());
						        
							
						}
						else {
							
							 table.addCell("Litigation");
							  table.addCell("NULL");
							
						}
						if(licenseDetails.getLandSchedule().getLitigationRemark()!= null) {
							
							  table.addCell("Litigation Remark");
							  table.addCell(licenseDetails.getLandSchedule().getLitigationRemark());
						        
							
						}
						else {
							
							 table.addCell("Litigation Remark");
							  table.addCell("NULL");
							
						}
						if(licenseDetails.getLandSchedule().getLitigationDoc()!= null) {
							
							  table.addCell("Litigation Doc");
							  table.addCell("ATTACHED");
						        
							
						}
						else {
							
							 table.addCell("Litigation Doc");
							  table.addCell("NULL");
							
						}
						if(licenseDetails.getLandSchedule().getCourt()!= null) {
							
							  table.addCell("Court");
							  table.addCell(licenseDetails.getLandSchedule().getCourt());
						        
							
						}
						else {
							
							 table.addCell("Court");
							  table.addCell("NULL");
							
						}
						if(licenseDetails.getLandSchedule().getCourtyCaseNo()!= null) {
							
							  table.addCell("Courty CaseNo");
							  table.addCell(licenseDetails.getLandSchedule().getCourtyCaseNo());
						        
							
						}
						else {
							
							 table.addCell("Courty CaseNo");
							  table.addCell("NULL");
							
						}
						if(licenseDetails.getLandSchedule().getCourtDoc()!= null) {
							
							  table.addCell("Court Doc");
							  table.addCell("ATTACHED");
						        
							
						}
						else {
							
							 table.addCell("Court Doc");
							  table.addCell("NULL");
							
						}
						if(licenseDetails.getLandSchedule().getInsolvency()!= null) {
							
							  table.addCell("Insolvency");
							  table.addCell(licenseDetails.getLandSchedule().getInsolvency());
						        
							
						}
						else {
							
							 table.addCell("Insolvency");
							  table.addCell("NULL");
							
						}
						if(licenseDetails.getLandSchedule().getInsolvencyRemark()!= null) {
							
							  table.addCell("Insolvency Remark");
							  table.addCell(licenseDetails.getLandSchedule().getInsolvencyRemark());
						        
							
						}
						else {
							
							 table.addCell("Insolvency Remark");
							  table.addCell("NULL");
							
						}
						
						if(licenseDetails.getLandSchedule().getInsolvencyDoc()!= null) {
							
							  table.addCell("Insolvency Doc");
							  table.addCell("ATTACHED");
						        
							
						}
						else {
							
							 table.addCell("Insolvency Doc");
							  table.addCell("NULL");
							
						}
						
						
						 doc.add(table);
							
  					}
                      
                      
                      if(licenseDetails.getLandSchedule()!= null) {
  						
  						Paragraph pls4 = new Paragraph();
  						pls4.setFont(catFont);
  						pls4.add("4:- Shajra Plan");
  						
  						doc.add(pls4);
  						
  						table = new PdfPTable(2);
  						table.setSpacingBefore(10f);
  						table.setSpacingAfter(10f);
  						table.setWidthPercentage(100f);
						
						if(licenseDetails.getLandSchedule().getAppliedLand()!= null) {
							
							  table.addCell("Applied Land");
							  table.addCell(licenseDetails.getLandSchedule().getAppliedLand());
						        
							
						}
						else {
							
							 table.addCell("Applied Land");
							  table.addCell("NULL");
							
						}
						if(licenseDetails.getLandSchedule().getAppliedLandDoc()!= null) {
							
							  table.addCell("AppliedLandDoc");
							  table.addCell("ATTACHED");
						        
							
						}
						else {
							
							 table.addCell("AppliedLandDoc");
							  table.addCell("NULL");
							
						}
						if(licenseDetails.getLandSchedule().getRevenueRasta()!= null) {
							
							  table.addCell("Revenue Rasta");
							  table.addCell(licenseDetails.getLandSchedule().getRevenueRasta());
						        
							
						}
						else {
							
							 table.addCell("Revenue Rasta");
							  table.addCell("NULL");
							
						}
						if(licenseDetails.getLandSchedule().getRevenueRastaWidth()!= null) {
							
							  table.addCell("RevenueRasta Width");
							  table.addCell(licenseDetails.getLandSchedule().getRevenueRastaWidth());
						        
							
						}
						else {
							
							 table.addCell("RevenueRasta Width");
							  table.addCell("NULL");
							
						}
						if(licenseDetails.getLandSchedule().getWaterCourse()!= null) {
							
							  table.addCell("WaterCourse");
							  table.addCell(licenseDetails.getLandSchedule().getWaterCourse());
						        
							
						}
						else {
							
							 table.addCell("WaterCourse");
							  table.addCell("NULL");
							
						}
						if(licenseDetails.getLandSchedule().getWaterCourseRemark()!= null) {
							
							  table.addCell("WaterCourse Remark");
							  table.addCell(licenseDetails.getLandSchedule().getWaterCourseRemark());
						        
							
						}
						else {
							
							 table.addCell("WaterCourse Remark");
							  table.addCell("NULL");
							
						}
						if(licenseDetails.getLandSchedule().getCompactBlock()!= null) {
							
							  table.addCell("CompactBlock");
							  table.addCell(licenseDetails.getLandSchedule().getCompactBlock());
						        
							
						}
						else {
							
							 table.addCell("CompactBlock");
							  table.addCell("NULL");
							
						}
						if(licenseDetails.getLandSchedule().getCompactBlockRemark()!= null) {
							
							  table.addCell("CompactBlock Remark");
							  table.addCell(licenseDetails.getLandSchedule().getCompactBlockRemark());
						        
							
						}
						else {
							
							 table.addCell("CompactBlock Remark");
							  table.addCell("NULL");
							
						}
						if(licenseDetails.getLandSchedule().getLandSandwiched()!= null) {
							
							  table.addCell("LandSandwiched");
							  table.addCell(licenseDetails.getLandSchedule().getLandSandwiched());
						        
							
						}
						else {
							
							 table.addCell("LandSandwiched");
							  table.addCell("NULL");
							
						}
						if(licenseDetails.getLandSchedule().getLandSandwichedRemark()!= null) {
							
							  table.addCell("LandSandwiched Remark");
							  table.addCell(licenseDetails.getLandSchedule().getLandSandwichedRemark());
						        
							
						}
						else {
							
							 table.addCell("LandSandwiched Remark");
							  table.addCell("NULL");
							
						}
						if(licenseDetails.getLandSchedule().getAcquistion()!= null) {
							
							  table.addCell("Acquistion");
							  table.addCell(licenseDetails.getLandSchedule().getAcquistion());
						        
							
						}
						else {
							
							 table.addCell("Acquistion");
							  table.addCell("NULL");
							
						}
						if(licenseDetails.getLandSchedule().getAcquistionRemark()!= null) {
							
							  table.addCell("Acquistion Remark");
							  table.addCell(licenseDetails.getLandSchedule().getAcquistionRemark());
						        
							
						}
						else {
							
							 table.addCell("Acquistion Remark");
							  table.addCell("NULL");
							
						}
						if(licenseDetails.getLandSchedule().getSectionFour()!= null) {
							
							  table.addCell("SectionFour");
							  table.addCell(licenseDetails.getLandSchedule().getSectionFour());
						        
							
						}
						else {
							
							 table.addCell("SectionFour");
							  table.addCell("NULL");
							
						}
						if(licenseDetails.getLandSchedule().getSectionSix()!= null) {
							
							  table.addCell("SectionSix");
							  table.addCell(licenseDetails.getLandSchedule().getSectionSix());
						        
							
						}
						else {
							
							 table.addCell("SectionSix");
							  table.addCell("NULL");
							
						}
						if(licenseDetails.getLandSchedule().getOrderUpload()!= null) {
							
							  table.addCell("Order Upload");
							  table.addCell(licenseDetails.getLandSchedule().getOrderUpload());
						        
							
						}
						else {
							
							 table.addCell("Order Upload");
							  table.addCell("NULL");
							
						}
						if(licenseDetails.getLandSchedule().getLandCompensation()!= null) {
							
							  table.addCell("Land Compensation");
							  table.addCell(licenseDetails.getLandSchedule().getLandCompensation());
						        
							
						}	
						else {
							
							 table.addCell("Land Compensation");
							  table.addCell("NULL");
							
						}
						if(licenseDetails.getLandSchedule().getReleaseStatus()!= null) {
							
							  table.addCell("Release Status");
							  table.addCell(licenseDetails.getLandSchedule().getReleaseStatus());
						        
							
						}
						else {
							
							 table.addCell("Release Status");
							  table.addCell("NULL");
							
						}
						if(licenseDetails.getLandSchedule().getAwardDate()!= null) {
							
							  table.addCell("Award Date");
							  table.addCell(licenseDetails.getLandSchedule().getAwardDate());
						        
							
						}
						else {
							
							 table.addCell("Award Date");
							  table.addCell("NULL");
							
						}
						if(licenseDetails.getLandSchedule().getReleaseDate()!= null) {
							
							  table.addCell("Release Date");
							  table.addCell(licenseDetails.getLandSchedule().getReleaseDate());
						        
							
						}
						else {
							
							 table.addCell("Release Date");
							  table.addCell("NULL");
							
						}
						if(licenseDetails.getLandSchedule().getSiteDetail()!= null) {
							
							  table.addCell("Site Detail");
							  table.addCell(licenseDetails.getLandSchedule().getSiteDetail());
						        
							
						}
						else {
							
							 table.addCell("Site Detail");
							  table.addCell("NULL");
							
						}
						if(licenseDetails.getLandSchedule().getSiteApproachable()!= null) {
							
							  table.addCell("Site Approachable");
							  table.addCell(licenseDetails.getLandSchedule().getSiteApproachable());
						      
						}
						else {
							
							 table.addCell("Site Approachable");
							  table.addCell("NULL");
							
						}
						
						
						 doc.add(table);
							
    					}
                        
						
                      if(licenseDetails.getLandSchedule()!= null) {
    						
    						Paragraph pls5 = new Paragraph();
    						pls5.setFont(catFont);
    						pls5.add("5:- Site Conditon");
    						
    						doc.add(pls5);
    						
    						table = new PdfPTable(2);
    						table.setSpacingBefore(10f);
    						table.setSpacingAfter(10f);
    						table.setWidthPercentage(100f);
						
						
						
						if(licenseDetails.getLandSchedule().getVacant()!= null) {
							
							  table.addCell("Vacant");
							  table.addCell(licenseDetails.getLandSchedule().getVacant());
						      
						}
						else {
							
							 table.addCell("Vacant");
							  table.addCell("NULL");
							
						}
						if(licenseDetails.getLandSchedule().getVacantRemark()!= null) {
							
							  table.addCell("Vacant Remark");
							  table.addCell(licenseDetails.getLandSchedule().getVacantRemark());
						      
						}
						else {
							
							 table.addCell("Vacant Remark");
							  table.addCell("NULL");
							
						}
						if(licenseDetails.getLandSchedule().getConstruction()!= null) {
							
							  table.addCell("Construction");
							  table.addCell(licenseDetails.getLandSchedule().getConstruction());
						      
						}
						else {
							
							 table.addCell("Construction");
							  table.addCell("NULL");
							
						}
						if(licenseDetails.getLandSchedule().getTypeOfConstruction()!= null) {
							
							  table.addCell("Type Of Construction");
							  table.addCell(licenseDetails.getLandSchedule().getTypeOfConstruction());
						      
						}
						else {
							
							 table.addCell("Type Of Construction");
							  table.addCell("NULL");
							
						}
						if(licenseDetails.getLandSchedule().getConstructionRemark()!= null) {
							
							  table.addCell("Construction Remark");
							  table.addCell(licenseDetails.getLandSchedule().getConstructionRemark());
						      
						}
						else {
							
							 table.addCell("Construction Remark");
							  table.addCell("NULL");
							
						}
						if(licenseDetails.getLandSchedule().getHt()!= null) {
							
							  table.addCell("Ht");
							  table.addCell(licenseDetails.getLandSchedule().getHt());
						      
						}
						else {
							
							 table.addCell("Ht");
							  table.addCell("NULL");
							
						}
						if(licenseDetails.getLandSchedule().getHtRemark()!= null) {
							
							  table.addCell("Ht Remark");
							  table.addCell(licenseDetails.getLandSchedule().getHtRemark());
						      
						}
						else {
							
							 table.addCell("Ht Remark");
							  table.addCell("NULL");
							
						}
						if(licenseDetails.getLandSchedule().getGasRemark()!= null) {
							
							  table.addCell("Gas Remark");
							  table.addCell(licenseDetails.getLandSchedule().getGasRemark());
						      
						}
						else {
							
							 table.addCell("Gas Remark");
							  table.addCell("NULL");
							
						}
						if(licenseDetails.getLandSchedule().getGas()!= null) {
							
							  table.addCell("Gas");
							  table.addCell(licenseDetails.getLandSchedule().getGas());
						      
						}
						else {
							
							 table.addCell("Gas");
							  table.addCell("NULL");
							
						}
						if(licenseDetails.getLandSchedule().getNallah()!= null) {
							
							  table.addCell("Nallah");
							  table.addCell(licenseDetails.getLandSchedule().getNallah());
						      
						}
						else {
							
							 table.addCell("Nallah");
							  table.addCell("NULL");
							
						}
						if(licenseDetails.getLandSchedule().getNallahRemark()!= null) {
							
							  table.addCell("Nallah Remark");
							  table.addCell(licenseDetails.getLandSchedule().getNallahRemark());
						      
						}
						else {
							
							 table.addCell("Nallah Remark");
							  table.addCell("NULL");
							
						}
						if(licenseDetails.getLandSchedule().getRoad()!= null) {
							
							  table.addCell("Road");
							  table.addCell(licenseDetails.getLandSchedule().getRoad());
						      
						}
						else {
							
							 table.addCell("Road");
							  table.addCell("NULL");
							
						}
						if(licenseDetails.getLandSchedule().getRoadWidth()!= null) {
							
							  table.addCell("Road Width");
							  table.addCell(licenseDetails.getLandSchedule().getRoadWidth());
						      
						}
						else {
							
							 table.addCell("Road Width");
							  table.addCell("NULL");
							
						}
						if(licenseDetails.getLandSchedule().getRoadRemark()!= null) {
							
							  table.addCell("Road Remark");
							  table.addCell(licenseDetails.getLandSchedule().getRoadRemark());
						      
						}
						else {
							
							 table.addCell("Road Remark");
							  table.addCell("NULL");
							
						}
						if(licenseDetails.getLandSchedule().getMarginalLand()!= null) {
							
							  table.addCell("MarginalLand");
							  table.addCell(licenseDetails.getLandSchedule().getMarginalLand());
						      
						}
						else {
							
							 table.addCell("MarginalLand");
							  table.addCell("NULL");
							
						}
						if(licenseDetails.getLandSchedule().getMarginalLandRemark()!= null) {
							
							  table.addCell("MarginalLand Remark");
							  table.addCell(licenseDetails.getLandSchedule().getMarginalLandRemark());
						      
						}
						else {
							
							 table.addCell("MarginalLand Remark");
							  table.addCell("NULL");
							
						}
						if(licenseDetails.getLandSchedule().getUtilityLine()!= null) {
							
							  table.addCell("Utility Line");
							  table.addCell(licenseDetails.getLandSchedule().getUtilityLine());
						      
						}
						else {
							
							 table.addCell("Utility Line");
							  table.addCell("NULL");
							
						}
						if(licenseDetails.getLandSchedule().getUtilityWidth()!= null) {
							
							  table.addCell("Utility Width");
							  table.addCell(licenseDetails.getLandSchedule().getUtilityWidth());
						      
						}
						else {
							
							 table.addCell("Utility Width");
							  table.addCell("NULL");
							
						}
						if(licenseDetails.getLandSchedule().getUtilityRemark()!= null) {
							
							  table.addCell("Utility Remark");
							  table.addCell(licenseDetails.getLandSchedule().getUtilityRemark());
						      
						}
						else {
							
							 table.addCell("Utility Remark");
							  table.addCell("NULL");
							
						}
						
						if(licenseDetails.getLandSchedule().getMinimumApproachFour()!= null) {
							
							  table.addCell("MinimumApproachFour");
							  table.addCell(licenseDetails.getLandSchedule().getMinimumApproachFour());
						      
						}
						else {
							
							 table.addCell("MinimumApproachFour");
							  table.addCell("NULL");
							
						}
						
						if(licenseDetails.getLandSchedule().getMinimumApproachEleven()!= null) {
							
							  table.addCell("MinimumApproachEleven");
							  table.addCell(licenseDetails.getLandSchedule().getMinimumApproachEleven());
						      
						}
						else {
							
							 table.addCell("MinimumApproachEleven");
							  table.addCell("NULL");
							
						}
						
						if(licenseDetails.getLandSchedule().getAlreadyConstructedSectorad()!= null) {
							
							  table.addCell("Already Constructed Sectorad");
							  table.addCell(licenseDetails.getLandSchedule().getAlreadyConstructedSectorad());
						      
						}
						else {
							
							 table.addCell("Already Constructed Sectorad");
							  table.addCell("NULL");
							
						}
						

						if(licenseDetails.getLandSchedule().getJoiningOwnLand()!= null) {
							
							  table.addCell("Joining OwnLand");
							  table.addCell(licenseDetails.getLandSchedule().getJoiningOwnLand());
						      
						}
						else {
							
							 table.addCell("Joining OwnLand");
							  table.addCell("NULL");
							
						}
						

						if(licenseDetails.getLandSchedule().getApplicantHasDonated()!= null) {
							
							  table.addCell("Applicant Has Donated");
							  table.addCell(licenseDetails.getLandSchedule().getApplicantHasDonated());
						      
						}
						else {
							
							 table.addCell("Applicant Has Donated");
							  table.addCell("NULL");
							
						}
						

						if(licenseDetails.getLandSchedule().getGiftDeedHibbanama()!= null) {
							
							  table.addCell("GiftDeedHibbanama");
							  table.addCell("ATTACHED																					");
						      
						}
						else {
							
							 table.addCell("GiftDeedHibbanama");
							  table.addCell("NULL");
							
						}
						

						if(licenseDetails.getLandSchedule().getAdjoiningOthersLand()!= null) {
							
							  table.addCell("Adjoining OthersLand");
							  table.addCell(licenseDetails.getLandSchedule().getAdjoiningOthersLand());
						      
						}
						else {
							
							 table.addCell("Adjoining OthersLand");
							  table.addCell("NULL");
							
						}
						

						if(licenseDetails.getLandSchedule().getLandOwnerDonated()!= null) {
							
							  table.addCell("LandOwner Donated");
							  table.addCell(licenseDetails.getLandSchedule().getLandOwnerDonated());
						      
						}
						else {
							
							 table.addCell("LandOwner Donated");
							  table.addCell("NULL");
							
						}
						

						if(licenseDetails.getLandSchedule().getConstructedRowWidth()!= null) {
							
							  table.addCell("Constructed RowWidth");
							  table.addCell(licenseDetails.getLandSchedule().getConstructedRowWidth());
						      
						}
						else {
							
							 table.addCell("Constructed RowWidth");
							  table.addCell("NULL");
							
						}
						

						if(licenseDetails.getLandSchedule().getIrrevocableConsent()!= null) {
							
							  table.addCell("IrrevocableConsent");
							  table.addCell(licenseDetails.getLandSchedule().getIrrevocableConsent());
						      
						}
						else {
							
							 table.addCell("IrrevocableConsent");
							  table.addCell("NULL");
							
						}
						
						if(licenseDetails.getLandSchedule().getUploadRrrevocableConsent()!= null) {
							
							  table.addCell("Upload RrrevocableConsent");
							  table.addCell(licenseDetails.getLandSchedule().getUploadRrrevocableConsent());
						      
						}
						else {
							
							 table.addCell("Upload RrrevocableConsent");
							  table.addCell("NULL");
							
						}
						
						if(licenseDetails.getLandSchedule().getApproachFromProposedSector()!= null) {
							
							  table.addCell("ApproachFromProposedSector");
							  table.addCell(licenseDetails.getLandSchedule().getApproachFromProposedSector());
						      
						}
						else {
							
							 table.addCell("ApproachFromProposedSector");
							  table.addCell("NULL");
							
						}
						
						if(licenseDetails.getLandSchedule().getSectorAndDevelopmentWidth()!= null) {
							
							  table.addCell("Sector And Development Width");
							  table.addCell(licenseDetails.getLandSchedule().getSectorAndDevelopmentWidth());
						      
						}
						else {
							
							 table.addCell("Sector And Development Width");
							  table.addCell("NULL");
							
						}
						
						if(licenseDetails.getLandSchedule().getWhetherAcquired()!= null) {
							
							  table.addCell("Whether Acquired");
							  table.addCell(licenseDetails.getLandSchedule().getWhetherAcquired());
						      
						}
						else {
							
							 table.addCell("Whether Acquired");
							  table.addCell("NULL");
							
						}
						
						if(licenseDetails.getLandSchedule().getWhetherConstructed()!= null) {
							
							  table.addCell("WhetherConstructed");
							  table.addCell(licenseDetails.getLandSchedule().getWhetherConstructed());
						      
						}
						else {
							
							 table.addCell("WhetherConstructed");
							  table.addCell("NULL");
							
						}
						
						if(licenseDetails.getLandSchedule().getServiceSectorRoadAcquired()!= null) {
							
							  table.addCell("Service Sector RoadAcquired");
							  table.addCell(licenseDetails.getLandSchedule().getServiceSectorRoadAcquired());
						      
						}
						else {
							
							 table.addCell("Service SectorRoad Acquired");
							  table.addCell("NULL");
							
						}
						
						if(licenseDetails.getLandSchedule().getServiceSectorRoadConstructed()!= null) {
							
							  table.addCell("Service Sector RoadConstructed");
							  table.addCell(licenseDetails.getLandSchedule().getServiceSectorRoadConstructed());
						      
						}
						else {
							
							 table.addCell("Service Sector RoadConstructed");
							  table.addCell("NULL");
							
						}
						
						if(licenseDetails.getLandSchedule().getApproachFromInternalCirculation()!= null) {
							
							  table.addCell("Approach From InternalCirculation");
							  table.addCell(licenseDetails.getLandSchedule().getApproachFromInternalCirculation());
						      
						}
						else {
							
							 table.addCell("Approach From InternalCirculation");
							  table.addCell("NULL");
							
						}
						
						if(licenseDetails.getLandSchedule().getInternalAndSectoralWidth()!= null) {
							
							  table.addCell("Internal And SectoralWidth");
							  table.addCell(licenseDetails.getLandSchedule().getInternalAndSectoralWidth());
						      
						}
						else {
							
							 table.addCell("Internal And SectoralWidth");
							  table.addCell("NULL");
							
						}
						
						if(licenseDetails.getLandSchedule().getParentLicenceApproach()!= null) {
							
							  table.addCell("Parent LicenceApproach");
							  table.addCell(licenseDetails.getLandSchedule().getParentLicenceApproach());
						      
						}
						else {
							
							 table.addCell("Parent LicenceApproach");
							  table.addCell("NULL");
							
						}
						
						if(licenseDetails.getLandSchedule().getAvailableExistingApproach()!= null) {
							
							  table.addCell("Available Existing Approach");
							  table.addCell(licenseDetails.getLandSchedule().getAvailableExistingApproach());
						      
						}
						else {
							
							 table.addCell("Available Existing Approach");
							  table.addCell("NULL");
							
						}
						
						if(licenseDetails.getLandSchedule().getAvailableExistingApproachDoc()!= null) {
							
							  table.addCell("Available Existing ApproachDoc");
							  table.addCell("ATTACHED");
						      
						}
						else {
							
							 table.addCell("Available Existing ApproachDoc");
							  table.addCell("NULL");
							
						}
						
						if(licenseDetails.getLandSchedule().getWhetherAcquiredForInternalCirculation()!= null) {
							
							  table.addCell("Whether Acquired For InternalCirculation");
							  table.addCell(licenseDetails.getLandSchedule().getWhetherAcquiredForInternalCirculation());
						      
						}
						else {
							
							 table.addCell("Whether Acquired For InternalCirculation");
							  table.addCell("NULL");
							
						}
						
						if(licenseDetails.getLandSchedule().getWhetherConstructedForInternalCirculation()!= null) {
							
							  table.addCell("Whether Constructed For InternalCirculation");
							  table.addCell(licenseDetails.getLandSchedule().getWhetherConstructedForInternalCirculation());
						      
						}
						else {
							
							 table.addCell("Whether Constructed For InternalCirculation");
							  table.addCell("NULL");
							
						}
						
						doc.add(table);
						
  					}
                      
                      
                      if(licenseDetails.getLandSchedule()!= null) {
  						
  						Paragraph pls6 = new Paragraph();
  						pls6.setFont(catFont);
  						pls6.add("6:- Enclose The Docume");
  						
  						doc.add(pls6);
  						
  						table = new PdfPTable(2);
  						table.setSpacingBefore(10f);
  						table.setSpacingAfter(10f);
  						table.setWidthPercentage(100f);
						
						
						if(licenseDetails.getLandSchedule().getLandSchedule()!= null) {
							
							  table.addCell("LandSchedule");
							  table.addCell("ATTACHED");
						      
						}
						else {
							
							 table.addCell("LandSchedule");
							  table.addCell("NULL");
							
						}
						
						
						if(licenseDetails.getLandSchedule().getMutation()!= null) {
							
							  table.addCell("Mutation");
							  table.addCell("ATTACHED");
						      
						}
						else {
							
							 table.addCell("Mutation");
							  table.addCell("NULL");
							
						}
						if(licenseDetails.getLandSchedule().getJambandhi()!= null) {
							
							  table.addCell("Jambandhi");
							  table.addCell("ATTACHED");
						      
						}
						else {
							
							 table.addCell("Jambandhi");
							  table.addCell("NULL");
							
						}
						if(licenseDetails.getLandSchedule().getDetailsOfLease()!= null) {
							
							  table.addCell("Details Of Lease");
							  table.addCell("ATTACHED");
						      
						}
						else {
							
							 table.addCell("Details Of Lease");
							  table.addCell("NULL");
							
						}
						if(licenseDetails.getLandSchedule().getAddSalesDeed()!= null) {
							
							  table.addCell("Add SalesDeed");
							  table.addCell("ATTACHED");
						      
						}
						else {
							
							 table.addCell("Add SalesDeed");
							  table.addCell("NULL");
							
						}
						if(licenseDetails.getLandSchedule().getRevisedLandSchedule()!= null) {
							
							  table.addCell("Revised LandSchedule");
							  table.addCell(licenseDetails.getLandSchedule().getRevisedLandSchedule());
						      
						}
						else {
							
							 table.addCell("Revised LandSchedule");
							  table.addCell("NULL");
							
						}
						if(licenseDetails.getLandSchedule().getCopyofSpaBoard()!= null) {
							
							  table.addCell("Copy of SpaBoard");
							  table.addCell("ATTACHED");
						      
						}
						else {
							
							 table.addCell("Copy of SpaBoard");
							  table.addCell("NULL");
							
						}
						if(licenseDetails.getLandSchedule().getCopyOfShajraPlan()!= null) {
							
							  table.addCell("Copy Of ShajraPlan");
							  table.addCell("ATTACHED");
						      
						}
						else {
							
							 table.addCell("Copy Of ShajraPlan");
							  table.addCell("NULL");
							
						}
						if(licenseDetails.getLandSchedule().getProposedLayoutPlan()!= null) {
							
							  table.addCell("Proposed LayoutPlan");
							  table.addCell(licenseDetails.getLandSchedule().getProposedLayoutPlan());
						      
						}
						else {
							
							 table.addCell("Proposed LayoutPlan");
							  table.addCell("NULL");
							
						}
						if(licenseDetails.getLandSchedule().getRevisedLansSchedule()!= null) {
							
							  table.addCell("Revised LansSchedule");
							  table.addCell(licenseDetails.getLandSchedule().getRevisedLansSchedule());
						      
						}
						else {
							
							 table.addCell("Revised LansSchedule");
							  table.addCell("NULL");
							
						}
						if(licenseDetails.getLandSchedule().getAreaOfParentLicence()!= null) {
							
							  table.addCell("Area Of ParentLicence");
							  table.addCell(licenseDetails.getLandSchedule().getAreaOfParentLicence());
						      
						}
						else {
							
							 table.addCell("Area Of ParentLicence");
							  table.addCell("NULL");
							
						}
						
						if(licenseDetails.getLandSchedule().getReraRegistered()!= null) {
							
							  table.addCell("Rera Registered");
							  table.addCell(licenseDetails.getLandSchedule().getReraRegistered());
						      
						}
						else {
							
							 table.addCell("Rera Registered");
							  table.addCell("NULL");
							
						}
						
						if(licenseDetails.getLandSchedule().getReraDocUpload()!= null) {
							
							  table.addCell("Rera DocUpload");
							  table.addCell(licenseDetails.getLandSchedule().getReraDocUpload());
						      
						}
						else {
							
							 table.addCell("Rera DocUpload");
							  table.addCell("NULL");
							
						}
						
						if(licenseDetails.getLandSchedule().getReraNonRegistrationDoc()!= null) {
							
							  table.addCell("Rera Non RegistrationDoc");
							  table.addCell(licenseDetails.getLandSchedule().getReraNonRegistrationDoc());
						      
						}
						else {
							
							 table.addCell("Rera Non RegistrationDoc");
							  table.addCell("NULL");
							
						}
						
					/**	List value
					   if(licenseDetails.getLandSchedule().getLandScheduleDetails()!= null) {
							
							  table.addCell("LandScheduleDetails");
							  table.addCell(licenseDetails.getLandSchedule().getLandScheduleDetails());
						      
						}
						else {
							
							 table.addCell("LandScheduleDetails");
							  table.addCell("NULL");
							
						}*/
						
						if(licenseDetails.getLandSchedule().getAnyOther()!= null) {
							
							  table.addCell("AnyOther");
							  table.addCell(licenseDetails.getLandSchedule().getAnyOther());
						      
						}
						else {
							
							 table.addCell("AnyOther");
							  table.addCell("NULL");
							
						}
						
						if(licenseDetails.getLandSchedule().getAnyOtherRemark()!= null) {
							
							  table.addCell("AnyOtherRemark");
							  table.addCell(licenseDetails.getLandSchedule().getAnyOtherRemark());
						      
						}
						else {
							
							 table.addCell("AnyOtherRemark");
							  table.addCell("NULL");
							
						}
						
						if(licenseDetails.getLandSchedule().getNone()!= null) {
							
							  table.addCell("None");
							  table.addCell(licenseDetails.getLandSchedule().getNone());
						      
						}
						else {
							
							 table.addCell("None");
							  table.addCell("NULL");
							
						}
						
						if(licenseDetails.getLandSchedule().getNoneRemark()!= null) {
							
							  table.addCell("NoneRemark");
							  table.addCell(licenseDetails.getLandSchedule().getNoneRemark());
						      
						}
						else {
							
							 table.addCell("NoneRemark");
							  table.addCell("NULL");
							
						}
						
						if(licenseDetails.getLandSchedule().getAdjoiningOwnLand()!= null) {
							
							  table.addCell("AdjoiningOwnLand");
							  table.addCell(licenseDetails.getLandSchedule().getAdjoiningOwnLand());
						      
						}
						else {
							
							 table.addCell("AdjoiningOwnLand");
							  table.addCell("NULL");
							
						}
						
						
						
						doc.add(table);
											
					}
					
					
					
					
					
					
					
					
					
					
					
					if(licenseDetails.getLandSchedule().getLandScheduleDetails()!= null && licenseDetails.getLandSchedule().getLandScheduleDetails().size()>0) {
						
						List<LandScheduleDetails> z= licenseDetails.getLandSchedule().getLandScheduleDetails();
						
						System.out.println(z);		                   
						Paragraph pald1 = new Paragraph();
						pald1.add("LandSchedule Details");
						doc.add(pald1);
						
						table = new PdfPTable(9);
						table.setSpacingBefore(10f);
						table.setSpacingAfter(10f);
						table.setWidthPercentage(100f);
						
						PdfPCell c3 = new PdfPCell(new Phrase("Previous Licensenumber"));
				        c3.setHorizontalAlignment(Element.ALIGN_CENTER);
				        table.addCell(c3);
				        
				        c3 = new PdfPCell(new Phrase("AreaOfParentLicence"));
				        c3.setHorizontalAlignment(Element.ALIGN_CENTER);
				        table.addCell(c3);
				        
				        c3 = new PdfPCell(new Phrase("PurposeOfParentLicence"));
				        c3.setHorizontalAlignment(Element.ALIGN_CENTER);
				        table.addCell(c3);

				        c3 = new PdfPCell(new Phrase("Validity"));
				        c3.setHorizontalAlignment(Element.ALIGN_CENTER);
				        table.addCell(c3);
				        
				        c3 = new PdfPCell(new Phrase("Date"));
				        c3.setHorizontalAlignment(Element.ALIGN_CENTER);
				        table.addCell(c3);
				        
				        c3 = new PdfPCell(new Phrase("AreaAppliedmigration"));
				        c3.setHorizontalAlignment(Element.ALIGN_CENTER);
				        table.addCell(c3);
				        
				        c3 = new PdfPCell(new Phrase("KhasraNumber"));
				        c3.setHorizontalAlignment(Element.ALIGN_CENTER);
				        table.addCell(c3);
				        
				        c3 = new PdfPCell(new Phrase("Area"));
				        c3.setHorizontalAlignment(Element.ALIGN_CENTER);
				        table.addCell(c3);
				        
				        c3 = new PdfPCell(new Phrase("BalanceOfParentLicence"));
				        c3.setHorizontalAlignment(Element.ALIGN_CENTER);
				        table.addCell(c3);
				        
				        
						
						for(int j=0;j<z.size();j++) {
							LandScheduleDetails g =  z.get(j);
						
						  table.addCell(g.getPreviousLicensenumber());
						  
						 
						  table.addCell(g.getAreaOfParentLicence());
						  
						
						  table.addCell(g.getPurposeOfParentLicence());
						  
						  table.addCell(g.getValidity());
						  
						  table.addCell(g.getDate());
						  
						  table.addCell(g.getAreaAppliedmigration());
						  
						  table.addCell(g.getKhasraNumber());
						  
						  table.addCell(g.getArea());
						  
						  table.addCell(g.getBalanceOfParentLicence());
						  
					
						
		                  
		                   }
		                   doc.add(table);
					}
					
					if(licenseDetails.getDetailsofAppliedLand()!= null) {
	                	
		                
	                  	table = new PdfPTable(2);
	                 	table.setSpacingBefore(10f);
	                  	table.setSpacingAfter(10f);
		                table.setWidthPercentage(100f);
		                Paragraph doal = new Paragraph();
	                	   doal.setFont(blackFont1);
	                	   doal.add("Details of AppliedLand");
	                	   doc.add(doal);
		                
		                if(licenseDetails.getDetailsofAppliedLand().getDgps()!=null && licenseDetails.getDetailsofAppliedLand().getDgps().size()>0) {
		                	 
                         	   
                         	 
                       	   
		                	List<List<GISDeatils>> f= licenseDetails.getDetailsofAppliedLand().getDgps();
	                           for(int j=0;j<f.size();j++) {
	                        	   List<GISDeatils> g =  f.get(j);
	                        	   for(int k=0;k<g.size();k++) {

	                        	   GISDeatils h = g.get(k);
	                        	   
	                        	 
	                        	   
	                        	   table.addCell("Latitude");
								   table.addCell(h.getLatitude());
								   
								   table.addCell("Longitude");
								   table.addCell(h.getLongitude());
									  
	                        	   }
	                        	   
	                        	   
	                        	   
	                           }
		                }
		                
		                doc.add(table); 
					}
					

					
                   if(licenseDetails.getDetailsofAppliedLand()!= null) {
	                	
		                
	                  	table = new PdfPTable(2);
	                 	table.setSpacingBefore(10f);
	                  	table.setSpacingAfter(10f);
		                table.setWidthPercentage(100f);
		                
		                Paragraph doal1 = new Paragraph();
	                	   doal1.setFont(blackFont1);
	                	   doal1.add("Bifurcation Of Purpose");
	                	   doc.add(doal1);
	                	   
	                	   table.addCell("Total Applied Area");
						   table.addCell(licenseDetails.getDetailsofAppliedLand().getDetailsAppliedLandPlot().getTotalAreaScheme());
	                	   
	                	   
		            
					
					if(licenseDetails.getDetailsofAppliedLand().getPurposeDetails()!=null && licenseDetails.getDetailsofAppliedLand().getPurposeDetails().size()>0) {
						

						
						List<PurposeDetails> f= licenseDetails.getDetailsofAppliedLand().getPurposeDetails();
						// String k= objectMapper.writeValueAsString(f);
					
						
						
						for(int j=0;j<f.size();j++) {
							
							PurposeDetails l1 = f.get(j);
							
							
							 table.addCell("ID");
							   table.addCell(l1.getId());
							   
							  
							   table.addCell("Name");
							   table.addCell(l1.getName());
							   
							   table.addCell("Code");
							   table.addCell(l1.getCode());
							
							   
							   table.addCell("Area");
							   table.addCell(l1.getArea());
							   
							   table.addCell("fars");
							   table.addCell(l1.getFar());
							   
							   
							   table.addCell("MaxPercentage");
							   table.addCell(l1.getMaxPercentage());
							   

							   table.addCell("MinPercentage");
							   table.addCell(l1.getMinPercentage());
							   
							   table.addCell("MinPercentage");
							   table.addCell(l1.getMinPercentage());
							   
							   
							   if(l1.getPurposeDetail()!=null && l1.getPurposeDetail().size()>0) {
								   
									
									List<PurposeDetails> l2 = l1.getPurposeDetail();
									
									for(int k1=0;k1<l2.size();k1++) {
										
										PurposeDetails l3 = l2.get(k1);
										
								  table.addCell("ID");
								   table.addCell(l3.getId());
								   
								   
								   table.addCell("Name");
								   table.addCell(l3.getName());
								   
								   table.addCell("Code");
								   table.addCell(l3.getCode());
								
								   
								   table.addCell("Area");
								   table.addCell(l3.getArea());
								   
								   table.addCell("fars");
								   table.addCell(l3.getFar());
								   
								   
								   table.addCell("MaxPercentage");
								   table.addCell(l3.getMaxPercentage());
								   

								   table.addCell("MinPercentage");
								   table.addCell(l3.getMinPercentage());
								   
								   table.addCell("MinPercentage");
								   table.addCell(l3.getMinPercentage());
								   
								   if(l3.getPurposeDetail()!=null && l3.getPurposeDetail().size()>0) {
									   
									  List<PurposeDetails> l4 = l3.getPurposeDetail();
									   
									   for(int k2=0;k2<l4.size();k2++) {
										   PurposeDetails l5= l4.get(k2);
										   
										   
										  
										   table.addCell("ID");
										   table.addCell(l5.getId());
										   
										   
										   table.addCell("Name");
										   table.addCell(l5.getName());
										   
										   table.addCell("Code");
										   table.addCell(l5.getCode());
										
										   
										   table.addCell("Area");
										   table.addCell(l5.getArea());
										   
										   table.addCell("fars");
										   table.addCell(l5.getFar());
										   
										   
										   table.addCell("MaxPercentage");
										   table.addCell(l5.getMaxPercentage());
										   

										   table.addCell("MinPercentage");
										   table.addCell(l5.getMinPercentage());
										   
										   table.addCell("MinPercentage");
										   table.addCell(l5.getMinPercentage());
										   
										   
									   }
									   
									   
									   
									   
									   
									   
								   }
								   
								  								   
								   
								   
								   
									}
								}
							
							
							
						}
						
					                           
					}
					
					
								
					doc.add(table);
					
					
  }
                   
                   if(licenseDetails.getDetailsofAppliedLand()!= null) {
	                	
		                
	                  	table = new PdfPTable(2);
	                 	table.setSpacingBefore(10f);
	                  	table.setSpacingAfter(10f);
		                table.setWidthPercentage(100f);
		                
		                Paragraph layout = new Paragraph();
						 layout.setFont(blackFont1);
						 layout.add("Layout Plan Document");
						 doc.add(layout);
	              	     
	              	     
	              	   if(licenseDetails.getDetailsofAppliedLand().getDetailsAppliedLandPlot().getLayoutPlanPdf()!= null) {
							
	              		 table.addCell("Layout PlanPdf");
						   table.addCell("ATTACHED");
						        
							
						}
						else {
							
							 table.addCell("Layout PlanPdf");
							  table.addCell("NULL");
							
						}
	              	 if(licenseDetails.getDetailsofAppliedLand().getDetailsAppliedLandPlot().getLayoutPlanDxf()!= null) {
							
	              		 table.addCell("Layout PlanDxf");
						   table.addCell("ATTACHED");
						        
							
						}
						else {
							
							 table.addCell("Layout PlanDxf");
							  table.addCell("NULL");
							
						}
	              	 if(licenseDetails.getDetailsofAppliedLand().getDetailsAppliedLandPlot().getUndertaking()!= null) {
							
	              		 table.addCell("Undertaking");
						   table.addCell("ATTACHED");
						        
							
						}
						else {
							
							 table.addCell("Undertaking");
							  table.addCell("NULL");
							
						}
	              	 if(licenseDetails.getDetailsofAppliedLand().getDetailsAppliedLandPlot().getDevelopmentPlan()!= null) {
							
	              		 table.addCell("DevelopmentPlan");
						   table.addCell("ATTACHED");
						        
							
						}
						else {
							
							 table.addCell("DevelopmentPlan");
							  table.addCell("NULL");
							
						}
	              	if(licenseDetails.getDetailsofAppliedLand().getDetailsAppliedLandPlot().getSectoralPlan()!= null) {
						
	              		 table.addCell("SectoralPlan");
						   table.addCell("ATTACHED");
						        
							
						}
						else {
							
							 table.addCell("SectoralPlan");
							  table.addCell("NULL");
							
						}
	              	if(licenseDetails.getDetailsofAppliedLand().getDetailsAppliedLandPlot().getExplanatoryNote()!= null) {
						
	              		 table.addCell("Explanatory Note");
						   table.addCell("ATTACHED");
						        
							
						}
						else {
							
							 table.addCell("Explanatory Note");
							  table.addCell("NULL");
							
						}
	              	if(licenseDetails.getDetailsofAppliedLand().getDetailsAppliedLandPlot().getGuideMap()!= null) {
						
	              		 table.addCell("GuideMap");
						   table.addCell("ATTACHED");
						        
							
						}
						else {
							
							 table.addCell("GuideMap");
							  table.addCell("NULL");
							
						}
	              	if(licenseDetails.getDetailsofAppliedLand().getDetailsAppliedLandPlot().getIdemnityBondDoc()!= null) {
						
	              		 table.addCell("Idemnity BondDoc");
						   table.addCell("ATTACHED");
						        
							
						}
						else {
							
							 table.addCell("Idemnity BondDoc");
							  table.addCell("NULL");
							
						}
                	   
	              	   
	              	 doc.add(table);
	              	   
                   }
                   
                   if(licenseDetails.getFeesAndCharges()!= null) {
	                	
                	   FeesAndCharges  y = licenseDetails.getFeesAndCharges();
                	   
	                  	table = new PdfPTable(2);
	                 	table.setSpacingBefore(10f);
	                  	table.setSpacingAfter(10f);
		                table.setWidthPercentage(100f);
		                
		                Paragraph fac = new Paragraph();
		                fac.setFont(blackFont1);
		                fac.add("Fees and Charges");
						 doc.add(fac);
	              	     
						   table.addCell("TotalArea");
						   table.addCell(y.getTotalArea());
						   
						   table.addCell("Purpose");
						   table.addCell(y.getPurpose());

						   table.addCell("Potential");
						   table.addCell(y.getPotential());
						  
						   table.addCell("DevelopmentPlan");
						   table.addCell(y.getDevelopmentPlan());
						  
						   
						   table.addCell("LicNumber");
						   table.addCell(y.getLicNumber());
						  
						   
						   table.addCell("Amount");
						   table.addCell(y.getAmount());
						  
						   
						   table.addCell("Amount Adjusted");
						   table.addCell(y.getAmountAdjusted());
						   
						   table.addCell("Amount Payable");
						   table.addCell(y.getAmountPayable());
						   
						   table.addCell("ScrutinyFee");
						   table.addCell(y.getScrutinyFee());
						   
						   table.addCell("License Fee");
						   table.addCell(y.getLicenseFee());
						   
						   table.addCell("Conversion Charges");
						   table.addCell(y.getConversionCharges());
						   
						   table.addCell("Payable Now");
						   table.addCell(y.getPayableNow());
						   
						   table.addCell("Remark");
						   table.addCell(y.getRemark());
						   
						   table.addCell("Adjust Fee");
						   table.addCell(y.getAdjustFee());
						   
						   table.addCell("Belongs Developer");
						   table.addCell(y.getBelongsDeveloper());
						   
						   table.addCell("ConsentLetter");
						   table.addCell(y.getConsentLetter());
						   
						   table.addCell("State Infrastructure Development Charges");
						   table.addCell(y.getStateInfrastructureDevelopmentCharges());
						   
						   table.addCell("IDW");
						   table.addCell(y.getIDW());
						   
						   table.addCell("EDC");
						   table.addCell(y.getEDC());
						   
						   
						  
               	   
	              	   
	              	 doc.add(table);
	              	   
                  }
 
  
  

					
					
			
			
			
		
		doc.close();
		writer.close();
		}
		
}
	
	}
	

}
