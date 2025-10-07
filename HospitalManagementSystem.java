package patient_appoinment;

import java.util.*;
import java.text.SimpleDateFormat;

public class HospitalManagementSystem {
    static class Patient {
        int patientId; String name; int age;
        Patient(int id, String n, int a) { patientId = id; name = n; age = a; }
        String getDetails() { return "ID: " + patientId + ", Name: " + name + ", Age: " + age; }
    }
    
    static class Doctor {
        int doctorId; String name; String specialization;
        Doctor(int id, String n, String s) { doctorId = id; name = n; specialization = s; }
        String getDetails() { return "ID: " + doctorId + ", Dr. " + name + ", " + specialization; }
    }
    
    static class Appointment {
        int appointmentId; String date; String time; 
        Patient patient; Doctor doctor; String status;
        Appointment(int id, String d, String t, Patient p, Doctor doc) {
            appointmentId = id; date = d; time = t; patient = p; doctor = doc; status = "SCHEDULED";
        }
        String getDetails() {
            return "Appointment ID: " + appointmentId + 
                   "\nDate: " + date + " Time: " + time +
                   "\nPatient: " + patient.name +
                   "\nDoctor: " + doctor.name +
                   "\nStatus: " + status;
        }
    }
    
    static class Consultation {
        int consultationId; String notes; Appointment appointment;
        Consultation(int id, String n, Appointment app) {
            consultationId = id; notes = n; appointment = app;
        }
        String getSummary() {
            return "Consultation ID: " + consultationId + 
                   "\nPatient: " + appointment.patient.name +
                   "\nDoctor: " + appointment.doctor.name +
                   "\nNotes: " + notes;
        }
    }
    
    static class Invoice {
        int invoiceId; double totalAmount; boolean paid; 
        Consultation consultation; String medicines; String dosage;
        Invoice(int id, Consultation cons, String med, String dos) {
            invoiceId = id; consultation = cons; medicines = med; dosage = dos; paid = false;
            calculateTotal();
        }
        void calculateTotal() {
            double consultationFee = 50.0;
            double medicineCost = medicines.split(",").length * 10.0;
            double tax = (consultationFee + medicineCost) * 0.1;
            totalAmount = consultationFee + medicineCost + tax;
        }
        String getDetails() {
            return "Invoice ID: " + invoiceId +
                   "\nPatient: " + consultation.appointment.patient.name +
                   "\nTotal: $" + String.format("%.2f", totalAmount) +
                   "\nStatus: " + (paid ? "PAID" : "UNPAID");
        }
    }
    
    static class Payment {
        int paymentId; double amountPaid; String paymentDate; Invoice invoice;
        Payment(int id, double amount, String date, Invoice inv) {
            paymentId = id; amountPaid = amount; paymentDate = date; invoice = inv;
        }
        boolean processPayment() {
            if (amountPaid >= invoice.totalAmount) {
                invoice.paid = true;
                return true;
            }
            return false;
        }
        String getDetails() {
            return "Payment ID: " + paymentId +
                   "\nAmount: $" + String.format("%.2f", amountPaid) +
                   "\nStatus: " + (invoice.paid ? "SUCCESS" : "FAILED");
        }
    }

    private static ArrayList<Patient> patients = new ArrayList<>();
    private static ArrayList<Doctor> doctors = new ArrayList<>();
    private static ArrayList<Appointment> appointments = new ArrayList<>();
    private static ArrayList<Consultation> consultations = new ArrayList<>();
    private static ArrayList<Invoice> invoices = new ArrayList<>();
    private static ArrayList<Payment> payments = new ArrayList<>();
    
    private static int patientId = 1, doctorId = 1, appointmentId = 1;
    private static int consultationId = 1, invoiceId = 1, paymentId = 1;
    
    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("=== HOSPITAL MANAGEMENT SYSTEM ===");
        addSampleData();
        
        while (true) {
            showMenu();
            int choice = getNumberInput("Choose option: ");
            
            switch (choice) {
                case 1: addPatient(); break;
                case 2: addDoctor(); break;
                case 3: scheduleAppointment(); break;
                case 4: completeConsultation(); break;
                case 5: createInvoice(); break;
                case 6: makePayment(); break;
                case 7: showAppointments(); break;
                case 8: showUnpaidInvoices(); break;
                case 9: 
                    System.out.println("Goodbye!");
                    return;
                default: 
                    System.out.println("Invalid choice!");
            }
        }
    }
    
    private static void showMenu() {
        System.out.println("\n1. Add Patient");
        System.out.println("2. Add Doctor");
        System.out.println("3. Schedule Appointment");
        System.out.println("4. Complete Consultation");
        System.out.println("5. Generate Invoice");
        System.out.println("6. Make Payment");
        System.out.println("7. View Appointments");
        System.out.println("8. Unpaid Invoices");
        System.out.println("9. Exit");
    }
    
    private static void addSampleData() {
        doctors.add(new Doctor(doctorId++, "Smith", "Cardiology"));
        doctors.add(new Doctor(doctorId++, "Johnson", "Pediatrics"));

        patients.add(new Patient(patientId++, "John Doe", 35));
        patients.add(new Patient(patientId++, "Jane Smith", 28));
        
        System.out.println("Sample data added!");
    }
    
    private static void addPatient() {
        System.out.print("Enter patient name: ");
        String name = scanner.nextLine();
        int age = getNumberInput("Enter age: ");
        
        Patient patient = new Patient(patientId++, name, age);
        patients.add(patient);
        System.out.println("Patient added! ID: " + patient.patientId);
    }
    
    private static void addDoctor() {
        System.out.print("Enter doctor name: ");
        String name = scanner.nextLine();
        System.out.print("Enter specialization: ");
        String spec = scanner.nextLine();
        
        Doctor doctor = new Doctor(doctorId++, name, spec);
        doctors.add(doctor);
        System.out.println("Doctor added! ID: " + doctor.doctorId);
    }
    
    private static void scheduleAppointment() {
        if (patients.isEmpty() || doctors.isEmpty()) {
            System.out.println("Need patients and doctors first!");
            return;
        }
        
        System.out.println("Available Patients:");
        for (Patient p : patients) {
            System.out.println(p.patientId + ". " + p.name);
        }
        int patientId = getNumberInput("Select patient: ");
        Patient patient = findPatient(patientId);
        
        System.out.println("Available Doctors:");
        for (Doctor d : doctors) {
            System.out.println(d.doctorId + ". " + d.name + " (" + d.specialization + ")");
        }
        int doctorId = getNumberInput("Select doctor: ");
        Doctor doctor = findDoctor(doctorId);
        
        if (patient == null || doctor == null) {
            System.out.println("Invalid selection!");
            return;
        }
        
        System.out.print("Enter date (YYYY-MM-DD): ");
        String date = scanner.nextLine();
        System.out.print("Enter time (HH:MM): ");
        String time = scanner.nextLine();
        
        Appointment appointment = new Appointment(appointmentId++, date, time, patient, doctor);
        appointments.add(appointment);
        System.out.println("Appointment scheduled! ID: " + appointment.appointmentId);
    }
    
    private static void completeConsultation() {
        System.out.println("Scheduled Appointments:");
        for (Appointment app : appointments) {
            if (app.status.equals("SCHEDULED")) {
                System.out.println(app.appointmentId + ". " + app.patient.name + " with " + app.doctor.name);
            }
        }
        
        int appId = getNumberInput("Enter appointment ID to complete: ");
        Appointment appointment = findAppointment(appId);
        
        if (appointment == null) {
            System.out.println("Appointment not found!");
            return;
        }
        
        appointment.status = "COMPLETED";
        System.out.print("Enter consultation notes: ");
        String notes = scanner.nextLine();
        
        Consultation consultation = new Consultation(consultationId++, notes, appointment);
        consultations.add(consultation);
        System.out.println("Consultation recorded! Summary:\n" + consultation.getSummary());
    }
    
    private static void createInvoice() {
        if (consultations.isEmpty()) {
            System.out.println("No consultations available!");
            return;
        }
        
        System.out.println("Recent Consultations:");
        for (Consultation cons : consultations) {
            System.out.println(cons.consultationId + ". " + cons.appointment.patient.name);
        }
        
        int consId = getNumberInput("Select consultation ID: ");
        Consultation consultation = findConsultation(consId);
        
        if (consultation == null) {
            System.out.println("Invalid consultation!");
            return;
        }
        
        System.out.print("Enter medicines (comma separated): ");
        String medicines = scanner.nextLine();
        System.out.print("Enter dosage: ");
        String dosage = scanner.nextLine();
        
        Invoice invoice = new Invoice(invoiceId++, consultation, medicines, dosage);
        invoices.add(invoice);
        System.out.println("Invoice created!\n" + invoice.getDetails());
    }
    
    private static void makePayment() {
        ArrayList<Invoice> unpaid = new ArrayList<>();
        for (Invoice inv : invoices) {
            if (!inv.paid) unpaid.add(inv);
        }
        
        if (unpaid.isEmpty()) {
            System.out.println("No unpaid invoices!");
            return;
        }
        
        System.out.println("Unpaid Invoices:");
        for (Invoice inv : unpaid) {
            System.out.println(inv.invoiceId + ". " + inv.consultation.appointment.patient.name + 
                             " - $" + String.format("%.2f", inv.totalAmount));
        }
        
        int invId = getNumberInput("Enter invoice ID: ");
        Invoice invoice = findInvoice(invId);
        
        if (invoice == null || invoice.paid) {
            System.out.println("Invalid invoice!");
            return;
        }
        
        double amount = getDoubleInput("Enter payment amount: $");
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        
        Payment payment = new Payment(paymentId++, amount, date, invoice);
        if (payment.processPayment()) {
            payments.add(payment);
            System.out.println("Payment successful!\n" + payment.getDetails());
        } else {
            System.out.println("Payment failed! Need: $" + String.format("%.2f", invoice.totalAmount));
        }
    }
    
    private static void showAppointments() {
        if (appointments.isEmpty()) {
            System.out.println("No appointments!");
            return;
        }
        
        for (Appointment app : appointments) {
            System.out.println("---");
            System.out.println(app.getDetails());
        }
    }
    
    private static void showUnpaidInvoices() {
        double totalDue = 0;
        for (Invoice inv : invoices) {
            if (!inv.paid) {
                System.out.println("---");
                System.out.println(inv.getDetails());
                totalDue += inv.totalAmount;
            }
        }
        
        if (totalDue > 0) {
            System.out.println("TOTAL DUE: $" + String.format("%.2f", totalDue));
        } else {
            System.out.println("All invoices are paid!");
        }
    }
    
    private static Patient findPatient(int id) {
        for (Patient p : patients) if (p.patientId == id) return p;
        return null;
    }
    
    private static Doctor findDoctor(int id) {
        for (Doctor d : doctors) if (d.doctorId == id) return d;
        return null;
    }
    
    private static Appointment findAppointment(int id) {
        for (Appointment a : appointments) if (a.appointmentId == id) return a;
        return null;
    }
    
    private static Consultation findConsultation(int id) {
        for (Consultation c : consultations) if (c.consultationId == id) return c;
        return null;
    }
    
    private static Invoice findInvoice(int id) {
        for (Invoice i : invoices) if (i.invoiceId == id) return i;
        return null;
    }
    
    private static int getNumberInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine());
            } catch (Exception e) {
                System.out.println("Please enter a valid number!");
            }
        }
    }
    
    private static double getDoubleInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Double.parseDouble(scanner.nextLine());
            } catch (Exception e) {
                System.out.println("Please enter a valid amount!");
            }
        }
    }
}


