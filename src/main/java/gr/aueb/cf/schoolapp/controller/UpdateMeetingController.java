package gr.aueb.cf.schoolapp.controller;


import gr.aueb.cf.schoolapp.dao.MeetingDAOHibernateImpl;
import gr.aueb.cf.schoolapp.dao.StudentDAOHibernateImpl;
import gr.aueb.cf.schoolapp.dao.TeacherDAOHibernateImpl;
import gr.aueb.cf.schoolapp.dao.dbutil.HibernateHelper;
import gr.aueb.cf.schoolapp.dao.exceptions.MeetingDAOException;
import gr.aueb.cf.schoolapp.dao.exceptions.StudentDAOException;
import gr.aueb.cf.schoolapp.dao.exceptions.TeacherDAOException;
import gr.aueb.cf.schoolapp.dto.MeetingUpdateDTO;
import gr.aueb.cf.schoolapp.model.Meeting;
import gr.aueb.cf.schoolapp.model.Student;
import gr.aueb.cf.schoolapp.model.Teacher;
import gr.aueb.cf.schoolapp.service.MeetingServiceImpl;
import gr.aueb.cf.schoolapp.service.StudentServiceImpl;
import gr.aueb.cf.schoolapp.service.TeacherServiceImpl;

import gr.aueb.cf.schoolapp.service.exceptions.MeetingNotFoundException;
import gr.aueb.cf.schoolapp.service.exceptions.StudentNotFoundException;
import gr.aueb.cf.schoolapp.service.exceptions.TeacherNotFoundException;
import gr.aueb.cf.schoolapp.validator.MeetingValidator;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import java.util.Map;
import java.util.Optional;

@WebServlet("/schoolapp/updateMeeting")
public class UpdateMeetingController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private EntityManagerFactory emf = Persistence.createEntityManagerFactory("myPU");
    private EntityManager entityManager = emf.createEntityManager();

    private MeetingDAOHibernateImpl meetingDAO = new MeetingDAOHibernateImpl(entityManager);
    private MeetingServiceImpl meetingService = new MeetingServiceImpl(meetingDAO);

    private StudentDAOHibernateImpl studentDAO = new StudentDAOHibernateImpl(entityManager);
    private StudentServiceImpl studentService = new StudentServiceImpl(studentDAO);

    private TeacherDAOHibernateImpl teacherDAO = new TeacherDAOHibernateImpl(entityManager);
    private TeacherServiceImpl teacherService = new TeacherServiceImpl(teacherDAO);

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/school/static/templates/meetingUpdate.jsp")
                .forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Integer id = Integer.parseInt(request.getParameter("id"));
        String room = request.getParameter("room");
        String meetingDateStr = request.getParameter("meetingDate");

        // Validate and convert meeting date to java.sql.Date format
        java.sql.Date meetingDate = null;
        if (meetingDateStr != null && !meetingDateStr.isEmpty()) {
            try {
                meetingDate = java.sql.Date.valueOf(meetingDateStr);
            } catch (IllegalArgumentException e) {
                // Handle invalid date format
                request.setAttribute("error", "Invalid date format. Use 'YYYY-MM-DD'.");
                request.getRequestDispatcher("/school/static/templates/meetingUpdate.jsp")
                        .forward(request, response);
                return;
            }
        }

        Teacher teacher;
        try {
            teacher = getTeacherFromRequestWithEagerLoading(request);
        } catch (TeacherNotFoundException e) {
            // Handle teacher not found
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/school/static/templates/meetingUpdate.jsp")
                    .forward(request, response);
            return;
        }

        Student student;
        try {
            student = getStudentFromRequestWithEagerLoading(request);
        } catch (StudentNotFoundException e) {
            // Handle student not found
            request.setAttribute("error", e.getMessage());
            request.getRequestDispatcher("/school/static/templates/meetingUpdate.jsp")
                    .forward(request, response);
            return;
        }

        MeetingUpdateDTO newMeetingDTO = new MeetingUpdateDTO();
        newMeetingDTO.setId(id);
        newMeetingDTO.setTeacher(teacher);
        newMeetingDTO.setStudent(student);
        newMeetingDTO.setRoom(room);
        newMeetingDTO.setMeetingDate(meetingDate);
        request.setAttribute("updatedMeeting", newMeetingDTO);

        try {
            Map<String, String> errors = MeetingValidator.validate(newMeetingDTO);

            if (!errors.isEmpty()) {
                String errorMessage = errors.entrySet().stream()
                        .map(entry -> entry.getKey() + ": " + entry.getValue())
                        .reduce((msg1, msg2) -> msg1 + " " + msg2)
                        .orElse("");
                request.setAttribute("error", errorMessage);
                request.getRequestDispatcher("/school/static/templates/meetingUpdate.jsp")
                        .forward(request, response);
                return;
            }

            Meeting meeting = meetingService.updateMeeting(newMeetingDTO);
            // Clear the entity manager
            entityManager.clear();

            request.setAttribute("message", "");
            request.setAttribute("meetingUpdated", meeting);
            request.getRequestDispatcher("/school/static/templates/meetingUpdated.jsp")
                    .forward(request, response);
        } catch (MeetingNotFoundException | MeetingDAOException e) {
            // Handle exceptions
            String message = e.getMessage();
            request.setAttribute("message", message);
            request.getRequestDispatcher("/school/static/templates/meetingUpdated.jsp")
                    .forward(request, response);
        }
    }

    private Student getStudentFromRequestWithEagerLoading(HttpServletRequest request) throws StudentNotFoundException {
        String studentId = request.getParameter("studentId");
        if (studentId != null && !studentId.trim().isEmpty()) {
            try {
                Optional<Student> studentOptional = Optional.ofNullable(studentService.getStudentById(Integer.parseInt(studentId)));
                if (studentOptional.isPresent()) {
                    Student student = studentOptional.get();
                    student.getCity(); // Eager loading of specialty
                    student.getMeetings().size(); // Eager loading of meetings
                    return student;
                }
            } catch (NumberFormatException e) {
                throw new StudentNotFoundException("Invalid student ID format.");
            } catch (StudentDAOException e) {
                throw new StudentNotFoundException("Student not found for provided ID.");
            }
        }
        throw new StudentNotFoundException("Student ID not provided or is empty.");
    }

    private Teacher getTeacherFromRequestWithEagerLoading(HttpServletRequest request) throws TeacherNotFoundException {
        String teacherId = request.getParameter("teacherId");
        if (teacherId != null && !teacherId.trim().isEmpty()) {
            try {
                Optional<Teacher> teacherOptional = Optional.ofNullable(teacherService.getTeacherById(Integer.parseInt(teacherId)));
                if (teacherOptional.isPresent()) {
                    Teacher teacher = teacherOptional.get();
                    teacher.getSpecialty(); // Eager loading of specialty
                    teacher.getMeetings().size(); // Eager loading of meetings
                    return teacher;
                }
            } catch (NumberFormatException e) {
                throw new TeacherNotFoundException("Invalid teacher ID format.");
            } catch (TeacherDAOException e) {
                throw new TeacherNotFoundException("Teacher not found for provided ID.");
            }
        }
        throw new TeacherNotFoundException("Teacher ID not provided or is empty.");
    }

    @Override
    public void destroy() {
        HibernateHelper.closeEntityManager();
        HibernateHelper.closeEMF();
    }




}
