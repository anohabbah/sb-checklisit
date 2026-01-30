package spring.checklisit.infra.api.rest.checklist;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import spring.checklisit.domain.checklist.ChecklistItem;
import spring.checklisit.domain.checklist.ChecklistUseCase;

import java.util.List;

@RestController
@RequestMapping("/checklist")
@RequiredArgsConstructor
@Validated
public class ChecklistResource {

    private final ChecklistUseCase checklistUseCase;

    @GetMapping("/items")
    public ResponseEntity<List<ChecklistItemDto>> getAllItems() {
        List<ChecklistItemDto> items = checklistUseCase.getAllItems().stream()
                .map(ChecklistItemDto::fromEntity)
                .toList();
        return ResponseEntity.ok(items);
    }

    @PostMapping("/items")
    public ResponseEntity<List<ChecklistItemDto>> createItems(@RequestBody @Valid List<@Valid ChecklistItemDto> items) {
        List<ChecklistItem> entities = items.stream()
                .map(ChecklistItemDto::toEntity)
                .toList();
        List<ChecklistItemDto> created = checklistUseCase.createItems(entities).stream()
                .map(ChecklistItemDto::fromEntity)
                .toList();
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/items")
    public ResponseEntity<List<ChecklistItemDto>> updateItems(@RequestBody @Valid List<@Valid ChecklistItemDto> items) {
        List<ChecklistItem> entities = items.stream()
                .map(ChecklistItemDto::toEntity)
                .toList();
        List<ChecklistItemDto> updated = checklistUseCase.updateItems(entities).stream()
                .map(ChecklistItemDto::fromEntity)
                .toList();
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/items/{id}")
    public ResponseEntity<Void> deleteItem(@PathVariable String id) {
        checklistUseCase.deleteItem(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<UserChecklistDto> getTodayChecklist() {
        UserChecklistDto checklist = UserChecklistDto.fromEntity(checklistUseCase.getTodayChecklist());
        return ResponseEntity.ok(checklist);
    }

    @PatchMapping("/{itemId}/complete")
    public ResponseEntity<UserChecklistDto> markItemComplete(@PathVariable String itemId) {
        UserChecklistDto checklist = UserChecklistDto.fromEntity(checklistUseCase.markItemComplete(itemId));
        return ResponseEntity.ok(checklist);
    }

    @PatchMapping("/{itemId}/uncomplete")
    public ResponseEntity<UserChecklistDto> markItemUncomplete(@PathVariable String itemId) {
        UserChecklistDto checklist = UserChecklistDto.fromEntity(checklistUseCase.markItemUncomplete(itemId));
        return ResponseEntity.ok(checklist);
    }

    @PostMapping("/reset")
    public ResponseEntity<UserChecklistDto> resetChecklist() {
        UserChecklistDto checklist = UserChecklistDto.fromEntity(checklistUseCase.resetChecklist());
        return ResponseEntity.ok(checklist);
    }
}
