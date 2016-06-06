package org.eclipse.che.api.user.server;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * @author Mihail Kuznyetsov
 */
public class UserNameValidatorTest {

    private UserNameValidator validator;


    @BeforeMethod
    public void setUp() {
        validator = new UserNameValidator();
    }

    @Test(dataProvider = "normalizeNames")
    public void testNormalizeUserName(String input, String expected) {

        Assert.assertEquals(validator.normalizeUserName(input), expected);
    }


    @Test(dataProvider = "validNames")
    public void testValidUserName(String input, String expected) {

        Assert.assertEquals(validator.normalizeUserName(input), expected);
    }

    @DataProvider(name = "normalizeNames")
    public Object[][] normalizeNames() {
        return new Object[][] {{"test", "test"},
                               {"test123", "test123"},
                               {"test 123", "test123"},
                               {"test@gmail.com", "testgmailcom"},
                               {"TEST", "TEST"},
                               {"test-", "test"},
                               {"te-st", "test"},
                               {"-test", "test"},
                               {"te_st", "test"},
                               {"te#st", "test"}
        };
    }

    @DataProvider(name = "validNames")
    public Object[][] validNames() {
        return new Object[][] {{"test", "true"},
                               {"test123", "true"},
                               {"test 123", "true"},
                               {"test@gmail.com", "false"},
                               {"TEST", "true"},
                               {"test-", "false"},
                               {"te-st", "false"},
                               {"-test", "false"},
                               {"te_st", "false"},
                               {"te#st", "false"}
        };
    }
}
