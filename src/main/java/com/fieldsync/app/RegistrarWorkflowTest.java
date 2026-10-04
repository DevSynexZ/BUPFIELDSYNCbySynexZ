package com.fieldsync.app;

import com.fieldsync.dao.NotificationDAO;
import com.fieldsync.dao.ReservationDAO;
import com.fieldsync.dao.UserDAO;
import com.fieldsync.model.Reservation;
import com.fieldsync.model.User;

import java.util.List;

public class RegistrarWorkflowTest {
    public static void main(String[] args) {
        UserDAO userDAO = new UserDAO();
        ReservationDAO reservationDAO = new ReservationDAO();
        NotificationDAO notificationDAO = new NotificationDAO();

        System.out.println("=== FULL WORKFLOW TEST: STUDENT REQUEST & REGISTRAR APPROVAL ===");

        // Fetch student and registrar accounts from DB
        User registrar = userDAO.authenticate("admin@fieldsync.edu", "admin123");
        User student = userDAO.authenticate("mahi@cs.fieldsync.edu", "password123");

        if (registrar == null || student == null) {
            System.err.println("Ensure both test users exist in the PostgreSQL database!");
            return;
        }

        // 1. Fetch Student's Pending Reservation (ID=1 from previous run)
        List<Reservation> pendingBookings = reservationDAO.getReservationsByUser(student.getId());
        if (pendingBookings.isEmpty()) {
            System.err.println("No reservations found for student.");
            return;
        }

        Reservation targetReservation = pendingBookings.get(0);
        System.out.println("\n[1] Found Pending Reservation ID: " + targetReservation.getId() + " | Status: " + targetReservation.getStatus());

        // 2. Registrar Approves the Reservation
        System.out.println("\n[2] Registrar approving reservation...");
        boolean approved = reservationDAO.updateStatus(targetReservation.getId(), "CONFIRMED", registrar.getId());
        System.out.println(" -> Approval Status Updated: " + approved);

        // 3. Send Notification to Student about Approval
        if (approved) {
            notificationDAO.createNotification(student.getId(), 
                "Your reservation for Field #" + targetReservation.getFieldId() + " has been APPROVED by the Registrar.");
        }

        // 4. Verify Student's Unread Notifications
        System.out.println("\n[3] Checking Student Notifications...");
        notificationDAO.getUnreadNotificationsByUser(student.getId())
                .forEach(n -> System.out.println(" -> " + n.getMessage()));
    }
}