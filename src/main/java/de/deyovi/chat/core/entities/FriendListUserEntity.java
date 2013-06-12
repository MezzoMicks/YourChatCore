package de.deyovi.chat.core.entities;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "friendlistuser")
public class FriendListUserEntity {

	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY )
	private long id;
	@Basic
	private boolean confirmed;
	@OneToOne
	private ChatUserEntity userEntity;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	public boolean isConfirmed() {
		return confirmed;
	}
	
	public void setConfirmed(boolean param) {
		this.confirmed = param;
	}

	public ChatUserEntity getUserEntity() {
	    return userEntity;
	}

	public void setUserEntity(ChatUserEntity param) {
	    this.userEntity = param;
	}

}