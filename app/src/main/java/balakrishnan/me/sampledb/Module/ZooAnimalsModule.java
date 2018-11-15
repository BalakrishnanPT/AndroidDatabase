package balakrishnan.me.sampledb.Module;

import balakrishnan.me.sampledb.model.Elephant;
import balakrishnan.me.sampledb.model.Lion;
import balakrishnan.me.sampledb.model.Zebra;
import io.realm.annotations.RealmModule;

@RealmModule(classes = {Elephant.class, Lion.class, Zebra.class})
public class ZooAnimalsModule {
}
