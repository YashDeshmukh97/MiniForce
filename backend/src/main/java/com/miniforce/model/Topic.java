package com.miniforce.model;
import jakarta.persistence.*;
@Entity @Table(name="topics") public class Topic {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,unique=true,length=70) private String name;
 public Long getId(){return id;} public String getName(){return name;} public void setName(String v){name=v;}
}
