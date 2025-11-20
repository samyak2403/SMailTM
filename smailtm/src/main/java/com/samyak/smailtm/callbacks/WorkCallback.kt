package com.samyak.smailtm.callbacks

/**
 * Functional interface for handling the status of a work operation.
 */
fun interface WorkCallback {
    /**
     * Called to indicate the status of a work operation.
     *
     * @param status true if the work operation was successful, false otherwise
     */
    fun workStatus(status: Boolean)
}
