package prompt.overshadowing.repositories;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import prompt.overshadowing.model.Pii;

@ApplicationScoped
public class PiiRepository implements PanacheRepository<Pii> {
    /**
     * Find the Pii by its ID
     * @param id the ID
     * @return the Pii found by its Id
     */
    public Pii findById(String id) {
        return this.find("id", id)
                .stream()
                .findFirst()
                .orElse(null);
    }

    /**
     * Find the Pii by its content
     * @param content the content
     * @return the Pii found by its content
     */
    public Pii findByContent(String content){
        return this.find("content", content)
                .stream()
                .findFirst()
                .orElse(null);
    }
}