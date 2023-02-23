package org.egov.web.notification.mail.consumer.contract;





import java.util.Set;
import org.egov.web.notification.mail.utils.Category;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Email {

	private Set<String> emailTo;
	private String subject;
	private String body;
	private Category category;
	@JsonProperty("isHTML")
	private boolean isHTML;

    public boolean isValid() {
        return isNotEmpty(emailTo);
    }

	private boolean isNotEmpty(Set<String> emailTo2) {
		// TODO Auto-generated method stub
		return false;
	}
}
