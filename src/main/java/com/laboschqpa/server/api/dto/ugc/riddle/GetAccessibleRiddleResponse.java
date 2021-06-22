package com.laboschqpa.server.api.dto.ugc.riddle;

import com.laboschqpa.server.entity.usergeneratedcontent.Riddle;
import com.laboschqpa.server.repo.usergeneratedcontent.dto.GetAccessibleRiddleJpaDto;
import lombok.Data;

import java.time.Instant;
import java.util.Set;

@Data
public class GetAccessibleRiddleResponse {
    private Long id;

    private Long creatorUserId;
    private Long editorUserId;
    private Instant creationTime;
    private Instant editTime;

    private String title;
    private String hint;
    private String solution;

    private Boolean wasHintUsed;
    private Boolean isAlreadySolved;

    private Set<Long> attachments;

    private Long firstSolvingTeamId;
    private String firstSolvingTeamName;
    private Instant firstSolvingTimestamp;

    public GetAccessibleRiddleResponse(GetAccessibleRiddleJpaDto getAccessibleRiddleJpaDto, boolean includeHint, boolean includeSolution) {
        this.id = getAccessibleRiddleJpaDto.getId();
        this.creatorUserId = getAccessibleRiddleJpaDto.getCreatorUserId();
        this.editorUserId = getAccessibleRiddleJpaDto.getEditorUserId();
        this.creationTime = getAccessibleRiddleJpaDto.getCreationTimeAsInstant();
        this.editTime = getAccessibleRiddleJpaDto.getEditTimeAsInstant();

        this.title = getAccessibleRiddleJpaDto.getTitle();
        this.wasHintUsed = getAccessibleRiddleJpaDto.getWasHintUsed();
        this.isAlreadySolved = getAccessibleRiddleJpaDto.getIsAlreadySolved();

        if (includeHint) {
            this.hint = getAccessibleRiddleJpaDto.getHint();
        }
        if (includeSolution) {
            this.solution = getAccessibleRiddleJpaDto.getSolution();
        }
    }

    public GetAccessibleRiddleResponse(Riddle riddle, boolean includeHint, boolean includeSolution, boolean includeAttachments) {
        this.id = riddle.getId();
        this.creatorUserId = riddle.getCreatorUser().getId();
        this.editorUserId = riddle.getEditorUser().getId();
        this.creationTime = riddle.getCreationTime();
        this.editTime = riddle.getEditTime();
        this.title = riddle.getTitle();

        this.attachments = riddle.getAttachments();

        if (includeHint) {
            this.hint = riddle.getHint();
        }
        if (includeSolution) {
            this.solution = riddle.getSolution();
        }
        if (includeAttachments) {
            this.attachments = riddle.getAttachments();
        }
    }
}
