package mercury.api.models;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;


public abstract class modelBase<T extends modelBase<T>> {

    public String toJsonString() throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        String output = mapper.writeValueAsString(this);       
        return output;
    }
}
