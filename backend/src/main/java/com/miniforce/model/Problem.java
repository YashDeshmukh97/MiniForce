package com.miniforce.model;
import jakarta.persistence.*; import java.time.Instant; import java.util.*;
@Entity @Table(name="problems", indexes={@Index(name="idx_problem_difficulty",columnList="difficulty"),@Index(name="idx_problem_slug",columnList="slug")})
public class Problem {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,unique=true,length=100) private String slug;
 @Column(nullable=false,length=180) private String title;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=10) private Difficulty difficulty;
 @Column(nullable=false,columnDefinition="TEXT") private String description;
 @Column(name="starter_code",columnDefinition="TEXT") private String starterCode;
 @Column(name="starter_code_python",columnDefinition="TEXT") private String starterCodePython;
 @Column(name="is_public",nullable=false) private Boolean isPublic = true;
 @Column(name="constraints_text",columnDefinition="TEXT") private String constraintsText;
 @Column(name="acceptance_rate",nullable=false) private double acceptanceRate;
 @Column(name="created_at",nullable=false,updatable=false) private Instant createdAt=Instant.now();
 @ManyToMany @JoinTable(name="problem_topics",joinColumns=@JoinColumn(name="problem_id"),inverseJoinColumns=@JoinColumn(name="topic_id")) private Set<Topic> topics=new LinkedHashSet<>();
 public Long getId(){return id;} public String getSlug(){return slug;} public void setSlug(String v){slug=v;} public String getTitle(){return title;} public void setTitle(String v){title=v;} public Difficulty getDifficulty(){return difficulty;} public void setDifficulty(Difficulty v){difficulty=v;} public String getDescription(){return description;} public void setDescription(String v){description=v;} public String getStarterCode(){return starterCode;} public void setStarterCode(String v){starterCode=v;} public String getStarterCodePython(){return starterCodePython;} public void setStarterCodePython(String v){starterCodePython=v;} public Boolean getIsPublic(){return isPublic;} public void setIsPublic(Boolean v){isPublic=v;} public String getConstraintsText(){return constraintsText;} public void setConstraintsText(String v){constraintsText=v;} public double getAcceptanceRate(){return acceptanceRate;} public void setAcceptanceRate(double v){acceptanceRate=v;} public Set<Topic> getTopics(){return topics;} public void setTopics(Set<Topic> v){topics=v;}
}
