package com.fieldsync.app;

import com.fieldsync.dao.FieldDAO;
import com.fieldsync.dao.ReservationDAO;
import com.fieldsync.dao.UserDAO;
import com.fieldsync.model.Field;
import com.fieldsync.model.Reservation;
import com.fieldsync.model.User;
import com.fieldsync.util.UserSession;

import java.time.LocalDateTime;
import java.util.List;

public class ReservationTest {
    public static void main(String[] args) {
        UserDAO userDAO = new UserDAO();
        FieldDAO fieldDAO = new FieldDAO();
        ReservationDAO reservationDAO = new ReservationDAO();

        System.out.println("=== PHASE 2: FIELD & RESERVATION SYSTEM VALIDATION ===");

        // 1. Authenticate & initialize UserSession (Integration with Phase 1)
        User student = userDAO.authenticate("mahi@cs.fieldsync.edu", "password123");
        if (student == null) {
            System.err.println("Authentication failed. Ensure mahi@cs.fieldsync.edu exists in database!");
            return;
        }
        UserSession.login(student);
        System.out.println("\n[VALIDATION 1] UserSession active for: " + UserSession.getInstance().getCurrentUser().getName());

        // 2. Retrieve active fields from PostgreSQL
        System.out.println("\n[VALIDATION 2] Fetching Active Campus Fields...");
        List<Field> fields = fieldDAO.getAllActiveFields();
        fields.forEach(f -> System.out.println(" -> " + f));

        if (fields.isEmpty()) {
            System.err.println("No active fields found in database. Please seed table 'fields' with test data.");
            return;
        }

        int selectedFieldId = fields.get(0).getFieldId();
        int activeUserId = UserSession.getInstance().getCurrentUser().getId();

        // 3. Define time slot (Tomorrow 10:00 AM - 12:00 PM)
        LocalDateTime startSlot = LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endSlot = startSlot.plusHours(2);

        System.out.println("\n[VALIDATION 3] Submitting Booking Request...");
        Reservation newBooking = new Reservation(0, selectedFieldId, activeUserId, null, startSlot, endSlot, "PENDING_APPROVAL", "CSE Department Football Match", null);
        boolean created = reservationDAO.createReservation(newBooking);
        System.out.println(" -> Reservation Created: " + created);

        // 4. Test Overlap Prevention (Attempting to book conflicting slot 10:30 AM - 11:30 AM)
        System.out.println("\n[VALIDATION 4] Testing Time-Slot Overlap Prevention...");
        boolean overlapDetected = reservationDAO.hasOverlap(selectedFieldId, startSlot.plusMinutes(30), endSlot.minusMinutes(30));
        System.out.println(" -> Overlap Detected Correctly? " + overlapDetected);

        // 5. Fetch all bookings for logged-in user
        System.out.println("\n[VALIDATION 5] Fetching User Booking History...");
        List<Reservation> userBookings = reservationDAO.getReservationsByUser(activeUserId);
        userBookings.forEach(b -> System.out.println(" -> " + b));
    }
}