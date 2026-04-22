package ch.zhaw.karateaicoach.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.concurrent.atomic.AtomicReference;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import org.junit.jupiter.api.Test;

class MongoTestControllerTest extends BaseControllerTest {

    @Test
    void testMongoDb_success_returnsConnectionOk() throws Exception {
        AtomicReference<DBObject> savedRef = new AtomicReference<>();

        when(mongoTemplate.save(any(DBObject.class), anyString())).thenAnswer(invocation -> {
            BasicDBObject obj = invocation.getArgument(0);
            obj.put("_id", "test-id");
            savedRef.set(obj);
            return obj;
        });

        when(mongoTemplate.findById(any(), eq(DBObject.class), anyString()))
                .thenAnswer(invocation -> savedRef.get());

        mockMvc.perform(get("/testmongodb"))
                .andExpect(status().isOk())
                .andExpect(content().string("Connection ok"));
    }

    @Test
    void testMongoDb_nullRead_returnsFailure() throws Exception {
        BasicDBObject saved = new BasicDBObject();
        saved.put("_id", "test-id");
        saved.put("time", 12345L);

        when(mongoTemplate.save(any(DBObject.class), anyString())).thenReturn(saved);
        when(mongoTemplate.findById(any(), eq(DBObject.class), anyString())).thenReturn(null);

        mockMvc.perform(get("/testmongodb"))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testMongoDb_exception_returnsFailure() throws Exception {
        when(mongoTemplate.save(any(DBObject.class), anyString()))
                .thenThrow(new RuntimeException("DB connection refused"));

        mockMvc.perform(get("/testmongodb"))
                .andExpect(status().isInternalServerError());
    }
}
