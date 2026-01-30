package spring.checklisit.domain.checklist;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ChecklistPort {
    List<ChecklistItem> findAllItems();
    Optional<ChecklistItem> findItemById(String id);
    List<ChecklistItem> saveAllItems(List<ChecklistItem> items);
    void deleteItemById(String id);

    Optional<UserChecklist> findUserChecklistByDate(LocalDate date);
    UserChecklist saveUserChecklist(UserChecklist userChecklist);
}
