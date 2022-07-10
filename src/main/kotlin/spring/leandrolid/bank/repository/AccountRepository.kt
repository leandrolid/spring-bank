package spring.leandrolid.bank.repository

import org.springframework.data.jpa.repository.JpaRepository
import spring.leandrolid.bank.model.Account

interface AccountRepository : JpaRepository<Account, Long>