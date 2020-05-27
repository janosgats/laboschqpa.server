package com.laboschqpa.server.entity.usergeneratedcontent;

import com.laboschqpa.server.enums.ugc.UserGeneratedContentTypeValues;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "riddle")
@DiscriminatorValue(value = UserGeneratedContentTypeValues.RIDDLE)
public class Riddle extends UserGeneratedContent {
    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "hint")
    private String hint;

    @Column(name = "solution", nullable = false)
    private String solution;
}
