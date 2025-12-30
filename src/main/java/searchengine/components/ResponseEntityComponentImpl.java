package searchengine.components;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import searchengine.dto.ResponseBody;
import searchengine.dto.api.IndexingResponse;

import static searchengine.logging.LoggingTemplates.TEMPLATE_SERVICE_RESPONSE;

@Slf4j
@Component
public class ResponseEntityComponentImpl implements ResponseEntityComponent {

    @Override
    public ResponseEntity<ResponseBody> createResponseEntity(String message,
                                                             boolean statusResult,
                                                             HttpStatusCode httpStatusCode) {
        IndexingResponse errorBody = new IndexingResponse();
        errorBody.setResult(statusResult);
        errorBody.setError(message);
        log.info(TEMPLATE_SERVICE_RESPONSE,
                httpStatusCode,
                errorBody);
        return new ResponseEntity<>(errorBody, httpStatusCode);
    }
}
