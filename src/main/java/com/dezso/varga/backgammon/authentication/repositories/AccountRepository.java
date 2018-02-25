package com.dezso.varga.backgammon.authentication.repositories;

import com.dezso.varga.backgammon.authentication.domain.Account;
import org.springframework.data.repository.CrudRepository;

/**
 * Created by dezso on 02.07.2017.
 */
public interface AccountRepository extends CrudRepository<Account, Long> {

    Account findByEmail(String email);
}
