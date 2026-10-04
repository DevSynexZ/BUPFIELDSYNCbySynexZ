package com.fieldsync.app;

import com.fieldsync.dao.UserDAO;
import com.fieldsync.model.User;
import com.fieldsync.util.UserSession;

public class RegisterAndSessionTest {
    public static void main(String[] args) {
        UserDAO userDAO = new UserDAO();

        System.out.println("=== STUDENT REGISTRATION & SESSION MANAGEMENT TEST ===");

        // Test credentials
        String testEmail = "mahi@cs.fieldsync.edu";
        String testPassword = "password123";
        String testName = "Mahi Shahriar";
        String testDept = "Computer Science";

        // 1. Check if user already exists
        if (userDAO.emailExists(testEmail)) {
            System.out.println("\n[INFO] Test user already registered. Proceeding to login test...");
        } else {
            System.out.println("\n[TEST 1] Registering New Student Representative...");
            User newUser = new User(0, testName, testEmail, testPassword, "STUDENT_REP", testDept);
            boolean registered = userDAO.registerStudentRep(newUser);

            if (registered) {
                System.out.println("-> Registration Successful for: " + testEmail);
            } else {
                System.err.println("-> Registration Failed!");
                return;
            }
        }

        // 2. Authenticate the newly registered user
        System.out.println("\n[TEST 2] Authenticating Registered Student...");
        User authUser = userDAO.authenticate(testEmail, testPassword);

        if (authUser != null) {
            System.out.println("-> Login Successful: " + authUser);

            // 3. Store user in UserSession
            UserSession.login(authUser);
            System.out.println("\n[TEST 3] UserSession Verification...");
            System.out.println("-> Is Logged In? " + UserSession.isLoggedIn());
            System.out.println("-> Active Session User ID: " + UserSession.getInstance().getCurrentUser().getId());
            System.out.println("-> Active Session User Name: " + UserSession.getInstance().getCurrentUser().getName());
            System.out.println("-> Is Student Rep? " + UserSession.getInstance().getCurrentUser().isStudentRep());

            // 4. Logout test
            System.out.println("\n[TEST 4] Testing Session Logout...");
            UserSession.logout();
            System.out.println("-> Is Logged In after logout? " + UserSession.isLoggedIn());

        } else {
            System.err.println("-> Authentication Failed!");
        }
    }
}