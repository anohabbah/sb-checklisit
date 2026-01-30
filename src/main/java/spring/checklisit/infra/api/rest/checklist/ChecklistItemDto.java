package spring.checklisit.infra.api.rest.checklist;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import spring.checklisit.domain.checklist.Category;
import spring.checklisit.domain.checklist.ChecklistItem;
import spring.checklisit.domain.checklist.Status;

public record ChecklistItemDto(
        String id,
        @NotBlank String label,
        @NotNull Category category,
        @NotNull Integer order,
        @NotNull Status status,
        @NotNull Boolean complete
) {
    public static ChecklistItemDto fromEntity(ChecklistItem entity) {
        return new ChecklistItemDto(
                entity.getId(),
                entity.getLabel(),
                entity.getCategory(),
                entity.getOrder(),
                entity.getStatus(),
                entity.isComplete()
        );
    }

    public ChecklistItem toEntity() {
        return ChecklistItem.builder()
                .id(id)
                .label(label)
                .category(category)
                .order(order)
                .status(status)
                .complete(complete)
                .build();
    }
}
