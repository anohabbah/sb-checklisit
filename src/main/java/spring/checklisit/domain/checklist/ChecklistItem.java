package spring.checklisit.domain.checklist;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "checklist_items")
public class ChecklistItem {
    @Id
    private String id;
    private String label;
    private Category category;
    private int order;
    private Status status;
    private boolean complete;
}
