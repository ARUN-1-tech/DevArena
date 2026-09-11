package com.devarena.team.model;

import com.devarena.common.model.BaseAuditEntity;
import com.devarena.user.model.UserEntity;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "teams")
public class TeamEntity extends BaseAuditEntity {

    @Column(name = "name", nullable = false, unique = true, length = 100)
    private String name;

    @Column(name = "slug", nullable = false, unique = true, length = 100)
    private String slug;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "avatar", length = 255)
    private String avatar = "team-avatar-1";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private UserEntity owner;

    @Column(name = "max_members", nullable = false)
    private int maxMembers = 10;

    @Column(name = "rating", nullable = false)
    private int rating = 1000;

    @Column(name = "wins", nullable = false)
    private int wins = 0;

    @Column(name = "losses", nullable = false)
    private int losses = 0;

    @Column(name = "battles", nullable = false)
    private int battles = 0;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeamMemberEntity> members = new ArrayList<>();

    public TeamEntity() {}

    public TeamEntity(String name, String slug, String description, String avatar, UserEntity owner, int maxMembers) {
        this.name = name;
        this.slug = slug;
        this.description = description;
        this.avatar = avatar != null ? avatar : "team-avatar-1";
        this.owner = owner;
        this.maxMembers = maxMembers > 0 ? maxMembers : 10;
        this.rating = 1000;
        this.wins = 0;
        this.losses = 0;
        this.battles = 0;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public UserEntity getOwner() {
        return owner;
    }

    public void setOwner(UserEntity owner) {
        this.owner = owner;
    }

    public int getMaxMembers() {
        return maxMembers;
    }

    public void setMaxMembers(int maxMembers) {
        this.maxMembers = maxMembers;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public int getBattles() {
        return battles;
    }

    public void setBattles(int battles) {
        this.battles = battles;
    }

    public List<TeamMemberEntity> getMembers() {
        return members;
    }

    public void setMembers(List<TeamMemberEntity> members) {
        this.members = members;
    }
}
