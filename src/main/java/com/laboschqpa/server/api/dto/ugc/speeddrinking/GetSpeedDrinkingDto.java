package com.laboschqpa.server.api.dto.ugc.speeddrinking;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.laboschqpa.server.api.dto.ugc.GetUserGeneratedContentDto;
import com.laboschqpa.server.entity.usergeneratedcontent.SpeedDrinking;
import com.laboschqpa.server.enums.converter.jackson.SpeedDrinkingCategoryToValueJacksonConverter;
import com.laboschqpa.server.enums.ugc.SpeedDrinkingCategory;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GetSpeedDrinkingDto extends GetUserGeneratedContentDto {
    private Long drinkerUserId;
    private Double time;
    @JsonSerialize(converter = SpeedDrinkingCategoryToValueJacksonConverter.class)
    private SpeedDrinkingCategory category;
    private String note;

    public GetSpeedDrinkingDto() {
        super();
    }

    public GetSpeedDrinkingDto(SpeedDrinking speedDrinking) {
        this(speedDrinking, false);
    }

    /**
     * @param includeAttachments Set this to {@code false} if the attachments should not be got
     *                           (e.g. to avoid {@link org.hibernate.LazyInitializationException})!
     */
    public GetSpeedDrinkingDto(SpeedDrinking speedDrinking, boolean includeAttachments) {
        super(speedDrinking, includeAttachments);

        this.drinkerUserId = speedDrinking.getDrinkerUserAcc().getId();
        this.time = speedDrinking.getTime();
        this.category = speedDrinking.getCategory();
        this.note = speedDrinking.getNote();
    }
}
