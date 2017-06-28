package de.mq.portfolio.support;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.mq.portfolio.batch.RulesEngine;
import de.mq.portfolio.gateway.ShareGatewayParameter;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { RulesConfiguration.class })
@ActiveProfiles("google")
@Ignore()
public class ShareGatewayParameterImportIntegrationTest {

    @Autowired
    @Qualifier("importArivaRateHistory")
    private RulesEngine rulesEngine;
    
    
    @Test
    public final void doImport() {

        Assert.assertNotNull(rulesEngine);
        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("filename", "data/ariva.csv");

        rulesEngine.fireRules(parameters);

        System.out.println("Failed: " + rulesEngine.failed());
        System.out.println("Processed: " + rulesEngine.processed());

        Assert.assertTrue(rulesEngine.failed().isEmpty());
        Assert.assertEquals(2, rulesEngine.processed().size());

        @SuppressWarnings("unchecked")
        final Collection<ShareGatewayParameter> results = (Collection<ShareGatewayParameter>) parameters
                .get(AbstractServiceRule.ITEMS_PARAMETER);

        results.forEach(shareGatewayParameter -> System.out.println(shareGatewayParameter));

    }


}
