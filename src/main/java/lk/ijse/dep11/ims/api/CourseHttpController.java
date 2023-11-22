package lk.ijse.dep11.ims.api;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lk.ijse.dep11.ims.to.CourseTO;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.annotation.PreDestroy;
import javax.validation.Valid;
import java.sql.*;
import java.util.LinkedList;
import java.util.List;

@RestController
@RequestMapping("/courses")
@CrossOrigin
public class CourseHttpController {
    private final HikariDataSource pool;
    public CourseHttpController() {
        HikariConfig config = new HikariConfig();
        config.setUsername("root");
        config.setPassword("mysql");
        config.setJdbcUrl("jdbc:mysql://localhost:3306/dep11_ims");
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.addDataSourceProperty("maximumPoolSize", 10);
        pool = new HikariDataSource(config);
    }
    @PreDestroy
    public void destroy(){
        pool.close();
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(produces = "application/json", consumes = "application/json")
    public CourseTO createCourse(@RequestBody @Validated CourseTO course){
        try {
            Connection connection = pool.getConnection();
            PreparedStatement stm = connection.prepareStatement("INSERT INTO course(name, duration_in_months) VALUES (?,?)", Statement.RETURN_GENERATED_KEYS);
            stm.setString(1,course.getName());
            stm.setString(2,course.getDuration());
            stm.executeUpdate();
            ResultSet generatedKeys = stm.getGeneratedKeys();
            generatedKeys.next();
            int id = generatedKeys.getInt(1);
            course.setId(id);
            return course;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping(produces = "application/json")
    public List<CourseTO> getAllCourses(){
        try (Connection connection = pool.getConnection()){
            Statement stm = connection.createStatement();
            ResultSet rst = stm.executeQuery("SELECT * FROM course ORDER BY id");
            LinkedList<CourseTO> courseList = new LinkedList<>();
            while (rst.next()){
                int id = rst.getInt("id");
                String name = rst.getString("name");
                String durationInMonths = rst.getString("duration_in_months");
                courseList.add(new CourseTO(id,name,durationInMonths));
            }
            return courseList;
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @GetMapping("{id}")
    public CourseTO getCourse(@PathVariable("id") int courseId){
        try(Connection connection = pool.getConnection()) {
            PreparedStatement stm = connection.prepareStatement("SELECT * FROM course WHERE id = ?");
            stm.setInt(1,courseId);
            ResultSet rst = stm.executeQuery();
            if(!rst.next()){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }else{
                    int id = rst.getInt("id");
                    String name = rst.getString("name");
                    String durationInMonths = rst.getString("duration_in_months");
                    CourseTO course = new CourseTO(id, name, durationInMonths);
                    return course;
            }

        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping("{id}")
    public void updateCourse(@PathVariable("id") int courseId,
                             @RequestBody @Valid CourseTO course){
        try {
            Connection connection = pool.getConnection();
            PreparedStatement stmExist = connection.prepareStatement("SELECT * FROM course WHERE id = ?");
            stmExist.setInt(1,courseId);
            if(!stmExist.executeQuery().next()){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Course not found!");
            }

            PreparedStatement stm = connection.prepareStatement("UPDATE course SET name = ?, duration_in_months = ? WHERE id=?");
            stm.setString(1,course.getName());
            stm.setString(2,course.getDuration());
            stm.setInt(3,courseId);
            stm.executeUpdate();
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("{id}")
    public void deleteCourse(@PathVariable("id") int courseId){
        try {
            Connection connection = pool.getConnection();
            PreparedStatement stmExit = connection.prepareStatement("SELECT * FROM course WHERE id = ?");
            stmExit.setInt(1,courseId);
            if(!stmExit.executeQuery().next()){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Course not found!");
            }

            PreparedStatement stm = connection.prepareStatement("DELETE FROM course WHERE id = ?");
            stm.setInt(1,courseId);
            stm.executeUpdate();
        }catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
}
