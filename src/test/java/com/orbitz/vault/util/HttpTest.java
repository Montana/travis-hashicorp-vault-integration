package com.orbitz.vault.util;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import org.junit.Test;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.mock.Calls;

import java.io.IOException;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

@SuppressWarnings("unchecked")
public class HttpTest {

    static class Result {
        private String value;

        public Result(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    @Test
    public void shouldExtractSuccessfulResponse() throws IOException {
        String value = UUID.randomUUID().toString();
        Result expected = new Result(value);
        Call<Result> call = Calls.response(expected);

        Result result = Http.extract(call);

        assertEquals(expected.getValue(), result.getValue());
    }

    @Test
    public void shouldThrowOnFailedResponse() throws IOException {
        Response<?> response = Response.error(500,
                ResponseBody.create(MediaType.parse("application/json"), "Error!"));

        Call<?> call = Calls.response(response);

        try {
            Http.extract(call);
        } catch (VaultApiException vae) {
            assertTrue(vae.getMessage().contains("Error!"));
            assertTrue(vae.getMessage().contains("500"));
        }
    }

    @Test
    public void shouldHandleSuccessfulResponse() throws IOException {
        Call<Void> call = mock(Call.class);
        Response<Void> response = Response.success(null);

        doReturn(response).when(call).execute();

        try {
            Http.handle(call);
        } catch(Exception ex) {
            fail("successful response shouldn't throw exception");
        }
    }

    @Test
    public void shouldThrowOnFailedHandleResponse() throws IOException {
        Response<?> response = Response.error(500,
                ResponseBody.create(MediaType.parse("application/json"), "Error!"));

        Call<Void> call = (Call<Void>) Calls.response(response);

        try {
            Http.handle(call);
        } catch (VaultApiException vae) {
            assertTrue(vae.getMessage().contains("Error!"));
            assertTrue(vae.getMessage().contains("500"));
        }
    }
}
