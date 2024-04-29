package prompt.overshadowing.model.langfuse;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

import java.util.ArrayList;
import java.util.List;
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class Batch {
    public List<Ingestion> batch;

    public Batch() {
        batch = new ArrayList<>();
    }

    public void addIngestion(Ingestion ingestion) {
        batch.add(ingestion);
    }
}

