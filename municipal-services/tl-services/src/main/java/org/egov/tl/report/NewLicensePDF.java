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
import org.egov.common.contract.response.ResponseInfo;
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

 //   private static String hindifont = "D:\\Bikash_UPYOG\\UPYOG\\municipal-services\\tl-services\\src\\main\\resources\\font\\FreeSans.ttf";
//	private static String hindifont = "D:\\upyog code\\UPYOG1\\UPYOG\\municipal-services\\tl-services\\src\\main\\resources\\font\\FreeSans.ttf";
	private static String hindifont = "/opt/UPYOG/municipal-services/tl-services/src/main/resources/font/FreeSans.ttf";
//	private static String hindifont ="D:\\Workspace_27-04-2023\\UPYOG\\municipal-services\\tl-services\\src\\main\\resources\\font\\\\FreeSans.ttf";

	@Autowired BPANotificationUtil bPANotificationUtil;
	@Autowired LicenseService licenseService;

	@Autowired public RestTemplate restTemplate;
	@Autowired TLConfiguration config;
	
	@Autowired
	Environment environment;
	
	
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
					
					//table.addCell(replaceNullWithNA("Name");
					  //table.addCell(replaceNullWithNA(licenseDetails.getApplicantInfo().getDevDetail().getAddInfo().getName()));
					  
					  table.addCell("Name");
					  table.addCell(replaceNullWithNA(licenseDetails.getApplicantInfo().getDevDetail().getAddInfo().getCompanyName()));
					  
					  table.addCell("Address");
					  table.addCell(replaceNullWithNA(licenseDetails.getApplicantInfo().getDevDetail().getAddInfo().getRegisteredAddress()));
					  
					  table.addCell("EmailId");
					  table.addCell(replaceNullWithNA(licenseDetails.getApplicantInfo().getDevDetail().getAddInfo().getEmailId()));
					  
					  
					  table.addCell("Developer Type");
					  table.addCell(replaceNullWithNA(licenseDetails.getApplicantInfo().getDevDetail().getAddInfo().getShowDevTypeFields()));
					  
					  table.addCell("Cin_Number");
					  table.addCell(replaceNullWithNA(licenseDetails.getApplicantInfo().getDevDetail().getAddInfo().getCin_Number()));
					  
					 
					  
					  
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
					
					  table.addCell(replaceNullWithNA(g.getDin()));
					  
					 
					  table.addCell(replaceNullWithNA(g.getName()));
					  
					
					  table.addCell(replaceNullWithNA(g.getContactNumber()));
					  
				
					
	                  
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
				
				  table.addCell(replaceNullWithNA(g.getDin()));
				  
				 
				  table.addCell(replaceNullWithNA(g.getName()));
				  
				
				  table.addCell(replaceNullWithNA(g.getContactNumber()));
				  
			
				
                  
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
					
					  table.addCell(replaceNullWithNA(g.getName()));
					  
					 
					  table.addCell(replaceNullWithNA(g.getDesignition()));
					  
					
					  table.addCell(replaceNullWithNA(g.getPercentage()));
					  
				
					
	                  
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
							  table.addCell(replaceNullWithNA(g.getName()));
							  
							  table.addCell("Mobile No");
							  table.addCell(replaceNullWithNA(g.getMobileNumber()));
							  
							  table.addCell("Emailid for Authorized Signatory");
							  table.addCell(replaceNullWithNA(g.getEmailId()));
							  
							  table.addCell("Pan No");
							  table.addCell(replaceNullWithNA(g.getPan()));
	                          
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
							  table.addCell(replaceNullWithNA(licenseDetails.getApplicantPurpose().getPurpose()));
							  
							 // table.addCell(replaceNullWithNA("State");
							  //table.addCell(replaceNullWithNA(licenseDetails.getApplicantPurpose().getState()));
							  
							  table.addCell("TotalArea");
							  table.addCell(replaceNullWithNA(licenseDetails.getApplicantPurpose().getTotalArea()));
						        
						
						
						
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
//					        table.addCell(replaceNullWithNA(c2);
//					        
//					        c2 = new PdfPCell(new Phrase("Kanal"));
//					        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
//					        table.addCell(replaceNullWithNA(c2);
//					        
//					        c2 = new PdfPCell(new Phrase("Marla"));
//					        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
//					        table.addCell(replaceNullWithNA(c2);
//					        
//					        c2 = new PdfPCell(new Phrase("Hadbast No"));
//					        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
//					        table.addCell(replaceNullWithNA(c2);
//					        
//					        c2 = new PdfPCell(new Phrase("Bigha"));
//					        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
//					        table.addCell(replaceNullWithNA(c2);
//					        
//					        c2 = new PdfPCell(new Phrase("Biswansi"));
//					        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
//					        table.addCell(replaceNullWithNA(c2);
//					        
//					        c2 = new PdfPCell(new Phrase("Biswa"));
//					        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
//					        table.addCell(replaceNullWithNA(c2);
//					        
//					       /** c2 = new PdfPCell(new Phrase("LandOwner"));
//					        PdfPCell  c3 = new PdfPCell(new Phrase(s1, font));
//					        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
//					        table.addCell(replaceNullWithNA(c2);*/
//					        
//					        c2 = new PdfPCell(new Phrase("Collaboration"));
//					        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
//					        table.addCell(replaceNullWithNA(c2);
//					        
//					        c2 = new PdfPCell(new Phrase("Developer Company"));
//					        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
//					        table.addCell(replaceNullWithNA(c2);
//					        
//					        c2 = new PdfPCell(new Phrase("AgreementValid From"));
//					        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
//					        table.addCell(replaceNullWithNA(c2);
//					        
//					        c2 = new PdfPCell(new Phrase("Validity Date"));
//					        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
//					        table.addCell(replaceNullWithNA(c2);
//					        
//					        c2 = new PdfPCell(new Phrase("Agreement Irrevocialble"));
//					        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
//					        table.addCell(replaceNullWithNA(c2);
//					        
//					        c2 = new PdfPCell(new Phrase("AuthSignature"));
//					        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
//					        table.addCell(replaceNullWithNA(c2);
//					        
//					        c2 = new PdfPCell(new Phrase("Name AuthSign"));
//					        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
//					        table.addCell(replaceNullWithNA(c2);
//					       
//					        c2 = new PdfPCell(new Phrase("Registering Authority"));
//					        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
//					        table.addCell(replaceNullWithNA(c2);
//					        
//					        c2 = new PdfPCell(new Phrase("Registering AuthorityDoc"));
//					        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
//					        table.addCell(replaceNullWithNA(c2);
//					        
//					        c2 = new PdfPCell(new Phrase("Khewats"));
//					        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
//					        table.addCell(replaceNullWithNA(c2);
//					        
//					        c2 = new PdfPCell(new Phrase("Consolidated Total"));
//					        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
//					        table.addCell(replaceNullWithNA(c2);
//					        
//					        c2 = new PdfPCell(new Phrase("NonConsolidated Total"));
//					        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
//					        table.addCell(replaceNullWithNA(c2);
//					        
//					        c2 = new PdfPCell(new Phrase("Edit Khewats"));
//					        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
//					        table.addCell(replaceNullWithNA(c2);
//					        
//					        c2 = new PdfPCell(new Phrase("Edit RectangleNo"));
//					        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
//					        table.addCell(replaceNullWithNA(c2);
//					        
//					        c2 = new PdfPCell(new Phrase("TypeLand"));
//					        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
//					        table.addCell(replaceNullWithNA(c2);








					       

					       
					        table.addCell("District");
					        table.addCell(replaceNullWithNA(appliedLandDetails.getDistrict().getLabel()));
					        table.addCell("Potential");
					        table.addCell(replaceNullWithNA(appliedLandDetails.getPotential()));
					        table.addCell("Zone");
					        table.addCell(replaceNullWithNA(appliedLandDetails.getZone()));
					        table.addCell("Development Plan");
					        table.addCell(replaceNullWithNA(appliedLandDetails.getDevelopmentPlan().getLabel()));
					        table.addCell("Sector");
					        table.addCell(replaceNullWithNA(appliedLandDetails.getSector()));
					        table.addCell("Tehsil");
						    table.addCell(replaceNullWithNA(appliedLandDetails.getTehsil().getLabel()));
					        table.addCell("Revenue Estate");
					        table.addCell(replaceNullWithNA(appliedLandDetails.getRevenueEstate().getLabel()));
					        table.addCell("Mustil");
					        table.addCell(replaceNullWithNA(appliedLandDetails.getMustil()));
					        table.addCell("Consolidation Type");
					        table.addCell(replaceNullWithNA(appliedLandDetails.getConsolidationType()));
					        table.addCell("Sarsai");
					        table.addCell(replaceNullWithNA(appliedLandDetails.getSarsai()));
					        table.addCell("Kanal");
					        table.addCell(replaceNullWithNA(appliedLandDetails.getKanal()));
					        table.addCell("Marla");
					        table.addCell(replaceNullWithNA(appliedLandDetails.getMarla()));
					        table.addCell("HadbastNo");
					        table.addCell(replaceNullWithNA(appliedLandDetails.getHadbastNo()));
					        table.addCell("Bigh");
					        table.addCell(replaceNullWithNA(appliedLandDetails.getBigha()));
					        table.addCell("Biswansi");
					        table.addCell(replaceNullWithNA(appliedLandDetails.getBiswansi()));
					        table.addCell("Biswa");
					        table.addCell(replaceNullWithNA(appliedLandDetails.getBiswa()));
					        table.addCell("LandOwner Registry");
					        table.addCell(replaceNullWithNA(appliedLandDetails.getLandOwnerRegistry()));
					        table.addCell("Collaboration");
					        table.addCell(replaceNullWithNA(appliedLandDetails.getCollaboration()));
					        table.addCell("Developer ompany");
					        table.addCell(replaceNullWithNA(appliedLandDetails.getDeveloperCompany()));
					        table.addCell("Agreement Valid From");
					        table.addCell(replaceNullWithNA(appliedLandDetails.getAgreementValidFrom()));
					        table.addCell("AgreementValidTo");
					        table.addCell(replaceNullWithNA(appliedLandDetails.getAgreementValidTo()));
					        table.addCell("Agreement Irrevocialble");
					        table.addCell(replaceNullWithNA(appliedLandDetails.getAgreementIrrevocialble()));
					        table.addCell("AuthSignature");
					        table.addCell(replaceNullWithNA(appliedLandDetails.getAuthSignature()));
					        table.addCell("Name Auth Sign");
					        table.addCell(replaceNullWithNA(appliedLandDetails.getNameAuthSign()));
					        table.addCell("Registering Authority");
					        table.addCell(replaceNullWithNA(appliedLandDetails.getRegisteringAuthority()));
					        table.addCell("Registering Authority Doc");
					        table.addCell(replaceNullWithNA(appliedLandDetails.getRegisteringAuthorityDoc()));
					        table.addCell("Khewats");
					        table.addCell(replaceNullWithNA(appliedLandDetails.getKhewats()));
					        table.addCell("Consolidated Total");
					        table.addCell(replaceNullWithNA(appliedLandDetails.getConsolidatedTotal()));
					        table.addCell("Non Consolidated Total");
					        table.addCell(replaceNullWithNA(appliedLandDetails.getNonConsolidatedTotal()));
					        table.addCell("Edit Khewats");
					        table.addCell(replaceNullWithNA(appliedLandDetails.getEditKhewats()));
					        table.addCell("Edit Rectangle No");
					        table.addCell(replaceNullWithNA(appliedLandDetails.getEditRectangleNo()));
					        table.addCell("TypeLand");
					        table.addCell(replaceNullWithNA(appliedLandDetails.getTypeLand().getLabel()));
					        table.addCell("RectangleNo");
					        table.addCell(replaceNullWithNA(appliedLandDetails.getRectangleNo()));
					        table.addCell("Non ConsolidationType");
					        table.addCell(replaceNullWithNA(appliedLandDetails.getNonConsolidationType()));
					        table.addCell("LandOwnerSPAGPADoc");
					        table.addCell(replaceNullWithNA(appliedLandDetails.getLandOwnerSPAGPADoc()));
					        table.addCell("DeveloperSPAGPADoc");
					        table.addCell(replaceNullWithNA(appliedLandDetails.getDeveloperSPAGPADoc()));
					        
					        
					        
						 
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
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getLicenseApplied()));
							
								  table.addCell("License Number");
								  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getLicenseNumber()));
							  
								  table.addCell("Potential");
								  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getPotential()));
							  
								  table.addCell("SiteLoc");
								  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getSiteLoc()));
							  
								  table.addCell("Approach Type");
								  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getApproachType()));
							  
								  table.addCell("Approach RoadWidth");
								  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getApproachRoadWidth()));
							      
								  table.addCell("Specify");
								  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getSpecify()));
							   
								  table.addCell("TypeLand");
								  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getTypeLand()));
							 	
								  table.addCell("Third Party");
								  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getThirdParty()));
							   
								  table.addCell("Third Party Remark");
								  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getThirdPartyRemark()));
							  
								  table.addCell("Third PartyDoc");
								  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getThirdPartyDoc()));
							        
								
						
						
					
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
						      
						      String migrationLic=licenseDetails.getLandSchedule().getMigrationLic();
						      table.addCell("MigrationLic");
							  table.addCell(migrationLic!=null?(migrationLic.equals("N")?"No":"Yes"):replaceNullWithNA(migrationLic));
						      
							  table.addCell("AreaUnder Migration");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getAreaUnderMigration()));
						   
							  table.addCell("PurposeParentLic");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getPurposeParentLic()));
						  
							  table.addCell("Lic No");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getLicNo()));
						 
							  table.addCell("Area of ParentLic");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getAreaofParentLic()));
						
							  table.addCell("Validity Of ParentLic");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getValidityOfParentLic()));
						        
							
							  table.addCell("Renewal Fee");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getRenewalFee()));
						  
							  table.addCell("Freshly Applied");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getFreshlyApplied()));
						 	
							  table.addCell("Approved LayoutPlan");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getApprovedLayoutPlan()));
							  table.addCell("UploadPreviously LayoutPlan");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getUploadPreviouslyLayoutPlan()));
						
						
						
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
								
								  table.addCell(replaceNullWithNA("LandScheduleDetails");
								  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getLandScheduleDetails()));
							      
							}
							else {
								
								 table.addCell(replaceNullWithNA("LandScheduleDetails");
								  table.addCell(replaceNullWithNA("NULL");
								
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
						
						  table.addCell(replaceNullWithNA(g.getPreviousLicensenumber()));
						  
						 
						  table.addCell(replaceNullWithNA(g.getAreaOfParentLicence()));
						  
						
						  table.addCell(replaceNullWithNA(g.getPurposeOfParentLicence()));
						  
						  table.addCell(replaceNullWithNA(g.getValidity()));
						  
						  table.addCell(replaceNullWithNA(g.getDate()));
						  
						  table.addCell(replaceNullWithNA(g.getAreaAppliedmigration()));
						  
						  table.addCell(replaceNullWithNA(g.getKhasraNumber()));
						  
						  table.addCell(replaceNullWithNA(g.getArea()));
						  
						  table.addCell(replaceNullWithNA(g.getBalanceOfParentLicence()));
						  
					
						
		                  
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
					
							  table.addCell("Encumburance");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getEncumburance()));
						   
							  table.addCell("Encumburance Other");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getEncumburanceOther()));
						    
							  table.addCell("Encumburance Doc");
							  table.addCell("ATTACHED");
						     
							  table.addCell("Litigation");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getLitigation()));
						    
							  table.addCell("Litigation Remark");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getLitigationRemark()));
						     
							  table.addCell("Litigation Doc");
							  table.addCell("ATTACHED");
						    
							  table.addCell("Court");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getCourt()));
						       
							  table.addCell("Courty CaseNo");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getCourtyCaseNo()));
							
							  table.addCell("Court Doc");
							  table.addCell("ATTACHED");
							
							  table.addCell("Insolvency");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getInsolvency()));
							
							  table.addCell("Insolvency Remark");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getInsolvencyRemark()));
							
							  table.addCell("Insolvency Doc");
							  table.addCell("ATTACHED");
						        
						
						
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
					
							  table.addCell("Applied Land");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getAppliedLand()));
							
							  table.addCell("AppliedLandDoc");
							  table.addCell("ATTACHED");
							
							  table.addCell("Revenue Rasta");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getRevenueRasta()));
						
							  table.addCell("RevenueRasta Width");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getRevenueRastaWidth()));
							
							  table.addCell("WaterCourse");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getWaterCourse()));
						
							  table.addCell("WaterCourse Remark");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getWaterCourseRemark()));
						        
							  table.addCell("CompactBlock");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getCompactBlock()));
							
							  table.addCell("CompactBlock Remark");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getCompactBlockRemark()));
							
							  table.addCell("LandSandwiched");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getLandSandwiched()));
								
							  table.addCell("LandSandwiched Remark");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getLandSandwichedRemark()));
					
							  table.addCell("Acquistion");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getAcquistion()));
								
							  table.addCell("Acquistion Remark");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getAcquistionRemark()));
								
							  table.addCell("SectionFour");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getSectionFour()));
								
							  table.addCell("SectionSix");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getSectionSix()));
						 		
							  table.addCell("Order Upload");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getOrderUpload()));
							
							  table.addCell("Land Compensation");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getLandCompensation()));
						    
							  table.addCell("Release Status");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getReleaseStatus()));
							
							  table.addCell("Award Date");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getAwardDate()));
							
							  table.addCell("Release Date");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getReleaseDate()));
							  table.addCell("Site Detail");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getSiteDetail()));
							
							  table.addCell("Site Approachable");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getSiteApproachable()));
						
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
						
							  table.addCell("Vacant");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getVacant()));
						 
							  table.addCell("Vacant Remark");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getVacantRemark()));
						    
							  table.addCell("Construction");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getConstruction()));
						    
							  table.addCell("Type Of Construction");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getTypeOfConstruction()));
						  
							  table.addCell("Construction Remark");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getConstructionRemark()));
						   
							  table.addCell("Ht");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getHt()));
						  
							  table.addCell("Ht Remark");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getHtRemark()));
						     
							  table.addCell("Gas Remark");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getGasRemark()));
						     
							  table.addCell("Gas");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getGas()));
						    
							  table.addCell("Nallah");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getNallah()));
						   
							  table.addCell("Nallah Remark");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getNallahRemark()));
						   
							  table.addCell("Road");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getRoad()));
						    
							  table.addCell("Road Width");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getRoadWidth()));
						  
							  table.addCell("Road Remark");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getRoadRemark()));
						    
							  table.addCell("MarginalLand");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getMarginalLand()));
						   
							  table.addCell("MarginalLand Remark");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getMarginalLandRemark()));
						   
							  table.addCell("Utility Line");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getUtilityLine()));
						    
							  table.addCell("Utility Width");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getUtilityWidth()));
						 
							  table.addCell("Utility Remark");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getUtilityRemark()));
						  
							  table.addCell("MinimumApproachFour");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getMinimumApproachFour()));
						 
							  table.addCell("MinimumApproachEleven");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getMinimumApproachEleven()));
						  
							  table.addCell("Already Constructed Sectorad");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getAlreadyConstructedSectorad()));
						   
							  table.addCell("Joining OwnLand");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getJoiningOwnLand()));
						 	
							  table.addCell("Applicant Has Donated");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getApplicantHasDonated()));
						 
							  table.addCell("GiftDeedHibbanama");
							  table.addCell("ATTACHED");																	
						   
							  table.addCell("Adjoining OthersLand");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getAdjoiningOthersLand()));
						  
							  table.addCell("LandOwner Donated");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getLandOwnerDonated()));
						  
							  table.addCell("Constructed RowWidth");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getConstructedRowWidth()));
						  
							  table.addCell("IrrevocableConsent");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getIrrevocableConsent()));
						   
							  table.addCell("Upload RrrevocableConsent");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getUploadRrrevocableConsent()));
						    
							  table.addCell("ApproachFromProposedSector");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getApproachFromProposedSector()));
						  
							  table.addCell("Sector And Development Width");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getSectorAndDevelopmentWidth()));
						    
							  table.addCell("Whether Acquired");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getWhetherAcquired()));
						   
							  table.addCell("WhetherConstructed");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getWhetherConstructed()));
						    
							  table.addCell("Service Sector RoadAcquired");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getServiceSectorRoadAcquired()));
						   
							  table.addCell("Service Sector RoadConstructed");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getServiceSectorRoadConstructed()));
						   
							  table.addCell("Approach From InternalCirculation");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getApproachFromInternalCirculation()));
						    
							  table.addCell("Internal And SectoralWidth");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getInternalAndSectoralWidth()));
						    
							  table.addCell("Parent LicenceApproach");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getParentLicenceApproach()));
						   
							  table.addCell("Available Existing Approach");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getAvailableExistingApproach()));
						   
							  table.addCell("Available Existing ApproachDoc");
							  table.addCell("ATTACHED");
						    
							  table.addCell("Whether Acquired For InternalCirculation");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getWhetherAcquiredForInternalCirculation()));
						    
							  table.addCell("Whether Constructed For InternalCirculation");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getWhetherConstructedForInternalCirculation()));
						      
						
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
						
							  table.addCell("Mutation");
							  table.addCell("ATTACHED");
													
							  table.addCell("Jambandhi");
							  table.addCell("ATTACHED");
						   
							  table.addCell("Details Of Lease");
							  table.addCell("ATTACHED");
						
							  table.addCell("Add SalesDeed");
							  table.addCell("ATTACHED");
						
							  table.addCell("Revised LandSchedule");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getRevisedLandSchedule()));
						  
							  table.addCell("Copy of SpaBoard");
							  table.addCell("ATTACHED");
						   
							  table.addCell("Copy Of ShajraPlan");
							  table.addCell("ATTACHED");
						  
							  table.addCell("Proposed LayoutPlan");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getProposedLayoutPlan()));
						   
							  table.addCell("Revised LansSchedule");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getRevisedLansSchedule()));
						 
							  table.addCell("Area Of ParentLicence");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getAreaOfParentLicence()));
						  
							  table.addCell("Rera Registered");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getReraRegistered()));
						  
							  table.addCell("Rera DocUpload");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getReraDocUpload()));
						     
							  table.addCell("Rera Non RegistrationDoc");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getReraNonRegistrationDoc()));
						    
						
					/**	List value
					   if(licenseDetails.getLandSchedule().getLandScheduleDetails()!= null) {
							
							  table.addCell(replaceNullWithNA("LandScheduleDetails");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getLandScheduleDetails()));
						      
						}
						else {
							
							 table.addCell(replaceNullWithNA("LandScheduleDetails");
							  table.addCell(replaceNullWithNA("NULL");
							
						}*/
						
						
							  table.addCell("AnyOther");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getAnyOther()));
						     
							  table.addCell("AnyOtherRemark");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getAnyOtherRemark()));
						    
							  table.addCell("None");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getNone()));
						 	
							  table.addCell("NoneRemark");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getNoneRemark()));
						   
							  table.addCell("AdjoiningOwnLand");
							  table.addCell(replaceNullWithNA(licenseDetails.getLandSchedule().getAdjoiningOwnLand()));
						
						
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
						
						  table.addCell(replaceNullWithNA(g.getPreviousLicensenumber()));
						  
						 
						  table.addCell(replaceNullWithNA(g.getAreaOfParentLicence()));
						  
						
						  table.addCell(replaceNullWithNA(g.getPurposeOfParentLicence()));
						  
						  table.addCell(replaceNullWithNA(g.getValidity()));
						  
						  table.addCell(replaceNullWithNA(g.getDate()));
						  
						  table.addCell(replaceNullWithNA(g.getAreaAppliedmigration()));
						  
						  table.addCell(replaceNullWithNA(g.getKhasraNumber()));
						  
						  table.addCell(replaceNullWithNA(g.getArea()));
						  
						  table.addCell(replaceNullWithNA(g.getBalanceOfParentLicence()));
						  
					
						
		                  
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
								   table.addCell(replaceNullWithNA(h.getLatitude()));
								   
								   table.addCell("Longitude");
								   table.addCell(replaceNullWithNA(h.getLongitude()));
									  
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
						   table.addCell(replaceNullWithNA(licenseDetails.getDetailsofAppliedLand().getDetailsAppliedLandPlot().getTotalAreaScheme()));
	                	   
	                	   
		            
					
					if(licenseDetails.getDetailsofAppliedLand().getPurposeDetails()!=null && licenseDetails.getDetailsofAppliedLand().getPurposeDetails().size()>0) {
						

						
						List<PurposeDetails> f= licenseDetails.getDetailsofAppliedLand().getPurposeDetails();
						// String k= objectMapper.writeValueAsString(f);
					
						
						
						for(int j=0;j<f.size();j++) {
							
							PurposeDetails l1 = f.get(j);
							
							
							 table.addCell("ID");
							   table.addCell(replaceNullWithNA(l1.getId()));
							   
							  
							   table.addCell("Name");
							   table.addCell(replaceNullWithNA(l1.getName()));
							   
							   table.addCell("Code");
							   table.addCell(replaceNullWithNA(l1.getCode()));
							
							   
							   table.addCell("Area");
							   table.addCell(replaceNullWithNA(l1.getArea()));
							   
							   table.addCell("fars");
							   table.addCell(replaceNullWithNA(l1.getFar()));
							   
							   
							   table.addCell("MaxPercentage");
							   table.addCell(replaceNullWithNA(l1.getMaxPercentage()));
							   

							   table.addCell("MinPercentage");
							   table.addCell(replaceNullWithNA(l1.getMinPercentage()));
							   
							   table.addCell("MinPercentage");
							   table.addCell(replaceNullWithNA(l1.getMinPercentage()));
							   
							   
							   if(l1.getPurposeDetail()!=null && l1.getPurposeDetail().size()>0) {
								   
									
									List<PurposeDetails> l2 = l1.getPurposeDetail();
									
									for(int k1=0;k1<l2.size();k1++) {
										
										PurposeDetails l3 = l2.get(k1);
										
								  table.addCell("ID");
								   table.addCell(replaceNullWithNA(l3.getId()));
								   
								   
								   table.addCell("Name");
								   table.addCell(replaceNullWithNA(l3.getName()));
								   
								   table.addCell("Code");
								   table.addCell(replaceNullWithNA(l3.getCode()));
								
								   
								   table.addCell("Area");
								   table.addCell(replaceNullWithNA(l3.getArea()));
								   
								   table.addCell("fars");
								   table.addCell(replaceNullWithNA(l3.getFar()));
								   
								   
								   table.addCell("MaxPercentage");
								   table.addCell(replaceNullWithNA(l3.getMaxPercentage()));
								   

								   table.addCell("MinPercentage");
								   table.addCell(replaceNullWithNA(l3.getMinPercentage()));
								   
								   table.addCell("MinPercentage");
								   table.addCell(replaceNullWithNA(l3.getMinPercentage()));
								   
								   if(l3.getPurposeDetail()!=null && l3.getPurposeDetail().size()>0) {
									   
									  List<PurposeDetails> l4 = l3.getPurposeDetail();
									   
									   for(int k2=0;k2<l4.size();k2++) {
										   PurposeDetails l5= l4.get(k2);
										   
										   
										  
										   table.addCell("ID");
										   table.addCell(replaceNullWithNA(l5.getId()));
										   
										   
										   table.addCell("Name");
										   table.addCell(replaceNullWithNA(l5.getName()));
										   
										   table.addCell("Code");
										   table.addCell(replaceNullWithNA(l5.getCode()));
										
										   
										   table.addCell("Area");
										   table.addCell(replaceNullWithNA(l5.getArea()));
										   
										   table.addCell("fars");
										   table.addCell(replaceNullWithNA(l5.getFar()));
										   
										   
										   table.addCell("MaxPercentage");
										   table.addCell(replaceNullWithNA(l5.getMaxPercentage()));
										   

										   table.addCell("MinPercentage");
										   table.addCell(replaceNullWithNA(l5.getMinPercentage()));
										   
										   table.addCell("MinPercentage");
										   table.addCell(replaceNullWithNA(l5.getMinPercentage()));
										   
										   
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
							
	              		 table.addCell("Layout PlanDxf");
						   table.addCell("ATTACHED");
							
	              		 table.addCell("Undertaking");
						   table.addCell("ATTACHED");
							
	              		 table.addCell("DevelopmentPlan");
						   table.addCell("ATTACHED");
						
	              		 table.addCell("SectoralPlan");
						   table.addCell("ATTACHED");
						
	              		 table.addCell("Explanatory Note");
						   table.addCell("ATTACHED");
						
	              		 table.addCell("GuideMap");
						   table.addCell("ATTACHED");
						
	              		 table.addCell("Idemnity BondDoc");
						 table.addCell("ATTACHED");
						 
                	   
	              	   
	              	 doc.add(table);
	              	   
                   }
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
						   table.addCell(replaceNullWithNA(y.getTotalArea()));
						   
						   table.addCell("Purpose");
						   table.addCell(replaceNullWithNA(y.getPurpose()));

						   table.addCell("Potential");
						   table.addCell(replaceNullWithNA(y.getPotential()));
						  
						   table.addCell("DevelopmentPlan");
						   table.addCell(replaceNullWithNA(y.getDevelopmentPlan().getLabel()));
						  
						   
						   table.addCell("LicNumber");
						   table.addCell(replaceNullWithNA(y.getLicNumber()));
						  
						   
						   table.addCell("Amount");
						   table.addCell(replaceNullWithNA(y.getAmount()));
						  
						   
						   table.addCell("Amount Adjusted");
						   table.addCell(replaceNullWithNA(y.getAmountAdjusted()));
						   
						   table.addCell("Amount Payable");
						   table.addCell(replaceNullWithNA(y.getAmountPayable()));
						   
						   table.addCell("ScrutinyFee");
						   table.addCell(replaceNullWithNA(y.getScrutinyFee()));
						   
						   table.addCell("License Fee");
						   table.addCell(replaceNullWithNA(y.getLicenseFee()));
						   
						   table.addCell("Conversion Charges");
						   table.addCell(replaceNullWithNA(y.getConversionCharges()));
						   
						   table.addCell("Payable Now");
						   table.addCell(replaceNullWithNA(y.getPayableNow()));
						   
						   table.addCell("Remark");
						   table.addCell(replaceNullWithNA(y.getRemark()));
						   
						   table.addCell("Adjust Fee");
						   table.addCell(replaceNullWithNA(y.getAdjustFee()));
						   
						   table.addCell("Belongs Developer");
						   table.addCell(replaceNullWithNA(y.getBelongsDeveloper()));
						   
						   table.addCell("ConsentLetter");
						   table.addCell(replaceNullWithNA(y.getConsentLetter()));
						   
						   table.addCell("State Infrastructure Development Charges");
						   table.addCell(replaceNullWithNA(y.getStateInfrastructureDevelopmentCharges()));
						   
						   table.addCell("IDW");
						   table.addCell(replaceNullWithNA(y.getIDW()));
						   
						   table.addCell("EDC");
						   table.addCell(replaceNullWithNA(y.getEDC()));
						   
						   
						  
               	   
	              	   
	              	 doc.add(table);
	              	   
                  }
 
                }
  

					
					
			
			
			
		
		doc.close();
		writer.close();
		}
		
}}
	
	}
	
private String replaceNullWithNA(String param) {
	return param==null||param.equals("null")?("N/A"):(param);
}
}
