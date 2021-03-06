package de.deyovi.chat.core.entities;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * Entity implementation class for Entity: User
 *
 */
@Entity
@Table(name="chatuser")
@NamedQueries({
	@NamedQuery(name="findUserByName", 
				query="SELECT u " //
					+ "FROM ChatUserEntity u " //
					+ "WHERE LOWER(u.name) = :name"//
				),
	@NamedQuery(name="findUserNamesByName", 
				query="SELECT u " //
					+ "FROM ChatUserEntity u " //
					+ "WHERE LOWER(u.name) like :name"//
				),
	@NamedQuery(name="findAll", 
				query="SELECT u " //
					+ "FROM ChatUserEntity u " //
				),
	@NamedQuery(name="deleteAll", 
				query="DELETE " //
					+ "FROM ChatUserEntity u " //
				)
})
public class ChatUserEntity implements Serializable {

	private static final long serialVersionUID = 1L;
	   
	@Id
	@GeneratedValue( strategy = GenerationType.IDENTITY )
	private long id;
	@Basic
	@Column(length=24)
	private String name;
	@Basic
	@Column(length=64)
	private String password;
	@Basic
	@Column(length=8)
	private String color;
	@Basic
	@Column(length=20)
	private String font;
	@Basic
	private boolean trusted;
	@Basic
	@Column(length=20)
	private String room;
	@Basic
	private Timestamp lastlogin;
	@OneToOne(fetch = FetchType.EAGER, orphanRemoval = true)
	private ProfileEntity profile;
	@Basic
	private boolean asyncmode;

	private Timestamp entryDate;
	public ChatUserEntity() {
		super();
	}   
	public long getId() {
		return this.id;
	}

	public void setId(long param) {
		this.id = param;
	}   
	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}   
	public String getPassword() {
		return this.password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public String getFont() {
		return font;
	}
	public void setFont(String font) {
		this.font = font;
	}
	public boolean isTrusted() {
		return trusted;
	}
	public void setTrusted(boolean trusted) {
		this.trusted = trusted;
	}
	public Timestamp getLastlogin() {
		return lastlogin;
	}
	public void setLastlogin(Timestamp lastLogin) {
		this.lastlogin = lastLogin;
	}
	public ProfileEntity getProfile() {
	    return profile;
	}
	public void setProfile(ProfileEntity param) {
	    this.profile = param;
	}
	public String getRoom() {
		return room;
	}
	public void setRoom(String room) {
		this.room = room;
	}
	public boolean isAsyncmode() {
		return asyncmode;
	}
	public void setAsyncmode(boolean asyncmode) {
		this.asyncmode = asyncmode;
	}
	public Timestamp getEntryDate() {
		return entryDate;
	}
	public void setEntryDate(Timestamp entryDate) {
		this.entryDate = entryDate;
	}
   
}
