package tech.shooting.ipsc.bean;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Accessors(chain = true)
public class WeaponTypeBean {
    @NotBlank
    @Size(min = 2, message = "Must be min 2 characters")
    private String name;
}
