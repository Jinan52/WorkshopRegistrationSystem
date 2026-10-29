
package patmal.workshops.impl.model;

import patmal.workshops.api.model.Student;
import patmal.workshops.api.model.Team;
import patmal.workshops.api.model.Workshop;

import java.util.ArrayList;
import java.util.List;

public class TeamImpl implements Team {

    private final Integer id;
    private final String name;
    private final List<Student> students;
    private final Workshop workshop;

    public TeamImpl(Integer id, String name, Workshop workshop,
                    List<Student> students) {
        this.id = id;
        this.name = name;
        this.workshop = workshop;
        this.students = new ArrayList<>(students);
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<Student> getStudents() {
        return new ArrayList<>(students);
    }

    @Override
    public Workshop getWorkshop() {
        return workshop;
    }
}
