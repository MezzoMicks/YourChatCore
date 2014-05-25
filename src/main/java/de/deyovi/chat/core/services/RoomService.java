package de.deyovi.chat.core.services;

import de.deyovi.chat.core.objects.ChatUser;
import de.deyovi.chat.core.objects.Room;
import de.deyovi.chat.core.objects.Room.RoomInfo;

import java.util.List;

public interface RoomService {

	Room spawn(String name);

	void remove(Room room);

	Room getByName(String name);

	boolean isMainRoom(String name);

	Room getDefault();

	List<RoomInfo> getOpenRooms();

    void join(Room room, ChatUser user);

}