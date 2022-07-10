package spring.leandrolid.bank

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import spring.leandrolid.bank.model.Account
import spring.leandrolid.bank.repository.AccountRepository

@SpringBootTest
@AutoConfigureMockMvc
class AccountControllerTest {

    @Autowired lateinit var mockMvc: MockMvc

    @Autowired lateinit var repository: AccountRepository

    @Test
    fun `test find all`() {
        repository.save(Account(name = "Test", document = "12345678901", phone = "(11) 98765-4321"))

        mockMvc.perform(MockMvcRequestBuilders.get("/accounts"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("\$").isArray)
            .andExpect(MockMvcResultMatchers.jsonPath("\$[0].id").isNumber)
            .andExpect(MockMvcResultMatchers.jsonPath("\$[0].name").isString)
            .andExpect(MockMvcResultMatchers.jsonPath("\$[0].document").isString)
            .andExpect(MockMvcResultMatchers.jsonPath("\$[0].phone").isString)
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `test find by id`() {
        val account = repository.save(Account(name = "Test", document = "12345678901", phone = "(11) 98765-4321"))

        mockMvc.perform(MockMvcRequestBuilders.get("/accounts/${account.getId()}"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.id").isNumber)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.name").value(account.name))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.document").value(account.document))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.phone").value(account.phone))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `test create account`() {
        repository.deleteAll()

        val account = Account(name = "Test", document = "12345678901", phone = "(11) 98765-4321")
        val json = ObjectMapper().writeValueAsString(account)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/accounts")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
            .andExpect(MockMvcResultMatchers.status().isCreated)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.id").isNumber)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.name").value(account.name))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.document").value(account.document))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.phone").value(account.phone))
            .andDo(MockMvcResultHandlers.print())

        Assertions.assertFalse(repository.findAll().isEmpty())
    }

    @Test
    fun `test update account`() {
        val account = repository
            .save(Account(name = "Test", document = "12345678901", phone = "(11) 98765-4321"))
            .copy(name = "Other Test")
        val json = ObjectMapper().writeValueAsString(account)

        mockMvc.perform(
            MockMvcRequestBuilders.put("/accounts/${account.getId()}")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.id").value(account.getId()))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.name").value(account.name))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.document").value(account.document))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.phone").value(account.phone))
            .andDo(MockMvcResultHandlers.print())

        val foundById = repository.findById(account.getId()!!)

        Assertions.assertTrue(foundById.isPresent)
        Assertions.assertEquals(foundById.get().name, account.name)
    }

    @Test
    fun `test delete account`() {
        repository.deleteAll()
        val account = repository.save(Account(name = "Test", document = "12345678901", phone = "(11) 98765-4321"))

        mockMvc.perform(MockMvcRequestBuilders.delete("/accounts/${account.getId()}"))
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andDo(MockMvcResultHandlers.print())

        val foundById = repository.findById(account.getId()!!)
        Assertions.assertFalse(foundById.isPresent)

        val foundAll = repository.findAll()
        Assertions.assertTrue(foundAll.isEmpty())
    }

    @Test
    fun `test create account validation error empty name`() {
        val account = Account(name = "", document = "12345678901", phone = "(11) 98765-4321")
        val json = ObjectMapper().writeValueAsString(account)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/accounts")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.statusCode").isNumber)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.statusCode").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.message").isString)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.message").value("[name] is required"))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `test create account validation error name should have 3 characters`() {
        val account = Account(name = "A", document = "12345678901", phone = "(11) 98765-4321")
        val json = ObjectMapper().writeValueAsString(account)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/accounts")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.statusCode").isNumber)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.statusCode").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.message").isString)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.message").value("[name] should have at least 3 characters"))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `test create account validation error empty document`() {
        val account = Account(name = "Test", document = "", phone = "(11) 98765-4321")
        val json = ObjectMapper().writeValueAsString(account)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/accounts")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.statusCode").isNumber)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.statusCode").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.message").isString)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.message").value("[document] is required"))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `test create account validation error document should have 11 characters`() {
        val account = Account(name = "Test", document = "123", phone = "(11) 98765-4321")
        val json = ObjectMapper().writeValueAsString(account)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/accounts")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.statusCode").isNumber)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.statusCode").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.message").isString)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.message").value("[document] should have 11 characters"))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `test create account validation error empty phone`() {
        val account = Account(name = "Test", document = "12345678901", phone = "")
        val json = ObjectMapper().writeValueAsString(account)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/accounts")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.statusCode").isNumber)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.statusCode").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.message").isString)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.message").value("[phone] is required"))
            .andDo(MockMvcResultHandlers.print())
    }

    @Test
    fun `test create account validation error phone should match pattern`() {
        val account = Account(name = "Test", document = "12345678901", phone = "90000-0000")
        val json = ObjectMapper().writeValueAsString(account)

        mockMvc.perform(
            MockMvcRequestBuilders.post("/accounts")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)
        )
            .andExpect(MockMvcResultMatchers.status().isBadRequest)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.statusCode").isNumber)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.statusCode").value(400))
            .andExpect(MockMvcResultMatchers.jsonPath("\$.message").isString)
            .andExpect(MockMvcResultMatchers.jsonPath("\$.message").value("[phone] should be (XX) XXXXX-XXXX"))
            .andDo(MockMvcResultHandlers.print())
    }
}