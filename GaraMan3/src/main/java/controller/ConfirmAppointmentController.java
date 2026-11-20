package controller;

import dao.AppointmentDAO3;
import dao.CarDAO3;
import dao.CustomerDAO3;
import model.Appointment3;
import model.AppointmentServiceDetail3;
import model.Car3;
import model.Customer3;
import model.SalesStaff3;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class ConfirmAppointmentController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession();

        Customer3 customer = (Customer3) session.getAttribute("customer");
        Car3 car = (Car3) session.getAttribute("car");
        Appointment3 appointment = (Appointment3) session.getAttribute("appointment");
        SalesStaff3 loggedStaff = (SalesStaff3) session.getAttribute("salesStaff");

        if (request.getParameter("subBack") != null) {
            response.sendRedirect("book-appointment");
            return;
        }

        boolean bookedOk = false;
        String bookingError = null;

        if ("POST".equalsIgnoreCase(request.getMethod())
                && request.getParameter("subConfirm") != null
                && customer != null && car != null && appointment != null) {
            CustomerDAO3 customerDAO = new CustomerDAO3();
            CarDAO3 carDAO = new CarDAO3();
            AppointmentDAO3 appointmentDAO = new AppointmentDAO3();

            if (appointmentDAO.getSlotCount(appointment.getAppointmentDate(), appointment.getAppointmentTime())
                    >= AppointmentDAO3.MAX_BOOKINGS_PER_SLOT) {
                bookingError = "Khung giờ đã hết chỗ. Vui lòng quay lại và chọn giờ khác.";
            } else if (customerDAO.saveCustomer(customer)) {
                car.setCustomerId(customer.getId());
                if (carDAO.saveCar(car)) {
                    appointment.setCustomer(customer);
                    appointment.setCar(car);
                    bookedOk = appointmentDAO.saveAppointment(appointment);
                }
            }

            if (bookedOk) {
                HomeController.clearBookingSession(session);
            } else if (bookingError == null) {
                bookingError = appointmentDAO.getSlotCount(appointment.getAppointmentDate(), appointment.getAppointmentTime())
                        >= AppointmentDAO3.MAX_BOOKINGS_PER_SLOT
                        ? "Khung giờ đã hết chỗ. Vui lòng quay lại và chọn giờ khác."
                        : "Không lưu được lịch hẹn. Vui lòng kiểm tra lại thông tin và thử lại.";
            }
        }

        if (!bookedOk && (customer == null || car == null || appointment == null)) {
            response.sendRedirect("book-appointment");
            return;
        }

        AppointmentServiceDetail3[] details = (appointment != null && appointment.getAppointmentServices() != null)
                ? appointment.getAppointmentServices() : new AppointmentServiceDetail3[0];

        request.setAttribute("customer", customer);
        request.setAttribute("car", car);
        request.setAttribute("appointment", appointment);
        request.setAttribute("details", details);
        request.setAttribute("loggedStaff", loggedStaff);
        request.setAttribute("bookedOk", Boolean.valueOf(bookedOk));
        request.setAttribute("bookingError", bookingError);
        request.setAttribute("fromController", Boolean.TRUE);
        request.getRequestDispatcher("/ConfirmScreen3.jsp").forward(request, response);
    }
}
