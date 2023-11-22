package lk.ijse.dep11.ims.api;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lk.ijse.dep11.ims.to.TeacherTo;
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
@RequestMapping("/teachers")
@CrossOrigin
public class TeacherHttpController {

    private final HikariDataSource pool;
    public TeacherHttpController(){
        HikariConfig config = new HikariConfig();
        config.setUsername("root");
        config.setPassword("As1017**");
        config.setJdbcUrl("jdbc:mysql://localhost:3306/dep11_ims");
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        config.addDataSourceProperty("maximumPoolSize",10);
        pool = new HikariDataSource(config);
    }

    @PreDestroy
    public void destroy(){
        pool.close();
    }
    /*Create new teacher*/
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping(produces = "application/json", consumes = "application/json")
    public TeacherTo createTeacher(@RequestBody @Validated TeacherTo teacher){
//        System.out.println("createTeacher()");
//        return null;
        try(Connection connection = pool.getConnection()) {
            PreparedStatement stm = connection.prepareStatement("INSERT INTO teacher(name, contact) VALUES (?, ?)",
                    Statement.RETURN_GENERATED_KEYS);
            stm.setString(1, teacher.getName());
            stm.executeUpdate();
            ResultSet generatedKeys = stm.getGeneratedKeys();
            generatedKeys.next();
            int id = generatedKeys.getInt(1);
            teacher.setId(id);
            teacher.setContact(teacher.getContact());
            return teacher;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    /* Update Teacher*/
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PatchMapping(value = "/{teachersId}", consumes = "application/json")
    public void updateTeacher(@PathVariable int id, @RequestBody @Validated TeacherTo teacher){
//        System.out.println("updateTeacher()");
        try(Connection connection = pool.getConnection()){
            PreparedStatement stmExist = connection.prepareStatement("SELECT * FROM teacher WHERE id=?");
            stmExist.setInt(1, id);
            if(!stmExist.executeQuery().next()){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found");
            }

            PreparedStatement stm = connection
                    .prepareStatement("UPDATE teacher SET name = ?, contact=? WHERE id=?");
            stm.setString(1, teacher.getName());
            stm.setString(2, teacher.getContact());
            stm.setInt(3, teacher.getId());
            stm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    /*Delete Teacher*/
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{teacherId}")
    public void deleteTeacher(@PathVariable int teacherId){
//        System.out.println("deleteTeacher()");
        try(Connection connection = pool.getConnection()) {
            PreparedStatement stmExist = connection.prepareStatement("SELECT * FROM teacher WHERE id=?");
            stmExist.setInt(1, teacherId);
            if(!stmExist.executeQuery().next()){
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Teacher not found");

            }
            PreparedStatement stm = connection.prepareStatement("DELETE FROM teacher WHERE id=?");
            stm.setInt(1, teacherId);
            stm.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    /*Get Teacher*/
    @GetMapping(value = "/{teacherId}", produces = "application/json")
    public void getTeacher(@PathVariable String teacherId){
        System.out.println("getTeacher()");
    }
    /*Get All Teacher*/
    @GetMapping(produces = "application/json")
    public List<TeacherTo> getAllTeacher(){
//        System.out.println("getAllTeacher()");
//        return null;
        try(Connection connection = pool.getConnection()) {
            Statement stm = connection.createStatement();
            ResultSet rst = stm.executeQuery("SELECT * FROM teacher ORDER BY id");
            LinkedList<TeacherTo> teacherList = new LinkedList<>();
            while (rst.next()){
                int id = rst.getInt("id");
                String name = rst.getString("name");
                String contact = rst.getString("contact");
                teacherList.add(new TeacherTo(id, name, contact));
            }
            return teacherList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
