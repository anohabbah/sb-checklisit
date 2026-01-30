package spring.checklisit.infra.api.rest.checklist;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import spring.checklisit.TestcontainersConfiguration;
import spring.checklisit.domain.checklist.Category;
import spring.checklisit.domain.checklist.ChecklistItem;
import spring.checklisit.domain.checklist.Status;
import spring.checklisit.domain.checklist.UserChecklist;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class ChecklistResourceIntegrationTest {

    @Autowired
    private MockMvcTester mockMvc;

    @Autowired
    private MongoTemplate mongoTemplate;

    @BeforeEach
    void setUp() {
        mongoTemplate.dropCollection(ChecklistItem.class);
        mongoTemplate.dropCollection(UserChecklist.class);
    }

    @Test
    void createItems_shouldReturnCreatedItems() {
        String requestBody = """
                [
                    {"label": "Task 1", "category": "MORNING", "order": 1, "status": "ACTIVE", "complete": false},
                    {"label": "Task 2", "category": "AFTERNOON", "order": 2, "status": "ACTIVE", "complete": true}
                ]
                """;

        assertThat(mockMvc.post().uri("/checklist/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .hasStatus(201)
                .bodyJson()
                .extractingPath("$[0].id").isNotNull();
    }

    @Test
    void listItems_shouldReturnAllItems() {
        ChecklistItem item = ChecklistItem.builder()
                .label("Task 1")
                .category(Category.MORNING)
                .order(1)
                .status(Status.ACTIVE)
                .complete(false)
                .build();
        mongoTemplate.save(item);

        assertThat(mockMvc.get().uri("/checklist/items"))
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$[0].label").isEqualTo("Task 1");
    }

    @Test
    void updateItems_shouldReturnUpdatedItems() {
        ChecklistItem item = ChecklistItem.builder()
                .label("Old Label")
                .category(Category.MORNING)
                .order(1)
                .status(Status.ACTIVE)
                .complete(false)
                .build();
        item = mongoTemplate.save(item);

        String requestBody = """
                [
                    {"id": "%s", "label": "New Label", "category": "AFTERNOON", "order": 2, "status": "INACTIVE", "complete": true}
                ]
                """.formatted(item.getId());

        assertThat(mockMvc.put().uri("/checklist/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$[0].label").isEqualTo("New Label");
    }

    @Test
    void deleteItem_shouldReturnNoContent() {
        ChecklistItem item = ChecklistItem.builder()
                .label("Task to delete")
                .category(Category.MORNING)
                .order(1)
                .status(Status.ACTIVE)
                .complete(false)
                .build();
        item = mongoTemplate.save(item);

        assertThat(mockMvc.delete().uri("/checklist/items/" + item.getId()))
                .hasStatus(204);

        assertThat(mongoTemplate.findById(item.getId(), ChecklistItem.class)).isNull();
    }

    @Test
    void createItems_withMissingRequiredFields_shouldReturn400() {
        String requestBody = """
                [
                    {"label": null, "category": "MORNING", "order": 1, "status": "ACTIVE", "complete": false}
                ]
                """;

        assertThat(mockMvc.post().uri("/checklist/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
                .hasStatus(400);
    }

    @Test
    void getTodayChecklist_shouldReturnUserChecklist() {
        ChecklistItem item = ChecklistItem.builder()
                .label("Task")
                .category(Category.MORNING)
                .order(1)
                .status(Status.ACTIVE)
                .complete(false)
                .build();
        mongoTemplate.save(item);

        mockMvc.post().uri("/checklist/reset").exchange();

        assertThat(mockMvc.get().uri("/checklist"))
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$.date").isNotNull();
    }

    @Test
    void completeItem_shouldMarkItemComplete() {
        ChecklistItem item = ChecklistItem.builder()
                .label("Task")
                .category(Category.MORNING)
                .order(1)
                .status(Status.ACTIVE)
                .complete(false)
                .build();
        item = mongoTemplate.save(item);

        mockMvc.post().uri("/checklist/reset").exchange();

        assertThat(mockMvc.patch().uri("/checklist/" + item.getId() + "/complete"))
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$.items[0].complete").isEqualTo(true);
    }

    @Test
    void uncompleteItem_shouldMarkItemUncomplete() {
        ChecklistItem item = ChecklistItem.builder()
                .label("Task")
                .category(Category.MORNING)
                .order(1)
                .status(Status.ACTIVE)
                .complete(true)
                .build();
        item = mongoTemplate.save(item);

        mockMvc.post().uri("/checklist/reset").exchange();

        assertThat(mockMvc.patch().uri("/checklist/" + item.getId() + "/uncomplete"))
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$.items[0].complete").isEqualTo(false);
    }

    @Test
    void resetChecklist_shouldGenerateChecklistWithOnlyActiveItems() {
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

        assertThat(mockMvc.post().uri("/checklist/reset"))
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$.items.length()").isEqualTo(1);
    }

    @Test
    void resetChecklist_shouldInheritCompleteValue() {
        ChecklistItem preCheckedItem = ChecklistItem.builder()
                .label("Pre-checked Task")
                .category(Category.MORNING)
                .order(1)
                .status(Status.ACTIVE)
                .complete(true)
                .build();
        mongoTemplate.save(preCheckedItem);

        assertThat(mockMvc.post().uri("/checklist/reset"))
                .hasStatusOk()
                .bodyJson()
                .extractingPath("$.items[0].complete").isEqualTo(true);
    }
}
