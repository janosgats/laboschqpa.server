package com.laboschqpa.server.api.dto.ugc.objective;

import com.laboschqpa.server.enums.converter.jackson.ObjectiveTypeFromValueJacksonConverter;
import com.laboschqpa.server.enums.ugc.ObjectiveType;
import com.laboschqpa.server.util.SelfValidator;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@EqualsAndHashCode(callSuper = true)
@Data
public class ListObjectivesRequest extends SelfValidator {
    private static final ObjectiveTypeFromValueJacksonConverter OBJECTIVE_TYPE_FROM_VALUE_JACKSON_CONVERTER = new ObjectiveTypeFromValueJacksonConverter();

    @NotNull
    private Set<ObjectiveType> objectiveTypes;

    public void setObjectiveTypes(Collection<Integer> objectiveTypes) {
        this.objectiveTypes = objectiveTypes.stream()
                .map(OBJECTIVE_TYPE_FROM_VALUE_JACKSON_CONVERTER::convert)
                .collect(Collectors.toSet());
    }
}
