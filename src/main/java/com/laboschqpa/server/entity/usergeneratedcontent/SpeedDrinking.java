package com.laboschqpa.server.entity.usergeneratedcontent;

import com.laboschqpa.server.entity.account.UserAcc;
import com.laboschqpa.server.enums.converter.attributeconverter.SpeedDrinkingCategoryAttributeConverter;
import com.laboschqpa.server.enums.ugc.SpeedDrinkingCategory;
import com.laboschqpa.server.enums.ugc.UserGeneratedContentTypeValues;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "speed_drinking")
@DiscriminatorValue(value = UserGeneratedContentTypeValues.SPEED_DRINKING)
public class SpeedDrinking extends UserGeneratedContent {
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "drinker_user_id", nullable = false)
    private UserAcc drinkerUserAcc;

    @Column(name = "time")
    private Double time;

    @Convert(converter = SpeedDrinkingCategoryAttributeConverter.class)
    @Column(name = "category", columnDefinition = "tinyint not null", nullable = false)
    private SpeedDrinkingCategory category;

    @Column(name = "note", columnDefinition = "text")
    private String note;
}
