package spring.leandrolid.bank.service

import org.springframework.stereotype.Service
import org.springframework.util.Assert
import spring.leandrolid.bank.model.Account
import spring.leandrolid.bank.repository.AccountRepository
import java.util.*

@Service
class AccountServiceImpl(private val repository: AccountRepository) : AccountService {
    override fun create(account: Account): Account {
        Assert.hasLength(account.name, "[name] is required")
        Assert.isTrue(
            account.name.length >= Verifications.MIN_NAME_CHARACTERS,
            "[name] should have at least ${Verifications.MIN_NAME_CHARACTERS} characters"
        )

        Assert.hasLength(account.document, "[document] is required")
        Assert.isTrue(
            account.document.length == Verifications.DOCUMENT_LENGTH,
            "[document] should have ${Verifications.DOCUMENT_LENGTH} characters"
        )

        Assert.hasLength(account.phone, "[phone] is required")
        Assert.isTrue(Regex(Verifications.PHONE_PATTERN).matches(account.phone), "[phone] should be (XX) XXXXX-XXXX")

        return repository.save(account)
    }

    override fun getAll(): List<Account> {
        return repository.findAll()
    }

    override fun getById(id: Long): Optional<Account> {
        return repository.findById(id)

    }

    override fun update(id: Long, account: Account): Optional<Account> {
        val optional = getById(id)
        if (optional.isEmpty) return Optional.empty<Account>()

        return optional.map {
            val accountToUpdate = it.copy(
                name = account.name,
                document = account.document,
                phone = account.phone
            )
            repository.save(accountToUpdate)
        }
    }

    override fun delete(id: Long) {
        repository.findById(id)
            .map { repository.delete(it) }
            .orElseThrow { throw RuntimeException("Id $id not found") }
    }

    object Verifications {
        const val MIN_NAME_CHARACTERS = 3
        const val DOCUMENT_LENGTH = 11
        const val PHONE_PATTERN = "^\\([1-9]{2}\\) (?:[2-8]|9[1-9])[0-9]{3}\\-[0-9]{4}\$"
    }
}