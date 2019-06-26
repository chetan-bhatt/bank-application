package assignment.bank.rest;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PATCH;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.jetty.http.HttpStatus;

import assignment.bank.exception.BankAccountException;
import assignment.bank.model.Account;
import assignment.bank.service.AccountService;
import assignment.bank.service.AccountServiceImpl;

@Path("/bank")
public class BankController {
	
	private AccountService accountService = new AccountServiceImpl();

	@POST
	@Path("accounts")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response create(AccountCreateRequest accountCreateRequest) throws BankAccountException {
		Account account = accountService.create(accountCreateRequest.getInitialDeposit());
		return Response.status(HttpStatus.CREATED_201).entity(account).build();
	}
	
	@GET
	@Path("accounts/{accountNumber}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response get(@PathParam(value = "accountNumber") String accountNumber) {
		try {
			Account account = accountService.get(accountNumber);
			return Response.status(HttpStatus.OK_200).entity(account).build();
		} catch (BankAccountException e) {
			ErrorResponse error = new ErrorResponse(e.getMessage());
			return Response.status(HttpStatus.BAD_REQUEST_400).entity(error).build();
		}
	}
	
	@PATCH
	@Path("accounts/{accountNumber}")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deposit(@PathParam(value = "accountNumber") String accountNumber, AccountDepositRequest depositRequest) {
		try {
			double availableBalance = accountService.deposit(accountNumber, depositRequest.getAmount());
			Map<String, Double> map = new HashMap<String, Double>();
			map.put("availableBalance", availableBalance);
			return Response.status(HttpStatus.OK_200).entity(map).build();
		} catch (BankAccountException e) {
			ErrorResponse error = new ErrorResponse(e.getMessage());
			return Response.status(HttpStatus.BAD_REQUEST_400).entity(error).build();
		}
	}
	
	@POST
	@Path("accounts/{accountNumber}/transfer")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response transfer(@PathParam(value = "accountNumber") String accountNumber, AccountTransferRequest transferRequest) {
		try {
			double availableBalance = accountService.transfer(accountNumber, transferRequest.getToAccountNumber(), transferRequest.getAmount());
			Map<String, Double> map = new HashMap<String, Double>();
			map.put("availableBalance", availableBalance);
			return Response.status(HttpStatus.OK_200).entity(map).build();
		} catch (BankAccountException e) {
			ErrorResponse error = new ErrorResponse(e.getMessage());
			return Response.status(HttpStatus.BAD_REQUEST_400).entity(error).build();
		}
	}
}
