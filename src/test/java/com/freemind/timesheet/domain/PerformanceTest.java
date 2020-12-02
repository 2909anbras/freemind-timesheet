package com.freemind.timesheet.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.freemind.timesheet.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

public class PerformanceTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Performance.class);
        Performance performance1 = new Performance();
        performance1.setId(1L);
        Performance performance2 = new Performance();
        performance2.setId(performance1.getId());
        assertThat(performance1).isEqualTo(performance2);
        performance2.setId(2L);
        assertThat(performance1).isNotEqualTo(performance2);
        performance1.setId(null);
        assertThat(performance1).isNotEqualTo(performance2);
    }
}
