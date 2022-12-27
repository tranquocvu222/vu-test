package com.nals.auction;

import com.github.javafaker.Faker;
import com.nals.auction.config.ApplicationProperties;
import com.tobedevoured.modelcitizen.CreateModelException;
import com.tobedevoured.modelcitizen.ModelFactory;
import com.tobedevoured.modelcitizen.RegisterBlueprintException;
import lombok.Getter;
import lombok.Setter;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.result.JsonPathResultMatchers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.time.ZoneId;
import java.util.Arrays;

@Getter
@Setter
@Component
public class AbstractTest {

    public static final String ACCOUNT_PASSWORD = "Uaa123#@!";
    public static final Long INVALID_ID = -1L;
    public static final Long CURRENT_USER_ID = 99L;
    public static final String CURRENT_USER_USERNAME = "username";
    private static final String DEFAULT_TIME_ZONE = "Asia/Ho_Chi_Minh";
    private static final String STORAGE = "storage";
    private static final String FILE_SERVICE = "fileService";

    @Autowired
    private HttpMessageConverter<?>[] httpMessageConverters;

    @Autowired
    private MappingJackson2HttpMessageConverter jacksonMessageConverter;

    @Autowired
    private PageableHandlerMethodArgumentResolver pageableArgumentResolver;

    @Mock
    private ApplicationProperties mockApplicationProperties;

    private ModelFactory modelFactory;
    private Faker faker;
    private ZoneId zoneId;

    @Before
    public void before() {
        this.modelFactory = new ModelFactory();
        this.faker = new Faker();

        Mockito.when(mockApplicationProperties.getTimezone())
               .thenReturn(DEFAULT_TIME_ZONE);

        zoneId = ZoneId.of(mockApplicationProperties.getTimezone());
    }

    public void registerBlueprints(final Class<?>... classes)
        throws RegisterBlueprintException {
        modelFactory.setRegisterBlueprints(Arrays.asList(classes));
    }

    public <T> T createFakeModel(final Class<T> clazz)
        throws CreateModelException {
        return modelFactory.createModel(clazz, true);
    }

    public JsonPathResultMatchers jsonPath(final String expression) {
        return MockMvcResultMatchers.jsonPath(expression);
    }

    public ResultMatcher matchJsonPath(final String expression, final Object expectedValue) {
        return jsonPath(expression).value(expectedValue);
    }
}
