package org.egov.tl.service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import org.egov.common.contract.request.RequestInfo;
import org.egov.tl.repository.ServiceRequestRepository;
import org.egov.tl.util.ConvertUtil;
import org.egov.tl.web.models.ApplicantInfo;
import org.egov.tl.web.models.AppliedLandDetails;
import org.egov.tl.web.models.CalculatorRequest;
import org.egov.tl.web.models.DetailsAppliedLandPlot;
import org.egov.tl.web.models.DetailsofAppliedLand;
import org.egov.tl.web.models.FeesAndCharges;
import org.egov.tl.web.models.LicenseDetails;
import org.egov.tl.web.models.LicenseServiceResponseInfo;
import org.egov.tl.web.models.RequestLOIReport;
import org.egov.tl.web.models.TradeLicense;
import org.egov.tl.web.models.TradeLicenseDetail;
import org.egov.tl.web.models.TradeLicenseSearchCriteria;
import org.egov.tl.web.models.User;
import org.egov.tl.web.models.UserResponse;
import org.egov.tl.web.models.UserSearchCriteria;
import org.egov.tl.web.models.calculation.CalculationRes;
import org.egov.tl.web.models.calculation.CalulationCriteria;
import org.egov.tl.web.models.calculation.FeeAndBillingSlabIds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.itextpdf.text.BaseColor;
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
import com.itextpdf.text.RomanList;
import com.itextpdf.text.pdf.PdfDiv;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.PdfDiv.FloatType;
import com.itextpdf.text.pdf.draw.LineSeparator;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class LoiReportService {

	@Value("${egov.user.host}")
	private String userHost;
	@Value("${egov.user.search.path}")
	private String userSearchPath;

	@Value("${egov.tl.calculator.host}")
	private String guranteeHost;
	@Value("${egov.tl.calculator.calculate.endpoint}")
	private String calculatorEndPoint;
	
	@Value("${egov.loireport}")
	private String loireportPath;
	@Value("${egov.timeZoneName}")
	private String timeZoneName;
	
	@Autowired
	private ServiceRequestRepository serviceRequestRepository;

	private static Font catFont = new Font(Font.FontFamily.TIMES_ROMAN, 18, Font.BOLD);
	private static Font smallBold = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
	private static Font normal = new Font(Font.FontFamily.TIMES_ROMAN, 12);
	private static Font small = new Font(Font.FontFamily.TIMES_ROMAN, 9);
	private static Font normalBold = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.BOLD);
	private static Font normalItalic = new Font(Font.FontFamily.TIMES_ROMAN, 12, Font.ITALIC);
	private static final DecimalFormat decfor = new DecimalFormat("0.000");

	@Autowired
	private Environment env;
	@Autowired
	private LicenseService licenseService;
	@Autowired
	private TradeLicenseService tradeLicenseService;

	@Autowired
	private ObjectMapper mapper;

	@Autowired
	private RestTemplate restTemplate;

	private String address = "N/A";
	private String totalArea = "0.0";
	private String applicationDate = null;
	private String currentDate = null;
	private String memoNumber = null;
	private String loiNumber = "N/A";
	
	String khasraNo = "N/A";
	private LicenseDetails licenseDetails = null;

	private String licenseFees = "0";
	private String licenseFeesInWord = "N/A";

	private String scrutinyFee = "0";
	private String scrutinyFeeInWord = "N/A";

	private String conversionCharges = "0";
	private String conversionChargesInWord = "N/A";

	private String stateInfrastructureDevelopmentCharges = "0";
	private String stateInfrastructureDevelopmentChargesInWord = "N/A";

	private String collaborationCompanyName = "N/A";

	private String dtcpUserName = "";
	private String hqUserName = "";;
	private String disticName = "";

	private String comericalComponent = "0.0";
	private String plottedComponent = "0.0";
	private String farAmount = "0.0";

	Double zoneWiseEdcAmount = 0.0;
	Double amountIDW=0.0;
	Double amountEDC=0.0;
	Double communityFacultyCost=0.0;
	
	
	

	/****************************************
	 * 1: LOI Purpose : Group Housing start *
	 ***************************************/

	private void getLoiReportGroupHousing(String applicationNumber, String userId, String hqUserId,
			RequestLOIReport requestLOIReport) {

		LicenseServiceResponseInfo licenseServiceResponceInfo = checkApplicationIsValid(requestLOIReport,
				applicationNumber, userId, hqUserId);
		if (licenseServiceResponceInfo == null) {
			return;
		} else {

			try {
				String myFile = loireportPath;
				Document doc = new Document(PageSize.A4);
				PdfWriter writer = PdfWriter.getInstance(doc,
						new FileOutputStream(myFile + "loi-report-" + applicationNumber + ".pdf"));
				doc.open();
				addTitlePage(doc, 1);
				memoNumber = "LC- " + (licenseServiceResponceInfo.getCaseNumber() != null
						? licenseServiceResponceInfo.getCaseNumber().replaceAll("LC-", "").split("~")[0]
						: "N/A") + "-JE (VA)- " + (applicationDate.split("\\s+")[0].split("\\.")[2]);
				int leftMarg = 70;
				Paragraph preface1 = new Paragraph();
				preface1.add(new Paragraph("To", smallBold));
				doc.add(preface1);
				addEmptyLine(preface1, 2);
				Paragraph preface2 = new Paragraph();
				preface2.setIndentationLeft(leftMarg);
				try {
					ApplicantInfo applicationInfo = licenseDetails.getApplicantInfo();
					preface2.add(new Paragraph(applicationInfo.getDevDetail().getAddInfo().getCompanyName()));
					preface2.add(new Paragraph(applicationInfo.getDevDetail().getAddInfo().getRegisteredAddress()));
//			preface2.add(new Paragraph((applicationInfo.getState()==null?"N/A":applicationInfo.getState())+"-"+applicationInfo.getDevDetail().getLicenceDetails().getPincode()));
				} catch (Exception e) {
					preface2.add(new Paragraph("_________  Pvt. Ltd.,"));
					preface2.add(new Paragraph("--, -----------, ----------,"));
					preface2.add(new Paragraph("---------------."));
					e.printStackTrace();
				}
				doc.add(preface2);
				
				Paragraph loiNumberParagraph = new Paragraph(
						"LOI Number : " + loiNumber);
				loiNumberParagraph.setIndentationLeft(leftMarg);
				loiNumberParagraph.setSpacingBefore(15f);
				doc.add(loiNumberParagraph);
				
				Paragraph paragraph = new Paragraph(
						"Memo No. " + memoNumber + "/                 Dated: " + currentDate.split("\\s+")[0]);
				paragraph.setIndentationLeft(leftMarg);
				paragraph.setSpacingBefore(15f);
				doc.add(paragraph);
				addEmptyLine(paragraph, 1);

				Paragraph preface12 = new Paragraph();
				preface12.add(new Paragraph(
						"Subject:         Request for grant of licence for setting up of Group Housing Colony over ",
						smallBold));
				preface12.setSpacingBefore(15f);
				doc.add(preface12);
				Paragraph preface13 = new Paragraph();
				preface13.add(new Paragraph(
						"an area measuring  " + totalArea + " acres in " + address + "-Issuance of LOI", smallBold));
				preface13.setIndentationLeft(leftMarg);
				doc.add(preface13);

				Paragraph preface14 = new Paragraph();
				preface14.add(new Paragraph("Please refer your application dated " + currentDate.split("\\s+")[0]
						+ " on the matter as subject cited above.", normal));
				preface14.setIndentationLeft(leftMarg);
				preface14.setSpacingBefore(20f);
				preface14.setSpacingAfter(20f);
				doc.add(preface14);

				List subList2 = new RomanList();
				subList2.setPostSymbol(". ");
				subList2.setIndentationLeft(25f);
				List subList3 = new List(List.ORDERED, List.ALPHABETICAL); // new RomanList();//
				subList3.setLowercase(List.LOWERCASE);
				subList3.setPostSymbol(") ");

				subList3.setIndentationLeft(15f);

				List list = new List(List.ORDERED);
				List subList = new List(List.ORDERED);
				subList.setIndentationLeft(30f);
				String content = "`                Your request for the grant of licence under section 3 of the Haryana Development and Regulation of Urban Areas Act, 1975 and Rules, 1976 framed thereunder for the development of Group Housing Colony over an area measuring "
						+ totalArea + " acres in " + address
						+ " has been considered and it is proposed to grant a licence for setting up of aforesaid colony. You are therefore, called upon to fulfill the following requirements/pre-requisites laid down in Rule 11 of the Haryana Development and Regulation of Urban Areas Rules, 1976 within a period of 60 days from the date of issue of this letter, failing which request for grant of licence shall be refused.";
				Paragraph para1 = new Paragraph("", normal);
				list.add(new ListItem(content, normal));
				list.add(new ListItem("To deposit an amount of " + licenseFees + "/-(" + licenseFeesInWord
						+ ") on account of balance licence fee and " + conversionCharges + "/-("
						+ conversionChargesInWord
						+ ") on account of conversion charges in favour of the Director, Town & Country Planning, Haryana through online mode",
						normal));
				list.add(new ListItem(
						"To submit BG amounting "+amountEDC*0.25+" /- lac against the amount of External Development Charges amounting "
								+ amountEDC + "/-.",
						normal));
				List subList01 = new List();
				subList01.setIndentationLeft(15f);
				subList01.add(new ListItem(new ListItem(
						"*It is made clear that rate of EDC have been calculated on the basis of EDC Indexation Mechanism Policy dated 11.02.2016, which stands approved by cabinet. If there will be any change and delay in the amendment in the Act/Rules w.r.t. the said rates, then differential amount from the original calculation will required to be deposited as per demand.",
						normal)));
				list.add(subList01);

				list.add(new ListItem(
						"To furnish bank guarantee amounting "+(amountIDW*0.25)+" /- lacs  against the total cost of Internal Development Works amounting "
								+ amountIDW + "/- lacs",
						normal));
				List subList02 = new List();
				subList02.setIndentationLeft(15f);
				subList02.add(new ListItem(new ListItem(
						"**It is made clear that bank guarantee of Internal Development Works has been worked out on the interim rates and you will have to submit the additional bank guarantee, if any required, at the time of approval of Service Plan/Estimate. With an increase in the cost of construction, you would be required to furnish an additional bank guarantee within 30 days on demand.",
						normal)));
				list.add(subList02);

				list.add(new ListItem(
						"That you shall execute two agreements i.e. LC-IV & Bilateral Agreement on Non-Judicial Stamp Paper of Rs. 100/-. Further, following additional clauses shall be added in LC-IV agreement as per Government instruction dated 14.08.2020:.",
						normal));

				subList2.add(new ListItem(new ListItem(
						"That the owner/developer shall integrate the bank account in which 70 percent allottee receipts are credited under Section-4(2)(I)(D) of the Real Estate Regulation and Development Act, 2016 with the online application/payment gateway of the Department, in such manner, so as to ensure that 10% of the total receipt from each payment made by an allottee is automatically deducted and gets credited to the EDC head in the State treasury.",
						normalItalic)));

				subList2.add(new ListItem(new ListItem(
						"That such 10% of the total receipt from each payment made by the allottee, which is received by the Department shall get automatically credited, on the date of receipt in the Government treasury against EDC dues.  ",
						normalItalic)));
				subList2.add(new ListItem(new ListItem(
						"That such 10% deduction shall continue to operate till the total EDC dues get recovered from the owner/developer.",
						normalItalic)));
				subList2.add(new ListItem(new ListItem(
						"The implementation of such mechanism shall, however, have no bearing on EDC instalment schedule conveyed to the owner/developer. The owner/developer shall continue to supplement such automatic EDC deductions with payments from its own funds to ensure that by the EDC instalments that are due for payment that paid as per the prescribed schedule. ",
						normalItalic)));
				list.add(subList2);

				list.add(new ListItem("To furnish an undertaking on non-judicial stamp paper to the following effect:-",
						normal));

				subList3.add(new ListItem(new ListItem(
						"That you shall pay the State Infrastructure Development Charges amounting to "
								+ stateInfrastructureDevelopmentCharges + "/-("
								+ stateInfrastructureDevelopmentChargesInWord
								+ "), in two equal instalments. First Instalment will be due within 60 days of grant of license and second Instalment within six months of grant of license failing which 18% PA interest will be liable for the delayed period",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That you shall pay proportionate EDC as per schedule prescribed by the Director.", normal)));
				subList3.add(new ListItem(new ListItem(
						"That you shall submit the additional bank guarantee, if any required at the time of approval of Service Plans/Estimate. With an increase in the cost of construction and increase in the number of facilities in Layout Plan, you will be required to furnish an additional bank guarantee within 30 days on demand. It is made clear that bank guarantee of Internal Development Works/EDC has been worked out on the interim rates.  ",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That you shall maintain and upkeep all roads open spaces, public parks and public health services for a period of five years from the date of issue to the completion certificate unless earlier relieved of this responsibility and thereupon to transfer all such roads/service roads, open spaces, public parks and public health services free of cost to the Government or the local authority, as the case may be, in accordable with the provisions of Section 3(3)(a)(iii) of the Haryana Development and Regulation of Urban Areas Act, 1975.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That you shall construct at your own cost, or get constructed by any other institution or individual at its cost, schools, hospitals, community centers and other community buildings on the lands set apart for this purpose, in a period as may be specified, and failing which action as per the Act/Rules shall be initiated. The land shall vest with the Government after such specified period, free of cost, in which case the Government shall be at liberty to transfer such-land to any person or institution including a local authority, for the said purposes, on such terms and conditions, as it may deem fit.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That you shall pay the proportionate cost of construction of such percentage of sites of such school, hospital, community centre and other community building and at such rates as specified by the Director.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That you shall arrange electric connection from HVPN/DHBVNL for electrification of your colony and shall install the electricity distribution infrastructure as per the peak load requirement of the colony for which you shall get the electrical (distribution) service plan / estimates approved from the agency responsible for installation of external electric services i.e. HVPN/DHBVNL Haryana and complete the same before obtaining completion certificate for the colony.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That you shall permit the Director or any other officer authorised by him to inspect the execution of the layout and the development works in the colony and to carry out all directions issued by him for ensuring due compliance of the execution of the layout and development works in accordance with the licence granted.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That you shall construct 24/30 m wide internal circulation road forming part of licenced area at your own costs and transfer the same free of cost to the Government.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That you shall construct and allot EWS category flats as per departmental policy dated 08.07.2013 and as amended from time to time.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That you shall submit NOC from the Ministry of Environment & Forest, Govt. of India with respect to their notification dated 14.09.2006 and clearance regarding PLPA, 1900 from competent authority before executing development works.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That you shall make arrangement for water supply, sewerage, drainage etc. to the satisfaction of DTCP till these services are made available from external infrastructure to be laid by HSVP.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That you shall convey the ‘Ultimate Power Load Requirement’ of the project to the concerned power utility, with a copy to the Director, within two months period from the date of grant of licence to enable provision of site in your land for Transformers/Switching Station/Electric Sub-Stations as per the norms prescribed by the power utility in the zoning plan of the project.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That you shall provide the rain water harvesting system as per Central Ground Water Authority Norms/Haryana Govt. notification as applicable.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That you shall aware that the development/construction cost of 24/30 m wide road/major internal road is not included in the EDC rates and you shall pay the proportionate cost for acquisition of land if any, alongwith the construction cost of 24/30 m wide road/major internal road as and when finalized and demanded by the Director Town & Country Planning, Haryana.  ",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That you shall provide the solar water heating system as provisions of HAREDA and shall be made operational where applicable before applying for an occupation certificate.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That you shall submit compliance of Rule 24, 26, 27 & 28 of Rules 1976 & Section 5 of Haryana Development and Regulation of Urban Areas Act, 1975, and shall inform account number and full particulars of the scheduled Bank wherein you have to deposit thirty percentum of the amount from the Flat/shop buyers for meeting the cost of Internal Development Works in the colony. ",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That you shall keep the pace of construction atleast in accordance with sale agreement executed with the buyers of the flats as and when scheme is launched.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That you shall pay the labour cess as per policy instructions issued by Haryana Government vide Memo No. Misc. 2057-5/25/2008/2TCP dated 25.02.2010.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That you shall not pre-launch/sale of flats before approval of the building plans.", normal)));
				subList3.add(new ListItem(new ListItem(
						"That you shall not use the ground water for the purpose of construction of building. The building plans shall be approved only after the source of water for construction purposal is explained to the satisfaction of HSVP Authority in terms of orders of the Hon'ble High Court dated 16.07.2012 in CWP’s no. 20032 of 2008, 13594 of 2009 and 807 of 2012.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That you shall obey all the directions/restrictions imposed by the Department from time to time in public interest.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That you shall specify the detail of calculations per Sqm/per sq ft, which is being demanded from the flat/shop owners on account of IDC/EDC, if being charged separately as per rates fixed by Govt. ",
						normal)));

				subList3.add(new ListItem(new ListItem(
						"That you shall not give any marketing and selling rights to any other company other than the collaborator company.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That you shall deposit thirty percentum of the amount realized, from time to time, by you, from the shop buyers within a period of 10 days of its realization in a separate account to be maintained in a scheduled Bank. This account shall only be utilized by you towards meeting the cost of internal development works in the colony.",
						normal)));

				list.add(subList3);
				list.add(new ListItem(
						"That you shall complete the demarcation at site within 7 days and will submit the demarcation Plan in the office of District Town Planner, Gurugram within 15 days of issuance of this letter.",
						normal));

				list.add(new ListItem("That you shall submit a certificate from the District Revenue Authority, "
						+ disticName
						+ " stating that there is no further sale of the land applied for license till date and applicant companies/individual are owners of the land.",
						normal));
//				if (requestLOIReport.getAddedContent() != null && !requestLOIReport.getAddedContent().isEmpty()) {
//					requestLOIReport.getAddedContent().stream().forEach(cd -> {
//						list.add(new ListItem(cd, normal));
//					});
//				}
				loiTableReportData(list, userId, applicationNumber, requestLOIReport);
				doc.add(list);
				addEmptyLine(para1, 1);
				String note = "Note:- That you will intimate your official “email ID” to the Department and correspondence done by Department on this ID shall be treated as official intimation & legally valid.";
				Paragraph para10 = new Paragraph(note, small);
				para10.setSpacingBefore(10f);
				doc.add(para10);
				doc.add(new Paragraph("DA/schedule of land. ", normal));
				addEmptyLine(para1, 1);
				Paragraph para12 = new Paragraph("Your filled data for following field(s) has not been approved:",
						normal);
				// You are requested to complete the following shortcomings immediately
				para12.setSpacingBefore(10f);
				Paragraph para13 = new Paragraph("".toUpperCase(), normal);
				para13.setSpacingBefore(10f);
//				doc.add(para12);
//				doc.add(para13);
//				loiTableReportData(doc, userId, applicationNumber, requestLOIReport);
				try {
					Paragraph preface0 = new Paragraph("(" + dtcpUserName + ", I.A.S)    ", smallBold);
					preface0.setIndentationLeft(10);
					preface0.setAlignment(Element.ALIGN_CENTER);
					preface0.setSpacingBefore(10f);
					Paragraph preface01 = new Paragraph("Director,", smallBold);
					preface01.setIndentationLeft(70);
					Paragraph preface02 = new Paragraph("Town & Country Planning", smallBold);
					preface02.setIndentationLeft(25);
					Paragraph preface3 = new Paragraph("Haryana Chandigarh", smallBold);
					preface3.setIndentationLeft(40);

					PdfDiv div = new PdfDiv();
					div.addElement(preface0);
					div.addElement(preface01);
					div.addElement(preface02);
					div.addElement(preface3);
					div.setPaddingLeft(320f);
					div.setFloatType(FloatType.RIGHT);
					doc.add(div);

					Paragraph preface4 = new Paragraph("Endst. " + memoNumber
							+ "/ 		                                    Dated: " + currentDate.split("\\s+")[0]);
					preface4.setAlignment(Element.ALIGN_LEFT);
					preface4.setSpacingBefore(10f);
					doc.add(preface4);
					Paragraph para102 = new Paragraph(
							"A copy is forwarded to the following for information and necessary action.", normal);
					para102.setSpacingBefore(10f);
					Paragraph para103 = new Paragraph("".toUpperCase(), normal);
					para103.setSpacingBefore(10f);
					doc.add(para102);
					doc.add(para103);

					List subList1 = new List(List.ORDERED);
					subList1.setIndentationLeft(30f);
					subList1.add(new ListItem("The Deputy Commissioner, Gurugram.", normal));
					subList1.add(new ListItem("The Additional Director, Urban Estate, Harysana, Sector-6 Panchkula.",
							normal));
					subList1.add(new ListItem("Senior Town Planner, Gurugram.", normal));
					subList1.add(new ListItem("District Town Planner, Gurugram.", normal));
					doc.add(subList1);

					Paragraph preface001 = new Paragraph("(" + hqUserName + ")", normal);
					preface001.setAlignment(Element.ALIGN_RIGHT);

					Paragraph preface102 = new Paragraph("District Town Planner (HQ)", normal);
					preface102.setAlignment(Element.ALIGN_RIGHT);

					Paragraph preface23 = new Paragraph("For: Director, Town & Country Planning", normal);
					preface23.setAlignment(Element.ALIGN_RIGHT);

					Paragraph preface34 = new Paragraph("Haryana, Chandigarh.", normal);
					preface34.setAlignment(Element.ALIGN_RIGHT);

					PdfDiv div2 = new PdfDiv();
					div2.addElement(preface001);
					div2.addElement(preface102);
					div2.addElement(preface23);
					div2.addElement(preface34);
					div2.setFloatType(FloatType.RIGHT);
					doc.add(div2);
				} catch (Exception e) {
					log.error("Exception : "+e.getMessage());
				}

				doc.close();
				writer.close();
			} catch (DocumentException e1) {
				log.error("DocumentException : "+e1.getMessage());
			} catch (Exception e) {
				log.error("Exception : "+e.getMessage());
				
			}
		}
	}

	/***************************************
	 * LOI Purpose : Group Housing end *
	 ***************************************/

	/****************************************************
	 * 2: LOI Purpose : Affordable Group Housing start *
	 ***************************************************/

	private void getLoiReportAffordableGroupHousing(String applicationNumber, String userId, String hqUserId,
			RequestLOIReport requestLOIReport) {

		LicenseServiceResponseInfo licenseServiceResponceInfo = checkApplicationIsValid(requestLOIReport,
				applicationNumber, userId, hqUserId);
		if (licenseServiceResponceInfo == null) {
			return;
		} else {
			try {
				String myFile = loireportPath;
				Document doc = new Document(PageSize.A4);
				PdfWriter writer = PdfWriter.getInstance(doc,
						new FileOutputStream(myFile + "loi-report-" + applicationNumber + ".pdf"));
				doc.open();
				addTitlePage(doc, 1);
				memoNumber = "LC- " + (licenseServiceResponceInfo.getCaseNumber() != null
						? licenseServiceResponceInfo.getCaseNumber().replaceAll("LC", "").split("-")[0]
						: "N/A") + "-JE (VA)- " + (applicationDate.split("\\s+")[0].split("\\.")[2]);

				int leftMarg = 70;
				Paragraph preface1 = new Paragraph();
				preface1.add(new Paragraph("To", smallBold));
				doc.add(preface1);
				addEmptyLine(preface1, 2);
				Paragraph preface2 = new Paragraph();
				preface2.setIndentationLeft(leftMarg);
				try {
					ApplicantInfo applicationInfo = licenseDetails.getApplicantInfo();
					preface2.add(new Paragraph(applicationInfo.getDevDetail().getAddInfo().getCompanyName()));
					preface2.add(new Paragraph(applicationInfo.getDevDetail().getAddInfo().getRegisteredAddress()));
//				preface2.add(new Paragraph((applicationInfo.getState() == null ? "N/A" : applicationInfo.getState())
//						+ "-" + applicationInfo.getDevDetail().getLicenceDetails().getPincode()));
				} catch (Exception e) {
					preface2.add(new Paragraph("_________  Pvt. Ltd.,"));
					preface2.add(new Paragraph("--, -----------, ----------,"));
					preface2.add(new Paragraph("---------------."));
					e.printStackTrace();
				}
				
				doc.add(preface2);
				
				Paragraph loiNumberParagraph = new Paragraph(
						"LOI Number : " + loiNumber);
				loiNumberParagraph.setIndentationLeft(leftMarg);
				loiNumberParagraph.setSpacingBefore(15f);
				doc.add(loiNumberParagraph);
				
				Paragraph paragraph = new Paragraph("Memo No. " + memoNumber
						+ "/ 		                                    Dated: " + currentDate.split("\\s+")[0]);
				paragraph.setIndentationLeft(leftMarg);
				paragraph.setSpacingBefore(15f);
				doc.add(paragraph);
				addEmptyLine(paragraph, 1);

				Paragraph preface12 = new Paragraph();
				preface12.add(new Paragraph(
						"Subject:         Request for grant of licence for setting up of an Affordable Group Housing Colony ",
						smallBold));
				preface12.setSpacingBefore(15f);
				doc.add(preface12);
				Paragraph preface13 = new Paragraph();
				preface13.add(new Paragraph("On the land measuring " + totalArea + " acres in the revenue estate of "
						+ address + "– Issuance of letter of intent.", smallBold));
				preface13.setIndentationLeft(leftMarg);
				doc.add(preface13);

				Paragraph preface14 = new Paragraph();
				preface14.add(new Paragraph("Please refer to your application dated " + applicationDate.split("\\s+")[0]
						+ " on the matter as subject cited above.", normal));
				preface14.setIndentationLeft(leftMarg);
				preface14.setSpacingBefore(20f);
				preface14.setSpacingAfter(20f);
				doc.add(preface14);

				List subList2 = new RomanList();
				subList2.setLowercase(List.UPPERCASE);
				subList2.setPostSymbol(". ");
				subList2.setIndentationLeft(25f);
				List subList3 = new RomanList();// new List(List.ORDERED, List.ALPHABETICAL);
				subList3.setLowercase(List.LOWERCASE);
				subList3.setPostSymbol(") ");
				subList3.setIndentationLeft(15f);

				List list = new List(List.ORDERED);
				List subList = new List(List.ORDERED);
				subList.setIndentationLeft(30f);
				String content = "`                Your request for grant of licence under section 3 of the Haryana Development and Regulation of Urban Areas Act, 1975 and the Haryana Development and Regulation of Urban Areas Rules, 1976 framed thereunder for the development of an Affordable Group Housing Colony on the land measuring "
						+ totalArea + " acres in the revenue estate of " + address
						+ " has been considered by the Department in accordance with the prevailing policy parameters and it is proposed to grant license subject to fulfilment of following requirements/pre-requisites as laid down in Rule 11 of the Haryana Development and Regulation of Urban Areas Rules, 1976 within a period of 60 days from the date of issuance of this letter:-";
				Paragraph para1 = new Paragraph("", normal);
				list.add(new ListItem(content, normal));
				list.add(new ListItem("To deposit an amount of " + conversionCharges + "/-(" + conversionChargesInWord
						+ ") on account of conversion charges in favour of the Director, Town & Country Planning, Haryana through online mode.",
						normal));
				list.add(new ListItem(
						"To submit BG amounting ₹ "+amountEDC*0.25+" lac against the amount of External Development Charges amounting "
								+ amountEDC + "/-.",
						normal));
				List subList01 = new List();
				subList01.setIndentationLeft(15f);
				subList01.add(new ListItem(new ListItem(
						"*It is made clear that rate of EDC have been calculated on the basis of EDC Indexation Mechanism Policy dated 11.02.2016, which stands approved by cabinet. If there will be any change and delay in the amendment in the Act/Rules w.r.t. the said rates, then differential amount from the original calculation will required to be deposited as per demand.",
						normal)));
				list.add(subList01);

				list.add(new ListItem(
						"To furnish bank guarantee amounting ₹ "+amountIDW*0.25+" lac against the total cost of Internal Development Works amounting "
								+ amountIDW + "/-",
						normal));
				List subList02 = new List();
				subList02.setIndentationLeft(15f);
				subList02.add(new ListItem(new ListItem(
						"**It is made clear that bank guarantee of Internal Development Works has been worked out on the interim rates and you will have to submit the additional bank guarantee, if any required, at the time of approval of Service Plan/Estimate. With an increase in the cost of construction, you would be required to furnish an additional bank guarantee within 30 days on demand.",
						normal)));
				list.add(subList02);

				list.add(new ListItem(
						"That you shall execute two agreements i.e. LC-IV & Bilateral Agreement on Non-Judicial Stamp Paper of Rs. 100/-. Further, following additional clauses shall be added in LC-IV agreement as per Government instruction dated 14.08.2020:.",
						normal));

				subList2.add(new ListItem(new ListItem(
						"That the owner/developer shall integrate the bank account in which 70 percent allottee receipts are credited under Section-4(2)(I)(D) of the Real Estate Regulation and Development Act, 2016 with the online application/payment gateway of the Department, in such manner, so as to ensure that 10% of the total receipt from each payment made by an allottee is automatically deducted and gets credited to the EDC head in the State treasury.",
						normalItalic)));
				subList2.add(new ListItem(new ListItem(
						"That such 10% of the total receipt from each payment made by the allottee, which is received by the Department shall get automatically credited, on the date of receipt in the Government treasury against EDC dues.  ",
						normal)));
				subList2.add(new ListItem(new ListItem(
						"That such 10% deduction shall continue to operate till the total EDC dues get recovered from the owner/developer.",
						normal)));
				subList2.add(new ListItem(new ListItem(
						"The implementation of such mechanism shall, however, have no bearing on EDC instalment schedule conveyed to the owner/developer. The owner/developer shall continue to supplement such automatic EDC deductions with payments from its own funds to ensure that by the EDC instalments that are due for payment that paid as per the prescribed schedule.",
						normal)));
				list.add(subList2);

				list.add(new ListItem("To furnish an undertaking on non-judicial stamp paper to the following effect:-",
						normal));

				subList3.add(new ListItem(
						"That area coming under the sector roads and restricted belt / green belt, if any, which forms part of licensed area and in lieu of which benefit to the extent permissible as per policy towards FAR is being granted, shall be transferred free of cost to the Govt",
						normal));
				subList3.add(new ListItem(
						"That you shall maintain and upkeep of all roads, open spaces, public park and public health services for a period of five years from the date of issue of the completion certificate unless earlier relieved of this responsibility and thereupon to transfer all such roads, open spaces, public parks and public health services free of cost to the Govt. or the local authority, as the case may be, in accordance with the provisions of Section 3(3)(a)(iii) of the Haryana Development and Regulation of Urban Areas Act, 1975.",
						normal));
				subList3.add(new ListItem(
						"That you shall construct portion of service road, internal circulation roads, forming the part of site area at your own cost and shall transfer the land falling within alignment of same free of cost to the Govt. u/s 3(3) (a) (iii) of the Haryana Development and Regulation of Urban Areas Act, 1975.",
						normal));
				subList3.add(new ListItem(
						"That you shall be liable to pay the actual rates of External Development Charges as and when determined and demanded as per prescribed schedule by the DTCP Haryana.",
						normal));
				subList3.add(new ListItem(
						"That the Affordable Group Housing Colony shall be laid out to conform to the approved building plans and the development works are executed according to the designs and specifications shown in the approved plan.",
						normal));
				subList3.add(new ListItem(
						"That the building plans of the Affordable Group Housing Colony shall be submitted within three months of the date of grant of licence and no construction/development shall be undertaken before approval of building plans.",
						normal));
				subList3.add(new ListItem(
						"That you shall construct at your own cost, or get constructed by any other institution or individual at its costs, the community buildings on the lands set apart for this purpose, within five years from grant of license extendable by the Director for another period of two years, for the reasons to be recorded in writing failing which the land shall vest with the Government after such specified period, free of cost, in which case the Government shall be at liberty to transfer such land to any person or any institution including a local authority, for the said purposes, on such terms & conditions, as it may deem fit.",
						normal));
				subList3.add(new ListItem(
						"That you shall integrate the services with Haryana Shahari Vikas Pradhikaran services as and when made available. ",
						normal));
				subList3.add(new ListItem(
						"That you have not submitted any other application for grant of license for development of the said land or part thereof for any purpose under the provisions of the Haryana Development and Regulation of Urban Areas Act, 1975 or any application seeking permission for change of land use under the provision of the Punjab Scheduled Roads and Controlled Area Restrictions of Unregulated Development Act, 1963.",
						normal));
				subList3.add(new ListItem(
						"That you have understood that the development/construction cost of 24 m/18 m major internal roads is not included in the EDC rates and you shall pay the proportionate cost for acquisition of land, if any, alongwith the construction cost of 24 m/18 m wide major internal roads as and when finalized and demanded by the Department. ",
						normal));
				subList3.add(new ListItem(
						"That you shall make arrangements for water supply, sewerage, drainage etc. to the satisfaction of DTCP till these services are made available from External Infrastructure to be laid by Haryana Urban Development Authority.",
						normal));
				subList3.add(new ListItem(
						"That the rain water harvesting system shall be provided as per Central Ground Water Authority Norms/Haryana Govt. notification as applicable.",
						normal));
				subList3.add(new ListItem(
						"That you shall make provision of solar power system as per guidelines of Haryana Renewable Energy Development Agency and shall make operational where applicable before applying for an Occupation Certificate. ",
						normal));
				subList3.add(new ListItem(
						"That you shall use only LED fitting for internal lighting as well as campus lighting.",
						normal));
				subList3.add(new ListItem(
						"That you shall convey the ‘Ultimate Power Load Requirement’ of the project to the concerned power utility, with a copy to the Director, within two months period from the date of grant of license to enable provision of site in licensed land for Transformers/Switching Stations/Electric Sub Stations as per the norms prescribed by the power utility in the zoning plan of the project.",
						normal));
				subList3.add(new ListItem(
						"That you shall submit compliance of Rule 24, 26, 27 & 28 of Rules 1976 & Section 5 of Haryana Development and Regulation of Urban Areas Act, 1975, and shall inform account number and full particulars of the scheduled bank wherein you have to deposit seventy percentum of the amount from the floor/space holders for meeting the cost of Internal Development Works in the colony.",
						normal));
				subList3.add(new ListItem(
						"That you shall permit the Director or any other office authorized by him to inspect the execution of the layout and the development works in the colony and to carry out all directions issued by him for ensuring due compliance of the execution of the layout and development works in accordance with the license granted.",
						normal));
				subList3.add(new ListItem(
						"That you shall not give any advertisement for sale of commercial area and flat in affordable Group Housing area before the approval of building plans.",
						normal));
				subList3.add(new ListItem(
						"That no part of applied land has been sold to any person after entering into collaboration agreement with the land owners and also that presently there is no collaboration agreement enforced with any other person for the same land.",
						normal));
				subList3.add(new ListItem(
						"That you shall submit NOC from the Ministry of Environment & Forest, Govt. of India with respect to their notification dated 14.09.2006 and clearance regarding PLPA, 1900 from competent authority before executing development works.",
						normal));
				subList3.add(new ListItem(
						"That you shall pay the labour cess as per policy instructions issued by Haryana Government vide Memo No. Misc. 2057-5/25/2008/ 2TCP dated 25.02.2010. ",
						normal));
				subList3.add(new ListItem(
						"That you shall keep pace of construction atleast in accordance with sale agreement executed with the buyers of the flats as and when scheme is launched.",
						normal));
				subList3.add(new ListItem(
						"That you shall abide by the terms and conditions as per Affordable Housing Policy-2013 notified on 19.08.2013 as amended from time to time.",
						normal));
				list.add(subList3);
				list.add(new ListItem(
						"That you shall submit a certificate from the Deputy Commissioner, Gurugram/District Revenue Authority stating that there is no further sale of the land applied for license till date and applicant are the owner of the land. Further, it may also be verified that no land acquisition proceedings on the applied land have been initiated till date.",
						normal));
				list.add(new ListItem(
						"That you shall complete the demarcation at site within 7 days and will submit the demarcation plan in the office of District Town Planner, Gurugram within 15 days of issuance of this letter for verification. ",
						normal));
//				if (requestLOIReport.getAddedContent() != null && !requestLOIReport.getAddedContent().isEmpty()) {
//					requestLOIReport.getAddedContent().stream().forEach(cd -> {
//						list.add(new ListItem(cd, normal));
//					});
//				}
				loiTableReportData(list, userId, applicationNumber, requestLOIReport);
				doc.add(list);
				addEmptyLine(para1, 1);
				String note = "Note:- That you shall intimate their official Email ID and the correspondence on this email ID by     	the Deptt. will be treated receipt of such correspondence.";
				Paragraph para10 = new Paragraph(note, small);
				para10.setSpacingBefore(10f);
				doc.add(para10);
				doc.add(new Paragraph("DA/schedule of land. ", normal));
				addEmptyLine(para1, 1);
				Paragraph para12 = new Paragraph("Your filled data for following field(s) has not been approved:",
						normal);
				// You are requested to complete the following shortcomings immediately
				para12.setSpacingBefore(10f);
				Paragraph para13 = new Paragraph("".toUpperCase(), normal);
				para13.setSpacingBefore(10f);
//				doc.add(para12);
//				doc.add(para13);
//				loiTableReportData(doc, userId, applicationNumber, requestLOIReport);
				try {
					Paragraph preface0 = new Paragraph("(" + dtcpUserName + ", I.A.S)    ", smallBold);
					preface0.setIndentationLeft(10);
					preface0.setAlignment(Element.ALIGN_CENTER);
					preface0.setSpacingBefore(10f);
					Paragraph preface01 = new Paragraph("Director,", smallBold);
					preface01.setIndentationLeft(70);
					Paragraph preface02 = new Paragraph("Town & Country Planning", smallBold);
					preface02.setIndentationLeft(25);
					Paragraph preface3 = new Paragraph("Haryana Chandigarh", smallBold);
					preface3.setIndentationLeft(40);

					PdfDiv div = new PdfDiv();
					div.addElement(preface0);
					div.addElement(preface01);
					div.addElement(preface02);
					div.addElement(preface3);
					div.setPaddingLeft(320f);
					div.setFloatType(FloatType.RIGHT);
					doc.add(div);

					Paragraph preface4 = new Paragraph("Endst. " + memoNumber
							+ "/ 		                                    Dated: " + currentDate.split("\\s+")[0]);
					preface4.setAlignment(Element.ALIGN_LEFT);
					preface4.setSpacingBefore(10f);
					doc.add(preface4);
					Paragraph para102 = new Paragraph(
							"A copy is forwarded to the followings for information and necessary action:-", normal);
					para102.setSpacingBefore(10f);
					Paragraph para103 = new Paragraph("".toUpperCase(), normal);
					para103.setSpacingBefore(10f);
					doc.add(para102);
					doc.add(para103);

					List subList1 = new List(List.ORDERED);
					subList1.setIndentationLeft(30f);
					subList1.add(new ListItem("The Deputy Commissioner, Gurugram.", normal));
					subList1.add(new ListItem("The Additional Director, Urban Estate, Harysana, Sector-6 Panchkula.",
							normal));
					subList1.add(new ListItem("Senior Town Planner, Gurugram.", normal));
					subList1.add(new ListItem("District Town Planner, Gurugram.", normal));
					doc.add(subList1);

					Paragraph preface001 = new Paragraph("(" + hqUserName + ")", normal);
					preface001.setAlignment(Element.ALIGN_RIGHT);

					Paragraph preface102 = new Paragraph("District Town Planner (HQ)", normal);
					preface102.setAlignment(Element.ALIGN_RIGHT);

					Paragraph preface23 = new Paragraph("For: Director, Town & Country Planning", normal);
					preface23.setAlignment(Element.ALIGN_RIGHT);

					Paragraph preface34 = new Paragraph("Haryana, Chandigarh.", normal);
					preface34.setAlignment(Element.ALIGN_RIGHT);

					PdfDiv div2 = new PdfDiv();
					div2.addElement(preface001);
					div2.addElement(preface102);
					div2.addElement(preface23);
					div2.addElement(preface34);
					div2.setFloatType(FloatType.RIGHT);
					doc.add(div2);
				} catch (Exception e) {
					log.error("Exception : "+e.getMessage());
				}

				doc.close();
				writer.close();
			} catch (DocumentException e1) {
				log.error("DocumentException : "+e1.getMessage());
			} catch (Exception e) {
				log.error("Exception : "+e.getMessage());
				
			}
		}

	}

	/**********************************************
	 * LOI Purpose : Affordable Group Housing end *
	 *********************************************/

	/*********************************************************
	 * 3: LOI Purpose : Group Housing under TOD policy start *
	 *********************************************************/
	private void getLoiReportGroupHousingUnderTODPolicy(String applicationNumber, String userId, String hqUserId,
			RequestLOIReport requestLOIReport) {
		LicenseServiceResponseInfo licenseServiceResponceInfo = checkApplicationIsValid(requestLOIReport,
				applicationNumber, userId, hqUserId);
		if (licenseServiceResponceInfo == null) {
			return;
		} else {

			try {
				String myFile = loireportPath;
				Document doc = new Document(PageSize.A4);
				PdfWriter writer = PdfWriter.getInstance(doc,
						new FileOutputStream(myFile + "loi-report-" + applicationNumber + ".pdf"));
				doc.open();
				String far = null;
				addTitlePage(doc, 1);
				memoNumber = "LC- " + (licenseServiceResponceInfo.getCaseNumber() != null
						? licenseServiceResponceInfo.getCaseNumber().replaceAll("LC", "").split("-")[0]
						: "N/A") + "-JE (VA)- " + (applicationDate.split("\\s+")[0].split("\\.")[2]);

				int leftMarg = 70;
				Paragraph preface1 = new Paragraph();
				preface1.add(new Paragraph("To", smallBold));
				doc.add(preface1);
				
				Paragraph loiNumberParagraph = new Paragraph(
						"LOI Number : " + loiNumber);
				loiNumberParagraph.setIndentationLeft(leftMarg);
				loiNumberParagraph.setSpacingBefore(15f);
				doc.add(loiNumberParagraph);
				
				addEmptyLine(preface1, 2);
				Paragraph preface2 = new Paragraph();
				preface2.setIndentationLeft(leftMarg);
				try {
					ApplicantInfo applicationInfo = licenseDetails.getApplicantInfo();
					preface2.add(new Paragraph(applicationInfo.getDevDetail().getAddInfo().getCompanyName()));
					preface2.add(new Paragraph(applicationInfo.getDevDetail().getAddInfo().getRegisteredAddress()));
					preface2.add(new Paragraph((applicationInfo.getState() == null ? "N/A" : applicationInfo.getState())
							+ "-" + applicationInfo.getDevDetail().getLicenceDetails().getPincode()));
				} catch (Exception e) {
					preface2.add(new Paragraph("_________  Pvt. Ltd.,"));
					preface2.add(new Paragraph("--, -----------, ----------,"));
					preface2.add(new Paragraph("---------------."));
					e.printStackTrace();
				}
				doc.add(preface2);
				Paragraph paragraph = new Paragraph("Memo No. " + memoNumber
						+ "/ 		                                    Dated: " + currentDate.split("\\s+")[0]);
				paragraph.setIndentationLeft(leftMarg);
				paragraph.setSpacingBefore(15f);
				doc.add(paragraph);
				addEmptyLine(paragraph, 1);

				Paragraph preface12 = new Paragraph();
				preface12.add(new Paragraph(
						"Subject:         Request for grant of licence for setting up of group housing  colony under TOD policy ",
						smallBold));
				preface12.setSpacingBefore(15f);
				doc.add(preface12);
				Paragraph preface13 = new Paragraph();
				preface13.add(new Paragraph("(with "+farAmount+" FAR) over an area measuring " + totalArea
						+ " acres in the revenue estate of " + address + " – Issuance of LOI.", smallBold));
				preface13.setIndentationLeft(leftMarg);
				doc.add(preface13);

				Paragraph preface14 = new Paragraph();
				preface14.add(new Paragraph("Please refer to your application dated " + applicationDate.split("\\s+")[0]
						+ " on the above cited subject.", normal));
				preface14.setIndentationLeft(leftMarg);
				preface14.setSpacingBefore(20f);
				preface14.setSpacingAfter(20f);
				doc.add(preface14);

				List subList2 = new List();
				subList2.setIndentationLeft(25f);
				List subList3 = new RomanList();// new List(List.ORDERED, List.ALPHABETICAL);
				subList3.setLowercase(List.LOWERCASE);
				subList3.setPostSymbol(". ");
				subList3.setIndentationLeft(15f);

				List list = new List(List.ORDERED);
				List subList = new List(List.ORDERED);
				subList.setIndentationLeft(30f);
				String content = "`                Your request for the grant of license under Section 3 of the Haryana Development and Regulation of Urban Areas Act, 1975 and the Haryana Development and Regulation of Urban Areas Rules, 1976 for development of Group Housing Colony under TOD policy (with 2.5 FAR) over an area measuring "
						+ totalArea + " acres {after excluding area measuring 0.49 acre falling in Khasra no. "
						+ khasraNo + " falling in open space zone} in the revenue estate of " + address
						+ " has been examined and considered by the Department and it is proposed to grant license. Accordingly, you are hereby called upon to fulfill the following requirements/pre-requisites as laid down in Rule 11 of the Haryana Development and Regulation of Urban Areas Rules, 1976 within a period of 60 days from the date of issuance of this letter, failing which the request for grant of license shall be refused.";
				Paragraph para1 = new Paragraph("", normal);
				list.add(new ListItem(content, normal));
				list.add(new ListItem(
						"To furnish the bank guarantee on account of Internal Development Works and External Development Charges for the amount calculated as under:-",
						normal));

//			list.add(new ListItem("To submit BG amounting ₹ 482.9 lac against the amount of External Development Charges amounting "+totalFee+"*.",normal));
				List subListI = new List();
				List subListE = new List();
				subListI.setIndentationLeft(30f);
				subListE.setIndentationLeft(30f);
				
				
//				Double plottedInternalCost = 20 * Double.parseDouble(plottedComponent);
				
				Double comericalInternalCost = 50 * amountIDW;
				Double totalInternalCost = communityFacultyCost + comericalInternalCost;
				Double bg25InternalPercentage = totalInternalCost * 0.25;
				
				Double plottedExternalCost = (Double.parseDouble(plottedComponent)*zoneWiseEdcAmount*2.5)/Double.parseDouble(farAmount);
				Double comericalExternalCost = (Double.parseDouble(comericalComponent)*zoneWiseEdcAmount*2.5)/Double.parseDouble(farAmount);
				Double totalExternalCost = plottedExternalCost + comericalExternalCost;
				Double bg25ExternalPercentage = totalExternalCost * 0.25;

				
				
				subListI.add(
						new ListItem("A)	Tentative rates for GH@ ₹ 50.00 Lac per acre 	= ₹ "+comericalInternalCost+" Lacs", normal));
				subListI.add(new ListItem("B)	Cost of Community Facilities				= "+communityFacultyCost+" ", normal));
				subListI.add(
						new ListItem("C)	Total cost of Internal Development Works		= ₹ "+totalInternalCost+" Lacs ", normal));
				subListI.add(new ListItem("D)	25% B.G. on account of IDW				= ₹ "+bg25InternalPercentage+" Lacs", normal));
				subListI.add(new ListItem("     (valid for 5 years in favour of DTCP)", normalBold));

				
				
				subListE.add(new ListItem("A)	EDC for area GH Component: ", normal));
				subListE.add(new ListItem(
						"     "+plottedComponent+" x "+zoneWiseEdcAmount+" (zone) x 2.5/"+farAmount+" (far) 				= ₹ "+plottedExternalCost+" Lacs          ",
						normal));
				subListE.add(new ListItem("B)	EDC for Commercial Component: ", normal));
				subListE.add(new ListItem(
						"     "+comericalComponent+" x "+zoneWiseEdcAmount+"(zone) x 2.5/"+farAmount+"(far)		  		= ₹ "+comericalExternalCost+" Lacs      ", normal));
				subListE.add(new ListItem("C)	Total cost of EDC                    				= ₹ "+totalExternalCost+" Lacs",
						normal));
				subListE.add(
						new ListItem("D)	BG required equivalent to 25% of total EDC     	= ₹ "+bg25ExternalPercentage+" Lacs", normal));
				subListE.add(new ListItem("     (valid for 5 years in favour of DTCP)", normalBold));

				subList.add(new ListItem(new ListItem("INTERNAL DEVELOPMENT WORKS:")));
				subList.add(subListI);

				subList.add(new ListItem(new ListItem("EXTERNAL DEVELOPMENT WORKS:")));
				subList.add(subListE);
				list.add(subList);

				List subList01 = new List();
				subList01.setIndentationLeft(15f);
				subList01.add(new ListItem(new ListItem(
						"*It is made clear that the Bank Guarantee of Internal Development Works has been worked out on the interim rates and you have to submit the additional bank guarantee if any, required at the time of approval of Service Plan/Estimate according to the approved layout plan. With an increase in the cost of construction and an increase in the number of facilities in the layout plan, you would be required to furnish an additional bank guarantee within 30 days on demand.",
						normal)));
				list.add(subList01);
				List subList02 = new List();
				subList02.setIndentationLeft(15f);
				subList02.add(new ListItem(new ListItem(
						"**The EDC rates have been calculated on the basis of indexation mechanism for calculation of EDC dated 11.02.2016 in the State of Haryana. The EDC rates are based on 2015 year level and are effective from 01.01.2016 for the period upto 31.03.2019. In the event of increase of rates of external development charges, you will have to pay the enhanced rates of external development charges as finally determined and as and when demanded by the DTCP, Haryana and furnish additional bank guarantee and submit an undertaking in this regard.",
						normal)));
				list.add(subList02);

				list.add(new ListItem(
						"That you shall execute two agreements i.e. LC-IV & Bilateral Agreement on Non-Judicial Stamp Paper of Rs. 100/-. Further, following additional clauses shall be added in LC-IV agreement as per Government instruction dated 14.08.2020:.",
						normal));

				subList2.add(new ListItem(new ListItem(
						"I.	  That the owner/developer shall integrate the bank account in which 70 percent allottee receipts are credited under Section-4(2)(I)(D) of the Real Estate Regulation and Development Act, 2016 with the online application/payment gateway of the Department, in such manner, so as to ensure that 10% of the total receipt from each payment made by an allottee is automatically deducted and gets credited to the EDC head in the State treasury.",
						normalItalic)));

				subList2.add(new ListItem(new ListItem(
						"II.   That such 10% of the total receipt from each payment made by the allottee, which is received by the Department shall get automatically credited, on the date of receipt in the Government treasury against EDC dues.  ",
						normalItalic)));
				subList2.add(new ListItem(new ListItem(
						"III.  That such 10% deduction shall continue to operate till the total EDC dues get recovered from the owner/developer.",
						normalItalic)));
				subList2.add(new ListItem(new ListItem(
						"IV.   The implementation of such mechanism shall, however, have no bearing on EDC instalment schedule conveyed to the owner/developer. The owner/developer shall continue to supplement such automatic EDC deductions with payments from its own funds to ensure that by the EDC instalments that are due for payment that paid as per the prescribed schedule. ",
						normalItalic)));
				subList2.add(new ListItem(new ListItem(
						"IV.	The implementation of such mechanism shall, however, have no bearing on EDC instalment schedule conveyed to the owner/developer. The owner/developer shall continue to supplement such automatic EDC deductions with payments from its own funds to ensure that by the EDC instalments that are due for payment that paid as per the prescribed schedule.",
						normal)));
				list.add(subList2);

				list.add(new ListItem("To deposit an amount of " + conversionCharges + "/-(" + conversionChargesInWord
						+ ") on account of conversion charges and " + stateInfrastructureDevelopmentCharges + "/-("
						+ stateInfrastructureDevelopmentChargesInWord
						+ ")  on account of Infrastructure Augmentation charges in favour of Director, Town & Country Planning, Haryana, through online mode.\r\n"
						+ "\nNote:- You have option to either make payment of complete amount of above said fee & charges or 50% of same in compliance of LOI and balance 50% in two equal installments of 3 months each with normal interest of 12% p.a. and penal interest of 3% for the delayed period. If option of making payment in installments is opted, then building plans will be approved only after recovery of full fee and charges as per aforesaid stipulation.",
						normal));
				list.add(new ListItem("To deposit an amount of " + licenseFees + "/-(" + licenseFeesInWord
						+ ") on account of balance licence fees in favour of Director, Town & Country Planning, Haryana through online mode."
						+ "\nNote:- You have option to either make payment of complete amount of above said balance licence fee in compliance of LOI or 25% of total licence fees within 60 days and balance 50% of the total amount  in two equal installments of 3 months each with normal interest of 12% p.a. and penal interest of 3% for the delayed period. If option of making payment in installments is opted, then building plans will be approved only after recovery of full fee and charges as per aforesaid stipulation."
						+ "\n(The fee and charges being conveyed are subject to audit and reconciliation of accounts.)\r\n"
						+ "", normal));
				list.add(new ListItem(
						"To furnish an undertaking on non judicial stamp paper of Rs. 100/- to the following effect:-",
						normal));

				subList3.add(new ListItem(new ListItem(
						"That you shall pay the State Infrastructure Development Charges amounting to "
								+ stateInfrastructureDevelopmentCharges + "/-("
								+ stateInfrastructureDevelopmentChargesInWord
								+ "), in two equal instalments. First Instalment will be due within 60 days of grant of license and second Instalment within six months of grant of license failing which 18% PA interest will be liable for the delayed period",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That you shall submit the additional bank guarantee, if any required at the time of approval of Service Plans/Estimate. With an increase in the cost of construction and increase in the number of facilities in Layout Plan, you will be required to furnish an additional bank guarantee within 30 days on demand. It is made clear that bank guarantee of Internal Development Works/EDC has been worked out on the interim rates.  ",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That you shall maintain and upkeep all roads open spaces, public parks and public health services for a period of five years from the date of issue to the completion certificate unless earlier relieved of this responsibility and thereupon to transfer all such roads/service roads, open spaces, public parks and public health services free of cost to the Government or the local authority, as the case may be, in accordable with the provisions of Section 3(3)(a)(iii) of the Haryana Development and Regulation of Urban Areas Act, 1975.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That you shall construct at your own cost, or get constructed by any other institution or individual at its cost, schools, hospitals, community centers and other community buildings on the lands set apart for this purpose, in a period as may be specified, and failing which action as per the Act/Rules shall be initiated. The land shall vest with the Government after such specified period, free of cost, in which case the Government shall be at liberty to transfer such-land to any person or institution including a local authority, for the said purposes, on such terms and conditions, as it may deem fit.",
						normal)));

				subList3.add(new ListItem(new ListItem(
						"That you shall pay the proportionate cost of construction of such percentage of sites of such school, hospital, community centre and other community building and at such rates as specified by the Director",
						normal)));

				subList3.add(new ListItem(new ListItem(
						"That you shall permit the Director or any other officer authorised by him to inspect the execution of the layout and the development works in the colony and to carry out all directions issued by him for ensuring due compliance of the execution of the layout and development works in accordance with the licence granted.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That you shall construct 18/24 m wide internal circulation road forming part of licensed area at your own costs and transfer the same free of cost to the Government",
						normal)));

				subList3.add(new ListItem(new ListItem(
						"That you shall construct and allot EWS category flats as per departmental policy dated 08.07.2013 and as amended from time to time.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That you shall submit NOC from the Ministry of Environment & Forest, Govt. of India with respect to their notification dated 14.09.2006 and clearance regarding PLPA, 1900 from competent authority before executing development works.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That you shall make arrangement for water supply, sewerage, drainage etc. to the satisfaction of DTCP till these services are made available from external infrastructure to be laid by HSVP.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That you shall convey the ‘Ultimate Power Load Requirement’ of the project to the concerned power utility, with a copy to the Director, within two months period from the date of grant of licence to enable provision of site in your land for Transformers/Switching Station/Electric Sub-Stations as per the norms prescribed by the power utility in the zoning plan of the project.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That you shall provide the rain water harvesting system as per Central Ground Water Authority Norms/Haryana Govt. notification as applicable.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That you shall aware that the development/construction cost of 24/30 m wide road/major internal road is not included in the EDC rates and you shall pay the proportionate cost for acquisition of land if any, alongwith the construction cost of 24/30 m wide road/major internal road as and when finalized and demanded by the Director Town & Country Planning, Haryana.  ",
						normal)));

				subList3.add(new ListItem(new ListItem(
						"That you shall provide the solar water heating system as provisions of HAREDA and shall be made operational where applicable before applying for an occupation certificate.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That you shall use only LED fitting for internal lighting as well as campus lighting.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That you shall submit compliance of Rule 24, 26, 27 & 28 of Rules 1976 & Section 5 of Haryana Development and Regulation of Urban Areas Act, 1975, and shall inform account number and full particulars of the scheduled Bank wherein you have to deposit thirty percentum of the amount from the Flat/shop buyers for meeting the cost of Internal Development Works in the colony. ",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That you shall keep the pace of construction atleast in accordance with sale agreement executed with the buyers of the flats as and when scheme is launched.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That you will integrate the services with the HSVP services as and when made available.",
						normal)));

				subList3.add(new ListItem(new ListItem(
						"That you shall pay the labour cess as per policy instructions issued by Haryana Government vide Memo No. Misc. 2057-5/25/2008/2TCP dated 25.02.2010.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That you shall not pre-launch/sale of flats before approval of the building plans.", normal)));
				subList3.add(new ListItem(
						"That the applied land does not exceed the ceiling prescribed in the Land Ceiling Act, 1972 at the time of making application for grant of licence.",
						normal));
//			subList3.add(new ListItem(new ListItem(
//					"That you shall not use the ground water for the purpose of construction of building. The building plans shall be approved only after the source of water for construction purposal is explained to the satisfaction of HSVP Authority in terms of orders of the Hon'ble High Court dated 16.07.2012 in CWP’s no. 20032 of 2008, 13594 of 2009 and 807 of 2012.",
//					normal)));
//			subList3.add(new ListItem(new ListItem(
//					"That you shall obey all the directions/restrictions imposed by the Department from time to time in public interest.",
//					normal)));

				subList3.add(new ListItem(new ListItem(
						"That you shall not use the ground water for the purpose of construction of building. The building plans shall be approved only after the source of water for construction purposal is explained to the satisfaction of HSVP Authority in terms of orders of the Hon'ble High Court dated 16.07.2012 in CWP’s no. 20032 of 2008, 13594 of 2009 and 807 of 2012.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That you shall obey all the directions/restrictions imposed by the Department from time to time in public interest.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That you shall specify the detail of calculations per sqm/per sq ft, which is being demanded from the flat/shop owners on account of IDC/EDC, if being charged separately as per rates fixed by Govt. ",
						normal)));

				list.add(subList3);
				list.add(new ListItem(
						"That you shall complete the demarcation at site within 7 days and will submit the demarcation Plan in the office of District Town Planner, Gurugram within 15 days of issuance of this letter.",
						normal));

				list.add(new ListItem("That you shall submit a certificate from the District Revenue Authority, "
						+ disticName
						+ " stating that there is no further sale of the land applied for license till date and applicant companies/individual are owners of the land.",
						normal));
//				if (requestLOIReport.getAddedContent() != null && !requestLOIReport.getAddedContent().isEmpty()) {
//					requestLOIReport.getAddedContent().stream().forEach(cd -> {
//						list.add(new ListItem(cd, normal));
//					});
//				}
				loiTableReportData(list, userId, applicationNumber, requestLOIReport);
				doc.add(list);
				addEmptyLine(para1, 1);
				String note = "Note:- That you will intimate your official “email ID” to the Department and correspondence done by Department on this ID shall be treated as official intimation & legally valid.";
				Paragraph para10 = new Paragraph(note, small);
				para10.setSpacingBefore(10f);
				doc.add(para10);
//			doc.add(new Paragraph("DA/schedule of land. ", normal));
				addEmptyLine(para1, 1);
				Paragraph para12 = new Paragraph("Your filled data for following field(s) has not been approved:",
						normal);
				// You are requested to complete the following shortcomings immediately
				para12.setSpacingBefore(10f);
				Paragraph para13 = new Paragraph("".toUpperCase(), normal);
				para13.setSpacingBefore(10f);
//				doc.add(para12);
//				doc.add(para13);
//
//				loiTableReportData(doc, userId, applicationNumber, requestLOIReport);

				try {
					Paragraph preface0 = new Paragraph("(" + dtcpUserName + ", I.A.S)    ", smallBold);
					preface0.setIndentationLeft(10);
					preface0.setAlignment(Element.ALIGN_CENTER);
					preface0.setSpacingBefore(10f);
					Paragraph preface01 = new Paragraph("Director,", smallBold);
					preface01.setIndentationLeft(70);
					Paragraph preface02 = new Paragraph("Town & Country Planning", smallBold);
					preface02.setIndentationLeft(25);
					Paragraph preface3 = new Paragraph("Haryana Chandigarh", smallBold);
					preface3.setIndentationLeft(40);

					PdfDiv div = new PdfDiv();
					div.addElement(preface0);
					div.addElement(preface01);
					div.addElement(preface02);
					div.addElement(preface3);
					div.setPaddingLeft(320f);
					div.setFloatType(FloatType.RIGHT);
					doc.add(div);

					Paragraph preface4 = new Paragraph("Endst. " + memoNumber
							+ "/ 		                                    Dated: " + currentDate.split("\\s+")[0]);
					preface4.setAlignment(Element.ALIGN_LEFT);
					preface4.setSpacingBefore(10f);
					doc.add(preface4);
					Paragraph para102 = new Paragraph(
							"A copy is forwarded to the following alongwith copy of land schedule, with direction to verify demarcation at the site.",
							normal);
					para102.setSpacingBefore(10f);
					Paragraph para103 = new Paragraph("".toUpperCase(), normal);
					para103.setSpacingBefore(10f);
					doc.add(para102);
					doc.add(para103);

					List subList1 = new List(List.ORDERED);
					subList1.setIndentationLeft(30f);
					subList1.add(new ListItem("Senior Town Planner, Gurugram.", normal));
					subList1.add(new ListItem("District Town Planner, Gurugram.", normal));
					doc.add(subList1);

					Paragraph preface001 = new Paragraph("(" + hqUserName + ")", normal);
					preface001.setAlignment(Element.ALIGN_RIGHT);

					Paragraph preface102 = new Paragraph("District Town Planner (HQ)", normal);
					preface102.setAlignment(Element.ALIGN_RIGHT);

					Paragraph preface23 = new Paragraph("For: Director, Town & Country Planning", normal);
					preface23.setAlignment(Element.ALIGN_RIGHT);

					Paragraph preface34 = new Paragraph("Haryana, Chandigarh.", normal);
					preface34.setAlignment(Element.ALIGN_RIGHT);

					PdfDiv div2 = new PdfDiv();
					div2.addElement(preface001);
					div2.addElement(preface102);
					div2.addElement(preface23);
					div2.addElement(preface34);
					div2.setFloatType(FloatType.RIGHT);
					doc.add(div2);
				} catch (Exception e) {
					log.error("Exception : "+e.getMessage());
				}

				doc.close();
				writer.close();
			} catch (DocumentException e1) {
				log.error("DocumentException : "+e1.getMessage());
			} catch (Exception e) {
				log.error("Exception : "+e.getMessage());
				
			}
		}
	}

	/****************************************************
	 * LOI Purpose : Group Housing under TOD policy end *
	 ****************************************************/

	/*********************************************************************
	 * 4: LOI Purpose : Affordable Plotted Colony under Deen Dayal start *
	 *********************************************************************/
	private void getLoiReportGAffordablePlottedDeenDayal(String applicationNumber, String userId, String hqUserId,
			RequestLOIReport requestLOIReport) {
		LicenseServiceResponseInfo licenseServiceResponceInfo = checkApplicationIsValid(requestLOIReport,
				applicationNumber, userId, hqUserId);
		if (licenseServiceResponceInfo == null) {
			return;
		} else {

			try {
				String myFile = loireportPath;
				Document doc = new Document(PageSize.A4);
				PdfWriter writer = PdfWriter.getInstance(doc,
						new FileOutputStream(myFile + "loi-report-" + applicationNumber + ".pdf"));
				doc.open();
				addTitlePage(doc, 1);
				memoNumber = "LC- " + (licenseServiceResponceInfo.getCaseNumber() != null
						? licenseServiceResponceInfo.getCaseNumber().replaceAll("LC", "").split("-")[0]
						: "N/A") + "-JE (VA)- " + (applicationDate.split("\\s+")[0].split("\\.")[2]);

				int leftMarg = 70;
				Paragraph preface1 = new Paragraph();
				preface1.add(new Paragraph("To", smallBold));
				doc.add(preface1);
				addEmptyLine(preface1, 2);
				Paragraph preface2 = new Paragraph();
				preface2.setIndentationLeft(leftMarg);
				try {
					ApplicantInfo applicationInfo = licenseDetails.getApplicantInfo();
					preface2.add(new Paragraph(applicationInfo.getDevDetail().getAddInfo().getCompanyName()));
					preface2.add(new Paragraph(applicationInfo.getDevDetail().getAddInfo().getRegisteredAddress()));
//				preface2.add(new Paragraph((applicationInfo.getState() == null ? "N/A" : applicationInfo.getState())
//						+ "-" + applicationInfo.getDevDetail().getLicenceDetails().getPincode()));
				} catch (Exception e) {
					preface2.add(new Paragraph("_________  Pvt. Ltd.,"));
					preface2.add(new Paragraph("--, -----------, ----------,"));
					preface2.add(new Paragraph("---------------."));
					e.printStackTrace();
				}
				doc.add(preface2);
				
				Paragraph loiNumberParagraph = new Paragraph(
						"LOI Number : " + loiNumber);
				loiNumberParagraph.setIndentationLeft(leftMarg);
				loiNumberParagraph.setSpacingBefore(15f);
				doc.add(loiNumberParagraph);
				
				Paragraph paragraph = new Paragraph("Memo No. " + memoNumber
						+ "/ 		                                    Dated: " + currentDate.split("\\s+")[0]);
				paragraph.setIndentationLeft(leftMarg);
				paragraph.setSpacingBefore(15f);
				doc.add(paragraph);
				addEmptyLine(paragraph, 1);

				Paragraph preface12 = new Paragraph();
				preface12.add(new Paragraph(
						"Subject:         Request for grant of licence for setting up of an Affordable Plotted Colony under Deen ",
						smallBold));
				preface12.setSpacingBefore(15f);
				doc.add(preface12);
				Paragraph preface13 = new Paragraph();
				preface13.add(new Paragraph("Dayal Jan Awas Yojna over an area measuring " + totalArea
						+ " acres in the revenue estate of " + address + "- Issuance of LOI.", smallBold));
				preface13.setIndentationLeft(leftMarg);
				doc.add(preface13);

				Paragraph preface14 = new Paragraph();
				preface14.add(new Paragraph("Please refer your application dated " + applicationDate.split("\\s+")[0]
						+ " on the matter as subject cited above.", normal));
				preface14.setIndentationLeft(leftMarg);
				preface14.setSpacingBefore(20f);
				preface14.setSpacingAfter(20f);
				doc.add(preface14);

				List subList2 = new RomanList();
				subList2.setPostSymbol(". ");
				subList2.setIndentationLeft(25f);
				List subList3 = new RomanList();// new List(List.ORDERED, List.ALPHABETICAL);
				subList3.setLowercase(List.LOWERCASE);
				subList3.setPostSymbol(". ");
				subList3.setIndentationLeft(15f);

				List list = new List(List.ORDERED);
				List subList = new List(List.ORDERED);
				subList.setIndentationLeft(30f);
				String content = "`                Your request for grant of licence under section 3 of the Haryana Development and Regulation of Urban Areas Act, 1975 and Rules, 1976 framed there under for development of an Affordable Plotted Colony (under DDJAY) over an area measuring "
						+ totalArea + " acres in the revenue estate of " + address
						+ " has been considered and it is proposed to grant a licence for setting up of aforesaid colony. You are, therefore, called upon to fulfill the following requirements/pre-requisites laid down in Rule, 11 of the Haryana Development and Regulation of Urban Areas Rules, 1976 within a period of 60 days from the date of issue of this letter, failing which request for grant of licence shall be refused. ";
				Paragraph para1 = new Paragraph("", normal);
				list.add(new ListItem(content, normal));
				list.add(new ListItem("To deposit an amount of " + licenseFees + "/-(" + licenseFeesInWord
						+ ") on account of balance licence fee and  " + conversionCharges + "/-("
						+ conversionChargesInWord
						+ ") on account of conversion charges in favour of the Director, Town & Country Planning, Haryana through online mode.",
						normal));
				list.add(new ListItem("To deposit an amount of " + amountEDC + "/- on account of EDC in favour of the Director, Town & Country Planning, Haryana through online mode.\n(Note: The above demanded fee & charges are subject to audit and reconciliation of accounts).",
						normal));
				list.add(new ListItem(
						"To submit BG amounting Rs. "+amountEDC*0.25+" lac against the balance amount of External Development Charges amounting "
								+ amountEDC + "/-. ",
						normal));
				List subList00 = new List();
				subList00.setIndentationLeft(15f);
				subList00.add(new ListItem(new ListItem(
						"*It is made clear that rate of EDC have been calculated on the basis of EDC Indexation Mechanism Policy dated 11.02.2016, which stands approved by cabinet. If there will be any change and delay in the amendment in the Act/Rules w.r.t. the said rates, then differential amount from the original calculation will required to be deposited as per demand.",
						normal)));
				list.add(subList00);

				list.add(new ListItem(
						"To furnish bank guarantee amounting Rs. "+amountEDC*0.25+" lac against the total cost of Internal Development Works amounting "
								+ amountIDW + "/-.",
						normal));

				List subList03 = new List();
				subList03.setIndentationLeft(100f);
				subList03.add(new ListItem(new ListItem("or", normal)));
				list.add(subList03);

				List subList01 = new List();
				subList01.setIndentationLeft(15f);
				subList01.add(new ListItem(new ListItem(
						"To mortgage 15% saleable area against submission of above said BG and in case, said option is opted, then the area to be mortgaged may be indicated on the layout plan to be issued alongwith the licence alongwith the revenue details thereof. The mortgage deed in this regard shall be executed as per the directions of the Department.",
						normal)));
				list.add(subList01);

				List subList02 = new List();
				subList02.setIndentationLeft(15f);
				subList02.add(new ListItem(new ListItem(
						" **It is made clear that bank guarantee of Internal Development Works has been worked out on the interim rates and you will have to submit the additional bank guarantee, if any required, at the time of approval of Service Plan/Estimate. With an increase in the cost of construction, you would be required to furnish an additional bank guarantee within 30 days on demand (in case, 15% saleable area is mortgaged against the BG of IDW, then this clause will not be applicable).",
						normal)));
				list.add(subList02);

				list.add(new ListItem(
						"To execute two agreements i.e. LC-IV & Bilateral Agreement on Non-Judicial Stamp Paper of 100/-. Further, following additional clauses shall be added in LC-IV agreement as per Government instruction dated 14.08.2020.",
						normal));

				subList2.add(new ListItem(new ListItem(
						"That the owner/developer shall integrate the bank account in which 70 percent allottee receipts are credited under Section-4(2)(I)(D) of the Real Estate Regulation and Development Act, 2016 with the online application/payment gateway of the Department, in such manner, so as to ensure that 10% of the total receipt from each payment made by an allottee is automatically deducted and gets credited to the EDC head in the State treasury",
						normalItalic)));

				subList2.add(new ListItem(new ListItem(
						"That such 10% of the total receipt from each payment made by the allottee, which is received by the Department shall get automatically credited, on the date of receipt in the Government treasury against EDC dues.  ",
						normalItalic)));
				subList2.add(new ListItem(new ListItem(
						"That such 10% deduction shall continue to operate till the total EDC dues get recovered from the owner/developer.",
						normalItalic)));
				subList2.add(new ListItem(new ListItem(
						"The implementation of such mechanism shall, however, have no bearing on EDC instalment schedule conveyed to the owner/developer. The owner/developer shall continue to supplement such automatic EDC deductions with payments from its own funds to ensure that by the EDC instalments that are due for payment that paid as per the prescribed schedule. ",
						normalItalic)));
				list.add(subList2);

				list.add(new ListItem("To deposit an amount of " + conversionCharges + "/-(" + conversionChargesInWord
						+ ") on account of conversion charges and " + stateInfrastructureDevelopmentCharges + "/-("
						+ stateInfrastructureDevelopmentChargesInWord
						+ ")  on account of Infrastructure Augmentation charges in favour of Director, Town & Country Planning, Haryana, through online mode.\r\n"
						+ "Note:- You have option to either make payment of complete amount of above said fee & charges or 50% of same in compliance of LOI and balance 50% in two equal installments of 3 months each with normal interest of 12% p.a. and penal interest of 3% for the delayed period. If option of making payment in installments is opted, then building plans will be approved only after recovery of full fee and charges as per aforesaid stipulation.",
						normal));
				list.add(new ListItem("To deposit an amount of " + licenseFees + "/-(" + licenseFeesInWord
						+ ") on account of balance licence fees in favour of Director, Town & Country Planning, Haryana through online mode.\r\n"
						+ "Note:- You have option to either make payment of complete amount of above said balance licence fee in compliance of LOI or 25% of total licence fees within 60 days and balance 50% of the total amount  in two equal installments of 3 months each with normal interest of 12% p.a. and penal interest of 3% for the delayed period. If option of making payment in installments is opted, then building plans will be approved only after recovery of full fee and charges as per aforesaid stipulation.\r\n"
						+ "\n(The fee and charges being conveyed are subject to audit and reconciliation of accounts.)\r\n"
						+ "", normal));
				list.add(new ListItem(
						"To furnish an undertaking on non judicial stamp paper of Rs. 100/- to the following effect:-",
						normal));
				subList3.add(new ListItem("That you will pay the State Infrastructure Development Charges amounting to "
						+ stateInfrastructureDevelopmentCharges + "/-(" + stateInfrastructureDevelopmentChargesInWord
						+ ") ₹ @1000/- per sq. mtr for the commercial area, and ₹ 500/- for plotted area in two equal installments. First Installment will be due within 60 days of grant of the licence and second Installment within six months of grant of licence failing which 18% PA interest will be liable for the delayed period.",
						normal));
				subList3.add(new ListItem(
						"That you shall maintain and upkeep of all roads, open spaces, public park and public health services for a period of five years from the date of issue of the completion certificate unless earlier relieved of this responsibility and thereupon to transfer all such roads, open spaces, public parks and public health services free of cost to the Govt. or the local authority, as the case may be, in accordance with the provisions of Section 3(3)(a)(iii) of the Haryana Development and Regulation of Urban Areas Act, 1975.",
						normal));
				subList3.add(new ListItem(
						"That you shall construct 18/24/30 m wide internal circulation road forming part of licenced area at your own costs and transfer the same free of cost to the Government.",
						normal));
				subList3.add(new ListItem(
						"That area coming under the sector roads and restricted belt/green belt, if any, which forms part of licenced area and in lieu of which benefit to the extent permissible as per policy towards FAR is being granted, shall be transferred free of cost to the Govt.",
						normal));
				subList3.add(new ListItem(
						"That you shall integrate the services with Haryana Shahari Vikas Pradhikaran services as and when made available. ",
						normal));
				subList3.add(new ListItem(
						"That you have not submitted any other application for grant of licence for development of the said land or part thereof for any purpose under the provisions of the Haryana Development and Regulation of Urban Areas Act, 1975 or any application seeking permission for change of land use under the provision of the Punjab Scheduled Roads and Controlled Area Restriction of Unregulated Development Act, 1963.",
						normal));
				subList3.add(new ListItem(
						"That you will transfer 10% area of the licenced colony free of cost to the Government for provision of community facilities. This will give flexibility to the Director to work out the requirement of community infrastructure at sector level and accordingly make provisions. The said area will be earmarked on the layout plan to be approved alongwith the licence.",
						normal));
				subList3.add(new ListItem(
						"That you understand that the development/construction cost of 24 m/18 m major internal roads is not included in the EDC rates and they shall pay the proportionate cost for acquisition of land, if any, alongwith the construction cost of 24 m/18 m wide major internal roads as and when finalized and demanded by the Department. ",
						normal));
				subList3.add(new ListItem(
						"That you shall obtain NOC/Clearance as per provisions of notification dated 14.09.2006 issued by Ministry of Environment & Forest, Govt. of India, if applicable before execution of development works at site.",
						normal));
				subList3.add(new ListItem(
						"That you shall make your own arrangements for water supply, sewerage, drainage etc. to the satisfaction of DTCP till these services are made available and the same is made functional from External Infrastructure to be laid by Haryana Shehari Vikas Pradhikaran or any other execution agency.",
						normal));
				subList3.add(new ListItem(
						"That you shall obtain clearance from competent authority, if required under Punjab Land Preservation Act, 1900 and any other clearance required under any other law.",
						normal));
				subList3.add(new ListItem(
						"That the rain water harvesting system shall be provided as per Central Ground Water Authority Norms/Haryana Govt. notification as applicable.",
						normal));
				subList3.add(new ListItem(
						"That you shall use only LED fitting for internal lighting as well as campus lighting.",
						normal));
				subList3.add(new ListItem(
						"That you shall convey the ‘Ultimate Power Load Requirement’ of the project to the concerned power utility, with a copy to the Director, within two months period from the date of grant of licence to enable provision of site in licenced land for Transformers/Switching Stations/Electric Sub Stations as per the norms prescribed by the power utility in the zoning plan of the project.",
						normal));
				subList3.add(new ListItem(
						"That it will be made clear at the time of booking of plots/commercial space that specified rates include or do not include EDC. In case of not inclusion of EDC in the booking rates, then it may be specified that same are to be charged separately as per rate fixed by the Govt. You shall also provide detail of calculation of EDC per Sqm/per sft. to the Allottees while raising such demand from the plot owners.",
						normal));
				subList3.add(new ListItem(
						"That you shall keep pace of development atleast in accordance with sale agreement executed with the buyers of the plots as and when scheme is launched. ",
						normal));
				subList3.add(new ListItem(
						"That you shall arrange power connection from UHBVNL/DHBVNL for electrification of the colony and shall install the electricity distribution infrastructure as per the peak load requirement of the colony for which licencee shall get the electrical (distribution) service plan/estimates approved from the agency responsible for installation of external electric services i.e. UHBVNL/DHBVNL and complete the same before obtaining completion certificate for the colony. ",
						normal));
				subList3.add(new ListItem(
						"That you shall complete the project within seven years (5+2 years) from date of grant of licence as per clause 1(ii) of the policy notified on 01.04.2016.",
						normal));
				subList3.add(new ListItem(
						"That no clubbing of residential plots for approval of integrated zoning plan of two adjoining plots under same ownership shall be permitted. ",
						normal));
				subList3.add(new ListItem(
						"That you will pay the labour cess as per policy instructions issued by Haryana Government vide Memo No. Misc. 2057-5/25/2008/2TCP dated 25.02.2010. ",
						normal));
				subList3.add(new ListItem(
						"That you shall submit compliance of Rule 24, 26, 27 & 28 of Rules 1976 & Section 5 of Haryana Development and Regulation of Urban Areas Act, 1975, and shall inform account number and full particulars of the scheduled bank wherein you have to deposit seventy percentum of the amount received from the plot holders for meeting the cost of Internal Development Works in the colony. ",
						normal));
				subList3.add(new ListItem(
						"That no further sale has taken place after submitting application for grant of licence. ",
						normal));
				subList3.add(new ListItem(
						"That you shall not give any advertisement for sale of plots/commercial area before the approval of layout plan.",
						normal));
				subList3.add(new ListItem(
						"That you shall follow the provisions of the Real Estate (Regulations and Development) Act, 2016 and Rules framed thereunder shall be followed by the applicant in letter and spirit.",
						normal));
				subList3.add(new ListItem(
						"That no provision of the Haryana Ceiling on Land Holding Act, 1972 has been violated due to purchase of applied land. ",
						normal));
				subList3.add(new ListItem(
						"That you shall abide by the terms and conditions of policy of DDJAY and other direction given by the Director time to time to execute the project.",
						normal));
				subList3.add(new ListItem(
						"That you shall execute the development works as per Environmental Clearance and company with the provisions of Environment Protection Act, 1986, Air (Prevention and Control of Pollution of Act 1981) and Water (Prevention and Control of Pollution of 1974). In case of any violation of the provisions of said statutes, applicant shall be liable for penal action by Haryana State Pollution Control Board or any other Authority Administering the said Act.",
						normal));
				subList3.add(new ListItem(
						"That you shall not encroach the revenue rasta passing through the site, if any and shall not object for free movement. ",
						normal));

				list.add(subList3);
				list.add(new ListItem(
						"That you shall submit a certificate from the Deputy Commissioner/District Revenue Authority stating that there is no further sale of the land applied for licence till date and applicant companies/individual are owners of the land.",
						normal));

//				if (requestLOIReport.getAddedContent() != null && !requestLOIReport.getAddedContent().isEmpty()) {
//					requestLOIReport.getAddedContent().stream().forEach(cd -> {
//						list.add(new ListItem(cd, normal));
//					});
//				}
				loiTableReportData(list, userId, applicationNumber, requestLOIReport);
				doc.add(list);
				addEmptyLine(para1, 1);
				String note = "Note:- That you will intimate your official “email ID” to the Department and correspondence done by Department on this ID shall be treated as official intimation & legally valid.";
				Paragraph para10 = new Paragraph(note, small);
				para10.setSpacingBefore(10f);
				doc.add(para10);
				doc.add(new Paragraph("DA/As above.	 ", normal));
				addEmptyLine(para1, 1);
				Paragraph para12 = new Paragraph("Your filled data for following field(s) has not been approved:",
						normal);
				// You are requested to complete the following shortcomings immediately
				para12.setSpacingBefore(10f);
				Paragraph para13 = new Paragraph("".toUpperCase(), normal);
				para13.setSpacingBefore(10f);
//				doc.add(para12);
//				doc.add(para13);
//				loiTableReportData(doc, userId, applicationNumber, requestLOIReport);
				try {
					Paragraph preface0 = new Paragraph("(" + dtcpUserName + ", I.A.S)    ", smallBold);
					preface0.setIndentationLeft(10);
					preface0.setAlignment(Element.ALIGN_CENTER);
					preface0.setSpacingBefore(10f);
					Paragraph preface01 = new Paragraph("Director,", smallBold);
					preface01.setIndentationLeft(70);
					Paragraph preface02 = new Paragraph("Town & Country Planning", smallBold);
					preface02.setIndentationLeft(25);
					Paragraph preface3 = new Paragraph("Haryana Chandigarh", smallBold);
					preface3.setIndentationLeft(40);

					PdfDiv div = new PdfDiv();
					div.addElement(preface0);
					div.addElement(preface01);
					div.addElement(preface02);
					div.addElement(preface3);
					div.setPaddingLeft(320f);
					div.setFloatType(FloatType.RIGHT);
					doc.add(div);

					Paragraph preface4 = new Paragraph("Endst. " + memoNumber
							+ "/ 		                                    Dated: " + currentDate.split("\\s+")[0]);
					preface4.setAlignment(Element.ALIGN_LEFT);
					preface4.setSpacingBefore(10f);
					doc.add(preface4);
					Paragraph para102 = new Paragraph(
							"A copy is forwarded to the followings for information and necessary action:-", normal);
					para102.setSpacingBefore(10f);
					Paragraph para103 = new Paragraph("".toUpperCase(), normal);
					para103.setSpacingBefore(10f);
					doc.add(para102);
					doc.add(para103);

					List subList1 = new List(List.ORDERED);
					subList1.setIndentationLeft(30f);
					subList1.add(new ListItem("The Deputy Commissioner, Gurugram.", normal));
					subList1.add(new ListItem("The Additional Director, Urban Estate, Haryana, Sector-6 Panchkula.",
							normal));
					subList1.add(new ListItem("Senior Town Planner, Gurugram.", normal));
					subList1.add(new ListItem("District Town Planner, Gurugram. ", normal));
					doc.add(subList1);

					Paragraph preface001 = new Paragraph("(" + hqUserName + ")", normal);
					preface001.setAlignment(Element.ALIGN_RIGHT);

					Paragraph preface102 = new Paragraph("District Town Planner (HQ)", normal);
					preface102.setAlignment(Element.ALIGN_RIGHT);

					Paragraph preface23 = new Paragraph("For: Director, Town & Country Planning", normal);
					preface23.setAlignment(Element.ALIGN_RIGHT);

					Paragraph preface34 = new Paragraph("Haryana, Chandigarh.", normal);
					preface34.setAlignment(Element.ALIGN_RIGHT);

					PdfDiv div2 = new PdfDiv();
					div2.addElement(preface001);
					div2.addElement(preface102);
					div2.addElement(preface23);
					div2.addElement(preface34);
					div2.setFloatType(FloatType.RIGHT);
					doc.add(div2);
				} catch (Exception e) {
					log.error("Exception : "+e.getMessage());
				}

				doc.close();
				writer.close();
			} catch (DocumentException e1) {
				log.error("DocumentException : "+e1.getMessage());
			} catch (Exception e) {
				log.error("Exception : "+e.getMessage());
				
			}
		}
	}

	/****************************************************************
	 * LOI Purpose : Affordable Plotted Colony under Deen Dayal end *
	 ****************************************************************/

	/****************************************************
	 * 5: LOI Purpose : Commercial Plotted colony start *
	 ****************************************************/

	private void getLoiReportCommercialPlottedColony(String applicationNumber, String userId, String hqUserId,
			RequestLOIReport requestLOIReport) {
		LicenseServiceResponseInfo licenseServiceResponceInfo = checkApplicationIsValid(requestLOIReport,
				applicationNumber, userId, hqUserId);
		if (licenseServiceResponceInfo == null) {
			return;
		} else {
			try {
				String myFile = loireportPath;
				Document doc = new Document(PageSize.A4);
				PdfWriter writer = PdfWriter.getInstance(doc,
						new FileOutputStream(myFile + "loi-report-" + applicationNumber + ".pdf"));
				doc.open();
				addTitlePage(doc, 1);
				memoNumber = "LC- " + (licenseServiceResponceInfo.getCaseNumber() != null
						? licenseServiceResponceInfo.getCaseNumber().replaceAll("LC", "").split("-")[0]
						: "N/A") + "-JE (VA)- " + (applicationDate.split("\\s+")[0].split("\\.")[2]);

				int leftMarg = 70;
				Paragraph preface1 = new Paragraph();
				preface1.add(new Paragraph("To", smallBold));
				doc.add(preface1);
				addEmptyLine(preface1, 2);
				Paragraph preface2 = new Paragraph();
				preface2.setIndentationLeft(leftMarg);
				try {
					ApplicantInfo applicationInfo = licenseDetails.getApplicantInfo();
					preface2.add(new Paragraph(applicationInfo.getDevDetail().getAddInfo().getCompanyName()));
					preface2.add(new Paragraph(applicationInfo.getDevDetail().getAddInfo().getRegisteredAddress()));
//				preface2.add(new Paragraph((applicationInfo.getState() == null ? "N/A" : applicationInfo.getState())
//						+ "-" + applicationInfo.getDevDetail().getLicenceDetails().getPincode()));
				} catch (Exception e) {
					preface2.add(new Paragraph("_________  Pvt. Ltd.,"));
					preface2.add(new Paragraph("--, -----------, ----------,"));
					preface2.add(new Paragraph("---------------."));
					e.printStackTrace();
				}
				doc.add(preface2);
				
				Paragraph loiNumberParagraph = new Paragraph(
						"LOI Number : " + loiNumber);
				loiNumberParagraph.setIndentationLeft(leftMarg);
				loiNumberParagraph.setSpacingBefore(15f);
				doc.add(loiNumberParagraph);
				
				Paragraph paragraph = new Paragraph("Memo No. " + memoNumber
						+ "/ 		                                    Dated: " + currentDate.split("\\s+")[0]);
				paragraph.setIndentationLeft(leftMarg);
				paragraph.setSpacingBefore(15f);
				doc.add(paragraph);
				addEmptyLine(paragraph, 1);

				Paragraph preface12 = new Paragraph();
				preface12.add(new Paragraph(
						"Subject:         Letter of Intent:-grant of licence for setting up of Commercial Plotted Colony on the ",
						smallBold));
				preface12.setSpacingBefore(15f);
				doc.add(preface12);
				Paragraph preface13 = new Paragraph();
				preface13.add(new Paragraph(
						"land measuring " + totalArea + " acres in the revenue estate of " + address
								+ (!collaborationCompanyName.equals("N/A") ? (" " + collaborationCompanyName) : ("")),
						smallBold));
				preface13.setIndentationLeft(leftMarg);
				doc.add(preface13);

				Paragraph preface14 = new Paragraph();
				preface14.add(new Paragraph("Please refer to your application received on "
						+ applicationDate.split("\\s+")[0] + " on the above cited subject. ", smallBold));
				preface14.setIndentationLeft(leftMarg);
				preface14.setSpacingBefore(30f);
				doc.add(preface14);

				List subList2 = new RomanList();
				subList2.setPostSymbol(". ");
				subList2.setIndentationLeft(15f);
				List subList3 = new RomanList();// new List(List.ORDERED, List.ALPHABETICAL);
				subList3.setLowercase(List.LOWERCASE);
				subList3.setPostSymbol(". ");
				subList3.setIndentationLeft(15f);

				List list = new List(List.ORDERED);
				List list1 = new List(List.ORDERED);
				List subList = new List(List.ORDERED, List.ALPHABETICAL);
				subList.setLowercase(List.UPPERCASE);
				subList.setPostSymbol(". ");
				subList.setIndentationLeft(30f);
				Font zapfdingbats = new Font();
				Chunk bullet = new Chunk("\u2022 ", zapfdingbats);
				List subListI = new List(List.UNORDERED);
				subListI.setListSymbol(bullet);
				subListI.setIndentationLeft(30f);
				List subListE = new List(List.UNORDERED);
				subListE.setListSymbol(bullet);
				subListE.setIndentationLeft(30f);

				String content = "\t                  Your request for the grant of licence under section 3 of the Haryana Development and Regulation of Urban Areas Act, 1975 and the Haryana Development and Regulation of Urban Areas Rules, 1976 framed thereunder for the setting up of Commercial Plotted Colony on the land measuring "
						+ totalArea + " acres in the revenue estate of " + address
						+ " has been examined/considered by the Department under the policy issued vide "
//					+ "memo no PF-00/0000/0/00/0000-2TCP dated 00.00.0000 "
						+ "memo no " + memoNumber + " dated " + currentDate.split("\\s+")[0]
						+ " and it is proposed to grant license to you. However, before grant of licence, you are called upon to fulfill the following requirements/pre-requisites laid down in Rule 11 of the Haryana Development and Regulation of Urban Areas Rules, 1976 within a period of 60 days from the date of issuance of this notice, failing which the grant of license shall be refused.";
				Paragraph para1 = new Paragraph("", normal);
				addEmptyLine(para1, 1);
				list1.add(new ListItem(content, normal));
				list1.add(new ListItem(
						"To furnish the bank guarantee on account of Internal Development Charges for the amount calculated as under:- ",
						normal));
				doc.add(para1);
				
				
//				Double plottedInternalCost = 20 * Double.parseDouble(plottedComponent);
				Double comericalInternalCost = 50 * Double.parseDouble(comericalComponent);
//				Double totalInternalCost = plottedInternalCost + comericalInternalCost;
				Double bg25InternalPercentage = amountEDC * 0.25;
				
//				Double plottedExternalCost = zoneWiseEdcAmount * Double.parseDouble(plottedComponent);
				Double comericalExternalCost = zoneWiseEdcAmount * Double.parseDouble(comericalComponent);
//				Double totalExternalCost = plottedExternalCost + comericalExternalCost;
				Double bg25ExternalPercentage = comericalExternalCost * 0.25;
				

				subListI.add(new ListItem("Area under commercial colony "+comericalComponent+" acres @ ₹ 50.00 Lac per acre ", normal));
				subListI.add(new ListItem(""+comericalComponent+" X 50 Lac					= ₹ "+comericalInternalCost+" Lacs", normal));
				subListI.add(new ListItem("25% B.G. on account of IDW			= ₹ "+bg25InternalPercentage+" Lacs ", normal));

				subListE.add(new ListItem("Total Area under commercial component	="+comericalComponent+" acres", normal));
				subListE.add(new ListItem("Interim rate for EDC 				= ₹ "+zoneWiseEdcAmount+" Lac ", normal));
				subListE.add(new ListItem("Total cost of development				= ₹ "+comericalExternalCost+" Lacs", normal));
				subListE.add(new ListItem("25% bank guarantee required			= ₹ "+bg25ExternalPercentage+" Lacs", normal));

				subList.add(new ListItem("INTERNAL DEVELOPMENT WORKS (IDW):"));
				subList.add(subListI);

				subList.add(new ListItem("EXTERNAL DEVELOPMENT CHARGES (EDC):"));
				subList.add(subListE);
				list1.add(subList);
				doc.add(list1);

				list.add(new ListItem(
						"It is made clear that the Bank Guarantee of Internal Development Works has been worked out on the interim rates and you have to submit the additional Bank Guarantee if any, required at the time of approval of Service Plan/Estimate according to the approved layout plan. With an increase in the cost of construction and an increase in the number of facilities in the layout plan, you would be required to furnish an additional bank guarantee within 30 days on demand. In the event of increase of rates of external development charges, you will have to pay the enhanced rates of external development charges as finally determined and as and when demanded by the DTCP, Haryana and furnish additional bank guarantee and submit an undertaking in this regard.",
						normal));

				list.add(new ListItem(
						"To execute two agreements i.e. LC-IV and Bilateral Agreement on non-judicial stamp paper of Rs. 10/-. ",
						normal));
				subList2.add(new ListItem(
						"That the owner/developer shall integrate the bank account in which 70 percent allottee receipts are credited under Section-4(2)(I)(D) of the Real Estate Regulation and Development Act, 2016 with the online application/payment gateway of the Department, in such manner, so as to ensure that 10% of the total receipt from each payment made by an allottee is automatically deducted and gets credited to the EDC head in the State treasury.",
						normal));

				subList2.add(new ListItem(
						"That such 10% of the total receipt from each payment made by the allottee, which is received by the Department shall get automatically credited, on the date of receipt in the Government treasury against EDC dues.  ",
						normal));
				subList2.add(new ListItem(
						"That such 10% deduction shall continue to operate till the total EDC dues get recovered from the owner/developer.",
						normal));
				subList2.add(new ListItem(new ListItem(
						"The implementation of such mechanism shall, however, have no bearing on EDC instalment schedule conveyed to the owner/developer. The owner/developer shall continue to supplement such automatic EDC deductions with payments from its own funds to ensure that by the EDC instalments that are due for payment that paid as per the prescribed schedule. ",
						normal)));
				list.add(subList2);
				list.add(new ListItem("That you shall deposit an amount of " + licenseFees + "/-(" + licenseFeesInWord
						+ ") on account of balance of licence fees  & an amount of " + conversionCharges + "/-("
						+ conversionChargesInWord
						+ ") on account of conversion charges to be deposited online at website i.e. www.tcpharyana.gov.in",
						normal));
				list.add(new ListItem(
						"To submit an undertaking on non-judicial stamp paper of Rs. 10/- to the effect that:-",
						normal));
				subList3.add(new ListItem("That you will pay the Infrastructure Development Charges amounting to "
						+ stateInfrastructureDevelopmentCharges
						+ "/-@ Rs. "+zoneWiseEdcAmount+"(zone)/- per sq. mtr for the commercial area, in two equal installments. First Installment will be due within 60 days of grant of license and second Installment within six months of grant of license failing which 18% PA interest will be liable for the delayed period.",
						normal));
				subList3.add(new ListItem(
						"That area coming under the sector roads and restricted belt / green belt, if any, which forms part of licensed area and in lieu of which benefit to the extent permissible as per policy towards FAR is being granted, shall be transferred free of cost to the Govt.",
						normal));
				subList3.add(new ListItem(
						"That you shall maintain and upkeep of all roads, open spaces, if any for a period of five years from the date of issue of the completion certificate unless earlier relieved of this responsibility and thereupon to transfer all such roads, open spaces, and public health services free of cost to the Govt. or the local authority, as the case may be, in accordance with the provisions of Section 3(3)(a)(iii) of the Haryana Development and Regulation of Urban Areas Act, 1975.",
						normal));
				subList3.add(new ListItem(
						"That you shall construct portion of service road, internal circulation roads, if any forming the part of site area at your own cost and shall transfer the land falling within alignment of same free of cost to the Govt. u/s 3(3)(a)(iii) of the Haryana Development and Regulation of Urban Areas Act, 1975.",
						normal));
				subList3.add(new ListItem(
						"That you shall be liable to pay the actual rates of External Development Charges as and when determined and demanded as per prescribed schedule by the DTCP Haryana.",
						normal));
				subList3.add(new ListItem(
						"That you shall integrate the services with Haryana Shahari Vikas Pradhikaran services as and when made available. ",
						normal));
				subList3.add(new ListItem(
						"That you have not submitted any other application for grant of license for development of the said land or part thereof for any purpose under the provisions of the Haryana Development and Regulation of Urban Areas Act, 1975 or any application seeking permission for change of land use under the provision of the Punjab Scheduled Roads and Controlled Area Restrictions of Unregulated Development Act, 1963.",
						normal));
				subList3.add(new ListItem(
						"That you have understood that the development/construction cost of 24 m/18 m major internal roads is not included in the EDC rates and you shall pay the proportionate cost for acquisition of land, if any, alongwith the construction cost of 24 m/18 m wide major internal roads as and when finalized and demanded by the Department. ",
						normal));
				subList3.add(new ListItem(
						"That you shall obtain NOC/Clearance as per provisions of notification dated 14.09.06 issued by Ministry of Environment & Forest, Govt. of India before execution of development works at site.",
						normal));
				subList3.add(new ListItem(
						"That you shall make arrangements for water supply, sewerage, drainage etc. to the satisfaction of DTCP till these services are made available from External Infrastructure to be laid by Haryana Shahari Vikas Pradhikaran.",
						normal));
				subList3.add(new ListItem(
						"That the rain water harvesting system shall be provided as per Central Ground Water Authority Norms/Haryana Govt. notification as applicable.",
						normal));
				subList3.add(new ListItem(
						"That you shall make provision of solar power system as per guidelines of Haryana Renewable Energy Development Agency and shall make operational where applicable before applying for an Occupation Certificate.",
						normal));
				subList3.add(new ListItem(
						"That you shall use only LED fitting for internal lighting as well as campus lighting.",
						normal));
				subList3.add(new ListItem(
						"That you shall convey the ‘Ultimate Power Load Requirement’ of the project to the concerned power utility, with a copy to the Director, within two months period from the date of grant of license to enable provision of site in licensed land for Transformers/Switching Stations/Electric Sub Stations as per the norms prescribed by the power utility in the zoning plan of the project.",
						normal));
				subList3.add(new ListItem(
						"That you shall submit compliance of Rule 24, 26, 27 & 28 of Rules 1976 & Section 5 of Haryana Development and Regulation of Urban Areas Act, 1975, and shall inform account number and full particulars of the scheduled bank wherein you have to deposit thirty percentum of the amount from the floor/space holders/ shops/plots for meeting the cost of Internal Development Works in the colony. ",
						normal));
				subList3.add(new ListItem(
						"That you shall arrange power connection from UHBVNL/DHBVNL for electrification of the colony and shall install the electricity distribution infrastructure as per the peak load requirement of the colony for which licencee shall get the electrical (distribution) service plan/estimates approved from the agency responsible for installation of external electric services i.e. UHBVNL/DHBVNL and complete the same before obtaining completion certificate for the colony.",
						normal));
				subList3.add(new ListItem(
						"That you shall permit the Director or any other office authorized by him to inspect the execution of the layout and the development works in the colony and to carry out all directions issued by him for ensuring due compliance of the execution of the layout and development works in accordance with the license granted.",
						normal));
				subList3.add(new ListItem(
						"That you shall not give any advertisement for sale of commercial area before the approval of layout plan of the same.",
						normal));
				subList3.add(new ListItem(
						"That you shall pay the labour cess as per policy instructions issued by Haryana Government vide Memo No. Misc. 2057-5/25/2008/2TCP dated 25.02.2010. ",
						normal));
				subList3.add(new ListItem(
						"That you shall keep pace of construction atleast in accordance with sale agreement executed with the buyers of the floor/space holders/ shops/plots as and when scheme is launched.",
						normal));
				subList3.add(new ListItem(
						"You shall submit the additional bank guarantee, if any required at the time of approval of Service Plans/Estimate. With an increase in the cost of construction and increase in the number of facilities in Layout Plan, you would be required to furnish an additional bank guarantee within 30 days on demand. It is made clear that bank guarantee of Internal Development Works/EDC has been worked out on the interim rates.  ",
						normal));
				subList3.add(new ListItem(
						"That you shall specify the detail of calculations per Sqm/per sq ft, which is being demanded from the floor/space holders/ shops/plots owners on account of IDC/EDC, if being charged separately as per rates fixed by Govt.",
						normal));
				subList3.add(new ListItem(
						"That the provisions of the Real Estate (Regulation and Development) Act, 2016 and rules framed thereunder shall be followed by the applicant in letter and spirit.",
						normal));
				subList3.add(new ListItem(
						"That no pre-launch/sale of commercial site will be undertaken before approval of the layout plan/building plans.",
						normal));
				subList3.add(new ListItem(
						"That you shall execute the development works as per Environmental Clearance and company with the provisions of Environment Protection Act, 1986, Air (Prevention and Control of Pollution of Act 1981) and Water (Prevention and Control of Pollution of 1974). In case of any violation of the provisions of said statutes, applicant shall be liable for penal action by Haryana State Pollution Control Board or any other Authority Administering the said Act.",
						normal));

				list.add(subList3);
				list.add(new ListItem(
						"That you shall submit a certificate from the Deputy Commissioner/District Revenue Authority stating that there is no further sale of the land applied for licence till date and applicant companies/individual are owners of the land. ",
						normal));
				list.add(new ListItem(
						"That you shall complete the demarcation at site as per Layout Plan and submit the same in the office of District Town Planner, "
								+ disticName + " within 2 months from issuance of the licence.",
						normal));
				list.add(new ListItem(
						"The above demanded fee and charges are subject to audit and reconciliation of accounts.",
						normal));
//				if (requestLOIReport.getAddedContent() != null && !requestLOIReport.getAddedContent().isEmpty()) {
//					requestLOIReport.getAddedContent().stream().forEach(cd -> {
//						list.add(new ListItem(cd, normal));
//					});
//				}
				loiTableReportData(list, userId, applicationNumber, requestLOIReport);
				doc.add(list);
				doc.add(new Paragraph("DA/schedule of land.", normal));
				addEmptyLine(para1, 1);
				Paragraph para12 = new Paragraph("Your filled data for following field(s) has not been approved:",
						normal);
				para12.setSpacingBefore(10f);
				Paragraph para13 = new Paragraph("".toUpperCase(), normal);
				para13.setSpacingBefore(10f);
//				doc.add(para12);
//				doc.add(para13);
//				loiTableReportData(doc, userId, applicationNumber, requestLOIReport);

				try {
					Paragraph preface0 = new Paragraph("(" + dtcpUserName + ", I.A.S)    ", smallBold);
					preface0.setIndentationLeft(10);
					preface0.setAlignment(Element.ALIGN_CENTER);
					preface0.setSpacingBefore(10f);
					Paragraph preface01 = new Paragraph("Director,", smallBold);
					preface01.setIndentationLeft(70);
					Paragraph preface02 = new Paragraph("Town & Country Planning", smallBold);
					preface02.setIndentationLeft(25);
					Paragraph preface3 = new Paragraph("Haryana Chandigarh", smallBold);
					preface3.setIndentationLeft(40);

					PdfDiv div = new PdfDiv();
					div.addElement(preface0);
					div.addElement(preface01);
					div.addElement(preface02);
					div.addElement(preface3);
					div.setPaddingLeft(320f);
					div.setFloatType(FloatType.RIGHT);
					doc.add(div);

					Paragraph preface4 = new Paragraph("Endst. " + memoNumber
							+ "/ 		                                    Dated: " + currentDate.split("\\s+")[0]);
					preface4.setAlignment(Element.ALIGN_LEFT);
					preface4.setSpacingBefore(10f);
					doc.add(preface4);
					Paragraph para102 = new Paragraph(
							"A copy is forwarded to the followings for information and necessary action:-", normal);
					para102.setSpacingBefore(10f);
					Paragraph para103 = new Paragraph("".toUpperCase(), normal);
					para103.setSpacingBefore(10f);
					doc.add(para102);
					doc.add(para103);

					List subList1 = new List(List.ORDERED);
					subList1.setIndentationLeft(30f);
					subList1.add(new ListItem("Chief Administrator, HSVP, Sector-6, Panchkula.", normal));
					subList1.add(new ListItem("Director, Urban Estates, Sector-6, Panchkula..", normal));
					subList1.add(new ListItem("Senior Town Planner, Gurugram.", normal));
					subList1.add(new ListItem("District Town Planner, Gurugram.", normal));

					doc.add(subList1);

					Paragraph preface001 = new Paragraph("(" + hqUserName + ")", normal);
					preface001.setAlignment(Element.ALIGN_RIGHT);

					Paragraph preface102 = new Paragraph("District Town Planner (HQ)", normal);
					preface102.setAlignment(Element.ALIGN_RIGHT);

					Paragraph preface23 = new Paragraph("For: Director, Town & Country Planning", normal);
					preface23.setAlignment(Element.ALIGN_RIGHT);

					Paragraph preface34 = new Paragraph("Haryana, Chandigarh.", normal);
					preface34.setAlignment(Element.ALIGN_RIGHT);

					PdfDiv div2 = new PdfDiv();
					div2.addElement(preface001);
					div2.addElement(preface102);
					div2.addElement(preface23);
					div2.addElement(preface34);
					div2.setFloatType(FloatType.RIGHT);
					doc.add(div2);
				} catch (Exception e) {
					log.error("Exception : "+e.getMessage());
				}

				doc.close();
				writer.close();
			} catch (DocumentException e1) {
				log.error("DocumentException : "+e1.getMessage());
			} catch (Exception e) {
				log.error("Exception : "+e.getMessage());
				
			}
		}
	}

	/*************************************************
	 * : LOI Purpose : Commercial Plotted colony end *
	 *************************************************/

	/*****************************************************
	 * 7: LOI Purpose : Industrial Plotted Colony start *
	 *****************************************************/

	private void getLoiReportIndustrialPlottedColony(String applicationNumber, String userId, String hqUserId,
			RequestLOIReport requestLOIReport) {

		LicenseServiceResponseInfo licenseServiceResponceInfo = checkApplicationIsValid(requestLOIReport,
				applicationNumber, userId, hqUserId);
		if (licenseServiceResponceInfo == null) {
			return;
		} else {
			StringJoiner applicationDated = new StringJoiner("&");
			try {
				licenseServiceResponceInfo.getNewServiceInfoData().stream().forEach(license -> {
					applicationDated.add(applicationDate.split("\\s+")[0]);
				});
				String myFile = loireportPath;
				Document doc = new Document(PageSize.A4);
				PdfWriter writer = PdfWriter.getInstance(doc,
						new FileOutputStream(myFile + "loi-report-" + applicationNumber + ".pdf"));
				doc.open();
				addTitlePage(doc, 1);
				memoNumber = "LC- " + (licenseServiceResponceInfo.getCaseNumber() != null
						? licenseServiceResponceInfo.getCaseNumber().replaceAll("LC", "").split("-")[0]
						: "N/A") + "/Asstt.(MS)/22";// + (currentDate.split("\\s+")[0].split("\\.")[2]);
				int leftMarg = 70;
				Paragraph preface1 = new Paragraph();
				preface1.add(new Paragraph("To", smallBold));
				doc.add(preface1);
				
				Paragraph loiNumberParagraph = new Paragraph(
						"LOI Number : " + loiNumber);
				loiNumberParagraph.setIndentationLeft(leftMarg);
				loiNumberParagraph.setSpacingBefore(15f);
				doc.add(loiNumberParagraph);
				
				addEmptyLine(preface1, 2);
				Paragraph preface2 = new Paragraph();
				preface2.setIndentationLeft(leftMarg);
				try {
					ApplicantInfo applicationInfo = licenseDetails.getApplicantInfo();
					preface2.add(new Paragraph(applicationInfo.getDevDetail().getAddInfo().getCompanyName()
							+ (!collaborationCompanyName.equals("N/A")
									? (" In collaboration with " + collaborationCompanyName)
									: (""))));
					preface2.add(new Paragraph(applicationInfo.getDevDetail().getAddInfo().getRegisteredAddress()));
				} catch (Exception e) {
					preface2.add(new Paragraph("_________  Pvt. Ltd.,"));
					preface2.add(new Paragraph("--, -----------, ----------,"));
					preface2.add(new Paragraph("---------------."));
					e.printStackTrace();
				}
				doc.add(preface2);
				Paragraph paragraph = new Paragraph("Memo No. " + memoNumber
						+ "/ 		                                    Dated: " + currentDate.split("\\s+")[0]);
				paragraph.setIndentationLeft(leftMarg);
				paragraph.setSpacingBefore(15f);
				doc.add(paragraph);
				addEmptyLine(paragraph, 1);

				Paragraph preface12 = new Paragraph();
				preface12.add(new Paragraph(
						"Subject:         Revised Letter of Intent for grant of licence for setting up of ",
						smallBold));
				preface12.setSpacingBefore(15f);
				doc.add(preface12);
				Paragraph preface13 = new Paragraph();
				preface13.add(new Paragraph("Industrial Plotted Colony over an area measuring " + totalArea
						+ " acres  in the revenue estate of  " + address + "", smallBold));
				preface13.setIndentationLeft(leftMarg);
				doc.add(preface13);

				Paragraph preface14 = new Paragraph();
				preface14.add(new Paragraph(
						"Please refer to your application dated " + applicationDated + " on the above cited subject.",
						normal));
				preface14.setIndentationLeft(leftMarg);
				preface14.setSpacingBefore(20f);
				preface14.setSpacingAfter(20f);
				doc.add(preface14);
				new Paragraph("", normal);
				List subList2 = new RomanList();
//				List subList2 = new List(List.ORDERED, List.ALPHABETICAL);
				subList2.setLowercase(List.UPPERCASE);
				subList2.setPostSymbol(". ");
				subList2.setIndentationLeft(25f);
				List subList3 = new RomanList();
				subList3.setLowercase(List.LOWERCASE);
				subList3.setPostSymbol(") ");
				subList3.setPreSymbol("( ");
				subList3.setIndentationLeft(20f);
				List subList4 = new List(List.ALIGN_JUSTIFIED);
				subList4.setIndentationLeft(15f);
				List subList5 = new List();
				subList5.setIndentationLeft(25f);

				List list = new List(List.ORDERED);
				list.setFirst(2);
				List subList = new List(List.ORDERED);
				subList.setIndentationLeft(30f);

				Paragraph para1 = new Paragraph("", normal);
//				Paragraph content1 = new Paragraph("Your request for grant of licence under section 3 of the Haryana Development and Regulation  ",normal);
				String content = "\t                     Your request for grant of licence under section 3 of the Haryana Development and Regulation \nof Urban Areas Act, 1975 and Rules, 1976 framed there under for development of Industrial Plotted Colony over an area measuring "
						+ totalArea + " acres falling in the revenue estate of " + address
						+ " has been considered and it is proposed to grant a license for setting up of Industrial Plotted colony. You are, therefore, called upon to fulfill the following requirements/ pre-requisites laid down in Rule, 11 of the Haryana Development and Regulation of Urban Areas Rules, 1976 within a period of 60 days from the date of issue of this letter, failing which request for grant of license shall be refused.";
//				content1.setIndentationLeft(leftMarg);
//				doc.add(content1);
				doc.add(new Paragraph(content, normal));
				doc.add(new Paragraph(
						"1.  To furnish bank guarantees on account of External Development Charges and Internal Development Works  for the amount calculated as under:-",
						normal));
				
				
				
				
				
				
				Double industrialComponent = zoneWiseEdcAmount * Double.parseDouble(totalArea);
				Double industrialComponent50Percentage = industrialComponent*0.50; //A
				Double plottedComponentCost = zoneWiseEdcAmount * Double.parseDouble(plottedComponent); // B
				Double comericalComponentCost = zoneWiseEdcAmount * Double.parseDouble(comericalComponent); // C
				Double BCcomponentCost=plottedComponentCost+comericalComponentCost;//D
				Double ADcomponentCost=BCcomponentCost+industrialComponent50Percentage; // 
				Double ADcomponentCost25Percentage=ADcomponentCost*0.25; // 
				
				
				
				Double internalIndestrialComponentCost=50*Double.parseDouble(totalArea);
				Double internalPottedComponentCost=20*Double.parseDouble(plottedComponent);
				Double internalComericalComponentCost=50*Double.parseDouble(comericalComponent);
				Double totalIntrnalCost=internalIndestrialComponentCost+internalPottedComponentCost+internalComericalComponentCost;
				Double totalInternalCost25percentage=(totalIntrnalCost)*0.25;
				
				
				
				Chunk underline = new Chunk("External Development Charges:-");
				underline.setUnderline(0.1f, -1f);
				underline.setFont(normalBold);
				doc.add(underline);

				float[] ecolumnWidths = { 1, 11 };
				PdfPTable etable = new PdfPTable(ecolumnWidths);
				etable.setWidthPercentage(100);
				int ecolIndex = 0;
				java.util.List<String> ehList = Arrays.asList("Sr No", "EDC required ");
				for (String ecolumnTitle : ehList) {
					ecolIndex = ecolIndex + 1;

					PdfPCell header = new PdfPCell();
					header.setBackgroundColor(BaseColor.LIGHT_GRAY);
					header.setBorderWidth(2);
					header.setPhrase(new Phrase(ecolumnTitle));
					etable.addCell(header);
				}
				etable.addCell("1");
				String edcRecorde = "Industrial component= "+totalArea+"x "+zoneWiseEdcAmount+" (EDC rate) = "+industrialComponent+" lacs\n"
						+ "\n50% EDC Required = "+industrialComponent50Percentage+" lacs ……… (A)\n"
						+ "\nPlotted component = "+plottedComponent+" x "+zoneWiseEdcAmount+" (EDC rate) = "+plottedComponentCost+" lacs…..(B)\n"
						+ "\nCommercial component = "+comericalComponent+" x "+zoneWiseEdcAmount+" (EDC rate) = "+comericalComponentCost+" lacs…… (C)\n"
						+ "\nTotal (B+C)= Rs. "+BCcomponentCost+" lacs……….. (D)\n"
						+ "\nTotal EDC required (A+D)= Rs. "+ADcomponentCost+" lacs\n"
						+ "\n25% BG required  = Rs. "+ADcomponentCost25Percentage+" lacs ";
				etable.addCell(edcRecorde);
				doc.add(etable);

				Chunk iunderline = new Chunk("Internal Development Works:-");
				iunderline.setUnderline(0.1f, -1f);
				iunderline.setFont(normalBold);
				doc.add(iunderline);

				float[] icolumnWidths = { 1, 2, 6, 3 };
				PdfPTable itable = new PdfPTable(icolumnWidths);
				itable.setWidthPercentage(100);
				int icolIndex = 0;
				java.util.List<String> ihList = Arrays.asList("Sr No", "Particulars", "Total IDW Cost",
						"25% BG to be demanded in the LOI");
				for (String icolumnTitle : ihList) {
					icolIndex = icolIndex + 1;

					PdfPCell header = new PdfPCell();
					header.setBackgroundColor(BaseColor.LIGHT_GRAY);
					header.setBorderWidth(2);
					header.setPhrase(new Phrase(icolumnTitle));
					itable.addCell(header);
				}

				itable.addCell("1");
				itable.addCell("IDW BG");
				String idcRecorde = "Industrial=  "+totalArea+"x 50 lakh = Rs. "+internalIndestrialComponentCost+" lakh\n"
						+ "\nPlotted = "+plottedComponent+" x 20 lakh = Rs. "+internalPottedComponentCost+" lakh\n"
						+ "\nComm.= "+comericalComponent+" x 50 lakh = Rs. "+internalComericalComponentCost+" lakh";
				itable.addCell(idcRecorde);
				itable.addCell("Rs. "+totalInternalCost25percentage+" lacs");

				itable.addCell("");
				itable.addCell("Total");
				itable.addCell("Rs. "+totalIntrnalCost+" lakh");
				itable.addCell("Rs. "+totalInternalCost25percentage+" lacs");
				doc.add(itable);

				doc.add(new Paragraph(
						"It is made clear that bank guarantee of Internal Development Works has been worked out on the interim rates and you will have to submit the additional bank guarantee, if any required at the time of approval of Service Plan/Estimate. With an increase in the cost of construction, you would be required to furnish an additional bank guarantee within 30 days on demand.",
						normal));

				list.add(new ListItem("To deposit an amount of " + scrutinyFee + "/-(" + scrutinyFeeInWord
						+ ") on account of deficit scrutiny fee, " + licenseFees + "/-(" + licenseFeesInWord
						+ ") on account of balance licence fee and " + conversionCharges + "/-("
						+ conversionChargesInWord + ") on account of conversion charges before grant of licence. ",
						normal));
				list.add(new ListItem(
						"To execute two agreements i.e. LC-IV & LC-IV-C (Bilateral Agreement) on Non-Judicial Stamp Paper of Rs. 10/-. Specimen copies of the said agreements are enclosed herewith for necessary action. Further, following additional clauses shall be added in LC-IV agreement as per Government instruction dated 14.08.2020:-",
						normal));

				subList2.add(new ListItem(new ListItem(
						"That the owner/developer shall integrate the bank account in which 70 percent allottee receipts are credited under Section-4(2)(I)(D) of the Real Estate Regulation and Development Act, 2016 with the online application/payment gateway of the Department, in such manner, so as to ensure that 10% of the total receipt from each payment made by an allottee is automatically deducted and gets credited to the EDC head in the State treasury.",
						normalItalic)));

				subList2.add(new ListItem(new ListItem(
						"That such 10% of the total receipt from each payment made by the allottee, which is received by the Department shall get automatically credited, on the date of receipt in the Government treasury against EDC dues.  ",
						normalItalic)));
				subList2.add(new ListItem(new ListItem(
						"That such 10% deduction shall continue to operate till the total EDC dues get recovered from the owner/developer.",
						normalItalic)));
				subList2.add(new ListItem(new ListItem(
						"The implementation of such mechanism shall, however, have no bearing on EDC instalment schedule conveyed to the owner/developer. The owner/developer shall continue to supplement such automatic EDC deductions with payments from its own funds to ensure that by the EDC instalments that are due for payment that paid as per the prescribed schedule. ",
						normalItalic)));
				list.add(subList2);

				list.add(new ListItem(
						"To furnish an undertaking on non judicial stamp paper to the following effect that:-     ",
						normal));

				subList3.add(new ListItem(new ListItem("To deposit a sum of " + stateInfrastructureDevelopmentCharges
						+ "/-(" + stateInfrastructureDevelopmentChargesInWord
						+ ") on account of Infrastructural Development Charges in two equal installments. First within 60 days from issuance of license and second within six months online at www.tcpharyana.gov.in. In failure of which, an interest @ 18% per annum for delay period shall be paid.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"You shall maintain and upkeep of all roads, open spaces, public park and public health services for a period of five years from the date of issue of the completion certificate unless earlier relieved of this responsibility and thereupon to transfer all such roads, open spaces, public parks and public health services free of cost to the Govt. or the local authority, as the case may be, in accordance with the provisions of Section 3(3)(a)(iii) of the Haryana Development and Regulation of Urban Areas Act, 1975.",
						normal)));

				subList3.add(new ListItem(new ListItem(
						"You have not submitted any other application for grant of license for development of the said land or part thereof for any purpose under the provisions of the Haryana Development and Regulation of Urban Areas Act, 1975 or any application seeking permission for change of land use under the provision of the Punjab Scheduled Roads and Controlled Areas Restriction of Unregulated Development Act, 1963.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"You shall obtain NOC/Clearance as per provisions of notification dated 14.09.2006 issued by Ministry of Environment & Forest, Govt. of India, if applicable before execution of development works at site.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"You shall make your own arrangements for water supply, sewerage, drainage etc. to the satisfaction of DTCP.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"You shall obtain clearance from competent authority, if required under Punjab Land Preservation Land Act, 1900 and any other clearance required under any other law.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That the rain water harvesting system shall be provided as per Central Ground Water Authority Norms/Haryana Govt. notification as applicable.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That the provision of solar water heating system shall be as per guidelines of Haryana Renewable Energy Development Agency and shall be made operational where applicable before applying for an Occupation Certificate.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"You shall use only LED fitting for internal lighting as well as campus lighting.", normal)));
				subList3.add(new ListItem(new ListItem(
						"You shall convey the ‘Ultimate Power Load Requirement’ of the project to the concerned power utility, with a copy to the Director, within two months period from the date of grant of license to enable provision of site in licensed land for Transformers/Switching Stations/Electric Sub Stations as per the norms prescribed by the power utility in the zoning plan of the project.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"You shall keep pace of development atleast in accordance with sale agreement executed with the buyers of the plots as and when scheme is launched. ",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"You shall arrange power connection from UHBVNL/DHBVNL for electrification of the colony and shall install the electricity distribution infrastructure as per the peak load requirement of the colony for which licencee shall get the electrical (distribution) service plan/estimates approved from the agency responsible for installation of external electric services i.e. UHBVNL/DHBVNL and complete the same before obtaining completion certificate for the colony. ",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That you will pay the labour cess as per policy instructions issued by Haryana Government vide Memo No. Misc. 2057-5/25/2008/2TCP dated 25.02.2010. ",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"You shall submit compliance of Rule 24, 26, 27 & 28 of Rules 1976 & Section 5 of Haryana Development and Regulation of Urban Areas Act, 1975, and shall inform account number and full particulars of the scheduled bank wherein you have to deposit thirty percentum of the amount received from the plot holders for meeting the cost of Internal Development Works in the colony. ",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That no further sale has taken place after submitting application for grant of license. ",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"You shall not give any advertisement for sale of plots/commercial area before the approval of layout plan.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That no provision of the Haryana Ceiling on Land Holding Act, 1972 has been violated due to purchase of applied land. ",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That the revenue rasta/khal if passing through the site shall not be encroached upon and shall be kept free from all hindrances for easy movement of general public. ",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That the licencee shall follow the provisions of the Real Estate (Regulations and Development) Act, 2016 and Rules framed thereunder shall be followed by the applicant in letter and spirit.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"You shall comply with the terms and conditions of policy dated 01.10.2015, 09.03.2019 and other direction given by the Director time to time to execute the project.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"You shall obtain the permission from competent authority for construction of culvert over dhana/ nala / drain / water channel what so ever, if passing through licenced land.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"You shall integrate the services with Haryana Shehri Vikas Pradhikaran Development Authority services as and when made available. ",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That area coming under the sector roads and restricted belt / green belt, if any, which forms part of licensed area and in lieu of which benefit to the extent permissible as per policy towards FAR is being granted, shall be transferred free of cost to the Govt.",
						normal)));

				subList3.add(new ListItem(new ListItem(
						"You will transfer 10% area of the licenced colony free of cost to the Government for provision of community facilities. This will give flexibility to the Director to workout the requirement of community infrastructure at sector level and accordingly make provisions. The said area will be earmarked on the layout plan to be approved alongwith the license. ",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"You understand that the development/ construction cost of 24 m/18 m major internal roads is not included in the EDC rates and they shall pay the proportionate cost for acquisition of land, if any, alongwith the construction cost of 24 m/18 m wide major internal roads as and when finalized and demanded by the Department. ",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That it will be made clear at the time of booking of plots/commercial space that specified rates include or do not include EDC. In case of non inclusion of EDC in the booking rates, then it may be specified that same are to be charged separately as per rate fixed by the Govt. applicant company shall also provide detail of calculation of EDC per sqm/per sft to the allottees while raising such demand from the plot owners.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That no clubbing of residential plots for approval of integrated zoning plan of two adjoining plots under same ownership shall be permitted.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"You shall abide by the terms and conditions of the policy notified on 01.04.2016.", normal)));
				subList3.add(new ListItem(new ListItem(
						"You shall abide by the terms and conditions of policy dated 08.02.2016 (DDJAY) and other direction given by the Director time to time to execute the project.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"You shall execute the development works as per Environmental Clearance and company with the provisions of Environment Protection Act, 1986, Air (Prevention and Control of Pollution of Act 1981) and Water (Prevention and Control of Pollution of 1974). In case of any violation of the provisions of said statutes, applicant shall be liable for penal action by Haryana State Pollution Control Board or any other Authority Administering the said Act.",
						normal)));
				list.add(subList3);
//				list.add(new ListItem(
//						"That you shall demolish the unauthorised construction of labour hut at Pocket E before issuance of final permission. ",
//						normal));
//
//				list.add(new ListItem(
//						"That you shall submit the undertaking to maintain the Right of Way of Gail Gas Pipe Line passes through the Pocket G. ",
//						normal));
				list.add(new ListItem(
						"That certificate from DRO/Deputy Commissioner, Gurugram will be submitted certifying that the applied land is still under ownership of applicant company.",
						normal));
//				list.add(new ListItem(
//						"That you shall submit the NOC from Divisional Forest Officer, Gurugram before grant of final permission.",
//						normal));
				list.add(new ListItem(
						"That you shall submit an indemnity bond indemnifying DTCP from any loss, if occurs due to submission of undertaking submitted in respect of non-creation of third party rights on the applied land.",
						normal));
				list.add(new ListItem(
						"That you shall undertake to indemnify State Govt. / Department for loss occurred or legal complication arising due to pending litigation and the land owning / developer company will be responsible for the same in respect of applied land.",
						normal));

//				list.add(new ListItem(
//						"That you shall submit an affidavit duly attested by 1st Class Magistrate, to the effect that applicants have not submitted any other application for grant of licence for development of the said land or part thereof for any purpose under the provisions of the Haryana Development and Regulation of Urban Areas Act, 1975 or any application seeking permission for change of land use under the provision of the Punjab Schedule Roads and Controlled Areas restrictions of Unregulated Development Act, 1963 or have not applied for licence/ permission under any other law for the time being in force. ",
//						normal));
//				list.add(new ListItem(
//						"That you shall submit an undertaking from the land owning companies/ owners that this land has not been sold to any person after entering into collaboration agreement with the colonizer to whom LOI is being issued and also that presently there is no collaboration agreement enforced with any other person for the same land.",
//						normal));
//				list.add(new ListItem(
//						"That you shall submit an undertaking that get the layout plan revised as and when final alignment of 60 mtr wide sector dividing road of Sector M9 & M14 is approved.",
//						normal));
//				list.add(new ListItem(
//						"That you shall furnish addendum agreement in continuation of the collaboration agreement submitted by "+collaborationCompanyName+" to the effect that:-",
//						normal));			
//				subList5.add(new ListItem(
//						"a)	 "+collaborationCompanyName+" shall be responsible for compliance of all terms and conditions of licence/ provisions of Act 8 of 1975 and Rules 1976 till the grant of final completion certificate to the colony or relieved of the responsibility by the DTCP, Haryana whichever is earlier.",
//						normal));
//				subList5.add(new ListItem(
//						"b)	 The said agreement is still valid and shall be irrevocable and no modification/ alteration etc. in the terms and conditions of the said agreement can be undertaken, except after obtaining prior approval of DTCP, Haryana.",
//						normal));
//				list.add(subList5);
//				list.add(new ListItem(
////						"That you shall increase the paid up capital of the company to the tune of Rs. 20 Crores before final permission. ",
//						"That you shall increase the paid up capital of the company to the tune of "+totalFee+"/-("+totalFeeInWord+") before final permission. ",
//						normal));
//				
//				list.add(new ListItem(
//						"That you shall submit the NOC of the Industries Department /HSIIDC for setting up of Industrial Township & having access to the site from the constructed 40 karam wide road. ",
//						normal));
//				list.add(new ListItem(
//						"That you will intimate their official Email ID and the correspondence on this email ID by the Deptt. will be treated as receipt of such correspondence.",
//						normal));
//				list.add(new ListItem(
//						"The above demanded fee and charges are subject to audit and reconciliation of accounts.",
//						normal));
//				
//				list.add(new ListItem(
//						"That you shall submit the non-encumbrance certificate issued by the revenue authority.", normal));
//				if (requestLOIReport.getAddedContent() != null && !requestLOIReport.getAddedContent().isEmpty()) {
//					requestLOIReport.getAddedContent().stream().forEach(cd -> {
//						list.add(new ListItem(cd, normal));
//					});
//				}
				loiTableReportData(list, userId, applicationNumber, requestLOIReport);
				doc.add(list);

				addEmptyLine(para1, 1);
//				String note = "Note:- That you will intimate your official “email ID” to the Department and correspondence done by Department on this ID shall be treated as official intimation & legally valid.";
//				Paragraph para10 = new Paragraph(note, small);
//				para10.setSpacingBefore(10f);
//				doc.add(para10);
//				doc.add(new Paragraph("DA/schedule of land. ", normal));
				addEmptyLine(para1, 1);
				Paragraph para12 = new Paragraph("Your filled data for following field(s) has not been approved:",
						normal);
				// You are requested to complete the following shortcomings immediately
				para12.setSpacingBefore(10f);
				Paragraph para13 = new Paragraph("".toUpperCase(), normal);
				para13.setSpacingBefore(10f);
//				doc.add(para12);
//				doc.add(para13);
//				loiTableReportData(doc, userId, applicationNumber, requestLOIReport);
				try {
					Paragraph preface0 = new Paragraph("(" + dtcpUserName + ", I.A.S)    ", smallBold);
					preface0.setIndentationLeft(10);
					preface0.setAlignment(Element.ALIGN_CENTER);
					preface0.setSpacingBefore(10f);
					Paragraph preface01 = new Paragraph("Director,", smallBold);
					preface01.setIndentationLeft(70);
					Paragraph preface02 = new Paragraph("Town & Country Planning", smallBold);
					preface02.setIndentationLeft(25);
					Paragraph preface3 = new Paragraph("Haryana Chandigarh", smallBold);
					preface3.setIndentationLeft(40);

					PdfDiv div = new PdfDiv();
					div.addElement(preface0);
					div.addElement(preface01);
					div.addElement(preface02);
					div.addElement(preface3);
					div.setPaddingLeft(320f);
					div.setFloatType(FloatType.RIGHT);
					doc.add(div);

					Paragraph preface4 = new Paragraph("Endst. " + "Endst. No LC-4711/Asstt. MS/2022"
							+ "/ 		                                    Dated: " + currentDate.split("\\s+")[0]);
					preface4.setAlignment(Element.ALIGN_LEFT);
					preface4.setSpacingBefore(10f);
					doc.add(preface4);
					Paragraph para102 = new Paragraph(
							"A copy is forwarded to the followings for information and necessary action:-", normal);
					para102.setSpacingBefore(10f);
					Paragraph para103 = new Paragraph("".toUpperCase(), normal);
					para103.setSpacingBefore(10f);
					doc.add(para102);
					doc.add(para103);

					List subList1 = new List(List.ORDERED);
					subList1.setIndentationLeft(30f);
					subList1.add(new ListItem("Deputy Commissioner, Gurugram. ", normal));
					subList1.add(new ListItem("District Revenue Officer, Gurugram.", normal));
					subList1.add(new ListItem("Senior Town Planner, Gurugram.", normal));
					subList1.add(new ListItem("District Town Planner, Gurugram.", normal));
					doc.add(subList1);

					Paragraph preface001 = new Paragraph("(" + hqUserName + ")", normal);
					preface001.setAlignment(Element.ALIGN_RIGHT);

					Paragraph preface102 = new Paragraph("District Town Planner (HQ)", normal);
					preface102.setAlignment(Element.ALIGN_RIGHT);

					Paragraph preface23 = new Paragraph("For: Director, Town & Country Planning", normal);
					preface23.setAlignment(Element.ALIGN_RIGHT);

					Paragraph preface34 = new Paragraph("Haryana, Chandigarh.", normal);
					preface34.setAlignment(Element.ALIGN_RIGHT);

					PdfDiv div2 = new PdfDiv();
					div2.addElement(preface001);
					div2.addElement(preface102);
					div2.addElement(preface23);
					div2.addElement(preface34);
					div2.setFloatType(FloatType.RIGHT);
					doc.add(div2);
				} catch (Exception e) {
					log.error("Exception : "+e.getMessage());
				}

				doc.close();
				writer.close();
			} catch (FileNotFoundException e1) {
				log.error("FileNotFoundException : "+e1.getMessage());
			} catch (DocumentException e1) {
				log.error("DocumentException : "+e1.getMessage());
			} catch (Exception e) {
				log.error("Exception : "+e.getMessage());
				
			}

		}
	}

	/*****************************************************
	 * : LOI Purpose : Industrial Plotted Colony end *
	 *****************************************************/

	/*****************************************************
	 * 8: LOI Purpose : IT Park/Cyber Park start *
	 *****************************************************/

	private void getLoiReportITCyberCity(String applicationNumber, String userId, String hqUserId,
			RequestLOIReport requestLOIReport) {

		LicenseServiceResponseInfo licenseServiceResponceInfo = checkApplicationIsValid(requestLOIReport,
				applicationNumber, userId, hqUserId);
		if (licenseServiceResponceInfo == null) {
			return;
		} else {

			try {
				String myFile = loireportPath;
				Document doc = new Document(PageSize.A4);
				PdfWriter writer = PdfWriter.getInstance(doc,
						new FileOutputStream(myFile + "loi-report-" + applicationNumber + ".pdf"));
				doc.open();
				addTitlePage(doc, 1);
				memoNumber = "LC- " + (licenseServiceResponceInfo.getCaseNumber() != null
						? licenseServiceResponceInfo.getCaseNumber().replaceAll("LC", "").split("-")[0]
						: "N/A") + "/JE (DS) 2022";// + (currentDate.split("\\s+")[0].split("\\.")[2]);
				int leftMarg = 70;
				Paragraph preface1 = new Paragraph();
				preface1.add(new Paragraph("To", smallBold));
				doc.add(preface1);
				
				Paragraph loiNumberParagraph = new Paragraph(
						"LOI Number : " + loiNumber);
				loiNumberParagraph.setIndentationLeft(leftMarg);
				loiNumberParagraph.setSpacingBefore(15f);
				doc.add(loiNumberParagraph);
				
				addEmptyLine(preface1, 2);
				Paragraph preface2 = new Paragraph();
				preface2.setIndentationLeft(leftMarg);
				try {
					ApplicantInfo applicationInfo = licenseDetails.getApplicantInfo();
					preface2.add(new Paragraph(applicationInfo.getDevDetail().getAddInfo().getCompanyName()));
					preface2.add(new Paragraph(applicationInfo.getDevDetail().getAddInfo().getRegisteredAddress()));
//				preface2.add(new Paragraph((applicationInfo.getState()==null?"N/A":applicationInfo.getState())+"-"+applicationInfo.getDevDetail().getLicenceDetails().getPincode()));
				} catch (Exception e) {
					preface2.add(new Paragraph("_________  Pvt. Ltd.,"));
					preface2.add(new Paragraph("--, -----------, ----------,"));
					preface2.add(new Paragraph("---------------."));
					e.printStackTrace();
				}
				doc.add(preface2);
				Paragraph paragraph = new Paragraph("Memo No. " + memoNumber
						+ "/ 		                                    Dated: " + currentDate.split("\\s+")[0]);
				paragraph.setIndentationLeft(leftMarg);
				paragraph.setSpacingBefore(15f);
				doc.add(paragraph);
				addEmptyLine(paragraph, 1);

				Paragraph preface12 = new Paragraph();
				preface12.add(new Paragraph(
						"Subject:         Letter of Intent - Request for grant of licence for setting up of an IT Cyber City/Park",
						smallBold));
				preface12.setSpacingBefore(15f);
				doc.add(preface12);
				Paragraph preface13 = new Paragraph();
				preface13.add(new Paragraph(
						"over an area measuring " + totalArea + " acres in the revenue estate of " + address
								+ (!collaborationCompanyName.equals("N/A") ? ("-" + collaborationCompanyName) : ("")),
						smallBold));
				preface13.setIndentationLeft(leftMarg);
				doc.add(preface13);

				Paragraph preface14 = new Paragraph();
				preface14.add(new Paragraph("Please refer to your application dated " + applicationDate.split("\\s+")[0]
						+ " on the above cited subject.", normal));
				preface14.setIndentationLeft(leftMarg);
				preface14.setSpacingBefore(20f);
				preface14.setSpacingAfter(20f);
				doc.add(preface14);

				List subList2 = new RomanList();// (List.ORDERED, List.ALPHABETICAL);
				subList2.setLowercase(List.UPPERCASE);
				subList2.setPostSymbol(". ");
				subList2.setIndentationLeft(25f);

				List subList3 = new RomanList();// new List(List.ORDERED, List.ALPHABETICAL);
				subList3.setLowercase(List.LOWERCASE);
				subList3.setPostSymbol(". ");
				subList3.setIndentationLeft(15f);

				List list = new List(List.ORDERED);
				list.setFirst(2);
				List subList = new List(List.ORDERED);
				subList.setIndentationLeft(30f);
				String content1 = "Your request for the grant of license under section 3 of the Haryana Development and";
				String content2 = "Regulation of Urban Areas Act, 1975 and the Haryana Development and Regulation of Urban Areas Rules, 1976 framed thereunder for the development of an IT Park/Cyber Park over an area measuring "
						+ totalArea + " acres in the revenue estate of " + address
						+ " has been examined/considered by the Department under the policy dated 08.02.2016 and it is proposed to grant license to you. However, before grant of licence, you are called upon to fulfill the following requirements/pre-requisites laid down in Rule 11 of the Haryana Development and Regulation of Urban Areas Rules, 1976 within a period of 60 days from the date of issuance of this notice, failing which the grant of license shall be refused.";
				Paragraph para1 = new Paragraph("", normal);
				Paragraph c1 = new Paragraph(content1, normal);
				c1.setIndentationLeft(leftMarg);
				doc.add(c1);
				doc.add(new Paragraph(content2, normal));
				doc.add(new Paragraph(
						"1.  To furnish the bank guarantees on account of Internal Development Charges and the External Development Charges for the amount calculated as under:-",
						normal));
				doc.add(new Paragraph("A).  External Development Charges: ", normalBold));
				
				
				
					
				Double totalItCost=Double.parseDouble(totalArea)*zoneWiseEdcAmount;
				Double totalComericalCost=Double.parseDouble(comericalComponent)*(zoneWiseEdcAmount);
				Double totalEdc=totalComericalCost+totalItCost;
				Double bg25ExternalPercentage = totalEdc * 0.25;
				
				
				Double plottedInternalCost = 50 * Double.parseDouble(plottedComponent);
				
				Double comericalInternalCost = 50 * Double.parseDouble(comericalComponent);
				Double totalInternalCost = plottedInternalCost + comericalInternalCost;
				Double grandTotal=communityFacultyCost+totalInternalCost;
				Double bg25InternalPercentage = grandTotal * 0.25;
				
				
				

				

				
				
				preface12.setSpacingBefore(15f);
				String externalRecord = "" + "i)\t    Total Area under IT				= "+totalArea+" acres\r\n"
						+ "ii)\t   Interim rate for EDC				= ₹ "+zoneWiseEdcAmount+" Lac per acres\r\n"
						+ "iii)\t  Total cost for IT Component			= ₹ "+totalItCost+" Lac\r\n"
						+ "iv)\t   Area under commercial component		= "+comericalComponent+" acre\r\n"
						+ "v)\t    Interim rate of EDC				= ₹ "+zoneWiseEdcAmount+" Lac per acre\r\n"
						+ "vi)\t   Total cost of Comm. Component		= ₹ "+totalComericalCost+" Lac\r\n"
						+ "vii\t   Total EDC 					= ₹ "+totalEdc+" Lac\r\n"
						+ "viii)\t Bank Guarantee required			= ₹ "+bg25ExternalPercentage+" lacs\r\n"
						+ " 							    			 (valid for 5 years)\r\n" + "";
				Paragraph extParagraph = new Paragraph(externalRecord, normal);
				extParagraph.setIndentationLeft(30f);
				doc.add(extParagraph);

				doc.add(new Paragraph("B).  Internal Development Works: ", normalBold));
				String internalRecord = "" + "i)\t    Plotted Area					= "+plottedComponent+" acres\r\n"
						+ "ii)\t   Interim rate for development		= ₹ 50.00 Lac per acre\r\n"
						+ "iii)\t  Plotted cost 					= ₹ "+plottedInternalCost+" Lacs	\r\n"
						+ "iv)\t   Commercial Area				= "+comericalComponent+" acre\r\n"
						+ "v)\t    Interim rate for development		= ₹ 50.00 Lac per acre\r\n"
						+ "vi)\t   Comm. Cost.					= ₹ "+comericalInternalCost+" Lacs\r\n"
						+ "vii)\t  Total cost of development 			= ₹ "+totalInternalCost+" Lac\r\n"
						+ "viii)\t Cost of community facilities			= ₹ "+communityFacultyCost+"\r\n"
						+ "ix)\t   Grand Total					= ₹ "+grandTotal+" Lac\r\n"
						+ "x)\t    25% bank guarantee required			= ₹ "+bg25InternalPercentage+" Lacs \r\n"
						+ "									(valid for 5 years)\r\n" + "";
				Paragraph intParagraph = new Paragraph(internalRecord, normal);
				intParagraph.setIndentationLeft(30f);
				doc.add(intParagraph);
				list.add(new ListItem(
						"It is made clear that the Bank Guarantee of Internal Development Works has been worked out on the interim rates and you have to submit the additional Bank Guarantee if any, required at the time of approval of Service Plan/Estimate according to the approved building plan. With an increase in the cost of construction and an increase in the number of facilities in the building plan, you would be required to furnish an additional bank guarantee within 30 days on demand. In the event of increase of rates of external development charges, you will have to pay the enhanced rates of external development charges as finally determined and as and when demanded by the DTCP, Haryana and furnish additional bank guarantee and submit an undertaking in this regard.",
						normal));
				list.add(new ListItem("That you shall deposit an amount of " + licenseFees + "/-(" + licenseFeesInWord
						+ ") on account of balance license fee and an amount of " + conversionCharges + "/-("
						+ conversionChargesInWord
						+ ") on account of conversion charges to be deposited online at website i.e. www.tcpharyana.gov.in.",
						normal));

				list.add(new ListItem(
						"To execute two agreements i.e. LC-IV & LC-IV-A Bilateral Agreement on Non-Judicial Stamp Paper of Rs. 10/-. Specimen copies of the said agreements are enclosed herewith for necessary action. Further, following additional clauses shall be added in LC-IV agreement as per Government instruction dated 14.08.2020.",
						normal));

				subList2.add(new ListItem(new ListItem(
						" That the owner/developer shall integrate the bank account in which 70 percent allottee receipts are credited under Section-4(2)(I)(D) of the Real Estate Regulation and Development Act, 2016 with the online application/payment gateway of the Department, in such manner, so as to ensure that 10% of the total receipt from each payment made by an allottee is automatically deducted and gets credited to the EDC head in the State treasury.",
						normal)));

				subList2.add(new ListItem(new ListItem(
						"That such 10% of the total receipt from each payment made by the allottee, which is received by the Department shall get automatically credited, on the date of receipt in the Government treasury against EDC dues.  ",
						normal)));
				subList2.add(new ListItem(new ListItem(
						"That such 10% deduction shall continue to operate till the total EDC dues get recovered from the owner/developer.",
						normal)));
				subList2.add(new ListItem(new ListItem(
						"The implementation of such mechanism shall, however, have no bearing on EDC instalment schedule conveyed to the owner/developer. The owner/developer shall continue to supplement such automatic EDC deductions with payments from its own funds to ensure that by the EDC instalments that are due for payment that paid as per the prescribed schedule. ",
						normal)));
				list.add(subList2);
				list.add(new ListItem(
						"To furnish an undertaking on non-judicial stamp paper of Rs. 10/- to the\nfollowing effect:- ",
						normal));

				subList3.add(new ListItem(new ListItem(
						"That you will pay the Infrastructure Development Charges amounting to "
								+ stateInfrastructureDevelopmentCharges + "/-("
								+ stateInfrastructureDevelopmentChargesInWord
								+ ") in two equal installments. First Installment will be due within 60 days of grant of license and second Installment within six months of grant of license failing which 18% PA interest will be liable for the delayed period.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That you shall maintain and upkeep of all roads, open spaces, public park and public health services for a period of five years from the date of issue of the completion certificate unless earlier relieved of this responsibility and thereupon to transfer all such roads, open spaces, public parks and public health services free of cost to the Govt. or the local authority, as the case may be, in accordance with the provisions of Section 3(3)(a)(iii) of the Haryana Development and Regulation of Urban Areas Act, 1975.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That you shall construct portion of sector/service road, internal circulation roads, forming the part of site area at your own cost and shall transfer the land falling within alignment of same free of cost to the Govt. u/s 3(3) (a) (iii) of the Haryana Development and Regulation of Urban Areas Act, 1975.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That you shall permit the Director or any other officer authorized by him to inspect the execution of the layout and the development works in the colony and to carry out all directions issued by him for ensuring due compliance of the execution of the layout and development works in accordance with the license granted. ",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That area coming under the sector road/green belt which forms part of licensed area and in lieu of which benefit to the extent permissible as per policy towards covered area is being granted, shall be transferred to the Govt. free of cost.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That permanent access shall be taken from service road proposed along the development plan road/internal circulation road as the case may be. ",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That you shall integrate the services with Haryana Urban Development Authority services as and when made available. ",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That no other application has been submitted for grant of license for development of the said land or part thereof for any purpose under the provisions of the Haryana Development and Regulation of Urban Areas Act, 1975 or any application seeking permission for change of land use under the provision of the Punjab Scheduled Roads and Controlled Area Restrictions of Unregulated Development Act, 1963.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That you understand that the development/construction cost of 24 m/18 m major internal roads is not included in the EDC rates and they shall pay the proportionate cost for acquisition of land, if any, alongwith the construction cost of 24 m/18 m wide major internal roads as and when finalized and demanded by the Department. ",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That NOC/Clearance as per provisions of notification dated 14.09.06 issued by Ministry of Environment & Forest, Govt. of India shall be obtained before execution of development works at site.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That you shall make arrangements for water supply, sewerage, drainage etc. to the satisfaction of DGTCP till these services are made available from External Infrastructure to be laid by Haryana Urban Development Authority.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That clearance from competent authority, if required under Punjab Land Preservation Act, 1900 and any other clearance required under any other law shall be obtained.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That the rain water harvesting system shall be provided as per Central Ground Water Authority Norms/Haryana Govt. notification as applicable.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That the instructions issued by Haryana Renewable Energy Development Agency in respect of making provision of Solar Energy Plant etc. in the licensed colony shall be followed. ",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That only LED lamps fitting for internal lighting as well as campus lighting shall be used.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That you shall convey the ‘Ultimate Power Load Requirement’ of the project to the concerned power utility, with a copy to the Director, within two months period from the date of grant of license to enable provision of site in licensed land for Transformers/Switching Stations/ Electric Sub Stations as per the norms prescribed by the power utility in the zoning plan of the project.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That compliance of Rule 24, 26, 27 & 28 of Rules 1976 & Section 5 of Haryana Development and Regulation of Urban Areas Act, 1975 shall be submitted and account number and full particulars of the scheduled bank wherein company  have to deposit thirty percentum of the amount received from the plot holders for meeting the cost of Internal Development Works in the colony shall be informed. ",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That the labour cess as per policy instructions issued by Haryana Government vide Memo No. Misc. 2057-5/25/2008/2TCP dated 25.02.2010 shall be paid. ",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That no sale of applied land has taken place after submission of license application. ",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That pace of development atleast in accordance with sale agreement executed with the buyers of the plots as and when scheme is launched shall be kept. ",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That you shall arrange power connection from UHBVNL/DHBVNL for electrification of the colony and shall install the electricity distribution infrastructure as per the peak load requirement of the colony for which licencee shall get the electrical (distribution) service plan/estimates approved from the agency responsible for installation of external electric services i.e. UHBVNL/DHBVNL and complete the same before obtaining completion certificate for the colony. ",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That you shall not advertise any floor area for sale before the approval of building plans.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That no provision of the Haryana Ceiling on Land Holding Act, 1972 has been violated due to purchase of applied land.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"The you shall complete the infrastructure facilities in the entire cyber city/park and allot at least 1/3rd of the permissible floor area to be utilized for cyber city/park purpose before occupying the facility created under the residential and/or commercial uses. ",
						normal)));
				subList3.add(
						new ListItem(new ListItem("That you shall abide by the policy dated 09.07.2013.", normal)));
				list.add(subList3);

//				list.add(new ListItem(
//						"That you shall intimate their official Email ID and the correspondence made to this email ID by the Department shall be treated legal.",
//						normal));

				list.add(new ListItem(
						"That you shall complete the demarcation at site within 7 days and will submit the Demarcation Plan in the office of District Town Planner, Gurugram within 15 days of issuance of this memo.",
						normal));
				list.add(new ListItem(
						"That you shall clear the outstanding dues of EDC pending against various licenses, if any, before grant of license.",
						normal));
				list.add(new ListItem(
						"That you shall submit the NOC from District Forest Officer Gurugram regarding applicability of any Forest Law/notifications.",
						normal));
				list.add(new ListItem(
						"That certificate from DRO/Deputy Commissioner, Gurugram will be submitted certifying that the applied land is still under ownership of applicant company.",
						normal));
//				if (requestLOIReport.getAddedContent() != null && !requestLOIReport.getAddedContent().isEmpty()) {
//					requestLOIReport.getAddedContent().stream().forEach(cd -> {
//						list.add(new ListItem(cd, normal));
//					});
//				}
				loiTableReportData(list, userId, applicationNumber, requestLOIReport);
				doc.add(list);
				addEmptyLine(para1, 1);
//				String note = "Note:- That you will intimate your official “email ID” to the Department and correspondence done by Department on this ID shall be treated as official intimation & legally valid.";
//				Paragraph para10 = new Paragraph(note, small);
//				para10.setSpacingBefore(10f);
//				doc.add(para10);
				doc.add(new Paragraph("DA/schedule of land. ", normal));
				addEmptyLine(para1, 1);
				Paragraph para12 = new Paragraph("Your filled data for following field(s) has not been approved:",
						normal);
				// You are requested to complete the following shortcomings immediately
				para12.setSpacingBefore(10f);
				Paragraph para13 = new Paragraph("".toUpperCase(), normal);
				para13.setSpacingBefore(10f);
//				doc.add(para12);
//				doc.add(para13);
//				loiTableReportData(doc, userId, applicationNumber, requestLOIReport);
				try {
					Paragraph preface0 = new Paragraph("(" + dtcpUserName + ", I.A.S)    ", smallBold);
					preface0.setIndentationLeft(10);
					preface0.setAlignment(Element.ALIGN_CENTER);
					preface0.setSpacingBefore(10f);
					Paragraph preface01 = new Paragraph("Director,", smallBold);
					preface01.setIndentationLeft(70);
					Paragraph preface02 = new Paragraph("Town & Country Planning", smallBold);
					preface02.setIndentationLeft(25);
					Paragraph preface3 = new Paragraph("Haryana Chandigarh", smallBold);
					preface3.setIndentationLeft(40);

					PdfDiv div = new PdfDiv();
					div.addElement(preface0);
					div.addElement(preface01);
					div.addElement(preface02);
					div.addElement(preface3);
					div.setPaddingLeft(320f);
					div.setFloatType(FloatType.RIGHT);
					doc.add(div);

					Paragraph preface4 = new Paragraph("Endst. " + memoNumber
							+ "/ 		                                    Dated: " + currentDate.split("\\s+")[0]);
					preface4.setAlignment(Element.ALIGN_LEFT);
					preface4.setSpacingBefore(10f);
					doc.add(preface4);
					Paragraph para102 = new Paragraph(
							"A copy is forwarded to the following alongwith copy of land schedule for information and necessary action:-",
							normal);
					para102.setSpacingBefore(10f);
					Paragraph para103 = new Paragraph("".toUpperCase(), normal);
					para103.setSpacingBefore(10f);
					doc.add(para102);
					doc.add(para103);

					List subList1 = new List(List.ORDERED);
					subList1.setIndentationLeft(30f);
					subList1.add(new ListItem("The Deputy Commissioner, Gurugram.", normal));
					subList1.add(new ListItem("District Revenue Officer, Gurugram", normal));
					subList1.add(new ListItem(
							"Senior Town Planner, Gurugram with request to report regarding condition no. 9.", normal));
					subList1.add(new ListItem("District Town Planner, Gurugram.", normal));
					subList1.add(new ListItem("Project Manager (IT) with the request to update the status on website.",
							normal));
					doc.add(subList1);

					Paragraph preface001 = new Paragraph("(" + hqUserName + ")", normal);
					preface001.setAlignment(Element.ALIGN_RIGHT);

					Paragraph preface102 = new Paragraph("District Town Planner (HQ)", normal);
					preface102.setAlignment(Element.ALIGN_RIGHT);

					Paragraph preface23 = new Paragraph("For: Director, Town & Country Planning", normal);
					preface23.setAlignment(Element.ALIGN_RIGHT);

					Paragraph preface34 = new Paragraph("Haryana, Chandigarh.", normal);
					preface34.setAlignment(Element.ALIGN_RIGHT);

					PdfDiv div2 = new PdfDiv();
					div2.addElement(preface001);
					div2.addElement(preface102);
					div2.addElement(preface23);
					div2.addElement(preface34);
					div2.setFloatType(FloatType.RIGHT);
					doc.add(div2);
				} catch (Exception e) {
					log.error("Exception : "+e.getMessage());
				}

				doc.close();
				writer.close();
			} catch (DocumentException e1) {
				log.error("DocumentException : "+e1.getMessage());
			} catch (Exception e) {
				log.error("Exception : "+e.getMessage());
				
			}
		}
	}

	/*****************************************************
	 * : LOI Purpose : IT Park/Cyber Park end *
	 *****************************************************/

	/*****************************************************
	 * 9: LOI Purpose : Industrial colony start *
	 *****************************************************/

	private void getLoiReportIndustrialColony(String applicationNumber, String userId, String hqUserId,
			RequestLOIReport requestLOIReport) {

		LicenseServiceResponseInfo licenseServiceResponceInfo = checkApplicationIsValid(requestLOIReport,
				applicationNumber, userId, hqUserId);
		if (licenseServiceResponceInfo == null) {
			return;
		} else {

			try {
				String myFile = loireportPath;
				Document doc = new Document(PageSize.A4);
				PdfWriter writer = PdfWriter.getInstance(doc,
						new FileOutputStream(myFile + "loi-report-" + applicationNumber + ".pdf"));
				doc.open();
				addTitlePage(doc, 0);
				memoNumber = "LC- " + (licenseServiceResponceInfo.getCaseNumber() != null
						? licenseServiceResponceInfo.getCaseNumber().replaceAll("LC", "").split("-")[0]
						: "N/A") + "-B-JE (MK) 2019";// + (currentDate.split("\\s+")[0].split("\\.")[2]);

				int leftMarg = 70;
				Paragraph preface1 = new Paragraph();
				preface1.add(new Paragraph("To", smallBold));
				doc.add(preface1);
				addEmptyLine(preface1, 2);
				Paragraph preface2 = new Paragraph();
				preface2.setIndentationLeft(leftMarg);
				try {
					ApplicantInfo applicationInfo = licenseDetails.getApplicantInfo();
					preface2.add(new Paragraph(applicationInfo.getDevDetail().getAddInfo().getCompanyName()));
					preface2.add(new Paragraph(applicationInfo.getDevDetail().getAddInfo().getRegisteredAddress()));
				} catch (Exception e) {
					preface2.add(new Paragraph("_________  Pvt. Ltd.,"));
					preface2.add(new Paragraph("--, -----------, ----------,"));
					preface2.add(new Paragraph("---------------."));
					e.printStackTrace();
				}
				doc.add(preface2);
				
				Paragraph loiNumberParagraph = new Paragraph(
						"LOI Number : " + loiNumber);
				loiNumberParagraph.setIndentationLeft(leftMarg);
				loiNumberParagraph.setSpacingBefore(15f);
				doc.add(loiNumberParagraph);
				
				Paragraph paragraph = new Paragraph("Memo No. " + memoNumber
						+ "/ 		                                    Dated: " + currentDate.split("\\s+")[0]);
				paragraph.setIndentationLeft(leftMarg);
				paragraph.setSpacingBefore(15f);
				doc.add(paragraph);
				addEmptyLine(paragraph, 1);

				Paragraph preface12 = new Paragraph();
				preface12.add(new Paragraph(
						"Subject:         Letter of Intent for grant of license for setting up of a industrial colony over an area ",
						smallBold));
				preface12.setSpacingBefore(15f);
				doc.add(preface12);
				Paragraph preface13 = new Paragraph();
				preface13.add(new Paragraph(
						" measuring " + totalArea + " acres in the revenue estate of " + address
								+ (!collaborationCompanyName.equals("N/A") ? (" " + collaborationCompanyName) : ("")),
						smallBold));
				preface13.setIndentationLeft(leftMarg);
				doc.add(preface13);

				Paragraph preface14 = new Paragraph();
				preface14.add(new Paragraph("Please refer to your application dated " + applicationDate.split("\\s+")[0]
						+ "  on the above cited subject.", normal));
				preface14.setIndentationLeft(leftMarg);
				preface14.setSpacingBefore(20f);
				preface14.setSpacingAfter(20f);
				doc.add(preface14);
				new Paragraph("", normal);
				List subList2 = new List(List.ORDERED, List.ALPHABETICAL);
				subList2.setLowercase(List.LOWERCASE);
				subList2.setPostSymbol(". ");
				subList2.setIndentationLeft(25f);

				List subList3 = new List(List.ORDERED, List.ALPHABETICAL);
				subList3.setLowercase(List.LOWERCASE);
				subList3.setPostSymbol(". ");
				subList3.setIndentationLeft(20f);

				List list = new List(List.ORDERED);
				list.setFirst(2);
				List subList = new List(List.ORDERED);
				subList.setIndentationLeft(30f);
				Paragraph content01 = new Paragraph(
						"Your request for grant of licence under section 3 of the Haryana Development and Regulation ",
						normal);
				content01.setIndentationLeft(leftMarg);
				String content = "Of Urban Areas Act, 1975 and Rules, 1976 framed there under for the development of an Industrial Colony over an area "
						+ totalArea
						+ " acres (under integrated industrial licensing policy dated 10.01.2017) falling in the revenue estate of "
						+ address + " has been examined/considered by the Department.";
				doc.add(content01);
				doc.add(new Paragraph(content, normal));
				doc.add(new Paragraph(
						"You are therefore, called upon to fulfill the following requirements/pre-requisites laid down in Rule-11 of the Haryana Development and Regulation of Urban Areas Rules, 1976 within a period of 60 days from the date of issue of this notice, failing which the grant of licence shall be refused:",
						normal));
				Paragraph para1 = new Paragraph("", normal);
				doc.add(new Paragraph(
						"1.\t  To furnish 25% bank guarantee on account of internal development works for the amount calculated as under:- ",
						normal));
				Chunk underline = new Chunk("INTERNAL DEVELOPMENT WORKS:");
				underline.setUnderline(0.1f, -1f);
				underline.setFont(normalBold);
				doc.add(underline);
				
				
		
				
				Double plottedInternalCost = 20 * Double.parseDouble(plottedComponent);
				Double comericalInternalCost = 50 * Double.parseDouble(comericalComponent);
				Double totalInternalCost = plottedInternalCost + comericalInternalCost;
				Double bg25InternalPercentage = totalInternalCost * 0.25;

				

				float[] icolumnWidths = { 2, 3, 2, 2, 3 };
				PdfPTable itable = new PdfPTable(icolumnWidths);
				itable.setWidthPercentage(100);
				int icolIndex = 0;
				java.util.List<String> ihList = Arrays.asList("", "Area\n(in acres)", "Rate per acre\n(in Lac)",
						"Amount\n(in Lac)", "25% BG required to be deposited\n(in Lac)");
				for (String icolumnTitle : ihList) {
					icolIndex = icolIndex + 1;

					PdfPCell header = new PdfPCell();
					header.setBackgroundColor(BaseColor.LIGHT_GRAY);
					header.setBorderWidth(2);
					header.setPhrase(new Phrase(icolumnTitle));
					itable.addCell(header);
				}
				itable.addCell("Plotted");
				itable.addCell(""+plottedComponent+"");
				itable.addCell("20");
				itable.addCell(""+plottedInternalCost+"");
				PdfPCell cell23 = new PdfPCell(new Phrase(""+bg25InternalPercentage+""));
				cell23.setColspan(1);
				cell23.setRowspan(3);
				itable.addCell(cell23);

				itable.addCell("Commercial  ");
				itable.addCell(""+comericalComponent+"");
				itable.addCell("50");
				itable.addCell(""+comericalInternalCost+"");

				itable.addCell(" ");
				itable.addCell(" ");
				itable.addCell("Total");
				itable.addCell(""+totalInternalCost+"");
				doc.add(itable);

				doc.add(new Paragraph(
						"It is made clear that the bank guarantee of internal development works has been worked out on the interim rates and you will have to submit the additional bank guarantee if any, required at the time of approval of service plan/estimates according to the approved layout plan/building plan. With an increase in the cost of construction and an increase in the number of facilities in the layout plan, you would be required to furnish an additional Bank Guarantee within 30 days of demand.",
						normal));

				list.add(new ListItem(
						"To execute two agreements i.e. LC-IV and Bilateral Agreement on non-judicial stamp paper of. Copies of specimen of the said agreements are enclosed herewith for necessary action.",
						normal));

				list.add(new ListItem("You shall deposit an amount of " + conversionCharges + "/-("
						+ conversionChargesInWord + ") against conversion charges online at www.tcpharyana.gov.in.",
						normal));
				list.add(new ListItem("You shall deposit an amount of " + licenseFees + "/-(" + licenseFeesInWord
						+ ") against Licence fee online at www.tcpharyana.gov.in.", normal));

				subList2.add(new ListItem(new ListItem(
						"That you shall deposit the Infrastructure Development Charges as and when demanded by well as “For an industrial licence granted in the Agriculture Zone, the cost of providing infrastructure by various agencies/ departments of the State Government sought by the licensee shall be charged on actual basis. For industrial licence granted in an urbanizable zone, external development charges shall be levied as per the rates determined by the Government from time to time.”",
						normalItalic)));

				subList2.add(new ListItem(new ListItem(
						"Infrastructure Development charges(IDC) 100% exemption of applicable charges. However statutory charges such a licence fee, scrutiny fee, conversion charges shall be payable”. ",
						normalItalic)));
				list.add(subList2);
				list.add(new ListItem(
						"To furnish an undertaking on non judicial stamp paper to the following effect that:-",
						normal));

//				list.add(new ListItem("To furnish an undertaking on non judicial stamp paper to the following effect that:-     ",
//						normal));

				subList3.add(new ListItem(new ListItem(
						"You shall maintain and upkeep of all roads, open spaces, public park and public health services for a period of five years from the date of issue of the completion certificate unless earlier relieved of this responsibility and thereupon to transfer all such roads, open spaces, public parks and public health services free of cost to the Govt. or the local authority, as the case may be, in accordance with the provisions of Section 3(3)(a)(iii) of the Haryana Development and Regulation of Urban Areas Act, 1975.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"You shall integrate the services with Haryana Urban Development Authority services as and when made available in future. ",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"You have not submitted any other application for grant of license for development of the said land or part thereof for any purpose under the provisions of the Haryana Development and Regulation of Urban Areas Act, 1975 or any application seeking permission for change of land use under the provision of the Punjab Scheduled Roads and Controlled Area Restrictions of Unregulated Development Act, 1963.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"You shall obtain NOC/Clearance as per provisions of notification dated 14.09.2006 issued by Ministry of Environment & Forest, Govt. of India, if applicable before execution of development works at site.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"You shall obtain NOC from DFO, to the effect that the site is not affected by any Forest Law/Act/notification.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"You shall make your own arrangements for water supply, sewerage, drainage etc. to the satisfaction of DTCP till these services are made available and the same is made functional from External Infrastructure to be laid by Haryana Urban Development Authority or any other execution agency.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"You shall obtain clearance from competent authority, if required under Punjab Land Preservation Land Act, 1900 and any other clearance required under any other law.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That the rain water harvesting system shall be provided as per Central Ground Water Authority Norms/Haryana Govt. notification as applicable.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That the provision of solar water heating system shall be as per guidelines of Haryana Renewable Energy Development Agency and shall be made operational where applicable before applying for an Occupation Certificate.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"You shall use only LED fitting for internal lighting as well as campus lighting.", normal)));
				subList3.add(new ListItem(new ListItem(
						"You shall convey the ‘Ultimate Power Load Requirement’ of the project to the concerned power utility, with a copy to the Director, within two months period from the date of grant of license to enable provision of site in licensed land for Transformers/Switching Stations/Electric Sub Stations as per the norms prescribed by the power utility in the zoning plan of the project.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"It will be made clear at the time of booking of plots/commercial space that specified rates include or do not include EDC. In case of not inclusion of EDC in the booking rates, then it may be specified that same are to be charged separately as per rate fixed by the Govt. You shall also provide detail of calculation of EDC per Sqm/per sft to the Allottees while raising such demand from the plot owners.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"You shall keep pace of development atleast in accordance with sale agreement executed with the buyers of the plots as and when scheme is launched. ",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"You shall arrange power connection from UHBVNL/DHBVNL for electrification of the colony and shall install the electricity distribution infrastructure as per the peak load requirement of the colony for which licencee shall get the electrical (distribution) service plan/estimates approved from the agency responsible for installation of external electric services i.e. UHBVNL/DHBVNL and complete the same before obtaining completion certificate for the colony. ",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"You shall abide by the terms and condition of policy dated 09.10.2017 and other direction given by the Director time to time to execute the project.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"You will pay the labour cess as per policy instructions issued by Haryana Government vide Memo No. Misc. 2057-5/25/2008/2TCP dated 25.02.2010. ",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"You shall submit compliance of Rule 24, 26, 27 & 28 of Rules 1976 & Section 5 of Haryana Development and Regulation of Urban Areas Act, 1975, and shall inform account number and full particulars of the scheduled bank wherein you have to deposit thirty percentum of the amount received from the plot holders for meeting the cost of Internal Development Works in the colony. \r\n"
								+ "\n(A board resolution assigning the responsibility for online compliance of the said Rules on the Department portal may also be submitted.)\r\n"
								+ "",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"No further sale has taken place after submitting application for grant of license.", normal)));
				subList3.add(new ListItem(new ListItem(
						"You have not submitted any other application for applied site for CLU /licence.", normal)));
				subList3.add(new ListItem(new ListItem(
						"You shall not give any advertisement for sale of plots/commercial area before the approval of layout plan.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"You shall get the access permission from PWD (B&R) before actual execution of work at site.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"No provision of the Haryana Ceiling on Land Holding Act, 1972 has been violated due to purchase of applied land. ",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"You shall not encroach the revenue rasta passes through the site and shall take approval from competent authority before laying services through revenue rasta.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"You shall inform account number & full particulars of the scheduled bank wherein you have to deposit thirty percentum of the amount from buyers for meeting the cost of internal development works in the colony in compliance of Rule-27 of Rules 1976 & Section -5 of Haryana Development and Regulation of Urban Areas Act, 1975",
						normal)));

				list.add(subList3);
				list.add(new ListItem(
						"That you shall submit five copies of layout plan of industrial colony as per policy dated 09.10.2017.",
						normal));

				list.add(new ListItem(
						"That company will intimate your official Email ID and the correspondence on this email ID by the Department will be treated receipt of such correspondence.",
						normal));

//				if (requestLOIReport.getAddedContent() != null && !requestLOIReport.getAddedContent().isEmpty()) {
//					requestLOIReport.getAddedContent().stream().forEach(cd -> {
//						list.add(new ListItem(cd, normal));
//					});
//				}
				loiTableReportData(list, userId, applicationNumber, requestLOIReport);
				doc.add(list);

				addEmptyLine(para1, 1);
//				String note = "Note:- That you will intimate your official “email ID” to the Department and correspondence done by Department on this ID shall be treated as official intimation & legally valid.";
//				Paragraph para10 = new Paragraph(note, small);
//				para10.setSpacingBefore(10f);
//				doc.add(para10);
//				doc.add(new Paragraph("DA/schedule of land. ", normal));
				addEmptyLine(para1, 1);
				Paragraph para12 = new Paragraph("Your filled data for following field(s) has not been approved:",
						normal);
				// You are requested to complete the following shortcomings immediately
				para12.setSpacingBefore(10f);
				Paragraph para13 = new Paragraph("".toUpperCase(), normal);
				para13.setSpacingBefore(10f);
//				doc.add(para12);
//				doc.add(para13);
//				loiTableReportData(doc, userId, applicationNumber, requestLOIReport);
				try {
					Paragraph preface0 = new Paragraph("(" + dtcpUserName + ", I.A.S)    ", smallBold);
					preface0.setIndentationLeft(10);
					preface0.setAlignment(Element.ALIGN_CENTER);
					preface0.setSpacingBefore(10f);
					Paragraph preface01 = new Paragraph("Director,", smallBold);
					preface01.setIndentationLeft(70);
					Paragraph preface02 = new Paragraph("Town & Country Planning", smallBold);
					preface02.setIndentationLeft(25);
					Paragraph preface3 = new Paragraph("Haryana Chandigarh", smallBold);
					preface3.setIndentationLeft(40);

					PdfDiv div = new PdfDiv();
					div.addElement(preface0);
					div.addElement(preface01);
					div.addElement(preface02);
					div.addElement(preface3);
					div.setPaddingLeft(320f);
					div.setFloatType(FloatType.RIGHT);
					doc.add(div);

					Paragraph preface4 = new Paragraph("Endst. " + "Endst. No LC-4711/Asstt. MS/2022"
							+ "/ 		                                    Dated: " + currentDate.split("\\s+")[0]);
					preface4.setAlignment(Element.ALIGN_LEFT);
					preface4.setSpacingBefore(10f);
					doc.add(preface4);
					Paragraph para102 = new Paragraph(
							"A copy is forwarded to the following for information and necessary action:-", normal);
					para102.setSpacingBefore(10f);
					Paragraph para103 = new Paragraph("".toUpperCase(), normal);
					para103.setSpacingBefore(10f);
					doc.add(para102);
					doc.add(para103);

					List subList1 = new List(List.ORDERED);
					subList1.setIndentationLeft(30f);
					subList1.add(new ListItem("Senior Town Planner, Rohtak.", normal));
					subList1.add(new ListItem("District Town Planner, Panipat.", normal));
					doc.add(subList1);

					Paragraph preface001 = new Paragraph("(" + hqUserName + ")", normal);
					preface001.setAlignment(Element.ALIGN_RIGHT);

					Paragraph preface102 = new Paragraph("District Town Planner (HQ)", normal);
					preface102.setAlignment(Element.ALIGN_RIGHT);

					Paragraph preface23 = new Paragraph("For: Director, Town & Country Planning", normal);
					preface23.setAlignment(Element.ALIGN_RIGHT);

					Paragraph preface34 = new Paragraph("Haryana, Chandigarh.", normal);
					preface34.setAlignment(Element.ALIGN_RIGHT);

					PdfDiv div2 = new PdfDiv();
					div2.addElement(preface001);
					div2.addElement(preface102);
					div2.addElement(preface23);
					div2.addElement(preface34);
					div2.setFloatType(FloatType.RIGHT);
					doc.add(div2);
				} catch (Exception e) {
					log.error("Exception : "+e.getMessage());
				}

				doc.close();
				writer.close();
			} catch (DocumentException e1) {
				log.error("DocumentException : "+e1.getMessage());
			} catch (Exception e) {
				log.error("Exception : "+e.getMessage());
				
			}
		}
	}

	/*****************************************************
	 * LOI Purpose : Industrial colony end *
	 *****************************************************/

	/*****************************************************
	 * 10: LOI Purpose : Develop Mixed Land Use Colony start *
	 *****************************************************/

	private void getLoiReportDevelopMixedLandUseColony(String applicationNumber, String userId, String hqUserId,
			RequestLOIReport requestLOIReport) {
		LicenseServiceResponseInfo licenseServiceResponceInfo = checkApplicationIsValid(requestLOIReport,
				applicationNumber, userId, hqUserId);
		if (licenseServiceResponceInfo == null) {
			return;
		} else {
			try {
				String myFile = loireportPath;
				Document doc = new Document(PageSize.A4);
				PdfWriter writer = PdfWriter.getInstance(doc,
						new FileOutputStream(myFile + "loi-report-" + applicationNumber + ".pdf"));
				doc.open();
				String residentialPercentage = "70";
				String comericialPercentage = "30";

				addTitlePage(doc, 1);
				memoNumber = "LC- " + (licenseServiceResponceInfo.getCaseNumber() != null
						? licenseServiceResponceInfo.getCaseNumber().replaceAll("LC", "").split("-")[0]
						: "N/A") + "/JE(SB) 2023";// + (currentDate.split("\\s+")[0].split("\\.")[2]);

				int leftMarg = 70;
				Paragraph preface1 = new Paragraph();
				preface1.add(new Paragraph("To", smallBold));
				doc.add(preface1);
				
				Paragraph loiNumberParagraph = new Paragraph(
						"LOI Number : " + loiNumber);
				loiNumberParagraph.setIndentationLeft(leftMarg);
				loiNumberParagraph.setSpacingBefore(15f);
				doc.add(loiNumberParagraph);
				
				addEmptyLine(preface1, 2);
				Paragraph preface2 = new Paragraph();
				preface2.setIndentationLeft(leftMarg);
				try {
					ApplicantInfo applicationInfo = licenseDetails.getApplicantInfo();
					preface2.add(new Paragraph(applicationInfo.getDevDetail().getAddInfo().getCompanyName()));
					preface2.add(new Paragraph(applicationInfo.getDevDetail().getAddInfo().getRegisteredAddress()));
//				preface2.add(new Paragraph((applicationInfo.getState() == null ? "N/A" : applicationInfo.getState())
//						+ "-" + applicationInfo.getDevDetail().getLicenceDetails().getPincode()));
				} catch (Exception e) {
					preface2.add(new Paragraph("_________  Pvt. Ltd.,"));
					preface2.add(new Paragraph("--, -----------, ----------,"));
					preface2.add(new Paragraph("---------------."));
					e.printStackTrace();
				}
				doc.add(preface2);
				Paragraph paragraph = new Paragraph("Memo No. " + memoNumber
						+ "/ 		                                    Dated: " + currentDate.split("\\s+")[0]);
				paragraph.setIndentationLeft(leftMarg);
				paragraph.setSpacingBefore(15f);
				doc.add(paragraph);
				addEmptyLine(paragraph, 1);

				Paragraph preface12 = new Paragraph();
				preface12.add(new Paragraph(
						"Subject:         Letter of Intent - Request for grant of licence to develop Mixed Land Use Colony ",
						smallBold));
				preface12.setSpacingBefore(15f);
				doc.add(preface12);
				Paragraph preface13 = new Paragraph();
				preface13.add(new Paragraph(
						"(" + residentialPercentage + "% Residential & " + comericialPercentage
								+ "% Commercial) under TOD policy over an area measuring " + totalArea
								+ " acre in the revenue estate of " + address
								+ (!collaborationCompanyName.equals("N/A") ? ("-" + collaborationCompanyName) : ("")),
						smallBold));
				preface13.setIndentationLeft(leftMarg);
				doc.add(preface13);

				Paragraph preface14 = new Paragraph();
				preface14.add(new Paragraph("Please refer to your application dated " + applicationDate.split("\\s+")[0]
						+ " on the above cited subject. ", smallBold));
				preface14.setIndentationLeft(leftMarg);
				preface14.setSpacingBefore(20f);
				preface14.setSpacingAfter(20f);
				doc.add(preface14);

				List subList2 = new RomanList();
				subList2.setPostSymbol(". ");
				subList2.setIndentationLeft(30f);
				List subList3 = new RomanList();// new List(List.ORDERED, List.ALPHABETICAL);
				subList3.setLowercase(List.LOWERCASE);
				subList3.setPostSymbol(". ");
				subList3.setIndentationLeft(15f);

				List list = new List(List.ORDERED);
				list.setFirst(2);
				List list1 = new List(List.ORDERED);
				List subList = new List(List.ORDERED, List.ALPHABETICAL);
				subList3.setLowercase(List.UPPERCASE);
				subList3.setPostSymbol(". ");

				subList.setIndentationLeft(30f);
				List subListI = new RomanList();
				subListI.setLowercase(List.LOWERCASE);
				subListI.setPostSymbol(". ");
				subListI.setIndentationLeft(30f);
				List subListE = new RomanList();
				subListE.setLowercase(List.LOWERCASE);
				subListE.setPostSymbol(". ");
				subListE.setIndentationLeft(30f);

				Paragraph content1 = new Paragraph(
						"Your request for the grant of license under section 3 of the Haryana Development", normal);
				content1.setIndentationLeft(leftMarg);
				Paragraph content = new Paragraph(
						"and Regulation of Urban Areas Act, 1975 and the Haryana Development and Regulation of Urban Areas Rules, 1976 framed thereunder for the development of Mixed Land Use Colony ("
								+ residentialPercentage + "% Residential & " + comericialPercentage
								+ "% Commercial) under TOD policy over an area measuring " + totalArea
								+ " acre in the revenue estate of " + address
								+ " has been examined/considered by the Department under the policy dated 09.02.2016 and it is proposed to grant license to you. However, before grant of licence, you are called upon to fulfill the following requirements/pre-requisites laid down in Rule 11 of the Haryana Development and Regulation of Urban Areas Rules, 1976 within a period of 60 days from the date of issuance of this notice, failing which the grant of license shall be refused.",
						normal);
				;
				Paragraph para1 = new Paragraph("", normal);
				addEmptyLine(para1, 1);
				doc.add(content1);
				doc.add(content);
				doc.add(new Paragraph(
						"1. To furnish the bank guarantees on account of Internal Development Charges and the External Development Charges for the amount calculated as under:-",
						normal));

				doc.add(para1);
				
				
				Double plottedInternalCost=20*Double.parseDouble(farAmount)*Double.parseDouble(plottedComponent);
				Double comericalInternalCost=50*Double.parseDouble(farAmount)*Double.parseDouble(comericalComponent);;
				Double totalEdc=plottedInternalCost+comericalInternalCost;
				Double bg25InternalPercentage = totalEdc * 0.25;
				
			
				
				
				Double plottedExternalCost = Double.parseDouble(plottedComponent)*Double.parseDouble(farAmount)*1.75;
				Double comericalExternalCost = Double.parseDouble(comericalComponent)*Double.parseDouble(farAmount)*1.75;
				Double totalExternalCost = plottedExternalCost + comericalExternalCost;
				Double bg25ExternalPercentage = totalExternalCost * 0.25;
			
				

				subListI.add(new ListItem(" Area under RGH Component "+plottedComponent+" acres		= Rs. "+plottedInternalCost+" lacs \r\n"
						+ "@ Rs. 20.00 Lac per acre ("+farAmount+" FAR)", normal));
				subListI.add(
						new ListItem("Area under Commercial Component "+comericalComponent+" acres	= Rs. "+comericalInternalCost+" lacs	 \r\n"
								+ "@ Rs. 50.00 Lac per acre ("+farAmount+" FAR)", normal));
				subListI.add(new ListItem("Total Cost of development 				= Rs. "+totalEdc+" Lacs", normal));
				subListI.add(new ListItem(
						"25% BG, which is required 				= Rs. "+bg25InternalPercentage+" Lacs \r\n" + "(validity for five years)",
						normal));

				subListE.add(new ListItem("Area under RGH Component "+plottedComponent+" acres		= Rs. "+plottedExternalCost+" lacs \r\n"
						+ "@ Rs. "+plottedComponent+" x "+farAmount+"/1.75 per acre ("+farAmount+" FAR)", normal));
				subListE.add(
						new ListItem("Area under Commercial Component "+comericalComponent+" acres	= Rs. "+comericalExternalCost+" lacs	 \r\n"
								+ "@ Rs. "+comericalComponent+" x "+farAmount+"/1.75 Lac per acre ("+farAmount+" FAR)", normal));
				subListE.add(new ListItem("Total Cost of development 				= Rs. "+totalExternalCost+" Lacs", normal));
				subListE.add(new ListItem(
						"25% BG, which is required 				= Rs. "+bg25ExternalPercentage+" Lacs \r\n" + "(validity for five years)",
						normal));

				subList.add(new ListItem(new ListItem("INTERNAL DEVELOPMENT WORKS (IDW):")));
				subList.add(subListI);

				subList.add(new ListItem(new ListItem("EXTERNAL DEVELOPMENT CHARGES (EDC):-")));
				subList.add(subListE);
				list1.add(subList);
				doc.add(list1);

				list.add(new ListItem(
						"It is made clear that the Bank Guarantee of Internal Development Works has been worked out on the interim rates and you have to submit the additional Bank Guarantee if any, required at the time of approval of Service Plan/Estimate according to the approved building plan. With an increase in the cost of construction and an increase in the number of facilities in the building plan, you would be required to furnish an additional bank guarantee within 30 days on demand. In the event of increase of rates of external development charges, you will have to pay the enhanced rates of external development charges as finally determined and as and when demanded by the DGTCP, Haryana and furnish additional bank guarantee and submit an undertaking in this regard.",
						normal));
				list.add(new ListItem("To deposit an amount of " + licenseFees + "/-(" + licenseFeesInWord
						+ ") on account of balance licence fee and an amount of " + conversionCharges + "/-("
						+ conversionChargesInWord
						+ ") on account of conversion charges online at website i.e. www.tcpharyana.gov.in. OR applicant company has option to deposit balance license fee and conversion charges as per amendment dated 26.12.2018 in TOD policy dated 09.02.2016.",
						normal));
				list.add(new ListItem("That you shall deposit an amount of " + stateInfrastructureDevelopmentCharges
						+ "/-(" + stateInfrastructureDevelopmentChargesInWord
						+ ") on account of Infrastructure Augmentation Charges online at website i.e. www.tcpharyana.gov.in. OR applicant company has option to deposit Infrastructure Augmentation Charges as per amendment dated 26.12.2018 in TOD policy dated 09.02.2016.",
						normal));
				list.add(new ListItem(
						"To execute two agreements i.e. LC-IV & LC-IV-B Bilateral Agreement on Non-Judicial Stamp Paper of Rs. 100/-. Specimen copies of the said agreements are enclosed herewith for necessary action. Further, following additional clauses shall be added in LC-IV agreement as per Government instruction dated 14.08.2020.",
						normal));
				subList2.add(new ListItem(new ListItem(
						"That the owner/developer shall integrate the bank account in which 70 percent allottee receipts are credited under Section-4(2)(I)(D) of the Real Estate Regulation and Development Act, 2016 with the online application/payment gateway of the Department, in such manner, so as to ensure that 10% of the total receipt from each payment made by an allottee is automatically deducted and gets credited to the EDC head in the State treasury",
						normal)));

				subList2.add(new ListItem(new ListItem(
						"That such 10% of the total receipt from each payment made by the allottee, which is received by the Department shall get automatically credited, on the date of receipt in the Government treasury against EDC dues.  ",
						normal)));
				subList2.add(new ListItem(new ListItem(
						"That such 10% deduction shall continue to operate till the total EDC dues get recovered from the owner/developer.",
						normal)));
				subList2.add(new ListItem(new ListItem(
						"The implementation of such mechanism shall, however, have no bearing on EDC instalment schedule conveyed to the owner/developer. The owner/developer shall continue to supplement such automatic EDC deductions with payments from its own funds to ensure that by the EDC instalments that are due for payment that paid as per the prescribed schedule.",
						normal)));
				list.add(subList2);

				list.add(new ListItem(
						"To furnish an undertaking on non-judicial stamp paper of Rs. 100/- to the following effect that:- ",
						normal));

				subList3.add(new ListItem(new ListItem(
						"That you will pay the Infrastructure Development Charges amounting to "
								+ stateInfrastructureDevelopmentCharges
								+ "/-@ (Rs. "+plottedComponent+" x "+farAmount+" per sq. mtr for RGH component and Rs. "+comericalComponent+" x "+farAmount+" per sq. mtr for commercial component) in two equal installments. First Installment will be due within 60 days of grant of license and second Installment within six months of grant of license failing which 18% PA interest will be liable for the delayed period.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That area coming under the sector roads and restricted belt/ green belt, if any, which forms part of licensed area and in lieu of which benefit to the extent permissible as per policy towards FAR is being granted, shall be transferred free of cost to the Govt.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That you shall maintain and upkeep of all roads, open spaces, public park and public health services for a period of five years from the date of issue of the completion certificate unless earlier relieved of this responsibility and thereupon to transfer all such roads, open spaces, public parks and public health services free of cost to the Govt. or the local authority, as the case may be, in accordance with the provisions of Section 3(3)(a)(iii) of the Haryana Development and Regulation of Urban Areas Act, 1975.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That you shall construct portion of service road, internal circulation roads, forming the part of site area at your own cost and shall transfer the land falling within alignment of same free of cost to the Govt. u/s 3(3)(a)(iii) of the Haryana Development and Regulation of Urban Areas Act, 1975.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That you shall be liable to pay the actual rates of External Development Charges as and when determined and demanded as per prescribed schedule by the DGTCP, Haryana.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That you shall integrate the services with Haryana Shehari Vikas Pradhikaran services as and when made available. ",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That you have not submitted any other application for grant of license for development of the said land or part thereof for any purpose under the provisions of the Haryana Development and Regulation of Urban Areas Act, 1975 or any application seeking permission for change of land use under the provision of the Punjab Scheduled Roads and Controlled Area Restrictions of Unregulated Development Act, 1963.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That you have understood that the development /construction cost of 24 m/18m major internal roads is not included in the EDC rates and applicant company shall pay the proportionate cost for acquisition of land, if any, alongwith the construction cost of 24 m/18 m wide major internal roads as and when finalized and demanded by the Department. ",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That you shall obtain NOC/Clearance as per provisions of notification dated 14.09.06 issued by Ministry of Environment & Forest, Govt. of India before execution of development works at site.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That you shall make arrangements for water supply, sewerage, drainage etc. to the satisfaction of DGTCP till these services are made available from External Infrastructure to be laid by Haryana Shehari Vikas Pradhikaran.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That the rain water harvesting system shall be provided as per Central Ground Water Authority Norms/Haryana Govt. notification as applicable.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That you shall make provision of solar power system as per guidelines of Haryana Renewable Energy Development Agency and shall make operational where applicable before applying for an Occupation Certificate.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That you shall use only LED fitting for internal lighting as well as campus lighting.",
						normal)));

				subList3.add(new ListItem(new ListItem(
						"That you shall convey the ‘Ultimate Power Load Requirement’ of the project to the concerned power utility, with a copy to the Director, within two months period from the date of grant of license to enable provision of site in licensed land for Transformers/Switching Stations/Electric Sub Stations as per the norms prescribed by the power utility in the zoning plan of the project.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That you shall submit compliance of Rule 24, 26, 27 & 28 of Rules 1976 & Section 5 of Haryana Development and Regulation of Urban Areas Act, 1975, and shall inform account number and full particulars of the scheduled bank wherein applicant company has to deposit thirty percentum of the amount from the floor/space holders for meeting the cost of Internal Development Works in the colony. ",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That you shall permit the Director or any other office authorized by him to inspect the execution of the layout and the development works in the colony and to carry out all directions issued by him for ensuring due compliance of the execution of the layout and development works in accordance with the license granted.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That you shall not give any advertisement for sale of applied /licensed area before the approval of layout plan / building plans of the same.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That you shall pay the labour cess as per policy instructions issued by Haryana Government vide Memo No. Misc. 2057-5/25/2008/2TCP dated 25.02.2010. ",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That you shall keep pace of construction at least in accordance with sale agreement executed with the buyers of the flats as and when scheme is launched.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That you shall submit the additional bank guarantee, if any required at the time of approval of Service Plans/Estimate. With an increase in the cost of construction and increase in the number of facilities in Layout Plan, applicant company would be required to furnish an additional bank guarantee within 30 days on demand. It is made clear that bank guarantee of Internal Development Works/EDC has been worked out on the interim rates.  ",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That you shall specify the detail of calculations per Sqm/per sq ft, which is being demanded from the flat/shop owners on account of IDC/EDC, if being charged separately as per rates fixed by Govt.",
						normal)));

				subList3.add(new ListItem(new ListItem(
						"That the provisions of the Real Estate (Regulation and Development) Act, 2016 and rules framed thereunder shall be followed by the applicant in letter and spirit.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That no pre-launch/sale of applied/licensed area will be undertaken before approval of the layout plan.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That you shall execute the development works as per Environmental Clearance and comply with the provisions of Environment Protection Act, 1986, Air (Prevention and Control of Pollution of Act, 1981) and Water (Prevention and Control of Pollution of 1974). In case of any violation of the provisions of said statutes, you shall be liable for penal action by Haryana State Pollution Control Board or any other Authority Administering the said Acts.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That you shall abide by with the Act/Rules and the policies notified by the Department for development of commercial colonies and other instructions issued by the Director under section 9A of the Haryana Development and Regulations of Urban Areas Act, Haryana Development and Regulations of Urban Areas Act, Haryana Development and Regulations of Urban Areas Act, 1975.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That the owner/developer shall derive maximum net profit at the rate of 15% of the total project cost of the development of the above said Colony after making provisions of the statutory taxes. In case, the net profit exceeds 15% after completion of the project period, the surplus amount shall be deposited within two months in the State Government Treasury by the Owner/Developer or they shall spend this money on further amenities/facilities in their colony for the benefit of the resident therein.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That licenced land forming the part of Sector, Road, Service roads, Green belts and 24/18 mtrs wide road as the case may be land pockets which are earmarked for community sites shall be transferred within a period of 30 days in favour of Government from the date of approval of Zoning Plan.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That you shall obey all the directions/restrictions imposed by the Department from time to time.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That you shall abide by all the provisions of Act no. 8 of 1975 and Rules framed thereunder as amended time to time.",
						normal)));
				subList3.add(new ListItem(new ListItem(
						"That you shall complete the project as per the policy dated 09.02.2016 and as amended time to time.",
						normal)));
				list.add(subList3);

				list.add(new ListItem(
						"That you shall complete the demarcation at site within 7 days and will submit the Demarcation Plan in the office of District Town Planner, Gurugram within 15 days of issuance of this memo.",
						normal));
//			list.add(new ListItem(
//					"That you shall submit the NOC from Divisional Forest Officer, Gurugram before grant of final permission.",
//					normal));
//			list.add(new ListItem(
//					"That you shall take prior permission from the Divisional Forest Officer, Gurugram regarding cutting of any tree at applied site.",
//					normal));
				list.add(new ListItem(
						"That certificate from DRO/Deputy Commissioner, Gurugram will be submitted certifying that the applied land is still under ownership of applicant company.",
						normal));
				list.add(new ListItem(
						"That you shall undertake to indemnify State Govt./ Department for loss occurred or legal complication arising due to pending litigation and the land owning / developer company will be responsible for the same in respect of applied land.",
						normal));
				list.add(new ListItem(
						"That the you shall submit an indemnity bond, indemnifying by the DGTCP against any loss/claim arising out of any pending litigation.",
						normal));
//			list.add(new ListItem(
//					"That you shall clear the outstanding dues amounting to Rs. 3.39 Cr. (as on 03.02.2022) on account of EDC and IDC against licence no. 7 of 2022, 30 of 2019 and 39 of 2021 OR mortgage the additional 10% saleable area/ built up area in accordance with the order dated 03.02.2023, before issuance of final permission.",
//					normal));
//			list.add(new ListItem(
//					"That you shall submit an affidavit duly attested by 1st Class Magistrate, to the effect that applicants have not submitted any other application for grant of licence for development of the said land or part thereof for any purpose under the provisions of the Haryana Development and Regulation of Urban Areas Act, 1975 or any application seeking permission for change of land use under the provision of the Punjab Schedule Roads and Controlled Areas restrictions of Unregulated Development Act, 1963 or have not applied for licence/ permission under any other law for the time being in force.",
//					normal));
//			list.add(new ListItem(
//					"That you shall enhance the paid up capital of the company upto Rs. 20.00 Cr. before issuance of final permission.",
//					normal));
				list.add(new ListItem(
						"That you shall intimate their official Email ID and the correspondence made to this email ID by the Department shall be treated legal",
						normal));

//				if (requestLOIReport.getAddedContent() != null && !requestLOIReport.getAddedContent().isEmpty()) {
//					requestLOIReport.getAddedContent().stream().forEach(cd -> {
//						list.add(new ListItem(cd, normal));
//					});
//				}
				loiTableReportData(list, userId, applicationNumber, requestLOIReport);
				doc.add(list);
				doc.add(new Paragraph("DA/schedule of land.", normal));
				addEmptyLine(para1, 1);
				Paragraph para12 = new Paragraph("Your filled data for following field(s) has not been approved:",
						normal);
				para12.setSpacingBefore(10f);
				Paragraph para13 = new Paragraph("".toUpperCase(), normal);
				para13.setSpacingBefore(10f);
//				doc.add(para12);
//				doc.add(para13);
//				loiTableReportData(doc, userId, applicationNumber, requestLOIReport);

				try {
					Paragraph preface0 = new Paragraph("(" + dtcpUserName + ", I.A.S)    ", smallBold);
					preface0.setIndentationLeft(10);
					preface0.setAlignment(Element.ALIGN_CENTER);
					preface0.setSpacingBefore(10f);
					Paragraph preface01 = new Paragraph("Director,", smallBold);
					preface01.setIndentationLeft(70);
					Paragraph preface02 = new Paragraph("Town & Country Planning", smallBold);
					preface02.setIndentationLeft(25);
					Paragraph preface3 = new Paragraph("Haryana Chandigarh", smallBold);
					preface3.setIndentationLeft(40);

					PdfDiv div = new PdfDiv();
					div.addElement(preface0);
					div.addElement(preface01);
					div.addElement(preface02);
					div.addElement(preface3);
					div.setPaddingLeft(320f);
					div.setFloatType(FloatType.RIGHT);
					doc.add(div);

					Paragraph preface4 = new Paragraph("Endst. " + memoNumber
							+ "/ 		                                    Dated: " + currentDate.split("\\s+")[0]);
					preface4.setAlignment(Element.ALIGN_LEFT);
					preface4.setSpacingBefore(10f);
					doc.add(preface4);
					Paragraph para102 = new Paragraph(
							"A copy is forwarded to the following alongwith copy of land schedule for information and necessary action:- ",
							normal);
					para102.setSpacingBefore(10f);
					Paragraph para103 = new Paragraph("".toUpperCase(), normal);
					para103.setSpacingBefore(10f);
					doc.add(para102);
					doc.add(para103);

					List subList1 = new List(List.ORDERED);
					subList1.setIndentationLeft(30f);
					subList1.add(new ListItem("Chief Administrator HSVP, Sector-6, Panchkula.", normal));
					subList1.add(new ListItem("Director, Urban Estates, Sector-6, Panchkula.", normal));
					subList1.add(new ListItem("Senior Town Planner, Gurugram.", normal));
					subList1.add(new ListItem("District Forest Officer, Gurugram.", normal));
					subList1.add(new ListItem(
							"District Town Planner, Gurugram with a request to send the duly verified demarcation plan.",
							normal));
					subList1.add(new ListItem("CAO O/o DGTCP, Haryana. ", normal));
					subList1.add(new ListItem("Nodal Officer (Website) O/o DGTCP, Hr.", normal));
					doc.add(subList1);

					Paragraph preface001 = new Paragraph("(" + hqUserName + ")", normal);
					preface001.setAlignment(Element.ALIGN_RIGHT);

					Paragraph preface102 = new Paragraph("District Town Planner (HQ)", normal);
					preface102.setAlignment(Element.ALIGN_RIGHT);

					Paragraph preface23 = new Paragraph("For: Director, Town & Country Planning", normal);
					preface23.setAlignment(Element.ALIGN_RIGHT);

					Paragraph preface34 = new Paragraph("Haryana, Chandigarh.", normal);
					preface34.setAlignment(Element.ALIGN_RIGHT);

					PdfDiv div2 = new PdfDiv();
					div2.addElement(preface001);
					div2.addElement(preface102);
					div2.addElement(preface23);
					div2.addElement(preface34);
					div2.setFloatType(FloatType.RIGHT);
					doc.add(div2);
				} catch (Exception e) {
					log.error("Exception : "+e.getMessage());
				}

				doc.close();
				writer.close();
			} catch (DocumentException e1) {
				log.error("DocumentException : "+e1.getMessage());
			} catch (Exception e) {
				log.error("Exception : "+e.getMessage());
				
			}
		}
	}

	/*****************************************************
	 * LOI Purpose : Develop Mixed Land Use Colony end *
	 *****************************************************/

	/**********************************************************
	 * 11: LOI Purpose : New Residential Plotted colony start *
	 **********************************************************/

	private void getLoiReportNewResidentialPlottedColony(String applicationNumber, String userId, String hqUserId,
			RequestLOIReport requestLOIReport) {
		String licenseNo = null;
		StringJoiner applicationDated = new StringJoiner("&");
		LicenseServiceResponseInfo licenseServiceResponceInfo = checkApplicationIsValid(requestLOIReport,
				applicationNumber, userId, hqUserId);
		if (licenseServiceResponceInfo == null) {
			return;
		} else {
			try {
				String myFile = loireportPath;
				Document doc = new Document(PageSize.A4);
				PdfWriter writer = PdfWriter.getInstance(doc,new FileOutputStream(myFile+"loi-report-" + applicationNumber + ".pdf"));
				doc.open();
				addTitlePage(doc, 1);
//				String mm=licenseServiceResponceInfo.getCaseNumber() ;
//				System.out.println("mm:---------"+mm);
				memoNumber = "LC- " + (licenseServiceResponceInfo.getCaseNumber() != null
						? licenseServiceResponceInfo.getCaseNumber().replaceAll("LC-", "").split("~")[0]
						: "N/A") + "-C/JE(SB)/-2022";// + (currentDate.split("\\s+")[0].split("\\.")[2]);
				int licenseList = licenseServiceResponceInfo.getNewServiceInfoData().size();
				if (licenseList > 1) {
					licenseNo = licenseServiceResponceInfo.getNewServiceInfoData().stream().map(license -> {
						return "License No. " + applicationNumber.substring(12) + " of "
								+ applicationNumber.substring(4, 8) + " dated " + applicationDate.split("\\s+")[0];
					}).collect(Collectors.toList()).stream().collect(Collectors.joining(" & "));

				}

				licenseServiceResponceInfo.getNewServiceInfoData().stream().forEach(license -> {
					applicationDated.add(applicationDate.split("\\s+")[0]);
				});

//				licenseNo="License No. 94 of 2013 dated 31.10.2013 & License No. 11 of 2015 dated 01.10.2015";

				int leftMarg = 70;
				Paragraph preface1 = new Paragraph();
				preface1.add(new Paragraph("To", smallBold));
				doc.add(preface1);
				addEmptyLine(preface1, 2);
				Paragraph preface2 = new Paragraph();
				preface2.setIndentationLeft(leftMarg);
				try {
					ApplicantInfo applicationInfo = licenseDetails.getApplicantInfo();
					preface2.add(new Paragraph(applicationInfo.getDevDetail().getAddInfo().getCompanyName()));
					preface2.add(new Paragraph(applicationInfo.getDevDetail().getAddInfo().getRegisteredAddress()));
//					preface2.add(new Paragraph((applicationInfo.getState() == null ? "N/A" : applicationInfo.getState())
//							+ "-" + applicationInfo.getDevDetail().getLicenceDetails().getPincode()));
				} catch (Exception e) {
					preface2.add(new Paragraph("_________  Pvt. Ltd.,"));
					preface2.add(new Paragraph("--, -----------, ----------,"));
					preface2.add(new Paragraph("---------------."));
					e.printStackTrace();
				}
				doc.add(preface2);
				
				Paragraph loiNumberParagraph = new Paragraph(
						"LOI Number : " + loiNumber);
				loiNumberParagraph.setIndentationLeft(leftMarg);
				loiNumberParagraph.setSpacingBefore(15f);
				doc.add(loiNumberParagraph);
				
				
				Paragraph paragraph = new Paragraph(
						"Memo No. " + memoNumber + "/                 Dated: " + currentDate.split("\\s+")[0]);
				paragraph.setIndentationLeft(leftMarg);
				paragraph.setSpacingBefore(15f);
				
				doc.add(paragraph);
				addEmptyLine(paragraph, 1);

				Paragraph preface12 = new Paragraph();
				preface12.add(new Paragraph(
						"Subject:         Letter of Intent for grant of license for setting up of Residential Plotted Colony"
								+ (licenseNo == null ? (" area ") : ("")),
						smallBold));
				preface12.setSpacingBefore(15f);
				doc.add(preface12);
				Paragraph preface13 = new Paragraph();
				String sub = (licenseNo == null ? ("") : ("over an additional area ")) + "measuring  " + totalArea
						+ " acres " + (licenseNo == null ? ("") : (" (In addition to " + licenseNo + " )"))
						+ " in the revenue estate of " + address + "";
				preface13.add(new Paragraph(sub.replaceFirst("\\s+", ""), smallBold));
				preface13.setIndentationLeft(leftMarg);
				doc.add(preface13);

				Paragraph preface14 = new Paragraph();
				preface14.add(new Paragraph("Please refer your application dated " + applicationDated
						+ " on the matter cited as subject above.", smallBold));
//				preface14.add(new Paragraph("Please refer your application dated "+applicationDate.split("\\s+")[0]+", 19.02.2022 & 13.06.2022 on the matter cited as subject above.", smallBold));
				preface14.setIndentationLeft(leftMarg);
				preface14.setSpacingBefore(20f);
				preface14.setSpacingAfter(20f);
				doc.add(preface14);

				List subList2 = new RomanList();
				subList2.setPostSymbol(". ");
				subList2.setIndentationLeft(30f);
				List subList3 = new RomanList();// new List(List.ORDERED, List.ALPHABETICAL);
				subList3.setLowercase(List.LOWERCASE);
				subList3.setPostSymbol(". ");
				subList3.setIndentationLeft(15f);

				List list = new List(List.ORDERED);
				list.setFirst(2);
				List list1 = new List(List.ORDERED);
				List subList = new List(List.ORDERED, List.ALPHABETICAL);
				subList.setLowercase(List.UPPERCASE);
				subList.setPostSymbol(". ");

				List subList5 = new List(List.UNORDERED);
				subList5.setIndentationLeft(30f);

				subList.setIndentationLeft(30f);
				List subListI = new RomanList();
				subListI.setLowercase(List.LOWERCASE);
				subListI.setPostSymbol(". ");
				subListI.setIndentationLeft(30f);
				List subListE = new RomanList();
				subListE.setLowercase(List.LOWERCASE);
				subListE.setPostSymbol(". ");
				subListE.setIndentationLeft(30f);

				Paragraph content1 = new Paragraph(
						"Your request for the grant of license under section 3 of the Haryana Development and", normal);
				content1.setIndentationLeft(leftMarg);
				Paragraph content = new Paragraph(
						"Regulation of Urban Areas Act, 1975 and the Haryana Development and Regulation of Urban Areas Rules, 1976 framed thereunder for the development of a Residential Plotted Colony over an additional area measuring "
								+ totalArea + " acres "
								+ (licenseNo == null ? ("") : (" (In addition to " + licenseNo + " )"))
								+ " in the revenue estate of " + address
								+ " has been examined and it is proposed to grant license to you. However, before grant of licence, you are called upon to fulfill the following requirements/pre-requisites laid down in Rule 11 of the Haryana Development and Regulation of Urban Areas Rules, 1976 within a period of 60 days from the date of issuance of this notice, failing which the grant of license shall be refused.",
						normal);
				;
				Paragraph para1 = new Paragraph("", normal);
				addEmptyLine(para1, 1);
				doc.add(content1);
				doc.add(content);
				doc.add(new Paragraph(
						"1. To furnish the bank guarantee on account of Internal Development Charges & External Development Charges for the amount calculated as under:- ",
						normal));

				doc.add(para1);

				Double plottedInternalCost = 20 * Double.parseDouble(plottedComponent);
				Double comericalInternalCost = 50 * Double.parseDouble(comericalComponent);
				Double totalInternalCost = plottedInternalCost + comericalInternalCost;
				Double bg25InternalPercentage = totalInternalCost * 0.25;

				subListI.add(new ListItem(" Plotted component				= " + plottedComponent + " acres", normal));
				subListI.add(new ListItem(" Rate per acre					= Rs. 20.00 lacs per acre", normal));
				subListI.add(new ListItem(
						" Cost of Plotted Component			= "
								+ ConvertUtil.numberToComa(decfor.format(plottedInternalCost)) + " Lacs ",
						normal));
				subListI.add(new ListItem(" Area under Commercial component		= " + comericalComponent + " acre ",
						normal));
				subListI.add(new ListItem(" Rate per acre					= Rs. 50.00 lacs per acre", normal));
				subListI.add(new ListItem(
						" Cost of commercial component		= "
								+ ConvertUtil.numberToComa(decfor.format(comericalInternalCost)) + " Lacs ",
						normal));
				subListI.add(new ListItem(
						" Total Cost of development			= "
								+ ConvertUtil.numberToComa(decfor.format(totalInternalCost)) + " Lacs ",
						normal));
				subListI.add(new ListItem(" 25% BG, which is required 			= "
						+ ConvertUtil.numberToComa(decfor.format(bg25InternalPercentage))
						+ " Lacs  (valid for 5 years)", normal));

				Double plottedExternalCost = zoneWiseEdcAmount * Double.parseDouble(plottedComponent);
				Double comericalExternalCost = zoneWiseEdcAmount * Double.parseDouble(comericalComponent);
				Double totalExternalCost = plottedExternalCost + comericalExternalCost;
				Double bg25ExternalPercentage = totalExternalCost * 0.25;

				subListE.add(new ListItem(
						"Total Area under Plotted component		= " + plottedComponent + " acres \r\n"
								+ "Interim rate for EDC @ " + zoneWiseEdcAmount + " Lacs per acres(Zone) ",
						normal));
				subListE.add(new ListItem("EDC Amount for Plotted component		= "
						+ ConvertUtil.numberToComa(decfor.format(plottedExternalCost)) + " Lacs ", normal));
				subListE.add(new ListItem(
						"Area under commercial component		= " + comericalComponent + "  acres\n"
								+ "Interim rate for EDC @ " + zoneWiseEdcAmount + " Lacs per acres(Zone) ",
						normal));
				subListE.add(new ListItem("EDC Amount for Commercial component	= "
						+ ConvertUtil.numberToComa(decfor.format(comericalExternalCost)) + " Lacs ", normal));
				subListE.add(new ListItem("Total cost of development			= "
						+ ConvertUtil.numberToComa(decfor.format(totalExternalCost)) + " Lacs ", normal));
				subListE.add(new ListItem("25% bank guarantee required			="
						+ ConvertUtil.numberToComa(decfor.format(bg25ExternalPercentage))
						+ " Lacs  (valid for 5 years)", normal));

				subList.add(new ListItem(new ListItem("INTERNAL DEVELOPMENT WORKS:")));
				subList.add(subListI);

				subList.add(new ListItem(new ListItem("EXTERNAL DEVELOPMENT CHARGES (EDC): ")));
				subList.add(subListE);
				list1.add(subList);
				doc.add(list1);
				list.add(new ListItem(
						"It is made clear that the Bank Guarantee of Internal Development Works has been worked out on the interim rates and you have to submit an additional Bank Guarantee if any, required at the time of approval of Service Plan/Estimate according to the approved layout plan. With an increase in the cost of construction and an increase in the number of facilities in the layout plan, you would be required to furnish an additional bank guarantee within 30 days on demand. In the event of increase of rates of external development charges, you will have to pay the enhanced rates of external development charges as finally determined and as and when demanded by the DTCP, Haryana and furnish additional bank guarantee and submit an undertaking in this regard.",
						normal));
				list.add(new ListItem(
						"To execute two agreements i.e. LC-IV and Bilateral Agreement on non-judicial stamp paper. Two copies of specimen of the said agreements are enclosed herewith for necessary action.",
						normal));
				list.add(new ListItem("To deposit an amount of " + licenseFees + "/-(" + licenseFeesInWord
						+ ") on account of Balance license fee and an amount of " + conversionCharges + "/-("
						+ conversionChargesInWord
						+ ") on account of conversion charges   through online e-payment module available on departmental website i.e. www.tcpharyana.gov.in.",
						normal));
				list.add(new ListItem("To furnish BG amounting "
						+ ConvertUtil.numberToWords(decfor.format(bg25ExternalPercentage))
						+ " Lacs against External Development Charges amounting "
						+ ConvertUtil.numberToComa(String.valueOf(totalExternalCost))
						+ " Lacs . You have an option to mortgage 10% saleable area against submission of above said BG and in case, said option is adopted, then the area to be mortgaged may be indicated on the layout plan to be issued alongwith the license alongwith the revenue details thereof. The mortgage deed in this regard shall be executed as per the directions of the Department.",
						normal));
				subList5.add(new ListItem(
						"*It is made clear that rate of EDC has been calculated on the basis of EDC Indexation Mechanism Policy dated 11.02.2016, which stands approved by cabinet. If there will be any change and delay in the amendment in the Act/Rules w.r.t. the said rates, then differential amount from the original calculation will required to be deposited as per demand.",
						normalItalic));
				list.add(subList5);
				list.add(new ListItem("To furnish the Bank Guarantee of "
						+ ConvertUtil.numberToComa(decfor.format(bg25InternalPercentage))
						+ " Lacs on account of Internal Development works to be deposited online at website i.e. www.tcpharyana.gov.in. You have an option to mortgage 10% saleable area against submission of above said BG and in case, said option is adopted, then the area to be mortgaged may be indicated on the layout plan to be issued alongwith the license alongwith the revenue details thereof. The mortgage deed in this regard shall be executed as per the directions of the Department.",
						normal));
				list.add(new ListItem(
						"Further, following additional clauses shall be added in LC-IV agreement as per Government instruction dated 14.08.2020.",
						normal));

				subList2.add(new ListItem(new ListItem(
						"That the owner/developer shall integrate the bank account in which 70 percent allottee receipts are credited under Section-4(2)(I)(D) of the Real Estate Regulation and Development Act, 2016 with the online application/payment gateway of the Department, in such manner, so as to ensure that 10% of the total receipt from each payment made by an allottee is automatically deducted and gets credited to the EDC head in the State treasury",
						normal)));

				subList2.add(new ListItem(new ListItem(
						"That such 10% of the total receipt from each payment made by the allottee, which is received by the Department shall get automatically credited, on the date of receipt in the Government treasury against EDC dues.  ",
						normal)));
				subList2.add(new ListItem(new ListItem(
						"That such 10% deduction shall continue to operate till the total EDC dues get recovered from the owner/developer.",
						normal)));
				subList2.add(new ListItem(new ListItem(
						"The implementation of such mechanism shall, however, have no bearing on EDC instalment schedule conveyed to the owner/developer. The owner/developer shall continue to supplement such automatic EDC deductions with payments from its own funds to ensure that by the EDC instalments that are due for payment that paid as per the prescribed schedule.",
						normal)));
				list.add(subList2);

				list.add(new ListItem(
						"To furnish an undertaking on non-judicial stamp paper of Rs. 100/- to the following   effect.",
						normal));
				subList3.add(new ListItem("That you will pay the Infrastructure Development Charges amounting to  "
						+ stateInfrastructureDevelopmentCharges + "/-(" + stateInfrastructureDevelopmentChargesInWord
						+ ") in two equal installments. First Installment will be due within 60 days of grant of license and second Installment within six months of grant of license failing which 18% PA interest will be liable for the delayed period.",
						normal));
				subList3.add(new ListItem(
						"That area coming under the sector roads and restricted belt / green belt, if any, which forms part of licensed area and in lieu of which benefit to the extent permissible as per policy towards FAR is being granted, shall be transferred free of cost to the Govt.",
						normal));
				subList3.add(new ListItem(
						"That you shall maintain and upkeep of all roads, open spaces, public park and public health services for a period of five years from the date of issue of the completion certificate unless earlier relieved of this responsibility and thereupon to transfer all such roads, open spaces, public parks and public health services free of cost to the Govt. or the local authority, as the case may be, in accordance with the provisions of Section 3(3)(a)(iii) of the Haryana Development and Regulation of Urban Areas Act, 1975.",
						normal));
				subList3.add(new ListItem(
						"That you shall construct portion of service road, internal circulation roads, forming the part of site area at your own cost and shall transfer the land falling within alignment of same free of cost to the Govt. u/s 3(3)(a)(iii) of the Haryana Development and Regulation of Urban Areas Act, 1975.",
						normal));
				subList3.add(new ListItem(
						"That you shall be liable to pay the actual rates of External Development Charges as and when determined and demanded as per prescribed schedule by the DGTCP Haryana.",
						normal));
				subList3.add(new ListItem(
						"That you shall integrate the services with Haryana Shehari Vikas Pradhikaran services as and when made available. ",
						normal));
				subList3.add(new ListItem(
						"That you have not submitted any other application for grant of license for development of the said land or part thereof for any purpose under the provisions of the Haryana Development and Regulation of Urban Areas Act, 1975 or any application seeking permission for change of land use under the provision of the Punjab Scheduled Roads and Controlled Area Restrictions of Unregulated Development Act, 1963.",
						normal));
				subList3.add(new ListItem(
						"That you have understood that the development /construction cost of 24 m/18 m major internal roads is not included in the EDC rates and applicant company shall pay the proportionate cost for acquisition of land, if any, alongwith the construction cost of 24 m/18 m wide major internal roads as and when finalized and demanded by the Department. ",
						normal));
				subList3.add(new ListItem(
						"That you shall obtain NOC/Clearance as per provisions of notification dated 14.09.06 issued by Ministry of Environment & Forest, Govt. of India before execution of development works at site.",
						normal));
				subList3.add(new ListItem(
						"That you shall make arrangements for water supply, sewerage, drainage etc. to the satisfaction of DGTCP till these services are made available from External Infrastructure to be laid by Haryana Shehari Vikas Pradhikaran.",
						normal));
				subList3.add(new ListItem(
						"That the rain water harvesting system shall be provided as per Central Ground Water Authority Norms/Haryana Govt. notification as applicable.",
						normal));
				subList3.add(new ListItem(
						"That you shall make provision of Solar Power System as per guidelines of Haryana Renewable Energy Development Agency and shall make operational where applicable before applying for an Occupation Certificate.",
						normal));
				subList3.add(new ListItem(
						"That you shall use only LED fitting for internal lighting as well as campus lighting.",
						normal));
				subList3.add(new ListItem(
						"That you shall convey the ‘Ultimate Power Load Requirement’ of the project to the concerned power utility, with a copy to the Director, within two months period from the date of grant of license to enable provision of site in licensed land for Transformers/Switching Stations/Electric Sub Stations as per the norms prescribed by the power utility in the zoning plan of the project.",
						normal));
				subList3.add(new ListItem(
						"That you shall submit compliance of Rule 24, 26, 27 & 28 of Rules 1976 & Section 5 of Haryana Development and Regulation of Urban Areas Act, 1975, and shall inform account number and full particulars of the scheduled bank wherein applicant company has to deposit thirty percentum of the amount from the floor/space holders for meeting the cost of Internal Development Works in the colony. ",
						normal));
				subList3.add(new ListItem(
						"That you shall permit the Director or any other office authorized by him to inspect the execution of the layout and the development works in the colony and to carry out all directions issued by him for ensuring due compliance of the execution of the layout and development works in accordance with the license granted.",
						normal));
				subList3.add(new ListItem(
						"That you shall not give any advertisement for sale of licensed/applied area before the approval of layout plan/ building plans of the same.",
						normal));
				subList3.add(new ListItem(
						"That you shall pay the labour cess as per policy instructions issued by Haryana Government vide Memo No. Misc. 2057-5/25/2008/2TCP dated 25.02.2010.",
						normal));
				subList3.add(new ListItem(
						"That you shall abide with policy dated 08.07.2013 and 26.02.2021 and amended from time to time related to allotment of EWS Flats/Plots.",
						normal));
				subList3.add(new ListItem(
						"That you shall keep pace of construction at least in accordance with sale agreement executed with the buyers of the flats as and when scheme is launched.",
						normal));
				subList3.add(new ListItem(
						"That you shall submit the additional bank guarantee, if any required at the time of approval of Service Plans/Estimate. With an increase in the cost of construction and increase in the number of facilities in Layout Plan, applicant company would be required to furnish an additional bank guarantee within 30 days on demand. It is made clear that bank guarantee of Internal Development Works/EDC has been worked out on the interim rates.  ",
						normal));
				subList3.add(new ListItem(
						"That you shall specify the detail of calculations per Sqm/per Sqft., which is being demanded from the flat/shop owners on account of IDC/EDC, if being charged separately as per rates fixed by Govt.",
						normal));
				subList3.add(new ListItem(
						"That the provisions of the Real Estate (Regulation and Development) Act, 2016 and rules framed thereunder shall be followed by the applicant in letter and spirit.",
						normal));
				subList3.add(new ListItem(
						"That no pre-launch/sale of applied/licensed land will be undertaken before approval of the layout plan.",
						normal));
				subList3.add(new ListItem(
						"That the owner/developer shall derive maximum net profit at the rate of 15% of the total project cost of the development of the above said Residential Plotted Colony after making provisions of the statutory taxes. In case, the net profit exceeds 15% after completion of the project period, the surplus amount shall be deposited within two months in the State Government Treasury by the Owner/Developer or they shall spend this money on further amenities/facilities in their colony for the benefit of the resident therein.",
						normal));
				subList3.add(new ListItem(
						"That you shall execute the development works as per Environmental Clearance and comply with the provisions of Environment Protection Act, 1986, Air (Prevention and Control of Pollution of Act, 1981) and Water (Prevention and Control of Pollution of 1974). In case of any violation of the provisions of said statutes, you shall be liable for penal action by Haryana State Pollution Control Board or any other Authority Administering the said Acts.",
						normal));
				subList3.add(new ListItem(
						"That you shall abide by with the Act/Rules and the policies notified by the Department for development of commercial colonies and other instructions issued by the Director under section 9A of the Haryana Development and Regulations of Urban Areas Act, Haryana Development and Regulations of Urban Areas Act, Haryana Development and Regulations of Urban Areas Act, 1975.",
						normal));
				subList3.add(new ListItem(
						"That you shall freeze the plots falling under ROW of 400 KV, 220 KV & 66 KV HT Lines and not create any third party rights on the freezed plots till the shifting/re-routing of 400 KV, 220 KV & 66 KV HT Lines from the site.",
						normal));
				subList3.add(new ListItem(
						"That you shall not encroach the revenue rasta passes through the applied site and keep it thoroughfare movement of general public.",
						normal));
				subList3.add(new ListItem(
						"That licenced land forming the part of Sector, Road, Service roads, Green belts and 24/18 mtrs wide road as the case may be land pockets which are earmarked for community sites shall be transferred within a period of 30 days in favour of Government from the date of approval of Zoning Plan.",
						normal));
				subList3.add(new ListItem(
						"That you shall abide with policy dated 19.12.2006 & 29.08.2019 and amended from time to time.",
						normal));
				subList3.add(new ListItem(
						"That you shall obey all the directions/restrictions imposed by the Department from time to time.",
						normal));
				list.add(subList3);

//				list.add(new ListItem(
//						"That you shall submit the NOC from District Forest Officer Gurugram regarding applicability of any Forest Law/notifications.",
//						normal));
//				list.add(new ListItem(
//						"That you shall take prior permission from the Divisional Forest Officer, Gurugram regarding cutting of any tree at applied site.",
//						normal));
				list.add(new ListItem(
						"That certificate from DRO/Deputy Commissioner, Gurugram will be submitted certifying that the applied land is still under ownership of land owning companies.",
						normal));
//				list.add(new ListItem(
//						"That you shall submit an affidavit duly attested by 1st Class Magistrate, to the effect that applicants have not submitted any other application for grant of licence for development of the said land or part thereof for any purpose under the provisions of the Haryana Development and Regulation of Urban Areas Act, 1975 or any application seeking permission for change of land use under the provision of the Punjab Schedule Roads and Controlled Areas restrictions of Unregulated Development Act, 1963 or have not applied for licence/ permission under any other law for the time being in force.",
//						normal));
				list.add(new ListItem(
						"That you shall submit an indemnity bond indemnifying DGTCP from any loss, if occurs due to submission of undertaking submitted in respect of non-creation of third party rights on the applied land.",
						normal));
				list.add(new ListItem(
						"That you shall undertake to indemnify State Govt./Department for loss occurred or legal complication arising due to pending litigation and the land owning / developer company will be responsible for the same in respect of applied land.",
						normal));
//				list.add(new ListItem(
//						"That you shall clear the outstanding dues on account of EDC/IDC pending against the various licenses granted in favour of developer company and its Boards of Directors before grant of license.",
//						normal));
//				list.add(new ListItem(
//						"17.	That the you shall invite objections from the existing allottees (If any) of License Nos. 94 of 2013 dated 31.10.2013  & License No. 11 of 2015 dated 01.10.2015 General Public through an  advertisement to be issued atleast in three National Newspapers within a period of 10 days from the issuance of LOI, informing about the revision of layout plan, with a request to submit objections if any, in writing within 30 days from the date of publication of such public notice in the office of Senior Town Planner, Gurugram and applicant company shall inform all the third parties who have got rights created under original licence, through Registered post with a copy endorsed to Senior Town Planner, Gurugram within two days from advertisement clearly indicating the last date for submission of objection. You shall submit report clearly indicating the objection, if any, received by him from allottees /general public and action taken thereof alongwith an undertaking to the effect that the rights of the existing plot holders have not been infringed. Any allottees/general public having any objection may file his/ her objection in the office of Senior Town Planner Gurugram also. The Public Notice may be published in atleast three National newspapers widely circulated in District, of which one should be in Hindi Language.",
//						normal));
//				list.add(new ListItem(
//						"That the you shall invite objections from the existing allottees (If any) of "+licenseNo+" General Public through an  advertisement to be issued atleast in three National Newspapers within a period of 10 days from the issuance of LOI, informing about the revision of layout plan, with a request to submit objections if any, in writing within 30 days from the date of publication of such public notice in the office of Senior Town Planner, Gurugram and applicant company shall inform all the third parties who have got rights created under original licence, through Registered post with a copy endorsed to Senior Town Planner, Gurugram within two days from advertisement clearly indicating the last date for submission of objection. You shall submit report clearly indicating the objection, if any, received by him from allottees /general public and action taken thereof alongwith an undertaking to the effect that the rights of the existing plot holders have not been infringed. Any allottees/general public having any objection may file his/ her objection in the office of Senior Town Planner Gurugram also. The Public Notice may be published in atleast three National newspapers widely circulated in District, of which one should be in Hindi Language.",
//						normal));

				list.add(new ListItem(
						"That you shall intimate their official Email ID and the correspondence made to this email ID by the Department shall be treated legal.",
						normal));
//				if (requestLOIReport.getAddedContent() != null && !requestLOIReport.getAddedContent().isEmpty()) {
//					requestLOIReport.getAddedContent().stream().forEach(cd -> {
//						list.add(new ListItem(cd, normal));
//					});
//				}
				loiTableReportData(list, userId, applicationNumber, requestLOIReport);
				doc.add(list);
				doc.add(new Paragraph("DA/as above ", normal));
				addEmptyLine(para1, 1);
				Paragraph para12 = new Paragraph("Your filled data for following field(s) has not been approved:",
						normal);
				para12.setSpacingBefore(10f);
				Paragraph para13 = new Paragraph("".toUpperCase(), normal);
				para13.setSpacingBefore(10f);
//				doc.add(para12);
//				doc.add(para13);
//				loiTableReportData(doc, userId, applicationNumber, requestLOIReport);

				try {
					Paragraph preface0 = new Paragraph("(" + dtcpUserName + ", I.A.S)    ", smallBold);
					preface0.setIndentationLeft(10);
					preface0.setAlignment(Element.ALIGN_CENTER);
					preface0.setSpacingBefore(10f);
					Paragraph preface01 = new Paragraph("Director,", smallBold);
					preface01.setIndentationLeft(70);
					Paragraph preface02 = new Paragraph("Town & Country Planning", smallBold);
					preface02.setIndentationLeft(25);
					Paragraph preface3 = new Paragraph("Haryana Chandigarh", smallBold);
					preface3.setIndentationLeft(40);

					PdfDiv div = new PdfDiv();
					div.addElement(preface0);
					div.addElement(preface01);
					div.addElement(preface02);
					div.addElement(preface3);
					div.setPaddingLeft(320f);
					div.setFloatType(FloatType.RIGHT);
					doc.add(div);

					Paragraph preface4 = new Paragraph("Endst. " + memoNumber
							+ "/ 		                                    Dated: " + currentDate.split("\\s+")[0]);
					preface4.setAlignment(Element.ALIGN_LEFT);
					preface4.setSpacingBefore(10f);
					doc.add(preface4);
					Paragraph para102 = new Paragraph(
							"A copy is forwarded to the following alongwith copy of land schedule for information and necessary action:- ",
							normal);
					para102.setSpacingBefore(10f);
					Paragraph para103 = new Paragraph("".toUpperCase(), normal);
					para103.setSpacingBefore(10f);
					doc.add(para102);
					doc.add(para103);

					List subList1 = new List(List.ORDERED);
					subList1.setIndentationLeft(30f);
					subList1.add(new ListItem("Chief Administrator HSVP, Sector-6, Panchkula.", normal));
					subList1.add(new ListItem("Director, Urban Estates, Sector-6, Panchkula.", normal));
					subList1.add(new ListItem(
							"Senior Town Planner, Gurugram with a request send the report in respect of condition no. 17.",
							normal));
					subList1.add(new ListItem("District Forest Officer, Gurugram.", normal));
					subList1.add(new ListItem("District Town Planner, Gurugram.", normal));
					subList1.add(new ListItem("CAO O/o DGTCP, Haryana. ", normal));
					subList1.add(new ListItem("Nodal Officer (Website) O/o DGTCP, Hr.", normal));
					doc.add(subList1);

					Paragraph preface001 = new Paragraph("(" + hqUserName + ")", normal);
					preface001.setAlignment(Element.ALIGN_RIGHT);

					Paragraph preface102 = new Paragraph("District Town Planner (HQ)", normal);
					preface102.setAlignment(Element.ALIGN_RIGHT);

					Paragraph preface23 = new Paragraph("For: Director, Town & Country Planning", normal);
					preface23.setAlignment(Element.ALIGN_RIGHT);

					Paragraph preface34 = new Paragraph("Haryana, Chandigarh.", normal);
					preface34.setAlignment(Element.ALIGN_RIGHT);

					PdfDiv div2 = new PdfDiv();
					div2.addElement(preface001);
					div2.addElement(preface102);
					div2.addElement(preface23);
					div2.addElement(preface34);
					div2.setFloatType(FloatType.RIGHT);
					doc.add(div2);
				} catch (Exception e) {
					log.error("Exception : "+e.getMessage());
				}

				doc.close();
				writer.close();
			} catch (DocumentException e1) {
				log.error("DocumentException : "+e1.getMessage());
			} catch (Exception e) {
				log.error("Exception : "+e.getMessage());
				
			}
		}

	}

	/******************************************************
	 * : LOI Purpose : new Residential Plotted Colony end *
	 ******************************************************/

	/*****************************************************************
	 * 12: LOI Purpose : Integrated Residential Plotted Colony start *
	 *****************************************************************/

	private void getLoiReportIntegratedResidentialPlottedColony(String applicationNumber, String userId,
			String hqUserId, RequestLOIReport requestLOIReport) {
		String licenseNo = "";
		StringJoiner applicationDated = new StringJoiner("&");
		LicenseServiceResponseInfo licenseServiceResponceInfo = checkApplicationIsValid(requestLOIReport,
				applicationNumber, userId, hqUserId);
		if (licenseServiceResponceInfo == null) {
			return;
		} else {
			try {
				String myFile = loireportPath;
				Document doc = new Document(PageSize.A4);
				PdfWriter writer = PdfWriter.getInstance(doc,
						new FileOutputStream(myFile + "loi-report-" + applicationNumber + ".pdf"));
				doc.open();
				addTitlePage(doc, 1);
				memoNumber = "LC- " + (licenseServiceResponceInfo.getCaseNumber() != null
						? licenseServiceResponceInfo.getCaseNumber().replaceAll("LC", "").split("-")[0]
						: "N/A") + "-A/JE(SB)/-2022";// + (currentDate.split("\\s+")[0].split("\\.")[2]);
				int licenseList = licenseServiceResponceInfo.getNewServiceInfoData().size();
				if (licenseList > 1) {
					licenseNo = " (Part area migration from License No. "
							+ licenseServiceResponceInfo.getNewServiceInfoData().stream().map(license -> {
								return "" + applicationNumber.substring(12) + " of " + applicationNumber.substring(4, 8)
										+ " dated " + applicationDate.split("\\s+")[0] + " (" + totalArea
										+ ") granted for development of Residential Plotted Colony";
							}).collect(Collectors.toList()).stream().collect(Collectors.joining(" , ")) + " ) ";

				}

				licenseServiceResponceInfo.getNewServiceInfoData().stream().forEach(license -> {
					applicationDated.add(applicationDate.split("\\s+")[0]);
				});

//				licenseNo="License No. 94 of 2013 dated 31.10.2013 & License No. 11 of 2015 dated 01.10.2015";

				int leftMarg = 70;
				Paragraph preface1 = new Paragraph();
				preface1.add(new Paragraph("To", smallBold));
				doc.add(preface1);
				addEmptyLine(preface1, 2);
				Paragraph preface2 = new Paragraph();
				preface2.setIndentationLeft(leftMarg);
				try {
					ApplicantInfo applicationInfo = licenseDetails.getApplicantInfo();
					preface2.add(new Paragraph(applicationInfo.getDevDetail().getAddInfo().getCompanyName()));
					preface2.add(new Paragraph(applicationInfo.getDevDetail().getAddInfo().getRegisteredAddress()));
//					preface2.add(new Paragraph((applicationInfo.getState() == null ? "N/A" : applicationInfo.getState())
//							+ "-" + applicationInfo.getDevDetail().getLicenceDetails().getPincode()));
				} catch (Exception e) {
					preface2.add(new Paragraph("_________  Pvt. Ltd.,"));
					preface2.add(new Paragraph("--, -----------, ----------,"));
					preface2.add(new Paragraph("---------------."));
					e.printStackTrace();
				}
				doc.add(preface2);
				
				Paragraph loiNumberParagraph = new Paragraph(
						"LOI Number : " + loiNumber);
				loiNumberParagraph.setIndentationLeft(leftMarg);
				loiNumberParagraph.setSpacingBefore(15f);
				doc.add(loiNumberParagraph);
				
				Paragraph paragraph = new Paragraph("Memo No. " + memoNumber
						+ "/ 		                                    Dated: " + currentDate.split("\\s+")[0]);
				paragraph.setIndentationLeft(leftMarg);
				paragraph.setSpacingBefore(15f);
				doc.add(paragraph);
				addEmptyLine(paragraph, 1);

				Paragraph preface12 = new Paragraph();
				preface12.add(new Paragraph(
						"Subject:         Letter of Intent for grant of license to setting up of Integrated Residential Plotted",
						smallBold));
				preface12.setSpacingBefore(15f);
				doc.add(preface12);
				Paragraph preface13 = new Paragraph();
				preface13.add(new Paragraph(
						"Colony under NILP – 2016 further amended on 11.05.2022 over an area measuring " + totalArea
								+ " acres " + licenseNo + " in the revenue estate of " + address
								+ (!collaborationCompanyName.equals("N/A") ? (" - " + collaborationCompanyName) : ("")),
						smallBold));
				preface13.setIndentationLeft(leftMarg);
				doc.add(preface13);

				Paragraph preface14 = new Paragraph();
				preface14.add(new Paragraph(
						"Please refer your application dated " + applicationDated + " on the subject cited above.",
						smallBold));
				preface14.setIndentationLeft(leftMarg);
				preface14.setSpacingBefore(20f);
				preface14.setSpacingAfter(20f);
				doc.add(preface14);

				List subList2 = new RomanList();
				subList2.setPostSymbol(". ");
				subList2.setIndentationLeft(30f);
				List subList3 = new RomanList();// new List(List.ORDERED, List.ALPHABETICAL);
				subList3.setLowercase(List.LOWERCASE);
				subList3.setPostSymbol(". ");
				subList3.setIndentationLeft(15f);

				List list = new List(List.ORDERED);
				list.setFirst(2);
				List list1 = new List(List.ORDERED);
				List subList = new List(List.ORDERED, List.ALPHABETICAL);
				subList.setLowercase(List.UPPERCASE);
				subList.setPostSymbol(". ");
				subList.setIndentationLeft(30f);

				List subList5 = new List(List.UNORDERED);
				subList5.setIndentationLeft(30f);

				Chunk bullet = new Chunk("\u2022 ", new Font());
				List subListI = new List(List.UNORDERED);
				subListI.setListSymbol(bullet);
				subListI.setIndentationLeft(30f);

				List subListE = new List(List.UNORDERED);
				subListE.setListSymbol(bullet);
				subListE.setIndentationLeft(30f);

				Paragraph content1 = new Paragraph(
						"Your request for grant of licence under section 3 of the Haryana Development and Regulation ",
						normal);
				content1.setIndentationLeft(leftMarg);
				Paragraph content = new Paragraph(
						"of Urban Areas Act, 1975, and Rules framed there under for development of Integrated Residential Plotted Colony under NILP – 2016 further amended on 11.05.2022 over an area measuring "
								+ totalArea + " acres " + licenseNo + " in the revenue estate of " + address
								+ " has been considered and it is proposed to grant license for setting up of aforesaid plotted colony. You are, therefore, called upon to fulfill the following requirements/ pre-requisites laid down in Rule 11 of the Haryana Development and Regulation of Urban Areas Rule, 1976 within a period of 60 days from the date of issue of this letter, failing which request for grant of license shall be refused: - ",
						normal);
				;
				Paragraph para1 = new Paragraph("", normal);
				addEmptyLine(para1, 1);
				doc.add(content1);
				doc.add(content);
				doc.add(new Paragraph(
						"1. To furnish bank guarantee on account of Internal Development Works and External Development Charges for the amount calculated as under: -",
						normal));

				doc.add(para1);
				
				Double plottedInternalCost = 20 * Double.parseDouble(plottedComponent);
				Double comericalInternalCost = 50 * Double.parseDouble(comericalComponent);
				Double totalInternalCost = plottedInternalCost + comericalInternalCost;
				Double bg25InternalPercentage = totalInternalCost * 0.25;
				
				Double plottedExternalCost = zoneWiseEdcAmount * Double.parseDouble(plottedComponent)* 5/7;
				Double comericalExternalCost = zoneWiseEdcAmount * Double.parseDouble(comericalComponent);
				Double totalExternalCost = plottedExternalCost + comericalExternalCost;
				Double bg25ExternalPercentage = totalExternalCost * 0.25;


				subListI.add(new ListItem(" Plotted component				= "+plottedComponent+" acres", normal));
				subListI.add(new ListItem(" Rate per acre					= Rs. 20.00 lacs per acre", normal));
				subListI.add(new ListItem(" Cost of Plotted Component			= Rs. "+plottedInternalCost+" Lacs", normal));
				subListI.add(new ListItem(" Area under Commercial component		= "+comericalComponent+" acre ", normal));
				subListI.add(new ListItem(" Rate per acre					= Rs. 50.00 lacs per acre", normal));
				subListI.add(new ListItem(" Cost of commercial component		= Rs."+comericalInternalCost+" Lacs", normal));
				subListI.add(new ListItem(" Total Cost of development			= Rs. "+totalInternalCost+" Lacs", normal));
				subListI.add(new ListItem(" 25% BG, which is required 			= Rs. "+bg25InternalPercentage+" Lacs (valid for 5 years)",
						normal));

				subListE.add(new ListItem("Total Area under Plotted component		= "+plottedComponent+" acres \r\n"
						+ "Interim rate for EDC @ Rs "+zoneWiseEdcAmount+" (EDC) Lac x 5/7 per acres ", normal));
				subListE.add(new ListItem("EDC Amount for Plotted component		= Rs. "+plottedExternalCost+" Lacs", normal));
				subListE.add(new ListItem("Area under commercial component		= "+comericalComponent+" acres\n"
						+ "Interim rate for EDC @ Rs "+zoneWiseEdcAmount+" (EDC) Lac per acres ", normal));
				subListE.add(new ListItem("EDC Amount for Commercial component	= Rs. "+comericalExternalCost+" Lacs", normal));
				subListE.add(new ListItem("Total cost of development			= Rs. "+totalExternalCost+" Lacs", normal));
				subListE.add(new ListItem("25% bank guarantee required			=Rs. "+bg25ExternalPercentage+" Lacs (valid for 5 years)",
						normal));

				subList.add(new ListItem(new ListItem("INTERNAL DEVELOPMENT WORKS:")));
				subList.add(subListI);

				subList.add(new ListItem(new ListItem("EXTERNAL DEVELOPMENT CHARGES (EDC): ")));
				subList.add(subListE);
				list1.add(subList);
				doc.add(list1);
				list.add(new ListItem(
						"It is made clear that the Bank Guarantee of Internal Development Works has been worked out on the interim rates and you have to submit an additional Bank Guarantee if any, required at the time of approval of Service Plan/Estimate according to the approved layout plan. With an increase in the cost of construction and an increase in the number of facilities in the layout plan, you would be required to furnish an additional bank guarantee within 30 days on demand. In the event of increase of rates of external development charges, you will have to pay the enhanced rates of external development charges as finally determined and as and when demanded by the DTCP, Haryana and furnish additional bank guarantee and submit an undertaking in this regard.",
						normal));
				list.add(new ListItem(
						"To execute two agreements i.e. LC-IV and Bilateral Agreement on non-judicial stamp paper. Two copies of specimen of the said agreements are enclosed herewith for necessary action.",
						normal));
				list.add(new ListItem("To deposit an amount of " + scrutinyFee + "/-(" + scrutinyFeeInWord
						+ ") on account of deficit scrutiny fee to be deposited online at website i.e. www.tcpharyana.gov.in",
						normal));
				list.add(new ListItem("To deposit an amount of " + licenseFees + "/-(" + licenseFeesInWord
						+ ") on account of Balance license fee to be deposited online at website i.e. www.tcpharyana.gov.in",
						normal));
				list.add(new ListItem("To deposit an amount of " + conversionCharges + "/-(" + conversionChargesInWord
						+ ") on account of conversion charges to be deposited online at website i.e. www.tcpharyana.gov.in",
						normal));
				list.add(new ListItem("To submit an undertaking on non-judical stamp paper to the effect that:-",
						normal));
				subList3.add(new ListItem(
						"That you shall pay the State Infrastructure Development Charges amounting to "
								+ stateInfrastructureDevelopmentCharges + "/-("
								+ stateInfrastructureDevelopmentChargesInWord
								+ "). First Installment will be due within 60 days of grant of license and second Installment within six months of grant of license failing which 18% PA interest will be liable for the delayed period.",
						normal));
				subList3.add(new ListItem("That you shall pay an amount of " + licenseFees + "/-(" + licenseFeesInWord
						+ ") in lieu of 10% land to be transferred in favour of Government (as per clause no. 3.1 of policy dated 11.05.2022) before grant of licence.",
						normal));
				subList3.add(new ListItem(
						"That the licensee shall maintain and upkeep of all roads, open spaces, public park and public health services for a period of five years from the date of issue of the completion certificate unless earlier relieved of this responsibility and thereupon to transfer all such roads, open spaces, public parks and public health services free of cost to the Govt. or the local authority, as the case may be, in accordance with the provisions of Section 3(3)(a)(iii) of the Haryana Development and Regulation of Urban Areas Act, 1975.",
						normal));
				subList3.add(new ListItem(
						"That area coming under the sector roads and restricted belt/green belt, if any, which forms part of licensed area and in lieu of which benefit to the extent permissible as per policy towards FAR is being granted, shall be transferred free of cost to the Govt. ",
						normal));
				subList3.add(new ListItem(
						"That you shall construct portion of service road, internal circulation roads, forming the part of site area at your own cost and shall transfer the land falling within alignment of same free of cost to the Govt. u/s 3(3) (a) (iii) of the Haryana Development and Regulation of Urban Areas Act, 1975.",
						normal));
				subList3.add(new ListItem(
						"That you shall integrate the services with Haryana Shehri Vikas Pradhikaran services as and when made available.",
						normal));
				subList3.add(new ListItem(
						"The you have not submitted any other application for grant of license for development of the said land or part thereof for any purpose under the provisions of the Haryana Development and Regulation of Urban Areas Act, 1975 or any application seeking permission for change of land use under the provision of the Punjab Scheduled Roads and Controlled Area Restrictions of Unregulated Development Act, 1963.",
						normal));
				subList3.add(new ListItem(
						"That you shall understand that the development/construction cost of 24 m/18 m major internal roads is not included in the EDC rates and they shall pay the proportionate cost for acquisition of land, if any, alongwith the construction cost of 24 m/18 m wide major internal roads as and when finalized and demanded by the Department.",
						normal));
				subList3.add(new ListItem(
						"That you shall obtain NOC/Clearance as per provisions of notification dated 14.09.2006 issued by Ministry of Environment & Forest, Govt. of India, if applicable before execution of development works at site.",
						normal));
				subList3.add(new ListItem(
						"That you shall make your own arrangements for water supply, sewerage, drainage etc. to the satisfaction of DTCP till these services are made available and the same is made functional from External Infrastructure to be laid by Haryana Shahari Vikas Pradhikaran services or any other execution agency.",
						normal));
				subList3.add(new ListItem(
						"That you shall obtain clearance from competent authority, if required under Punjab Land Preservation Land Act, 1900 and any other clearance required under any other law.",
						normal));
				subList3.add(new ListItem(
						"That the rain water harvesting system shall be provided as per Central Ground Water Authority Norms/Haryana Govt. notification as applicable.",
						normal));
				subList3.add(new ListItem(
						"That the provision of solar water heating system shall be as per guidelines of Haryana Renewable Energy Development Agency and shall be made operational where applicable before applying for an Occupation Certificate.",
						normal));
				subList3.add(new ListItem(
						"That you shall use only LED fitting for internal lighting as well as campus lighting.",
						normal));
				subList3.add(new ListItem(
						"That you shall convey the ‘Ultimate Power Load Requirement’ of the project to the concerned power utility, with a copy to the Director, within two months period from the date of grant of license to enable provision of site in licensed land for Transformers/Switching Stations/Electric Sub Stations as per the norms prescribed by the power utility in the zoning plan of the project.",
						normal));
				subList3.add(new ListItem(
						"That it will be made clear at the time of booking of plots/commercial space that specified rates include or do not include EDC. In case of not inclusion of EDC in the booking rates, then it may be specified that same are to be charged separately as per rate fixed by the Govt. You shall also provide detail of calculation of EDC per sqm/per sft to the allottees while raising such demand from the plot owners.",
						normal));
				subList3.add(new ListItem(
						"That you shall keep pace of development at least in accordance with sale agreement executed with the buyers of the plots as and when scheme is launched. ",
						normal));

				subList3.add(new ListItem(
						"That you shall arrange power connection from UHBVNL/DHBVNL for electrification of the colony and shall install the electricity distribution infrastructure as per the peak load requirement of the colony for which licencee shall get the electrical (distribution) service plan/estimates approved from the agency responsible for installation of external electric services i.e. UHBVNL/DHBVNL and complete the same before obtaining completion certificate for the colony.",
						normal));
				subList3.add(new ListItem(
						"That you will pay the labour cess as per policy instructions issued by Haryana Government vide memo no. Misc-2057-5/25/2008/2TCP dated 25.02.2010.",
						normal));
				subList3.add(new ListItem(
						"That you shall submit compliance of Rule 24, 26, 27 & 28 of Rules 1976 & Section 5 of Haryana Development and Regulation of Urban Areas Act, 1975, and shall inform account number and full particulars of the scheduled bank wherein licencee have to deposit thirty percentage of the amount received from the plot holders for meeting the cost of Internal Development Works in the colony. ",
						normal));
				subList3.add(new ListItem(
						"That no further sale has taken place after submitting application for grant of license.",
						normal));
				subList3.add(new ListItem(
						"That you shall not give any advertisement for sale of plots/ commercial are before the approval of layout plan.",
						normal));
				subList3.add(new ListItem(
						"That you shall construct the access to the site upto higher order road in concurrence with the concerned authority before allotment of plot.",
						normal));
				subList3.add(new ListItem(
						"That you shall follow the provisions of the Real Estate (Regulations and Development) Act, 2016 and Rules framed thereunder shall be followed by the applicant in letter and spirit.",
						normal));
				subList3.add(new ListItem(
						"That no provision of the Haryana Ceiling on Land Holding Act, 1972 has been violated due to purchase of applied land.",
						normal));
				subList3.add(new ListItem(
						"That you shall abide with policy dated 18.02.2016, NILP-09.02.2016, 05.12.2018, 18.09.2019 and 11.05.2022 as amended from time to time related to allotment of EWS Flats/Plots.",
						normal));
				subList3.add(new ListItem(
						"That you shall integrate the bank account in which 70 percent allottee receipts are credited under Section-4(2)(I)(D) of the Real Estate Regulation and Development Act, 2016 with the online application/payment gateway of the Department, in such manner, so as to ensure that 10% of the total receipt from each payment made by an allottee is automatically deducted and gets credited to the EDC head in the State treasury.",
						normal));

				subList3.add(new ListItem(
						"That such 10% of the total receipt from each payment made by the allottee, which is received by the Department shall get automatically credited, on the date of receipt in the Government treasury against EDC dues.",
						normal));
				subList3.add(new ListItem(
						"That such 10% deduction shall continue to operate till the total EDC dues get recovered from the owner/developer.",
						normal));
				subList3.add(new ListItem(
						"The implementation of such mechanism shall, however, have no bearing on EDC installment schedule conveyed to the owner/developer. The owner/developer shall continue to supplement such automatic EDC deductions with payments from its own funds to ensure that by the EDC installments that are due for payment that paid as per the prescribed schedule. ",
						normal));
				subList3.add(new ListItem(
						"That you shall execute the development works as per Environmental Clearance and comply with the provisions of Environment Protection Act, 1986, Air (Prevention and Control of Pollution of Act 1981) and Water (Prevention and Control of Pollution of 1974). In case of any violation of the provisions of said statutes, you shall be liable for penal action by Haryana State Pollution Controlled Board or any other Authority Administering the said Act.",
						normal));
				subList3.add(new ListItem(
						"That you shall abide by all the provisions of Act no. 8 of 1975 and Rules framed thereunder as amended time to time.",
						normal));

				subList3.add(new ListItem(
						"That the owner/developer shall derive maximum net profit at the rate of 15% of the total project cost of the development of the above said Residential Plotted Colony after making provisions of the statutory taxes. In case, the net profit exceeds 15% after completion of the project period, the surplus amount shall be deposited within two months in the State Government Treasury by the Owner/Developer or they shall spend this money on further amenities/facilities in their colony for the benefit of the resident therein. ",
						normal));
				subList3.add(new ListItem(
						"That you shall not encroach the revenue rasta passing through the applied site and keep them open for thoroughfare movement of the general public.",
						normal));
				subList3.add(new ListItem(
						"That licenced land forming the part of Sector, Road, Service roads, Green belts and 24/18 mtrs wide road as the case may be land pockets which are earmarked for community sites shall be transferred within a period of 30 days in favour of Government from the date of approval of Zoning Plan.",
						normal));
				subList3.add(new ListItem(
						"That you shall abide by Resolution Plan approved by Hon’ble NCLT vide order dated 06.08.2021. ",
						normal));
				subList3.add(new ListItem(
						"That you shall complete the project within seven years (5+2 years) from date of grant of license as per clause 9.2 of the policy dated 11.05.2022.",
						normal));
				list.add(subList3);
				list.add(new ListItem(
						"That you shall submit an indemnity bond, indemnifying by the DTCP against any loss/claim arising out of any pending litigation.",
						normal));
				list.add(new ListItem("That certificate from Deputy Commissioner, " + disticName
						+ " will be submitted certifying that the applied land is still under ownership of applicant company.",
						normal));
				list.add(new ListItem(
						"The you shall intimate your official Email ID and the correspondence on this email ID by the Deptt. will be treated receipt of such correspondence.",
						normal));

//				if (requestLOIReport.getAddedContent() != null && !requestLOIReport.getAddedContent().isEmpty()) {
//					requestLOIReport.getAddedContent().stream().forEach(cd -> {
//						list.add(new ListItem(cd, normal));
//					});
//				}
				loiTableReportData(list, userId, applicationNumber, requestLOIReport);
				doc.add(list);
				doc.add(new Paragraph("DA/as above ", normal));
				addEmptyLine(para1, 1);
				Paragraph para12 = new Paragraph("Your filled data for following field(s) has not been approved:",
						normal);
				para12.setSpacingBefore(10f);
				Paragraph para13 = new Paragraph("".toUpperCase(), normal);
				para13.setSpacingBefore(10f);
//				doc.add(para12);
//				doc.add(para13);
//				loiTableReportData(doc, userId, applicationNumber, requestLOIReport);

				try {
					Paragraph preface0 = new Paragraph("(" + dtcpUserName + ", I.A.S)    ", smallBold);
					preface0.setIndentationLeft(10);
					preface0.setAlignment(Element.ALIGN_CENTER);
					preface0.setSpacingBefore(10f);
					Paragraph preface01 = new Paragraph("Director,", smallBold);
					preface01.setIndentationLeft(70);
					Paragraph preface02 = new Paragraph("Town & Country Planning", smallBold);
					preface02.setIndentationLeft(25);
					Paragraph preface3 = new Paragraph("Haryana Chandigarh", smallBold);
					preface3.setIndentationLeft(40);

					PdfDiv div = new PdfDiv();
					div.addElement(preface0);
					div.addElement(preface01);
					div.addElement(preface02);
					div.addElement(preface3);
					div.setPaddingLeft(320f);
					div.setFloatType(FloatType.RIGHT);
					doc.add(div);

					Paragraph preface4 = new Paragraph("Endst. " + memoNumber
							+ "/ 		                                    Dated: " + currentDate.split("\\s+")[0]);
					preface4.setAlignment(Element.ALIGN_LEFT);
					preface4.setSpacingBefore(10f);
					doc.add(preface4);
					Paragraph para102 = new Paragraph(
							"A copy is forwarded to the followings for information and necessary action:-", normal);
					para102.setSpacingBefore(10f);
					Paragraph para103 = new Paragraph("".toUpperCase(), normal);
					para103.setSpacingBefore(10f);
					doc.add(para102);
					doc.add(para103);

					List subList1 = new List(List.ORDERED);
					subList1.setIndentationLeft(30f);
					subList1.add(new ListItem("The Deputy Commissioner, Panchkula.", normal));
					subList1.add(new ListItem(
							"District Revenue Officer, Panchkula with a request to provide the collector rate applicable on the applied land. ",
							normal));
					subList1.add(new ListItem(
							"Senior Town Planner, Gurugram with a request send the report in respect of condition no. 17.",
							normal));
					subList1.add(new ListItem("Senior Town Planner, Panchkula.", normal));
					subList1.add(new ListItem("District Town Planner, Panchkula.", normal));
					subList1.add(new ListItem("Project Manager (IT) with the request to update the status on website.",
							normal));
					doc.add(subList1);

					Paragraph preface001 = new Paragraph("(" + hqUserName + ")", normal);
					preface001.setAlignment(Element.ALIGN_RIGHT);

					Paragraph preface102 = new Paragraph("District Town Planner (HQ)", normal);
					preface102.setAlignment(Element.ALIGN_RIGHT);

					Paragraph preface23 = new Paragraph("For: Director, Town & Country Planning", normal);
					preface23.setAlignment(Element.ALIGN_RIGHT);

					Paragraph preface34 = new Paragraph("Haryana, Chandigarh.", normal);
					preface34.setAlignment(Element.ALIGN_RIGHT);

					PdfDiv div2 = new PdfDiv();
					div2.addElement(preface001);
					div2.addElement(preface102);
					div2.addElement(preface23);
					div2.addElement(preface34);
					div2.setFloatType(FloatType.RIGHT);
					doc.add(div2);
				} catch (Exception e) {
					log.error("Exception : "+e.getMessage());
				}

				doc.close();
				writer.close();
			} catch (DocumentException e1) {
				log.error("DocumentException : "+e1.getMessage());
			} catch (Exception e) {
				log.error("Exception : "+e.getMessage());
				
			}
		}

	}

	/*****************************************************************
	 * : LOI Purpose : Integrated Residential Plotted Colony start *
	 ****************************************************************/

	/**********************************************************
	 * 13: LOI Purpose : Commercial Integrated Colony start *
	 **********************************************************/

	private void getLoiReportCommercialIntegratedColony(String applicationNumber, String userId, String hqUserId,
			RequestLOIReport requestLOIReport) {
//		String licenseNo = null;
		StringJoiner applicationDated = new StringJoiner("&");
		LicenseServiceResponseInfo licenseServiceResponceInfo = checkApplicationIsValid(requestLOIReport,
				applicationNumber, userId, hqUserId);
		if (licenseServiceResponceInfo == null) {
			return;
		} else {
			try {
				String myFile = loireportPath;
				Document doc = new Document(PageSize.A4);
				PdfWriter writer = PdfWriter.getInstance(doc,
						new FileOutputStream(myFile + "loi-report-" + applicationNumber + ".pdf"));
				doc.open();
				addTitlePage(doc, 1);
				memoNumber = "LC- " + (licenseServiceResponceInfo.getCaseNumber() != null
						? licenseServiceResponceInfo.getCaseNumber().replaceAll("LC", "").split("-")[0]
						: "N/A") + "/JE(SB) 2023";// + (currentDate.split("\\s+")[0].split("\\.")[2]);
//				int licenseList = licenseServiceResponceInfo.getNewServiceInfoData().size();
//				if (licenseList > 1) {
//					licenseNo = licenseServiceResponceInfo.getNewServiceInfoData().stream().map(license -> {
//						return "License No. " + applicationNumber.substring(12) + " of "
//								+ applicationNumber.substring(4, 8) + " dated " + applicationDate.split("\\s+")[0];
//					}).collect(Collectors.toList()).stream().collect(Collectors.joining(" & "));
//
//				}

				licenseServiceResponceInfo.getNewServiceInfoData().stream().forEach(license -> {
					applicationDated.add(applicationDate.split("\\s+")[0]);
				});

//				licenseNo="License No. 94 of 2013 dated 31.10.2013 & License No. 11 of 2015 dated 01.10.2015";

				int leftMarg = 70;
				Paragraph preface1 = new Paragraph();
				preface1.add(new Paragraph("To", smallBold));
				doc.add(preface1);
				addEmptyLine(preface1, 2);
				Paragraph preface2 = new Paragraph();
				preface2.setIndentationLeft(leftMarg);
				try {
					ApplicantInfo applicationInfo = licenseDetails.getApplicantInfo();
					preface2.add(new Paragraph(applicationInfo.getDevDetail().getAddInfo().getCompanyName()));
					preface2.add(new Paragraph(applicationInfo.getDevDetail().getAddInfo().getRegisteredAddress()));
//					preface2.add(new Paragraph((applicationInfo.getState() == null ? "N/A" : applicationInfo.getState())
//							+ "-" + applicationInfo.getDevDetail().getLicenceDetails().getPincode()));
				} catch (Exception e) {
					preface2.add(new Paragraph("_________  Pvt. Ltd.,"));
					preface2.add(new Paragraph("--, -----------, ----------,"));
					preface2.add(new Paragraph("---------------."));
					e.printStackTrace();
				}
				doc.add(preface2);
				
				Paragraph loiNumberParagraph = new Paragraph(
						"LOI Number : " + loiNumber);
				loiNumberParagraph.setIndentationLeft(leftMarg);
				loiNumberParagraph.setSpacingBefore(15f);
				doc.add(loiNumberParagraph);
				
				Paragraph paragraph = new Paragraph("Memo No. " + memoNumber
						+ "/ 		                                    Dated: " + currentDate.split("\\s+")[0]);
				paragraph.setIndentationLeft(leftMarg);
				paragraph.setSpacingBefore(15f);
				doc.add(paragraph);
				addEmptyLine(paragraph, 1);

				Paragraph preface12 = new Paragraph();
				preface12.add(new Paragraph(
						"Subject:         Letter of Intent – Request for grant of licence for setting up of Commercial Colony",
						smallBold));
				preface12.setSpacingBefore(15f);
				doc.add(preface12);
				Paragraph preface13 = new Paragraph();
				preface13.add(new Paragraph("over an area measuring " + totalArea
						+ " acres falling in the revenue estate of " + address + ".", smallBold));
				preface13.setIndentationLeft(leftMarg);
				doc.add(preface13);

				Paragraph preface14 = new Paragraph();
				preface14.add(new Paragraph(
						"Please refer to your application dated " + applicationDated + " on the above cited subject.",
						smallBold));
//				preface14.add(new Paragraph("Please refer your application dated "+applicationDate.split("\\s+")[0]+", 19.02.2022 & 13.06.2022 on the matter cited as subject above.", smallBold));
				preface14.setIndentationLeft(leftMarg);
				preface14.setSpacingBefore(20f);
				preface14.setSpacingAfter(20f);
				doc.add(preface14);

				List subList2 = new RomanList();
				subList2.setPostSymbol(". ");
				subList2.setIndentationLeft(30f);
				List subList3 = new RomanList();// new List(List.ORDERED, List.ALPHABETICAL);
				subList3.setLowercase(List.LOWERCASE);
				subList3.setPostSymbol(". ");
				subList3.setIndentationLeft(15f);

				List list = new List(List.ORDERED);
				list.setFirst(2);
				List list1 = new List(List.ORDERED);
				List subList = new List(List.ORDERED, List.ALPHABETICAL);
				subList.setLowercase(List.UPPERCASE);
				subList.setPostSymbol(". ");

				List subList5 = new List(List.UNORDERED);
				subList5.setIndentationLeft(30f);

				subList.setIndentationLeft(30f);
				List subListI = new RomanList();
				subListI.setLowercase(List.LOWERCASE);
				subListI.setPostSymbol(". ");
				subListI.setIndentationLeft(30f);
				List subListE = new RomanList();
				subListE.setLowercase(List.LOWERCASE);
				subListE.setPostSymbol(". ");
				subListE.setIndentationLeft(30f);

				Paragraph content1 = new Paragraph(
						"Your request for the grant of license under section 3 of the Haryana Development and", normal);
				content1.setIndentationLeft(leftMarg);
				Paragraph content = new Paragraph(
						"Regulation of Urban Areas Act, 1975 and the Haryana Development and Regulation of Urban Areas Rules, 1976 framed thereunder for the development of a Commercial Colony over an area measuring "
								+ totalArea + " acres falling in the revenue estate of " + address
								+ " has been examined/considered by the Department under the policy dated 19.12.2006 and it is proposed to grant license to you. However, before grant of licence, you are called upon to fulfill the following requirements/pre-requisites laid down in Rule 11 of the Haryana Development and Regulation of Urban Areas Rules, 1976 within a period of 60 days from the date of issuance of this notice, failing which the grant of license shall be refused.",
						normal);
				;
				Paragraph para1 = new Paragraph("", normal);
				addEmptyLine(para1, 1);
				doc.add(content1);
				doc.add(content);
				doc.add(new Paragraph(
						"1. To furnish the bank guarantees on account of Internal Development Charges and the External Development Charges for the amount calculated as under:-",
						normal));

				doc.add(para1);
				
				
				Double comericalInternalCost = 50 * Double.parseDouble(comericalComponent);
				Double bg25InternalPercentage = comericalInternalCost * 0.25;
				
				Double comericalExternalCost = zoneWiseEdcAmount * Double.parseDouble(comericalComponent);
				Double bg25ExternalPercentage = comericalExternalCost * 0.25;

				

				subListI.add(new ListItem(" Commercial Area				= "+comericalComponent+" acres", normal));
				subListI.add(new ListItem(" Interim rate for development			= Rs. 50.00 Lac per acre", normal));
				subListI.add(new ListItem(" Total cost of development 			= Rs. "+comericalInternalCost+" Lac", normal));
				subListI.add(new ListItem(" 25% bank guarantee required			= Rs. "+bg25InternalPercentage+" Lacs \r\n"
						+ "							(valid for 5 years)", normal));

				subListE.add(new ListItem("Total Commercial Area 			= "+comericalComponent+" acres", normal));
				subListE.add(new ListItem("Interim rate for EDC				= Rs. "+zoneWiseEdcAmount+" Lac per acre", normal));
				subListE.add(new ListItem("Total EDC 					= Rs. "+comericalExternalCost+" Lac", normal));
				subListE.add(new ListItem("25% Bank Guarantee required			= Rs. "+bg25ExternalPercentage+" lacs\r\n"
						+ " 							    			 (valid for 5 years)", normal));

				subList.add(new ListItem(new ListItem(" Internal Development Works: ")));
				subList.add(subListI);

				subList.add(new ListItem(new ListItem("External Development Charges:  ")));
				subList.add(subListE);
				list1.add(subList);
				doc.add(list1);
				list.add(new ListItem(
						"It is made clear that the Bank Guarantee of Internal Development Works has been worked out on the interim rates and you have to submit the additional Bank Guarantee if any, required at the time of approval of Service Plan/Estimate according to the approved building plan. With an increase in the cost of construction and an increase in the number of facilities in the building plan, you would be required to furnish an additional bank guarantee within 30 days on demand. In the event of increase of rates of external development charges, you will have to pay the enhanced rates of external development charges as finally determined and as and when demanded by the DGTCP, Haryana and furnish additional bank guarantee and submit an undertaking in this regard",
						normal));
				list.add(new ListItem("To deposit an amount of " + licenseFees + "/-(" + licenseFeesInWord
						+ ") on account of balance license fee and an amount of " + conversionCharges + "/-("
						+ conversionChargesInWord
						+ ") on account of conversion charges to be deposited online at website i.e. www.tcpharyana.gov.in.",
						normal));
				list.add(new ListItem(
						"To furnish BG amounting Rs. "+bg25ExternalPercentage+" lac against External Development Charges amounting "
								+ comericalExternalCost + " lac. You have an option to mortgage 10% saleable area against submission of above said BG and in case, said option is adopted, then the area to be mortgaged may be indicated on the layout plan to be issued alongwith the license alongwith the revenue details thereof. The mortgage deed in this regard shall be executed as per the directions of the Department.",
						normal));
				subList5.add(new ListItem(
						"*It is made clear that rate of EDC has been calculated on the basis of EDC Indexation Mechanism Policy dated 11.02.2016, which stands approved by cabinet. If there will be any change and delay in the amendment in the Act/Rules w.r.t. the said rates, then differential amount from the original calculation will required to be deposited as per demand.",
						normalItalic));
				list.add(subList5);
				list.add(new ListItem("To furnish the Bank Guarantee of " + bg25InternalPercentage + "lac on account of Internal Development works to be deposited online at website i.e. www.tcpharyana.gov.in. You have an option to mortgage 10% saleable area against submission of above said BG and in case, said option is adopted, then the area to be mortgaged may be indicated on the layout plan to be issued alongwith the license alongwith the revenue details thereof. The mortgage deed in this regard shall be executed as per the directions of the Department.",
						normal));

				list.add(new ListItem(
						"To execute two agreements i.e. LC-IV & LC-IV-B Bilateral Agreement on Non-Judicial Stamp Paper of Rs. 100/-. Specimen copies of the said agreements are enclosed herewith for necessary action. Further, following additional clauses shall be added in LC-IV agreement as per Government instruction dated 14.08.2020.",
						normal));
				subList2.add(new ListItem(new ListItem(
						"That the owner/developer shall integrate the bank account in which 70 percent allottee receipts are credited under Section-4(2)(I)(D) of the Real Estate Regulation and Development Act, 2016 with the online application/payment gateway of the Department, in such manner, so as to ensure that 10% of the total receipt from each payment made by an allottee is automatically deducted and gets credited to the EDC head in the State treasury",
						normal)));
				subList2.add(new ListItem(new ListItem(
						"That such 10% of the total receipt from each payment made by the allottee, which is received by the Department shall get automatically credited, on the date of receipt in the Government treasury against EDC dues.  ",
						normal)));
				subList2.add(new ListItem(new ListItem(
						"That such 10% deduction shall continue to operate till the total EDC dues get recovered from the owner/developer.",
						normal)));
				subList2.add(new ListItem(new ListItem(
						"The implementation of such mechanism shall, however, have no bearing on EDC instalment schedule conveyed to the owner/developer. The owner/developer shall continue to supplement such automatic EDC deductions with payments from its own funds to ensure that by the EDC instalments that are due for payment that paid as per the prescribed schedule.",
						normal)));
				list.add(subList2);
				list.add(new ListItem(
						"To furnish an undertaking on non-judicial stamp paper of Rs. 100/- to the following effect that:- ",
						normal));
				subList3.add(new ListItem("You will pay the Infrastructure Development Charges amounting to "
						+ stateInfrastructureDevelopmentCharges + "/-(" + stateInfrastructureDevelopmentChargesInWord
						+ ") in two equal installments. First Installment will be due within 60 days of grant of license and second Installment within six months of grant of license failing which 18% PA interest will be liable for the delayed period.",
						normal));
				subList3.add(new ListItem(
						"That area coming under the sector roads and restricted belt / green belt, if any, which forms part of licensed area and in lieu of which benefit to the extent permissible as per policy towards FAR is being granted, shall be transferred free of cost to the Govt.",
						normal));
				subList3.add(new ListItem(
						"You shall maintain and upkeep of all roads, open spaces, public park and public health services for a period of five years from the date of issue of the completion certificate unless earlier relieved of this responsibility and thereupon to transfer all such roads, open spaces, public parks and public health services free of cost to the Govt. or the local authority, as the case may be, in accordance with the provisions of Section 3(3)(a)(iii) of the Haryana Development and Regulation of Urban Areas Act, 1975.",
						normal));
				subList3.add(new ListItem(
						"You shall construct portion of service road, internal circulation roads, forming the part of site area at your own cost and shall transfer the land falling within alignment of same free of cost to the Govt. u/s 3(3)(a)(iii) of the Haryana Development and Regulation of Urban Areas Act, 1975.",
						normal));
				subList3.add(new ListItem(
						"That you shall be liable to pay the actual rates of External Development Charges as and when determined and demanded as per prescribed schedule by the DGTCP Haryana.",
						normal));
				subList3.add(new ListItem(
						"That you shall integrate the services with Haryana Shehari Vikas Pradhikaran services as and when made available. ",
						normal));
				subList3.add(new ListItem(
						"That you have not submitted any other application for grant of license for development of the said land or part thereof for any purpose under the provisions of the Haryana Development and Regulation of Urban Areas Act, 1975 or any application seeking permission for change of land use under the provision of the Punjab Scheduled Roads and Controlled Area Restrictions of Unregulated Development Act, 1963.",
						normal));
				subList3.add(new ListItem(
						"That you have understood that the development /construction cost of 24 m/18 m major internal roads is not included in the EDC rates and applicant company shall pay the proportionate cost for acquisition of land, if any, alongwith the construction cost of 24 m/18 m wide major internal roads as and when finalized and demanded by the Department. ",
						normal));
				subList3.add(new ListItem(
						"That you shall obtain NOC/Clearance as per provisions of notification dated 14.09.06 issued by Ministry of Environment & Forest, Govt. of India before execution of development works at site.",
						normal));
				subList3.add(new ListItem(
						"That you shall make arrangements for water supply, sewerage, drainage etc. to the satisfaction of DGTCP till these services are made available from External Infrastructure to be laid by Haryana Shehari Vikas Pradhikaran.",
						normal));
				subList3.add(new ListItem(
						"That the rain water harvesting system shall be provided as per Central Ground Water Authority Norms/Haryana Govt. notification as applicable.",
						normal));
				subList3.add(new ListItem(
						"That you shall make provision of solar power system as per guidelines of Haryana Renewable Energy Development Agency and shall make operational where applicable before applying for an Occupation Certificate.",
						normal));
				subList3.add(new ListItem(
						"That you shall use only LED fitting for internal lighting as well as campus lighting.",
						normal));
				subList3.add(new ListItem(
						"That you shall convey the ‘Ultimate Power Load Requirement’ of the project to the concerned power utility, with a copy to the Director, within two months period from the date of grant of license to enable provision of site in licensed land for Transformers/Switching Stations/Electric Sub Stations as per the norms prescribed by the power utility in the zoning plan of the project.",
						normal));
				subList3.add(new ListItem(
						"That you shall submit compliance of Rule 24, 26, 27 & 28 of Rules 1976 & Section 5 of Haryana Development and Regulation of Urban Areas Act, 1975, and shall inform account number and full particulars of the scheduled bank wherein applicant company has to deposit thirty percentum of the amount from the floor/space holders for meeting the cost of Internal Development Works in the colony. ",
						normal));
				subList3.add(new ListItem(
						"That you shall permit the Director or any other office authorized by him to inspect the execution of the layout and the development works in the colony and to carry out all directions issued by him for ensuring due compliance of the execution of the layout and development works in accordance with the license granted.",
						normal));
				subList3.add(new ListItem(
						"That you shall not give any advertisement for sale of applied/licensed land before the approval of zoning plan / building plans of the same.",
						normal));
				subList3.add(new ListItem(
						"That you shall pay the labour cess as per policy instructions issued by Haryana Government vide Memo No. Misc. 2057-5/25/2008/2TCP dated 25.02.2010.",
						normal));
				subList3.add(new ListItem(
						"That you shall keep pace of construction at least in accordance with sale agreement executed with the buyers of the flats as and when scheme is launched.",
						normal));
				subList3.add(new ListItem(
						"That you shall submit the additional bank guarantee, if any required at the time of approval of Service Plans/Estimate. With an increase in the cost of construction and increase in the number of facilities in Layout Plan, applicant company would be required to furnish an additional bank guarantee within 30 days on demand. It is made clear that bank guarantee of Internal Development Works/EDC has been worked out on the interim rates.  ",
						normal));
				subList3.add(new ListItem(
						"That you shall specify the detail of calculations per Sqm/per sq ft, which is being demanded from the flat/shop owners on account of IDC/EDC, if being charged separately as per rates fixed by Govt.",
						normal));
				subList3.add(new ListItem(
						"That the provisions of the Real Estate (Regulation and Development) Act, 2016 and rules framed thereunder shall be followed by the applicant in letter and spirit.",
						normal));
				subList3.add(new ListItem(
						"That no pre-launch/sale of applied/licensed land will be undertaken before approval of the zoning/building plan.",
						normal));
				subList3.add(new ListItem("That applied land has not been used in any other development or license.",
						normal));
				subList3.add(new ListItem(
						"That the owner/developer shall derive maximum net profit at the rate of 15% of the total project cost of the development of the above said Commercial Colony after making provisions of the statutory taxes. In case, the net profit exceeds 15% after completion of the project period, the surplus amount shall be deposited within two months in the State Government Treasury by the Owner/Developer or they shall spend this money on further amenities/facilities in their colony for the benefit of the resident therein.",
						normal));
				subList3.add(new ListItem(
						"That you shall execute the development works as per Environmental Clearance and comply with the provisions of Environment Protection Act, 1986, Air (Prevention and Control of Pollution of Act, 1981) and Water (Prevention and Control of Pollution of 1974). In case of any violation of the provisions of said statutes, you shall be liable for penal action by Haryana State Pollution Control Board or any other Authority Administering the said Acts.",
						normal));
				subList3.add(new ListItem(
						"That you shall abide by with the Act/Rules and the policies notified by the Department for development of Commercial Colony and other instructions issued by the Director under section 9A of the Haryana Development and Regulations of Urban Areas Act, Haryana Development and Regulations of Urban Areas Act, Haryana Development and Regulations of Urban Areas Act, 1975.",
						normal));
				subList3.add(new ListItem(
						"That you shall abide with policy dated 19.12.2006 & 29.08.2019 and amended from time to time.",
						normal));
				subList3.add(new ListItem(
						"That licenced land forming the part of Sector, Road, Service roads, Green belts and 24/18 mtrs wide road as the case may be land pockets which are earmarked for community sites shall be transferred within a period of 30 days in favour of Government from the date of approval of Zoning Plan, if applicable.",
						normal));
				subList3.add(new ListItem(
						"That you shall obey all the directions/restrictions imposed by the Department from time to time.",
						normal));
				list.add(subList3);

				list.add(new ListItem(
						"That certificate from DRO/Deputy Commissioner, Gurugram will be submitted certifying that the applied land is still under ownership of applicant company before grant of licence",
						normal));
				list.add(new ListItem(
						"That you shall submit an indemnity bond indemnifying DGTCP from any loss, if occurs due to submission of undertaking submitted in respect of non-creation of third party rights on the applied land.",
						normal));
				list.add(new ListItem(
						"That you shall undertake to indemnify State Govt./ Department for loss occurred or legal complication arising due to pending litigation and the land owning / developer company will be responsible for the same in respect of applied land.",
						normal));
				list.add(new ListItem(
						"That you shall intimate their official Email ID and the correspondence made to this email ID by the Department shall be treated legal.",
						normal));

//				if (requestLOIReport.getAddedContent() != null && !requestLOIReport.getAddedContent().isEmpty()) {
//					requestLOIReport.getAddedContent().stream().forEach(cd -> {
//						list.add(new ListItem(cd, normal));
//					});
//				}
				loiTableReportData(list, userId, applicationNumber, requestLOIReport);
				doc.add(list);
				doc.add(new Paragraph("DA/schedule of land.", normal));
				addEmptyLine(para1, 1);
				Paragraph para12 = new Paragraph("Your filled data for following field(s) has not been approved:",
						normal);
				para12.setSpacingBefore(10f);
				Paragraph para13 = new Paragraph("".toUpperCase(), normal);
				para13.setSpacingBefore(10f);
//				doc.add(para12);
//				doc.add(para13);
//				loiTableReportData(doc, userId, applicationNumber, requestLOIReport);

				try {
					Paragraph preface0 = new Paragraph("(" + dtcpUserName + ", I.A.S)    ", smallBold);
					preface0.setIndentationLeft(10);
					preface0.setAlignment(Element.ALIGN_CENTER);
					preface0.setSpacingBefore(10f);
					Paragraph preface01 = new Paragraph("Director,", smallBold);
					preface01.setIndentationLeft(70);
					Paragraph preface02 = new Paragraph("Town & Country Planning", smallBold);
					preface02.setIndentationLeft(25);
					Paragraph preface3 = new Paragraph("Haryana Chandigarh", smallBold);
					preface3.setIndentationLeft(40);

					PdfDiv div = new PdfDiv();
					div.addElement(preface0);
					div.addElement(preface01);
					div.addElement(preface02);
					div.addElement(preface3);
					div.setPaddingLeft(320f);
					div.setFloatType(FloatType.RIGHT);
					doc.add(div);

					Paragraph preface4 = new Paragraph("Endst. " + memoNumber
							+ "/ 		                                    Dated: " + currentDate.split("\\s+")[0]);
					preface4.setAlignment(Element.ALIGN_LEFT);
					preface4.setSpacingBefore(10f);
					doc.add(preface4);
					Paragraph para102 = new Paragraph(
							"A copy is forwarded to the following alongwith copy of land schedule for information and necessary action:- ",
							normal);
					para102.setSpacingBefore(10f);
					Paragraph para103 = new Paragraph("".toUpperCase(), normal);
					para103.setSpacingBefore(10f);
					doc.add(para102);
					doc.add(para103);

					List subList1 = new List(List.ORDERED);
					subList1.setIndentationLeft(30f);
					subList1.add(new ListItem("Chief Administrator HSVP, Sector-6, Panchkula.", normal));
					subList1.add(new ListItem("Director, Urban Estates, Sector-6, Panchkula.", normal));
					subList1.add(new ListItem("Senior Town Planner, Gurugram.", normal));
					subList1.add(new ListItem("District Forest Officer, Gurugram.", normal));
					subList1.add(new ListItem(
							"District Town Planner, Gurugram with a request to send a copy of duly verified demarcation plan through Circle Office.",
							normal));
					subList1.add(new ListItem("CAO O/o DGTCP, Haryana. ", normal));
					subList1.add(new ListItem("Nodal Officer (Website) O/o DGTCP, Haryana.", normal));
					doc.add(subList1);

					Paragraph preface001 = new Paragraph("(" + hqUserName + ")", normal);
					preface001.setAlignment(Element.ALIGN_RIGHT);

					Paragraph preface102 = new Paragraph("District Town Planner (HQ)", normal);
					preface102.setAlignment(Element.ALIGN_RIGHT);

					Paragraph preface23 = new Paragraph("For: Director, Town & Country Planning", normal);
					preface23.setAlignment(Element.ALIGN_RIGHT);

					Paragraph preface34 = new Paragraph("Haryana, Chandigarh.", normal);
					preface34.setAlignment(Element.ALIGN_RIGHT);

					PdfDiv div2 = new PdfDiv();
					div2.addElement(preface001);
					div2.addElement(preface102);
					div2.addElement(preface23);
					div2.addElement(preface34);
					div2.setFloatType(FloatType.RIGHT);
					doc.add(div2);
				} catch (Exception e) {
					log.error("Exception : "+e.getMessage());
				}

				doc.close();
				writer.close();
			} catch (DocumentException e1) {
				log.error("DocumentException : "+e1.getMessage());
			} catch (Exception e) {
				log.error("Exception : "+e.getMessage());
				
			}
		}

	}

	/**********************************************************
	 * : LOI Purpose : Commercial Integrated Colony colony start *
	 **********************************************************/

	/****************************************************************************************************************************
	 * :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*
	 * :::::::::::::::::::::::::::::::::::::::::::Below is Root and Common Method ::::::::::::::::::::::::::::::::::::::::::::::*
	 * :::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::::*
	 ****************************************************************************************************************************
	 *																														    *
	 *																														    *
	 ***************************************************** LOI Purpose : Common method start************************************/

	public void getCalculatorData(String applicationNumber, LicenseDetails newobj, RequestLOIReport requestLOIReport) {
		decfor.setRoundingMode(RoundingMode.UP);
		
     try {
			TradeLicenseSearchCriteria tradeLicenseRequest = new TradeLicenseSearchCriteria();
			tradeLicenseRequest.setApplicationNumber(applicationNumber);
			java.util.List<TradeLicense> tradeLicenses = tradeLicenseService.getLicensesWithOwnerInfo(tradeLicenseRequest,
					requestLOIReport.getRequestInfo());
	
			// --------------------------calculation--------------------------------//
			StringBuilder calculatorUrl = new StringBuilder(guranteeHost);
			calculatorUrl.append(calculatorEndPoint);
	
			CalulationCriteria calulationCriteriaRequest = new CalulationCriteria();
			calulationCriteriaRequest.setTenantId(tradeLicenses.get(0).getTenantId());
			calulationCriteriaRequest.setTradelicense(tradeLicenses.get(0));
			java.util.List<CalulationCriteria> calulationCriteria = Arrays.asList(calulationCriteriaRequest);
	
			CalculatorRequest calculator = new CalculatorRequest();
			calculator.setApplicationNumber(applicationNumber);
			calculator.setPotenialZone(newobj.getApplicantPurpose().getAppliedLandDetails().get(0).getPotential());
			calculator.setPurposeCode(newobj.getApplicantPurpose().getPurpose());
	//		calculator.setTotalLandSize(new BigDecimal(newobj.getApplicantPurpose().getTotalArea()));
	
			Map<String, Object> calculatorMap = new HashMap<>();
			calculatorMap.put("CalulationCriteria", calulationCriteria);
			calculatorMap.put("CalculatorRequest", calculator);
			calculatorMap.put("RequestInfo", requestLOIReport.getRequestInfo());
			Object responseCalculator = serviceRequestRepository.fetchResult(calculatorUrl, calculatorMap);
	
			String data = null;
			try {
				data = mapper.writeValueAsString(responseCalculator);
			} catch (JsonProcessingException e) { // TODO Auto-generated catch block
				log.error("JsonProcessingException : "+e.getMessage());
			}
			CalculationRes calculationRes = null;
			ObjectReader objectReaders = mapper.readerFor(new TypeReference<CalculationRes>() {
			});
			try {
	
				calculationRes = objectReaders.readValue(data);
			} catch (IOException e) {
				log.error("IOException : "+e.getMessage());
			}
			
			
			double dblicenseFees=0.0;
			double dbscrutinyFees=0.0;
			double dbconversionCharges=0.0;
			double dbstateInfrastructureDevelopmentCharges=0.0;
			
			FeeAndBillingSlabIds charges = calculationRes.getCalculations().get(0).getTradeTypeBillingIds();
		
			
			zoneWiseEdcAmount = Optional.ofNullable(charges.getExternalDevelopmentCharges()).orElseThrow(RuntimeException::new);
			zoneWiseEdcAmount = Double.parseDouble(String.valueOf(zoneWiseEdcAmount).toString().replace("E", ""));
			zoneWiseEdcAmount = Double.parseDouble(decfor.format(zoneWiseEdcAmount));
			
			dblicenseFees=Optional.ofNullable(charges.getLicenseFeeCharges()).orElseThrow(RuntimeException::new);
			dblicenseFees = Double.parseDouble(String.valueOf(dblicenseFees).toString().replace("E", ""));
			dblicenseFees = Double.parseDouble(decfor.format(dblicenseFees));
			licenseFees = ConvertUtil.numberToComa(String.valueOf(dblicenseFees));
			licenseFeesInWord = ConvertUtil.numberToWords(String.valueOf(dblicenseFees));
			
			
			dbscrutinyFees=Optional.ofNullable(charges.getScrutinyFeeCharges()).orElseThrow(RuntimeException::new);
			dbscrutinyFees = Double.parseDouble(String.valueOf(dbscrutinyFees).toString().replace("E", ""));
			dbscrutinyFees = Double.parseDouble(decfor.format(dbscrutinyFees));
			scrutinyFee=ConvertUtil.numberToComa(String.valueOf(dbscrutinyFees));
			scrutinyFeeInWord=ConvertUtil.numberToWords(String.valueOf(dbscrutinyFees));

			
			dbconversionCharges=Optional.ofNullable(charges.getConversionCharges()).orElseThrow(RuntimeException::new);
			dbconversionCharges = Double.parseDouble(String.valueOf(dbconversionCharges).toString().replace("E", ""));
			dbconversionCharges = Double.parseDouble(decfor.format(dbconversionCharges));
			conversionCharges = ConvertUtil.numberToComa(String.valueOf(dbconversionCharges));
			conversionChargesInWord = ConvertUtil.numberToWords(String.valueOf(dbconversionCharges));
			
			
			
			dbstateInfrastructureDevelopmentCharges=Optional.ofNullable(charges.getStateInfrastructureDevelopmentCharges()).orElseThrow(RuntimeException::new);
			dbstateInfrastructureDevelopmentCharges = Double.parseDouble(String.valueOf(dbstateInfrastructureDevelopmentCharges).toString().replace("E", ""));
			dbstateInfrastructureDevelopmentCharges = Double.parseDouble(decfor.format(dbstateInfrastructureDevelopmentCharges));
			stateInfrastructureDevelopmentChargesInWord = ConvertUtil.numberToWords(String.valueOf(dbstateInfrastructureDevelopmentCharges));
			stateInfrastructureDevelopmentCharges = ConvertUtil.numberToComa(String.valueOf(dbstateInfrastructureDevelopmentCharges));
			
			
			FeesAndCharges feesAndCharges=newobj.getFeesAndCharges();
			
			
			
		} catch (Exception e) {
			log.error("Exception : "+e.getMessage());
		}
	
		// --------------------------calculation end--------------------------------//
	}

	private LicenseServiceResponseInfo checkApplicationIsValid(RequestLOIReport requestLOIReport,
			String applicationNumber, String userId, String hqUserId) {
		try {
			LicenseServiceResponseInfo licenseServiceResponceInfo = licenseService
					.getNewServicesInfoById(applicationNumber, requestLOIReport.getRequestInfo());
			if (licenseServiceResponceInfo != null && licenseServiceResponceInfo.getNewServiceInfoData() != null
					&& !licenseServiceResponceInfo.getNewServiceInfoData().isEmpty()) {

				
				String myFile = loireportPath;
				File file = new File(myFile);
				if (!file.exists()) {
					file.mkdirs();
				}

				User user = getUserInfo(userId).getUser().get(0);
				org.egov.common.contract.request.User reqUser = org.egov.common.contract.request.User.builder()
						.id(user.getId()).emailId(user.getEmailId()).roles(user.getRoles()).tenantId(user.getTenantId())
						.type(user.getType()).uuid(user.getUuid()).build();
				requestLOIReport.getRequestInfo().setUserInfo(reqUser);

				currentDate = ConvertUtil.getCurrentDate(timeZoneName, null);
				licenseDetails = licenseServiceResponceInfo.getNewServiceInfoData().get(0);
				getCalculatorData(applicationNumber, licenseDetails, requestLOIReport);

				applicationDate = ConvertUtil.getCurrentDate(timeZoneName,
						Long.parseLong(String.valueOf(licenseServiceResponceInfo.getApplicationDate())));
				loiNumber=licenseServiceResponceInfo.getTcpLoiNumber();
				
				totalArea = licenseDetails.getApplicantPurpose().getTotalArea();
				AppliedLandDetails appliedLandDetails = licenseDetails.getApplicantPurpose().getAppliedLandDetails()
						.get(0);
				address = " village-" + appliedLandDetails.getRevenueEstate() + ", sector-"
						+ appliedLandDetails.getSector() + ",Tehsil-" + appliedLandDetails.getTehsil() + ",district-"
						+ appliedLandDetails.getDistrict();
				disticName = appliedLandDetails.getDistrict() != null ? appliedLandDetails.getDistrict() : "";
				collaborationCompanyName = appliedLandDetails.getDeveloperCompany() != null
						? (appliedLandDetails.getDeveloperCompany())
						: ("N/A");
				dtcpUserName = getUserInfo(userId).getUser().get(0).getName();
				hqUserName = getUserInfo(hqUserId).getUser().get(0).getName();

				
				
				khasraNo = appliedLandDetails.getKhewats();
//				String licenseFee = "0";
				String edc="0";
				String idw="0";
//				String scrutinyFees="0";
				try {
			
					edc = Optional.ofNullable(String.valueOf(licenseServiceResponceInfo.getEdc())).orElseThrow(RuntimeException::new);
					amountEDC=Double.parseDouble(decfor.format(Double.parseDouble(edc!="0"?(edc.replace("E", "")):("0.0"))));
					
					idw = Optional.ofNullable(String.valueOf(licenseServiceResponceInfo.getIdw())).orElseThrow(RuntimeException::new);
					amountIDW=Double.parseDouble(decfor.format(Double.parseDouble(idw!="0"?(idw.replace("E", "")):("0.0"))));
						
//					FeesAndCharges feesAndCharges = licenseDetails.getFeesAndCharges();
//
//					licenseFee = Optional.ofNullable(feesAndCharges.getLicenseFee()).orElseThrow(RuntimeException::new);
//					scrutinyFees = Optional.ofNullable(feesAndCharges.getScrutinyFee())
//							.orElseThrow(RuntimeException::new);
//					
//					conversionCharges = Optional.ofNullable(feesAndCharges.getConversionCharges())
//							.orElseThrow(RuntimeException::new);
//					stateInfrastructureDevelopmentCharges = Optional
//							.ofNullable(feesAndCharges.getStateInfrastructureDevelopmentCharges())
//							.orElseThrow(RuntimeException::new);
//					
//					edc = Optional.ofNullable(feesAndCharges.getEDC())
//							.orElseThrow(RuntimeException::new);
//					idw = Optional.ofNullable(feesAndCharges.getIDW())
//							.orElseThrow(RuntimeException::new);
//					amountIDW=Double.parseDouble(idw);
//					amountEDC=Double.parseDouble(edc);
				} catch (Exception e) {
					log.error("Exception : "+e.getMessage());
				
				}
				try {
					
					DetailsofAppliedLand detailsofAppliedLand = Optional
							.ofNullable(licenseDetails.getDetailsofAppliedLand()).orElseThrow(RuntimeException::new);
//					DetailsAppliedLandPlot detailsAppliedLandPlot = detailsofAppliedLand.getDetailsAppliedLandPlot();
//					farAmount = Optional.ofNullable(detailsAppliedLandPlot.getFAR()).orElseThrow(RuntimeException::new);
//					plottedComponent = Optional.ofNullable(detailsAppliedLandPlot.getUnderPlot())
//							.orElseThrow(RuntimeException::new);
//					comericalComponent = Optional.ofNullable(detailsAppliedLandPlot.getCommercial())
//							.orElseThrow(RuntimeException::new);
					try {
						detailsofAppliedLand.getPurposeDetails().stream().forEach(purposedetails->{
					
							if(purposedetails.getCode().equals("CPRS")) {
								plottedComponent=Optional.ofNullable(purposedetails.getArea()).orElseThrow(RuntimeException::new);
								farAmount=Optional.ofNullable(String.valueOf(purposedetails.getFar().charAt(0))).orElseThrow(RuntimeException::new);
							}else {
								comericalComponent=Optional.ofNullable(comericalComponent=purposedetails.getArea()).orElseThrow(RuntimeException::new);
							}
						});
					}catch (Exception e) {
						log.error("Exception: "+e.getMessage());
					}

//					licenseFees = ConvertUtil.numberToComa(licenseFee);
//					licenseFeesInWord = ConvertUtil.numberToWords(licenseFee);
//					conversionChargesInWord = ConvertUtil.numberToWords(conversionCharges.toString());
//					conversionCharges = ConvertUtil.numberToComa(conversionCharges.toString());
//					stateInfrastructureDevelopmentChargesInWord = ConvertUtil
//							.numberToWords(stateInfrastructureDevelopmentCharges.toString());
//					stateInfrastructureDevelopmentCharges = ConvertUtil
//							.numberToComa(stateInfrastructureDevelopmentCharges.toString());
//					scrutinyFee=ConvertUtil.numberToComa(scrutinyFees);
//					scrutinyFeeInWord=ConvertUtil.numberToWords(scrutinyFees);

				} catch (Exception e) {
					log.error("Exception : "+e.getMessage());
				}

				return licenseServiceResponceInfo;
			} else {
				return null;
			}
		} catch (Exception e) {
			log.error("Exception : "+e.getMessage());
			return null;
		}
	}

	public void createLoiReport(String applicationNumber, String userId, RequestLOIReport requestLOIReport,
			String hqUserId) {
	    LicenseServiceResponseInfo licenseServiceResponceInfo = licenseService.getNewServicesInfoById(applicationNumber,
				requestLOIReport.getRequestInfo());
		String purpose = licenseServiceResponceInfo.getNewServiceInfoData().get(0).getApplicantPurpose().getPurpose();
		log.info("LOI Report creation method called for Purpose : "+purpose);
		
		switch (purpose) {
		case "RGP": { // 1 done
			getLoiReportGroupHousing(applicationNumber, userId, hqUserId, requestLOIReport);
			break;
		}
		case "AGH": { // 2  done
			getLoiReportAffordableGroupHousing(applicationNumber, userId, hqUserId, requestLOIReport);
			break;
		}
		case "TODMGH": { // 3 done
			getLoiReportGroupHousingUnderTODPolicy(applicationNumber, userId, hqUserId, requestLOIReport);
			break;
		}
		case "DDJAY_APHP": { // 4 done
			getLoiReportGAffordablePlottedDeenDayal(applicationNumber, userId, hqUserId, requestLOIReport);
			break;
		}
		case "CPRS": {// 5 done
			getLoiReportCommercialPlottedColony(applicationNumber, userId, hqUserId, requestLOIReport);
			break;
		}
		case "RPL": { // 6  done
			getLoiReportNewResidentialPlottedColony(applicationNumber, userId, hqUserId, requestLOIReport);
			break;
		}
		case "IPA": { // 7 done
			getLoiReportIndustrialPlottedColony(applicationNumber, userId, hqUserId, requestLOIReport);
			break;
		}
		case "ITC": { // 8 done
			getLoiReportITCyberCity(applicationNumber, userId, hqUserId, requestLOIReport);
			break;
		}
		case "ITP": { // 9 done
			getLoiReportITCyberCity(applicationNumber, userId, hqUserId, requestLOIReport);
			break;
		}
		case "IPULP": { // 10 done
			getLoiReportIndustrialColony(applicationNumber, userId, hqUserId, requestLOIReport);
			break;
		}
		case "MLU-CZ": { // 11 done
			getLoiReportDevelopMixedLandUseColony(applicationNumber, userId, hqUserId, requestLOIReport);
			break;
		}
		case "UNKNOWANAN": { // 12 Integrated Residential Plotted Colony   done
			getLoiReportIntegratedResidentialPlottedColony(applicationNumber, userId, hqUserId, requestLOIReport);
			break;
		}
		case "CICS": { // 13 done
			getLoiReportCommercialIntegratedColony(applicationNumber, userId, hqUserId, requestLOIReport);
			break;
		}
		default:
			getLoiReportIntegratedResidentialPlottedColony(applicationNumber, userId, hqUserId, requestLOIReport);
			break;
		}
		log.info("Created LOI Report for Purpose : "+purpose);
		
	}

//	private void loiTableReportData(Document doc, String userId, String applicationNumber,
//			RequestLOIReport requestLOIReport) {
	private void loiTableReportData(List list, String userId, String applicationNumber,
			RequestLOIReport requestLOIReport) {
		try {
//			float[] columnWidths = { 1, 3,8 };
//			PdfPTable table = new PdfPTable(columnWidths);
//			table.setWidthPercentage(100);
			HttpHeaders headers = new HttpHeaders();
			headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
			HttpEntity<RequestInfo> entity = new HttpEntity<RequestInfo>(requestLOIReport.getRequestInfo(), headers);
			Map<String, String> param = new HashMap<>();
			param.put("applicationNumber", applicationNumber);
//			param.put("userId", userId);
			String urlParameter = param.toString().replace("{", "").replace("}", "").replaceAll(",", "&")
					.replaceAll("\\s+", "");
			Map<String, Object> rest1 = restTemplate
					.exchange(env.getProperty("egov.land-service.host") + env.getProperty("egov.land-service.path")
							+ "?" + urlParameter, HttpMethod.POST, entity, HashMap.class)
					.getBody();
			java.util.List<Map<String, Object>> rest2 = (java.util.List<Map<String, Object>>) rest1.get("egScrutiny");

			for (Iterator iterator = rest2.iterator(); iterator.hasNext();) {
				Map<String, Object> map = (Map<String, Object>) iterator.next();
				System.out.println(map);
				if (map.get("isLOIPart") != null ? (map.get("isLOIPart").toString().contains("true")) : (false)) {
					if(map.get("comment") != null) {
						 list.add(new ListItem(String.valueOf(map.get("comment")),normal));
					}
				 }
			}

				
			
//			System.out.println("rest2:---" + rest2);
//			int colIndex = 0;
//			java.util.List<String> hList = Arrays.asList("Sr No", "Description","Remarks");
//			for (String columnTitle : hList) {
//				colIndex = colIndex + 1;
//
//				PdfPCell header = new PdfPCell();
//				header.setBackgroundColor(BaseColor.LIGHT_GRAY);
//				header.setBorderWidth(2);
//				header.setPaddingBottom(5f);
//				header.setPhrase(new Phrase(columnTitle));
//				table.addCell(header);
//			}
//
//			int index = 1;
//			for (Iterator iterator = rest2.iterator(); iterator.hasNext();) {
//				Map<String, Object> map = (Map<String, Object>) iterator.next();
//				System.out.println(map);
//				if (map.get("isLOIPart") != null ? (map.get("isLOIPart").toString().contains("true")) : (false)) {
//					table.addCell(index + "");
//					table.addCell(map.get("fieldIdL") != null ? map.get("fieldIdL") + "" : "N/A");
//					table.addCell(map.get("comment") != null ? map.get("comment") + "" : "N/A");
//					index++;
//				}
//
//			}
//			if (rest2.size() < 1) {
//				PdfPCell cell23 = new PdfPCell(new Phrase("Record Not Found"));
//				cell23.setHorizontalAlignment(Element.ALIGN_CENTER);
//				cell23.setPaddingBottom(5f);
//				cell23.setPaddingTop(5f);
//				cell23.setColspan(2);
//				cell23.setRowspan(1);
//				table.addCell(cell23);
//			}
//			doc.add(table);
//		} catch (DocumentException e1) {
//			log.error("DocumentException : "+e1.getMessage());
		} catch (Exception e) {
			log.error("Exception : "+e.getMessage());
		}
	}

	private static void addTitlePage(Document doc, int addressType) throws DocumentException {
		String dctpFormTitleAddress = "SCO-71-75, 2nd Floor, Sector 17 C, Chandigarh";
		String dctpFormTitleContacrInfo = "Phone: 0172-2549349 e-mail:tcpharyana7@gmail.com";
		if (addressType == 1) {
			dctpFormTitleAddress = "Nagar Yojna Bhawan, Plot No. 3, Block-A, Madhya Marg, Sector 18A, Chandigarh.";
			dctpFormTitleContacrInfo = "Phone : 0172-2549349  Email: tcpharyana7@gmail.com  ";
		}

		Paragraph preface0 = new Paragraph("Directorate of Town & Country Planning, Haryana", catFont);
		preface0.setAlignment(Element.ALIGN_CENTER);
		doc.add(preface0);

		Paragraph preface1 = new Paragraph(dctpFormTitleAddress, smallBold);
		preface1.setAlignment(Element.ALIGN_CENTER);
		doc.add(preface1);

		Paragraph preface2 = new Paragraph(dctpFormTitleContacrInfo, smallBold);
		preface2.setAlignment(Element.ALIGN_CENTER);
		doc.add(preface2);

		Paragraph preface3 = new Paragraph("website:-http://tcpharyana.gov.in", smallBold);
		preface3.setAlignment(Element.ALIGN_CENTER);
		doc.add(preface3);
//	    DottedLineSeparator  customLine =new DottedLineSeparator();
//	    customLine.setGap(0);

		doc.add(Chunk.NEWLINE);
		LineSeparator ls = new LineSeparator();
		ls.setAlignment(Element.ALIGN_CENTER);
		ls.setOffset(5);
		ls.setLineColor(BaseColor.BLACK);
		doc.add(new Chunk(ls));
		// LC-III
		Paragraph preface4 = new Paragraph("LC-III", smallBold);
		preface4.setAlignment(Element.ALIGN_CENTER);
		doc.add(preface4);
		Paragraph preface5 = new Paragraph("(See Rule 10)", smallBold);
		preface5.setAlignment(Element.ALIGN_CENTER);
		doc.add(preface5);
	}

	private UserResponse getUserInfo(String... userId) {
		UserSearchCriteria userSearchCriteria = new UserSearchCriteria();
		userSearchCriteria.setId(Arrays.asList(Long.parseLong(userId[0])));

		userSearchCriteria.setTenantId("hr");

		StringBuilder url = new StringBuilder(userHost);
		url.append(userSearchPath);

		Object searchUser = serviceRequestRepository.fetchResult(url, userSearchCriteria);
		String data1 = null;

		try {
			data1 = mapper.writeValueAsString(searchUser);
		} catch (JsonProcessingException e) { // TODO Auto-generated catch block
			 log.error("JsonProcessingException : "+e.getMessage());
		}
		UserResponse userData = null;
		ObjectReader readerData = mapper.readerFor(new TypeReference<UserResponse>() {
		});
		try {
			userData = readerData.readValue(data1);
		} catch (IOException e) {
		    log.error("IOException : "+e.getMessage());
		}catch (Exception e) {
			log.error("IOException : "+e.getMessage());
    	}
		return userData;

	}

	private static void addEmptyLine(Paragraph paragraph, int number) {
		for (int i = 0; i < number; i++) {
			paragraph.add(new Paragraph(" "));
		}
	}


	/***************************************
	 * LOI Purpose : Common method end *
	 ***************************************/

}
