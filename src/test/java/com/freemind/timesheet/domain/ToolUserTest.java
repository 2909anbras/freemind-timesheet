package com.freemind.timesheet.domain;

import static org.assertj.core.api.Assertions.assertThat;

import com.freemind.timesheet.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

public class ToolUserTest {

    @Test
    public void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ToolUser.class);
        ToolUser toolUser1 = new ToolUser();
        toolUser1.setId(1L);
        ToolUser toolUser2 = new ToolUser();
        toolUser2.setId(toolUser1.getId());
        assertThat(toolUser1).isEqualTo(toolUser2);
        toolUser2.setId(2L);
        assertThat(toolUser1).isNotEqualTo(toolUser2);
        toolUser1.setId(null);
        assertThat(toolUser1).isNotEqualTo(toolUser2);
    }
}
