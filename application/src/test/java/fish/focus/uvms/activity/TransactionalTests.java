package fish.focus.uvms.activity;

import fish.focus.uvms.activity.rest.BaseActivityArquillianTest;
import org.junit.After;
import org.junit.Before;

import javax.inject.Inject;
import javax.transaction.*;

public class TransactionalTests extends BaseActivityArquillianTest {

    @Inject
    protected UserTransaction userTransaction;

    @Before
    public void before() throws SystemException, NotSupportedException {
        userTransaction.begin();
    }

    @After
    public void after() throws SystemException {
        userTransaction.rollback();
    }
}
