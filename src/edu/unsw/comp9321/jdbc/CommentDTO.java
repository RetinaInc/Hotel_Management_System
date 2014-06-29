package edu.unsw.comp9321.jdbc;

import java.util.Date;

public class CommentDTO {
	
	private int characterID;
	private String characterName;
	private String user;
	private Date commentDate;
	private String comment;
	
	
	public CommentDTO(int characterID, String characterName, String user,
			Date commentDate, String comment) {

		this.characterID = characterID;
		this.characterName = characterName;
		this.user = user;
		this.commentDate = commentDate;
		this.comment = comment;
	}
	
	public int getCharacterID() {
		return characterID;
	}
	
	public void setCharacterID(int characterID) {
		this.characterID = characterID;
	}
	public String getCharacterName() {
		return characterName;
	}
	public void setCharacterName(String characterName) {
		this.characterName = characterName;
	}
	public String getUser() {
		return user;
	}
	public void setUser(String user) {
		this.user = user;
	}
	public Date getCommentDate() {
		return commentDate;
	}
	public void setCommentDate(Date commentDate) {
		this.commentDate = commentDate;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}

}
