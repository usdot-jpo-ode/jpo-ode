package us.dot.its.jpo.ode.config;

import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.DefaultConversionService;

@Configuration
public class CustomConversionServiceConfig {

    @Bean
    @ConfigurationPropertiesBinding
    public Converter<String, Integer> stringToIntegerConverter() {
        return new StringToIntegerConverter();
    }

    @Bean
    public DefaultConversionService conversionService() {
        DefaultConversionService conversionService = new DefaultConversionService();
        conversionService.addConverter(stringToIntegerConverter());
        return conversionService;
    }
}