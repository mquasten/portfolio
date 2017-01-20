package de.mq.portfolio.support;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import de.mq.portfolio.batch.RulesEngine;
import de.mq.portfolio.user.User;
import junit.framework.Assert;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { RulesConfiguration.class })
@Ignore
public class UsersImportIntegrationTest {

    @Autowired
    @Qualifier("importUsers")
    private RulesEngine rulesEngine;

    @Test
    @Ignore
    public final void doImport() {

        Assert.assertNotNull(rulesEngine);
        final Map<String, Object> parameters = new HashMap<>();
        parameters.put("filename", "data/users.csv");

        rulesEngine.fireRules(parameters);

        System.out.println("Failed: " + rulesEngine.failed());
        System.out.println("Processed: " + rulesEngine.processed());

        Assert.assertTrue(rulesEngine.failed().isEmpty());
        Assert.assertEquals(3, rulesEngine.processed().size());

        @SuppressWarnings("unchecked")
        final Collection<User> results = (Collection<User>) parameters
                .get(AbstractServiceRule.ITEMS_PARAMETER);

        results.forEach(user -> System.out.println(user));

    }

}
