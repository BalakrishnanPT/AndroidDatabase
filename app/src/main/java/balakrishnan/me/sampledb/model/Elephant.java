package balakrishnan.me.sampledb.model;

import io.realm.RealmObject;

public class Elephant extends RealmObject {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
