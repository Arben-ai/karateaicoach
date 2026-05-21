package ch.zhaw.karateaicoach.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.List;

import org.junit.jupiter.api.Test;

class TrainingsplanStatusAggregationDTOTest {

    @Test
    void defaultConstructor_fieldsAreNull() {
        TrainingsplanStatusAggregationDTO dto = new TrainingsplanStatusAggregationDTO();
        assertNull(dto.getId());
        assertEquals(0, dto.getCount());
        assertNull(dto.getTrainingsplanIds());
    }

    @Test
    void setters_andGetters_workCorrectly() {
        TrainingsplanStatusAggregationDTO dto = new TrainingsplanStatusAggregationDTO();
        dto.setId("ACTIVE");
        dto.setCount(3);
        dto.setTrainingsplanIds(List.of("tp-1", "tp-2", "tp-3"));

        assertEquals("ACTIVE", dto.getId());
        assertEquals(3, dto.getCount());
        assertEquals(List.of("tp-1", "tp-2", "tp-3"), dto.getTrainingsplanIds());
    }
}
