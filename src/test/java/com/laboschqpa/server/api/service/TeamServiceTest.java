package com.laboschqpa.server.api.service;

import com.laboschqpa.server.exceptions.apierrordescriptor.ContentNotFoundException;
import com.laboschqpa.server.repo.TeamRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {
    @Mock
    TeamRepository teamRepository;
    @InjectMocks
    TeamService teamService;

    @Test
    void assertTeamExists() {
        long id = 123;
        when(teamRepository.findById(id))
                .thenReturn(Optional.empty());

        assertThrows(ContentNotFoundException.class, () -> teamService.assertTeamExists(id));
    }
}