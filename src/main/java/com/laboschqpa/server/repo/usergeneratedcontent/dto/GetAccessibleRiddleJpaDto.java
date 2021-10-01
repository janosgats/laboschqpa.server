package com.laboschqpa.server.repo.usergeneratedcontent.dto;

import com.laboschqpa.server.enums.RiddleCategory;
import com.laboschqpa.server.enums.converter.attributeconverter.RiddleCategoryAttributeConverter;

public interface GetAccessibleRiddleJpaDto extends GetUserGeneratedContentJpaDto {
    String getTitle();

    Integer getCategoryVal();

    default RiddleCategory getCategory() {
        if (getCategoryVal() != null) {
            return new RiddleCategoryAttributeConverter().convertToEntityAttribute(getCategoryVal());
        }
        return null;
    }

    String getHint();

    String getSolution();

    Boolean getWasHintUsed();

    /**
     * LIMITATION of JPA PROJECTION: This method has to return an Object {@code Boolean}, not a Primitive {@code boolean},
     * because the JPA mapper is only able to handle projections if the return value is NOT a Primitive.
     * <br>
     * LIMITATION of BEAN ACCESSOR: Because the return value is not a Primitive but an Object,
     * the method name has to start with {@code "get"} instead of {@code "is"}
     * to be handled as an accessor method when used by the spring Aspect stuff.
     *
     * @return {@code True} if the current team already solved the riddle, {@code false} otherwise.
     */
    Boolean getIsAlreadySolved();
}
