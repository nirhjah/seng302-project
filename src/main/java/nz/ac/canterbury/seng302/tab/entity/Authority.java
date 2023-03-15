package nz.ac.canterbury.seng302.tab.entity;

import jakarta.persistence.*;

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
