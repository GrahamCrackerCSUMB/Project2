package com.example.dogtraininglog;

import static org.junit.Assert.*;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.dogtraininglog.database.DogTrainingDatabase;
import com.example.dogtraininglog.database.UserDAO;
import com.example.dogtraininglog.database.entities.User;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class UserDaoCrudTest {

    private DogTrainingDatabase db;
    private UserDAO userDao;

    /*Set up before and after*/
    @Before
    public void setUp() {
        Context ctx = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(ctx, DogTrainingDatabase.class)
                .allowMainThreadQueries()  // OK for tests
                .build();
        userDao = db.userDAO();
    }

    @After
    public void tearDown() {
        db.close();
    }

    /*Test insert*/
    @Test
    public void insert_insertsUser() {
        User u = new User("alice", "pw1");

        /*Insert new user*/
        userDao.insert(u);

        /*Check to see if that user is there*/
        User got = userDao.getByUsernameSync("alice");
        assertNotNull(got);
        assertEquals("alice", got.getUsername());
        assertEquals("pw1", got.getPassword());
    }

    /*Test update*/
    @Test
    public void update_updatesFields() {
        /*Insert a user*/
        userDao.insert(new User("bob", "pw1"));


        User existing = userDao.getByUsernameSync("bob");
        assertNotNull(existing);

        /*make changes*/
        existing.setPassword("pw2");
        existing.setAdmin(true);

        /*update*/
        userDao.update(existing);

        /*check the changes*/
        User after = userDao.getByUsernameSync("bob");
        assertNotNull(after);
        assertEquals("pw2", after.getPassword());
        assertTrue(after.isAdmin());
    }


    /*Test delete*/
    @Test
    public void delete_removesUser() {
        userDao.insert(new User("carol", "pw"));
        User existing = userDao.getByUsernameSync("carol");
        assertNotNull(existing);

        /*Delete somebody*/
        userDao.delete(existing);

        /*They are gone!*/
        User deleted = userDao.getByUsernameSync("carol");
        assertNull(deleted);
    }
}
