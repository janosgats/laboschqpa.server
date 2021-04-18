package com.laboschqpa.server.api.dto.ugc.speeddrinking;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.laboschqpa.server.enums.converter.jackson.SpeedDrinkingCategoryFromValueJacksonConverter;
import com.laboschqpa.server.enums.ugc.SpeedDrinkingCategory;
import com.laboschqpa.server.util.SelfValidator;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class DisplayListSpeedDrinkingRequest extends SelfValidator {
    @NotNull
    @JsonDeserialize(converter = SpeedDrinkingCategoryFromValueJacksonConverter.class)
    private SpeedDrinkingCategory category;
    @Min(1)
    private Long teamId;
}
