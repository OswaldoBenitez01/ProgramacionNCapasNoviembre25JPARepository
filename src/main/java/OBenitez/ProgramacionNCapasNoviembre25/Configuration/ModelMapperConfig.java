package OBenitez.ProgramacionNCapasNoviembre25.Configuration;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper  modelMapper() {
        
        ModelMapper modelMapper = new ModelMapper();

        modelMapper.getConfiguration()
                .setMatchingStrategy(MatchingStrategies.STRICT) // solo campos exactos
                .setAmbiguityIgnored(true) // ignora conflictos
                .setSkipNullEnabled(true) // omite valores nulos
                .setFieldMatchingEnabled(true) // permite acceso directo a campos
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE);

        return modelMapper;
    }

}
