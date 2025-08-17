package com.example.dogtraininglog;

import static org.junit.Assert.*;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.dogtraininglog.database.DogTrainingDatabase;
import com.example.dogtraininglog.database.DogTrainingLogDAO;
import com.example.dogtraininglog.database.DogDAO;
import com.example.dogtraininglog.database.UserDAO;
import com.example.dogtraininglog.database.DogLog;
import com.example.dogtraininglog.database.entities.Dog;
import com.example.dogtraininglog.database.entities.User;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;

@RunWith(AndroidJUnit4.class)
public class DogTrainingLogDaoCrudTest {

    private DogTrainingDatabase db;
    private DogTrainingLogDAO logDao;
    private UserDAO userDao;
    private DogDAO dogDao;

    /*Before and after test*/
    @Before
    public void setUp() {
        Context ctx = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(ctx, DogTrainingDatabase.class)
                .allowMainThreadQueries()
                .build();
        logDao = db.dogTrainingLogDAO();
        userDao = db.userDAO();
        dogDao = db.dogDAO();
    }

    @After
    public void tearDown() {
        db.close();
    }


    /*Helpers*/
    private int ensureUserId(String username) {
        userDao.insert(new User(username, "pw"));
        User u = userDao.getByUsernameSync(username);
        assertNotNull(u);
        return u.getId();
    }

    private int newDogId(String name, String ownerLabel) {
        Dog d = new Dog();
        d.setName(name);
        d.setOwner(ownerLabel);
        long id = dogDao.insert(d);
        return (int) id;
    }

    private DogLog makeLog(int userId, int dogId, String activity, int count, boolean success) {
        /*constructor*/
        return new DogLog(activity, count, success, userId, dogId);
    }


    /*Test insert*/
    @Test
    public void insert_thenQueryByUser_returnsRow() {
        int uid = ensureUserId("u_logs_1");
        int did = newDogId("LogDog1", "ownerA");

        logDao.insert(makeLog(uid, did, "Sit", 3, true));

        List<DogLog> logs = logDao.getRecordableUserId(uid);
        assertNotNull(logs);
        assertTrue(logs.size() >= 1);

        DogLog newest = logs.get(0);
        assertEquals(uid, newest.getUserId());
        assertEquals(did, newest.getDogId());
        assertEquals("Sit", newest.getActivity());
        assertTrue(newest.isSuccessful());
    }

    /*test update*/
    @Test
    public void update_changesArePersisted() {
        int uid = ensureUserId("u_logs_2");
        int did = newDogId("LogDog2", "ownerB");

        logDao.insert(makeLog(uid, did, "Down", 2, false));

        List<DogLog> before = logDao.getRecordableUserId(uid);
        assertFalse(before.isEmpty());
        DogLog entry = before.get(0);

        /*Cahnge fields*/
        entry.setActivity("Heel");
        entry.setSuccessful(true);
        logDao.update(entry);

        List<DogLog> after = logDao.getRecordableUserId(uid);
        assertFalse(after.isEmpty());

        /*then we check and see if the change was made*/
        DogLog updated = null;
        Integer targetId = entry.getId();
        for (DogLog l : after) {
            if (targetId != null && targetId.equals(l.getId())) {
                updated = l;
                break;
            }
        }
        assertNotNull(updated);
        assertEquals("Heel", updated.getActivity());
        assertTrue(updated.isSuccessful());
    }

    /*Test delete*/
    @Test
    public void delete_removesRow() {
        int uid = ensureUserId("u_logs_3");
        int did = newDogId("LogDog3", "ownerC");

        logDao.insert(makeLog(uid, did, "Stay", 1, true));
        int before = logDao.getRecordableUserId(uid).size();

        /*Delete it*/
        DogLog toDelete = logDao.getRecordableUserId(uid).get(0);
        logDao.delete(toDelete);

        /*It is gone!*/
        int after = logDao.getRecordableUserId(uid).size();
        assertEquals(before - 1, after);
    }
}
