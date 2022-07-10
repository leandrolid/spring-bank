package spring.leandrolid.bank.controller

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import spring.leandrolid.bank.model.Account
import spring.leandrolid.bank.service.AccountService

@RestController
@RequestMapping(
    path = ["/accounts"],
    produces = ["application/json"]
)
class AccountController(private val service: AccountService) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(@RequestBody account: Account): Account = service.create(account)

    @GetMapping
    fun getAll(): List<Account> = service.getAll()

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): ResponseEntity<Account> = service.getById(id)
        .map { ResponseEntity.ok(it) }
        .orElse(ResponseEntity.notFound().build())

    @PutMapping("/{id}")
    fun update(@PathVariable id: Long, @RequestBody account: Account): ResponseEntity<Account> {
        return service.update(id, account)
            .map { ResponseEntity.ok(it) }
            .orElse(ResponseEntity.notFound().build())
    }

    @DeleteMapping("/{id}")
    fun delete(@PathVariable id: Long): ResponseEntity<Void> {
        service.delete(id)
        return ResponseEntity<Void>(HttpStatus.OK)
    }
}