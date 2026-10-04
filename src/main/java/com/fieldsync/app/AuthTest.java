package com.fieldsync.app;

import com.fieldsync.dao.UserDAO;
import com.fieldsync.model.User;


public class AuthTest {
    public static void main(String[] args){
        UserDAO userDAO = new UserDAO();

        System.out.println("---ROLE-BASED AUTHENTICATION---");

        System.out.println("\n[TEST 1] Testing Field Registrar Login... ");
        User registrar = userDAO.authenticate("admin@fieldsync.edu","admin123");

        if(registrar!=null){
            System.out.println("-> Auth Success: "+ registrar);
            System.out.println("-> Is Registrar? "+ registrar.isRegistrar());
        }else{
            System.out.println("-> Auth Failed for Registrar.");
        }

        System.out.println("\n[TEST 2] Testing Student Representative Login...");
        User student = userDAO.authenticate("rep@cs.fieldsync.edu","rep123");

        if(student!=null){
            System.out.println("-> Auth Success : "+ student);
            System.out.println("-> Is Student Rep? "+ student.isStudentRep());
        }else {
            System.out.println("-> Auth Failed for Student Rep.");
        }

        System.out.println("\n[TEST 3] Testing Invalid Credentials...");
        User invalidid = userDAO.authenticate("wrong@bup.edu.bd","wrongpass");
        System.out.println("-> Invalid Auth Result: "+ (invalidid==null?"Passed(Returned Null":"Failed"));
    }
}
