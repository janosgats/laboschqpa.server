package com.laboschqpa.server.api.service.riddle;

import org.apache.commons.lang3.tuple.Triple;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class RiddleHelpersTest {
    private static Stream<Triple<String, String, Boolean>> provideArgumentsFor_areSolutionsSimilar() {
        return Stream.of(
                Triple.of("", "", true),
                Triple.of("+-.:() \t", "", true),
                Triple.of("asd", "asd", true),
                Triple.of("AsdASD", "asdasd", true),
                Triple.of("a  a", "aa", true),
                Triple.of(" a  a ", "a a ", true),
                Triple.of("f7h-za3-", "f7hza3", true),
                Triple.of("34qfp+!()", "34qfp", true),
                Triple.of("As+A!f-Ea", "a-s.a_f:ea", true),
                Triple.of("a,b", "ab", true),
                Triple.of("sdf", "fds", false),
                Triple.of("s  s", "a  s", false),
                Triple.of("a", "", false),
                Triple.of("", "d-", false),
                Triple.of("sdvbag4e3r", "87lk978", false)
        );
    }

    @ParameterizedTest
    @MethodSource("provideArgumentsFor_areSolutionsSimilar")
    void areSolutionsSimilar(Triple<String, String, Boolean> triple) {
        assertEquals(triple.getRight(), RiddleHelpers.areSolutionsSimilar(triple.getLeft(), triple.getMiddle()));
    }
}