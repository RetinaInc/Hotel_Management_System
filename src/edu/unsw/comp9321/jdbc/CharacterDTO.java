package edu.unsw.comp9321.jdbc;

import java.util.List;

public class CharacterDTO {

	public CharacterDTO(int id, String name, String diet, String[] sounds) {
		super();
		this.id = id;
		this.name = name;
		this.diet = diet;
		this.sounds = sounds;
	}
	
	private int id;
	private String name;
	private String diet;
	private String[] sounds;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDiet() {
		return diet;
	}
	public void setDiet(String diet) {
		this.diet = diet;
	}
	public String[] getSounds() {
		return sounds;
	}
	public void setSounds(String[] sounds) {
		this.sounds = sounds;
	}
	
}
