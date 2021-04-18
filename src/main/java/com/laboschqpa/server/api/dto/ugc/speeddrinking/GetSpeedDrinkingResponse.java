package com.laboschqpa.server.api.dto.ugc.speeddrinking;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.laboschqpa.server.api.dto.ugc.GetUserGeneratedContentResponse;
import com.laboschqpa.server.entity.usergeneratedcontent.SpeedDrinking;
import com.laboschqpa.server.enums.converter.jackson.SpeedDrinkingCategoryToValueJacksonConverter;
import com.laboschqpa.server.enums.ugc.SpeedDrinkingCategory;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class GetSpeedDrinkingResponse extends GetUserGeneratedContentResponse {
    private Long drinkerUserId;
    private String drinkerFirstName;
    private String drinkerLastName;
    private String drinkerNickName;
    private Long drinkerTeamId;
    private String drinkerTeamName;

    private Double time;
    @JsonSerialize(converter = SpeedDrinkingCategoryToValueJacksonConverter.class)
    private SpeedDrinkingCategory category;
    private String note;

    public GetSpeedDrinkingResponse() {
        super();
    }

    public GetSpeedDrinkingResponse(SpeedDrinking speedDrinking) {
        this(speedDrinking, false);
    }

    public GetSpeedDrinkingResponse(SpeedDrinking speedDrinking, boolean withDrinkerUserAndTeam) {
        super(speedDrinking, false);

        this.drinkerUserId = speedDrinking.getDrinkerUserAcc().getId();
        this.time = speedDrinking.getTime();
        this.category = speedDrinking.getCategory();
        this.note = speedDrinking.getNote();

        if (withDrinkerUserAndTeam) {
            this.drinkerFirstName = speedDrinking.getDrinkerUserAcc().getFirstName();
            this.drinkerLastName = speedDrinking.getDrinkerUserAcc().getLastName();
            this.drinkerNickName = speedDrinking.getDrinkerUserAcc().getNickName();
            this.drinkerTeamId = speedDrinking.getDrinkerUserAcc().getTeam().getId();
            this.drinkerTeamName = speedDrinking.getDrinkerUserAcc().getTeam().getName();
        }
    }
}
