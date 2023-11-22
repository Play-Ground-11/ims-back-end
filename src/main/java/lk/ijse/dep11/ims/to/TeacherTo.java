package lk.ijse.dep11.ims.to;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;
import javax.validation.constraints.Pattern;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TeacherTo implements Serializable {
    @Null(message = "Id should be empty")
    private Integer id;
    @NotBlank(message = "Name should not be empty")
    @Pattern(regexp = "^[A-Za-z ]+$", message = "Invalid Name")
    private String name;
    @Pattern(regexp = "^\\d{3}-\\d{7}$", message = "Invalid Contact Number")
    private String contact;
}
