package com.laboschqpa.server.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "team",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"name"}, name = "name__unique")
        }
)
public class Team {
    public Team(Long id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "archived", nullable = false)
    private Boolean archived = false;

    @Column(name = "auto_approve_applications", nullable = false)
    private Boolean autoApproveApplications = false;

    @Column(name = "allow_members_to_exit", nullable = false)
    private Boolean allowMembersToExit = true;
}
