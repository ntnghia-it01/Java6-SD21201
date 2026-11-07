package com.fpoly.java6.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "categories")
public class CategoryEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private int id;
	
	@Column(name = "name", nullable = false, columnDefinition = "NVARCHAR (150)")
	private String name;
}


//CREATE TABLE [dbo].[categories] (
//	    [id]   INT           IDENTITY (1, 1) NOT NULL,
//	    [name] NVARCHAR (150) NOT NULL,
//	    PRIMARY KEY CLUSTERED ([id] ASC)
//	);
//
