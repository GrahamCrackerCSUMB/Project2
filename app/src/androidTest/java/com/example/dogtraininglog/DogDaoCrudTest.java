package com.example.dogtraininglog;

import static org.junit.Assert.*;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.dogtraininglog.database.DogTrainingDatabase;
import com.example.dogtraininglog.database.DogDAO;
import com.example.dogtraininglog.database.entities.Dog;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class DogDaoCrudTest {

    private DogTrainingDatabase db;
    private DogDAO dogDao;

    @Before
    public void setUp() {
        Context ctx = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(ctx, DogTrainingDatabase.class)
                .allowMainThreadQueries()
                .build();
        dogDao = db.dogDAO();
    }

    @After
    public void tearDown() {
        db.close();
    }

    private Dog makeDog(String name, String owner) {
        Dog d = new Dog();
        d.setName(name);
        d.setOwner(owner);
        return d;
    }

    /*Test insert*/
    @Test
    public void insert_thenReadBack_withFindOneByNameOwner() {
        int beforeCount = dogDao.countDogs();

        /*Insert new record*/
        dogDao.insert(makeDog("Fido", "ownerA"));

        /*See new record*/
        Dog got = dogDao.findOneByNameOwner("Fido", "ownerA");
        assertNotNull(got);
        assertEquals("Fido", got.getName());
        assertEquals("ownerA", got.getOwner());
        assertEquals(beforeCount + 1, dogDao.countDogs());
    }

    /*Test update*/
    @Test
    public void update_persistsChanges_withoutFindById() {
        dogDao.insert(makeDog("Rex", "ownerB"));

        Dog rex = dogDao.findOneByNameOwner("Rex", "ownerB");
        assertNotNull(rex);

        /*Make changes*/
        rex.setName("Rexy");
        rex.setOwner("ownerC");
        dogDao.update(rex);

        /*Old record is gone*/
        assertNull(dogDao.findOneByNameOwner("Rex", "ownerB"));

        /*New value should be present*/
        Dog after = dogDao.findOneByNameOwner("Rexy", "ownerC");
        assertNotNull(after);
        assertEquals("Rexy", after.getName());
        assertEquals("ownerC", after.getOwner());
    }

    /*Test delete*/
    @Test
    public void delete_removesRow_withoutFindById() {
        dogDao.insert(makeDog("Spot", "ownerD"));
        int before = dogDao.countDogs();

        Dog spot = dogDao.findOneByNameOwner("Spot", "ownerD");
        assertNotNull(spot);

        dogDao.delete(spot);

        assertEquals(before - 1, dogDao.countDogs());
        assertNull(dogDao.findOneByNameOwner("Spot", "ownerD"));
    }
}
