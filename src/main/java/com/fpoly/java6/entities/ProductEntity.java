package com.fpoly.java6.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
@Table(name = "products")
public class ProductEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false)
	private int id;
	
	@Column(name = "name", nullable = false, columnDefinition = "NVARCHAR (200)")
	private String name;
	
	@Column(name = "description", nullable = true, columnDefinition = "NVARCHAR (MAX)")
	private String desc;
	
	@Column(name = "price", nullable = false)
	private int price;
	
	@Column(name = "quantity", nullable = false)
	private int quantity = 0;
	
	@Column(name = "status", nullable = false)
	private int status = 1;
	
	@Column(name = "image", nullable = false, length = 200)
	private String image;
	
	@ManyToOne
	@JoinColumn(name = "category_id")
	private CategoryEntity categoryEntity;
}


//CREATE TABLE [dbo].[products] (
//	    [id]          INT           IDENTITY (1, 1) NOT NULL,
//	    [category_id] INT           NULL,
//	    [name]        NVARCHAR (200) NOT NULL,
//	    [description] NVARCHAR (MAX) NULL,
//	    [price]       INT           NOT NULL,
//	    [quantity]    INT           DEFAULT ((0)) NOT NULL,
//	    [status]      TINYINT       DEFAULT ((1)) NOT NULL,
//	    [image]       VARCHAR (200) NOT NULL,
//	    PRIMARY KEY CLUSTERED ([id] ASC),
//	    FOREIGN KEY ([category_id]) REFERENCES [dbo].[categories] ([id])
//	);
//
