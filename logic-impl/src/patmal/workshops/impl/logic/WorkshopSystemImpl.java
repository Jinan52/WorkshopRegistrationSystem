
package patmal.workshops.impl.logic;

import patmal.workshops.api.logic.WorkshopSystem;
import patmal.workshops.api.model.Instructor;
import patmal.workshops.api.model.Student;
import patmal.workshops.api.model.Team;
import patmal.workshops.api.model.Workshop;

import patmal.workshops.impl.model.InstructorImpl;
import patmal.workshops.impl.model.StudentImpl;
import patmal.workshops.impl.model.TeamImpl;
import patmal.workshops.impl.model.WorkshopImpl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class WorkshopSystemImpl implements WorkshopSystem {

    private final Map<String, Instructor> instructors = new LinkedHashMap<>();
    private final Map<String, Student> students = new LinkedHashMap<>();
    private final Map<Integer, Workshop> workshops = new LinkedHashMap<>();
    private final Map<Integer, Team> teams = new LinkedHashMap<>();

    private int nextWorkshopId = 1;
    private int nextTeamId = 1;

    public WorkshopSystemImpl() {
    }

    @Override
    public Instructor createInstructor(String id, String name, String email) {
        if (instructors.containsKey(id)) {
            throw new IllegalArgumentException("Instructor ID already exists: " + id);
        }

        Instructor instructor = new InstructorImpl(id, name, email);
        instructors.put(id, instructor);
        return instructor;
    }

    @Override
    public Workshop createWorkshop(String name, String description) {
        Workshop workshop = new WorkshopImpl(nextWorkshopId, name, description);
        workshops.put(nextWorkshopId, workshop);
        nextWorkshopId++;
        return workshop;
    }

    @Override
    public void associateWorkshopWithInstructor(Instructor instructor,
                                                Workshop workshop) {
        if (instructor == null || workshop == null ||
                instructors.get(instructor.getId()) != instructor ||
                workshops.get(workshop.getId()) != workshop) {
            throw new IllegalArgumentException("Unknown instructor or workshop");
        }

        WorkshopImpl actualWorkshop = (WorkshopImpl) workshop;
        Instructor previous = workshop.getInstructor();

        if (previous == instructor) {
            return;
        }

        if (previous != null) {
            ((InstructorImpl) previous).removeWorkshop(workshop);
        }

        actualWorkshop.setInstructor(instructor);
        ((InstructorImpl) instructor).addWorkshop(workshop);
    }

    @Override
    public Student createStudent(String id, String name, String email) {
        if (students.containsKey(id)) {
            throw new IllegalArgumentException("Student ID already exists: " + id);
        }

        Student student = new StudentImpl(id, name, email);
        students.put(id, student);
        return student;
    }

    @Override
    public Team createTeam(String name, Workshop workshop,
                           List<String> memberIds) {

        if (workshop == null ||
                workshops.get(workshop.getId()) != workshop ||
                memberIds == null) {
            throw new IllegalArgumentException("Invalid workshop or members");
        }

        List<Student> members = new ArrayList<>();
        Set<String> seenIds = new HashSet<>();

        // Validate all members before changing system state.
        for (String id : memberIds) {

            if (!seenIds.add(id)) {
                throw new IllegalArgumentException("Duplicate student: " + id);
            }

            Student student = students.get(id);

            if (student == null) {
                throw new IllegalArgumentException("Unknown student: " + id);
            }

            if (((StudentImpl) student).getTeam() != null) {
                throw new IllegalArgumentException(
                        "Student already belongs to a team: " + id
                );
            }

            members.add(student);
        }

        Team team = new TeamImpl(nextTeamId, name, workshop, members);

        teams.put(nextTeamId, team);
        nextTeamId++;

        for (Student student : members) {
            ((StudentImpl) student).setTeam(team);
        }

        return team;
    }

    @Override
    public List<Team> getTeams() {
        return new ArrayList<>(teams.values());
    }

    @Override
    public Team getTeam(Integer id) {
        return teams.get(id);
    }

    @Override
    public List<Student> getStudents() {
        return new ArrayList<>(students.values());
    }

    @Override
    public Student getStudent(String id) {
        return students.get(id);
    }

    @Override
    public List<Workshop> getWorkshops() {
        return new ArrayList<>(workshops.values());
    }

    @Override
    public Workshop getWorkshop(Integer id) {
        return workshops.get(id);
    }

    @Override
    public List<Instructor> getInstructors() {
        return new ArrayList<>(instructors.values());
    }

    @Override
    public Instructor getInstructor(String id) {
        return instructors.get(id);
    }

    @Override
    public List<Student> getFreeStudents() {
        List<Student> freeStudents = new ArrayList<>();

        for (Student student : students.values()) {
            if (((StudentImpl) student).getTeam() == null) {
                freeStudents.add(student);
            }
        }

        return freeStudents;
    }

    @Override
    public List<Student> getStudentsOfInstructor(String instructorId) {
        List<Student> result = new ArrayList<>();
        Instructor instructor = instructors.get(instructorId);

        if (instructor == null) {
            return result;
        }

        for (Team team : teams.values()) {
            if (team.getWorkshop().getInstructor() == instructor) {
                result.addAll(team.getStudents());
            }
        }

        return result;
    }

    @Override
    public List<Student> getStudentsOfWorkshop(Integer workshopId) {
        List<Student> result = new ArrayList<>();
        Workshop workshop = workshops.get(workshopId);

        if (workshop == null) {
            return result;
        }

        for (Team team : teams.values()) {
            if (team.getWorkshop() == workshop) {
                result.addAll(team.getStudents());
            }
        }

        return result;
    }
}
