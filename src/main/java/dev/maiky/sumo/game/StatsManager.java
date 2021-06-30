package dev.maiky.sumo.game;

import com.mongodb.*;
import dev.maiky.sumo.Sumo;

import java.util.HashMap;
import java.util.UUID;

/**
 * Door: Maiky
 * Info: Sumo - 12 Apr 2021
 * Package: dev.maiky.sumo.game
 */

public class StatsManager {

	private MongoClient client;
	private DB database;
	private DBCollection collection;

	public StatsManager(){
		this.client = new MongoClient("localhost", 27017);
		this.database = this.client.getDB("events");
		this.collection = this.database.getCollection(Sumo.getSumo().getName());
	}

	public HashMap<UUID, Integer> getTop3() {
		HashMap<UUID, Integer> hashMap = new HashMap<>();

		try(DBCursor cursor = this.collection.find().sort(new BasicDBObject(Statistic.WIN.toString(), -1)).limit(10)) {
			while(cursor.hasNext()) {
				DBObject dbObject = cursor.next();
				hashMap.put(UUID.fromString((String)dbObject.get("uuid")), (int)dbObject.get(Statistic.WIN.toString()));
			}
		}

		return hashMap;
	}

	public void insertUser(UUID uuid) {
		if (exists(uuid)) return;

		DBObject dbObject = new BasicDBObject("uuid", uuid.toString());
		for (Statistic value : Statistic.values()) {
			dbObject.put(value.toString(), 0);
		}
		this.collection.insert(dbObject);
	}

	private boolean exists(UUID uuid) {
		DBObject dbObject = new BasicDBObject("uuid", uuid.toString());
		return this.collection.findOne(dbObject) != null;
	}

	public int getStatistic(UUID uuid, Statistic statistic) {
		DBObject dbObject = new BasicDBObject("uuid", uuid.toString());
		DBObject found = this.collection.findOne(dbObject);

		if (found == null) {
			return 0;
		}

		return (int) found.get(statistic.toString());
	}

	public void updateStatistic(UUID uuid, Statistic statistic, int score) {
		DBObject dbObject = new BasicDBObject("uuid", uuid.toString());
		DBObject found = this.collection.findOne(dbObject);

		if (found == null) {
			return;
		}

		BasicDBObject set = new BasicDBObject("$set", dbObject);
		set.append("$set", new BasicDBObject(statistic.toString(), score));
		this.collection.update(found, set);
	}

	public void resetUser(UUID uuid) {
		DBObject dbObject = new BasicDBObject("uuid", uuid.toString());
		DBObject found = this.collection.findOne(dbObject);

		if (found == null) {
			return;
		}

		this.collection.remove(found);
		this.insertUser(uuid);
	}

	public static enum Statistic {

		WIN,LOSE,EVENT_WIN;

	}

}
