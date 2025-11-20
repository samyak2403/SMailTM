package com.samyak.smailtm.util

import com.google.gson.JsonParser
import com.samyak.smailtm.Config
import com.samyak.smailtm.SMailTM
import com.samyak.smailtm.io.IO
import javax.security.auth.login.LoginException

/**
 * The SMaliBuilder class provides methods for account creation and authentication.
 */
object SMaliBuilder {

    /**
     * Logs in to the API and returns a SMailTM instance.
     *
     * @param email the email address to log in with
     * @param password the password for authentication
     * @return a new SMailTM instance for the authenticated user
     * @throws LoginException if authentication fails or network errors occur
     */
    @Throws(LoginException::class)
    fun login(email: String, password: String): SMailTM {
        return try {
            val jsonData = "{\"address\" : \"${email.trim()}\",\"password\" : \"${password.trim()}\"}"
            val response = IO.requestPOST("${Config.BASEURL}/token", jsonData)
            
            if (response.responseCode == 200) {
                val json = JsonParser.parseString(response.response).asJsonObject
                SMailTM(json.get("token").asString, json.get("id").asString)
            } else {
                throw LoginException(response.response)
            }
        } catch (e: Exception) {
            throw LoginException("Network error something went wrong $e")
        }
    }

    /**
     * Creates a new account with the specified email and password.
     *
     * @param email the email address for the new account
     * @param password the password for the new account
     * @return true if the account was created successfully; false otherwise
     * @throws LoginException if the account already exists or invalid inputs are provided
     */
    @Throws(LoginException::class)
    fun create(email: String, password: String): Boolean {
        return try {
            val jsonData = "{\"address\" : \"${email.trim().lowercase()}\",\"password\" : \"${password.trim().lowercase()}\"}"
            val response = IO.requestPOST("${Config.BASEURL}/accounts", jsonData)
            
            response.responseCode == 200 || response.responseCode == 201
        } catch (e: Exception) {
            false
        }
    }

    /**
     * Creates a new account and logs in to it.
     *
     * @param email the email address for the new account
     * @param password the password for the new account
     * @return a new SMailTM instance for the created and authenticated user
     * @throws LoginException if account creation or login fails
     */
    @Throws(LoginException::class)
    fun createAndLogin(email: String, password: String): SMailTM {
        return try {
            val jsonData = "{\"address\" : \"${email.trim().lowercase()}\",\"password\" : \"${password.trim()}\"}"
            val response = IO.requestPOST("${Config.BASEURL}/accounts", jsonData)

            when (response.responseCode) {
                201 -> login(email.trim().lowercase(), password.trim())
                422 -> throw LoginException("Account Already Exists! Error 422")
                429 -> throw LoginException("Too many requests! Error 429 Rate limited")
                else -> throw LoginException("Something went wrong while creating account! Try Again")
            }
        } catch (e: Exception) {
            throw LoginException(e.toString())
        }
    }

    /**
     * Creates and logs in to a randomly generated account.
     *
     * @param password the password for the new account
     * @return a new SMailTM instance for the created and authenticated user
     * @throws LoginException if account creation or login fails
     */
    @Throws(LoginException::class)
    fun createDefault(password: String): SMailTM {
        return try {
            val email = "${Utility.createRandomString(8)}@${Domains.getRandomDomain().domainName}"
            createAndLogin(email, password)
        } catch (e: Exception) {
            throw LoginException(e.toString())
        }
    }

    /**
     * Login into an account with token
     *
     * @param token the jwt token of the account
     * @return the SMailTM instance to a jwt specified account
     * @throws LoginException when network error or token provided is invalid
     */
    @Throws(LoginException::class)
    fun loginWithToken(token: String): SMailTM {
        return try {
            val response = IO.requestGET("${Config.BASEURL}/me", token)
            
            when (response.responseCode) {
                401 -> throw LoginException("Invalid Token Provided")
                200 -> {
                    val json = JsonParser.parseString(response.response).asJsonObject
                    SMailTM(token, json.get("id").asString)
                }
                else -> throw LoginException("Invalid response received")
            }
        } catch (e: Exception) {
            throw LoginException(e.message ?: "Unknown error")
        }
    }
}
