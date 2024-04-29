package langfuse.sdk.model;

import lombok.Getter;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Batch {
    /**
     * This class follows the JSON format to the HTTP request
     * <a href=https://api.reference.langfuse.com/#post-/api/public/ingestion>
     * Link to API documentation
     * </a>
     */
    public List<Ingestion> batch;
    public Batch() {this.batch = new ArrayList<>();}

    /**
     * Adds an ingestion to the batch
     * @param ingestion the ingestion to be added
     */
    public void addIngestion(Ingestion ingestion) {
        this.batch.add(ingestion);
    }
}
