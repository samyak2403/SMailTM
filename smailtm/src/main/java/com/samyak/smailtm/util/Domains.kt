package com.samyak.smailtm.util

import com.google.gson.Gson
import com.google.gson.JsonParser
import com.samyak.smailtm.Config
import com.samyak.smailtm.exceptions.DomainNotFoundException
import com.samyak.smailtm.io.IO
import org.slf4j.LoggerFactory

/**
 * The Domains class provides functionality for managing email domains.
 */
object Domains {
    private val log = LoggerFactory.getLogger(Domains::class.java)
    private val gson = Gson()
    private var domains = mutableListOf<Domain>()

    /**
     * Gets the list of available domains.
     */
    fun getDomainList(): List<Domain> = domains

    /**
     * Updates the list of available domains from the server.
     */
    fun updateDomains(): Boolean {
        domains.clear()
        return try {
            val response = IO.requestGET("${Config.BASEURL}/domains?page=1")
            if (response.responseCode == 200) {
                val jsonArray = JsonParser.parseString(response.response).asJsonArray
                jsonArray.forEach { element ->
                    domains.add(gson.fromJson(element.asJsonObject, Domain::class.java))
                }
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Fetches and returns the list of available domains.
     */
    fun fetchDomains(): List<Domain> {
        domains.clear()
        
        return try {
            val response = IO.requestGET("${Config.BASEURL}/domains?page=1")
            if (response.responseCode == 200) {
                val jsonArray = JsonParser.parseString(response.response).asJsonArray
                jsonArray.forEach { element ->
                    domains.add(gson.fromJson(element.asJsonObject, Domain::class.java))
                }
            }
            domains
        } catch (e: Exception) {
            log.warn("Failed to fetch Domains", e)
            domains
        }
    }

    /**
     * Fetches the Domain information by DomainID
     * @throws DomainNotFoundException when domain was not found on server
     */
    @Throws(DomainNotFoundException::class)
    fun fetchDomainById(id: String): Domain {
        return try {
            val response = IO.requestGET("${Config.BASEURL}/domains/$id")
            
            if (response.responseCode == 200) {
                val json = JsonParser.parseString(response.response).asJsonObject
                gson.fromJson(json, Domain::class.java)
            } else {
                throw DomainNotFoundException("ID Specified can not be Found!")
            }
        } catch (e: Exception) {
            throw DomainNotFoundException(e.toString())
        }
    }

    /**
     * Get a Random Domain From List
     */
    fun getRandomDomain(): Domain {
        updateDomains()
        return domains[0]
    }
}
