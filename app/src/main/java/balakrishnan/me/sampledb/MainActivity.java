package balakrishnan.me.sampledb;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import balakrishnan.me.sampledb.Module.DomesticAnimalsModule;
import balakrishnan.me.sampledb.model.Cat;
import balakrishnan.me.sampledb.model.Dog;
import io.realm.OrderedCollectionChangeSet;
import io.realm.OrderedRealmCollectionChangeListener;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Realm realm;

    public static int getPrimaryKey(Class c) {
        Realm realm = Realm.getDefaultInstance();

        String primaryKeyFied = realm.getSchema().get(c.getSimpleName()).getPrimaryKey();
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
        RealmConfiguration config = new RealmConfiguration.Builder()
                .name(getPackageName() + ".realm")
                .schemaVersion(2)
                .modules(new DomesticAnimalsModule())
                .build();

        realm = Realm.getInstance(config);

        final RealmResults<Dog> puppies = realm.where(Dog.class).findAll();

        Log.d(TAG, "realmSetup: number of dogs" + puppies.size());

        puppies.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<Dog>>() {
            @Override
            public void onChange(RealmResults<Dog> results, OrderedCollectionChangeSet changeSet) {
                // Query results are updated in real time with fine grained notifications.
                Log.d(TAG, "onChange: Puppies" + changeSet.getInsertions());// => [0] is added.
            }
        });

        final RealmResults<Cat> kitten = realm.where(Cat.class).findAll();
        Log.d(TAG, "realmSetup: number of kitten" + kitten.size()); // => 0 because no dogs have been added to the Realm yet// Listeners will be notified when data changes

        kitten.addChangeListener(new OrderedRealmCollectionChangeListener<RealmResults<Cat>>() {
            @Override
            public void onChange(RealmResults<Cat> results, OrderedCollectionChangeSet changeSet) {
                // Query results are updated in real time with fine grained notifications.
                for (Cat cat : results) {
                    Log.d(TAG, "onChange: Puppies" + cat.getName());
                }
            }
        });
        addAnimals(4);
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Dog dog = realm.createObject(Dog.class);
                dog.setName("Fido");
                dog.setAge(5);
            }
        });
    }

    public void addAnimals(final int count) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                for (int i = 0; i < count; i++) {
                    Cat cat = realm.createObject(Cat.class);
                    cat.setName("Cat " + i);
                }
            }
        });
    }
}
