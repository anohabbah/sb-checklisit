package spring.checklisit.infra.api.rest.checklist;

import spring.checklisit.domain.checklist.Category;
import spring.checklisit.domain.checklist.UserChecklist;
import spring.checklisit.domain.checklist.UserChecklistItem;

import java.time.LocalDate;
import java.util.List;

public record UserChecklistDto(
        String id,
        LocalDate date,
        List<UserChecklistItemDto> items
) {
    public static UserChecklistDto fromEntity(UserChecklist entity) {
        return new UserChecklistDto(
                entity.getId(),
                entity.getDate(),
                entity.getItems().stream()
                        .map(UserChecklistItemDto::fromEntity)
                        .toList()
        );
    }

    public record UserChecklistItemDto(
            String itemId,
            String label,
            Category category,
            int order,
            boolean complete
    ) {
        public static UserChecklistItemDto fromEntity(UserChecklistItem entity) {
            return new UserChecklistItemDto(
                    entity.getItemId(),
                    entity.getLabel(),
                    entity.getCategory(),
                    entity.getOrder(),
                    entity.isComplete()
            );
        }
    }
}
