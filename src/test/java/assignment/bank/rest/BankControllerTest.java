package assignment.bank.rest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

import javax.ws.rs.core.Response;

import org.hamcrest.core.Is;
import org.junit.Test;

import assignment.bank.exception.BankAccountException;
import assignment.bank.model.Account;
import assignment.bank.rest.AccountCreateRequest;
import assignment.bank.rest.BankController;

public class BankControllerTest {
	
	private BankController controller = new BankController();
	
	@Test
	public void testCreateAccount() throws BankAccountException {
		AccountCreateRequest req = new AccountCreateRequest();
		double initialDeposit = 2000;
		req.setInitialDeposit(initialDeposit);
		Response response = controller.create(req);
		assertNotNull(response);
		assertThat(response.getStatus(), Is.is(201));
		Object entity = response.getEntity();
		assertNotNull(entity);
		assertThat(entity instanceof Account, Is.is(true));
		assertEquals(initialDeposit, ((Account)entity).getBalance(), 0);
	}
	
	@Test
	public void testDeposit() throws BankAccountException {
		AccountCreateRequest req = new AccountCreateRequest();
		double initialDeposit = 2000;
		req.setInitialDeposit(initialDeposit);
		
		Response response = controller.create(req);
		assertNotNull(response);
		assertThat(response.getStatus(), Is.is(201));
		assertNotNull(response.getEntity());
		assertThat(response.getEntity() instanceof Account, Is.is(true));
		String accountNumber = ((Account)response.getEntity()).getAccountNumber();

		AccountDepositRequest depositReq = new AccountDepositRequest();
		depositReq.setAmount(1000);
		response = controller.deposit(accountNumber, depositReq);
		assertNotNull(response);
		assertThat(response.getStatus(), Is.is(200));
		assertNotNull(response.getEntity());		
		double availableBalance = (double) ((Map)response.getEntity()).get("availableBalance");
		assertEquals(3000, availableBalance, 0);
	}
	
	@Test
	public void testDepositNegative() throws BankAccountException {
		AccountCreateRequest req = new AccountCreateRequest();
		double initialDeposit = 2000;
		req.setInitialDeposit(initialDeposit);
		
		Response response = controller.create(req);
		assertNotNull(response);
		assertThat(response.getStatus(), Is.is(201));
		assertNotNull(response.getEntity());
		assertThat(response.getEntity() instanceof Account, Is.is(true));
		String accountNumber = ((Account)response.getEntity()).getAccountNumber();

		AccountDepositRequest depositReq = new AccountDepositRequest();
		depositReq.setAmount(-1000);
		response = controller.deposit(accountNumber, depositReq);
		assertNotNull(response);
		assertThat(response.getStatus(), Is.is(400));
	}
	
	@Test
	public void testTransfer() throws BankAccountException {
		AccountCreateRequest req1 = new AccountCreateRequest();
		double initialDeposit = 2000;
		req1.setInitialDeposit(initialDeposit);
		
		Response response = controller.create(req1);		
		assertNotNull(response);
		assertThat(response.getStatus(), Is.is(201));
		assertNotNull(response.getEntity());
		assertThat(response.getEntity() instanceof Account, Is.is(true));

		String accountNumber1 = ((Account)response.getEntity()).getAccountNumber();
		
		AccountCreateRequest req2 = new AccountCreateRequest();
		initialDeposit = 1000;
		req2.setInitialDeposit(initialDeposit);
		
		response = controller.create(req2);
		assertNotNull(response);
		assertThat(response.getStatus(), Is.is(201));
		assertNotNull(response.getEntity());
		assertThat(response.getEntity() instanceof Account, Is.is(true));
		String accountNumber2 = ((Account)response.getEntity()).getAccountNumber();
		
		AccountTransferRequest transferRequest = new AccountTransferRequest();
		transferRequest.setAmount(1000);
		transferRequest.setToAccountNumber(accountNumber2);
		
		response = controller.transfer(accountNumber1, transferRequest);		
		assertNotNull(response);
		assertThat(response.getStatus(), Is.is(200));
		assertNotNull(response.getEntity());
		assertThat(response.getEntity() instanceof Map, Is.is(true));
		double availableBalance = (double) ((Map)response.getEntity()).get("availableBalance");
		assertEquals(1000, availableBalance, 0);
		
		response = controller.get(accountNumber2);
		assertNotNull(response);
		assertThat(response.getStatus(), Is.is(200));
		assertNotNull(response.getEntity());
		assertThat(response.getEntity() instanceof Account, Is.is(true));
		double balanceInSecondAccount = ((Account)response.getEntity()).getBalance();
		assertEquals(2000, balanceInSecondAccount, 0);
	}
	
	@Test
	public void testGetNegative() {
		Response response = controller.get("1111");
		assertEquals(400, response.getStatus());
	}
	
	@Test
	public void testTransferNegative() throws BankAccountException {
		AccountCreateRequest req1 = new AccountCreateRequest();
		double initialDeposit = 2000;
		req1.setInitialDeposit(initialDeposit);
		
		Response response = controller.create(req1);		
		String accountNumber1 = ((Account)response.getEntity()).getAccountNumber();
		
		AccountCreateRequest req2 = new AccountCreateRequest();
		initialDeposit = 1000;
		req2.setInitialDeposit(initialDeposit);
		
		response = controller.create(req2);
		String accountNumber2 = ((Account)response.getEntity()).getAccountNumber();
		
		AccountTransferRequest transferRequest = new AccountTransferRequest();
		transferRequest.setAmount(5000);
		transferRequest.setToAccountNumber(accountNumber2);
		
		response = controller.transfer(accountNumber1, transferRequest);
		assertEquals(400, response.getStatus());
	}
	
	@Test
	public void testTransferMultipleThread() throws BankAccountException, InterruptedException {
		AccountCreateRequest req = new AccountCreateRequest();
		double initialDepositForFirstAccount = 2000;
		req.setInitialDeposit(initialDepositForFirstAccount);
		Response response = controller.create(req);
		assertNotNull(response);
		assertThat(response.getStatus(), Is.is(201));
		String accountNumber1 = ((Account)response.getEntity()).getAccountNumber();

		req = new AccountCreateRequest();
		double initialDepositForSecondAccount = 1000;
		req.setInitialDeposit(initialDepositForSecondAccount);
		response = controller.create(req);
		assertNotNull(response);
		assertThat(response.getStatus(), Is.is(201));
		String accountNumber2 = ((Account)response.getEntity()).getAccountNumber();

		req = new AccountCreateRequest();
		double initialDepositForThirdAccount = 1000;
		req.setInitialDeposit(initialDepositForThirdAccount);
		response = controller.create(req);
		assertNotNull(response);
		assertThat(response.getStatus(), Is.is(201));
		String accountNumber3 = ((Account)response.getEntity()).getAccountNumber();

        CyclicBarrier barrier = new CyclicBarrier(2);
        AccountTransferRequest transferReq = new AccountTransferRequest();
        transferReq.setToAccountNumber(accountNumber2);
        transferReq.setAmount(500);
        Thread t1 = new Thread(new Runnable() {			
			@Override
			public void run() {
				try {
					barrier.await();
					controller.transfer(accountNumber1, transferReq);
				} catch (InterruptedException | BrokenBarrierException e) {
					e.printStackTrace();
				}
			}
		});
        t1.start();
        
        AccountTransferRequest transferReq2 = new AccountTransferRequest();
        transferReq2.setToAccountNumber(accountNumber3);
        transferReq2.setAmount(500);
        Thread t2 = new Thread(new Runnable() {			
			@Override
			public void run() {
				try {
					barrier.await();
					controller.transfer(accountNumber1, transferReq2);
				} catch (InterruptedException | BrokenBarrierException e) {
					e.printStackTrace();
				}
			}
		});
        t2.start();
        
        Thread.sleep(5000);
  
        response = controller.get(accountNumber1);
        assertNotNull(response);
		assertThat(response.getStatus(), Is.is(200));
		assertEquals(1000, ((Account)response.getEntity()).getBalance(), 0);     
		
        response = controller.get(accountNumber2);
        assertNotNull(response);
		assertThat(response.getStatus(), Is.is(200));
		assertEquals(1500, ((Account)response.getEntity()).getBalance(), 0);  
		
        response = controller.get(accountNumber3);
        assertNotNull(response);
		assertThat(response.getStatus(), Is.is(200));
		assertEquals(1500, ((Account)response.getEntity()).getBalance(), 0); 
	}
	
	@Test
	public void testTransferMultipleThread2() throws BankAccountException, InterruptedException {
		AccountCreateRequest req = new AccountCreateRequest();
		double initialDepositForFirstAccount = 2000;
		req.setInitialDeposit(initialDepositForFirstAccount);
		Response response = controller.create(req);
		assertNotNull(response);
		assertThat(response.getStatus(), Is.is(201));
		String accountNumber1 = ((Account)response.getEntity()).getAccountNumber();

		req = new AccountCreateRequest();
		double initialDepositForSecondAccount = 1000;
		req.setInitialDeposit(initialDepositForSecondAccount);
		response = controller.create(req);
		assertNotNull(response);
		assertThat(response.getStatus(), Is.is(201));
		String accountNumber2 = ((Account)response.getEntity()).getAccountNumber();

		req = new AccountCreateRequest();
		double initialDepositForThirdAccount = 1000;
		req.setInitialDeposit(initialDepositForThirdAccount);
		response = controller.create(req);
		assertNotNull(response);
		assertThat(response.getStatus(), Is.is(201));
		String accountNumber3 = ((Account)response.getEntity()).getAccountNumber();

        CyclicBarrier barrier = new CyclicBarrier(2);
        AccountTransferRequest transferReq = new AccountTransferRequest();
        transferReq.setToAccountNumber(accountNumber2);
        transferReq.setAmount(500);
        Thread t1 = new Thread(new Runnable() {			
			@Override
			public void run() {
				try {
					barrier.await();
					controller.transfer(accountNumber1, transferReq);
				} catch (InterruptedException | BrokenBarrierException e) {
					e.printStackTrace();
				}
			}
		});
        t1.start();
        
        AccountTransferRequest transferReq2 = new AccountTransferRequest();
        transferReq2.setToAccountNumber(accountNumber3);
        transferReq2.setAmount(5000);
        Thread t2 = new Thread(new Runnable() {			
			@Override
			public void run() {
				try {
					barrier.await();
					controller.transfer(accountNumber1, transferReq2);
				} catch (InterruptedException | BrokenBarrierException e) {
					e.printStackTrace();
				}
			}
		});
        t2.start();
        
        Thread.sleep(5000);
  
        response = controller.get(accountNumber1);
        assertNotNull(response);
		assertThat(response.getStatus(), Is.is(200));
		assertEquals(1500, ((Account)response.getEntity()).getBalance(), 0);     
		
        response = controller.get(accountNumber2);
        assertNotNull(response);
		assertThat(response.getStatus(), Is.is(200));
		assertEquals(1500, ((Account)response.getEntity()).getBalance(), 0);  
		
        response = controller.get(accountNumber3);
        assertNotNull(response);
		assertThat(response.getStatus(), Is.is(200));
		assertEquals(1000, ((Account)response.getEntity()).getBalance(), 0); 
	}
	
	@Test
	public void testTransferMultipleThread3() throws BankAccountException, InterruptedException {
		AccountCreateRequest req = new AccountCreateRequest();
		double initialDepositForFirstAccount = 2000;
		req.setInitialDeposit(initialDepositForFirstAccount);
		Response response = controller.create(req);
		assertNotNull(response);
		assertThat(response.getStatus(), Is.is(201));
		String accountNumber1 = ((Account)response.getEntity()).getAccountNumber();

		req = new AccountCreateRequest();
		double initialDepositForSecondAccount = 2000;
		req.setInitialDeposit(initialDepositForSecondAccount);
		response = controller.create(req);
		assertNotNull(response);
		assertThat(response.getStatus(), Is.is(201));
		String accountNumber2 = ((Account)response.getEntity()).getAccountNumber();

        CyclicBarrier barrier = new CyclicBarrier(2);
        AccountTransferRequest transferReq = new AccountTransferRequest();
        transferReq.setToAccountNumber(accountNumber2);
        transferReq.setAmount(500);
        Thread t1 = new Thread(new Runnable() {			
			@Override
			public void run() {
				try {
					barrier.await();
					controller.transfer(accountNumber1, transferReq);
				} catch (InterruptedException | BrokenBarrierException e) {
					e.printStackTrace();
				}
			}
		});
        t1.start();
        
        AccountTransferRequest transferReq2 = new AccountTransferRequest();
        transferReq2.setToAccountNumber(accountNumber1);
        transferReq2.setAmount(500);
        Thread t2 = new Thread(new Runnable() {			
			@Override
			public void run() {
				try {
					barrier.await();
					controller.transfer(accountNumber2, transferReq2);
				} catch (InterruptedException | BrokenBarrierException e) {
					e.printStackTrace();
				}
			}
		});
        t2.start();
        
        Thread.sleep(5000);
  
        response = controller.get(accountNumber1);
        assertNotNull(response);
		assertThat(response.getStatus(), Is.is(200));
		assertEquals(2000, ((Account)response.getEntity()).getBalance(), 0);     
		
        response = controller.get(accountNumber2);
        assertNotNull(response);
		assertThat(response.getStatus(), Is.is(200));
		assertEquals(2000, ((Account)response.getEntity()).getBalance(), 0);  
		        
	}
	
}
