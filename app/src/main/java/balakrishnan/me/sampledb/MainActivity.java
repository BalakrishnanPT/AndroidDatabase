package balakrishnan.me.sampledb;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import java.util.Objects;

import balakrishnan.me.sampledb.Module.DomesticAnimalsModule;
import balakrishnan.me.sampledb.model.Cat;
import balakrishnan.me.sampledb.model.Dog;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Realm realm;

    /**
     * There is no auto increment annotation in Realm hence we have to do it manually
     *
     * @param c Class get primary key
     * @return unique primary value
     */
    public static int getPrimaryKey(Class c) {
        Realm realm = Realm.getDefaultInstance();

        String primaryKeyFied = Objects.requireNonNull(realm.getSchema().get(c.getSimpleName())).getPrimaryKey();
        if (realm.where(c).max(primaryKeyFied) == null)
            return 1;
        int value = realm.where(c).max(primaryKeyFied).intValue();
        return value + 1;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        realmSetup();
    }

    private void realmSetup() {

        //DB Configuration we can use default configuration or custom configurations
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name(getPackageName() + ".realm")
                .schemaVersion(1)
                .modules(new DomesticAnimalsModule())
                .build();

        realm = Realm.getInstance(config);

        //Query
        final RealmResults<Dog> puppies = realm.where(Dog.class).findAll();
        Log.d(TAG, "realmSetup: number of dogs" + puppies.size());

        puppies.addChangeListener((results, changeSet) -> {
            // Query results are updated in real time with fine grained notifications.
            Log.d(TAG, "onChange: Puppies" + changeSet.getInsertions());
        });

        final RealmResults<Cat> kitten = realm.where(Cat.class).findAll();
        Log.d(TAG, "realmSetup: number of kitten" + kitten.size()); // => 0 because no dogs have been added to the Realm yet// Listeners will be notified when data changes

        kitten.addChangeListener((results, changeSet) -> {
            // Query results are updated in real time with fine grained notifications.
            for (Cat cat : results) {
                Log.d(TAG, "onChange: Puppies" + cat.getName());
            }
        });

        addCat(5);
        deleteRecord(1);

        //CreateObject example
        realm.executeTransaction(realm -> {
            Dog dog = realm.createObject(Dog.class);
            dog.setName("Fido");
            dog.setAge(5);
        });
        updateRecord(2, "ChangedName");
    }

    /**
     * This function explains copyToRealm
     *
     * @param count
     */
    public void addCat(final int count) {
        // Copy the object to Realm. Any further changes must happen on realmUser
        realm.beginTransaction();
        for (int i = 0; i < count; i++) {
            Cat temp = new Cat();
            temp.setName("Cat " + i);
            temp.setAge(i);
            realm.copyToRealm(temp);
        }
        realm.commitTransaction();
    }

    public void deleteRecord(int t) {

/*
        final RealmResults<Cat> students = realm.where(Cat.class).findAll();

        Cat kitten = students.where().equalTo("age", t).findFirst();

        if (kitten != null) {

            if (!realm.isInTransaction()) {
                realm.beginTransaction();
            }

            kitten.deleteFromRealm();

            realm.commitTransaction();
        }
*/

        realm.executeTransactionAsync(localRealm -> localRealm
                        .where(Cat.class)
                        .equalTo("age", t)
                        .findAll().deleteAllFromRealm(),
                () -> realm.close(), error -> realm.close());

    }

    public void updateRecord(int t, String name) {
//        Async update
        realm.executeTransactionAsync(localRealm -> localRealm
                        .where(Cat.class)
                        .equalTo("age", t)
                        .findAll().setValue("name", name),
                () -> realm.close(), error -> realm.close());
//        Sync update
        realm.executeTransaction(localRealm -> localRealm
                .where(Cat.class)
                .equalTo("age", t + 1)
                .findAll().setValue("name", name + "sync"));
        realm.close();
    }

}
