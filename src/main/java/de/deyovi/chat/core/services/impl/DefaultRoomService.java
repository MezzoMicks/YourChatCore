package de.deyovi.chat.core.services.impl;

import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import de.deyovi.chat.core.objects.Room;
import de.deyovi.chat.core.objects.Room.RoomInfo;
import de.deyovi.chat.core.objects.impl.DefaultRoom;
import de.deyovi.chat.core.services.RoomService;
import de.deyovi.chat.core.utils.ChatConfiguration;

public class DefaultRoomService implements RoomService {

	private final static Logger logger = Logger.getLogger(DefaultRoomService.class);
	
	private static volatile RoomService instance;
	
	private final TreeMap<String, Room> mains = new TreeMap<String, Room>();
	private final TreeMap<String, Room> subs = new TreeMap<String, Room>();
	
	private final Room defaultRoom;

	private DefaultRoomService() {
		// for later, we will need a default room
		Room defaultRoom = null;
		logger.info("Setting up Channels");
		// cycle through all configured rooms
		for (String channel : ChatConfiguration.getRooms()) {
			String name = channel;
			String color;
			// find the first pipe-symbol
			int ixOfPipe = name.indexOf('|');
			String motd4room;
			// in case there's one
			if (ixOfPipe >= 0) {
				// let's find the next one
				int ixOf2ndPipe = name.indexOf('|', ixOfPipe + 1);
				// if there's a second pipe
				if (ixOf2ndPipe >= 0) {
					// the color is between those two
					color = name.substring(ixOfPipe + 1, ixOf2ndPipe);
					// and the message of the day is after that
					motd4room = name.substring(ixOf2ndPipe + 1);
				} else {
					// the color is after that
					color = name.substring(ixOfPipe + 1);
					// and there's not motd
					motd4room = null;
				}
				name = name.substring(0, ixOfPipe);
			// there's no pipe
			} else {
				// so default background color (white)
				color = "FFFFFF";
				// and no motd
				motd4room = null;
			}
			logger.info("Room : " + name + " (" + color + ")");
			// lets create the room-object
			Room room = new DefaultRoom(name, false);
			room.setColor(color);
			room.setOpen(true);
			room.setAnonymous(false);
			mains.put(name.toLowerCase(), room);
			// no default room yet, let's take the first one
			if (defaultRoom == null) {
				defaultRoom = room;
			}
			if (motd4room != null) {
				room.setMotd(null, motd4room, null, null);
			}
		}
		this.defaultRoom = defaultRoom;
	}
	
	public static RoomService getInstance() {
		if (instance == null) {
			createInstance();
		}
		return instance;
	}
	
	public synchronized static void createInstance() {
		if (instance == null) {
			instance = new DefaultRoomService();
		}
	}
	
	/* (non-Javadoc)
	 * @see de.deyovi.chat.core.services.impl.RoomService#spawn(java.lang.String)
	 */
	@Override
	public DefaultRoom spawn(String name) {
		String lcName = name.toLowerCase();
		if (!mains.containsKey(lcName) && !subs.containsKey(lcName)) {
			DefaultRoom result = new DefaultRoom(name, true);
			subs.put(lcName, result);
			return result;
		} else {
			return null;
		}
	}
	
	/* (non-Javadoc)
	 * @see de.deyovi.chat.core.services.impl.RoomService#remove(de.deyovi.chat.core.objects.impl.DefaultRoom)
	 */
	@Override
	public void remove(DefaultRoom room) {
		String lcName = room.getName().toLowerCase();
		subs.remove(lcName);
	}
	
	/* (non-Javadoc)
	 * @see de.deyovi.chat.core.services.impl.RoomService#getByName(java.lang.String)
	 */
	@Override
	public Room getByName(String name) {
		if (name != null) {
			String lcRoomName = name.trim().toLowerCase();
			Room result = mains.get(lcRoomName);
			if (result == null) {
				result = subs.get(lcRoomName);
			}
			return result;
		} else {
			return null;
		}
	}

	/* (non-Javadoc)
	 * @see de.deyovi.chat.core.services.impl.RoomService#isMainRoom(java.lang.String)
	 */
	@Override
	public boolean isMainRoom(String name) {
		if (name != null) {
			String lcRoomName = name.trim().toLowerCase();
			return mains.containsKey(lcRoomName);
		} else {
			return false;
		}
	}
	
	/* (non-Javadoc)
	 * @see de.deyovi.chat.core.services.impl.RoomService#getDefault()
	 */
	@Override
	public Room getDefault() {
		return defaultRoom;
	}
	
	/* (non-Javadoc)
	 * @see de.deyovi.chat.core.services.impl.RoomService#getOpenRooms()
	 */
	@Override
	public List<RoomInfo> getOpenRooms() {
		List<RoomInfo> result = new LinkedList<RoomInfo>();
		for (Room main : mains.values()) {
			result.add(main.getInfoForUser(null));
		}
		for (Room sub : subs.values()) {
			if (sub.isVisible()) {
				result.add(sub.getInfoForUser(null));
			}
		}
		return result;
	}
	
}
