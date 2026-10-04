package com.miniforce.model;
import jakarta.persistence.*;
@Entity @Table(name="contest_problems",uniqueConstraints=@UniqueConstraint(columnNames={"contest_id","problem_id"})) public class ContestProblem {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id; @ManyToOne(optional=false) @JoinColumn(name="contest_id") private Contest contest; @ManyToOne(optional=false) @JoinColumn(name="problem_id") private Problem problem; @Column(nullable=false) private int points=100;
 public Long getId(){return id;} public Contest getContest(){return contest;} public void setContest(Contest v){contest=v;} public Problem getProblem(){return problem;} public void setProblem(Problem v){problem=v;} public int getPoints(){return points;} public void setPoints(int v){points=v;}
}
