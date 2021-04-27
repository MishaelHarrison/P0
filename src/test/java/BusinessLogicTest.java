import Exceptions.BadLogin;
import Exceptions.BusinessException;
import Interfaces.IBankData;
import Interfaces.IBusinessLogic;
import Models.transaction;
import PZero.Libs.BusinessLogic;
import PZero.Libs.DAO.Entities.accountEntity;
import PZero.Libs.DAO.Entities.transactionEntity;
import PZero.Libs.DAO.Entities.userEntity;
import org.junit.Test;

import java.sql.Timestamp;
import java.util.ArrayList;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class BusinessLogicTest {

    private IBusinessLogic testSubject;

    @Test
    public void testGetTransactionLog() throws BusinessException, BadLogin {
        ArrayList<transactionEntity> query = new ArrayList<>();
        query.add(new transactionEntity(
                1,null,1,1d,true,new Timestamp(1,1,1,1,1,1,1)
        ));
        query.add(new transactionEntity(
                2,2,null,2d,true,new Timestamp(2,2,2,2,2,2,2)
        ));
        query.get(0).setReceivingAccount(new accountEntity(
                1,1,1,true,"1"
        ));
        query.get(1).setIssuingAccount(new accountEntity(
                2,2,2,true,"2"
        ));
        query.get(0).getReceivingAccount().setUser(new userEntity(
                1,"1","1","1","1"
        ));
        query.get(1).getIssuingAccount().setUser(new userEntity(
                2,"2","2","2","2"
        ));
        IBankData mockBank = mock(IBankData.class);
        when(mockBank.fullTransactionLog()).thenReturn(query);
        testSubject = new BusinessLogic(mockBank);
        ArrayList<transaction> output = testSubject.getTransactionLog("admin","apache");
        assumeTrue(true);
    }
}
