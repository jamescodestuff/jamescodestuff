import java.util.ArrayList;

/**
 * This class contains methods which perform various operations on a layered
 * linked list to simulate transit
 * 
 * @author Ishaan Ivaturi
 * @author Prince Rawal
 */
public class Transit {
	/**
	 * Makes a layered linked list representing the given arrays of train stations,
	 * bus stops, and walking locations. Each layer begins with a location of 0,
	 * even though the arrays don't contain the value 0.
	 * 
	 * @param trainStations Int array listing all the train stations
	 * @param busStops      Int array listing all the bus stops
	 * @param locations     Int array listing all the walking locations (always
	 *                      increments by 1)
	 * @return The zero node in the train layer of the final layered linked list
	 */
	public static TNode makeList(int[] trainStations, int[] busStops, int[] locations) {
		TNode head = new TNode();
		TNode ptr = head; // walking
		head.location = 0;

		TNode bhead = new TNode();
		TNode bptr = bhead; // bus
		bhead.location = 0;

		TNode thead = new TNode();
		TNode tptr = thead; // train
		thead.location = 0;

		// walking
		for (int i = 0; i < locations.length; i++) {
			TNode temp = new TNode(locations[i]);
			ptr.next = temp;
			ptr = ptr.next;
		}

		// bus
		ptr = head;
		for (int i = 0; i <= busStops.length; i++) {
			if (i < busStops.length) {
				TNode temp = new TNode(busStops[i]);
				bptr.next = temp;
			}
			for (int j = 0; ptr != null && bptr.location != ptr.location; j++) {
				ptr = ptr.next;
			}
			if (ptr != null)
				bptr.down = ptr;
			// System.out.println("Bus ptr "+bptr.location+" links to "+bptr.down.location);
			ptr = head;
			bptr = bptr.next;
		}

		// train
		bptr = bhead;
		for (int i = 0; i <= trainStations.length; i++) {
			if (i < trainStations.length) {
				TNode temp = new TNode(trainStations[i]);
				tptr.next = temp;
			}
			for (int j = 0; ptr != null && tptr.location != bptr.location; j++) {
				bptr = bptr.next;
			}
			if (bptr != null)
				tptr.down = bptr;
			bptr = bhead;
			tptr = tptr.next;

		}
		return thead;
	}

	/**
	 * Modifies the given layered list to remove the given train station but NOT its
	 * associated bus stop or walking location. Do nothing if the train station
	 * doesn't exist
	 * 
	 * @param trainZero The zero node in the train layer of the given layered list
	 * @param station   The location of the train station to remove
	 */
	public static void removeTrainStation(TNode trainZero, int station) {
		if (trainZero.location == station) {
			trainZero = trainZero.next;
			return;
		}
		TNode ptr = trainZero;
		for (ptr = trainZero; ptr != null; ptr = ptr.next) {
			if (ptr != null && ptr.next != null && ptr.next.location == station) {
				ptr.next = ptr.next.next;
				break;
			}
		}
		if (ptr == null)
			return;
		ptr = ptr.next;
	}

	/**
	 * Modifies the given layered list to add a new bus stop at the specified
	 * location. Do nothing if there is no corresponding walking location.
	 * 
	 * @param trainZero The zero node in the train layer of the given layered list
	 * @param busStop   The location of the bus stop to add
	 */
	public static void addBusStop(TNode trainZero, int busStop) {
		TNode ptr = new TNode(trainZero.down.location, null, null); // bus layer
		TNode bus = new TNode(busStop, null, null);
		for (ptr = trainZero.down; ptr != null; ptr = ptr.next) {
			// System.out.println("ptr: " + ptr.location);
			if (ptr.location == bus.location) {
				break;
			}
			if (ptr != null && ptr.next != null && ptr.location < busStop && ptr.next.location > busStop) {
				TNode temp = ptr.next;
				System.out.println(temp.location);
				ptr.next = bus;
				bus.next = temp;

				TNode walking = trainZero.down.down; // making a walking head to link onto the bus
				for (int j = 0; walking != null && bus.location != walking.location; j++) {
					walking = walking.next;
				}
				if (walking != null)
					bus.down = walking;
				// System.out.println(ptr.location);
			} else if (ptr.next == null && bus.next == null) {
				ptr.next = bus;
				TNode walking = trainZero.down.down; // making a walking head to link onto the bus
				for (int j = 0; walking != null && bus.location != walking.location; j++) {
					walking = walking.next;
				}
				if (walking != null)
					bus.down = walking;
				break;
			}
		}

	}

	/**
	 * Determines the optimal path to get to a given destination in the walking
	 * layer, and collects all the nodes which are visited in this path into an
	 * arraylist.
	 * 
	 * @param trainZero   The zero node in the train layer of the given layered list
	 * @param destination An int representing the destination
	 * @return
	 */
	public static ArrayList<TNode> bestPath(TNode trainZero, int destination) {
		ArrayList<TNode> paths = new ArrayList<TNode>();
		TNode bus = new TNode(trainZero.down.location);
		TNode walking = new TNode(trainZero.down.down.location);
		// Train
		for (TNode ptr = trainZero; ptr != null; ptr = ptr.next) {
			// locate destination on the train layer
			if (ptr != null && ptr.location < destination) {
				paths.add(ptr);
				if (ptr.next != null && ptr.next.location > destination) {
					bus = ptr.down; // connecting the train to bus layer
					// paths.add(bus);
				} else if (ptr != null && ptr.next == null && destination > ptr.location) {
					paths.add(ptr);
					bus = ptr.down;
					paths.add(bus);
				}
			} else if (ptr != null && ptr.location == destination) { // checks for linking down
				paths.add(ptr);
				bus = ptr.down;
				paths.add(bus);
			}
		}
		// Bus
		for (TNode bptr = bus; bptr != null; bptr = bptr.next) { // locate destination on the bus layer
			if (bptr != null && bptr.location < destination) {
				paths.add(bptr);
				if (bptr.next != null && bptr.next.location > destination) {
					walking = bptr.down;
					paths.add(walking);
				} else if (bptr != null && bptr.next == null && destination > bptr.location) {
					// paths.add(bptr);
					walking = bptr.down;
					paths.add(walking);
				}
			} else if (bptr != null && bptr.location == destination) {
				// paths.add(bptr);
				walking = bptr.down;
				paths.add(walking);
			}
		}

		// Walking
		for (TNode wptr = walking; wptr != null; wptr = wptr.next) { // locate destination on the walking layer
			if (wptr != null && wptr.next != null && wptr.location < destination) {
				paths.add(wptr.next);
				if (wptr.next.location == destination) {
					bus = wptr;
					// paths.add(bus);
				}
			}
		}
		return paths;
	}

	/**
	 * Returns a deep copy of the given layered list, which contains exactly the
	 * same locations and connections, but every node is a NEW node.
	 * 
	 * @param trainZero The zero node in the train layer of the given layered list
	 * @return
	 */
	public static TNode duplicate(TNode trainZero) {
		// WALKING
		TNode whead = new TNode();// return Node
		TNode newPtr = whead;// Traverse Node
		TNode walking = trainZero.down.down.next;
		while (walking != null) { // walking
			TNode temp = new TNode(walking.location);
			newPtr.next = temp;
			newPtr = newPtr.next;
			walking = walking.next;
		}
		// BUS
		newPtr = whead;
		TNode bhead = new TNode();
		TNode newBptr = bhead;
		int counter = 0;
		TNode bus = trainZero.down.next;
		while (bus != null) { // go through, find length
			bus = bus.next;
			counter++;
		}
		TNode busDriver = trainZero.down.next;
		for (int i = 0; i <= counter; i++) {
			if (i < counter) {
				TNode temp = new TNode(busDriver.location);
				busDriver = busDriver.next;
				newBptr.next = temp;
			}
			for (int j = 0; newPtr != null && newBptr.location != newPtr.location; j++) {
				newPtr = newPtr.next;
			}
			if (newPtr != null)
				newBptr.down = newPtr;
			// System.out.println("Bus ptr "+bptr.location+" links to "+bptr.down.location);
			newPtr = whead;
			newBptr = newBptr.next;
		}
		// train
		newBptr = bhead;
		TNode thead = new TNode();
		TNode newTptr = thead;
		int trainCounter = 0;
		TNode train = trainZero.next;
		while (train != null) { // go through, find length
			train = train.next;
			trainCounter++;
		}
		TNode trainDriver = trainZero.next;
		for (int i = 0; i <= trainCounter; i++) {
			if (i < trainCounter) {
				TNode temp = new TNode(trainDriver.location);
				trainDriver = trainDriver.next;
				newTptr.next = temp;
			}
			for (int j = 0; newPtr != null && newTptr.location != newBptr.location; j++) {
				newBptr = newBptr.next;
			}
			if (newBptr != null)
				newTptr.down = newBptr;
			newBptr = bhead;
			newTptr = newTptr.next;
		}
		return thead;
	}

	/**
	 * Modifies the given layered list to add a scooter layer in between the bus and
	 * walking layer.
	 * 
	 * @param trainZero    The zero node in the train layer of the given layered
	 *                     list
	 * @param scooterStops An int array representing where the scooter stops are
	 *                     located
	 */
	public static void addScooter(TNode trainZero, int[] scooterStops) {
		TNode whead = trainZero.down.down;
		TNode bhead = trainZero.down;
		TNode walking = whead; // store walking
		TNode bus = trainZero.down;

		TNode shead = new TNode(); // Make ScooterList
		// connect scooter to walker!! then set trainZero.down.down.down to walking
		TNode scooterDriver = shead;
		for (int i = 0; i <= scooterStops.length; i++) {
			if (i < scooterStops.length) {
				TNode temp = new TNode(scooterStops[i]);
				scooterDriver.next = temp;
			}
			for (int j = 0; walking != null && scooterDriver.location != walking.location; j++) {
				walking = walking.next;
			}
			if (walking != null)
				scooterDriver.down = walking;
			walking = whead;
			scooterDriver = scooterDriver.next;
		}
		trainZero.down.down = shead;

		// int counter = 0;
		// while (bus != null) { // go through, find length
		// bus = bus.next;
		// counter++;
		// }
		bus = bhead;
		// System.out.println("busnext: "+bus.next.location);
		while (bus != null) {
			// bus = bus.next;
			// System.out.println("busnext: "+bus.next.location);
			for (int j = 0; bus != null && scooterDriver != null && bus.location != scooterDriver.location; j++) {
				scooterDriver = scooterDriver.next;
			}
			if (scooterDriver != null)
				bus.down = scooterDriver;
			scooterDriver = shead;
			bus = bus.next;
		}
		trainZero.down.down = shead;
		trainZero.down = bhead;

	}
}