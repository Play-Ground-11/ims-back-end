package lk.ijse.dep11.ims.to;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.*;
import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CourseTO implements Serializable {
    @Null(message = "id should be empty")
    private Integer id;
    @NotBlank(message = "name should not be empty")
    @Length(min = 3)
    private String name;
    @NotNull(message = "duration should not be empty")
    private String duration;
}
