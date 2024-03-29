package com.laboschqpa.server.repo.usergeneratedcontent;

import com.laboschqpa.server.entity.usergeneratedcontent.Riddle;
import com.laboschqpa.server.enums.RiddleCategory;
import com.laboschqpa.server.enums.converter.attributeconverter.RiddleCategoryAttributeConverter;
import com.laboschqpa.server.enums.riddle.RiddleResolutionStatusValues;
import com.laboschqpa.server.repo.usergeneratedcontent.dto.GetAccessibleRiddleJpaDto;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Accessible riddles = Solved riddles + riddles visible for solving as next.
 */
public interface RiddleRepository extends JpaRepository<Riddle, Long> {
    /**
     * The teams can see this many pieces of the next unsolved riddles in the row. (number of riddles visible for solving as next)
     */
    String NUMBER_OF_FORWARD_VISIBLE_RIDDLES = "2";

    @Modifying
    @Query("delete from Riddle where id = :id")
    int deleteByIdAndGetDeletedRowCount(Long id);

    @EntityGraph(attributePaths = {"attachments"})
    @Query("select r from Riddle r where r.id = :id")
    Optional<Riddle> findByIdWithEagerAttachments(Long id);

    List<Riddle> findAllByCategory(RiddleCategory category);

    //language=MySQL
    String QUERY_TO_SELECT_ACCESSIBLE_RIDDLE_IDS =
            "select " +
                    "id, hint_used, 'TRUE' as is_already_solved\n" +
                    "from (\n" +
                    "         select riddle.id as id, IF(resolution.hint_used <=> 1, 'TRUE', 'FALSE') as hint_used\n" +
                    "         from riddle\n" +
                    "                  inner join riddle_resolution resolution\n" +
                    "                             on resolution.riddle_id = riddle.id\n" +
                    "                                 and resolution.team_id = :teamId\n" +
                    "         where resolution.status = " + RiddleResolutionStatusValues.SOLVED + "\n" +
                    "               and riddle.category = :categoryValue" +
                    "     ) as solved_riddle_ids\n" +
                    "" +
                    "UNION\n" +
                    "" +
                    "select id, hint_used, 'FALSE' as is_already_solved\n" +
                    "from (\n" +
                    "         select riddle.id as id, IF(resolution.hint_used <=> 1, 'TRUE', 'FALSE') as hint_used\n" +
                    "         from riddle\n" +
                    "                  left join riddle_resolution resolution\n" +
                    "                            on resolution.riddle_id = riddle.id\n" +
                    "                                and resolution.team_id = :teamId\n" +
                    "         where" +
                    "               (resolution.status is NULL or resolution.status = " + RiddleResolutionStatusValues.UNSOLVED + ")\n" +
                    "               and riddle.category = :categoryValue " +
                    "         order by riddle.id asc\n" +
                    "         limit " + NUMBER_OF_FORWARD_VISIBLE_RIDDLES + "\n" +
                    "     ) as next_riddles_to_solve_ids";

    @Query(value = QUERY_TO_SELECT_ACCESSIBLE_RIDDLE_IDS,
            nativeQuery = true)
    List<Long> findAccessibleRiddleIds_internal(@Param("teamId") Long teamId, @Param("categoryValue") Integer categoryValue);

    default List<Long> findAccessibleRiddleIds(Long teamId, RiddleCategory category) {
        final Integer categoryValue = new RiddleCategoryAttributeConverter().convertToDatabaseColumn(category);
        return findAccessibleRiddleIds_internal(teamId, categoryValue);
    }

    @Query(value =
            "select \n" +
                    "       ugc.id              as id,\n" +
                    "       ugc.creator_user_id as creatorUserId,\n" +
                    "       ugc.editor_user_id  as editorUserId,\n" +
                    "       ugc.creation_time   as creationTime,\n" +
                    "       ugc.edit_time       as editTime,\n" +
                    "       riddle.title        as title,\n" +
                    "       riddle.category        as categoryVal,\n" +
                    "       riddle.hint         as hint,\n" +
                    "       riddle.solution     as solution,\n" +
                    "       is_already_solved   as isAlreadySolved,\n" +
                    "       hint_used           as wasHintUsed\n" +
                    "from riddle\n" +
                    "         join user_generated_content ugc on riddle.id = ugc.id\n" +
                    "         inner join (\n" +
                    "    " +
                    QUERY_TO_SELECT_ACCESSIBLE_RIDDLE_IDS +
                    "\n" +
                    ") as accessible_riddle_ids\n" +
                    "                    on riddle.id = accessible_riddle_ids.id;",
            nativeQuery = true)
    List<GetAccessibleRiddleJpaDto> findAccessibleRiddles_internal(@Param("teamId") Long teamId, @Param("categoryValue") Integer categoryValue);

    default List<GetAccessibleRiddleJpaDto> findAccessibleRiddles(Long teamId, RiddleCategory category) {
        final Integer categoryValue = new RiddleCategoryAttributeConverter().convertToDatabaseColumn(category);
        return findAccessibleRiddles_internal(teamId, categoryValue);
    }
}
