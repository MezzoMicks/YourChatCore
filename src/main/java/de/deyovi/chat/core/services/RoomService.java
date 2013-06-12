package de.deyovi.chat.core.services;

import java.util.List;

import de.deyovi.chat.core.objects.Room;
import de.deyovi.chat.core.objects.Room.RoomInfo;
import de.deyovi.chat.core.objects.impl.DefaultRoom;

public interface RoomService {

	public abstract Room spawn(String name);

	public abstract void remove(DefaultRoom room);

	public abstract Room getByName(String name);

	public abstract boolean isMainRoom(String name);

	public abstract Room getDefault();

	public abstract List<RoomInfo> getOpenRooms();

}