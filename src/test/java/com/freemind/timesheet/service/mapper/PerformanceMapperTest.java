package com.freemind.timesheet.service.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PerformanceMapperTest {
    private PerformanceMapper performanceMapper;

    @BeforeEach
    public void setUp() {
        performanceMapper = new PerformanceMapperImpl();
    }

    @Test
    public void testEntityFromId() {
        Long id = 1L;
        assertThat(performanceMapper.fromId(id).getId()).isEqualTo(id);
        assertThat(performanceMapper.fromId(null)).isNull();
    }
}
