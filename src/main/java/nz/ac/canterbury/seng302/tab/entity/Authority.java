package nz.ac.canterbury.seng302.tab.entity;

import jakarta.persistence.*;

/**
 * Code repurposed from Morgan English's security example:
 * https://eng-git.canterbury.ac.nz/men63/spring-security-example-2023
 */
@Entity
public class Authority {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "authorityId")
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "userId")
    private User user;

    @Column
    private String role;

    protected Authority() {}

    public Authority(String role)
    {
        this.role = role;
    }

    public String getRole()
    {
        return role;
    }
}
