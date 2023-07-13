package org.egov.tl.report;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.itextpdf.text.Anchor;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class FileStoreMethod {
	@Value("${egov.filestore.host}")
	private String fileStoreHost;
	@Value("${egov.filestore.context.path}")
	private String fileStoreContextPath;

	public String fileStore(String id, String tenantId) throws DocumentException {
		String r = null;
		String apiResponse = null;
        String gh = null;
        Document doc = new Document(PageSize.A4.rotate());
		StringBuilder fileStoreUrl = new StringBuilder(fileStoreHost);
		fileStoreUrl.append(fileStoreContextPath);
		fileStoreUrl.append("?tenantId=" + tenantId);
		fileStoreUrl.append("&fileStoreIds=" + id);

		log.info("fileStoreUrl" + fileStoreUrl);

		try {
			URL url = new URL(fileStoreUrl.toString());
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");

			int responseCode = connection.getResponseCode();

			if (responseCode == HttpURLConnection.HTTP_OK) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
				String line;
				StringBuilder response = new StringBuilder();

				while ((line = reader.readLine()) != null) {
					response.append(line);
				}
				reader.close();

				apiResponse = response.toString();
				System.out.println(apiResponse);

				@SuppressWarnings("deprecation")
				JsonParser parser = new JsonParser();
				@SuppressWarnings("deprecation")
				JsonElement root = parser.parse(apiResponse);

				// Get the URL
				r = root.getAsJsonObject().get("fileStoreIds").getAsJsonArray().get(0).getAsJsonObject().get("url")
						.getAsString();

				log.info("URL: " + r);
//                   
//                   Link link = new Link("Click here to access the document", PdfAction.createURI(ORIGINAL_LINK));
//                   link.setFontColor(DeviceGray.BLACK);

				// String hyperlink = "<a href=\"" + r + "\">" + r + "</a>";
				String hyperlink = "<html><a href=\"" + r + "\">" + r +" style"+"="+"color:red"+ "</a></html>";
				log.info("Hyperlink: " + hyperlink);

  //                 Anchor anchor = new Anchor("Click Here");
//                   Anchor anchor1 = new Anchor("Click Here1");
//                   Anchor anchor2 = new Anchor("Click Here2");
//                   
//                  
 //                  anchor.setReference(r);
//                   anchor1.setReference(r);
//                   anchor2.setReference(r);
//                   Paragraph g = new Paragraph();
//                   Paragraph g1 = new Paragraph();
//                  g.add(anchor1);
//                  g1.add(anchor2);
//                 doc.add(anchor);
//                  doc.add(g);
//                  doc.add(g1 );
//                 ObjectMapper objectMapper = new ObjectMapper();
//                   JsonNode doc1 = objectMapper.readTree(apiResponse);
//                   
//                   String gh = doc1.get("fileStoreIds").get(0).get("url").asText();
//                   
//                
//                   System.out.println(gh);
				 ObjectMapper objectMapper = new ObjectMapper();
               JsonNode doc1 = objectMapper.readTree(apiResponse);
               
                gh = doc1.get("fileStoreIds").get(0).get("url").asText();
                
			} else {
				System.out.println("API call failed with response code: " + responseCode);
			}

			connection.disconnect();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return gh;

	}
//	public static void main(String[] args) throws FileNotFoundException, DocumentException {
//
//	    Document document = new Document(PageSize.A4); 
//	    PdfWriter.getInstance(document, new FileOutputStream("test3.pdf"));
//	    
//	    //Open the document before adding any content
//	    document.open();
//	    
//	    Font myFonColor = FontFactory.getFont(FontFactory.TIMES_ROMAN, 24, BaseColor.ORANGE);
//	    Paragraph paragraph = new Paragraph("BE THE CODER", myFonColor);
//	    document.add(paragraph);
//	    
//	    //Close the document
//	    document.close();
//	  }

}
