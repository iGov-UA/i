package org.igov.service.business.flow.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.text.ParseException;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.igov.model.flow.FlowSlot;
import org.igov.util.JSON.JsonRestUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: goodg_000
 * Date: 29.06.2015
 * Time: 21:38
 */
public class DefaultFlowSlotGeneratorTest {

    private static final String DEFAULT_DURATION = "PT15M";

    public DefaultFlowSlotGenerator generator;

    private DateTime startDate;
    private DateTime endDate;
    private int maxGeneratedSlotsCount;
    private String defaultSlotName;

    @Before
    public void setUp() {
        generator = new DefaultFlowSlotGenerator();
        maxGeneratedSlotsCount = 3333;
        defaultSlotName = DEFAULT_DURATION;

        startDate = new DateTime(2015, 6, 29, 0, 0);
        endDate = new DateTime(2015, 6, 30, 0, 0);
    }

    @Test
    public void testGenerateEmptyConfiguration() throws JsonProcessingException, ParseException {
        Map<String, String> configuration = new HashMap<>();
        String sData = JsonRestUtils.toJson(configuration);
        List<FlowSlot> slot = generator.generateObjects(configuration, startDate, endDate, maxGeneratedSlotsCount,
                defaultSlotName, null);

        Assert.assertTrue(slot.isEmpty());
    }

    private void validateGeneration(String cronExpression, int slotsCount) throws JsonProcessingException, ParseException {
        Map<String, String> configuration = new HashMap<>();
        configuration.put(cronExpression, DEFAULT_DURATION);

        List<FlowSlot> slot = generator.generateObjects(configuration, startDate, endDate, maxGeneratedSlotsCount,
                defaultSlotName, null);

        Assert.assertEquals(slotsCount, slot.size());
    }

    @Test
    public void testGenerate() throws JsonProcessingException, ParseException {

        for (int i = 1; i <= 4; ++i) {
            endDate = startDate.plusDays(i);

            // Fire at 10:15am every day
            validateGeneration("0 15 10 ? * *", i);

            // Fire every minute starting at 2pm and ending at 2:59pm, every day
            validateGeneration("0 * 14 * * ?", 60 * i);

            // Fire every minute starting at 2pm and ending at 2:59pm, every day
            validateGeneration("0 0/5 14,18 * * ?", 24 * i);
        }

        //Fire at 10:15am every Monday, Tuesday, Wednesday, Thursday and Friday
        endDate = startDate.plusDays(7);
        validateGeneration("0 15 10 ? * MON-FRI", 5);
    }

    @Test
    public void testGenerateNearToRealData1() throws JsonProcessingException, ParseException {
        startDate = new DateTime(2015, 7, 1, 0, 0);
        endDate = new DateTime(2015, 7, 2, 0, 0);
        validateGeneration("0 0/15 8-15 ? * MON-FRI *", 32);
    }

    @Test(expected = IllegalStateException.class)
    public void testTooManyValues() throws JsonProcessingException, ParseException {
        maxGeneratedSlotsCount = 59;
        validateGeneration("0 * 14 * * ?", 60);
    }
}
