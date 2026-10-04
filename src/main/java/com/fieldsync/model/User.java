// package com.fieldsync.model;

// public class User {

//     private int id;
//     private String name;
//     private String email;
//     private String password;
//     private String role; // Student or Feild_Register
//     private String department;

//     public User() {
//     }

//     public User(int id, String name, String email, String password, String role, String department) {
//         this.id = id;
//         this.name = name;
//         this.email = email;
//         this.password = password;
//         this.role = role;
//         this.department = department;
//     }

//     public int getId() {
//         return id;
//     }

//     public void setId(int id) {
//         this.id = id;
//     }

//     public String getName() {
//         return name;
//     }

//     public void setName(String name) {
//         this.name = name;
//     }

//     public String getEmail() {
//         return email;
//     }

//     public void setEmail(String email) {
//         this.email = email;
//     }

//     public String getPassword() {
//         return password;
//     }

//     public void setPassword(String password) {
//         this.password = password;
//     }

//     public String getRole() {
//         return role;
//     }

//     public void setRole(String role) {
//         this.role = role;
//     }

//     public String getDepartment() {
//         return department;
//     }

//     public void setDepartment(String department) {
//         this.department = department;
//     }

//     // Role-based helper methods
//     // public boolean isRegistrar(){
//     // return "FIELD_REGISTRAR".equalsIgnoreCase(this.role);
//     // }

//     // public boolean isStudentRep(){
//     // return "STUDENT_REP".equalsIgnoreCase(this.role);
//     // }

//     public boolean isRegistrar() {
//         if (this.role == null)
//             return false;
//         String normalized = this.role.toUpperCase().trim();
//         // Checks for FIELD_REGISTRAR, REGISTRAR, etc.
//         return normalized.contains("REGISTRAR");
//     }

//     public boolean isStudentRep() {
//         if (this.role == null)
//             return false;
//         String normalized = this.role.toUpperCase().trim();
//         return normalized.contains("STUDENT") || normalized.contains("REP");
//     }

//     @Override
//     public String toString() {
//         return String.format("User[ID=%d, Name='%s', Role='%s', Dept='%s']", id, name, role, department);
//     }
// }



// package com.fieldsync.model;

// public class User {

//     private int id;
//     private String name;
//     private String email;
//     private String password;
//     private String role; // Student or Field_Registrar
//     private String department;

//     public User() {
//     }

//     public User(int id, String name, String email, String password, String role, String department) {
//         this.id = id;
//         this.name = name;
//         this.email = email;
//         this.password = password;
//         this.role = role;
//         this.department = department;
//     }

//     public int getId() {
//         return id;
//     }

//     /**
//      * Alias getter to support getUserId() calls across controllers
//      */
//     public int getUserId() {
//         return id;
//     }

//     public void setId(int id) {
//         this.id = id;
//     }

//     public String getName() {
//         return name;
//     }

//     public void setName(String name) {
//         this.name = name;
//     }

//     public String getEmail() {
//         return email;
//     }

//     public void setEmail(String email) {
//         this.email = email;
//     }

//     public String getPassword() {
//         return password;
//     }

//     public void setPassword(String password) {
//         this.password = password;
//     }

//     public String getRole() {
//         return role;
//     }

//     public void setRole(String role) {
//         this.role = role;
//     }

//     public String getDepartment() {
//         return department;
//     }

//     public void setDepartment(String department) {
//         this.department = department;
//     }

//     public boolean isRegistrar() {
//         if (this.role == null)
//             return false;
//         String normalized = this.role.toUpperCase().trim();
//         return normalized.contains("REGISTRAR");
//     }

//     public boolean isStudentRep() {
//         if (this.role == null)
//             return false;
//         String normalized = this.role.toUpperCase().trim();
//         return normalized.contains("STUDENT") || normalized.contains("REP");
//     }

//     @Override
//     public String toString() {
//         return String.format("User[ID=%d, Name='%s', Role='%s', Dept='%s']", id, name, role, department);
//     }
// }




package com.fieldsync.model;

public class User {

    private int id;
    private String name;
    private String email;
    private String password;
    private String role;
    private String department;

    public User() {
    }

    public User(int id, String name, String email, String password, String role, String department) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.department = department;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public boolean isRegistrar() {
        if (this.role == null) return false;
        String normalized = this.role.toUpperCase().trim();
        return normalized.contains("REGISTRAR");
    }

    public boolean isStudentRep() {
        if (this.role == null) return false;
        String normalized = this.role.toUpperCase().trim();
        return normalized.contains("STUDENT") || normalized.contains("REP");
    }

    @Override
    public String toString() {
        return String.format("User[ID=%d, Name='%s', Role='%s', Dept='%s']", id, name, role, department);
    }
}