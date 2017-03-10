package com.sanjoyghosh.company.db.model;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Cacheable(false)
public class AlexaUser {

	@Id()
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private int id;
	@Column
	private String alexaUser;
	
	
	public AlexaUser() {}


	public int getId() {
		return id;
	}


	public void setId(int id) {
		this.id = id;
	}


	public String getAlexaUser() {
		return alexaUser;
	}


	public void setAlexaUser(String alexaUser) {
		this.alexaUser = alexaUser;
	}


	@Override
	public String toString() {
		return "AlexaUser [id=" + id + ", alexaUser=" + alexaUser + "]";
	}
}
