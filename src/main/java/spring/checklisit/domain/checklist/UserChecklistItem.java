package spring.checklisit.domain.checklist;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserChecklistItem {
    private String itemId;
    private String label;
    private Category category;
    private int order;
    private boolean complete;
}
