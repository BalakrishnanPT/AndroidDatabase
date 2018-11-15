package balakrishnan.me.sampledb.Module;

import balakrishnan.me.sampledb.model.Cat;
import balakrishnan.me.sampledb.model.Dog;
import io.realm.annotations.RealmModule;

@RealmModule(classes = {Cat.class, Dog.class})
public class DomesticAnimalsModule {
}
