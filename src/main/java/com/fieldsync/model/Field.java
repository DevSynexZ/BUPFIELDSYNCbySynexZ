// package com.fieldsync.model;

// public class Field {

//     private int id;
//     private String name;
//     private String location;
//     private boolean active;

//     public Field() {}

//     public Field(int id, String name, String location, boolean active) {
//         this.id = id;
//         this.name = name;
//         this.location = location;
//         this.active = active;
//     }

//     public int getId() { return id; }
//     public void setId(int id) { this.id = id; }

//     public String getName() { return name; }
//     public void setName(String name) { this.name = name; }

//     public String getLocation() { return location; }
//     public void setLocation(String location) { this.location = location; }

//     public boolean isActive() { return active; }
//     public void setActive(boolean active) { this.active = active; }

//     @Override
//     public String toString() {
//         // return String.format("Field[ID=%d, Name='%s', Location='%s', Active=%b]", id, name, location, active);
//         return getFieldName();
//     }
// }


package com.fieldsync.model;

public class Field {
    private int fieldId;
    private String fieldName;
    private String location;
    private boolean isActive;

    public Field(int fieldId, String fieldName, String location, boolean isActive) {
        this.fieldId = fieldId;
        this.fieldName = fieldName;
        this.location = location;
        this.isActive = isActive;
    }

    public int getFieldId() {
        return fieldId;
    }

    


    public String getFieldName() {
        return fieldName;
    }

    public String getLocation() {
        return location;
    }

    public boolean isActive() {
        return isActive;
    }

    @Override
    public String toString() {
        return fieldName;
    }
}