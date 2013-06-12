package de.deyovi.chat.core.objects;

public interface ChatUserSettings {

	public String getColor();

	public void setColor(String color);

	public String getFont();

	public void setFont(String font);

	public String getFavouriteRoom();
	
	public void setFavouriteRoom(String room);

	public boolean isTrusted();

	public void setTrusted(boolean trusted);
	
	public boolean hasAsyncMode();

	public void setAsyncmode(boolean asyncmode);
	
}
