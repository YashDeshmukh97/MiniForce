package com.miniforce.model;
import jakarta.persistence.*;
@Entity @Table(name="test_cases") public class TestCase {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @ManyToOne(optional=false,fetch=FetchType.LAZY) @JoinColumn(name="problem_id") private Problem problem;
 @Column(name="input_data",nullable=false,columnDefinition="TEXT") private String inputData;
 @Column(name="expected_output",nullable=false,columnDefinition="TEXT") private String expectedOutput;
 @Column(nullable=false) private boolean sampleCase;
 public Long getId(){return id;} public Problem getProblem(){return problem;} public void setProblem(Problem v){problem=v;} public String getInputData(){return inputData;} public void setInputData(String v){inputData=v;} public String getExpectedOutput(){return expectedOutput;} public void setExpectedOutput(String v){expectedOutput=v;} public boolean isSampleCase(){return sampleCase;} public void setSampleCase(boolean v){sampleCase=v;}
}
