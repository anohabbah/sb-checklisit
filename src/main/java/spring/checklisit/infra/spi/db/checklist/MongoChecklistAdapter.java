package spring.checklisit.infra.spi.db.checklist;

import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Repository;
import spring.checklisit.domain.checklist.ChecklistItem;
import spring.checklisit.domain.checklist.ChecklistPort;
import spring.checklisit.domain.checklist.UserChecklist;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class MongoChecklistAdapter implements ChecklistPort {

    private final MongoTemplate mongoTemplate;

    @Override
    public List<ChecklistItem> findAllItems() {
        return mongoTemplate.findAll(ChecklistItem.class);
    }

    @Override
    public Optional<ChecklistItem> findItemById(String id) {
        return Optional.ofNullable(mongoTemplate.findById(id, ChecklistItem.class));
    }

    @Override
    public List<ChecklistItem> saveAllItems(List<ChecklistItem> items) {
        return items.stream()
                .map(mongoTemplate::save)
                .toList();
    }

    @Override
    public void deleteItemById(String id) {
        Query query = new Query(Criteria.where("_id").is(id));
        mongoTemplate.remove(query, ChecklistItem.class);
    }

    @Override
    public Optional<UserChecklist> findUserChecklistByDate(LocalDate date) {
        Query query = new Query(Criteria.where("date").is(date));
        return Optional.ofNullable(mongoTemplate.findOne(query, UserChecklist.class));
    }

    @Override
    public UserChecklist saveUserChecklist(UserChecklist userChecklist) {
        return mongoTemplate.save(userChecklist);
    }
}
