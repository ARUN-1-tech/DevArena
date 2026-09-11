package com.devarena.skill.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "skills")
public class SkillEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true, length = 64)
    private String code;

    @Column(nullable = false, length = 128)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 64)
    private SkillCategory category;

    @Column(nullable = false, length = 255)
    private String description;

    @Column(nullable = false, length = 64)
    private String icon;

    @Column(name = "max_level", nullable = false)
    private int maxLevel = 5;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prerequisite_id")
    private SkillEntity prerequisite;

    @Column(name = "order_index", nullable = false)
    private int orderIndex = 0;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public SkillEntity() {}

    public SkillEntity(String code, String name, SkillCategory category, String description,
                       String icon, int maxLevel, SkillEntity prerequisite, int orderIndex) {
        this.code = code;
        this.name = name;
        this.category = category;
        this.description = description;
        this.icon = icon;
        this.maxLevel = maxLevel;
        this.prerequisite = prerequisite;
        this.orderIndex = orderIndex;
        this.createdAt = Instant.now();
    }

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public SkillCategory getCategory() { return category; }
    public void setCategory(SkillCategory category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public int getMaxLevel() { return maxLevel; }
    public void setMaxLevel(int maxLevel) { this.maxLevel = maxLevel; }

    public SkillEntity getPrerequisite() { return prerequisite; }
    public void setPrerequisite(SkillEntity prerequisite) { this.prerequisite = prerequisite; }

    public int getOrderIndex() { return orderIndex; }
    public void setOrderIndex(int orderIndex) { this.orderIndex = orderIndex; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
