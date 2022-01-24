package org.madhusudhan.awslambdas.tokenauthorizer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import com.amazonaws.lambda.io.AuthPolicy;
import com.amazonaws.lambda.io.TokenAuthorizerContext;
import com.amazonaws.lambda.thirdparty.com.google.gson.Gson;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.tests.annotations.Event;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.madhusudhan.awslambdas.validatejwt.TokenValidator;



@ExtendWith(MockitoExtension.class)
public class LambdaAuthorizerTest {
    @Mock
    private Context mockedContext;

    @Mock
    private LambdaLogger loggerMock;

    private LambdaAuthorizer handler;

    private static final String VALID_TOKEN = "Valid Token";


    @BeforeEach
    public void setUp() throws Exception {
        when(mockedContext.getLogger()).thenReturn(loggerMock);
        doAnswer(call -> {
            System.out.println((String)call.getArgument(0));//print to the console
            return null;
        }).when(loggerMock).log(anyString());
        handler = new LambdaAuthorizer();
      }

    @ParameterizedTest
    @Event(value = "event.json", type = TokenAuthorizerContext.class)
    public void testHandleRequest(TokenAuthorizerContext event) {
        try(MockedStatic<TokenValidator> mb  = Mockito.mockStatic(TokenValidator.class)) {
            // stub the static method that is called by the class under test
            mb.when(() -> TokenValidator.validateToken(any(String.class )))
            .thenReturn(VALID_TOKEN);
            // given
            AuthPolicy result = null;
            handler = new LambdaAuthorizer();
            try {
                result = getJsonResponse("authPolicy.json");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            // when
            AuthPolicy response = handler.handleRequest(event, mockedContext);

            // then
            assertThat(response).isNotNull();
            assertThat(response.getPrincipalId()).isEqualTo(result.getPrincipalId());

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        

    }

    private AuthPolicy getJsonResponse(String jsonFile) throws IOException {
        Reader reader = new InputStreamReader(this.getClass().getClassLoader()
                .getResourceAsStream(jsonFile));
        AuthPolicy response = new Gson().fromJson(reader, AuthPolicy.class);
        return response;
    }
}
