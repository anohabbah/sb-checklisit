package spring.checklisit.domain.checklist;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import spring.checklisit.TestcontainersConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class ChecklistUseCaseIntegrationTest {

    @Autowired
    private ChecklistUseCase checklistUseCase;

    @Autowired
    private MongoTemplate mongoTemplate;

    @BeforeEach
    void setUp() {
        mongoTemplate.dropCollection(ChecklistItem.class);
        mongoTemplate.dropCollection(UserChecklist.class);
    }

    @Test
    void resetChecklist_shouldCreateSnapshotWithOnlyActiveItems() {
        ChecklistItem activeItem = ChecklistItem.builder()
                .label("Active Task")
                .category(Category.MORNING)
                .order(1)
                .status(Status.ACTIVE)
                .complete(false)
                .build();
        ChecklistItem inactiveItem = ChecklistItem.builder()
                .label("Inactive Task")
                .category(Category.AFTERNOON)
                .order(2)
                .status(Status.INACTIVE)
                .complete(false)
                .build();
        mongoTemplate.save(activeItem);
        mongoTemplate.save(inactiveItem);

        UserChecklist result = checklistUseCase.resetChecklist();

        assertThat(result).isNotNull();
        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getItems().get(0).getLabel()).isEqualTo("Active Task");
    }

    @Test
    void resetChecklist_shouldInheritCompleteValueFromChecklistItem() {
        ChecklistItem preCheckedItem = ChecklistItem.builder()
                .label("Pre-checked Task")
                .category(Category.MORNING)
                .order(1)
                .status(Status.ACTIVE)
                .complete(true)
                .build();
        ChecklistItem uncheckedItem = ChecklistItem.builder()
                .label("Unchecked Task")
                .category(Category.AFTERNOON)
                .order(2)
                .status(Status.ACTIVE)
                .complete(false)
                .build();
        mongoTemplate.save(preCheckedItem);
        mongoTemplate.save(uncheckedItem);

        UserChecklist result = checklistUseCase.resetChecklist();

        assertThat(result.getItems()).hasSize(2);
        UserChecklistItem preChecked = result.getItems().stream()
                .filter(i -> i.getLabel().equals("Pre-checked Task"))
                .findFirst()
                .orElseThrow();
        UserChecklistItem unchecked = result.getItems().stream()
                .filter(i -> i.getLabel().equals("Unchecked Task"))
                .findFirst()
                .orElseThrow();
        assertThat(preChecked.isComplete()).isTrue();
        assertThat(unchecked.isComplete()).isFalse();
    }

    @Test
    void markItemComplete_shouldToggleCompleteToTrue() {
        ChecklistItem item = ChecklistItem.builder()
                .label("Task")
                .category(Category.MORNING)
                .order(1)
                .status(Status.ACTIVE)
                .complete(false)
                .build();
        item = mongoTemplate.save(item);

        checklistUseCase.resetChecklist();
        UserChecklist result = checklistUseCase.markItemComplete(item.getId());

        assertThat(result.getItems().get(0).isComplete()).isTrue();
    }

    @Test
    void markItemUncomplete_shouldToggleCompleteToFalse() {
        ChecklistItem item = ChecklistItem.builder()
                .label("Task")
                .category(Category.MORNING)
                .order(1)
                .status(Status.ACTIVE)
                .complete(true)
                .build();
        item = mongoTemplate.save(item);

        checklistUseCase.resetChecklist();
        UserChecklist result = checklistUseCase.markItemUncomplete(item.getId());

        assertThat(result.getItems().get(0).isComplete()).isFalse();
    }
}
