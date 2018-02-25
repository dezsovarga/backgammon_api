package com.dezso.varga.backgammon.authentication.services;

import com.dezso.varga.backgammon.authentication.AuthUtils;
import com.dezso.varga.backgammon.authentication.domain.Account;
import com.dezso.varga.backgammon.authentication.repositories.AccountRepository;
import com.dezso.varga.backgammon.exeptions.UserAlreadyExistsException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by dezso on 25.02.2018.
 */
@Service
public class AccountService {

    @Autowired
    private AccountRepository accountRepository;

    public Account saveAccount(String confirmToken) throws Exception{
        Account account = AuthUtils.validateConfirmToken(confirmToken);
        Account existingAccount = accountRepository.findByEmail(account.getEmail());
        if (existingAccount == null) {
            accountRepository.save(account);
        } else {
            throw new UserAlreadyExistsException("User already verified", HttpStatus.CONFLICT.value());
        }
        return account;
    }

    public List<Account> findAllAccounts() {
        List<Account> users = new ArrayList<>();
        accountRepository.findAll().forEach(users::add);
        return users;
    }
}
