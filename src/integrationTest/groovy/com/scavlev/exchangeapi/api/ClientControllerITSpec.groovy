package com.scavlev.exchangeapi.api

import com.scavlev.exchangeapi.account.AccountData
import com.scavlev.exchangeapi.account.domain.AccountStatus
import com.scavlev.exchangeapi.client.ClientData
import com.scavlev.exchangeapi.client.ClientDoesntExistException
import com.scavlev.exchangeapi.client.ClientNotFoundException
import com.scavlev.exchangeapi.client.UpdateClientRequest
import com.scavlev.exchangeapi.client.services.*
import org.spockframework.spring.SpringBean
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.http.MediaType
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import spock.lang.Specification

import static com.scavlev.exchangeapi.api.FixtureHelper.clientDataFixture
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(ClientController)
@AutoConfigureMockMvc
@ContextConfiguration(classes = [DataModelAssemblerConfiguration])
class ClientControllerITSpec extends Specification {

    @Autowired
    MockMvc mvc

    @SpringBean
    ListClients listClients = Mock()
    @SpringBean
    FindClient findClient = Mock()
    @SpringBean
    RegisterClient registerClient = Mock()
    @SpringBean
    DeactivateClient deactivateClient = Mock()
    @SpringBean
    GetClientAccounts getClientAccounts = Mock()
    @SpringBean
    UpdateClient updateClient = Mock()

    def "should return list of clients"() {
        given:
        1 * listClients.list(_ as Pageable) >> { PageRequest pageRequest ->
            new PageImpl<ClientData>((1..50).collect({
                clientDataFixture(id: it)
            }), pageRequest, 50)
        }

        expect:
        mvc.perform(get("/clients?page=1&size=10"))
                .andExpect(status().isOk())
                .andExpect(content().json(getResponseExample('list')))
                .andReturn()
    }

    def "should return a client"() {
        given:
        1 * findClient.find(_) >> { Long id ->
            Optional.of(clientDataFixture(id: id))
        }

        expect:
        mvc.perform(get("/clients/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(getResponseExample('get-client')))
                .andReturn()
    }

    def "should return status code 404 if client is not found"() {
        given:
        1 * findClient.find(_) >> { Long id ->
            throw new ClientNotFoundException(id)
        }

        expect:
        mvc.perform(get("/clients/1"))
                .andExpect(status().isNotFound())
                .andReturn()
    }

    def "should return a list of client accounts"() {
        given:
        1 * getClientAccounts.get(_ as Long) >> { Long id ->
            (1..50).collect({
                AccountData.builder()
                        .id(it)
                        .clientId(id)
                        .currency("EUR")
                        .status(AccountStatus.ACTIVE)
                        .balance(it * 77.3 as BigDecimal)
                        .build()
            })
        }

        expect:
        mvc.perform(get("/clients/1/accounts"))
                .andExpect(status().isOk())
                .andExpect(content().json(getResponseExample('accounts')))
                .andReturn()
    }

    def "should return status 400 if client is not found while listing accounts"() {
        given:
        1 * getClientAccounts.get(_ as Long) >> { Long id ->
            throw new ClientDoesntExistException(id)
        }

        expect:
        mvc.perform(get("/clients/1/accounts"))
                .andExpect(status().isBadRequest())
                .andReturn()
    }

    def "should deactivate client"() {
        given:
        1 * deactivateClient.deactivate(_ as Long) >> { Long id ->
            clientDataFixture(id: id)
        }

        expect:
        mvc.perform(delete("/clients/1"))
                .andExpect(status().isOk())
                .andExpect(content().json(getResponseExample('delete')))
                .andReturn()
    }

    def "should return status 404 if deactivating client does not exist"() {
        given:
        1 * deactivateClient.deactivate(_ as Long) >> { Long id ->
            throw new ClientNotFoundException(id)
        }

        expect:
        mvc.perform(delete("/clients/1"))
                .andExpect(status().isNotFound())
                .andReturn()
    }

    def "should update client"() {
        given:
        1 * updateClient.update(_ as Long, _ as UpdateClientRequest) >> { Long id, UpdateClientRequest updateClientRequest ->
            clientDataFixture(id: id, status: updateClientRequest.status)
        }

        expect:
        mvc.perform(patch("/clients/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content('{"status": "DEACTIVATED"}'))
                .andExpect(status().isOk())
                .andExpect(content().json(getResponseExample('update')))
                .andReturn()
    }

    def "should return status code 404 if there is no client to update"() {
        given:
        1 * updateClient.update(_ as Long, _ as UpdateClientRequest) >> { Long id, UpdateClientRequest updateClientRequest ->
            throw new ClientNotFoundException(id)
        }

        expect:
        mvc.perform(patch("/clients/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content('{"status": "DEACTIVATED"}'))
                .andExpect(status().isNotFound())
                .andReturn()
    }

    def "should create new client"() {
        given:
        1 * registerClient.register() >> {
            clientDataFixture()
        }

        expect:
        mvc.perform(post("/clients"))
                .andExpect(status().isOk())
                .andExpect(content().json(getResponseExample('create')))
                .andReturn()
    }

    static String getResponseExample(String filename) {
        new File("src/integrationTest/resources/api-responses/clients/${filename}.json").text
    }
}
