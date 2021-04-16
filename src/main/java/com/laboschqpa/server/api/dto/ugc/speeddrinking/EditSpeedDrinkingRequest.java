package com.laboschqpa.server.api.dto.ugc.speeddrinking;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.laboschqpa.server.enums.converter.jackson.SpeedDrinkingCategoryFromValueJacksonConverter;
import com.laboschqpa.server.enums.ugc.SpeedDrinkingCategory;
import com.laboschqpa.server.util.SelfValidator;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@EqualsAndHashCode(callSuper = true)
@Data
public class EditSpeedDrinkingRequest extends SelfValidator {
    @NotNull
    @Min(1)
    private Long id;
    @NotNull
    @Min(1)
    private Long drinkerUserId;
    @NotNull
    @DecimalMin(value = "0", inclusive = false)
    private Double time;
    @NotNull
    @JsonDeserialize(converter = SpeedDrinkingCategoryFromValueJacksonConverter.class)
    private SpeedDrinkingCategory category;
    @Length(max = 250)
    private String note;
}
