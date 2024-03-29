package gr.aueb.cf.schoolapp.service;

import gr.aueb.cf.schoolapp.dao.SpecialtyDAOHibernateImpl;
import gr.aueb.cf.schoolapp.dao.exceptions.SpecialtyDAOException;
import gr.aueb.cf.schoolapp.dto.SpecialtyInsertDTO;
import gr.aueb.cf.schoolapp.dto.SpecialtyUpdateDTO;
import gr.aueb.cf.schoolapp.model.Specialty;

import gr.aueb.cf.schoolapp.model.Teacher;
import gr.aueb.cf.schoolapp.service.exceptions.SpecialtyNotFoundException;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

public class SpecialtyServiceImpl implements ISpecialtyService {

    private SpecialtyDAOHibernateImpl specialtyDAO;

    public SpecialtyServiceImpl(SpecialtyDAOHibernateImpl specialtyDAO) {
        this.specialtyDAO = specialtyDAO;
    }


    @Override
    public Specialty insertSpecialty(SpecialtyInsertDTO dto) throws SpecialtyDAOException {
        if (dto == null) return null;
        Specialty specialty;
        try {
            specialty = map(dto);

            // Use convenience method to associate teachers with the specialty
            for (Teacher teacher : specialty.getTeachers()) {
                teacher.addSpecialty(specialty);
            }

            return specialtyDAO.insert(specialty);
        } catch (SpecialtyDAOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    @Transactional
    public Specialty updateSpecialty(SpecialtyUpdateDTO dto) throws SpecialtyDAOException, SpecialtyNotFoundException {
        if (dto == null) return null;
        Specialty specialty;
        try {
            specialty = map(dto);

            if (specialtyDAO.getById(specialty.getId()) == null) {
                throw new SpecialtyNotFoundException(specialty);
            }

            // Use convenience method to associate teachers with the specialty
            for (Teacher teacher : specialty.getTeachers()) {
                teacher.addSpecialty(specialty);
            }

            return specialtyDAO.update(specialty);
        } catch (SpecialtyDAOException | SpecialtyNotFoundException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void deleteSpecialty(int id) throws SpecialtyDAOException, SpecialtyNotFoundException {
        Specialty specialty;
        try {
            specialty = specialtyDAO.getById(id);

            if (specialty == null) {
                throw new SpecialtyNotFoundException("Specialty with id: " + id + " was not found");
            }

            for (Teacher teacher : specialty.getTeachers()) {
                teacher.removeSpecialty();
            }

            specialtyDAO.delete(id);
        } catch (SpecialtyDAOException | SpecialtyNotFoundException e) {
            e.printStackTrace();
            throw e;
        }
    }


    @Override
    public List<Specialty> getSpecialtiesBySpecialtyName(String name) throws SpecialtyDAOException, SpecialtyNotFoundException {
        if (name == null) {
            throw new IllegalArgumentException("Specialty name cannot be null");
        }
        try {
            Optional<List<Specialty>> specialtiesOptional = specialtyDAO.getSpecialtyByName(name);

            if (!specialtiesOptional.isPresent()) {
                throw new SpecialtyNotFoundException("No specialties found with name: " + name);
            }

            return specialtiesOptional.get();

        } catch (SpecialtyDAOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public Specialty getSpecialtyById(int id) throws SpecialtyDAOException, SpecialtyNotFoundException {
        Specialty specialty;
        try {
            specialty = specialtyDAO.getById(id);

            if (specialty == null) {
                throw new SpecialtyNotFoundException("Search Error: Specialty with id: " + id + " was not found");
            }
            return specialty;
        } catch (SpecialtyDAOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public List<Specialty> getAllSpecialties() {
        List<Specialty> specialties = null;
        try {
            specialties = specialtyDAO.getAllSpecialties();
        } catch (SpecialtyDAOException e) {
            e.printStackTrace();
            // handle exception
        }
        return specialties;
    }

    private Specialty map(SpecialtyInsertDTO dto) {
        return new Specialty(null, dto.getName());
    }

    private Specialty map(SpecialtyUpdateDTO dto) {
        return new Specialty(dto.getId(), dto.getName());
    }
}
