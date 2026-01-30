package spring.checklisit.domain.checklist;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChecklistUseCase {
    private final ChecklistPort checklistPort;

    public List<ChecklistItem> getAllItems() {
        return checklistPort.findAllItems();
    }

    public List<ChecklistItem> createItems(List<ChecklistItem> items) {
        return checklistPort.saveAllItems(items);
    }

    public List<ChecklistItem> updateItems(List<ChecklistItem> items) {
        items.forEach(item -> {
            if (item.getId() == null || item.getId().isBlank()) {
                throw new IllegalArgumentException("Item id is required for update");
            }
            checklistPort.findItemById(item.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("ChecklistItem not found with id: " + item.getId()));
        });

        // Sync: delete items not in the update request
        Set<String> updateIds = items.stream()
                .map(ChecklistItem::getId)
                .collect(Collectors.toSet());

        checklistPort.findAllItems().stream()
                .map(ChecklistItem::getId)
                .filter(id -> !updateIds.contains(id))
                .forEach(checklistPort::deleteItemById);

        return checklistPort.saveAllItems(items);
    }

    public void deleteItem(String id) {
        checklistPort.findItemById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ChecklistItem not found with id: " + id));
        checklistPort.deleteItemById(id);
    }

    public UserChecklist getTodayChecklist() {
        LocalDate today = LocalDate.now();
        return checklistPort.findUserChecklistByDate(today)
                .orElseThrow(() -> new ResourceNotFoundException("No checklist found for today"));
    }

    public UserChecklist markItemComplete(String itemId) {
        return updateItemCompletion(itemId, true);
    }

    public UserChecklist markItemUncomplete(String itemId) {
        return updateItemCompletion(itemId, false);
    }

    private UserChecklist updateItemCompletion(String itemId, boolean complete) {
        LocalDate today = LocalDate.now();
        UserChecklist checklist = checklistPort.findUserChecklistByDate(today)
                .orElseThrow(() -> new ResourceNotFoundException("No checklist found for today"));

        boolean itemFound = false;
        for (UserChecklistItem item : checklist.getItems()) {
            if (item.getItemId().equals(itemId)) {
                item.setComplete(complete);
                itemFound = true;
                break;
            }
        }

        if (!itemFound) {
            throw new ResourceNotFoundException("Item not found in today's checklist with id: " + itemId);
        }

        return checklistPort.saveUserChecklist(checklist);
    }

    public UserChecklist resetChecklist() {
        LocalDate today = LocalDate.now();

        List<ChecklistItem> activeItems = checklistPort.findAllItems().stream()
                .filter(item -> item.getStatus() == Status.ACTIVE)
                .toList();

        List<UserChecklistItem> userItems = activeItems.stream()
                .map(item -> UserChecklistItem.builder()
                        .itemId(item.getId())
                        .label(item.getLabel())
                        .category(item.getCategory())
                        .order(item.getOrder())
                        .complete(item.isComplete())
                        .build())
                .toList();

        UserChecklist checklist = checklistPort.findUserChecklistByDate(today)
                .map(existing -> {
                    existing.setItems(userItems);
                    return existing;
                })
                .orElse(UserChecklist.builder()
                        .date(today)
                        .items(userItems)
                        .build());

        return checklistPort.saveUserChecklist(checklist);
    }
}
