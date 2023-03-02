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

import org.egov.common.contract.request.RequestInfo;
import org.egov.tl.config.TLConfiguration;
import org.egov.tl.service.LicenseService;
import org.egov.tl.util.BPANotificationUtil;
import org.egov.tl.web.models.AddRemoveAuthoizedUsers;
import org.egov.tl.web.models.AppliedLandDetails;
import org.egov.tl.web.models.DirectorsInformation;
import org.egov.tl.web.models.DirectorsInformationMCA;
import org.egov.tl.web.models.LicenseDetails;
import org.egov.tl.web.models.LicenseServiceResponseInfo;
import org.egov.tl.web.models.ShareholdingPattens;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


@RestController
public class NewLicensePDF {
	
	
	private static Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD,BaseColor.BLACK);
	private static Font blackFont = new Font(Font.FontFamily.TIMES_ROMAN, 14, Font.BOLDITALIC, BaseColor.BLACK);
	private static Font blackFont1 = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD, BaseColor.BLACK);
	//private static String hindifont = "D:\\upyog code\\UPYOG1\\UPYOG\\municipal-services\\tl-services\\src\\main\\resources\\font\\FreeSans.ttf";
	private static String hindifont = "\\opt\\UPYOG\\municipal-services\\tl-services\\src\\main\\resources\\font\\FreeSans.ttf";

	@Autowired BPANotificationUtil bPANotificationUtil;
	@Autowired LicenseService licenseService;

	@Autowired public RestTemplate restTemplate;
	@Autowired TLConfiguration config;

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
				mimeType = "application/octet-stream";
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
		
		System.out.println(applicationNumber);
		boolean flag = true;
		System.out.println(requestInfo);
		LicenseServiceResponseInfo licenseServiceResponceInfo = licenseService.getNewServicesInfoById(applicationNumber, requestInfo);
		
		System.out.println(licenseServiceResponceInfo);

		Image img = Image.getInstance("govt.jpg");
		img.scaleAbsolute(200, 100);
		//img.scaleAbsoluteWidth(100);

		Document doc = new Document(PageSize.A4.rotate());
		PdfWriter writer = PdfWriter.getInstance(doc, new FileOutputStream(applicationNumber+".pdf"));
		doc.open();
		/**Font f1 = FontFactory.getFont(hindifont, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
		 Paragraph p4 = new Paragraph("\u0915\u093e\u0930 \u092a\u093e\u0930\u094d\u0915\u093f\u0902\u0917", f1);
	        doc.add(p4);
		Paragraph p5 = new Paragraph("\\u0915 \u0915 \\u093e \u093e \\0930 \u0930\n"
                + "\\u092a \u092a \\u093e \u093e \\u0930 \u0930 \\u094d \u094d"
                + "\\u0915 \u0915 \\u093f \\u093f \u093f \\u0902 \u0902"
                + "\\u0917 \u0917", f1);
        doc.add(p5);*/

		      
		
		 Paragraph p2 = new Paragraph("Department of Town & Country Planning, Haryana", new
				 Font(FontFamily.HELVETICA, 18, Font.BOLDITALIC, new BaseColor(0, 0, 255)) );
		 
		 final ZonedDateTime now = ZonedDateTime.now(ZoneId.of("Asia/Kolkata"));
		 Paragraph p3 = new Paragraph(now.toString());
		// Paragraph p3 = new Paragraph((new Date().toString()));
		Paragraph p1 = new Paragraph();
		p1.setFont(blackFont);
		p1.add("License Detail");
		img.setAlignment(Element.ALIGN_CENTER);
		p1.setAlignment(Element.ALIGN_LEFT);
		p2.setAlignment(Element.ALIGN_CENTER);
		p3.setAlignment(Element.ALIGN_RIGHT);
		doc.add(img);
		doc.add(p2);
		doc.add(p3);
		
	
		
		

	PdfPTable table = null;
	
	
    /** BaseFont unicode = BaseFont.createFont(hindifont, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
 Font font=new Font(unicode,12,Font.NORMAL,new BaseColor(50,205,50));
     //PdfPTable table = new PdfPTable(new float[] { 10, 60, 30 });
     table.setWidthPercentage(100);
     PdfPCell customerLblCell = new PdfPCell(new Phrase("CUSTOMERS"));
     PdfPCell balanceLblCell = new PdfPCell(new Phrase("\u0915\u093e\u0930\u092a\u093e\u0930\u094d\u0915\u093f\u0902\u0917", font));
     table.addCell(customerLblCell);
     table.addCell(balanceLblCell);
    // table.completeRow();
     table.setSpacingBefore(10);
     doc.add(table);
   
	
		  /** table = new PdfPTable(3);
			table.setSpacingBefore(10f);
			table.setSpacingAfter(10f);
			table.setWidthPercentage(80f);
			
			//new
		PdfPCell c1 = new PdfPCell(new Phrase("Application Number"));
	        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        table.addCell(c1);

	        c1 = new PdfPCell(new Phrase("Dairy Number"));
	        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        table.addCell(c1);
	        

	        c1 = new PdfPCell(new Phrase("Case Number"));
	        c1.setHorizontalAlignment(Element.ALIGN_CENTER);
	        table.addCell(c1);
	        
	        table.setHeaderRows(1);
	        table.addCell(licenseServiceResponceInfo.getApplicationNumber());
	        System.out.println("app"+licenseServiceResponceInfo.getApplicationNumber());
	        table.addCell(licenseServiceResponceInfo.getDairyNumber());
	        System.out.println("dairy"+licenseServiceResponceInfo.getDairyNumber());
	        table.addCell(licenseServiceResponceInfo.getCaseNumber());
	        System.out.println("case"+licenseServiceResponceInfo.getCaseNumber());*/

		
        
       


		//doc.add(table);
		if(licenseServiceResponceInfo.getNewServiceInfoData()!=null&&licenseServiceResponceInfo.getNewServiceInfoData().size()>0) {
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
					  table.addCell(licenseDetails.getApplicantInfo().getDevDetail().getAddInfo().getEmail());
					  
					  
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
						
					
						//Applied Land Details
						if(licenseDetails.getApplicantPurpose().getAppliedLandDetails()!=null && licenseDetails.getApplicantPurpose().getAppliedLandDetails().size()>0) {
							
							Paragraph pald = new Paragraph();
							pald.setFont(blackFont1);
							pald.add("Applied Land Details");
							doc.add(pald);
							
							table = new PdfPTable(26);
							table.setSpacingBefore(5f);
							table.setSpacingAfter(5f);
							table.setWidthPercentage(100f);
							
						for(int j=0;j<licenseDetails.getApplicantPurpose().getAppliedLandDetails().size();j++) {
							
							System.out.println(licenseDetails.getApplicantPurpose().getAppliedLandDetails().size());							
							AppliedLandDetails appliedLandDetails = licenseDetails.getApplicantPurpose().getAppliedLandDetails().get(j);
							
							String s1 = appliedLandDetails.getLandOwner();
							
							
														
							 BaseFont unicode = BaseFont.createFont(hindifont, BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
							 Font font=new Font(unicode,12,Font.NORMAL,new BaseColor(51,0,255));
							 Paragraph lo = new Paragraph();
							 lo.setFont(blackFont);
							 lo.add("Name of Land Owner:");
							 doc.add(lo);
							 Paragraph p6 = new Paragraph(s1, font);
							 System.out.println(s1);
							 
						     doc.add(p6);
							
					        PdfPCell c2 = new PdfPCell(new Phrase("Tehsil"));
					        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
					        table.addCell(c2);
					        
					        c2 = new PdfPCell(new Phrase("Revenue Estate"));
					        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
					        table.addCell(c2);
					        
					        c2 = new PdfPCell(new Phrase("Mustil"));
					        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
					        table.addCell(c2);
					        
					        c2 = new PdfPCell(new Phrase("Consolidation Type"));
					        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
					        table.addCell(c2);
					        
					        c2 = new PdfPCell(new Phrase("Sarsai"));
					        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
					        table.addCell(c2);
					        
					        c2 = new PdfPCell(new Phrase("Kanal"));
					        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
					        table.addCell(c2);
					        
					        c2 = new PdfPCell(new Phrase("Marla"));
					        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
					        table.addCell(c2);
					        
					        c2 = new PdfPCell(new Phrase("Hadbast No"));
					        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
					        table.addCell(c2);
					        
					        c2 = new PdfPCell(new Phrase("Bigha"));
					        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
					        table.addCell(c2);
					        
					        c2 = new PdfPCell(new Phrase("Biswansi"));
					        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
					        table.addCell(c2);
					        
					        c2 = new PdfPCell(new Phrase("Biswa"));
					        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
					        table.addCell(c2);
					        
					       /** c2 = new PdfPCell(new Phrase("LandOwner"));
					        PdfPCell  c3 = new PdfPCell(new Phrase(s1, font));
					        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
					        table.addCell(c2);*/
					        
					        c2 = new PdfPCell(new Phrase("Collaboration"));
					        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
					        table.addCell(c2);
					        
					        c2 = new PdfPCell(new Phrase("Developer Company"));
					        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
					        table.addCell(c2);
					        
					        c2 = new PdfPCell(new Phrase("AgreementValid From"));
					        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
					        table.addCell(c2);
					        
					        c2 = new PdfPCell(new Phrase("Validity Date"));
					        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
					        table.addCell(c2);
					        
					        c2 = new PdfPCell(new Phrase("Agreement Irrevocialble"));
					        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
					        table.addCell(c2);
					        
					        c2 = new PdfPCell(new Phrase("AuthSignature"));
					        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
					        table.addCell(c2);
					        
					        c2 = new PdfPCell(new Phrase("Name AuthSign"));
					        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
					        table.addCell(c2);
					       
					        c2 = new PdfPCell(new Phrase("Registering Authority"));
					        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
					        table.addCell(c2);
					        
					        c2 = new PdfPCell(new Phrase("Registering AuthorityDoc"));
					        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
					        table.addCell(c2);
					        
					        c2 = new PdfPCell(new Phrase("Khewats"));
					        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
					        table.addCell(c2);
					        
					        c2 = new PdfPCell(new Phrase("Consolidated Total"));
					        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
					        table.addCell(c2);
					        
					        c2 = new PdfPCell(new Phrase("NonConsolidated Total"));
					        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
					        table.addCell(c2);
					        
					        c2 = new PdfPCell(new Phrase("Edit Khewats"));
					        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
					        table.addCell(c2);
					        
					        c2 = new PdfPCell(new Phrase("Edit RectangleNo"));
					        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
					        table.addCell(c2);
					        
					        c2 = new PdfPCell(new Phrase("TypeLand"));
					        c2.setHorizontalAlignment(Element.ALIGN_CENTER);
					        table.addCell(c2);








					       

					       
					        
					        table.setHeaderRows(1);
					        table.addCell(appliedLandDetails.getTehsil());
					        table.addCell(appliedLandDetails.getRevenueEstate());
					        table.addCell(appliedLandDetails.getMustil());
					        table.addCell(licenseDetails.getApplicantPurpose().getAppliedLandDetails().get(j).getConsolidationType());
					        table.addCell(licenseDetails.getApplicantPurpose().getAppliedLandDetails().get(j).getSarsai());
					        table.addCell(licenseDetails.getApplicantPurpose().getAppliedLandDetails().get(j).getKanal());
					        table.addCell(licenseDetails.getApplicantPurpose().getAppliedLandDetails().get(j).getMarla());
					        table.addCell(licenseDetails.getApplicantPurpose().getAppliedLandDetails().get(j).getHadbastNo());
					        System.out.println(appliedLandDetails.getHadbastNo());	
						    table.addCell(licenseDetails.getApplicantPurpose().getAppliedLandDetails().get(j).getBigha());
					        table.addCell(licenseDetails.getApplicantPurpose().getAppliedLandDetails().get(j).getBiswansi());
					        table.addCell(licenseDetails.getApplicantPurpose().getAppliedLandDetails().get(j).getBiswa());
					        //table.addCell(c3);
					        table.addCell(licenseDetails.getApplicantPurpose().getAppliedLandDetails().get(j).getCollaboration());
					        table.addCell(licenseDetails.getApplicantPurpose().getAppliedLandDetails().get(j).getDeveloperCompany());
					        table.addCell(licenseDetails.getApplicantPurpose().getAppliedLandDetails().get(j).getAgreementValidFrom());
					        table.addCell(licenseDetails.getApplicantPurpose().getAppliedLandDetails().get(j).getAgreementValidTo());
					        table.addCell(appliedLandDetails.getAgreementIrrevocialble());
					        table.addCell(appliedLandDetails.getAuthSignature());
					        table.addCell(appliedLandDetails.getNameAuthSign());
					        table.addCell(appliedLandDetails.getRegisteringAuthority());
					        table.addCell(appliedLandDetails.getRegisteringAuthorityDoc());
					        table.addCell(appliedLandDetails.getKhewats());
					        table.addCell(appliedLandDetails.getConsolidatedTotal());
					        table.addCell(appliedLandDetails.getNonConsolidatedTotal());
					        table.addCell(appliedLandDetails.getEditKhewats());
					        table.addCell(appliedLandDetails.getEditRectangleNo());
					        table.addCell(appliedLandDetails.getTypeLand());
					        
					        
					        
						 
					        doc.add(table);
							 System.out.println(appliedLandDetails.getHadbastNo());	
							   
							
					
						}
						
						
						}
				
				
				
					if(licenseDetails.getLandSchedule()!= null) {
						Paragraph pls = new Paragraph();
						pls.setFont(blackFont1);
						pls.add("Land Schedule");
						doc.add(pls);
						
						table = new PdfPTable(2);
						table.setSpacingBefore(10f);
						table.setSpacingAfter(10f);
						table.setWidthPercentage(100f);
						
						if(licenseDetails.getLandSchedule().getLicenseApplied()!= null) {
							
							  table.addCell("License Applied");
							  table.addCell(licenseDetails.getLandSchedule().getLicenseApplied());
						        
							
						}
						
						if(licenseDetails.getLandSchedule().getLicenseNumber()!= null) {
							
							  table.addCell("License Number");
							  table.addCell(licenseDetails.getLandSchedule().getLicenseNumber());
						        
							
						}
						
						if(licenseDetails.getLandSchedule().getPotential()!= null) {
							
							  table.addCell("Potential");
							  table.addCell(licenseDetails.getLandSchedule().getPotential());
						        
							
						}
						
						if(licenseDetails.getLandSchedule().getSiteLoc()!= null) {
							
							  table.addCell("SiteLoc");
							  table.addCell(licenseDetails.getLandSchedule().getSiteLoc());
						        
							
						}

						
						if(licenseDetails.getLandSchedule().getApproachType()!= null) {
							
							  table.addCell("Approach Type");
							  table.addCell(licenseDetails.getLandSchedule().getApproachType());
						        
							
						}

						
						if(licenseDetails.getLandSchedule().getApproachRoadWidth()!= null) {
							
							  table.addCell("Approach RoadWidth");
							  table.addCell(licenseDetails.getLandSchedule().getApproachRoadWidth());
						        
							
						}
						
						if(licenseDetails.getLandSchedule().getSpecify()!= null) {
							
							  table.addCell("Specify");
							  table.addCell(licenseDetails.getLandSchedule().getSpecify());
						        
							
						}

						if(licenseDetails.getLandSchedule().getTypeLand()!= null) {
							
							  table.addCell("TypeLand");
							  table.addCell(licenseDetails.getLandSchedule().getTypeLand());
						        
							
						}

						if(licenseDetails.getLandSchedule().getThirdParty()!= null) {
							
							  table.addCell("Third Party");
							  table.addCell(licenseDetails.getLandSchedule().getThirdParty());
						        
							
						}

						if(licenseDetails.getLandSchedule().getThirdPartyRemark()!= null) {
							
							  table.addCell("Third Party Remark");
							  table.addCell(licenseDetails.getLandSchedule().getThirdPartyRemark());
						        
							
						}

						if(licenseDetails.getLandSchedule().getThirdPartyDoc()!= null) {
							
							  table.addCell("ThirdPartyDoc");
							  table.addCell(licenseDetails.getLandSchedule().getThirdPartyDoc());
						        
							
						}
						
						if(licenseDetails.getLandSchedule().getMigrationLic()!= null) {
							
							  table.addCell("MigrationLic");
							  table.addCell(licenseDetails.getLandSchedule().getMigrationLic());
						        
							
						}
						
						if(licenseDetails.getLandSchedule().getAreaUnderMigration()!= null) {
							
							  table.addCell("AreaUnder Migration");
							  table.addCell(licenseDetails.getLandSchedule().getAreaUnderMigration());
						        
							
						}

						if(licenseDetails.getLandSchedule().getPurposeParentLic()!= null) {
							
							  table.addCell("PurposeParentLic");
							  table.addCell(licenseDetails.getLandSchedule().getPurposeParentLic());
						        
							
						}

						if(licenseDetails.getLandSchedule().getLicNo()!= null) {
							
							  table.addCell("LicNo");
							  table.addCell(licenseDetails.getLandSchedule().getLicNo());
						        
							
						}

						if(licenseDetails.getLandSchedule().getAreaofParentLic()!= null) {
							
							  table.addCell("AreaofParentLic");
							  table.addCell(licenseDetails.getLandSchedule().getAreaofParentLic());
						        
							
						}
						
						if(licenseDetails.getLandSchedule().getValidityOfParentLic()!= null) {
							
							  table.addCell("ValidityOfParentLic");
							  table.addCell(licenseDetails.getLandSchedule().getValidityOfParentLic());
						        
							
						}
						if(licenseDetails.getLandSchedule().getRenewalFee()!= null) {
							
							  table.addCell("RenewalFee");
							  table.addCell(licenseDetails.getLandSchedule().getRenewalFee());
						        
							
						}
						if(licenseDetails.getLandSchedule().getApprovedLayoutPlan()!= null) {
							
							  table.addCell("Approved LayoutPlan");
							  table.addCell(licenseDetails.getLandSchedule().getApprovedLayoutPlan());
						        
							
						}
						if(licenseDetails.getLandSchedule().getUploadPreviouslyLayoutPlan()!= null) {
							
							  table.addCell("UploadPreviously LayoutPlan");
							  table.addCell(licenseDetails.getLandSchedule().getUploadPreviouslyLayoutPlan());
						        
							
						}
						if(licenseDetails.getLandSchedule().getEncumburance()!= null) {
							
							  table.addCell("Encumburance");
							  table.addCell(licenseDetails.getLandSchedule().getEncumburance());
						        
							
						}
						
						if(licenseDetails.getLandSchedule().getEncumburanceOther()!= null) {
							
							  table.addCell("EncumburanceOther");
							  table.addCell(licenseDetails.getLandSchedule().getEncumburanceOther());
						        
							
						}
						if(licenseDetails.getLandSchedule().getLitigation()!= null) {
							
							  table.addCell("Litigation");
							  table.addCell(licenseDetails.getLandSchedule().getLitigation());
						        
							
						}
						if(licenseDetails.getLandSchedule().getLitigationRemark()!= null) {
							
							  table.addCell("Litigation Remark");
							  table.addCell(licenseDetails.getLandSchedule().getLitigationRemark());
						        
							
						}
						if(licenseDetails.getLandSchedule().getLitigationDoc()!= null) {
							
							  table.addCell("Litigation Doc");
							  table.addCell(licenseDetails.getLandSchedule().getLitigationDoc());
						        
							
						}
						if(licenseDetails.getLandSchedule().getCourt()!= null) {
							
							  table.addCell("Court");
							  table.addCell(licenseDetails.getLandSchedule().getCourt());
						        
							
						}
						if(licenseDetails.getLandSchedule().getCourtyCaseNo()!= null) {
							
							  table.addCell("CourtyCaseNo");
							  table.addCell(licenseDetails.getLandSchedule().getCourtyCaseNo());
						        
							
						}
						if(licenseDetails.getLandSchedule().getCourtDoc()!= null) {
							
							  table.addCell("CourtDoc");
							  table.addCell(licenseDetails.getLandSchedule().getCourtDoc());
						        
							
						}
						if(licenseDetails.getLandSchedule().getInsolvency()!= null) {
							
							  table.addCell("Insolvency");
							  table.addCell(licenseDetails.getLandSchedule().getInsolvency());
						        
							
						}
						if(licenseDetails.getLandSchedule().getInsolvencyRemark()!= null) {
							
							  table.addCell("Insolvency Remark");
							  table.addCell(licenseDetails.getLandSchedule().getInsolvencyRemark());
						        
							
						}
						if(licenseDetails.getLandSchedule().getAppliedLand()!= null) {
							
							  table.addCell("Applied Land");
							  table.addCell(licenseDetails.getLandSchedule().getAppliedLand());
						        
							
						}
						if(licenseDetails.getLandSchedule().getAppliedLandDoc()!= null) {
							
							  table.addCell("AppliedLandDoc");
							  table.addCell(licenseDetails.getLandSchedule().getAppliedLandDoc());
						        
							
						}
						if(licenseDetails.getLandSchedule().getRevenueRasta()!= null) {
							
							  table.addCell("Revenue Rasta");
							  table.addCell(licenseDetails.getLandSchedule().getRevenueRasta());
						        
							
						}
						if(licenseDetails.getLandSchedule().getRevenueRastaWidth()!= null) {
							
							  table.addCell("RevenueRastaWidth");
							  table.addCell(licenseDetails.getLandSchedule().getRevenueRastaWidth());
						        
							
						}
						if(licenseDetails.getLandSchedule().getWaterCourse()!= null) {
							
							  table.addCell("WaterCourse");
							  table.addCell(licenseDetails.getLandSchedule().getWaterCourse());
						        
							
						}
						if(licenseDetails.getLandSchedule().getWaterCourseRemark()!= null) {
							
							  table.addCell("WaterCourse Remark");
							  table.addCell(licenseDetails.getLandSchedule().getWaterCourseRemark());
						        
							
						}
						if(licenseDetails.getLandSchedule().getCompactBlock()!= null) {
							
							  table.addCell("CompactBlock");
							  table.addCell(licenseDetails.getLandSchedule().getCompactBlock());
						        
							
						}
						if(licenseDetails.getLandSchedule().getCompactBlockRemark()!= null) {
							
							  table.addCell("CompactBlock Remark");
							  table.addCell(licenseDetails.getLandSchedule().getCompactBlockRemark());
						        
							
						}
						if(licenseDetails.getLandSchedule().getLandSandwiched()!= null) {
							
							  table.addCell("LandSandwiched");
							  table.addCell(licenseDetails.getLandSchedule().getLandSandwiched());
						        
							
						}
						if(licenseDetails.getLandSchedule().getLandSandwichedRemark()!= null) {
							
							  table.addCell("LandSandwiched Remark");
							  table.addCell(licenseDetails.getLandSchedule().getLandSandwichedRemark());
						        
							
						}
						if(licenseDetails.getLandSchedule().getAcquistion()!= null) {
							
							  table.addCell("Acquistion");
							  table.addCell(licenseDetails.getLandSchedule().getAcquistion());
						        
							
						}
						if(licenseDetails.getLandSchedule().getAcquistionRemark()!= null) {
							
							  table.addCell("Acquistion Remark");
							  table.addCell(licenseDetails.getLandSchedule().getAcquistionRemark());
						        
							
						}
						if(licenseDetails.getLandSchedule().getSectionFour()!= null) {
							
							  table.addCell("SectionFour");
							  table.addCell(licenseDetails.getLandSchedule().getSectionFour());
						        
							
						}
						if(licenseDetails.getLandSchedule().getSectionSix()!= null) {
							
							  table.addCell("SectionSix");
							  table.addCell(licenseDetails.getLandSchedule().getSectionSix());
						        
							
						}
						if(licenseDetails.getLandSchedule().getOrderUpload()!= null) {
							
							  table.addCell("Order Upload");
							  table.addCell(licenseDetails.getLandSchedule().getOrderUpload());
						        
							
						}
						if(licenseDetails.getLandSchedule().getLandCompensation()!= null) {
							
							  table.addCell("Land Compensation");
							  table.addCell(licenseDetails.getLandSchedule().getLandCompensation());
						        
							
						}
						if(licenseDetails.getLandSchedule().getReleaseStatus()!= null) {
							
							  table.addCell("Release Status");
							  table.addCell(licenseDetails.getLandSchedule().getReleaseStatus());
						        
							
						}
						if(licenseDetails.getLandSchedule().getAwardDate()!= null) {
							
							  table.addCell("Award Date");
							  table.addCell(licenseDetails.getLandSchedule().getAwardDate());
						        
							
						}
						if(licenseDetails.getLandSchedule().getReleaseDate()!= null) {
							
							  table.addCell("Release Date");
							  table.addCell(licenseDetails.getLandSchedule().getReleaseDate());
						        
							
						}
						if(licenseDetails.getLandSchedule().getSiteDetail()!= null) {
							
							  table.addCell("Site Detail");
							  table.addCell(licenseDetails.getLandSchedule().getSiteDetail());
						        
							
						}
						if(licenseDetails.getLandSchedule().getSiteApproachable()!= null) {
							
							  table.addCell("Site Approachable");
							  table.addCell(licenseDetails.getLandSchedule().getSiteApproachable());
						      
						}
						if(licenseDetails.getLandSchedule().getVacant()!= null) {
							
							  table.addCell("Vacant");
							  table.addCell(licenseDetails.getLandSchedule().getVacant());
						      
						}
						if(licenseDetails.getLandSchedule().getVacantRemark()!= null) {
							
							  table.addCell("Vacant Remark");
							  table.addCell(licenseDetails.getLandSchedule().getVacantRemark());
						      
						}
						if(licenseDetails.getLandSchedule().getConstruction()!= null) {
							
							  table.addCell("Construction");
							  table.addCell(licenseDetails.getLandSchedule().getConstruction());
						      
						}
						if(licenseDetails.getLandSchedule().getTypeOfConstruction()!= null) {
							
							  table.addCell("Type Of Construction");
							  table.addCell(licenseDetails.getLandSchedule().getTypeOfConstruction());
						      
						}
						if(licenseDetails.getLandSchedule().getConstructionRemark()!= null) {
							
							  table.addCell("Construction Remark");
							  table.addCell(licenseDetails.getLandSchedule().getConstructionRemark());
						      
						}
						if(licenseDetails.getLandSchedule().getHt()!= null) {
							
							  table.addCell("Ht");
							  table.addCell(licenseDetails.getLandSchedule().getHt());
						      
						}
						if(licenseDetails.getLandSchedule().getHtRemark()!= null) {
							
							  table.addCell("Ht Remark");
							  table.addCell(licenseDetails.getLandSchedule().getHtRemark());
						      
						}
						if(licenseDetails.getLandSchedule().getGasRemark()!= null) {
							
							  table.addCell("Gas Remark");
							  table.addCell(licenseDetails.getLandSchedule().getGasRemark());
						      
						}
						if(licenseDetails.getLandSchedule().getGas()!= null) {
							
							  table.addCell("Gas");
							  table.addCell(licenseDetails.getLandSchedule().getGas());
						      
						}
						if(licenseDetails.getLandSchedule().getNallah()!= null) {
							
							  table.addCell("Nallah");
							  table.addCell(licenseDetails.getLandSchedule().getNallah());
						      
						}
						if(licenseDetails.getLandSchedule().getNallahRemark()!= null) {
							
							  table.addCell("Nallah Remark");
							  table.addCell(licenseDetails.getLandSchedule().getNallahRemark());
						      
						}
						if(licenseDetails.getLandSchedule().getRoad()!= null) {
							
							  table.addCell("Road");
							  table.addCell(licenseDetails.getLandSchedule().getRoad());
						      
						}
						if(licenseDetails.getLandSchedule().getRoadWidth()!= null) {
							
							  table.addCell("Road Width");
							  table.addCell(licenseDetails.getLandSchedule().getRoadWidth());
						      
						}
						if(licenseDetails.getLandSchedule().getRoadRemark()!= null) {
							
							  table.addCell("Road Remark");
							  table.addCell(licenseDetails.getLandSchedule().getRoadRemark());
						      
						}
						if(licenseDetails.getLandSchedule().getMarginalLand()!= null) {
							
							  table.addCell("MarginalLand");
							  table.addCell(licenseDetails.getLandSchedule().getMarginalLand());
						      
						}
						if(licenseDetails.getLandSchedule().getMarginalLandRemark()!= null) {
							
							  table.addCell("MarginalLand Remark");
							  table.addCell(licenseDetails.getLandSchedule().getMarginalLandRemark());
						      
						}
						if(licenseDetails.getLandSchedule().getUtilityLine()!= null) {
							
							  table.addCell("Utility Line");
							  table.addCell(licenseDetails.getLandSchedule().getUtilityLine());
						      
						}
						if(licenseDetails.getLandSchedule().getUtilityWidth()!= null) {
							
							  table.addCell("Utility Width");
							  table.addCell(licenseDetails.getLandSchedule().getUtilityWidth());
						      
						}
						if(licenseDetails.getLandSchedule().getUtilityRemark()!= null) {
							
							  table.addCell("Utility Remark");
							  table.addCell(licenseDetails.getLandSchedule().getUtilityRemark());
						      
						}
						if(licenseDetails.getLandSchedule().getLandSchedule()!= null) {
							
							  table.addCell("LandSchedule");
							  table.addCell(licenseDetails.getLandSchedule().getLandSchedule());
						      
						}
						if(licenseDetails.getLandSchedule().getMutation()!= null) {
							
							  table.addCell("Mutation");
							  table.addCell(licenseDetails.getLandSchedule().getMutation());
						      
						}
						if(licenseDetails.getLandSchedule().getJambandhi()!= null) {
							
							  table.addCell("Jambandhi");
							  table.addCell(licenseDetails.getLandSchedule().getJambandhi());
						      
						}
						if(licenseDetails.getLandSchedule().getDetailsOfLease()!= null) {
							
							  table.addCell("Details Of Lease");
							  table.addCell(licenseDetails.getLandSchedule().getDetailsOfLease());
						      
						}
						if(licenseDetails.getLandSchedule().getAddSalesDeed()!= null) {
							
							  table.addCell("Add SalesDeed");
							  table.addCell(licenseDetails.getLandSchedule().getAddSalesDeed());
						      
						}
						if(licenseDetails.getLandSchedule().getRevisedLandSchedule()!= null) {
							
							  table.addCell("Revised LandSchedule");
							  table.addCell(licenseDetails.getLandSchedule().getRevisedLandSchedule());
						      
						}
						if(licenseDetails.getLandSchedule().getCopyofSpaBoard()!= null) {
							
							  table.addCell("Copy of SpaBoard");
							  table.addCell(licenseDetails.getLandSchedule().getCopyofSpaBoard());
						      
						}
						if(licenseDetails.getLandSchedule().getCopyOfShajraPlan()!= null) {
							
							  table.addCell("Copy Of ShajraPlan");
							  table.addCell(licenseDetails.getLandSchedule().getCopyOfShajraPlan());
						      
						}
						if(licenseDetails.getLandSchedule().getProposedLayoutPlan()!= null) {
							
							  table.addCell("Proposed LayoutPlan");
							  table.addCell(licenseDetails.getLandSchedule().getProposedLayoutPlan());
						      
						}
						if(licenseDetails.getLandSchedule().getRevisedLansSchedule()!= null) {
							
							  table.addCell("Revised LansSchedule");
							  table.addCell(licenseDetails.getLandSchedule().getRevisedLansSchedule());
						      
						}
						if(licenseDetails.getLandSchedule().getAreaOfParentLicence()!= null) {
							
							  table.addCell("Area Of ParentLicence");
							  table.addCell(licenseDetails.getLandSchedule().getAreaOfParentLicence());
						      
						}

						doc.add(table);
						
					}
			
			
			}
		
		doc.close();
		writer.close();
		}
		
}
	
	}
	

}
