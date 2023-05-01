package org.egov.egovdocumentuploader.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import org.egov.egovdocumentuploader.web.models.AuditDetails;
import org.egov.egovdocumentuploader.web.models.DocumentEntity;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

@Component
public class DocumentRowMapper implements ResultSetExtractor<List<DocumentEntity>> {
    public List<DocumentEntity> extractData(ResultSet rs) throws SQLException, DataAccessException {
        Map<String,DocumentEntity> documentEntityMap = new LinkedHashMap<>();

        while (rs.next()){
            String uuid = rs.getString("uuid");
            DocumentEntity documentEntity = documentEntityMap.get(uuid);

            if(documentEntity == null) {

                Long lastModifiedTime = rs.getLong("lastmodifiedtime");
                if (rs.wasNull()) {
                    lastModifiedTime = null;
                }

                AuditDetails auditdetails = AuditDetails.builder()
                        .createdBy(rs.getString("createdby"))
                        .createdTime(rs.getLong("createdtime"))
                        .lastModifiedBy(rs.getString("lastmodifiedby"))
                        .lastModifiedTime(lastModifiedTime)
                        .build();

                documentEntity = DocumentEntity.builder()
                        .uuid(rs.getString("uuid"))
                        .tenantId(rs.getString("tenantid"))
                        .name(rs.getString("name"))
                        .category(rs.getString("category"))
                        .description(rs.getString("description"))
                        .filestoreId(rs.getString("filestoreid"))
                        .documentLink(rs.getString("documentlink"))
                        .postedBy(rs.getString("postedby"))
                        .fileType(rs.getString("filetype"))
                        .fileSize(rs.getLong("filesize"))
                        .active(rs.getBoolean("active"))
                        .auditDetails(auditdetails)
                        .build();
            }

            documentEntityMap.put(uuid, documentEntity);
        }
        return new ArrayList<>(documentEntityMap.values());
    }
}
