package com.dezso.varga.backgammon.home;

import com.dezso.varga.backgammon.authentication.domain.Account;
import com.dezso.varga.backgammon.authentication.services.AccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * Created by dezso on 02.12.2017.
 */
@RestController
@RequestMapping("home")
public class HomeController {

    @Autowired
    private AccountService accountService;

    @RequestMapping(method= RequestMethod.GET, value="/users")
    public List<Account> getAccounts() throws Exception {
        return accountService.findAllAccounts();
    }
}
