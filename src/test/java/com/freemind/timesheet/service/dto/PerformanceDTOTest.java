package com.freemind.timesheet.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.freemind.timesheet.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

public class PerformanceDTOTest {

    @Test
    public void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(PerformanceDTO.class);
        PerformanceDTO performanceDTO1 = new PerformanceDTO();
        performanceDTO1.setId(1L);
        PerformanceDTO performanceDTO2 = new PerformanceDTO();
        assertThat(performanceDTO1).isNotEqualTo(performanceDTO2);
        performanceDTO2.setId(performanceDTO1.getId());
        assertThat(performanceDTO1).isEqualTo(performanceDTO2);
        performanceDTO2.setId(2L);
        assertThat(performanceDTO1).isNotEqualTo(performanceDTO2);
        performanceDTO1.setId(null);
        assertThat(performanceDTO1).isNotEqualTo(performanceDTO2);
    }
}
