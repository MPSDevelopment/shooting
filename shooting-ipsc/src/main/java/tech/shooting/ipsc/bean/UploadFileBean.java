package tech.shooting.ipsc.bean;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class UploadFileBean {

    @JsonProperty
    private String message;

    @JsonProperty
    private String path;

    public UploadFileBean(String path, String message, Object... objects) {
        this.path = path;
        this.message = String.format(message, objects);
    }

}
