package searchengine.components;

import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import searchengine.dto.ResponseBody;

public interface ResponseEntityComponent {
    ResponseEntity<ResponseBody> createResponseEntity(String message, boolean statusResult, HttpStatusCode httpStatusCode);
}
