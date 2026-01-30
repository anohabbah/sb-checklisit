package spring.checklisit.domain.checklist;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Category {
    MORNING(""),
    AFTERNOON(""),
    NIGHT("");

    private final String text;
}
